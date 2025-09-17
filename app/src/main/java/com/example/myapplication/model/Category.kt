package com.example.myapplication.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String
)