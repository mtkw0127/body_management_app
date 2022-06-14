package com.app.body_manage.ui.graph

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.body_manage.databinding.ActivityGraphBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class GraphActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGraphBinding

    private lateinit var viewModel: GraphViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        viewModel = GraphViewModel(application = application)
        viewModel.loadBodyMeasure()

        viewModel.weightEntryList.observe(this) {
            createLineChart(binding.chart1)
        }

        viewModel.fatEntryList.observe(this) {
            createLineChart(binding.chart2)
        }
    }

    private fun createLineChart(lineChart: LineChart) {
        //LineDataSetのList
        val lineDataSets = mutableListOf<ILineDataSet>()
        //②DataSetにデータ格納
        val lineDataSet = LineDataSet(viewModel.fatEntryList.value, "square")
        //③DataSetにフォーマット指定(3章で詳説)
        lineDataSet.color = Color.BLUE
        //リストに格納
        lineDataSets.add(lineDataSet)

        //④LineDataにLineDataSet格納
        val lineData = LineData(lineDataSets)
        //⑤LineChartにLineData格納

        //⑥Chartのフォーマット指定(3章で詳説)
        //X軸の設定
        lineChart.apply {
            data = lineData
            xAxis.isEnabled = true
            xAxis.textColor = Color.BLACK
        }.invalidate()
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, GraphActivity::class.java)
    }
}