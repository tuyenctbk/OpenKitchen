package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpoonacularSearchResponse(
    @Json(name = "results") val results: List<SpoonacularRecipeDto> = emptyList(),
    @Json(name = "offset") val offset: Int = 0,
    @Json(name = "number") val number: Int = 0,
    @Json(name = "totalResults") val totalResults: Int = 0
)

@JsonClass(generateAdapter = true)
data class SpoonacularRecipeDto(
    @Json(name = "id") val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "image") val image: String? = null,
    @Json(name = "readyInMinutes") val readyInMinutes: Int? = null,
    @Json(name = "servings") val servings: Int? = null,
    @Json(name = "summary") val summary: String? = null,
    @Json(name = "instructions") val instructions: String? = null,
    @Json(name = "dishTypes") val dishTypes: List<String>? = null,
    @Json(name = "diets") val diets: List<String>? = null,
    @Json(name = "cuisines") val cuisines: List<String>? = null,
    @Json(name = "extendedIngredients") val extendedIngredients: List<SpoonacularIngredientDto>? = null,
    @Json(name = "analyzedInstructions") val analyzedInstructions: List<SpoonacularInstructionGroupDto>? = null
) {
    fun toRecipe(categoryLabel: String = "Popular"): Recipe {
        val ingredientItems = extendedIngredients?.map {
            val measure = if (!it.original.isNullOrBlank()) it.original else "${it.amount ?: ""} ${it.unit ?: ""}".trim()
            IngredientItem(name = it.name ?: "Ingredient", measure = measure)
        } ?: emptyList()

        val stepList = mutableListOf<String>()
        analyzedInstructions?.forEach { group ->
            group.steps?.forEach { stepItem ->
                if (!stepItem.step.isNullOrBlank()) {
                    stepList.add(stepItem.step.trim())
                }
            }
        }
        val instructionText = if (stepList.isNotEmpty()) {
            stepList.mapIndexed { i, s -> "${i + 1}. $s" }.joinToString("\n\n")
        } else if (!instructions.isNullOrBlank()) {
            instructions.replace(Regex("<[^>]*>"), "").trim()
        } else {
            summary?.replace(Regex("<[^>]*>"), "")?.take(400) ?: "Follow standard culinary preparation technique."
        }

        val primaryCategory = when {
            diets?.any { it.contains("vegan", true) } == true -> "Vegan"
            dishTypes?.any { it.contains("breakfast", true) } == true -> "Breakfast"
            dishTypes?.any { it.contains("dessert", true) } == true -> "Dessert"
            categoryLabel.isNotBlank() -> categoryLabel
            else -> dishTypes?.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Main Course"
        }

        val time = readyInMinutes ?: (20 + (id % 30).toInt())
        val diff = when {
            time > 45 || ingredientItems.size > 10 -> "Advanced"
            time > 25 || ingredientItems.size > 5 -> "Intermediate"
            else -> "Easy"
        }

        return Recipe(
            id = "sp_$id",
            name = title,
            category = primaryCategory,
            area = cuisines?.firstOrNull() ?: "Global",
            instructions = instructionText,
            thumbnailUrl = image ?: "https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=800",
            tags = diets.orEmpty() + dishTypes.orEmpty(),
            youtubeUrl = null,
            sourceUrl = "https://spoonacular.com/recipes/$id",
            ingredients = ingredientItems,
            baseServings = servings ?: 4,
            prepTimeMinutes = time,
            difficulty = diff,
            nutrition = NutritionInfo.estimate(primaryCategory, id.toString(), ingredientItems.size)
        )
    }
}

@JsonClass(generateAdapter = true)
data class SpoonacularIngredientDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "original") val original: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "unit") val unit: String? = null
)

@JsonClass(generateAdapter = true)
data class SpoonacularInstructionGroupDto(
    @Json(name = "name") val name: String? = null,
    @Json(name = "steps") val steps: List<SpoonacularStepDto>? = null
)

@JsonClass(generateAdapter = true)
data class SpoonacularStepDto(
    @Json(name = "number") val number: Int? = null,
    @Json(name = "step") val step: String? = null
)
