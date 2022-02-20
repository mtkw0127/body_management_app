package com.app.calendar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.app.calendar.model.TrainingEntity
import com.app.calendar.repository.TrainingRepository
import com.app.calendar.util.DateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate

class TrainingMeasureListActivity: AppCompatActivity() {

    private val trainingRepository: TrainingRepository by lazy {
        (application as TrainingApplication).repository
    }

    private val trainingFormActivityLauncher = registerForActivityResult(StartActivityForResult()) {

    }

    companion object {
        private const val INTENT_KEY = "DATE"
        fun createTrainingMeasureListIntent(context: Context, localDate: LocalDate): Intent{
            val intent = Intent(context.applicationContext, TrainingMeasureListActivity::class.java)
            intent.putExtra(INTENT_KEY, localDate)
            return intent
        }
    }

    private lateinit var dateTextView: TextView
    private lateinit var trainingMeasureRecyclerView : RecyclerView
    private lateinit var addBtn: Button
    private lateinit var backBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.training_measure_list)

        dateTextView = findViewById(R.id.date_text)
        trainingMeasureRecyclerView = findViewById(R.id.training_measure_list)
        addBtn = findViewById(R.id.save_btn)
        backBtn = findViewById(R.id.back_btn)

        val localDate = intent.getSerializableExtra(INTENT_KEY) as LocalDate
        dateTextView.text = localDate.toString()
        // 対象の日付に紐づくデータが存在すれば取得する.
        CoroutineScope(Dispatchers.Main).launch {
            dateTextView.text = localDate.toString()
            val trainingEntityList = trainingRepository.getEntityListByDate(localDate)
            trainingEntityList.collect {
                val adapter = TrainingMeasureListAdapter(it)
                trainingMeasureRecyclerView.adapter = adapter
                (trainingMeasureRecyclerView.adapter as RecyclerView.Adapter).notifyDataSetChanged()
            }
        }
        addBtn.setOnClickListener {
            val intent = TrainingFormActivity.createTrainingMeasureFormIntent(this, localDate)
            trainingFormActivityLauncher.launch(intent)
        }
        backBtn.setOnClickListener {
            finish()
        }
    }


    class TrainingMeasureListAdapter(private val trainingMeasureList: List<TrainingEntity>):
        RecyclerView.Adapter<TrainingMeasureListAdapter.ViewHolder>() {

        class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            var measureTimeTextView: TextView = view.findViewById(R.id.measure_time)
            var measureWeightTextView: TextView = view.findViewById(R.id.weight)
            var measureFatTextView: TextView = view.findViewById(R.id.fat)
            var captureImageView: ImageView = view.findViewById(R.id.image_view)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(viewGroup.context)
            val view = layoutInflater.inflate(R.layout.training_measure_list_cell, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val trainingEntity = trainingMeasureList[position]
            holder.measureTimeTextView.text = DateUtil.localDateConvertLocalTimeDateToTime(trainingEntity.capturedTime)
            holder.measureWeightTextView.text = "体重：${trainingEntity.weight}kg"
            holder.measureFatTextView.text = "体脂肪率：${trainingEntity.fatRate}%"
            holder.captureImageView.setImageURI(trainingEntity.photoUri?.toUri())

        }

        override fun getItemCount(): Int = trainingMeasureList.size
    }
}