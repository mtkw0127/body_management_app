package com.app.calendar.ui.measure.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.app.calendar.R.string
import com.app.calendar.TrainingApplication
import com.app.calendar.databinding.TrainingMeasureListBinding
import com.app.calendar.model.BodyMeasureEntity
import com.app.calendar.repository.BodyMeasureRepository
import com.app.calendar.ui.measure.body.form.BodyMeasureFormActivity
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MeasureListActivity : AppCompatActivity() {

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).repository
    }

    private val trainingFormActivityLauncher =
        registerForActivityResult(StartActivityForResult()) {}

    private val bodyMeasureEditFormActivityLauncher =
        registerForActivityResult(StartActivityForResult()) {}

    private var fabMenuVisibility = View.GONE

    private var loading = MutableLiveData(false)

    private var entityList: List<BodyMeasureEntity> = mutableListOf()

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
            runCatching {
                trainingEntityList.collect {
                    binding.isEmptyMessage.text =
                        this@MeasureListActivity.resources.getString(string.not_yet_measure_message)
                    if (it.isEmpty()) {
                        binding.isEmptyMessage.visibility = View.VISIBLE
                    } else {
                        binding.isEmptyMessage.visibility = View.GONE
                        this@MeasureListActivity.entityList = it
                        loading.postValue(false)
                    }
                }
            }.onFailure { e -> e.printStackTrace() }
        }

        loading.observe(this) { loading ->
            if (loading.not()) {
                val adapter = MeasureListAdapter(
                    entityList,
                    this@MeasureListActivity,
                    bodyMeasureEditFormActivityLauncher
                )
                binding.trainingMeasureList.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }

        binding.floatingActionButton.setOnClickListener {
            fabMenuVisibility = if (fabMenuVisibility == View.GONE) View.VISIBLE else View.GONE
            binding.endCard.visibility = fabMenuVisibility
        }

        binding.bodyBtn.setOnClickListener {
            val intent = BodyMeasureFormActivity.createTrainingMeasureFormIntent(this, localDate)
            trainingFormActivityLauncher.launch(intent)
        }
        binding.backBtn.setOnClickListener { finish() }
    }

    companion object {
        private const val INTENT_KEY = "DATE"
        fun createTrainingMeasureListIntent(context: Context, localDate: LocalDate): Intent {
            val intent = Intent(context.applicationContext, MeasureListActivity::class.java)
            intent.putExtra(INTENT_KEY, localDate)
            return intent
        }
    }
}