package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemStepBinding

class MethodAdapter(private val steps: List<String>) :
    RecyclerView.Adapter<MethodAdapter.StepViewHolder>() {

    inner class StepViewHolder(val binding: ItemStepBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val binding = ItemStepBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = steps[position]
        holder.binding.tvStepNumber.text = "${position + 1}."
        holder.binding.tvStepDescription.text = step
    }

    override fun getItemCount(): Int = steps.size
}