package com.example.myapplication.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.PREFS_NAME
import com.example.myapplication.data.RecipeApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.example.myapplication.data.RecipesRepository

class AppContainer(context: Context) {

    private val db: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "databaseRecipe"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val categoriesDao = db.categoriesDao()
    private val recipeDao = db.recipesDao()

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()
    private val contentType = "application/json".toMediaType()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://recipes.androidsprint.ru/api/")
        .client(client)
        .addConverterFactory(Json.asConverterFactory(contentType))
        .build()

    private val recipeApiService: RecipeApiService = retrofit.create(RecipeApiService::class.java)

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val repository = RecipesRepository(
        recipeDao = recipeDao,
        categoriesDao = categoriesDao,
        recipeApiService = recipeApiService,
        ioDispatcher = ioDispatcher,
        sharedPrefs = sharedPreferences,
    )

    val categoriesListViewModelFactory = CategoriesListViewModelFactory(repository)
    val recipesListViewModelFactory = RecipeListViewModelFactory(repository)
    val recipeViewModelFactory = RecipeViewModelFactory(repository)
    val favoritesViewModelFactory = FavoritesViewModelFactory(repository)
}