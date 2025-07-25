package com.example.myapplication.ui.recipes.recipe

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemStepBinding

class MethodAdapter(private var methods: List<String>) :
    RecyclerView.Adapter<MethodAdapter.StepViewHolder>() {

    inner class StepViewHolder(val binding: ItemStepBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun updateData(newMethods: List<String>) {
        methods = newMethods
        notifyDataSetChanged()
        Log.d("MethodAdapter", "Data updated: ${newMethods.size} items")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val binding = ItemStepBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val method = methods[position]
        holder.binding.tvStep.text = "${position + 1}. $method"
    }

    override fun getItemCount(): Int = methods.size
}