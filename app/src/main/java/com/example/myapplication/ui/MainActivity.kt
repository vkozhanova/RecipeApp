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
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

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
    val imageUrl: String,

    )

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    private lateinit var navController: NavController
    private val gson = Gson()
    val threadPool = Executors.newFixedThreadPool(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()

        Log.i("!!!", "Метод onCreate() выполняется на потоке: ${Thread.currentThread().name}")

        Thread {
            try {
                val url = URL("https://recipes.androidsprint.ru/api/category")
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val jsonString = connection.inputStream.bufferedReader().readText()
                Log.i("!!!", "Body: $jsonString")

                val categories = gson.fromJson(jsonString, Array<Category>::class.java).toList()

                val categoryIds = categories.map { it.id }
                Log.i("!!!", "Список категорий: $categoryIds")

                getRecipesByIds(categoryIds)

                Log.i("!!!", "Выполняю запрос на потоке: ${Thread.currentThread().name}")
            } catch (e: Exception) {
                Log.i("!!!", "Ошибка при загрузке категорий", e)
            }
        }.start()
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

                    val url = URL("https://recipes.androidsprint.ru/api/category/$categoryId/recipes")

                    val connection = url.openConnection() as HttpURLConnection
                    connection.connect()

                    val jsonString = connection.inputStream.bufferedReader().readText()

                    val recipes =
                        gson.fromJson(jsonString, Array<Recipe>::class.java).toList()

                    recipes.forEach { recipe ->
                        Log.i("!!!", "Рецепт ${recipe.title} (ID: ${recipe.id}) из категории ${categoryId}")
                        Log.i("!!!", "----")
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