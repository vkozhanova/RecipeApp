package com.example.myapplication

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemCategoryBinding

class CategoriesListAdapter(
    private val categories: List<Category>
) : RecyclerView.Adapter<CategoriesListAdapter.CategoryViewHolder>() {

    interface OnItemClickListener{
        fun  onItemClick(position: Int)
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
            val drawable = try {
                Drawable.createFromStream(
                    binding.cardImage.context.assets.open(category.imageUrl),
                    null
                )
            } catch (e: Exception) {
                Log.e("CategoriesAdapter", "Error loading image: ${e.message}", e)
                null
            }
            binding.cardImage.setImageDrawable(drawable)
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
        holder.itemView.setOnClickListener { itemClickListener?.onItemClick(position) }
    }

    override fun getItemCount(): Int = categories.size
}