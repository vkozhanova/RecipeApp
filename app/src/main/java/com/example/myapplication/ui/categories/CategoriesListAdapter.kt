package com.example.myapplication.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.data.BASE_IMAGE_URL
import com.example.myapplication.model.Category
import com.example.myapplication.databinding.ItemCategoryBinding
import com.example.myapplication.R

class CategoriesListAdapter(
    private var categories: List<Category>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<CategoriesListAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.cardTitle.text = category.title
            binding.cardText.text = category.description

            Glide.with(binding.root.context)
                .load("${BASE_IMAGE_URL}${category.imageUrl}")
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_error)
                .into(binding.cardImage)

            binding.root.setOnClickListener {
                onItemClick(category.id)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    fun updateCategories(newCategories: List<Category>) {
        println("Updating categories: ${newCategories.size} items")
        newCategories.forEach { println("Category: ${it.title} (ID: ${it.id})") }
        categories = newCategories
        notifyDataSetChanged()
    }
}