package com.app.body_manage.ui.measure.body.edit

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.body_manage.databinding.SlideItemContainerBinding
import com.app.body_manage.ui.measure.body.edit.BodyMeasureEditFormViewModel.PhotoModel

class SliderAdapter(
    var sliderItems: List<PhotoModel>,
    private val photoDeleteAction: OnClickListener
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    class SliderViewHolder(val view: View, val binding: SlideItemContainerBinding) :
        RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SlideItemContainerBinding.inflate(layoutInflater, parent, false)
        return SliderViewHolder(binding.root, binding)
    }


    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.binding.imageSlide.setImageURI(sliderItems[position].uri)
        // 位置をtoolTipに設定
        holder.binding.deletePhotoBtn.tooltipText = position.toString()
        // 削除
        holder.binding.deletePhotoBtn.setOnClickListener(photoDeleteAction)
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = sliderItems.size
}