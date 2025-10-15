package com.example.myapplication.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.RecipesRepository
import com.example.myapplication.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val recipesRepository: RecipesRepository,
) : ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>>
        get() = _categories

    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    private val _navigateToRecipes = MutableLiveData<Int?>()
    val navigateToRecipes: LiveData<Int?>
        get() = _navigateToRecipes

    init {
        loadCategories()
    }

    fun loadCategories() {

        viewModelScope.launch {
            try {
                val cachedCategories = recipesRepository.getCategoriesFromCache()
                if (cachedCategories.isNotEmpty()) {
                    _categories.value = cachedCategories
                }

                val categories = recipesRepository.getCategories()
                if (categories != null) {
                    _categories.value = categories
                    recipesRepository.saveCategoriesToCache(categories)
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
}