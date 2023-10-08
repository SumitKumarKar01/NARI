package com.nari.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class pin : AppCompatActivity() {
    // Shared Preferences
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("Pin", Context.MODE_PRIVATE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)



        val editText = findViewById<EditText>(R.id.pinField)

        editText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(
                v: TextView?,
                actionId: Int,
                event: KeyEvent?
            ): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_GO) {
                    // Handle "Done" or "Go" button press here
                    val enteredPin = editText.text.toString()
                    checkPin(enteredPin)
                    return true
                }
                return false
            }
        })


    }

    private fun openAnotherPage() {

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("passed",true)
        startActivity(intent)
        finish()
    }
    private fun checkPin(enteredPin: String){
        val storedPin = sharedPreferences.getString("pin","")

        if (storedPin == enteredPin){
            openAnotherPage()
        }
        else{
            Toast.makeText(this,"Incorrect Pin",Toast.LENGTH_SHORT).show()
        }

    }
}