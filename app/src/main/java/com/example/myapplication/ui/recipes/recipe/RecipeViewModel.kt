package com.example.myapplication.ui.recipes.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.RecipesRepository
import com.example.myapplication.model.Ingredient
import com.example.myapplication.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val recipesRepository: RecipesRepository,
) : ViewModel() {
    private val _state = MutableLiveData<RecipeState>()
    val state: LiveData<RecipeState>
        get() = _state

    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    init {
        Log.i("RecipeViewModel", "VIEWMODEL INITIALIZED")
        _state.value = RecipeState()
    }

    data class RecipeState(
        val recipe: Recipe? = null,
        val isFavorite: Boolean = false,
        val portionsCount: Int = 1
    )

    fun getAdjustedIngredients(): List<Ingredient> {
        return try {
            val state = state.value ?: return emptyList()
            val recipe = state.recipe ?: return emptyList()
            val portionsCount = state.portionsCount

            recipe.ingredients.map { ingredient ->
                try {

                    val quantityStr = ingredient.quantity.replace(",", ".")
                    val quantityValue = if (quantityStr.isNotEmpty()) {
                        BigDecimal(quantityStr).multiply(BigDecimal(portionsCount))
                            .setScale(1, RoundingMode.HALF_UP)
                            .stripTrailingZeros()
                            .toPlainString()
                    } else {
                        ingredient.quantity
                    }

                    ingredient.copy(quantity = quantityValue)
                } catch (e: Exception) {
                    ingredient
                }
            }
        } catch (e: Exception) {
            Log.e("RecipeViewModel", "Error in getAdjustedIngredients", e)
            emptyList()
        }
    }

    fun loadRecipe(recipeId: Int) {
        viewModelScope.launch {
            try {

                val recipe = recipesRepository.getRecipeById(recipeId)
                Log.d("RecipeViewModel", "Recipe found: ${recipe?.title ?: "null"}")

                val isFavorite = recipesRepository.getFavoritesFromCache().contains(recipeId)

                val currentState = state.value ?: RecipeState()

                if (recipe != null) {
                    _state.postValue(
                        currentState.copy(
                            recipe = recipe,
                            isFavorite = isFavorite,
                            portionsCount = currentState.portionsCount
                        )
                    )
                    _error.postValue(null)
                } else {
                    _error.postValue("Рецепт с ID $recipeId не найден")
                    _state.postValue(currentState.copy(recipe = null, isFavorite = false))
                }
            } catch (e: Exception) {
                _error.postValue("Ошибка при получении данных: ${e.message}")
            }
        }
    }

    fun onFavoritesClicked() {
        val currentState = state.value ?: return
        val recipeId = currentState.recipe?.id ?: return
        val newFavoriteState = !currentState.isFavorite

        _state.value = currentState.copy(isFavorite = newFavoriteState)

        updateFavorites(recipeId, newFavoriteState)

        viewModelScope.launch {
            try {
                val allFavorites = recipesRepository.getFavoritesFromCache()
                val existingFavorites = recipesRepository.getFavoritesFromDatabase().toMutableList()
                val updatesRecipes = recipesRepository.getRecipesByIds(allFavorites) ?: emptyList()
                val mergedFavorites = (existingFavorites + updatesRecipes).distinctBy { it.id }

                mergedFavorites.forEach { recipe ->
                    if (recipe.id == recipeId) {
                        recipe.isFavorite = newFavoriteState
                    }
                }
                recipesRepository.saveFavoritesToDatabase(mergedFavorites)
            } catch (e: Exception) {
                Log.e("!!!", "Ошибка при обновлении избранных рецептов", e)
            }
        }
    }

    fun updateFavorites(recipeId: Int, isFavorite: Boolean) {
        val newFavorites = recipesRepository.getFavoritesFromCache().toMutableSet().apply {
            if (isFavorite) add(recipeId) else remove(recipeId)
        }
        recipesRepository.saveFavoritesToCache(newFavorites)

        viewModelScope.launch {
            try {
                val recipes = recipesRepository.getRecipesByIds(newFavorites)
                recipes?.forEach { recipe ->
                    recipe.isFavorite = isFavorite
                }
                recipesRepository.saveFavoritesToDatabase(recipes ?: emptyList())
            } catch (e: Exception) {
                Log.e("!!!", "Ошибка при обновлении избранных рецептов в базе данных", e)
            }

        }
    }

    fun setPortionsCount(count: Int) {
        val currentState = state.value ?: return

        _state.value = currentState.copy(portionsCount = count)
    }

    fun clearError() {
        _error.value = null
    }
}