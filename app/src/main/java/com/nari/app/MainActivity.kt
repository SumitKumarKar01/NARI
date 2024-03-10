package com.nari.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.applandeo.materialcalendarview.CalendarDay
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.applandeo.materialcalendarview.CalendarView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val dateRangeDao: DateRangeDao by lazy {
        val db = Room.databaseBuilder(
            applicationContext,
            DateRangeDatabase::class.java, "date_ranges"
        ).build()

        db.dateRangeDao()
    }




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
                calendarButtonNavigation()
                navigation()
            }
        }





        //Calendar
        calendar()

    }

    private fun calendar() {
        val calendarView: CalendarView = findViewById(R.id.calendarView)
        val calendarDayText: TextView = findViewById(R.id.calendarDayText)

        // Predict the next period start and end date based on the fixed previous end date
        applicationScope.launch {
            // Predict the next period start and end date based on the fixed previous end date
            val predictedDates = predictNextPeriod()

            val predictedStartDate = Calendar.getInstance()
            predictedStartDate.time = predictedDates.first

            val predictedEndDate = Calendar.getInstance()
            predictedEndDate.time = predictedDates.second

            val datesInRange = ArrayList<Calendar>()
            val currentDate = predictedStartDate.clone() as Calendar

            for (i in 0 until 12) {
                while (currentDate.before(predictedEndDate) || currentDate == predictedEndDate) {
                    datesInRange.add(currentDate.clone() as Calendar)
                    currentDate.add(Calendar.DATE, 1)
                }

                // Add the cycle length to the start and end dates for the next month
                predictedStartDate.add(Calendar.DATE, calculateModeCycleLength())
                predictedEndDate.add(Calendar.DATE, calculateModeCycleLength())
                currentDate.time = predictedStartDate.time
            }

            withContext(Dispatchers.Main) {
                val calendarDayList = datesInRange.map{
                    CalendarDay(it).apply{
                        imageDrawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_selected_day)
                    }
                }
                calendarView.setCalendarDays(calendarDayList)
                val today = Calendar.getInstance()
                if (datesInRange.any { it.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) && it.get(Calendar.YEAR) == today.get(Calendar.YEAR) }) {
                    // If it is, change the text of calendarDayText
                    calendarDayText.text = "You might have period today!"
                } else {
                    // If it's not, set the text to a default value
                    calendarDayText.text = "No Alert Today"
                }

            }
        }


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

    private fun calendarButtonNavigation(){
        val calendarLog: RelativeLayout = findViewById(R.id.calendarLog)
        val calendarPreviousMenstruatuion: RelativeLayout = findViewById(R.id.calendarPreviousMenstruation)
        val calendarSwitchMode: RelativeLayout = findViewById(R.id.calendarSwitchMode)
        val calendarHospitals: RelativeLayout = findViewById(R.id.calendarHospitals)

        calendarLog.setOnClickListener {
            val intent = Intent(this,CalendarLog::class.java)
            startActivity(intent)
            finish()
        }

        calendarPreviousMenstruatuion.setOnClickListener {
            val intent = Intent(this,PreviousMenstruation::class.java)
            startActivity(intent)
            finish()
        }
        calendarSwitchMode.setOnClickListener {
            switchCalendarModes()
        }
        calendarHospitals.setOnClickListener {
            val intent = Intent(this,Hospitals::class.java)
            startActivity(intent)
            finish()
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


    private suspend fun predictNextPeriod(): Pair<Date, Date> = withContext(Dispatchers.IO) {

        val cycleLength = calculateModeCycleLength()
        val periodLength = calculateModePeriodLength()

        // Get the last end date from the database
        val lastStartDate = dateRangeDao.getLastStartDate()


        // Convert the last end date to a Calendar object
        val calendar = Calendar.getInstance()
        lastStartDate?.let {
            calendar.timeInMillis = it
        }

        // Add the cycle length to the calendar
        calendar.add(Calendar.DATE, cycleLength)

        // Convert the calendar back to a Date object
        val nextStartDate = calendar.time

        // Calculate nextEndDate the same way if needed
        calendar.add(Calendar.DATE, periodLength)
        val nextEndDate = calendar.time
        // Log the predicted dates

        Log.d("MainActivity", "Predicted start date: $nextStartDate")
        Log.d("MainActivity", "Predicted end date: $nextEndDate")

        return@withContext Pair(nextStartDate, nextEndDate)
    }
    private fun switchCalendarModes(){

    }

    private fun calculateModeCycleLength(): Int {
        // Get all date ranges from the database
        val dateRanges = dateRangeDao.getAll()

        // Calculate the differences between consecutive start dates
        val differences = mutableListOf<Int>()
        for (i in 1 until dateRanges.size) {
            val diff = dateRanges[i].startDate - dateRanges[i - 1].startDate
            differences.add((diff / (1000 * 60 * 60 * 24)).toInt()) // Convert to days
        }

        // Calculate the mode of the differences
        val frequencyMap = differences.groupingBy { it }.eachCount()
        val mode = frequencyMap.maxByOrNull { it.value }?.key

        return mode ?: 28 // Return the mode or a default value of 28 if no mode can be calculated
    }
    private fun calculateModePeriodLength(): Int {
        // Get all date ranges from the database
        val dateRanges = dateRangeDao.getAll()

        // Calculate the differences between start and end dates
        val differences = dateRanges.map { dateRange ->
            val diff = dateRange.endDate - dateRange.startDate
            (diff / (1000 * 60 * 60 * 24)).toInt() // Convert to days
        }

        // Calculate the mode of the differences
        val frequencyMap = differences.groupingBy { it }.eachCount()
        val mode = frequencyMap.maxByOrNull { it.value }?.key

        return mode ?: 5 // Return the mode or a default value of 5 if no mode can be calculated
    }
    override fun onDestroy() {
        super.onDestroy()
        applicationScope.cancel() // Cancel the CoroutineScope when the activity is destroyed
    }




}