package com.example.myapplication.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.data.ARG_CATEGORY_ID
import com.example.myapplication.data.ARG_CATEGORY_IMAGE_URL
import com.example.myapplication.data.ARG_CATEGORY_NAME
import com.example.myapplication.R
import com.example.myapplication.data.STUB
import com.example.myapplication.databinding.FragmentListCategoriesBinding
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import com.example.myapplication.ui.recipes.recipeList.RecipesListFragment
import kotlin.getValue

class CategoriesListFragment : Fragment() {
    private var _binding: FragmentListCategoriesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentListCategoriesBinding must not be null")

    private val viewModel: CategoryListViewModel by viewModels()

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

        val adapter = CategoriesListAdapter(emptyList()) { categoryId ->
            viewModel.onCategorySelected(categoryId)
        }

        binding.rvCategories.adapter = adapter

        binding.rvCategories.layoutManager = GridLayoutManager(requireContext(), 2)

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.updateCategories(categories)
        }

        viewModel.navigateToRecipes.observe(viewLifecycleOwner) { categoryId ->
            categoryId?.let {
                openRecipesByCategoryId(it)
                viewModel.onNavigationComplete()
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

    fun openRecipesByCategoryId(categoryId: Int) {
        val category = STUB.getCategories().find { it.id == categoryId }
        val categoryName = category?.title ?: ""
        val categoryImageUrl = category?.imageUrl ?: ""

        val bundle = Bundle().apply {
            putInt(ARG_CATEGORY_ID, categoryId)
            putString(ARG_CATEGORY_NAME, categoryName)
            putString(ARG_CATEGORY_IMAGE_URL, categoryImageUrl)
        }

        parentFragmentManager.commit {
            replace<RecipesListFragment>(R.id.mainContainer, args = bundle)
            addToBackStack(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}