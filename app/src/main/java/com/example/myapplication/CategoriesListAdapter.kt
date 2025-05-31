package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ItemCategoryBinding

class CategoriesListAdapter(
    private val categories: List<Category>
) : RecyclerView.Adapter<CategoriesListAdapter.CategoryViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(categoryId: Int)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.cardTitle.text = category.title
            binding.cardText.text = category.description

            Glide.with(binding.root.context)
                .load("$ASSETS_BASE_PATH${category.imageUrl}")
                .into(binding.cardImage)

            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(category.id)
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
}