package com.example.myapplication.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.RecipesRepository
import com.example.myapplication.model.Category
import java.util.concurrent.Executors

class CategoryListViewModel() : ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>>
        get() = _categories

    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    private val _navigateToRecipes = MutableLiveData<Int?>()
    val navigateToRecipes: LiveData<Int?>
        get() = _navigateToRecipes

    private val executor = Executors.newSingleThreadExecutor()

    init {
        loadCategories()
    }

    fun loadCategories() {

        executor.execute {
            try {
                val categories = RecipesRepository.getCategories()
                if (categories != null) {
                    _categories.postValue(categories)
                } else {
                    _error.postValue("Ошибка при получении данных")
                }
            } catch (e: Exception) {
                _error.postValue("Ошибка при получении данных")
            }
        }

    }

    fun onCategorySelected(categoryId: Int) {
        _navigateToRecipes.value = categoryId
    }

    fun onNavigationComplete() {
        _navigateToRecipes.value = null
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }
}