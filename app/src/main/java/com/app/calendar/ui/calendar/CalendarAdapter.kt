package com.app.calendar.ui.calendar

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.app.calendar.R
import com.app.calendar.TrainingApplication
import com.app.calendar.repository.BodyMeasureRepository
import com.app.calendar.ui.calendar.CalendarAdapter.MonthType.CURRENT
import com.app.calendar.ui.calendar.CalendarAdapter.MonthType.NEXT
import com.app.calendar.ui.calendar.CalendarAdapter.MonthType.NONE
import com.app.calendar.ui.calendar.CalendarAdapter.MonthType.PREV
import com.app.calendar.ui.measure.list.MeasureListActivity
import com.app.calendar.util.DateUtil
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalendarAdapter(
    var localDate: LocalDate,
    private val context: Context,
    private val trainingMeasureListLauncher: ActivityResultLauncher<Intent>,
) : BaseAdapter() {
    // その月の日付一覧
    private var dayOfWeek = arrayListOf("日", "月", "火", "水", "木", "金", "土")
    private var dateList = Array(42) { CellInfo(LocalDate.now(), NONE) }

    private var inflater: LayoutInflater

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (context.applicationContext as TrainingApplication).bodyMeasureRepository
    }

    enum class MonthType {
        PREV, CURRENT, NEXT, NONE
    }

    // セル情報格納Obj
    class CellInfo(val localDate: LocalDate, val monthType: MonthType)

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
        for ((cnt, dateIndex) in (0 until firstIndexOfThisMonth).withIndex()) {
            val prevMonthDate =
                lastDateOfPrevMonth.minusDays((firstIndexOfThisMonth - cnt - 1).toLong())
            dateList[dateIndex] = CellInfo(prevMonthDate, PREV)
        }

        // 当月
        // 当月のデータを適切なインデックに格納
        val lastDayOfThisMonth = localDate.range(ChronoField.DAY_OF_MONTH).maximum.toInt()
        val lastIndexOfThisMonth = firstIndexOfThisMonth + lastDayOfThisMonth
        for ((cnt, dateIndex) in (firstIndexOfThisMonth until lastIndexOfThisMonth).withIndex()) {
            val currentDate = firstDateOfThisMonth.plusDays(cnt.toLong())
            dateList[dateIndex] = CellInfo(currentDate, CURRENT)
        }

        // 翌月
        for ((cnt, dateIndex) in (lastIndexOfThisMonth until dateList.size).withIndex()) {
            val nextMonthDate = firstDateOfNextMonth.plusDays(cnt.toLong())
            dateList[dateIndex] = CellInfo(nextMonthDate, NEXT)
        }


        notifyDataSetInvalidated()
    }

    /**
     * ViewHolderの数を返却
     */
    override fun getCount(): Int = dateList.size + dayOfWeek.size

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
        val dayOfWeekCellView =
            checkNotNull(inflater.inflate(R.layout.calendar_cell_day_of_week, null))
        //  曜日設定
        if (pos < 7) {
            dayOfWeekCellView.findViewById<TextView>(R.id.day_of_week).text = dayOfWeek[pos]
            return dayOfWeekCellView
        }

        val calendarCellView = checkNotNull(inflater.inflate(R.layout.calendar_cell, null))
        // 日付設定
        // cellInfoからView情報を定義する
        val cellInfo = getItem(pos - 7)

        // 日付の設定
        val dateTextView = calendarCellView.findViewById<TextView>(R.id.date)
        dateTextView.text = cellInfo.localDate.dayOfMonth.toString()

        // 文字色設定
        when (cellInfo.localDate.let { DateUtil.isHoliday(it) }) {
            // 休日
            DateUtil.DateType.HOLIDAY, DateUtil.DateType.COMPENSATION -> dateTextView.setTextColor(
                Color.RED
            )
            // 平日
            DateUtil.DateType.WEEKDAY -> dateTextView.setTextColor(Color.BLACK)
        }
        // 土曜日は青色にする
        if (cellInfo.localDate.dayOfWeek == DayOfWeek.SATURDAY) dateTextView.setTextColor(Color.BLUE)

        // 当日の場合背景に丸を表示
        if (cellInfo.localDate.isEqual(LocalDate.now())) {
            // TextViewと同じ高さのBitmap作成
            val color = ContextCompat.getColor(parent.context, R.color.teal_200)
            dateTextView.setBackgroundColor(color)
        }
        // 先月・翌月の背景は灰色に変更
        when (cellInfo.monthType) {
            PREV, NEXT -> {
                val color = ContextCompat.getColor(parent.context, R.color.grey)
                val dateTextView = calendarCellView.findViewById<TextView>(R.id.date)
                dateTextView.setBackgroundColor(color)
            }
            else -> {}
        }

        CoroutineScope(Dispatchers.Main).launch {
            runCatching { bodyMeasureRepository.getEntityListByDate(cellInfo.localDate) }
                .onFailure { e -> e.printStackTrace() }
                .onSuccess { it ->
                    // セルタッチ時のイベント
                    calendarCellView.setOnClickListener {
                        val intent = MeasureListActivity.createTrainingMeasureListIntent(
                            it.context,
                            cellInfo.localDate
                        )
                        trainingMeasureListLauncher.launch(intent)
                    }
                    if (it.isNotEmpty()) {
                        val measureCntView =
                            calendarCellView.findViewById<TextView>(R.id.measure_cnt)
                        measureCntView.text = it.size.toString()
                        measureCntView.background =
                            context.resources.getDrawable(R.drawable.label, null)
                    }
                }
        }

        return calendarCellView
    }

}