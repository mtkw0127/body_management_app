package com.app.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import com.app.calendar.adapter.CalendarAdapter
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val calendarView = findViewById<GridView>(R.id.calendarGridView)
        calendarView.adapter = CalendarAdapter(LocalDate.now(), this.applicationContext)
    }
}