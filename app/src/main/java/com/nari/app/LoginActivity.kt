package com.nari.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {

    //Firebase Initialization
    private lateinit var auth: FirebaseAuth
    // Shared Preferences
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("NewInstall", Context.MODE_PRIVATE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        auth = FirebaseAuth.getInstance()

        val button = findViewById<Button>(R.id.loginButton)
        val registerLink = findViewById<TextView>(R.id.registerLink)


        button.setOnClickListener {
            val uid = findViewById<EditText>(R.id.UIDField).text.toString()
            val password = findViewById<EditText>(R.id.PassField).text.toString()

            if (uid.isBlank() || !isValidEmail(uid)) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check password length
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                signIn(uid, password)
            }

            onBackPressedDispatcher.addCallback(this,object :OnBackPressedCallback(enabled = true){
                override fun handleOnBackPressed() {
                    finishAffinity()
                    exitProcess(0)
                }
            })
        }

        registerLink.setOnClickListener {
            // Handle the click event, for example, navigate to the registration activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }


    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        return emailRegex.matches(email)
    }


    private fun signIn(uid: String, password: String) {
        auth.signInWithEmailAndPassword(uid, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI or navigate to the next activity
                    moveToNext()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun moveToNext() {
        val isNew = sharedPreferences.getBoolean("isNew",true)
        if (isNew){
            saveNewStatus(!isNew)
            startActivity(Intent(this,SetupPin::class.java))
            finish()
        }
        else{
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }

    private fun saveNewStatus(isNew: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("isNew", isNew)
            apply()
        }
    }








}