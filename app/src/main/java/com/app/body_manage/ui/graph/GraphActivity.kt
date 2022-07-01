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
import com.app.body_manage.ui.graph.GraphViewModel.MyEntry
import com.app.body_manage.ui.photoList.PhotoListActivity
import com.google.android.material.tabs.TabLayoutMediator

class GraphActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGraphBinding

    private lateinit var viewModel: GraphViewModel

    // 当日のトレーニング詳細画面 -> 一覧に戻ってきた場合の処理
    private val calendarLauncher =
        registerForActivityResult(StartActivityForResult()) {}

    // グラフ画面遷移
    private val photoLauncher =
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

        val navigation = binding.bottomNavigator
        val menuCalendar = navigation.menu.findItem(R.id.menu_calendar)
        val menuPhoto = navigation.menu.findItem(R.id.menu_photo)
        val menuGraph = navigation.menu.findItem(R.id.menu_graph)
        menuCalendar.setOnMenuItemClickListener {
            finish()
            return@setOnMenuItemClickListener true
        }
        menuPhoto.setOnMenuItemClickListener {
            calendarLauncher.launch(PhotoListActivity.createIntent(applicationContext))
            return@setOnMenuItemClickListener true
        }
        menuGraph.setOnMenuItemClickListener {
            onResume()
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