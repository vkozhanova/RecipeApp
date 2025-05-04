package com.example.myapplication
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.fragments.CategoriesListFragment
import com.example.myapplication.fragments.FavoritesFragment

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            showFragment(CategoriesListFragment())
        }
        setupNavigation()
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.mainContainer, fragment)
            setReorderingAllowed(true)
            addToBackStack(null)
        }

    }

    private fun setupNavigation() {
        binding.btnCategories.setOnClickListener {
           showFragment(CategoriesListFragment())

        }
        binding.btnFavorites.setOnClickListener {
            showFragment(FavoritesFragment())

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}