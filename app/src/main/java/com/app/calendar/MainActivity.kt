package com.app.calendar

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import com.app.calendar.adapter.CalendarAdapter
import com.app.calendar.util.DateUtil
import com.app.calendar.util.OnSwipeTouchListener
import java.time.LocalDate
import java.util.*

class MainActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendarView = findViewById<GridView>(R.id.calendarGridView)
        val calendarAdapter = CalendarAdapter(LocalDate.now(), this.applicationContext)
        calendarView.adapter = calendarAdapter

        // 初期画面の年月設定
        findViewById<TextView>(R.id.year_month_txt).text = DateUtil.localDateConvertJapaneseFormatYearMonth(calendarAdapter.localDate)

        // 先月・翌月のリスナー登録
        val prevMonthBtn = findViewById<Button>(R.id.prev_month_btn)
        val nextMonthBtn = findViewById<Button>(R.id.next_month_btn)
        prevMonthBtn.setOnClickListener {
            val adapter = (calendarView.adapter as CalendarAdapter)
            adapter.createPrevMonthCalendar()
            findViewById<TextView>(R.id.year_month_txt).text = DateUtil.localDateConvertJapaneseFormatYearMonth(adapter.localDate)
        }
        nextMonthBtn.setOnClickListener {
            val adapter = (calendarView.adapter as CalendarAdapter)
            adapter.createNextMonthCalendar()
            findViewById<TextView>(R.id.year_month_txt).text = DateUtil.localDateConvertJapaneseFormatYearMonth(adapter.localDate)
        }

        calendarView.setOnTouchListener(object : OnSwipeTouchListener(this.applicationContext) {
            override fun up() {}
            override fun down() {}
            override fun right() {
                prevMonthBtn.callOnClick()
            }
            override fun left() {
                nextMonthBtn.callOnClick()
            }
        })
    }
}