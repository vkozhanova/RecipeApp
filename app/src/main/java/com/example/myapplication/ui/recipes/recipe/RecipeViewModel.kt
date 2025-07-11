package com.example.myapplication.ui.recipes.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.Ingredient
import com.example.myapplication.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class RecipeViewModel() : ViewModel() {
    private val _state = MutableLiveData<RecipeState>()
    val state: LiveData<RecipeState>
        get() = _state

    init {
        Log.i("RecipeViewModel", "VIEWMODEL INITIALIZED")
        _state.value = RecipeState(isFavorite = false)
    }

    data class RecipeState(

        val recipe: Recipe? = null,
        val isFavorite: Boolean = false
    )

    fun setRecipe(recipe: Recipe) {
        _state.value = _state.value?.copy(recipe = recipe)
    }

    fun setIsFavorite(isFavorite: Boolean) {
        _state.value = _state.value?.copy(isFavorite = isFavorite)
    }
}