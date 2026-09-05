package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MealListResponse(
    @Json(name = "meals") val meals: List<MealDto>?
)

@JsonClass(generateAdapter = true)
data class CategoryListResponse(
    @Json(name = "categories") val categories: List<CategoryDto>?
)

@JsonClass(generateAdapter = true)
data class CategoryDto(
    @Json(name = "idCategory") val idCategory: String,
    @Json(name = "strCategory") val strCategory: String,
    @Json(name = "strCategoryThumb") val strCategoryThumb: String?,
    @Json(name = "strCategoryDescription") val strCategoryDescription: String?
)

@JsonClass(generateAdapter = true)
data class MealDto(
    @Json(name = "idMeal") val idMeal: String,
    @Json(name = "strMeal") val strMeal: String,
    @Json(name = "strCategory") val strCategory: String? = null,
    @Json(name = "strArea") val strArea: String? = null,
    @Json(name = "strInstructions") val strInstructions: String? = null,
    @Json(name = "strMealThumb") val strMealThumb: String? = null,
    @Json(name = "strTags") val strTags: String? = null,
    @Json(name = "strYoutube") val strYoutube: String? = null,
    @Json(name = "strSource") val strSource: String? = null,
    @Json(name = "strIngredient1") val strIngredient1: String? = null,
    @Json(name = "strIngredient2") val strIngredient2: String? = null,
    @Json(name = "strIngredient3") val strIngredient3: String? = null,
    @Json(name = "strIngredient4") val strIngredient4: String? = null,
    @Json(name = "strIngredient5") val strIngredient5: String? = null,
    @Json(name = "strIngredient6") val strIngredient6: String? = null,
    @Json(name = "strIngredient7") val strIngredient7: String? = null,
    @Json(name = "strIngredient8") val strIngredient8: String? = null,
    @Json(name = "strIngredient9") val strIngredient9: String? = null,
    @Json(name = "strIngredient10") val strIngredient10: String? = null,
    @Json(name = "strIngredient11") val strIngredient11: String? = null,
    @Json(name = "strIngredient12") val strIngredient12: String? = null,
    @Json(name = "strIngredient13") val strIngredient13: String? = null,
    @Json(name = "strIngredient14") val strIngredient14: String? = null,
    @Json(name = "strIngredient15") val strIngredient15: String? = null,
    @Json(name = "strIngredient16") val strIngredient16: String? = null,
    @Json(name = "strIngredient17") val strIngredient17: String? = null,
    @Json(name = "strIngredient18") val strIngredient18: String? = null,
    @Json(name = "strIngredient19") val strIngredient19: String? = null,
    @Json(name = "strIngredient20") val strIngredient20: String? = null,
    @Json(name = "strMeasure1") val strMeasure1: String? = null,
    @Json(name = "strMeasure2") val strMeasure2: String? = null,
    @Json(name = "strMeasure3") val strMeasure3: String? = null,
    @Json(name = "strMeasure4") val strMeasure4: String? = null,
    @Json(name = "strMeasure5") val strMeasure5: String? = null,
    @Json(name = "strMeasure6") val strMeasure6: String? = null,
    @Json(name = "strMeasure7") val strMeasure7: String? = null,
    @Json(name = "strMeasure8") val strMeasure8: String? = null,
    @Json(name = "strMeasure9") val strMeasure9: String? = null,
    @Json(name = "strMeasure10") val strMeasure10: String? = null,
    @Json(name = "strMeasure11") val strMeasure11: String? = null,
    @Json(name = "strMeasure12") val strMeasure12: String? = null,
    @Json(name = "strMeasure13") val strMeasure13: String? = null,
    @Json(name = "strMeasure14") val strMeasure14: String? = null,
    @Json(name = "strMeasure15") val strMeasure15: String? = null,
    @Json(name = "strMeasure16") val strMeasure16: String? = null,
    @Json(name = "strMeasure17") val strMeasure17: String? = null,
    @Json(name = "strMeasure18") val strMeasure18: String? = null,
    @Json(name = "strMeasure19") val strMeasure19: String? = null,
    @Json(name = "strMeasure20") val strMeasure20: String? = null
)

data class IngredientItem(
    val name: String,
    val measure: String
) {
    fun scaleMeasure(multiplier: Float): String {
        if (multiplier == 1.0f || measure.isBlank()) return measure
        // Try parsing leading number (e.g., "2 cups", "1/2 tsp", "250g")
        val regex = Regex("""^(\d+(?:\.\d+)?|\d+/\d+)(.*)$""")
        val match = regex.find(measure.trim()) ?: return "$measure (x$multiplier)"
        val numStr = match.groupValues[1]
        val unit = match.groupValues[2]

        val parsedVal = if (numStr.contains("/")) {
            val parts = numStr.split("/")
            val numerator = parts.getOrNull(0)?.toDoubleOrNull() ?: 1.0
            val denominator = parts.getOrNull(1)?.toDoubleOrNull() ?: 1.0
            numerator / denominator
        } else {
            numStr.toDoubleOrNull() ?: 1.0
        }
        val scaled = parsedVal * multiplier
        val formatted = if (scaled % 1.0 == 0.0) {
            scaled.toInt().toString()
        } else {
            String.format(java.util.Locale.US, "%.1f", scaled)
        }
        return "$formatted$unit"
    }
}

