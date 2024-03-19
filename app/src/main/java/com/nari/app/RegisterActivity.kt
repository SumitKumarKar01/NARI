package com.nari.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    //Firebase Initialization
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val button = findViewById<Button>(R.id.loginButton)

        button.setOnClickListener {
            val uid = findViewById<EditText>(R.id.UIDField).text.toString()
            val password = findViewById<EditText>(R.id.PassField).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.ConfirmPassField).text.toString()

            if (uid.isBlank() || !isValidEmail(uid)) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check password length
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show()
            }

            else {
                register(uid, password)
            }
        }


    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        return emailRegex.matches(email)
    }

    private fun register(uid: String, password: String) {
        auth.createUserWithEmailAndPassword(uid, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration success
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,PreviousMenstruation::class.java))
                    finish()
                } else {
                    // If registration fails, display a message to the user.
                    Toast.makeText(this, "Registration failed, Try again later", Toast.LENGTH_SHORT).show()
                }
            }
    }

}