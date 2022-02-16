package com.app.calendar

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.app.calendar.adapter.CalendarAdapter
import com.app.calendar.util.DateUtil
import com.app.calendar.util.OnSwipeTouchListener
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private val trainingDetailActivityLauncher = registerForActivityResult(StartActivityForResult()) {
        // 当日のトレーニング詳細画面 -> 一覧に戻ってきた場合の処理
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendarView = findViewById<GridView>(R.id.calendarGridView)
        val calendarAdapter = CalendarAdapter(LocalDate.now(), this.applicationContext, trainingDetailActivityLauncher)
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