package com.app.body_manage.ui.measure.list

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.app.body_manage.R.id
import com.app.body_manage.model.BodyMeasureEntity
import com.app.body_manage.ui.measure.body.edit.BodyMeasureEditFormActivity
import com.app.body_manage.ui.measure.list.MeasureListAdapter.ViewHolder
import com.app.body_manage.util.DateUtil

class MeasureListAdapter(
    private val bodyMeasureMeasureList: List<BodyMeasureEntity>,
    private val context: Context,
    private val bodyMeasureEditFormActivityLauncher: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        holder.measureTimeTextView.text =
            DateUtil.localDateConvertLocalTimeDateToTime(trainingEntity.capturedTime)
        holder.measureWeightTextView.text = "体重：${trainingEntity.weight}kg"
        holder.measureFatTextView.text = "体脂肪率：${trainingEntity.fatRate}%"
        holder.captureImageView.setImageURI(trainingEntity.photoUri?.toUri())

        holder.itemView.setOnClickListener {
            val intent = BodyMeasureEditFormActivity.createMeasureEditIntent(
                context = context,
                captureTime = trainingEntity.capturedTime
            )
            bodyMeasureEditFormActivityLauncher.launch(intent)
        }
    }

    override fun getItemCount(): Int = bodyMeasureMeasureList.size
}