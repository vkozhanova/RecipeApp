package com.example.myapplication.ui.recipes.recipe


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.FAVORITES_KEY
import com.example.myapplication.model.Recipe

interface PreferencesStorage {
    fun getFavorites(): Set<String>
    fun saveFavorites(favorites: Set<String>)
}

class SharedPreferencesStorage(
    private val sharedPreferences: SharedPreferences
) : PreferencesStorage {

    override fun getFavorites(): Set<String> {
        return sharedPreferences.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()
    }

    override fun saveFavorites(favorites: Set<String>) {
        sharedPreferences.edit().putStringSet(FAVORITES_KEY, favorites).apply()
    }
}

class RecipeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val sharedPrefs = context.getSharedPreferences("recipe_preferences", Context.MODE_PRIVATE)
        val storage = SharedPreferencesStorage(sharedPrefs)

        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeViewModel(storage) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class RecipeViewModel(
    private val storage: PreferencesStorage
) : ViewModel() {
    private val _state = MutableLiveData<RecipeState>()
    val state: LiveData<RecipeState>
        get() = _state

    init {
        Log.i("RecipeViewModel", "VIEWMODEL INITIALIZED")
        _state.value = RecipeState(isFavorite = false)
    }

    data class RecipeState(

        val recipe: Recipe? = null,
        val isFavorite: Boolean = false
    )

    fun setRecipe(recipe: Recipe) {
        val favorites = storage.getFavorites()
        val isFavorite = favorites.contains(recipe.id.toString())
        _state.value = _state.value?.copy(recipe = recipe, isFavorite = isFavorite)
    }

    fun setIsFavorite(isFavorite: Boolean) {
        _state.value?.recipe?.let { recipe ->
            val favorites = storage.getFavorites().toMutableSet()

            if (isFavorite) {
                favorites.add(recipe.id.toString())
            } else {
                favorites.remove(recipe.id.toString())
            }

            storage.saveFavorites(favorites)

            _state.value = _state.value?.copy(isFavorite = isFavorite)
        }
    }
}