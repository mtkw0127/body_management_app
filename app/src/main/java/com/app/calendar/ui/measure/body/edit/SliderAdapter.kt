package com.app.calendar.ui.measure.body.edit

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.calendar.R
import com.makeramen.roundedimageview.RoundedImageView

class SliderAdapter(
    private val sliderItems: List<Uri>,
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    class SliderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: RoundedImageView = view.findViewById(R.id.imageSlide)
        fun setImage(uri: Uri) {
            imageView.setImageURI(uri)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder =
        SliderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.slide_item_container, parent, false)
        )

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.setImage(sliderItems[position])
    }

    override fun getItemCount(): Int = sliderItems.size
}