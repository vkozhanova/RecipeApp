package com.example.myapplication.di

import com.example.myapplication.data.RecipesRepository
import com.example.myapplication.ui.categories.CategoryListViewModel

class CategoriesListViewModelFactory(
    private val recipesRepository: RecipesRepository
) : Factory<CategoryListViewModel>{

    override fun create(): CategoryListViewModel {
        return CategoryListViewModel(recipesRepository)
    }
}