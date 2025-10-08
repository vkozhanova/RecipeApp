package com.example.myapplication.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "recipe")
@Serializable
data class Recipe(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "category_id") val categoryId: Int = 0,
    @ColumnInfo(name = "is_favorite") var isFavorite: Boolean = false,
) {
    @Ignore
    val ingredients: List<Ingredient> = emptyList()

    @Ignore
    val method: List<String> = emptyList()
}