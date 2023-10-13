package com.nari.app

import JsonDatabaseSync
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // Shared Preferences
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("Pin", Context.MODE_PRIVATE)
    }

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user == null) {
            // User is signed out, navigate to login screen
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() // Close the current activity
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)


        auth = FirebaseAuth.getInstance()
        val pinStatus = intent.getBooleanExtra("passed",false)

        if(pinStatus){
            setContentView(R.layout.activity_main)
        }
        else{
            if (isPinSet()){
                startActivity(Intent(this,pin::class.java))
                finish()
            }
            else{
                setContentView(R.layout.activity_main)
            }
        }






        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener{
            auth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this,Login::class.java)
//            startActivity(intent)

            finish() // Close the current activity
        }

        //Bottom Navigation
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.info)

        bottomNavigationView.selectedItemId  = R.id.page_1
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.page_1  -> {
                    true
                }
                R.id.page_2 ->{
                    startActivity(Intent(this,ReadInfoActivity::class.java))
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

        // Create an instance of DataUpdater and call updateDataFromJson
        val dataUpdater = JsonDatabaseSync(this)
        dataUpdater.updateDataFromJson()

        // Retrieve and log data from the database
        val itemRepository = ItemRepository(this)
        val itemList = itemRepository.getAllItems()

        // Log the items to the console
        for (item in itemList) {
            Log.d("DatabaseLog", "ID: ${item.id}, Title: ${item.title}, Description: ${item.description}, Image: ${item.image}, Category: ${item.category}")
        }
    }



    override fun onStart() {
        super.onStart()
        // Add the AuthStateListener in onStart
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        // Remove the AuthStateListener in onStop
        auth.removeAuthStateListener(authStateListener)
    }

    private fun isPinSet(): Boolean {
        // Check if PIN is already set
        return sharedPreferences.getBoolean("isPinSet", false)
    }


}