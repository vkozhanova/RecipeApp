package com.example.myapplication.ui.recipes.favorites

import android.app.Application
import android.content.Context
import android.util.Log

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.myapplication.data.FAVORITES_KEY
import com.example.myapplication.data.PREFS_NAME
import com.example.myapplication.data.STUB
import com.example.myapplication.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableLiveData(FavoritesState())
    val state: LiveData<FavoritesState>
        get() = _state

    data class FavoritesState(
        val favoritesRecipe: List<Recipe> = emptyList(),
        val navigateToRecipe: Int? = null
    )

    val favoritesRecipe: LiveData<List<Recipe>> = state.map { it.favoritesRecipe }
    val  navigateToRecipe: LiveData<Int?> = state.map { it.navigateToRecipe }
    private val sharedPrefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


    init {
        loadFavorites()
    }

    fun loadFavorites() {
        try {
            val favoriteIds = getFavorites()
            Log.d("Favorites", "Loading recipes for IDs: $favoriteIds")
            _state.value = _state.value?.copy(STUB.getRecipesByIds(favoriteIds))
        } catch (e: Exception) {
            Log.d("Favorites", "Error loading favorites", e)
            _state.value = _state.value?.copy(favoritesRecipe = emptyList())
        }
    }

    fun onRecipeClicked(recipeId: Int) {
        _state.value = _state.value?.copy(navigateToRecipe = recipeId)
    }

    fun resetNavigation() {
        _state.value = _state.value?.copy(navigateToRecipe = null)
    }

    fun getFavorites(): Set<Int> {
        val favoriteSet = sharedPrefs.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()

        return favoriteSet.mapNotNull { it.toIntOrNull() }.toSet()

    }
}