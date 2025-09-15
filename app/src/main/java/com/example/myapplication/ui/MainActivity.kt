package com.example.myapplication.ui

import android.os.Bundle
import android.util.Log
import android.util.Log.e
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.R
import com.example.myapplication.data.RecipeApiService
import com.example.myapplication.data.RecipesRepository
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.model.Category
import com.example.myapplication.model.Ingredient
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

data class Recipe(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("ingredients")
    val ingredients: List<Ingredient>,
    @SerializedName("method")
    val method: List<String>,
    @SerializedName("imageUrl")
    val imageUrl: String
)

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    private lateinit var navController: NavController
    private val threadPool = Executors.newFixedThreadPool(10)
    private lateinit var repository: RecipesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()

        repository = RecipesRepository()
        Log.i("!!!", "Метод onCreate() выполняется на потоке: ${Thread.currentThread().name}")

        threadPool.execute {
            try {
                    val categories = repository.getCategories()
                if (categories != null) {
                    Log.i("!!!", "categories: $categories")

                    val categoryIds = categories.map { it.id }
                    Log.i("!!!", "Список категорий: $categoryIds")

                    getRecipesByCategoryIds(categoryIds)

                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("!!!", "Ошибка при загрузке категорий", e)
                runOnUiThread {
                    Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun getRecipesByCategoryIds(categoryIds: List<Int>) {

        if (categoryIds.isEmpty()) {
            Log.i("!!!", "Список ID категорий пуст")
            return
        }

        categoryIds.forEach { categoryId ->
            threadPool.execute {
                try {
                    val recipes = repository.getRecipesByCategoryId(categoryId)

                    recipes?.forEach { recipe ->
                            Log.i(
                                "!!!",
                                "Рецепт ${recipe.title} (ID: ${recipe.id}) из категории ${categoryId}"
                            )
                            Log.i("!!!", "----")
                        }
                } catch (e: Exception) {
                    Log.e(
                        "!!!",
                        "Ошибка при загрузке рецептов для категории $categoryId",
                        e
                    )
                }
            }
        }
    }

    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        binding.btnFavorites.setOnClickListener {
            navController.navigate(R.id.favoritesFragment)
        }

        binding.btnCategories.setOnClickListener {
            navController.navigate(R.id.categoriesListFragment)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        threadPool.shutdown()
        _binding = null
    }
}