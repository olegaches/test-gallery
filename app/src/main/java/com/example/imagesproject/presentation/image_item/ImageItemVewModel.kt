package com.example.imagesproject.presentation.image_item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.imagesproject.presentation.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageItemVewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {
    val imageUrl = savedStateHandle.get<String>(Constants.IMAGE_URL_PARAM)
}