package com.nari.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import java.text.SimpleDateFormat
import java.util.*

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
            navigation()
        }
        else{
            if (isPinSet()){
                startActivity(Intent(this,pin::class.java))
                finish()
            }
            else{
                setContentView(R.layout.activity_main)
                navigation()
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

        //Calendar
        calendar()
        val predictedDates = predictNextPeriod()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val predictedStartDate = dateFormat.format(predictedDates.first)
        val predictedEndDate = dateFormat.format(predictedDates.second)

        Log.d("PeriodPrediction", "Predicted Next Period Start Date: $predictedStartDate")
        Log.d("PeriodPrediction", "Predicted Next Period End Date: $predictedEndDate")
    }

    private fun calendar() {
        val calendarView: CalendarView = findViewById(R.id.calendarView)

        // Predict the next period start and end date based on the fixed previous end date
        val predictedDates = predictNextPeriod()
        val predictedStartDate = Calendar.getInstance()
        predictedStartDate.time = predictedDates.first

        val predictedEndDate = Calendar.getInstance()
        predictedEndDate.time = predictedDates.second

        // Create a list of Calendar instances to represent the range
        val datesInRange = ArrayList<Calendar>()
        val currentDate = predictedStartDate.clone() as Calendar

        while (currentDate.before(predictedEndDate) || currentDate == predictedEndDate) {
            datesInRange.add(currentDate.clone() as Calendar)
            currentDate.add(Calendar.DATE, 1)
        }

        // Create a list of EventDay objects to represent the range
        val events = datesInRange.map { EventDay(it, R.drawable.ic_selected_day) }

        // Set the selected dates in the CalendarView
        calendarView.setEvents(events)
    }


    private fun navigation(){

        //Top Navigation
        val topNavigationView: MaterialToolbar = findViewById(R.id.topAppBar)

        topNavigationView.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    val intent = Intent(this,SettingsActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.action_logout -> {
                    auth.signOut()
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    finish() // Close the current activity
                    true
                }
                R.id.action_edit -> {
                    val intent = Intent(this,DatePickerActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }



        //Bottom Navigation
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.info)

        bottomNavigationView.selectedItemId  = R.id.page_1
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


    private fun predictNextPeriod(): Pair<Date, Date> {

        val periodDate: SharedPreferences = getSharedPreferences("PeriodDate", Context.MODE_PRIVATE)
        val previousEndDateString = periodDate.getString("selectedDate", null)


        val calendar = Calendar.getInstance()

        // Set the fixed previous period end date
        // Set the fixed previous period end date or use the retrieved date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val previousEndDate = if (previousEndDateString != null) {
            dateFormat.parse(previousEndDateString)
        } else {
            // If not available in SharedPreferences, use a default date or handle accordingly
            dateFormat.parse("2023-11-01")
        }

        Log.d("PeriodPrediction", "Previous Period End Date: $previousEndDate")

        calendar.time = previousEndDate

        // Assuming a menstrual cycle length of 28 days, you can adjust this value accordingly
        val cycleLength = 28

        // Predict next period start date
        calendar.add(Calendar.DAY_OF_MONTH, cycleLength)
        val nextStartDate = calendar.time

        // Predict next period end date
        calendar.add(Calendar.DAY_OF_MONTH, 4) // Assuming a period lasts for 5 days, you can adjust this value
        val nextEndDate = calendar.time

        return Pair(nextStartDate, nextEndDate)
    }


}