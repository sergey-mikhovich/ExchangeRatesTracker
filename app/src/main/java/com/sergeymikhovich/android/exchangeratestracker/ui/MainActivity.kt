package com.sergeymikhovich.android.exchangeratestracker.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.navigation.NavigationBarView
import com.sergeymikhovich.android.exchangeratestracker.R
import com.sergeymikhovich.android.exchangeratestracker.databinding.ActivityMainBinding
import com.sergeymikhovich.android.exchangeratestracker.ui.favorites.FavoritesFragment
import com.sergeymikhovich.android.exchangeratestracker.ui.popular.PopularFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

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

        binding.bottomNavigation.setOnItemSelectedListener(this)

        onBackPressedDispatcher.addCallback(this) {
            when (supportFragmentManager.findFragmentById(R.id.fragmentContainer)) {
                is PopularFragment -> finish()
                is FavoritesFragment -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<PopularFragment>(R.id.fragmentContainer)
                    }
                    binding.bottomNavigation.selectedItemId = R.id.popularFragment
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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