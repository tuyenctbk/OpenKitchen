package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.IngredientItem
import com.example.data.model.Recipe
import com.example.ui.components.RecipeCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun recipe_card_screenshot() {
    val sampleRecipe = Recipe(
      id = "test_1",
      name = "Arrabiata Penne",
      category = "Pasta",
      area = "Italian",
      instructions = "1. Cook pasta al dente.\n2. Sauté garlic with chili.\n3. Combine and serve.",
      thumbnailUrl = "",
      tags = listOf("Pasta", "Quick"),
      youtubeUrl = null,
      sourceUrl = null,
      ingredients = listOf(
        IngredientItem("Penne", "300g"),
        IngredientItem("Garlic", "3 cloves")
      ),
      baseServings = 4,
      prepTimeMinutes = 20,
      difficulty = "Easy"
    )
    composeTestRule.setContent {
      MyApplicationTheme {
        RecipeCard(
          recipe = sampleRecipe,
          onClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/recipe_card.png")
  }
}
