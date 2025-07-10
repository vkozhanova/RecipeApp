package com.example.myapplication.ui.recipes.recipe

import androidx.lifecycle.ViewModel
import com.example.myapplication.model.Ingredient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecipeViewModel(): ViewModel() {
    private val _state = MutableStateFlow(RecipeState())
    val state: StateFlow<RecipeState> = _state.asStateFlow()
    data class RecipeState (
        val id: Int? = null,
        val title: String? = null,
        val ingredients: List<Ingredient> = emptyList(),
        val method: List<String> = emptyList(),
        val imageUrl: String? = null,
        val isFavorite: Boolean = false
    )
}