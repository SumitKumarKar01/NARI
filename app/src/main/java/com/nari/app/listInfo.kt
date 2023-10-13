package com.nari.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class listInfo : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_info)


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //Intent from previous
        val category = intent.getStringExtra("category")

        // Create an instance of DataUpdater and call updateDataFromJson
        val dataUpdater = JsonDatabaseSync(this)
        dataUpdater.updateDataFromJson()

        // Retrieve and log data from the database
        val itemRepository = ItemRepository(this)
        val itemList = itemRepository.getItemsByCategory(category.toString())


        // Mapping each item to a CardData object
        val data = itemList.map {
            CardData(it.id, it.title, it.description, it.image, it.category)
        }


        val adapter = CardAdapter(data)
        adapter.onCardClickListener = object : CardAdapter.OnCardClickListener {
            override fun onCardClick(cardId: Int) {
                // Handle the card click here, use cardId as needed
                intent = Intent(this@listInfo,ReadInfoActivity::class.java)
                intent.putExtra("card_id",cardId)
                startActivity(intent)
                finish()

            }
        }
        recyclerView.adapter = adapter




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