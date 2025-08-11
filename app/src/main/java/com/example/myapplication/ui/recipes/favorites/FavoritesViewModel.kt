package com.example.myapplication.ui.recipes.favorites

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.FAVORITES_KEY
import com.example.myapplication.data.PREFS_NAME
import com.example.myapplication.data.STUB
import com.example.myapplication.model.Recipe

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    val _favoriteRecipes = MutableLiveData<List<Recipe>>()
    val favoriteRecipes: LiveData<List<Recipe>> get() = _favoriteRecipes

    val _navigateToRecipe = MutableLiveData<Int?>()
    val navigateToRecipe: LiveData<Int?>
        get() = _navigateToRecipe
    private val sharedPrefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        try {
            val favoriteIds = getFavorites()
            Log.d("Favorites", "Loading recipes for IDs: $favoriteIds")
            _favoriteRecipes.value = STUB.getRecipesByIds(favoriteIds)
        } catch (e: Exception) {
            Log.d("Favorites", "Error loading favorites", e)
            _favoriteRecipes.value = emptyList()
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
}