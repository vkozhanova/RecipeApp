package com.example.myapplication.di

import com.example.myapplication.data.RecipesRepository
import com.example.myapplication.ui.recipes.recipe.RecipeViewModel

class RecipeViewModelFactory(
    private val recipesRepository: RecipesRepository
) : Factory<RecipeViewModel> {

    override fun create(): RecipeViewModel {
        return RecipeViewModel(recipesRepository)
    }
}