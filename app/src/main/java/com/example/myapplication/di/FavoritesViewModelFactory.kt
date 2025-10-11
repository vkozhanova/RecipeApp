package com.example.myapplication.di

import com.example.myapplication.data.RecipesRepository
import com.example.myapplication.ui.recipes.favorites.FavoritesViewModel

class FavoritesViewModelFactory(
    private val recipesRepository: RecipesRepository
) : Factory<FavoritesViewModel>{

    override fun create(): FavoritesViewModel {
        return FavoritesViewModel(recipesRepository)
    }
}