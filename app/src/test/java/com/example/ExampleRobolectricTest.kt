package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
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
    val ingredient = com.example.data.model.IngredientItem("Flour", "200g")
    val scaledDouble = ingredient.scaleMeasure(2.0f)
    assertEquals("400g", scaledDouble)

    val halfScale = ingredient.scaleMeasure(0.5f)
    assertEquals("100g", halfScale)
  }

  @Test
  fun `curated recipes have valid steps and ingredients`() {
    val recipes = com.example.data.repository.CuratedRecipes.featuredRecipes
    assert(recipes.isNotEmpty())
    val first = recipes.first()
    assert(first.ingredients.isNotEmpty())
    assert(first.steps.isNotEmpty())
  }
}
