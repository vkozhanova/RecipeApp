package com.example.myapplication.ui.categories

import com.example.myapplication.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.FragmentListCategoriesBinding
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.findNavController
import com.example.myapplication.RecipeApplication


class CategoriesListFragment : Fragment() {
    private var _binding: FragmentListCategoriesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentListCategoriesBinding must not be null")

    private lateinit var categoriesListViewModel: CategoryListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (requireActivity().application as RecipeApplication).appContainer
        categoriesListViewModel = appContainer.categoriesListViewModelFactory.create()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.headerImage.setImageResource(R.drawable.bcg_categories)

        val adapter = CategoriesListAdapter(emptyList()) { categoryId ->
            categoriesListViewModel.onCategorySelected(categoryId)
        }

        binding.rvCategories.apply {
            this.adapter = adapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        categoriesListViewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.updateCategories(categories)

        }

        categoriesListViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                categoriesListViewModel.clearError()
            }
        }

        categoriesListViewModel.navigateToRecipes.observe(viewLifecycleOwner) { categoryId ->
            categoryId?.let {
                openRecipesByCategoryId(it)
                categoriesListViewModel.onNavigationComplete()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.bcgCategories.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = systemBars.top
            }
            insets
        }
    }


    private fun openRecipesByCategoryId(categoryId: Int) {
        val category = categoriesListViewModel.categories.value?.find { it.id == categoryId }

        if (category == null) {
            Toast.makeText(requireContext(), "Категория не найдена", Toast.LENGTH_SHORT).show()
            return
        }

        val direction = CategoriesListFragmentDirections
            .actionCategoriesListFragmentToRecipesListFragment(categoryId = categoryId)
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}