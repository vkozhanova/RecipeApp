package com.example.myapplication.fragments

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
import com.example.myapplication.ARG_CATEGORY_ID
import com.example.myapplication.ARG_CATEGORY_IMAGE_URL
import com.example.myapplication.ARG_CATEGORY_NAME
import com.example.myapplication.CategoriesListAdapter
import com.example.myapplication.R
import com.example.myapplication.STUB
import com.example.myapplication.databinding.FragmentListCategoriesBinding
import androidx.core.view.updateLayoutParams

class CategoriesListFragment : Fragment() {
    private var _binding: FragmentListCategoriesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentListCategoriesBinding must not be null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun initRecycler() {
        val categories = STUB.getCategories()
        val adapter = CategoriesListAdapter(categories)

        adapter.setOnItemClickListener(object : CategoriesListAdapter.OnItemClickListener {
            override fun onItemClick(categoryId: Int) {
                openRecipesByCategoryId(categoryId)
            }
        })
        binding.rvCategories.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCategories.adapter = adapter
    }

    private fun openRecipesByCategoryId(categoryId: Int) {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.bcgCategories.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = systemBars.top
            }
            insets
        }
            initRecycler()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }