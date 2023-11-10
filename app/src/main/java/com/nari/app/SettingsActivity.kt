package com.nari.app

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.widget.Toolbar



class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()

//        val themeTextView: TextView = findViewById(R.id.themeSettings)
        val themeIcon: ImageView = findViewById(R.id.iconSetting1)

//        val logoutTextView : TextView = findViewById(R.id.logoutSettings)
        val logoutIcon : ImageView = findViewById(R.id.iconSetting2)

        // Set up a click listener for the "Theme" item
//        themeTextView.setOnClickListener { v ->
//            showPopupMenu(v)
//        }

        themeIcon.setOnClickListener { v ->
            showPopupMenu(v)
        }

        // Set up a click listener for the "Settings" item
//        logoutTextView.setOnClickListener { v ->
//            auth.signOut()
//            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
//            finish() // Close the current activity
//            true
//        }

        logoutIcon.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Login::class.java))
            finish()  // Close the current activity
        }


        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { // Navigate back to the previous page
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.theme_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.lightTheme -> {
                    applyTheme(AppCompatDelegate.MODE_NIGHT_NO)
                    true
                }
                R.id.darkTheme -> {
                    applyTheme(AppCompatDelegate.MODE_NIGHT_YES)
                    true
                }
                R.id.systemDefaultTheme -> {
                    applyTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun applyTheme(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        recreate()

    }




}
