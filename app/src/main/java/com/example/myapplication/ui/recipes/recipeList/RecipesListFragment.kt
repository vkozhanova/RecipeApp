package com.example.myapplication.ui.recipes.recipeList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.data.ARG_CATEGORY_ID
import com.example.myapplication.data.ARG_CATEGORY_IMAGE_URL
import com.example.myapplication.data.ARG_CATEGORY_NAME
import com.example.myapplication.data.ASSETS_BASE_PATH
import com.example.myapplication.databinding.FragmentListRecipesBinding
import com.example.myapplication.model.Recipe
import com.example.myapplication.ui.NavigationUtils
import kotlin.getValue


class RecipesListFragment : Fragment() {
    private var _binding: FragmentListRecipesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentListRecipesBinding must not be null")
    private val viewModel: RecipesListViewModel by viewModels()

    private lateinit var adapter: RecipesListAdapter

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

        adapter = RecipesListAdapter(emptyList()) {recipe ->
            viewModel.onRecipeClicked(recipe.id)
        }

        binding.rvRecipes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RecipesListFragment.adapter
        }

        val categoryId = requireArguments().getInt(ARG_CATEGORY_ID)
        val categoryName = requireArguments().getString(ARG_CATEGORY_NAME) ?: ""
        val categoryImageUrl = requireArguments().getString(ARG_CATEGORY_IMAGE_URL)

        binding.titleText.text = categoryName

        categoryImageUrl?.let { fileName ->
            Glide.with(requireContext())
                .load("${ASSETS_BASE_PATH}$fileName")
                .into(binding.headerImage)

        }

        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            adapter.updateRecipes(recipes)
        }

        viewModel.navigateToId.observe(viewLifecycleOwner) { recipeId ->
            recipeId?.let {
                NavigationUtils.openRecipeByRecipeId(this, it)
                viewModel.resetNavigation()
            }
        }
        viewModel.loadRecipes(categoryId)

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