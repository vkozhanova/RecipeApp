package com.example.myapplication.ui.recipes.recipe

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemIngredientBinding
import com.example.myapplication.model.Ingredient
import java.math.BigDecimal
import java.math.RoundingMode

class IngredientsAdapter(initialIngredients: List<Ingredient>) :
    RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

        private var ingredients: List<Ingredient> = initialIngredients
        private var multiplier: Int = 1

    fun updateState(newIngredients: List<Ingredient>, newMultiplier: Int) {
        if (ingredients != newIngredients || multiplier != newMultiplier) {
            ingredients = newIngredients
            multiplier = newMultiplier
            notifyDataSetChanged()
            Log.d("Ingredient Adapter", "Updated: ${newIngredients.size} items *$newMultiplier")
        }
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.binding.tvIngredientName.text = ingredient.description

        val formattedQuantity = try {
            BigDecimal(ingredient.quantity.replace(",", "."))
                .multiply(BigDecimal(multiplier))
                .setScale(1, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString()
        } catch (_: NumberFormatException) {
            ingredient.quantity
        }
        holder.binding.tvIngredientAmount.text =
            "$formattedQuantity ${ingredient.unitOfMeasure}"
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