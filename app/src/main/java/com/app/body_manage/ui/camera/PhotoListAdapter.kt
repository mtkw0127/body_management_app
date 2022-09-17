package com.app.body_manage.ui.camera

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.body_manage.R

class PhotoListAdapter(private val dataSet: List<Uri>, private val deletePhoto: (Int) -> Unit) :
    RecyclerView.Adapter<PhotoListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView
        private val countText: TextView
        private val deleteButton: Button

        init {
            imageView = view.findViewById(R.id.photo_item)
            countText = view.findViewById(R.id.counter)
            deleteButton = view.findViewById(R.id.delete_photo)
        }

        fun setImage(uri: Uri) {
            imageView.setImageURI(uri)
        }

        fun setDeleteButtonClickListener(position: Int, deletePhoto: (Int) -> Unit) {
            deleteButton.setOnClickListener {
                deletePhoto.invoke(position)
            }
        }

        fun setCounter(position: Int) {
            countText.text = "${position + 1}枚目"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.photo_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setImage(dataSet[position])
        holder.setDeleteButtonClickListener(position, deletePhoto)
        holder.setCounter(position)
    }

    override fun getItemCount(): Int = dataSet.size

}