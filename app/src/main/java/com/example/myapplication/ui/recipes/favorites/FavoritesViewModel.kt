package com.example.myapplication.ui.recipes.favorites

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.FAVORITES_KEY
import com.example.myapplication.data.PREFS_NAME
import com.example.myapplication.data.RecipesRepository
import com.example.myapplication.model.Recipe
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RecipesRepository(application)

    private val _favoritesRecipe = MutableLiveData<List<Recipe>>()
    val favoritesRecipe: LiveData<List<Recipe>>
        get() = _favoritesRecipe

    private val _navigateToRecipe = MutableLiveData<Int?>()
    val navigateToRecipe: LiveData<Int?>
        get() = _navigateToRecipe

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?>
        get() = _error

    private val sharedPrefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val favoritesFromDatabase = repository.getFavoritesFromDatabase()

                if (favoritesFromDatabase.isNotEmpty()) {
                    _favoritesRecipe.postValue(favoritesFromDatabase)
                    return@launch
                }

                val favoriteIds = getFavoritesFromCache()
                if (favoriteIds.isNotEmpty()) {
                    val networkFavoritesRecipes =
                        repository.getRecipesByIds(favoriteIds) ?: emptyList()
                    repository.saveFavoritesToDatabase(networkFavoritesRecipes)
                    _favoritesRecipe.postValue(networkFavoritesRecipes)
                    return@launch
                }

                val cacheRecipes = repository.getFavoritesFromDatabase()
                _favoritesRecipe.postValue(cacheRecipes)

            } catch (e: Exception) {
                Log.d("!!!", "Ошибка при загрузке избранных рецептов", e)
                _error.postValue("Ошибка получения данных")
            }
        }
    }


    fun getFavoritesFromCache(): Set<Int> {
        val favoriteSet = sharedPrefs.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()

        return favoriteSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun onRecipeClicked(recipeId: Int) {
        _navigateToRecipe.value = recipeId
    }

    fun resetNavigation() {
        _navigateToRecipe.value = null
    }

    fun clearError() {
        _error.value = null
    }
}