data class CookingStep(
    val stepNumber: Int,
    val instruction: String,
    val timerSeconds: Int? = null
)

data class NutritionInfo(
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val fiberGrams: Int = 4,
    val sodiumMg: Int = 580
) {
    val proteinCalories: Int get() = proteinGrams * 4
    val carbsCalories: Int get() = carbsGrams * 4
    val fatCalories: Int get() = fatGrams * 9
    val totalMacroCalories: Int get() = (proteinCalories + carbsCalories + fatCalories).coerceAtLeast(1)

    val proteinRatio: Float get() = (proteinCalories.toFloat() / totalMacroCalories).coerceIn(0f, 1f)
    val carbsRatio: Float get() = (carbsCalories.toFloat() / totalMacroCalories).coerceIn(0f, 1f)
    val fatRatio: Float get() = (fatCalories.toFloat() / totalMacroCalories).coerceIn(0f, 1f)

    fun scaleFor(multiplier: Float): NutritionInfo {
        if (multiplier == 1.0f) return this
        return NutritionInfo(
            calories = (calories * multiplier).toInt().coerceAtLeast(0),
            proteinGrams = (proteinGrams * multiplier).toInt().coerceAtLeast(0),
            carbsGrams = (carbsGrams * multiplier).toInt().coerceAtLeast(0),
            fatGrams = (fatGrams * multiplier).toInt().coerceAtLeast(0),
            fiberGrams = (fiberGrams * multiplier).toInt().coerceAtLeast(0),
            sodiumMg = (sodiumMg * multiplier).toInt().coerceAtLeast(0)
        )
    }

    companion object {
        fun estimate(category: String, id: String, ingredientsCount: Int): NutritionInfo {
            val hash = kotlin.math.abs(id.hashCode())
            val deltaCal = (hash % 11) * 10 - 50
            val deltaProt = (hash % 7) - 3
            val deltaCarb = (hash % 9) - 4
            val deltaFat = (hash % 5) - 2

            return when (category.lowercase()) {
                "seafood" -> NutritionInfo(
                    calories = (380 + deltaCal).coerceIn(280, 520),
                    proteinGrams = (42 + deltaProt).coerceIn(28, 55),
                    carbsGrams = (18 + deltaCarb).coerceIn(6, 35),
                    fatGrams = (12 + deltaFat).coerceIn(6, 22),
                    fiberGrams = 3,
                    sodiumMg = 590
                )
                "pasta" -> NutritionInfo(
                    calories = (540 + deltaCal).coerceIn(420, 680),
                    proteinGrams = (22 + deltaProt).coerceIn(14, 32),
                    carbsGrams = (78 + deltaCarb).coerceIn(55, 95),
                    fatGrams = (14 + deltaFat).coerceIn(8, 24),
                    fiberGrams = 6,
                    sodiumMg = 680
                )
                "chicken" -> NutritionInfo(
                    calories = (460 + deltaCal).coerceIn(360, 590),
                    proteinGrams = (44 + deltaProt).coerceIn(32, 58),
                    carbsGrams = (24 + deltaCarb).coerceIn(10, 42),
                    fatGrams = (16 + deltaFat).coerceIn(9, 26),
                    fiberGrams = 4,
                    sodiumMg = 620
                )
                "beef" -> NutritionInfo(
                    calories = (590 + deltaCal).coerceIn(480, 740),
                    proteinGrams = (46 + deltaProt).coerceIn(34, 60),
                    carbsGrams = (20 + deltaCarb).coerceIn(8, 38),
                    fatGrams = (28 + deltaFat).coerceIn(18, 42),
                    fiberGrams = 3,
                    sodiumMg = 720
                )
                "vegetarian" -> NutritionInfo(
                    calories = (340 + deltaCal).coerceIn(240, 480),
                    proteinGrams = (16 + deltaProt).coerceIn(10, 26),
                    carbsGrams = (52 + deltaCarb).coerceIn(34, 70),
                    fatGrams = (11 + deltaFat).coerceIn(5, 20),
                    fiberGrams = 9,
                    sodiumMg = 460
                )
                "dessert" -> NutritionInfo(
                    calories = (450 + deltaCal).coerceIn(320, 620),
                    proteinGrams = (6 + deltaProt).coerceIn(3, 12),
                    carbsGrams = (64 + deltaCarb).coerceIn(42, 85),
                    fatGrams = (21 + deltaFat).coerceIn(12, 34),
                    fiberGrams = 2,
                    sodiumMg = 220
                )
                "breakfast" -> NutritionInfo(
                    calories = (390 + deltaCal).coerceIn(290, 510),
                    proteinGrams = (22 + deltaProt).coerceIn(14, 32),
                    carbsGrams = (36 + deltaCarb).coerceIn(20, 52),
                    fatGrams = (17 + deltaFat).coerceIn(9, 28),
                    fiberGrams = 5,
                    sodiumMg = 510
                )
                else -> NutritionInfo(
                    calories = (450 + deltaCal).coerceIn(320, 600),
                    proteinGrams = (28 + deltaProt).coerceIn(16, 42),
                    carbsGrams = (45 + deltaCarb).coerceIn(25, 65),
                    fatGrams = (16 + deltaFat).coerceIn(8, 26),
                    fiberGrams = 4,
                    sodiumMg = 580
                )
            }
        }
    }
}

