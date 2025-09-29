package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.model.Category
import com.example.myapplication.model.Recipe
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit

object RecipesRepository {
    private val apiService: RecipeApiService

    init {
        val contentType = "application/json".toMediaType()
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://recipes.androidsprint.ru/api/")
            .client(client)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()

        apiService = retrofit.create(RecipeApiService::class.java)
    }

    suspend fun getCategories(): List<Category>? {
        return withContext(Dispatchers.IO) { executeCall(apiService.getCategories()) }
    }

    suspend fun getRecipesByCategoryId(categoryId: Int): List<Recipe>? {
        return withContext(Dispatchers.IO) {
            executeCall(
                apiService.getRecipesByCategoryId(
                    categoryId
                )
            )
        }
    }

    suspend fun getRecipeById(id: Int): Recipe? {
        return withContext(Dispatchers.IO) { executeCall(apiService.getRecipeById(id)) }
    }

    suspend fun getRecipesByIds(ids: Set<Int>): List<Recipe>? {
        return withContext(Dispatchers.IO) {

            if (ids.isEmpty()) return@withContext emptyList()

            val idsParam = ids.joinToString(",")

            executeCall(apiService.getRecipesByIds(idsParam))
        }
    }

    private fun <T> executeCall(call: Call<T>): T? {
        return try {
            val response: Response<T> = call.execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("!!!", "Ошибка запроса: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("!!!", "Ошибка запроса", e)
            null
        }
    }
}