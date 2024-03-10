package com.nari.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        // Set this adapter to your RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCards)
        recyclerView.layoutManager = LinearLayoutManager(this)

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

        val searchView = findViewById<SearchView>(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // User submitted the search. Perform your search action here (if necessary)
                Log.d("SearchView", "Search text is: $query")

                if (query.isNullOrEmpty()) {
                    // Show all cards
                    card1.visibility = View.VISIBLE
                    card2.visibility = View.VISIBLE
                    card3.visibility = View.VISIBLE
                    card4.visibility = View.VISIBLE
                    card5.visibility = View.VISIBLE
                } else {
                    // Hide all cards
                    card1.visibility = View.GONE
                    card2.visibility = View.GONE
                    card3.visibility = View.GONE
                    card4.visibility = View.GONE
                    card5.visibility = View.GONE

                    // Get all items from the repository
                    val itemRepository = ItemRepository(this@info)
                    val items = itemRepository.getAllItems()

                    // Search for the query in the descriptions
                    val queryWords = query.split(" ")
                    val matchedItems = items.filter { item ->
                        queryWords.any { word ->
                            item.description.contains(word, ignoreCase = true)
                        }
                    }

                    // Log the IDs of the matched items
                    matchedItems.forEach { item ->
                        Log.d("SearchView", "Matched item ID: ${item.id}")
                    }

                    // Create a new instance of CardAdapter with the matchedItems as the data
                    val cardDataList = matchedItems.map { item ->
                        CardData(item.id, item.title, item.description, item.image, item.category)
                    }
                    val cardAdapter = CardAdapter(cardDataList)

                    // Log the size of the CardAdapter
                    Log.d("SearchView", "CardAdapter size: ${cardAdapter.itemCount}")


                    recyclerView.adapter = cardAdapter

                    cardAdapter.onCardClickListener = object : CardAdapter.OnCardClickListener {
                        override fun onCardClick(cardId: Int) {
                            // Handle the card click here, use cardId as needed
                            intent = Intent(this@info,ReadInfoActivity::class.java)
                            intent.putExtra("card_id",cardId)
                            startActivity(intent)


                        }
                    }
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Text has changed, log it
                if(newText.isNullOrEmpty()){
                    // Show all cards
                    card1.visibility = View.VISIBLE
                    card2.visibility = View.VISIBLE
                    card3.visibility = View.VISIBLE
                    card4.visibility = View.VISIBLE
                    card5.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }

                return false
            }
        })

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
                    startActivity(Intent(this,FeedActivity::class.java))
                    finish()
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