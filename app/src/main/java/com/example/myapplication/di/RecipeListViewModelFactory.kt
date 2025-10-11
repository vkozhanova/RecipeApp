package com.example.myapplication.di

import com.example.myapplication.data.RecipesRepository
import com.example.myapplication.ui.recipes.recipeList.RecipesListViewModel

class RecipeListViewModelFactory(
    private val recipesRepository: RecipesRepository,
) : Factory<RecipesListViewModel>{

    override fun create(): RecipesListViewModel{
        return RecipesListViewModel(recipesRepository)
    }
}