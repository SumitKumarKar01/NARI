package com.nari.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



         val button = findViewById<Button>(R.id.loginButton) // Replace with your actual button ID

        button.setOnClickListener {
            // Create an Intent to navigate to the Pin activity
            val intent = Intent(this@Login, pin::class.java)
            startActivity(intent)
        }
    }

}