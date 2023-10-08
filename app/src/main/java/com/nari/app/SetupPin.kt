package com.nari.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText

class SetupPin : AppCompatActivity() {

    // Shared Preferences
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("Pin", Context.MODE_PRIVATE)
    }

    // UI elements
    private lateinit var pinField: TextInputEditText
    private lateinit var confirmPinField: TextInputEditText
    private lateinit var confirmButton: Button
    private lateinit var skipText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_pin)

        pinField = findViewById(R.id.pinField)
        confirmPinField = findViewById(R.id.confirmPinField)
        confirmButton = findViewById(R.id.confirmButton)
        skipText = findViewById(R.id.skipText)

        confirmButton.setOnClickListener {
            handleConfirmButtonClick()
        }

        skipText.setOnClickListener {
            handleSkipTextClick()
        }
    }
    private fun handleConfirmButtonClick() {
        val pin = pinField.text.toString()
        val confirmPin = confirmPinField.text.toString()

        if (pin == confirmPin) {
            // Save the PIN and set the boolean key
            savePinSetStatus(true)
            savePinToSharedPreferences(pin)
            moveToNextPage()
        } else {
            // Handle PIN mismatch
            // You may want to show an error message to the user
        }
    }

    private fun handleSkipTextClick() {
        // Set the boolean key to false
        savePinSetStatus(false)
        moveToNextPage()
    }
    private fun savePinToSharedPreferences(pin: String) {
        with(sharedPreferences.edit()) {
            putString("pin", pin)
            apply()
        }
    }

    private fun savePinSetStatus(isPinSet: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("isPinSet", isPinSet)
            apply()
        }
    }

    private fun moveToNextPage() {
        startActivity(Intent(this,pin::class.java))
        finish()
    }


}