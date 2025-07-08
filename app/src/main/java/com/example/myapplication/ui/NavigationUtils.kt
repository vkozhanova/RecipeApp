package com.example.myapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myapplication.data.ARG_RECIPE
import com.example.myapplication.R
import com.example.myapplication.data.STUB
import com.example.myapplication.ui.recipes.recipe.RecipeFragment

object NavigationUtils {
    fun openRecipeByRecipeId(fragment: Fragment, recipeId: Int) {
        val recipe = STUB.getRecipeById(recipeId) ?: return
        val bundle = Bundle().apply {
            putParcelable(ARG_RECIPE, recipe)
        }

        val recipeFragment = RecipeFragment().apply {
            arguments = bundle
        }

        fragment.parentFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, recipeFragment)
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }
}