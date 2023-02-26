package com.app.body_manage.ui.graph

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.body_manage.R
import com.app.body_manage.databinding.ActivityGraphBinding
import com.app.body_manage.ui.calendar.CalendarActivity
import com.app.body_manage.ui.compare.CompareActivity
import com.app.body_manage.ui.graph.GraphViewModel.MyEntry
import com.app.body_manage.ui.photoList.PhotoListActivity
import com.app.body_manage.ui.setting.SettingActivity
import com.google.android.material.tabs.TabLayoutMediator

class GraphActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGraphBinding

    private lateinit var viewModel: GraphViewModel

    // 当日のトレーニング詳細画面 -> 一覧に戻ってきた場合の処理
    private val simpleLauncher =
        registerForActivityResult(StartActivityForResult()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        viewModel = GraphViewModel(application = application)
        viewModel.loadBodyMeasure()

        viewModel.entryList.observe(this) {
            binding.pager.adapter = PagerAdapter(fa = this, entryList = it.toList())

            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                tab.text = if (position == 0) "体重" else "体脂肪率"
            }.attach()
        }

        val navigation = binding.bottomSheetInclude.bottomNavigator
        val menuCalendar = navigation.menu.findItem(R.id.menu_calendar)
        val menuCompare = navigation.menu.findItem(R.id.menu_compare)
        val menuPhoto = navigation.menu.findItem(R.id.menu_photo)
        val menuSettings = navigation.menu.findItem(R.id.menu_setting)
        menuCalendar.setOnMenuItemClickListener {
            simpleLauncher.launch(CalendarActivity.createIntent(this))
            return@setOnMenuItemClickListener true
        }
        menuCompare.setOnMenuItemClickListener {
            simpleLauncher.launch(CompareActivity.createIntent(this))
            return@setOnMenuItemClickListener true
        }
        menuPhoto.setOnMenuItemClickListener {
            simpleLauncher.launch(PhotoListActivity.createIntent(this))
            return@setOnMenuItemClickListener true
        }
        menuSettings.setOnMenuItemClickListener {
            simpleLauncher.launch(SettingActivity.createIntent(this))
            return@setOnMenuItemClickListener true
        }
    }

    private inner class PagerAdapter(fa: FragmentActivity, val entryList: List<List<MyEntry>>) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = entryList.size

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) {
                GraphPageFragment.newInstance(entryList[position], "体重")
            } else {
                GraphPageFragment.newInstance(entryList[position], "体脂肪率")
            }
        }
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, GraphActivity::class.java)
    }
}