package com.example.myapplication.ui.recipes.recipeList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.RecipeApplication
import com.example.myapplication.data.BASE_IMAGE_URL
import com.example.myapplication.databinding.FragmentListRecipesBinding
import kotlin.getValue


class RecipesListFragment : Fragment() {
    private var _binding: FragmentListRecipesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentListRecipesBinding must not be null")

    private val args: RecipesListFragmentArgs by navArgs()
    private lateinit var adapter: RecipesListAdapter
    private lateinit var recipeListViewModel: RecipesListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (requireActivity().application as RecipeApplication).appContainer
        recipeListViewModel = appContainer.recipesListViewModelFactory.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryId = args.categoryId
        recipeListViewModel.loadRecipes(categoryId)

        adapter = RecipesListAdapter(emptyList()) { recipe ->
            recipeListViewModel.onRecipeClicked(recipe.id)
        }

        binding.rvRecipes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RecipesListFragment.adapter
        }

        recipeListViewModel.state.observe(viewLifecycleOwner) { state ->
            adapter.updateRecipes(state.recipes)


            state.category?.let {
                binding.titleText.text = it.title
                Glide.with(requireContext())
                    .load("${BASE_IMAGE_URL}${it.imageUrl}")
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_error)
                    .into(binding.headerImage)
            }


            state.error?.let { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                recipeListViewModel.clearError()
            }


            state.navigateToId?.let { recipeId ->
                val direction =
                    RecipesListFragmentDirections.actionRecipesListFragmentToRecipeFragment(recipeId = recipeId)
                findNavController().navigate(direction)
                recipeListViewModel.resetNavigation()
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.bcgRecipes.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = systemBars.top
            }
            insets
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}