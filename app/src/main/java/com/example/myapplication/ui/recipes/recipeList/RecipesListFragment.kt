package com.example.myapplication.ui.recipes.recipeList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.BASE_IMAGE_URL
import com.example.myapplication.databinding.FragmentListRecipesBinding
import kotlin.getValue


class RecipesListFragment : Fragment() {
    private var _binding: FragmentListRecipesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentListRecipesBinding must not be null")
    private val viewModel: RecipesListViewModel by viewModels()
    private val args: RecipesListFragmentArgs by navArgs()
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

        val categoryId = args.categoryId
        viewModel.loadRecipes(categoryId)

        adapter = RecipesListAdapter(emptyList()) { recipe ->
            viewModel.onRecipeClicked(recipe.id)
        }

        binding.rvRecipes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RecipesListFragment.adapter
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            adapter.updateRecipes(state.recipes)


        state.category?.let {
                binding.titleText.text = it.title
                Log.d("RecipesFragment", "Category imageUrl = ${it.imageUrl}")
                Glide.with(requireContext())
                    .load("${BASE_IMAGE_URL}${it.imageUrl}")
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_error)
                    .into(binding.headerImage)
            }


        state.error?.let { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }


        state.navigateToId?.let { recipeId ->
                val direction =
                    RecipesListFragmentDirections.actionRecipesListFragmentToRecipeFragment(recipeId = recipeId)
                findNavController().navigate(direction)
                viewModel.resetNavigation()
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