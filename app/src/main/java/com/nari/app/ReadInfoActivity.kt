package com.nari.app

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ReadInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_info)


        // Set up the Material Toolbar
        val toolbar: Toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        val dataUpdater = JsonDatabaseSync(this)
        dataUpdater.updateDataFromJson()



        val itemId = intent.getIntExtra("card_id",1) // Replace with the actual ID you're looking for

        val itemRepository = ItemRepository(this)
        val specificItem = itemRepository.getItemById(itemId)

        if (specificItem != null) {
            Log.d("DatabaseLogger", "Specific Item - ID: ${specificItem.id}, Title: ${specificItem.title}, Description: ${specificItem.description}, Image: ${specificItem.image}, Category: ${specificItem.category}")
            val title = findViewById<TextView>(R.id.headingTextView)
            title.text = specificItem.title
            val description = findViewById<TextView>(R.id.DescriptionTextView)
            description.text = specificItem.description

        } else {
            Log.d("DatabaseLogger", "Item with ID $itemId not found")
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return true
    }
}