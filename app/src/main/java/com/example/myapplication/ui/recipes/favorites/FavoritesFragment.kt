package com.example.myapplication.ui.recipes.favorites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.FAVORITES_KEY
import com.example.myapplication.data.PREFS_NAME
import com.example.myapplication.data.STUB
import com.example.myapplication.databinding.FragmentFavoritesBinding
import com.example.myapplication.model.Recipe
import com.example.myapplication.ui.NavigationUtils
import com.example.myapplication.ui.recipes.recipeList.RecipesListAdapter

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentFavoritesBinding must not be null")

    private lateinit var adapter: RecipesListAdapter
    private var favoriteRecipes: List<Recipe> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.titleText.text = getString(R.string.favorites_title)

        Glide.with(requireContext())
            .load(R.drawable.bcg_favorites)
            .into(binding.headerImageFavorites)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.bcgFavorites.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = systemBars.top
            }
            insets
        }

        initRecycler()
        loadFavoritesRecipes()
    }

    private fun initRecycler() {
        adapter = RecipesListAdapter(
            recipes = favoriteRecipes,
            onItemClick = { recipe ->
                NavigationUtils.openRecipeByRecipeId(
                    this@FavoritesFragment,
                    recipe.id
                )
            },
        )

        binding.rvFavorites.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoritesFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun loadFavoritesRecipes() {
        val favoriteIds = getFavorites()
        favoriteRecipes = STUB.getRecipesByIds(favoriteIds)
        updateUI()
    }

    private fun updateUI() {
        adapter.updateRecipes(favoriteRecipes)
        if (favoriteRecipes.isEmpty()) {
            showEmptyState()
        } else {
            hideEmptyState()
        }
    }

    private fun showEmptyState() {
        binding.rvFavorites.visibility = View.GONE
        binding.emptyStateText.text = getString(R.string.empty_favorites_message)
        binding.emptyStateView.visibility = View.VISIBLE
    }

    private fun hideEmptyState() {
        binding.rvFavorites.visibility = View.VISIBLE
        binding.emptyStateView.visibility = View.GONE
    }

    fun saveFavorites(recipesIds: Set<Int>) {
        val sharedPrefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        sharedPrefs.edit()
            .putStringSet(FAVORITES_KEY, recipesIds.map { it.toString() }.toSet())
            .apply()
    }

    fun getFavorites(): Set<Int> {
        val sharedPrefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        return sharedPrefs.getStringSet(FAVORITES_KEY, emptySet())?.mapNotNull { it.toIntOrNull() }
            ?.toSet() ?: emptySet()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}