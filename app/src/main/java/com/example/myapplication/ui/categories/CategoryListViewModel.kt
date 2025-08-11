package com.example.myapplication.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.STUB
import com.example.myapplication.model.Category

class CategoryListViewModel(application: Application) : AndroidViewModel(application) {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>>
        get() = _categories
    private val _navigateToRecipes = MutableLiveData<Int?>()
    val navigateToRecipes: LiveData<Int?>
        get() = _navigateToRecipes

    init {
        loadCategories()
    }

    fun loadCategories() {
        _categories.value = STUB.getCategories()
    }

    fun onCategorySelected(categoryId: Int) {
        _navigateToRecipes.value = categoryId
    }

    fun onNavigationComplete() {
        _navigateToRecipes.value = null
    }
}