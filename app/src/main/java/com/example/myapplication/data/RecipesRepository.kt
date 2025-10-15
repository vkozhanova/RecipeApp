package com.example.myapplication.data


import android.content.SharedPreferences
import android.util.Log
import com.example.myapplication.di.IODispatcher
import com.example.myapplication.model.Category
import com.example.myapplication.model.Recipe
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class RecipesRepository @Inject constructor(
    private val recipeDao: RecipeDao,
    private val categoriesDao: CategoriesDao,
    private val recipeApiService: RecipeApiService,
    @IODispatcher private val ioDispatcher: CoroutineContext,
    private val sharedPrefs: SharedPreferences,
) {

    fun saveFavoritesToCache(favorites: Set<Int>) {
        sharedPrefs.edit()
            .putStringSet(FAVORITES_KEY, favorites.map { it.toString() }.toSet())
            .apply()
    }

    fun getFavoritesFromCache(): Set<Int> {
        val favoriteSet = sharedPrefs.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()

        return favoriteSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    suspend fun getCategoriesFromCache(): List<Category> {
        return withContext(ioDispatcher) {
            categoriesDao.getCategories()
        }
    }

    suspend fun getFavoritesFromDatabase(): List<Recipe> {
        return withContext(ioDispatcher) {
            recipeDao.getFavorites()
        }
    }

    suspend fun saveFavoritesToDatabase(favorites: List<Recipe>) {
        withContext(ioDispatcher) {
            recipeDao.insertAll(favorites)
        }
    }

    suspend fun saveCategoriesToCache(categories: List<Category>) {
        withContext(ioDispatcher) {
            categoriesDao.insertAll(categories)
        }
    }

    suspend fun getRecipesFromCache(): List<Recipe> {
        return withContext(ioDispatcher) {
            recipeDao.getRecipes()
        }
    }

    suspend fun getRecipesByCategoryIdFromCache(categoryId: Int): List<Recipe> {
        return withContext(ioDispatcher) {
            recipeDao.getRecipesByCategoryId(categoryId)
        }

    }

    suspend fun saveRecipesToCache(recipes: List<Recipe>) {
        withContext(ioDispatcher) {
            recipeDao.insertAll(recipes)
        }
    }


    suspend fun getCategories(): List<Category>? {
        return withContext(ioDispatcher) { executeCall(recipeApiService.getCategories()) }
    }

    suspend fun getRecipesByCategoryId(categoryId: Int): List<Recipe>? {
        return withContext(ioDispatcher) {
            executeCall(
                recipeApiService.getRecipesByCategoryId(
                    categoryId
                )
            )
        }
    }

    suspend fun getRecipeById(id: Int): Recipe? {
        return withContext(ioDispatcher) { executeCall(recipeApiService.getRecipeById(id)) }
    }

    suspend fun getRecipesByIds(ids: Set<Int>): List<Recipe>? {
        return withContext(ioDispatcher) {

            if (ids.isEmpty()) return@withContext emptyList()

            executeCall(recipeApiService.getRecipesByIds(ids.joinToString(",")))
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