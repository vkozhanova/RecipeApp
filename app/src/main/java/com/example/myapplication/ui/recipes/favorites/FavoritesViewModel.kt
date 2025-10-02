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

    val _error = MutableLiveData<String?>()
    val error: LiveData<String?>
        get() = _error

    private val sharedPrefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val favoriteIds = getFavorites()
                if (favoriteIds.isEmpty()) {
                    _favoritesRecipe.postValue(emptyList())
                    return@launch
                }
                Log.d("Favorites", "Loading recipes for IDs: $favoriteIds")

                val recipes = repository.getRecipesByIds(favoriteIds).orEmpty()

                _favoritesRecipe.postValue(recipes)

            } catch (e: Exception) {
                Log.d("Favorites", "Error loading favorites", e)
                _error.postValue("Ошибка получения данных")
            }
        }
    }

    fun onRecipeClicked(recipeId: Int) {
        _navigateToRecipe.value = recipeId
    }

    fun resetNavigation() {
        _navigateToRecipe.value = null
    }

    fun getFavorites(): Set<Int> {
        val favoriteSet = sharedPrefs.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()

        return favoriteSet.mapNotNull { it.toIntOrNull() }.toSet()

    }

    fun clearError() {
        _error.value = null
    }
}