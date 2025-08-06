package com.example.myapplication.ui.recipes.recipe


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemIngredientBinding
import com.example.myapplication.model.Ingredient

class IngredientsAdapter(initialIngredients: List<Ingredient>) :
    RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {
    var ingredients: List<Ingredient> = initialIngredients
    fun updateIngredients(newIngredients: List<Ingredient>) {
        ingredients = newIngredients
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.binding.tvIngredientName.text = ingredient.description

        val displayQuantity = ingredient.quantity.replace('.', ',')
        holder.binding.tvIngredientAmount.text =
            "${displayQuantity} ${ingredient.unitOfMeasure}"
    }

    inner class IngredientViewHolder(val binding: ItemIngredientBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemIngredientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IngredientViewHolder(binding)
    }

    override fun getItemCount(): Int = ingredients.size
}