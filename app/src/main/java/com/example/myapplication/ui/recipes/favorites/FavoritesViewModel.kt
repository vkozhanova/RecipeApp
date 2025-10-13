package com.example.myapplication.ui.recipes.favorites


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.RecipesRepository
import com.example.myapplication.model.Recipe
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val recipesRepository: RecipesRepository,
) : ViewModel() {
    private val _favoritesRecipe = MutableLiveData<List<Recipe>>()
    val favoritesRecipe: LiveData<List<Recipe>>
        get() = _favoritesRecipe

    private val _navigateToRecipe = MutableLiveData<Int?>()
    val navigateToRecipe: LiveData<Int?>
        get() = _navigateToRecipe

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?>
        get() = _error

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {

                val favoriteIds = recipesRepository.getFavoritesFromCache()

                val favoritesFromDatabase =
                    recipesRepository.getFavoritesFromDatabase().toMutableList()

                val missingIds = favoriteIds - favoritesFromDatabase.map { it.id }.toSet()

                if (missingIds.isNotEmpty()) {
                    val networkFavoritesRecipes =
                        recipesRepository.getRecipesByIds(missingIds.toSet()) ?: emptyList()
                    favoritesFromDatabase.addAll(networkFavoritesRecipes)
                    recipesRepository.saveFavoritesToDatabase(favoritesFromDatabase)
                }

                val favoriteMap = favoritesFromDatabase.associateBy { it.id }
                val sortedFavorites = favoriteIds.mapNotNull { favoriteMap[it] }

                _favoritesRecipe.postValue(sortedFavorites)
                _error.postValue(null)

            } catch (e: Exception) {
                Log.d("!!!", "Ошибка при загрузке избранных рецептов", e)
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

    fun clearError() {
        _error.value = null
    }
}