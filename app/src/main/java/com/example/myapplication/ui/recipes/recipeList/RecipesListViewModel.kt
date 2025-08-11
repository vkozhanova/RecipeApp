package com.example.myapplication.ui.recipes.recipeList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.STUB
import com.example.myapplication.model.Recipe

class RecipesListViewModel(application: Application) : AndroidViewModel(application) {
    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>>
        get() = _recipes

    private val _navigateToId = MutableLiveData<Int?>()
    val navigateToId: LiveData<Int?>
        get() = _navigateToId

    fun loadRecipes(categoryId: Int) {
        _recipes.value = STUB.getRecipesByCategoryId(categoryId)
    }

    fun onRecipeClicked(recipeId: Int) {
        _navigateToId.value = recipeId
    }

    fun resetNavigation() {
        _navigateToId.value = null
    }
}