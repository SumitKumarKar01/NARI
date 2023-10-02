package com.nari.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class info : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.info)

        bottomNavigationView.selectedItemId  = R.id.page_3
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.page_3  -> {
                    true
                }
                R.id.page_2 ->{
                    true
                }
                R.id.page_3 ->{
                    true
                }
                else -> false
            }
        }
    }
}