package com.nari.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class listInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_info)



        val commonClickListener = View.OnClickListener {
           startActivity(Intent(this,infoPage::class.java))
        }

        val parentLayout = findViewById<ViewGroup>(R.id.pcards) // Replace with the ID of the layout containing your cards

        for (i in 0 until parentLayout.childCount) {
            val childView = parentLayout.getChildAt(i)
            if (childView is MaterialCardView) {
                childView.setOnClickListener(commonClickListener)
            }
        }



        //Bottom Navigation
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