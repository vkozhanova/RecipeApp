package com.example.myapplication.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.CategoriesDao
import com.example.myapplication.data.PREFS_NAME
import com.example.myapplication.data.RecipeApiService
import com.example.myapplication.data.RecipeDao
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.example.myapplication.data.RecipesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IODispatcher

@Module
@InstallIn(SingletonComponent::class)
class RecipeModule {

    @Provides
    @IODispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun providesDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "databaseRecipe"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCategoriesDao(appDatabase: AppDatabase) = appDatabase.categoriesDao()

    @Provides
    fun provideRecipesDao(appDatabase: AppDatabase) = appDatabase.recipesDao()

    @Provides
    fun providesHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()

    @Provides
    fun providesRetrofit(client: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://recipes.androidsprint.ru/api/")
            .client(client)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    fun providesRecipeApiService(retrofit: Retrofit): RecipeApiService =
        retrofit.create(RecipeApiService::class.java)

    @Provides
    fun providesSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @Provides
    fun provideRepository(
        recipeDao: RecipeDao,
        categoriesDao: CategoriesDao,
        recipeApiService: RecipeApiService,
        @IODispatcher ioDispatcher: CoroutineDispatcher,
        sharedPrefs: SharedPreferences
    ): RecipesRepository = RecipesRepository(
        recipeDao = recipeDao,
        categoriesDao = categoriesDao,
        recipeApiService = recipeApiService,
        ioDispatcher = ioDispatcher,
        sharedPrefs = sharedPrefs,
    )
}