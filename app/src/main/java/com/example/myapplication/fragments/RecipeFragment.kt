package com.example.myapplication.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.ARG_RECIPE
import com.example.myapplication.ASSETS_BASE_PATH
import com.example.myapplication.IngredientsAdapter
import com.example.myapplication.MethodAdapter
import com.example.myapplication.R
import com.example.myapplication.Recipe
import com.example.myapplication.databinding.FragmentRecipeBinding
import com.google.android.material.divider.MaterialDividerItemDecoration

class RecipeFragment : Fragment() {
    private var isFavorite: Boolean = false
    private var _binding: FragmentRecipeBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentRecipeBinding must not be null")

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

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.bcgRecipes.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = systemBars.top
            }
            insets
        }
        getRecipeFromArguments()?.let { recipe ->
            initUI(recipe)
            initRecycler(recipe)
        }
    }

    private fun getRecipeFromArguments(): Recipe? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_RECIPE, Recipe::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_RECIPE)
        }
    }

    private fun initUI(recipe: Recipe) {
        Glide.with(requireContext())
            .load("$ASSETS_BASE_PATH${recipe.imageUrl}")
            .into(binding.headerImage)

        binding.titleText.text = recipe.title

        updateFavoriteIcon(isFavorite)
        binding.iconFavorites.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteIcon(isFavorite)
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val iconRes = if (isFavorite){
            R.drawable.ic_heart
    }else {
        R.drawable.ic_heart_empty
    }
    binding.iconFavorites.setImageResource(iconRes)
}

    private fun initRecycler(recipe: Recipe) {
        val portionValue = binding.tvPortionsValue
        val seekbar = binding.seekbar
        portionValue.text = seekbar.progress.toString()

        val ingredientsAdapter = IngredientsAdapter(recipe.ingredients)
        ingredientsAdapter.updateIngredients(seekbar.progress)

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val portion = progress
                portionValue.text = portion.toString()
                ingredientsAdapter.updateIngredients(portion)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val divider = MaterialDividerItemDecoration(requireContext(), RecyclerView.VERTICAL).apply {
            isLastItemDecorated = false
            dividerInsetStart = resources.getDimensionPixelSize(R.dimen.divider_padding)
            dividerInsetEnd = resources.getDimensionPixelSize(R.dimen.divider_padding)
            dividerColor = resources.getColor(R.color.divider_light_gray_color, null)
        }

        binding.rvIngredients.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ingredientsAdapter
            addItemDecoration(divider)
            setHasFixedSize(true)
        }

        binding.rvMethod.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = MethodAdapter(recipe.method)
            addItemDecoration(divider)
            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}