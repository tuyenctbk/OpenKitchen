package com.example.data.repository

import com.example.data.local.FavoriteDao
import com.example.data.local.FavoriteEntity
import com.example.data.local.RecipeDao
import com.example.data.local.SavedRecipeEntity
import com.example.data.model.CategoryDto
import com.example.data.model.Recipe
import com.example.data.remote.SpoonacularApi
import com.example.data.remote.TheMealDbApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RecipeRepository(
    private val api: TheMealDbApi,
    private val spoonacularApi: SpoonacularApi,
    private val dao: RecipeDao,
    private val favoriteDao: FavoriteDao
) {
    val savedRecipes: Flow<List<Recipe>> = dao.getAllSavedRecipes().map { list ->
        list.map { it.toRecipe() }
    }

    val favoriteRecipes: Flow<List<Recipe>> = favoriteDao.getAllFavorites().map { list ->
        list.map { it.toRecipe() }
    }

    fun isRecipeSaved(id: String): Flow<Boolean> = dao.isRecipeSaved(id)

    fun isRecipeFavorite(id: String): Flow<Boolean> = favoriteDao.isFavorite(id)

    suspend fun toggleFavorite(recipe: Recipe, notes: String = "") = withContext(Dispatchers.IO) {
        val isFav = favoriteDao.isFavorite(recipe.id).firstOrNull() ?: false
        if (isFav) {
            favoriteDao.deleteFavoriteById(recipe.id)
            dao.deleteSavedRecipeById(recipe.id)
        } else {
            favoriteDao.insertFavorite(FavoriteEntity.fromRecipe(recipe, notes))
            dao.insertSavedRecipe(SavedRecipeEntity.fromRecipe(recipe, notes))
        }
    }

    suspend fun updateFavoriteNotes(id: String, notes: String) = withContext(Dispatchers.IO) {
        favoriteDao.updateFavoriteNotes(id, notes)
        dao.updateUserNotes(id, notes)
    }

    private val defaultCategories = listOf(
        CategoryDto("1", "Breakfast", null, null),
        CategoryDto("2", "Vegan", null, null),
        CategoryDto("3", "Quick Meals", null, null),
        CategoryDto("4", "Vegetarian", null, null),
        CategoryDto("5", "Pasta", null, null),
        CategoryDto("6", "Seafood", null, null),
        CategoryDto("7", "Dessert", null, null),
        CategoryDto("8", "Chicken", null, null),
        CategoryDto("9", "Beef", null, null)
    )

    suspend fun getCategories(): List<CategoryDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.getCategories()
            val apiCategories = response.categories ?: emptyList()
            if (apiCategories.isEmpty()) return@withContext defaultCategories
            val priorityOrder = listOf(
                "Breakfast", "Vegan", "Quick Meals", "Starter", "Vegetarian",
                "Pasta", "Seafood", "Dessert", "Chicken", "Beef"
            )
            apiCategories.sortedBy { cat ->
                val idx = priorityOrder.indexOfFirst { it.equals(cat.strCategory, ignoreCase = true) }
                if (idx != -1) idx else 100
            }
        } catch (e: Exception) {
            defaultCategories
        }
    }

    suspend fun getFeaturedRecipes(): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            coroutineScope {
                val pastaDeferred = async { fetchFullRecipesForCategory("Pasta", 4) }
                val seafoodDeferred = async { fetchFullRecipesForCategory("Seafood", 4) }
                val vegetarianDeferred = async { fetchFullRecipesForCategory("Vegetarian", 4) }
                val dessertDeferred = async { fetchFullRecipesForCategory("Dessert", 4) }

                val pasta = pastaDeferred.await()
                val seafood = seafoodDeferred.await()
                val veg = vegetarianDeferred.await()
                val dessert = dessertDeferred.await()

                (pasta + seafood + veg + dessert).distinctBy { it.id }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchRecipes(query: String): List<Recipe> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val response = api.searchByName(query.trim())
            val apiMeals = response.meals?.map { Recipe.fromDto(it) } ?: emptyList()
            apiMeals.distinctBy { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipesByCategory(category: String): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            val apiCategory = when (category.lowercase()) {
                "quick meals" -> "Starter"
                else -> category
            }
            fetchFullRecipesForCategory(apiCategory, 12)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun fetchFullRecipesForCategory(category: String, limit: Int): List<Recipe> = coroutineScope {
        try {
            val response = api.filterByCategory(category)
            val meals = response.meals?.take(limit) ?: emptyList()
            if (meals.isEmpty()) return@coroutineScope emptyList<Recipe>()

            meals.map { m ->
                async {
                    try {
                        api.lookupById(m.idMeal).meals?.firstOrNull()?.let { Recipe.fromDto(it) }
                            ?: Recipe(
                                id = m.idMeal,
                                name = m.strMeal,
                                category = category,
                                area = "International",
                                instructions = "Follow recipe instructions and enjoy your meal.",
                                thumbnailUrl = m.strMealThumb.orEmpty(),
                                tags = listOf(category),
                                youtubeUrl = null,
                                sourceUrl = null,
                                ingredients = emptyList(),
                                baseServings = 4,
                                prepTimeMinutes = 25,
                                difficulty = "Medium"
                            )
                    } catch (e: Exception) {
                        Recipe(
                            id = m.idMeal,
                            name = m.strMeal,
                            category = category,
                            area = "International",
                            instructions = "Follow recipe instructions and enjoy your meal.",
                            thumbnailUrl = m.strMealThumb.orEmpty(),
                            tags = listOf(category),
                            youtubeUrl = null,
                            sourceUrl = null,
                            ingredients = emptyList(),
                            baseServings = 4,
                            prepTimeMinutes = 25,
                            difficulty = "Medium"
                        )
                    }
                }
            }.awaitAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Category-based filter with Spoonacular API integration and MealDB dynamic fallback.
     */
    suspend fun getCategoryFilteredRecipes(
        category: String,
        maxReadyTime: Int? = null,
        diet: String? = null,
        query: String? = null,
        spoonacularApiKey: String = ""
    ): List<Recipe> = withContext(Dispatchers.IO) {
        if (spoonacularApiKey.isNotBlank()) {
            try {
                val spoonacularType = when (category.lowercase()) {
                    "breakfast" -> "breakfast"
                    "dessert" -> "dessert"
                    "pasta" -> "main course"
                    else -> null
                }
                val spoonacularDiet = when {
                    category.equals("vegan", ignoreCase = true) -> "vegan"
                    category.equals("vegetarian", ignoreCase = true) -> "vegetarian"
                    !diet.isNullOrBlank() -> diet.lowercase()
                    else -> null
                }
                val spoonacularTime = maxReadyTime ?: if (category.equals("quick meals", ignoreCase = true)) 25 else null

                val spoonResponse = spoonacularApi.complexSearch(
                    query = query,
                    type = spoonacularType,
                    diet = spoonacularDiet,
                    maxReadyTime = spoonacularTime,
                    number = 15,
                    apiKey = spoonacularApiKey
                )

                if (spoonResponse.results.isNotEmpty()) {
                    return@withContext spoonResponse.results.map { it.toRecipe(category) }
                }
            } catch (e: Exception) {
                // Silently fallback to MealDB
            }
        }

        val baseList = getRecipesByCategory(category)
        var filtered = baseList
        if (maxReadyTime != null) {
            filtered = filtered.filter { it.prepTimeMinutes <= maxReadyTime }
        }
        if (!query.isNullOrBlank()) {
            filtered = filtered.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.ingredients.any { ing -> ing.name.contains(query, ignoreCase = true) }
            }
        }

        filtered
    }

    suspend fun getRecipeById(id: String): Recipe? = withContext(Dispatchers.IO) {
        // 1. Check favorites table first
        val favorite = favoriteDao.getFavoriteById(id).firstOrNull()
        if (favorite != null) return@withContext favorite.toRecipe()

        // 2. Check local saved DB
        val saved = dao.getSavedRecipeById(id).firstOrNull()
        if (saved != null) return@withContext saved.toRecipe()

        // 3. Query API
        try {
            val response = api.lookupById(id)
            response.meals?.firstOrNull()?.let { Recipe.fromDto(it) }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getRandomRecipe(): Recipe? = withContext(Dispatchers.IO) {
        try {
            val response = api.getRandomMeal()
            response.meals?.firstOrNull()?.let { Recipe.fromDto(it) }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun toggleSave(recipe: Recipe, notes: String = "") = withContext(Dispatchers.IO) {
        toggleFavorite(recipe, notes)
    }

    suspend fun updateNotes(id: String, notes: String) = withContext(Dispatchers.IO) {
        updateFavoriteNotes(id, notes)
    }
}
