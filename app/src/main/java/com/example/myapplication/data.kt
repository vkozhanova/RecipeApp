package com.example.myapplication

data class Category(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String
)

data class Recipe(
    val id: Int,
    val title: String,
    val ingredients: List<Ingredient>,
    val method: String,
    val imageUrl: String
)

data class Ingredient(
    val quantity: String,
    val unitOfMeasure: String,
    val description: String
)