package com.example.myapplication.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.R
import com.example.myapplication.model.Ingredient
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import okhttp3.logging.HttpLoggingInterceptor

data class Category(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("imageUrl")
    val imageUrl: String
)

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
    private val gson = Gson()
    private val threadPool = Executors.newFixedThreadPool(10)
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()

        Log.i("!!!", "Метод onCreate() выполняется на потоке: ${Thread.currentThread().name}")

        threadPool.execute {
            try {
                val request: Request = Request.Builder()
                    .url("https://recipes.androidsprint.ru/api/category")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw Exception("Ошибка запроса $response")
                    }

                    val responseBody = response.body?.string()
                    Log.i("!!!", "Body: $responseBody")

                    if (responseBody != null) {
                        val categories =
                            gson.fromJson(responseBody, Array<Category>::class.java).toList()

                        val categoryIds = categories.map { it.id }
                        Log.i("!!!", "Список категорий: $categoryIds")

                        getRecipesByIds(categoryIds)
                    } else {
                        Log.e("!!!", "Response body is null")
                    }
                }
                Log.i("!!!", "Выполняю запрос на потоке: ${Thread.currentThread().name}")
            } catch (e: Exception) {
                Log.e("!!!", "Ошибка при загрузке категорий", e)
            }
        }
    }

    private fun getRecipesByIds(categoryIds: List<Int>) {

        if (categoryIds.isEmpty()) {
            Log.i("!!!", "Список ID категорий пуст")
            return
        }

        val latch = CountDownLatch(categoryIds.size)

        categoryIds.forEach { categoryId ->
            threadPool.execute {
                try {
                    val request = Request.Builder()
                        .url("https://recipes.androidsprint.ru/api/category/$categoryId/recipes")
                        .build()

                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw Exception("Ошибка запроса $response")
                        }

                        val jsonString = response.body?.string()

                        val recipes = gson.fromJson(jsonString, Array<Recipe>::class.java).toList()

                        recipes.forEach { recipe ->
                            Log.i(
                                "!!!",
                                "Рецепт ${recipe.title} (ID: ${recipe.id}) из категории ${categoryId}"
                            )
                            Log.i("!!!", "----")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(
                        "!!!",
                        "Ошибка при загрузке рецептов для категории $categoryId",
                        e
                    )
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await()
        Log.i("!!!", "Все запросы рецептов завершены")
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