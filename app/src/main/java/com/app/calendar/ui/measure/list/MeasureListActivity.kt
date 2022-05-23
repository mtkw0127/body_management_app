package com.app.calendar.ui.measure.list

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
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.calendar.ui.measure.body.edit.BodyMeasureEditFormActivity
import com.app.calendar.ui.measure.body.form.BodyMeasureFormActivity
import com.app.calendar.R.id
import com.app.calendar.R.layout
import com.app.calendar.R.string
import com.app.calendar.TrainingApplication
import com.app.calendar.databinding.TrainingMeasureListBinding
import com.app.calendar.model.BodyMeasureEntity
import com.app.calendar.repository.BodyMeasureRepository
import com.app.calendar.ui.measure.list.MeasureListActivity.TrainingMeasureListAdapter.ViewHolder
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

    private val trainingFormActivityLauncher = registerForActivityResult(StartActivityForResult()) {}

    private val bodyMeasureEditFormActivityLauncher = registerForActivityResult(StartActivityForResult()) {}

    private var fabMenuVisibility = View.GONE

    private var loading = MutableLiveData(false)

    private var entityList:List<BodyMeasureEntity> = mutableListOf()

    private lateinit var binding: TrainingMeasureListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TrainingMeasureListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val localDate = intent.getSerializableExtra(INTENT_KEY) as LocalDate
        binding.dateText.text = localDate.toString()
        // 対象の日付に紐づくデータが存在すれば取得する.
        CoroutineScope(Dispatchers.IO).launch {
            loading.postValue(true)
            binding.dateText.text = localDate.toString()
            val trainingEntityList = bodyMeasureRepository.getEntityListByDate(localDate)
            try {
                trainingEntityList.collect {
                    val isEmptyMessage = findViewById<TextView>(id.is_empty_message)
                    isEmptyMessage.text = this@MeasureListActivity.resources.getString(string.not_yet_measure_message)
                    if(it.isEmpty()) {
                        isEmptyMessage.visibility = View.VISIBLE
                    } else {
                        isEmptyMessage.visibility = View.GONE
                        this@MeasureListActivity.entityList = it
                        loading.postValue(false)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        loading.observe(this) { loading ->
            if(loading.not()) {
                val adapter = TrainingMeasureListAdapter(
                    entityList,
                    this@MeasureListActivity,
                    bodyMeasureEditFormActivityLauncher
                )
                binding.trainingMeasureList.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }

        binding.floatingActionButton.setOnClickListener {
            fabMenuVisibility = if(fabMenuVisibility == View.GONE)View.VISIBLE else View.GONE
            findViewById<ConstraintLayout>(id.end_card).visibility = fabMenuVisibility
        }

        binding.bodyBtn.setOnClickListener {
            val intent = BodyMeasureFormActivity.createTrainingMeasureFormIntent(this, localDate)
            trainingFormActivityLauncher.launch(intent)
        }
        binding.backBtn.setOnClickListener{finish()}
    }


    class TrainingMeasureListAdapter(
        private val bodyMeasureMeasureList: List<BodyMeasureEntity>,
        private val context: Context,
        private val bodyMeasureEditFormActivityLauncher: ActivityResultLauncher<Intent>
        ):
        RecyclerView.Adapter<ViewHolder>() {

        class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            var measureTimeTextView: TextView = view.findViewById(id.measure_time)
            var measureWeightTextView: TextView = view.findViewById(id.weight)
            var measureFatTextView: TextView = view.findViewById(id.fat)
            var captureImageView: ImageView = view.findViewById(id.image_view)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(viewGroup.context)
            val view = layoutInflater.inflate(layout.training_measure_list_cell, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val trainingEntity = bodyMeasureMeasureList[position]
            holder.measureTimeTextView.text = DateUtil.localDateConvertLocalTimeDateToTime(trainingEntity.capturedTime)
            holder.measureWeightTextView.text = "体重：${trainingEntity.weight}kg"
            holder.measureFatTextView.text = "体脂肪率：${trainingEntity.fatRate}%"
            holder.captureImageView.setImageURI(trainingEntity.photoUri?.toUri())

            holder.itemView.setOnClickListener {
                val intent = BodyMeasureEditFormActivity.createMeasureFormEditIntent(
                    context,
                    trainingEntity.capturedTime
                )
                bodyMeasureEditFormActivityLauncher.launch(intent)
            }
        }

        override fun getItemCount(): Int = bodyMeasureMeasureList.size
    }

    companion object {
        private const val INTENT_KEY = "DATE"
        fun createTrainingMeasureListIntent(context: Context, localDate: LocalDate): Intent{
            val intent = Intent(context.applicationContext, MeasureListActivity::class.java)
            intent.putExtra(INTENT_KEY, localDate)
            return intent
        }
    }
}