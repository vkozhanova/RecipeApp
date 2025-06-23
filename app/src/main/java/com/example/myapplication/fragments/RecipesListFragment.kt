package com.example.myapplication.fragments

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
import com.example.myapplication.ARG_CATEGORY_ID
import com.example.myapplication.ARG_CATEGORY_IMAGE_URL
import com.example.myapplication.ARG_CATEGORY_NAME
import com.example.myapplication.ASSETS_BASE_PATH
import com.example.myapplication.STUB
import com.example.myapplication.databinding.FragmentListRecipesBinding
import com.example.myapplication.RecipesListAdapter
import com.example.myapplication.NavigationUtils

class RecipesListFragment : Fragment() {
    private var _binding: FragmentListRecipesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentListRecipesBinding must not be null")

    private var categoryId: Int? = null
    private var categoryName: String? = null
    private var categoryImageUrl: String? = null

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

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.bcgRecipes.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = systemBars.top
            }
            insets
        }

        arguments?.let { bundle ->
            categoryId = bundle.getInt(ARG_CATEGORY_ID)
            categoryName = bundle.getString(ARG_CATEGORY_NAME)
            categoryImageUrl = bundle.getString(ARG_CATEGORY_IMAGE_URL)
        }

        categoryName?.let { binding.titleText.text = it }
        categoryImageUrl?.let { fileName ->
            Glide.with(requireContext())
                .load("$ASSETS_BASE_PATH$fileName")
                .into(binding.headerImage)
        }
        val recipes = STUB.getRecipesByCategoryId(categoryId ?: 0)
        val adapter = RecipesListAdapter(
            recipes = recipes,
            onItemClick = { recipe ->
                NavigationUtils.openRecipeByRecipeId(this@RecipesListFragment, recipe.id)
            }
        )

        binding.rvRecipes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecipes.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}