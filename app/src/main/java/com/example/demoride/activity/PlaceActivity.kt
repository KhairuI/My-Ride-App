package com.example.demoride.activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.demoride.R
import com.example.demoride.databinding.ActivityPlaceBinding

class PlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController= Navigation.findNavController(this,R.id.navHostFragment)
        NavigationUI.setupWithNavController(binding.bottomNavigation,navController)
    }
}