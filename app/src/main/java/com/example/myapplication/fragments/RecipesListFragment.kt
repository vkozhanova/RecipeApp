package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.ARG_CATEGORY_ID
import com.example.myapplication.ARG_CATEGORY_IMAGE_URL
import com.example.myapplication.ARG_CATEGORY_NAME
import com.example.myapplication.databinding.FragmentListRecipesBinding

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

        arguments?.let { bundle ->
            categoryId = bundle.getInt(ARG_CATEGORY_ID)
            categoryName = bundle.getString(ARG_CATEGORY_NAME)
            categoryImageUrl = bundle.getString(ARG_CATEGORY_IMAGE_URL)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

