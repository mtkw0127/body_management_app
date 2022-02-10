package com.app.calendar.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.app.calendar.R
import com.app.calendar.util.DateUtil
import java.time.LocalDate
import java.time.temporal.ChronoField

class CalendarAdapter(var localDate: LocalDate, private val context: Context): BaseAdapter() {
    // その月の日付一覧
    private var dateList = Array(42){CellInfo(LocalDate.now())}

    private var inflater:LayoutInflater

    // セル情報格納Obj
    class CellInfo(val localDate: LocalDate)

    /**
     * カレンダーの6*7のArrayを生成.
     */
    init {
        createDateList()
        // inflater初期化
        inflater = LayoutInflater.from(context)
    }

    /**
     * 先月のカレンダー構築
     */
    fun createPrevMonthCalendar() {
        localDate = localDate.minusMonths(1)
        createDateList()
    }

    /**
     * 翌月のカレンダー構築
     */
    fun createNextMonthCalendar() {
        localDate = localDate.plusMonths(1)
        createDateList()
    }

    private fun createDateList() {
        // 当該月の最初の曜日を取得
        val firstDateOfThisMonth = LocalDate.of(localDate.year, localDate.monthValue, 1)

        // 前月の最終日を取得
        val lastDateOfPrevMonth = firstDateOfThisMonth.minusDays(1)

        // 翌月の初日を取得
        val firstDateOfNextMonth = firstDateOfThisMonth.plusMonths(1)

        // 月曜(1)->日曜(7)を利用して当月の1日の配列のインデックス特定
        val firstIndexOfThisMonth = firstDateOfThisMonth.dayOfWeek.value % 7

        // 前月
        for((cnt, dateIndex) in (0 until firstIndexOfThisMonth).withIndex()) {
            dateList[dateIndex] = CellInfo(lastDateOfPrevMonth.minusDays((firstIndexOfThisMonth - cnt -1).toLong()))
        }

        // 当月
        // 当月のデータを適切なインデックに格納
        val lastDayOfThisMonth = localDate.range(ChronoField.DAY_OF_MONTH).maximum.toInt()
        val lastIndexOfThisMonth = firstIndexOfThisMonth+lastDayOfThisMonth
        for((cnt, dateIndex) in (firstIndexOfThisMonth until lastIndexOfThisMonth).withIndex()) {
            dateList[dateIndex] = CellInfo(firstDateOfThisMonth.plusDays(cnt.toLong()))
        }

        // 翌月
        for((cnt, dateIndex) in (lastIndexOfThisMonth until dateList.size).withIndex()) {
            dateList[dateIndex] = CellInfo(firstDateOfNextMonth.plusDays(cnt.toLong()))
        }
        notifyDataSetInvalidated()
    }

    /**
     * ViewHolderの数を返却
     */
    override fun getCount(): Int = dateList.size

    /**
     * アイテム取得
     */
    override fun getItem(p0: Int): CellInfo = dateList[p0]

    /**
     * ItemId取得
     */
    override fun getItemId(p0: Int): Long = p0.toLong()

    /**
     * ViewHolder生成
     */
    override fun getView(pos: Int, convertView: View?, parent: ViewGroup): View {
        // cellInfoからView情報を定義する
        val cellInfo = getItem(pos)
        val view = inflater.inflate(R.layout.calendar_cell, null)

        // 日付の設定
        val dateTextView = view.findViewById<TextView>(R.id.date)
        dateTextView.text = cellInfo.localDate.dayOfMonth.toString()

        // 文字色設定
        when(cellInfo.localDate.let { DateUtil.isHoliday(it) }) {
            // 休日
            DateUtil.DateType.HOLIDAY, DateUtil.DateType.COMPENSATION -> dateTextView.setTextColor(Color.RED)
            // 平日
            DateUtil.DateType.WEEKDAY -> dateTextView.setTextColor(Color.BLACK)
        }
        // 当日の場合背景に丸を表示
        if(cellInfo.localDate.isEqual(LocalDate.now())) {
            // TextViewと同じ高さのBitmap作成
            val color = ContextCompat.getColor(parent.context, R.color.teal_200)
            dateTextView.setBackgroundColor(color)
        }
        return view
    }
}