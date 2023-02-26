package com.app.body_manage.ui.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.app.body_manage.R
import com.app.body_manage.databinding.ActivityMainBinding
import com.app.body_manage.ui.compare.CompareActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.measure.form.BodyMeasureEditFormActivity
import com.app.body_manage.ui.photoList.PhotoListActivity
import com.app.body_manage.ui.setting.SettingActivity
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
            BodyMeasureEditFormActivity.RESULT_CREATE -> "追加しました"
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

        onBackPressedDispatcher.addCallback {}

        adapter = CalendarAdapter(
            viewModel.today,
            this.applicationContext,
            measureListLauncher
        )
        binding.calendarGridView.adapter = adapter

        // 初期画面の年月設定
        supportActionBar?.title = viewModel.yearMonth

        initListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        val navigation = binding.bottomSheetInclude.bottomNavigator
        val menuCompare = navigation.menu.findItem(R.id.menu_compare)
        val menuPhoto = navigation.menu.findItem(R.id.menu_photo)
        val menuGraph = navigation.menu.findItem(R.id.menu_graph)
        val menuSettings = navigation.menu.findItem(R.id.menu_settings)
        menuCompare.setOnMenuItemClickListener {
            simpleLauncher.launch(CompareActivity.createIntent(this))
            return@setOnMenuItemClickListener true
        }
        menuPhoto.setOnMenuItemClickListener {
            simpleLauncher.launch(PhotoListActivity.createIntent(this))
            return@setOnMenuItemClickListener true
        }
        menuGraph.setOnMenuItemClickListener {
            simpleLauncher.launch(GraphActivity.createIntent(this))
            return@setOnMenuItemClickListener true
        }
        menuSettings.setOnMenuItemClickListener {
            simpleLauncher.launch(SettingActivity.createIntent(this))
            return@setOnMenuItemClickListener true
        }
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
                BodyMeasureEditFormActivity.createMeasureFormIntent(
                    this,
                    formType = BodyMeasureEditFormActivity.FormType.ADD,
                    formDate = LocalDate.now()
                )
            )
        }
    }
}