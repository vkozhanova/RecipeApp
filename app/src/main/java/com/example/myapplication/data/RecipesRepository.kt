package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.model.Category
import com.example.myapplication.model.Recipe
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit

class RecipesRepository() {
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

    fun getCategories(): List<Category>? {
        return executeCall(apiService.getCategories())
    }

    fun getRecipesByCategoryId(categoryId: Int): List<Recipe>? {
        return executeCall(apiService.getRecipesByCategoryId(categoryId))
    }

    fun getRecipeById(id: Int): Recipe? {
        return executeCall(apiService.getRecipeById(id))
    }

    fun getRecipesByIds(ids: Set<Int>): List<Recipe>? {
        if (ids.isEmpty()) return emptyList()

        val idsParam = ids.joinToString(",")

        return executeCall(apiService.getRecipesByIds(idsParam))
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