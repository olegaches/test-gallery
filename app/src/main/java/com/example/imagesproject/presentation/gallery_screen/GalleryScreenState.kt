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
data class GalleryScreenState(
    val isLoading: Boolean = false,
    val imagesList: List<String> = emptyList(),
    val error: @RawValue UiText? = null,
    val lazyGridState: @RawValue LazyGridState = LazyGridState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0),
    val itemOffsetToScroll: Int = 0,
    val pagerScreenState: PagerScreenState = PagerScreenState(),
): Parcelable {
    companion object {
        val Saver: Saver<MutableStateFlow<GalleryScreenState>, *> = listSaver(
            save = { stateFlow ->
                val value = stateFlow.value
                val lazyGridState = value.lazyGridState
                listOf(
                    value.isLoading,
                    value.imagesList.joinToString(separator = " ")
                    ,
                    value.error.toString(),
                    lazyGridState.firstVisibleItemIndex,
                    lazyGridState.firstVisibleItemScrollOffset,
                    value.itemOffsetToScroll,
                    value.pagerScreenState,
                )
            },
            restore = {
                MutableStateFlow(
                    GalleryScreenState(
                        isLoading = it[0] as Boolean,
                        imagesList = (it[1] as String).split(' '),
                        //error = it[2] as UiText?, not parsing TODO
                        lazyGridState = LazyGridState(it[3] as Int, it[4] as Int),
                        itemOffsetToScroll = it[5] as Int,
                        pagerScreenState = it[6] as PagerScreenState,
                    )
                )
            }
        )
    }
}