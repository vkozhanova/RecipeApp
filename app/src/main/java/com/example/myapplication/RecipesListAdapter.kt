package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.updateLayoutParams
import com.example.myapplication.databinding.ItemRecipeBinding

class RecipesListAdapter(
    private var recipes: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit,

) : RecyclerView.Adapter<RecipesListAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            binding.recipeTitle.text = recipe.title

            Glide.with(binding.root.context)
                .load("$ASSETS_BASE_PATH${recipe.imageUrl}")
                .into(binding.recipeImage)

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