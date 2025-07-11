package com.example.myapplication.ui.recipes.recipe

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.data.ARG_RECIPE
import com.example.myapplication.data.ASSETS_BASE_PATH
import com.example.myapplication.data.FAVORITES_KEY
import com.example.myapplication.data.PREFS_NAME
import com.example.myapplication.R
import com.example.myapplication.model.Recipe
import com.example.myapplication.databinding.FragmentRecipeBinding
import com.google.android.material.divider.MaterialDividerItemDecoration

class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentRecipeBinding must not be null")

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.bcgRecipes.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = systemBars.top
            }
            insets
        }

        binding.iconFavorites.setOnClickListener {
            viewModel.state.value?.let { state ->
                viewModel.setIsFavorite(!state.isFavorite)
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            Log.i("!!!", "isFavorite: ${state.isFavorite}")

            state.recipe?.let { recipe ->
                Glide.with(requireContext())
                    .load("$ASSETS_BASE_PATH${recipe.imageUrl}")
                    .into(binding.headerImage)

                binding.titleText.text = recipe.title
                updateFavoriteIcon(state.isFavorite)

                if (binding.rvIngredients.adapter == null) {
                    initRecycler(recipe)
                }
                saveFavoritesStatus(recipe.id, state.isFavorite)
            }
        }

        val recipe = getRecipeFromArguments()
        if (recipe != null) {
            viewModel.setRecipe(recipe)
            val isFavorite = getFavorites().contains(recipe.id.toString())
            viewModel.setIsFavorite(isFavorite)
        }
    }

    private fun getRecipeFromArguments(): Recipe? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_RECIPE, Recipe::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_RECIPE)
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val iconRes = if (isFavorite) {
            R.drawable.ic_heart
        } else {
            R.drawable.ic_heart_empty
        }
        binding.iconFavorites.setImageResource(iconRes)
    }

    fun saveFavoritesStatus(recipeId: Int, isFavorite: Boolean) {
        val favorites = getFavorites().toMutableSet()
        if (isFavorite) {
            favorites.add(recipeId.toString())
        } else {
            favorites.remove(recipeId.toString())
        }
        saveFavorites(favorites)
    }

    fun saveFavorites(recipesIds: Set<String>) {
        val sharedPrefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        sharedPrefs.edit()
            .putStringSet(FAVORITES_KEY, recipesIds)
            .apply()
    }

    fun getFavorites(): MutableSet<String> {
        val sharedPrefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val favorites = sharedPrefs.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()

        return HashSet(favorites)
    }

    private fun initRecycler(recipe: Recipe) {
        val portionValue = binding.tvPortionsValue
        val seekbar = binding.seekbar
        portionValue.text = seekbar.progress.toString()

        val ingredientsAdapter = IngredientsAdapter(recipe.ingredients)
        ingredientsAdapter.updateIngredients(seekbar.progress)

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val portion = progress
                portionValue.text = portion.toString()
                ingredientsAdapter.updateIngredients(portion)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val divider = MaterialDividerItemDecoration(requireContext(), RecyclerView.VERTICAL).apply {
            isLastItemDecorated = false
            dividerInsetStart = resources.getDimensionPixelSize(R.dimen.divider_padding)
            dividerInsetEnd = resources.getDimensionPixelSize(R.dimen.divider_padding)
            dividerColor = resources.getColor(R.color.divider_light_gray_color, null)
        }

        binding.rvIngredients.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ingredientsAdapter
            addItemDecoration(divider)
            setHasFixedSize(true)
        }

        binding.rvMethod.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = MethodAdapter(recipe.method)
            addItemDecoration(divider)
            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}