package com.nari.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class info : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val card1 = findViewById<MaterialCardView>(R.id.card1)
        card1.setOnClickListener { startActivity(Intent(this, listInfo::class.java)) }
        val card2 = findViewById<MaterialCardView>(R.id.card2)
        card2.setOnClickListener { startActivity(Intent(this, listInfo::class.java)) }
        val card3 = findViewById<MaterialCardView>(R.id.card3)
        card3.setOnClickListener { startActivity(Intent(this, listInfo::class.java)) }
        val card4 = findViewById<MaterialCardView>(R.id.card4)
        card4.setOnClickListener { startActivity(Intent(this, listInfo::class.java)) }
        val card5 = findViewById<MaterialCardView>(R.id.card5)
        card5.setOnClickListener { startActivity(Intent(this, listInfo::class.java)) }

        //Bottom Navigation
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.info)

        bottomNavigationView.selectedItemId  = R.id.page_3
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.page_1  -> {
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
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