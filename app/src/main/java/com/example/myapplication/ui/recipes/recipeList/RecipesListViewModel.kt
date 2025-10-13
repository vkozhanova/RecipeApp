package com.example.myapplication.ui.recipes.recipeList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.RecipesRepository
import com.example.myapplication.model.Category
import com.example.myapplication.model.Recipe
import kotlinx.coroutines.launch

class RecipesListViewModel(
    private val recipesRepository: RecipesRepository,
) : ViewModel() {
    private val _state = MutableLiveData(RecipeListState())
    val state: LiveData<RecipeListState>
        get() = _state

    data class RecipeListState(
        val recipes: List<Recipe> = emptyList(),
        val category: Category? = null,
        val navigateToId: Int? = null,
        val error: String? = null
    )

    fun loadRecipes(categoryId: Int) {
        viewModelScope.launch {
            try {
                var categories = recipesRepository.getCategoriesFromCache()
                if (categories.isEmpty()) {
                    Log.d("!!!", "No categories in cache, loading from network")
                    val networkCategories = recipesRepository.getCategories()
                    if (networkCategories != null) {
                        recipesRepository.saveCategoriesToCache(networkCategories)
                        categories = networkCategories
                    }
                }
                val cachedRecipes = recipesRepository.getRecipesByCategoryIdFromCache(categoryId)
                if (cachedRecipes.isNotEmpty()) {
                    _state.value = _state.value?.copy(recipes = cachedRecipes)
                }

                val networkRecipes =
                    recipesRepository.getRecipesByCategoryId(categoryId) ?: emptyList()
                recipesRepository.saveRecipesToCache(networkRecipes)

                val category = categories.find { it.id == categoryId }
                _state.postValue(
                    _state.value?.copy(
                        recipes = networkRecipes,
                        category = category,
                        error = null
                    )
                )

            } catch (e: Exception) {
                Log.e("!!!", "Error loading recipes", e)
                _state.postValue(
                    _state.value?.copy(error = e.message ?: "Ошибка при получении данных")
                )
            }
        }
    }

    fun onRecipeClicked(recipeId: Int) {
        _state.value = _state.value?.copy(
            navigateToId = recipeId
        )
    }

    fun resetNavigation() {
        _state.value = _state.value?.copy(
            navigateToId = null
        )
    }

    fun clearError() {
        _state.value = _state.value?.copy(error = null)
    }
}