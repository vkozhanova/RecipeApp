package com.example.myapplication.ui.recipes.recipe


import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.FAVORITES_KEY
import com.example.myapplication.data.PREFS_NAME
import com.example.myapplication.data.RecipesRepository
import com.example.myapplication.model.Ingredient
import com.example.myapplication.model.Recipe
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RecipesRepository(application)
    private val sharedPrefs = getApplication<Application>()
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
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

                val recipe = repository.getRecipeById(recipeId)
                Log.d("RecipeViewModel", "Recipe found: ${recipe?.title ?: "null"}")

                val isFavorite = getFavoritesFromCache().contains(recipeId)

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


    fun getFavoritesFromCache(): Set<Int> {
        val favorites = sharedPrefs.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()
        Log.d("Favorites", "Loaded favorites: $favorites")
        return favorites.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun saveFavoritesToCache(favorites: Set<Int>) {
        sharedPrefs.edit()
            .putStringSet(FAVORITES_KEY, favorites.map { it.toString() }.toSet())
            .apply()
    }

    fun onFavoritesClicked() {
        val currentState = state.value ?: return
        val recipeId = currentState.recipe?.id ?: return
        val newFavoriteState = !currentState.isFavorite

        _state.value = currentState.copy(isFavorite = newFavoriteState)

        updateFavorites(recipeId, newFavoriteState)
        saveFavoritesToCache(getFavoritesFromCache())
    }

    fun updateFavorites(recipeId: Int, isFavorite: Boolean) {
        val newFavorites = getFavoritesFromCache().toMutableSet().apply {
            if (isFavorite) add(recipeId) else remove(recipeId)
        }
        saveFavoritesToCache(newFavorites)
    }

    fun setPortionsCount(count: Int) {
        val currentState = state.value ?: return

        _state.value = currentState.copy(portionsCount = count)
    }

    fun clearError() {
        _error.value = null
    }
}