package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.updateLayoutParams

import com.example.myapplication.databinding.ItemRecipeBinding

class RecipesListAdapter(private val recipes: List<Recipe>) :
    RecyclerView.Adapter<RecipesListAdapter.RecipeViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(recipeId: Int)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    inner class RecipeViewHolder(val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root)

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
        val recipe = recipes[position]
        holder.binding.recipeTitle.text = recipe.title
        Glide.with(holder.itemView.context)
            .load("$ASSETS_BASE_PATH${recipe.imageUrl}")
            .into(holder.binding.recipeImage)

        holder.binding.root.setOnClickListener {
            itemClickListener?.onItemClick(recipe.id)
        }

        val resources = holder.itemView.context.resources
        val bottomMargin = if (position == itemCount - 1) {
            resources.getDimensionPixelSize(R.dimen.main_space_16)
        } else {
            0
        }

        holder.itemView.updateLayoutParams<MarginLayoutParams> {
            this.bottomMargin = bottomMargin
        }
    }

    override fun getItemCount(): Int = recipes.size
}
