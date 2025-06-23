package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import com.example.myapplication.databinding.ItemRecipeBinding

class RecipesListAdapter(
    private var recipes: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit,
    private val onFavoriteClick: ((Recipe) -> Unit)? = null,
) : RecyclerView.Adapter<RecipesListAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val favoriteIcon = ImageView(binding.root.context).apply {
            val context = binding.root.context
            layoutParams = MarginLayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT
            ).also { params ->
                val marginEnd = context.resources.getDimensionPixelSize(R.dimen.main_space_half_8)
                val topMargin = marginEnd
                params.topMargin = marginEnd
                params.topMargin = topMargin
            }
            setImageResource(R.drawable.ic_heart_empty)
            visibility = View.GONE
        }

        init {
            (binding.root as ViewGroup).addView(favoriteIcon)
        }

        fun bind(recipe: Recipe) {
            binding.recipeTitle.text = recipe.title

            Glide.with(binding.root.context)
                .load("$ASSETS_BASE_PATH${recipe.imageUrl}")
                .into(binding.recipeImage)

            favoriteIcon.setImageResource(
                if (recipe.isFavorite) R.drawable.ic_heart else R.drawable.ic_heart_empty
            )

            favoriteIcon.setOnClickListener {
                onFavoriteClick?.invoke(recipe)
            }

            binding.root.setOnClickListener {
                onItemClick(recipe)
            }

            val bottomMargin = if (adapterPosition == itemCount - 1) {
                binding.root.context.resources.getDimensionPixelSize(R.dimen.main_space_16)
            } else {
                0
            }
            binding.root.updateLayoutParams<MarginLayoutParams> {
                this.bottomMargin = bottomMargin
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecipeViewHolder,
        position: Int
    ) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    fun updateRecipes(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}