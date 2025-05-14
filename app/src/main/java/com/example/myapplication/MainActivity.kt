package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.fragments.CategoriesListFragment
import com.example.myapplication.fragments.FavoritesFragment
import androidx.fragment.app.replace

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            initFirstFragment()
        }
        initNavigation()
    }

    private fun initFirstFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<CategoriesListFragment>(R.id.mainContainer)
        }
    }

    private fun initNavigation() {
        binding.btnCategories.setOnClickListener {
            showFragment<CategoriesListFragment>()
        }
        binding.btnFavorites.setOnClickListener {
            showFragment<FavoritesFragment>()
        }
    }

    private inline fun <reified T : Fragment> showFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<T>(R.id.mainContainer)
            addToBackStack(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}