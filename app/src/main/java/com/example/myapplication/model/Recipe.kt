package com.example.myapplication.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Recipe(
    val id: Int,
    val title: String,
    val ingredients: List<Ingredient>,
    val method: List<String>,
    val imageUrl: String,
    var isFavorite: Boolean = false
) : Parcelable