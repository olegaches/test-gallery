package com.example.imagesproject.presentation.gallery_screen.images_list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.ListAdapter
import com.example.imagesproject.R

class ImageListAdapter(
) : ListAdapter<String, ImageViewHolder>(ImagesDiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ImageViewHolder(
            inflater.inflate(R.layout.image_item, parent, false)
            //ComposeView(parent.context)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}