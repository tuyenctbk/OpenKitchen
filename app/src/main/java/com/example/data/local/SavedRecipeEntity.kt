package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.IngredientItem
import com.example.data.model.NutritionInfo
import com.example.data.model.Recipe

@Entity(tableName = "saved_recipes")
data class SavedRecipeEntity(
    @PrimaryKey
    val idMeal: String,
    val strMeal: String,
    val strCategory: String,
    val strArea: String,
    val strMealThumb: String,
    val strInstructions: String,
    val ingredientsCsv: String,
    val measuresCsv: String,
    val strYoutube: String?,
    val strSource: String?,
    val tagsCsv: String,
    val userNotes: String = "",
    val calories: Int = 0,
    val proteinG: Int = 0,
    val carbsG: Int = 0,
    val fatG: Int = 0,
    val fiberG: Int = 0,
    val sodiumMg: Int = 0,
    val savedAt: Long = System.currentTimeMillis()
) {
    fun toRecipe(): Recipe {
        val ingList = ingredientsCsv.split("||").filter { it.isNotBlank() }
        val measList = measuresCsv.split("||")
        val ingredients = ingList.mapIndexed { index, name ->
            IngredientItem(name, measList.getOrElse(index) { "" })
        }
        val tagList = tagsCsv.split(",").filter { it.isNotBlank() }

        val prepTime = when {
            ingredients.size > 12 -> 45
            ingredients.size > 7 -> 30
            else -> 20
        }
        val diff = when {
            ingredients.size > 10 || strInstructions.length > 800 -> "Advanced"
            ingredients.size > 5 -> "Intermediate"
            else -> "Easy"
        }

        val nutrition = if (calories > 0) {
            NutritionInfo(
                calories = calories,
                proteinGrams = proteinG,
                carbsGrams = carbsG,
                fatGrams = fatG,
                fiberGrams = if (fiberG > 0) fiberG else 4,
                sodiumMg = if (sodiumMg > 0) sodiumMg else 580
            )
        } else {
            NutritionInfo.estimate(strCategory, idMeal, ingredients.size)
        }

        return Recipe(
            id = idMeal,
            name = strMeal,
            category = strCategory,
            area = strArea,
            instructions = strInstructions,
            thumbnailUrl = strMealThumb,
            tags = tagList,
            youtubeUrl = strYoutube,
            sourceUrl = strSource,
            ingredients = ingredients,
            baseServings = 4,
            prepTimeMinutes = prepTime,
            difficulty = diff,
            userNotes = userNotes.ifBlank { null },
            nutrition = nutrition
        )
    }

    companion object {
        fun fromRecipe(recipe: Recipe, notes: String = ""): SavedRecipeEntity {
            return SavedRecipeEntity(
                idMeal = recipe.id,
                strMeal = recipe.name,
                strCategory = recipe.category,
                strArea = recipe.area,
                strMealThumb = recipe.thumbnailUrl,
                strInstructions = recipe.instructions,
                ingredientsCsv = recipe.ingredients.joinToString("||") { it.name },
                measuresCsv = recipe.ingredients.joinToString("||") { it.measure },
                strYoutube = recipe.youtubeUrl,
                strSource = recipe.sourceUrl,
                tagsCsv = recipe.tags.joinToString(","),
                userNotes = notes.ifBlank { recipe.userNotes.orEmpty() },
                calories = recipe.nutrition.calories,
                proteinG = recipe.nutrition.proteinGrams,
                carbsG = recipe.nutrition.carbsGrams,
                fatG = recipe.nutrition.fatGrams,
                fiberG = recipe.nutrition.fiberGrams,
                sodiumMg = recipe.nutrition.sodiumMg,
                savedAt = System.currentTimeMillis()
            )
        }
    }
}
