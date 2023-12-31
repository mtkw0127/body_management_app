package com.app.body_manage.ui.graph

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.app.body_manage.R
import com.app.body_manage.ui.graph.GraphViewModel.MyEntry
import com.app.body_manage.util.DateUtil
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class GraphPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.graph, container, false)
        val entryList = requireArguments().getSerializable(KEY_ENTRY_LIST) as List<MyEntry>
        val label = arguments?.getString(KEY_LABEL) ?: ""
        val chart = view.findViewById<LineChart>(R.id.chart)
        createLineChart(chart, entryList, label)
        return view
    }

    private fun createLineChart(
        lineChart: LineChart,
        myEntryList: List<MyEntry>,
        label: String
    ) {
        // LineDataSetのList
        val lineDataSets = mutableListOf<ILineDataSet>()
        // ②DataSetにデータ格納
        val entryList = myEntryList.mapIndexed { index, data ->
            Entry().apply {
                x = index.toFloat()
                y = data.y
            }
        }.toList()
        val xAxisLabels =
            myEntryList.map { DateUtil.localDateConvertMMDD(it.axisLocalDateTime) }.toList()
        val lineDataSet = LineDataSet(entryList, label)

        // ③DataSetにフォーマット指定(3章で詳説)
        lineDataSet.color = Color.BLUE
        // リストに格納
        lineDataSets.add(lineDataSet)

        // ④LineDataにLineDataSet格納
        val lineData = LineData(lineDataSets)
        // ⑤LineChartにLineData格納
        // yの最大値取得
        val yMax = entryList.map { it.y }.maxOrNull() ?: 0F

        // ⑥Chartのフォーマット指定(3章で詳説)
        // X軸の設定
        val chart = lineChart.apply {
            data = lineData
            with(xAxis) {
                this.isEnabled = true
                this.setDrawGridLines(false)
                this.textColor = Color.BLACK
                this.position = BOTTOM
                this.setLabelCount(entryList.size, true)
                this.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
            }
            with(axisLeft) {
                this.axisMinimum = 0F
                this.axisMaximum = yMax * 1.1F
                this.setDrawGridLines(false)
            }
            with(axisRight) {
                this.isEnabled = false
            }
        }
        chart.notifyDataSetChanged()
        chart.invalidate()
    }

    companion object {
        private const val KEY_ENTRY_LIST = "KEY_ENTRY_LIST"
        private const val KEY_LABEL = "KEY_LABEL"
        fun newInstance(entryList: List<MyEntry>, label: String): GraphPageFragment {
            return GraphPageFragment().apply {
                arguments = bundleOf(
                    KEY_ENTRY_LIST to entryList,
                    KEY_LABEL to label
                )
            }
        }
    }
}
