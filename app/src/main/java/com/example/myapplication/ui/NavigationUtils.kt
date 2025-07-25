package com.example.myapplication.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.myapplication.data.ARG_RECIPE
import com.example.myapplication.R
import com.example.myapplication.ui.recipes.recipe.RecipeFragment

object NavigationUtils {
    fun openRecipeByRecipeId(fragment: Fragment, recipeId: Int) {
        val bundle = Bundle().apply {
            putInt(ARG_RECIPE, recipeId)
        }

        val recipeFragment = RecipeFragment().apply {
            arguments = bundle
        }

        fragment.parentFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, recipeFragment)
            .setReorderingAllowed(true)
            .addToBackStack("recipe_$recipeId")
            .commit()

        Log.d("Navigation", "Opened recipe ID: $recipeId")
    }
}