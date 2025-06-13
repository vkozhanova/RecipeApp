package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemIngredientBinding
import java.math.BigDecimal
import java.math.RoundingMode

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

        val quantity = try {
            BigDecimal(ingredient.quantity.replace(",", "."))
        } catch (_: NumberFormatException) {
            BigDecimal.ZERO
        }
        val totalQuantity = quantity.multiply(BigDecimal(multiplier))

        val formattedQuantity = if (totalQuantity.scale() <= 0 || totalQuantity.setScale(1,
                RoundingMode.HALF_UP).stripTrailingZeros().scale() <= 0) {
            totalQuantity.toInt().toString()
        } else {
            totalQuantity.setScale(1, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
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