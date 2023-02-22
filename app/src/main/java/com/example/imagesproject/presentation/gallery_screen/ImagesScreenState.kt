package com.example.imagesproject.presentation.gallery_screen

import android.os.Parcelable
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.example.imagesproject.core.util.UiText
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import kotlinx.coroutines.flow.MutableStateFlow
@Parcelize
data class ImagesScreenState(
    val isLoading: Boolean = false,
    val imagesList: List<String> = emptyList(),
    val error: @RawValue UiText? = null,
    val topBarVisible: Boolean = false,
    val topBarTitleText: String = "",
    val systemNavigationBarVisible: Boolean = true,
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
                    value.topBarVisible,
                    value.topBarTitleText,
                    value.systemNavigationBarVisible,
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
                        topBarVisible = it[3] as Boolean,
                        topBarTitleText = it[4] as String,
                        systemNavigationBarVisible = it[5] as Boolean,
                        lazyGridState = LazyGridState(it[6] as Int, it[7] as Int),
                        itemOffsetToScroll = it[8] as Int,
                        notValidImagesIndexes = if((it[9] as String).isNotBlank()) {
                            (it[9] as String).split(' ').map { item -> item.toInt() }
                        } else {
                            emptyList()
                        },
                        imageScreenState = it[10] as ImageScreenState,
                    )
                )
            }
        )
    }
}