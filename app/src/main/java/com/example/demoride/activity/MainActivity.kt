package com.example.demoride.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.demoride.R
import com.example.demoride.fragments.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState== null){
            supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_right_animation,
                R.anim.slide_out_left_animation,
                R.anim.slide_in_left_animation,
                R.anim.slide_out_right_animation
            ).replace(R.id.fragment_container,MainFragment()).commit()
        }
    }
}