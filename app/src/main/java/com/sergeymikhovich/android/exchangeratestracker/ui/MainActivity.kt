package com.sergeymikhovich.android.exchangeratestracker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.sergeymikhovich.android.exchangeratestracker.R
import com.sergeymikhovich.android.exchangeratestracker.databinding.ActivityMainBinding
import com.sergeymikhovich.android.exchangeratestracker.ui.favorites.FavoritesFragment
import com.sergeymikhovich.android.exchangeratestracker.ui.popular.PopularFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<PopularFragment>(R.id.fragmentContainer)
            }
        }

        setBottomNavigationItemListener()
    }

    private fun setBottomNavigationItemListener() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.popularFragment -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<PopularFragment>(R.id.fragmentContainer)
                    }
                    true
                }
                R.id.favoriteFragment -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FavoritesFragment>(R.id.fragmentContainer)
                    }
                    true
                }
                else -> false
            }
        }
    }
}