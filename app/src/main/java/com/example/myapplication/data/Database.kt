package com.example.myapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.model.Category
import com.example.myapplication.model.Recipe

@Database(
    entities = [Category::class, Recipe::class],
    version = 2, exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao

    abstract fun recipesDao(): RecipeDao
}