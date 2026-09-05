package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM saved_recipes ORDER BY savedAt DESC")
    fun getAllSavedRecipes(): Flow<List<SavedRecipeEntity>>

    @Query("SELECT * FROM saved_recipes WHERE idMeal = :id LIMIT 1")
    fun getSavedRecipeById(id: String): Flow<SavedRecipeEntity?>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_recipes WHERE idMeal = :id)")
    fun isRecipeSaved(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedRecipe(recipe: SavedRecipeEntity)

    @Query("DELETE FROM saved_recipes WHERE idMeal = :id")
    suspend fun deleteSavedRecipeById(id: String)

    @Query("UPDATE saved_recipes SET userNotes = :notes WHERE idMeal = :id")
    suspend fun updateUserNotes(id: String, notes: String)
}
