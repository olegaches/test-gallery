package com.example.imagesproject.presentation.gallery_screen

import android.os.Parcelable
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.example.imagesproject.core.util.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ImagesScreenState(
    val isLoading: Boolean = false,
    val imagesList: List<String> = emptyList(),
    val error: @RawValue UiText? = null,
    val lazyGridState: @RawValue LazyGridState = LazyGridState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0),
    val itemOffsetToScroll: Int = 0,
    val notValidImagesIndexes: List<Int> = emptyList(),
    val imageScreenState: ImageScreenState = ImageScreenState(),
): Parcelable {
    companion object {
        val Saver: Saver<MutableStateFlow<ImagesScreenState>, *> = listSaver(
            save = { stateFlow ->
                val value = stateFlow.value
                listOf(
                    value.isLoading,
                    value.imagesList.joinToString(separator = " ")
                    ,
                    value.error.toString(),
                    value.lazyGridState.firstVisibleItemIndex,
                    value.lazyGridState.firstVisibleItemScrollOffset,
                    value.itemOffsetToScroll,
                    value.notValidImagesIndexes.joinToString(separator = " "),
                    value.imageScreenState,
                )
            },
            restore = {
                MutableStateFlow(
                    ImagesScreenState(
                        isLoading = it[0] as Boolean,
                        imagesList = (it[1] as String).split(' '),
                        //error = it[2] as UiText?, not parsing TODO
                        lazyGridState = LazyGridState(it[3] as Int, it[4] as Int),
                        itemOffsetToScroll = it[5] as Int,
                        notValidImagesIndexes = if((it[6] as String).isNotBlank()) {
                            (it[6] as String).split(' ').map { item -> item.toInt() }
                        } else {
                            emptyList()
                        },
                        imageScreenState = it[7] as ImageScreenState,
                    )
                )
            }
        )
    }
}