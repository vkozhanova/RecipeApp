package com.example.myapplication.ui.recipes.recipeList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.myapplication.data.STUB
import com.example.myapplication.model.Recipe

class RecipesListViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableLiveData(RecipeListState())
    val state:LiveData<RecipeListState>
        get() = _state

    val recipes: LiveData<List<Recipe>> = state.map { it.recipes }
    val navigateToId: LiveData<Int?> = state.map { it.navigateToId }

    data class RecipeListState(
        val recipes: List<Recipe> = emptyList(),
        val navigateToId: Int? = null
    )
    fun loadRecipes(categoryId: Int) {
        _state.value = _state.value?.copy(
            recipes = STUB.getRecipesByCategoryId(categoryId)
        )
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
}