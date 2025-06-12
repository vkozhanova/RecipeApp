package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemIngredientBinding

class IngredientsAdapter(private val ingredients: List<Ingredient>) :
    RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    private var multiplier: Int = 1

    fun updateIngredients(progress: Int) {
        multiplier = progress
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.binding.tvIngredientName.text = ingredient.description

        val quantity = ingredient.quantity.replace(",", ".").toDoubleOrNull() ?: 0.0
        val totalQuantity = quantity * multiplier

        val formattedQuantity = if (totalQuantity % 1 == 0.0) {
            "${totalQuantity.toInt()}"
        } else {
            "%.1f".format(totalQuantity).replace(',', '.')
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