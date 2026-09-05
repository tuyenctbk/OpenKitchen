package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the 'favorites' table in Room.
 */
@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY favoritedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorites WHERE idMeal = :id LIMIT 1")
    fun getFavoriteById(id: String): Flow<FavoriteEntity?>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE idMeal = :id)")
    fun isFavorite(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE idMeal = :id")
    suspend fun deleteFavoriteById(id: String)

    @Query("UPDATE favorites SET userNotes = :notes WHERE idMeal = :id")
    suspend fun updateFavoriteNotes(id: String, notes: String)
}
