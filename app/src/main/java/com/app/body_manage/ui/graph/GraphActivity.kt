package com.app.body_manage.ui.graph

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.body_manage.databinding.ActivityGraphBinding
import com.github.mikephil.charting.data.Entry

class GraphActivity : FragmentActivity() {

    private lateinit var binding: ActivityGraphBinding

    private lateinit var viewModel: GraphViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        viewModel = GraphViewModel(application = application)
        viewModel.loadBodyMeasure()

        viewModel.entryList.observe(this) {
            binding.pager.adapter = PagerAdapter(fa = this, entryList = it.toList())
        }
    }

    private inner class PagerAdapter(fa: FragmentActivity, val entryList: List<List<Entry>>) :
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