package com.app.body_manage.ui.graph

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.app.body_manage.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class GraphPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.graph, container, false)
        val entryList = arguments?.getParcelableArrayList<Entry>(KEY_ENTRY_LIST)
        val label = arguments?.getString(KEY_LABEL) ?: ""
        val chart = view.findViewById<LineChart>(R.id.chart)
        if (entryList != null) {
            createLineChart(chart, entryList, label)
        }
        return view
    }

    private fun createLineChart(
        lineChart: LineChart,
        entryList: List<Entry>,
        label: String
    ) {
        //LineDataSetのList
        val lineDataSets = mutableListOf<ILineDataSet>()
        //②DataSetにデータ格納
        val lineDataSet = LineDataSet(entryList, label)
        //③DataSetにフォーマット指定(3章で詳説)
        lineDataSet.color = Color.BLUE
        //リストに格納
        lineDataSets.add(lineDataSet)

        //④LineDataにLineDataSet格納
        val lineData = LineData(lineDataSets)
        //⑤LineChartにLineData格納

        // yの最大値取得
        val yMax = entryList.map { it.y }.maxOrNull() ?: 0F

        //⑥Chartのフォーマット指定(3章で詳説)
        //X軸の設定
        val chart = lineChart.apply {
            data = lineData
            xAxis.isEnabled = true
            xAxis.textColor = Color.BLACK
            axisLeft.axisMinimum = 0F
            axisLeft.axisMaximum = yMax * 1.1F
        }
        chart.invalidate()
    }

    companion object {
        private const val KEY_ENTRY_LIST = "KEY_ENTRY_LIST"
        private const val KEY_LABEL = "KEY_LABEL"
        fun newInstance(entryList: List<Entry>, label: String): GraphPageFragment {
            return GraphPageFragment().apply {
                arguments = bundleOf(
                    KEY_ENTRY_LIST to entryList,
                    KEY_LABEL to label
                )
            }
        }
    }
}