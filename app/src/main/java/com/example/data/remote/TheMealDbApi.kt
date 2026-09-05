package com.example.data.remote

import com.example.data.model.CategoryListResponse
import com.example.data.model.MealListResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface TheMealDbApi {

    @GET("search.php")
    suspend fun searchByName(
        @Query("s") query: String
    ): MealListResponse

    @GET("lookup.php")
    suspend fun lookupById(
        @Query("i") id: String
    ): MealListResponse

    @GET("random.php")
    suspend fun getRandomMeal(): MealListResponse

    @GET("categories.php")
    suspend fun getCategories(): CategoryListResponse

    @GET("filter.php")
    suspend fun filterByCategory(
        @Query("c") category: String
    ): MealListResponse

    @GET("filter.php")
    suspend fun filterByArea(
        @Query("a") area: String
    ): MealListResponse

    @GET("filter.php")
    suspend fun filterByIngredient(
        @Query("i") ingredient: String
    ): MealListResponse

    companion object {
        private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

        fun create(): TheMealDbApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(TheMealDbApi::class.java)
        }
    }
}
