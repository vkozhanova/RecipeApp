package com.example.myapplication.ui.recipes.recipe


import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.FAVORITES_KEY
import com.example.myapplication.data.PREFS_NAME
import com.example.myapplication.data.STUB
import com.example.myapplication.model.Recipe
import java.io.IOException
import java.io.InputStream

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPrefs = getApplication<Application>()
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _state = MutableLiveData<RecipeState>()
    val state: LiveData<RecipeState>
        get() = _state

    private var currentRecipeId: Int = -1

    init {
        Log.i("RecipeViewModel", "VIEWMODEL INITIALIZED")
        _state.value = RecipeState()
    }

    data class RecipeState(
        val recipe: Recipe? = null,
        val isFavorite: Boolean = false,
        val portionsCount: Int = 1,
        val recipeImage: Drawable? = null
    )

     fun loadRecipe(recipeId: Int) {
        //TODO(Загрузить рецепт по id из сети или базы данных)
        if (currentRecipeId == recipeId) return

        currentRecipeId = recipeId
        Log.d("RecipeViewModel", "Loading recipe ID: $recipeId")

        val recipe = STUB.getRecipeById(recipeId)
        Log.d("RecipeViewModel", "Recipe found: ${recipe?.title ?: "null"}")

        val isFavorite = getFavorites().contains(recipeId.toString())

         val currentState = state.value ?: RecipeState()

         val recipeImage = recipe?.imageUrl?.let { imageName ->
             loadImageFromAssets(imageName)
         }

         _state.value = currentState.copy(
             recipe = recipe,
             isFavorite = isFavorite,
             recipeImage = recipeImage
        )
    }
    private fun loadImageFromAssets(imageName: String): Drawable? {
        return try {
            val inputStream: InputStream = getApplication<Application>().assets.open(imageName)
            Drawable.createFromStream(inputStream, null).apply {
                inputStream.close()
            }
        } catch (e: IOException) {
            Log.e("RecipeViewModel", "Error loading image: ${e.message}", e)
            null
        }
    }

    fun getFavorites(): Set<String> {
        return sharedPrefs.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()
    }

    fun onFavoritesClicked() {
        val currentState = state.value ?: return
        val recipeId = currentState.recipe?.id ?: return
        val newFavoriteState = !currentState.isFavorite

        updateFavorites(recipeId, newFavoriteState)

        _state.value = currentState.copy(isFavorite = newFavoriteState)
    }

    fun updateFavorites(recipeId: Int, isFavorite: Boolean) {
        val newFavorites = getFavorites().toMutableSet().apply {
            if (isFavorite) add(recipeId.toString()) else remove(recipeId.toString())
        }
        saveFavorites(newFavorites)
    }

    fun saveFavorites(favorites: Set<String>) {
        sharedPrefs.edit()
            .putStringSet(FAVORITES_KEY, favorites)
            .apply()
    }

    fun setPortionsCount(count: Int) {
        val currentState = state.value ?: return

        _state.value = currentState.copy(portionsCount = count)
    }
}