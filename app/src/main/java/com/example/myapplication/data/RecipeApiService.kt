package com.example.myapplication.data

import com.example.myapplication.model.Category
import com.example.myapplication.model.Recipe
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface RecipeApiService {

    @GET("category")
    fun getCategories(): Call<List<Category>>

    @GET("category/{categoryId}/recipes")
    fun getRecipesByCategoryId(@Path("categoryId") categoryId: Int): Call<List<Recipe>>

    @GET("recipe/{id}")
    fun getRecipeById(@Path("id")id: Int): Call<Recipe>

    @GET("recipes")
    fun getRecipesByIds(@Query("ids") ids: String): Call<List<Recipe>>
}