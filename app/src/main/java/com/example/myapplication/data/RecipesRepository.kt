package com.example.myapplication.data

import android.content.Context
import android.util.Log
import androidx.room.Room
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

class RecipesRepository(val context: Context) {
    private val apiService: RecipeApiService
    private val db: AppDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "databaseRecipe"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    private val categoriesDao: CategoriesDao by lazy {
        db.categoriesDao()
    }

    private val recipeDao: RecipeDao by lazy {
        db.recipesDao()
    }

    suspend fun getCategoriesFromCache(): List<Category> {
        return withContext(Dispatchers.IO) {
            categoriesDao.getCategories()
        }
    }

    suspend fun getFavoritesFromDatabase(): List<Recipe> {
        return withContext(Dispatchers.IO) {
            recipeDao.getFavorites()
        }
    }

    suspend fun saveFavoritesToDatabase(favorites: List<Recipe>) {
        withContext(Dispatchers.IO) {
            recipeDao.insertAll(favorites)
        }
    }

    suspend fun saveCategoriesToCache(categories: List<Category>) {
        withContext(Dispatchers.IO) {
            categoriesDao.insertAll(categories)
        }
    }

    suspend fun getRecipesFromCache(): List<Recipe> {
        return withContext(Dispatchers.IO) {
            recipeDao.getRecipes()
        }
    }

    suspend fun getRecipesByCategoryIdFromCache(categoryId: Int): List<Recipe> {
        return withContext(Dispatchers.IO) {
            recipeDao.getRecipesByCategoryId(categoryId)
        }

    }

    suspend fun saveRecipesToCache(recipes: List<Recipe>) {
        withContext(Dispatchers.IO) {
            recipeDao.insertAll(recipes)
        }
    }

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

            executeCall(apiService.getRecipesByIds(ids.joinToString(",")))
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