package com.example.imagesproject.presentation.gallery_screen.images_list

import android.content.Context
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.ListAdapter

class ImageListAdapter(
    private val context: Context,
    private val imageList: List<String>
) : ListAdapter<String, ImageViewHolder>(ImagesDiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImageViewHolder(
            ComposeView(parent.context)
        )

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun onViewRecycled(holder: ImageViewHolder) {
        // This is from the previous guidance
        // NOTE: You **do not** want to do this with Compose 1.2.0-beta02+
        // and RecyclerView 1.3.0-alpha02+
        holder.composeView.disposeComposition()
    }
}