data class Recipe(
    val id: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val thumbnailUrl: String,
    val tags: List<String>,
    val youtubeUrl: String?,
    val sourceUrl: String?,
    val ingredients: List<IngredientItem>,
    val baseServings: Int = 4,
    val prepTimeMinutes: Int = 30,
    val difficulty: String = "Medium",
    val userNotes: String? = null,
    val nutrition: NutritionInfo = NutritionInfo.estimate(category, id, ingredients.size)
) {
    val steps: List<CookingStep> by lazy {
        parseInstructions(instructions)
    }

    companion object {
        fun parseInstructions(raw: String): List<CookingStep> {
            if (raw.isBlank()) return emptyList()
            // Split by step headers or newlines or double periods
            val rawSteps = raw
                .split(Regex("""(?:\r?\n\s*\r?\n)|(?:STEP\s+\d+:?)|(?:\d+\.\s+)"""))
                .map { it.trim() }
                .filter { it.length > 5 }

            val list = if (rawSteps.isEmpty()) {
                raw.split(Regex("""(?<=[.!?])\s+""")).filter { it.isNotBlank() }
            } else rawSteps

            return list.mapIndexed { index, text ->
                // Detect timer hint e.g., "cook for 10 minutes" or "bake 25 mins"
                val timerMinutes = extractTimerMinutes(text)
                CookingStep(
                    stepNumber = index + 1,
                    instruction = text,
                    timerSeconds = timerMinutes?.times(60)
                )
            }
        }

        private fun extractTimerMinutes(text: String): Int? {
            val regex = Regex("""(\d+)\s*(?:-|to)?\s*(\d+)?\s*(?:mins?|minutes?)""", RegexOption.IGNORE_CASE)
            val match = regex.find(text) ?: return null
            val min = match.groupValues[1].toIntOrNull() ?: return null
            val max = match.groupValues.getOrNull(2)?.toIntOrNull()
            return max ?: min
        }

        fun fromDto(dto: MealDto): Recipe {
            val ingredientsList = mutableListOf<IngredientItem>()
            val pairs = listOf(
                dto.strIngredient1 to dto.strMeasure1,
                dto.strIngredient2 to dto.strMeasure2,
                dto.strIngredient3 to dto.strMeasure3,
                dto.strIngredient4 to dto.strMeasure4,
                dto.strIngredient5 to dto.strMeasure5,
                dto.strIngredient6 to dto.strMeasure6,
                dto.strIngredient7 to dto.strMeasure7,
                dto.strIngredient8 to dto.strMeasure8,
                dto.strIngredient9 to dto.strMeasure9,
                dto.strIngredient10 to dto.strMeasure10,
                dto.strIngredient11 to dto.strMeasure11,
                dto.strIngredient12 to dto.strMeasure12,
                dto.strIngredient13 to dto.strMeasure13,
                dto.strIngredient14 to dto.strMeasure14,
                dto.strIngredient15 to dto.strMeasure15,
                dto.strIngredient16 to dto.strMeasure16,
                dto.strIngredient17 to dto.strMeasure17,
                dto.strIngredient18 to dto.strMeasure18,
                dto.strIngredient19 to dto.strMeasure19,
                dto.strIngredient20 to dto.strMeasure20
            )
            for ((ing, measure) in pairs) {
                if (!ing.isNullOrBlank()) {
                    ingredientsList.add(IngredientItem(ing.trim(), measure?.trim().orEmpty()))
                }
            }

            val tagsList = dto.strTags?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()

            // Estimate prep time based on ingredient count and text length
            val prepTime = when {
                ingredientsList.size > 12 -> 45
                ingredientsList.size > 7 -> 30
                else -> 20
            }

            val difficulty = when {
                ingredientsList.size > 10 || (dto.strInstructions?.length ?: 0) > 800 -> "Advanced"
                ingredientsList.size > 5 -> "Intermediate"
                else -> "Easy"
            }

            return Recipe(
                id = dto.idMeal,
                name = dto.strMeal,
                category = dto.strCategory ?: "General",
                area = dto.strArea ?: "International",
                instructions = dto.strInstructions.orEmpty(),
                thumbnailUrl = dto.strMealThumb.orEmpty(),
                tags = tagsList,
                youtubeUrl = dto.strYoutube?.takeIf { it.isNotBlank() },
                sourceUrl = dto.strSource?.takeIf { it.isNotBlank() },
                ingredients = ingredientsList,
                baseServings = 4,
                prepTimeMinutes = prepTime,
                difficulty = difficulty
            )
        }
    }
}
