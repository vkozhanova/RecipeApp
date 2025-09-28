package com.example.myapplication.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.R
import com.example.myapplication.data.RecipesRepository
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()

        Log.i("!!!", "Метод onCreate() выполняется на потоке: ${Thread.currentThread().name}")

        lifecycleScope.launch {
            try {
                val categories = RecipesRepository.getCategories()
                if (categories != null) {
                    Log.i("!!!", "categories: $categories")

                    val categoryIds = categories.map { it.id }
                    Log.i("!!!", "Список категорий: $categoryIds")

                    getRecipesByCategoryIds(categoryIds)

                } else {
                        Toast.makeText(this@MainActivity, "Ошибка получения данных", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("!!!", "Ошибка при загрузке категорий", e)
                    Toast.makeText(this@MainActivity, "Ошибка получения данных", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getRecipesByCategoryIds(categoryIds: List<Int>) {

        if (categoryIds.isEmpty()) {
            Log.i("!!!", "Список ID категорий пуст")
            return
        }

        categoryIds.forEach { categoryId ->
            lifecycleScope.launch {
                try {
                    val recipes = RecipesRepository.getRecipesByCategoryId(categoryId)

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
        _binding = null
    }
}