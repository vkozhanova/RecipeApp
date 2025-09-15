package com.example.myapplication.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val quantity: String,
    val unitOfMeasure: String,
    val description: String
)