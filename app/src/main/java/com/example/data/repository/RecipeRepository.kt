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

    suspend fun getCategories(): List<CategoryDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.getCategories()
            val apiCategories = if (!response.categories.isNullOrEmpty()) response.categories else emptyList()
            val extraCategories = CuratedRecipes.categories.filter { cur ->
                apiCategories.none { it.strCategory.equals(cur.strCategory, ignoreCase = true) }
            }
            val all = if (apiCategories.isNotEmpty()) apiCategories + extraCategories else CuratedRecipes.categories

            val priorityOrder = listOf(
                "Breakfast", "Vegan", "Quick Meals", "Vegetarian",
                "Pasta", "Seafood", "Dessert", "Chicken", "Beef"
            )
            all.sortedBy { cat ->
                val idx = priorityOrder.indexOfFirst { it.equals(cat.strCategory, ignoreCase = true) }
                if (idx != -1) idx else 100
            }
        } catch (e: Exception) {
            CuratedRecipes.categories
        }
    }

    suspend fun getFeaturedRecipes(): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            // Fetch seafood or pasta recipes to supplement featured list
            val response = api.filterByCategory("Seafood")
            val remoteList = response.meals?.take(6)?.mapNotNull { meal ->
                try {
                    val full = api.lookupById(meal.idMeal).meals?.firstOrNull()
                    full?.let { Recipe.fromDto(it) }
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()

            if (remoteList.isNotEmpty()) {
                (CuratedRecipes.featuredRecipes + remoteList).distinctBy { it.id }
            } else {
                CuratedRecipes.featuredRecipes
            }
        } catch (e: Exception) {
            CuratedRecipes.featuredRecipes
        }
    }

    suspend fun searchRecipes(query: String): List<Recipe> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val response = api.searchByName(query.trim())
            val apiMeals = response.meals?.map { Recipe.fromDto(it) } ?: emptyList()

            // Also search in curated and return union
            val localMatches = CuratedRecipes.featuredRecipes.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true) ||
                it.ingredients.any { ing -> ing.name.contains(query, ignoreCase = true) }
            }

            (apiMeals + localMatches).distinctBy { it.id }
        } catch (e: Exception) {
            CuratedRecipes.featuredRecipes.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
            }
        }
    }

    suspend fun getRecipesByCategory(category: String): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            if (category.equals("Quick Meals", ignoreCase = true)) {
                return@withContext CuratedRecipes.featuredRecipes.filter { it.prepTimeMinutes <= 25 }
            }
            if (category.equals("Vegan", ignoreCase = true)) {
                val curatedVegan = CuratedRecipes.featuredRecipes.filter { it.category.equals("Vegan", true) }
                val apiResponse = try { api.filterByCategory("Vegan") } catch (e: Exception) { null }
                val meals = apiResponse?.meals?.take(6)?.mapNotNull { m ->
                    try { api.lookupById(m.idMeal).meals?.firstOrNull()?.let { Recipe.fromDto(it) } } catch (e: Exception) { null }
                } ?: emptyList()
                return@withContext (curatedVegan + meals).distinctBy { it.id }
            }

            val response = api.filterByCategory(category)
            val meals = response.meals?.take(10) ?: emptyList()
            if (meals.isNotEmpty()) {
                val fullRecipes = meals.mapNotNull { m ->
                    try {
                        api.lookupById(m.idMeal).meals?.firstOrNull()?.let { Recipe.fromDto(it) }
                    } catch (e: Exception) {
                        null
                    }
                }
                if (fullRecipes.isNotEmpty()) return@withContext fullRecipes
            }
            // Fallback to curated
            CuratedRecipes.featuredRecipes.filter { it.category.equals(category, ignoreCase = true) }
        } catch (e: Exception) {
            CuratedRecipes.featuredRecipes.filter { it.category.equals(category, ignoreCase = true) }
        }
    }

    /**
     * Category-based filter with Spoonacular API integration and robust offline fallback.
     * Supports filtering by category (Breakfast, Vegan, Quick Meals, etc.), diet, and max preparation time.
     */
    suspend fun getCategoryFilteredRecipes(
        category: String,
        maxReadyTime: Int? = null,
        diet: String? = null,
        query: String? = null,
        spoonacularApiKey: String = ""
    ): List<Recipe> = withContext(Dispatchers.IO) {
        // 1. Attempt Spoonacular complexSearch if API key is provided
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
                // Silently fallback to MealDB and curated collection
            }
        }

        // 2. MealDB & Curated fallback
        val baseList = when {
            category.equals("Quick Meals", ignoreCase = true) -> {
                CuratedRecipes.featuredRecipes.filter { it.prepTimeMinutes <= 25 }
            }
            category.equals("Vegan", ignoreCase = true) -> {
                val curatedVegan = CuratedRecipes.featuredRecipes.filter {
                    it.category.equals("Vegan", true) || it.tags.any { t -> t.contains("vegan", true) }
                }
                val apiVegan = try {
                    api.filterByCategory("Vegan").meals?.take(8)?.mapNotNull {
                        try { api.lookupById(it.idMeal).meals?.firstOrNull()?.let { m -> Recipe.fromDto(m) } } catch (e: Exception) { null }
                    } ?: emptyList()
                } catch (e: Exception) { emptyList() }
                (curatedVegan + apiVegan).distinctBy { it.id }
            }
            category.equals("Breakfast", ignoreCase = true) -> {
                val curatedBfast = CuratedRecipes.featuredRecipes.filter { it.category.equals("Breakfast", true) }
                val apiBfast = try {
                    api.filterByCategory("Breakfast").meals?.take(8)?.mapNotNull {
                        try { api.lookupById(it.idMeal).meals?.firstOrNull()?.let { m -> Recipe.fromDto(m) } } catch (e: Exception) { null }
                    } ?: emptyList()
                } catch (e: Exception) { emptyList() }
                (curatedBfast + apiBfast).distinctBy { it.id }
            }
            else -> {
                getRecipesByCategory(category)
            }
        }

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

        // 3. Check curated
        val curated = CuratedRecipes.featuredRecipes.find { it.id == id }
        if (curated != null) return@withContext curated

        // 4. Query API
        try {
            val response = api.lookupById(id)
            response.meals?.firstOrNull()?.let { Recipe.fromDto(it) }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getRandomRecipe(): Recipe = withContext(Dispatchers.IO) {
        try {
            val response = api.getRandomMeal()
            val meal = response.meals?.firstOrNull()
            if (meal != null) {
                Recipe.fromDto(meal)
            } else {
                CuratedRecipes.featuredRecipes.randomOrNull() ?: CuratedRecipes.featuredRecipes.first()
            }
        } catch (e: Exception) {
            CuratedRecipes.featuredRecipes.randomOrNull() ?: CuratedRecipes.featuredRecipes.first()
        }
    }

    suspend fun toggleSave(recipe: Recipe, notes: String = "") = withContext(Dispatchers.IO) {
        // Keep both saved recipes and favorites synchronized
        toggleFavorite(recipe, notes)
    }

    suspend fun updateNotes(id: String, notes: String) = withContext(Dispatchers.IO) {
        updateFavoriteNotes(id, notes)
    }
}
