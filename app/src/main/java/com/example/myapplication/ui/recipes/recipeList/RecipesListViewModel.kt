package com.example.myapplication.ui.recipes.recipeList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.RecipesRepository
import com.example.myapplication.model.Category
import com.example.myapplication.model.Recipe
import kotlinx.coroutines.launch

class RecipesListViewModel(application: Application) : AndroidViewModel(application) {

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
                val recipes = RecipesRepository.getRecipesByCategoryId(categoryId) ?: emptyList()

                val category = RecipesRepository.getCategories()?.find { it.id == categoryId }

                _state.postValue(
                    _state.value?.copy(
                        recipes = recipes,
                        error = null
                    )
                )

            } catch (e: Exception) {
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