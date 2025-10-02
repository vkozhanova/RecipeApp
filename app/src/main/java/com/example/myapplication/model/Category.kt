package com.example.myapplication.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "category")
@Serializable
data class Category(
    @PrimaryKey val id: Int,
    @ColumnInfo (name = "title") val title: String,
    @ColumnInfo (name = "description") val description: String,
    @ColumnInfo (name = "image_url") val imageUrl: String
)