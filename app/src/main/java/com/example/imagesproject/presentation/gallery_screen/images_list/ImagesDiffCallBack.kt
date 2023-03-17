package com.example.imagesproject.presentation.gallery_screen.images_list

import androidx.recyclerview.widget.DiffUtil

object ImagesDiffCallBack : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(oldItem: String, newItem: String) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: String, newItem: String) =
        oldItem == newItem
}