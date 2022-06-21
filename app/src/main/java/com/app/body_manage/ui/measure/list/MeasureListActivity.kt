package com.app.body_manage.ui.measure.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.databinding.ActivityTrainingMeasureListBinding
import com.app.body_manage.ui.measure.form.BodyMeasureEditFormActivity
import com.app.body_manage.util.DateUtil
import java.time.LocalDate

class MeasureListActivity : AppCompatActivity() {

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).bodyMeasureRepository
    }

    private val trainingFormActivityLauncher =
        registerForActivityResult(StartActivityForResult()) { viewModel.reload() }

    private val bodyMeasureEditFormActivityLauncher =
        registerForActivityResult(StartActivityForResult()) { viewModel.reload() }

    private val localDate: LocalDate by lazy { intent.getSerializableExtra(INTENT_KEY) as LocalDate }

    private lateinit var binding: ActivityTrainingMeasureListBinding

    private lateinit var viewModel: MeasureListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingMeasureListBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        binding.dateText.text = DateUtil.localDateConvertJapaneseFormatYearMonthDay(localDate)
        viewModel = MeasureListViewModel(localDate, bodyMeasureRepository)
        binding.vm = viewModel
        setListener()
    }

    private fun setListener() {
        viewModel.measureList.observe(this) {
            val adapter = MeasureListAdapter(
                it,
                this@MeasureListActivity,
                bodyMeasureEditFormActivityLauncher
            )
            binding.trainingMeasureList.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        binding.bodyBtn.setOnClickListener {
            val intent = BodyMeasureEditFormActivity.createMeasureFormIntent(
                context = it.context,
                formDate = localDate,
            )
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