package com.app.calendar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.app.calendar.model.BodyMeasureEntity
import com.app.calendar.repository.BodyMeasureRepository
import com.app.calendar.util.DateUtil
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate

class MeasureListActivity: AppCompatActivity() {

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).repository
    }

    private val trainingFormActivityLauncher = registerForActivityResult(StartActivityForResult()) {

    }

    private val bodyMeasureEditFormActivityLauncher = registerForActivityResult(StartActivityForResult()) {

    }

    companion object {
        private const val INTENT_KEY = "DATE"
        fun createTrainingMeasureListIntent(context: Context, localDate: LocalDate): Intent{
            val intent = Intent(context.applicationContext, MeasureListActivity::class.java)
            intent.putExtra(INTENT_KEY, localDate)
            return intent
        }
    }

    private lateinit var dateTextView: TextView
    private lateinit var trainingMeasureRecyclerView : RecyclerView
    private lateinit var fab: FloatingActionButton
    private var fabMenuVisibility = View.GONE
    // Fabボタン
    private lateinit var bodyFabBtn: MaterialButton
    private lateinit var exerciseFabBtn: MaterialButton
    private lateinit var foodFabBtn: MaterialButton
    // 戻るボタン
    private lateinit var backBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.training_measure_list)

        dateTextView = findViewById(R.id.date_text)
        trainingMeasureRecyclerView = findViewById(R.id.training_measure_list)

        val localDate = intent.getSerializableExtra(INTENT_KEY) as LocalDate
        dateTextView.text = localDate.toString()
        // 対象の日付に紐づくデータが存在すれば取得する.
        CoroutineScope(Dispatchers.Main).launch {
            dateTextView.text = localDate.toString()
            val trainingEntityList = bodyMeasureRepository.getEntityListByDate(localDate)
            trainingEntityList.collect {
                val isEmptyMessage = findViewById<TextView>(R.id.is_empty_message)
                isEmptyMessage.text = this@MeasureListActivity.resources.getString(R.string.not_yet_measure_message)
                if(it.isEmpty()) {
                    isEmptyMessage.visibility = View.VISIBLE
                } else {
                    isEmptyMessage.visibility = View.GONE
                    val adapter = TrainingMeasureListAdapter(
                        it,
                        this@MeasureListActivity,
                        bodyMeasureEditFormActivityLauncher
                    )
                    trainingMeasureRecyclerView.adapter = adapter
                    (trainingMeasureRecyclerView.adapter as RecyclerView.Adapter).notifyDataSetChanged()
                }
            }
        }
        fab = findViewById(R.id.floating_action_button)
        fab.setOnClickListener {
            fabMenuVisibility = if(fabMenuVisibility == View.GONE)View.VISIBLE else View.GONE
            findViewById<ConstraintLayout>(R.id.end_card).visibility = fabMenuVisibility
        }

        bodyFabBtn = findViewById(R.id.body_btn)
        exerciseFabBtn = findViewById(R.id.exercise_btn)
        foodFabBtn = findViewById(R.id.food_btn)
        bodyFabBtn.setOnClickListener {
            val intent = BodyMeasureFormActivity.createTrainingMeasureFormIntent(this, localDate)
            trainingFormActivityLauncher.launch(intent)
        }
        backBtn = findViewById(R.id.back_btn)
        backBtn.setOnClickListener{finish()}
    }


    class TrainingMeasureListAdapter(
        private val bodyMeasureMeasureList: List<BodyMeasureEntity>,
        private val context: Context,
        private val bodyMeasureEditFormActivityLauncher: ActivityResultLauncher<Intent>
        ):
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
            val trainingEntity = bodyMeasureMeasureList[position]
            holder.measureTimeTextView.text = DateUtil.localDateConvertLocalTimeDateToTime(trainingEntity.capturedTime)
            holder.measureWeightTextView.text = "体重：${trainingEntity.weight}kg"
            holder.measureFatTextView.text = "体脂肪率：${trainingEntity.fatRate}%"
            holder.captureImageView.setImageURI(trainingEntity.photoUri?.toUri())

            holder.itemView.setOnClickListener {
                val intent = BodyMeasureEditFormActivity.createMeasureFormEditIntent(context, trainingEntity.capturedTime)
                bodyMeasureEditFormActivityLauncher.launch(intent)
            }
        }

        override fun getItemCount(): Int = bodyMeasureMeasureList.size
    }
}