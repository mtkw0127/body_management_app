package com.app.body_manage.ui.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.app.body_manage.R
import com.app.body_manage.databinding.ActivityMainBinding
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.photoList.PhotoListActivity
import com.app.body_manage.ui.setting.SettingActivity
import com.app.body_manage.util.DateUtil
import com.app.body_manage.util.OnSwipeTouchListener

class CalendarActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context) = Intent(context, CalendarActivity::class.java)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CalendarAdapter

    // 当日のトレーニング詳細画面 -> 一覧に戻ってきた場合の処理
    private val photoListLauncher =
        registerForActivityResult(StartActivityForResult()) {}

    // グラフ画面遷移
    private val graphListLauncher =
        registerForActivityResult(StartActivityForResult()) {}

    // 当日のトレーニング一覧画面
    private val trainingMeasureListActivityLauncher =
        registerForActivityResult(StartActivityForResult()) {
            adapter.notifyDataSetChanged()
        }

    // 設定画面への遷移
    private val settingMenuActivityLauncher =
        registerForActivityResult(StartActivityForResult()) {}

    private val viewModel: CalendarListViewModel = CalendarListViewModel()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)

        adapter = CalendarAdapter(
            viewModel.today,
            this.applicationContext,
            trainingMeasureListActivityLauncher
        )
        binding.calendarGridView.adapter = adapter

        // 初期画面の年月設定
        binding.yearMonthTxt.text = viewModel.yearMonth

        initListener()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.setting, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_setting -> {
                val intent = SettingActivity.createIntent(this)
                settingMenuActivityLauncher.launch(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        // 先月・翌月のリスナー登録
        binding.prevMonthBtn.setOnClickListener {
            val adapter = (binding.calendarGridView.adapter as CalendarAdapter)
            adapter.createPrevMonthCalendar()
            binding.yearMonthTxt.text =
                DateUtil.localDateConvertJapaneseFormatYearMonth(adapter.localDate)
        }
        binding.nextMonthBtn.setOnClickListener {
            val adapter = (binding.calendarGridView.adapter as CalendarAdapter)
            adapter.createNextMonthCalendar()
            binding.yearMonthTxt.text =
                DateUtil.localDateConvertJapaneseFormatYearMonth(adapter.localDate)
        }

        binding.calendarGridView.setOnTouchListener(object :
            OnSwipeTouchListener(this.applicationContext) {
            override fun up() {}
            override fun down() {}
            override fun right() {
                binding.prevMonthBtn.callOnClick()
            }

            override fun left() {
                binding.nextMonthBtn.callOnClick()
            }
        })

        val navigation = binding.bottomNavigator
        val menuCalendar = navigation.menu.findItem(R.id.menu_calendar)
        val menuPhoto = navigation.menu.findItem(R.id.menu_photo)
        val menuGraph = navigation.menu.findItem(R.id.menu_graph)
        menuCalendar.setOnMenuItemClickListener {
            onResume()
            return@setOnMenuItemClickListener true
        }
        menuPhoto.setOnMenuItemClickListener {
            photoListLauncher.launch(PhotoListActivity.createIntent(applicationContext))
            return@setOnMenuItemClickListener true
        }
        menuGraph.setOnMenuItemClickListener {
            graphListLauncher.launch(GraphActivity.createIntent(applicationContext))
            return@setOnMenuItemClickListener true
        }
    }
}