package com.example.myapplication.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Category(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String
) : Parcelable