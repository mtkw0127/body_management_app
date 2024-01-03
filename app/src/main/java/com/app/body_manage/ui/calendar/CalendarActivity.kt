package com.app.body_manage.ui.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.app.body_manage.R
import com.app.body_manage.databinding.ActivityMainBinding
import com.app.body_manage.ui.measure.form.MeasureFormActivity
import com.app.body_manage.util.DateUtil
import java.time.LocalDate

class CalendarActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context) = Intent(context, CalendarActivity::class.java)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CalendarAdapter

    // シンプルなランチャー
    private val simpleLauncher = registerForActivityResult(StartActivityForResult()) {}

    private val measureListLauncher = registerForActivityResult(StartActivityForResult()) {
        adapter.notifyDataSetChanged()
    }

    private val registeredFromFab = registerForActivityResult(StartActivityForResult()) {
        val message = when (it.resultCode) {
            MeasureFormActivity.RESULT_CODE_ADD -> getString(R.string.message_saved)
            else -> null
        }
        message?.let {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
        adapter.notifyDataSetChanged()
    }

    private val viewModel: CalendarListViewModel = CalendarListViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)

        adapter = CalendarAdapter(
            viewModel.today,
            this.applicationContext,
            measureListLauncher
        )
        binding.calendarGridView.adapter = adapter
        // Show menu
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title =
            DateUtil.localDateConvertJapaneseFormatYearMonth(LocalDate.now())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        initListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        binding.next.setOnClickListener {
            val adapter = (binding.calendarGridView.adapter as CalendarAdapter)
            adapter.createNextMonthCalendar()
            supportActionBar?.title =
                DateUtil.localDateConvertJapaneseFormatYearMonth(adapter.localDate)
        }
        binding.prev.setOnClickListener {
            val adapter = (binding.calendarGridView.adapter as CalendarAdapter)
            adapter.createPrevMonthCalendar()
            supportActionBar?.title =
                DateUtil.localDateConvertJapaneseFormatYearMonth(adapter.localDate)
        }
        binding.addButton.setOnClickListener {
            registeredFromFab.launch(
                MeasureFormActivity.createMeasureFormIntent(
                    this,
                    LocalDate.now()
                )
            )
        }
    }
}
