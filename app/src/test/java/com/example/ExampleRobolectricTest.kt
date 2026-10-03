package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.IngredientItem
import com.example.data.model.Recipe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("OpenKitchen", appName)
  }

  @Test
  fun `ingredient scaling scales quantities accurately`() {
    val ingredient = IngredientItem("Flour", "200g")
    val scaledDouble = ingredient.scaleMeasure(2.0f)
    assertEquals("400g", scaledDouble)

    val halfScale = ingredient.scaleMeasure(0.5f)
    assertEquals("100g", halfScale)
  }

  @Test
  fun `recipe parsing parses steps and timer minutes correctly`() {
    val sampleInstructions = """
      1. Bring salted water to a boil and cook pasta for 10 minutes.
      2. In a skillet, heat olive oil and sauté garlic.
      3. Mix pasta with sauce and simmer for 5 mins before serving.
    """.trimIndent()
    val steps = Recipe.parseInstructions(sampleInstructions)
    assertTrue(steps.size >= 3)
    assertEquals(10 * 60, steps[0].timerSeconds)
    assertEquals(5 * 60, steps[2].timerSeconds)
  }
}
