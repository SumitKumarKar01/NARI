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
        val card2 = findViewById<MaterialCardView>(R.id.card2)
        val card3 = findViewById<MaterialCardView>(R.id.card3)
        val card4 = findViewById<MaterialCardView>(R.id.card4)
        val card5 = findViewById<MaterialCardView>(R.id.card5)

        card1.setOnClickListener {
            val intent = Intent(this, listInfo::class.java)
            intent.putExtra("category", "Period")
            startActivity(intent)
        }

        card2.setOnClickListener {
            val intent = Intent(this, listInfo::class.java)
            intent.putExtra("category", "Pregnancy")
            startActivity(intent)
        }

        card3.setOnClickListener {
            val intent = Intent(this, listInfo::class.java)
            intent.putExtra("category", "Sex Health")
            startActivity(intent)
        }

        card4.setOnClickListener {
            val intent = Intent(this, listInfo::class.java)
            intent.putExtra("category", "Contraceptive")
            startActivity(intent)
        }

        card5.setOnClickListener {
            val intent = Intent(this, listInfo::class.java)
            intent.putExtra("category", "Period Pain")
            startActivity(intent)
        }

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
                    startActivity(Intent(this,info::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}