package com.example.myapplication.ui.recipes.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentFavoritesBinding
import com.example.myapplication.ui.recipes.recipeList.RecipesListAdapter

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentFavoritesBinding must not be null")
    private val viewModel: FavoritesViewModel by viewModels()

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

        val adapter = RecipesListAdapter(emptyList()) { recipe ->
            viewModel.onRecipeClicked(recipe.id)
        }

        binding.rvFavorites.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }

        viewModel.favoritesRecipe.observe(viewLifecycleOwner) { recipes ->
            adapter.updateRecipes(recipes)
            if (recipes.isEmpty()) showEmptyState() else hideEmptyState()
        }

        viewModel.navigateToRecipe.observe(viewLifecycleOwner) { recipeId ->
            recipeId?.let {
                val direction = FavoritesFragmentDirections.actionFavoritesFragmentToRecipeFragment(recipeId = it)
                try {
                    findNavController().navigate(direction)
                    viewModel.resetNavigation()
                } catch (e: Exception) {
                    viewModel.resetNavigation()
                }
            }
        }

        viewModel.loadFavorites()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.bcgFavorites.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = systemBars.top
            }
            insets
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}