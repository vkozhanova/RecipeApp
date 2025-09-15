package com.example.myapplication.ui.recipes.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.ASSETS_BASE_PATH
import com.example.myapplication.data.INVALID_RECIPE_ID
import com.example.myapplication.databinding.FragmentRecipeBinding
import com.google.android.material.divider.MaterialDividerItemDecoration

class PortionSeekBarListener(private var onChangeIngredients: (Int) -> Unit) :
    SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(
        seekBar: SeekBar?,
        progress: Int,
        fromUser: Boolean
    ) {
        onChangeIngredients(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}

class RecipeFragment : Fragment() {
    private var _binding: FragmentRecipeBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentRecipeBinding must not be null")
    private val viewModel: RecipeViewModel by viewModels()
    private val args: RecipeFragmentArgs by navArgs()
    private var ingredientsAdapter: IngredientsAdapter? = null
    private var methodAdapter: MethodAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWindowInsets()
        initUI()
    }

    private fun initUI() {
        val recipeId = args.recipeId
        Log.d("RecipeFragment", "Initializing UI for recipe ID: $recipeId")

        if (recipeId == INVALID_RECIPE_ID) {
            Log.e("RecipeFragment", "Invalid recipe ID")
            return
        }

        viewModel.loadRecipe(recipeId)

        initRecyclers()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            Log.d("RecipeFragment", "State updated: ${state.recipe?.title}")

            state.recipe?.let { recipe ->
                binding.titleText.text = recipe.title

                val adjustedIngredients = viewModel.getAdjustedIngredients()

                ingredientsAdapter?.updateIngredients(adjustedIngredients)
                methodAdapter?.updateData(recipe.method)
            }

            state.recipe?.imageUrl?.let { imageUrl ->
                Glide.with(requireContext())
                    .load("${ASSETS_BASE_PATH}$imageUrl")
                    .into(binding.headerImage)
            } ?: run {
                Log.e("RecipeFragment", "Failed to load image for recipe: ${state.recipe?.title}")
            }

            updateFavoriteIcon(state.isFavorite)
            Log.d("RecipeFragment", "Favorite state: ${state.isFavorite}")

            updatePortionsUI(state.portionsCount)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        binding.iconFavorites.setOnClickListener {
            viewModel.onFavoritesClicked()
        }

        binding.seekbar.setOnSeekBarChangeListener(PortionSeekBarListener { currentProgress ->
            viewModel.setPortionsCount(currentProgress)
        })
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val iconRes = if (isFavorite) {
            R.drawable.ic_heart
        } else {
            R.drawable.ic_heart_empty
        }
        binding.iconFavorites.setImageResource(iconRes)
    }

    private fun updatePortionsUI(portionsCount: Int) {
        binding.tvPortionsValue.text = portionsCount.toString()
        if (binding.seekbar.progress != portionsCount) {
            binding.seekbar.progress = portionsCount
        }
    }

    private fun initRecyclers() {
        Log.d("RecipeFragment", "Initializing recyclers")

        ingredientsAdapter = IngredientsAdapter(emptyList())

        binding.rvIngredients.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ingredientsAdapter
            setHasFixedSize(true)
            addItemDecoration(
                createDivider()
            )
        }

        methodAdapter = MethodAdapter()
        binding.rvMethod.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = methodAdapter
            setHasFixedSize(true)
            addItemDecoration(createDivider())
        }
    }

    private fun createDivider(): MaterialDividerItemDecoration {
        return MaterialDividerItemDecoration(requireContext(), RecyclerView.VERTICAL).apply {
            isLastItemDecorated = false
            dividerInsetStart = resources.getDimensionPixelSize(R.dimen.divider_padding)
            dividerInsetEnd = resources.getDimensionPixelSize(R.dimen.divider_padding)
            dividerColor = resources.getColor(R.color.divider_light_gray_color, null)
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = systemBars.top
            }
            insets
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        ingredientsAdapter = null
        methodAdapter = null
    }
}