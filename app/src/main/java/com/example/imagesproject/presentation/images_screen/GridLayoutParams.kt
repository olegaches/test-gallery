package com.example.imagesproject.presentation.images_screen

data class GridLayoutParams(
    val visibleRows: Int,
    val visibleColumns: Int,
    val visibleGridSize: Int,
    val lastFullVisibleIndex: Int,
    val itemOffset: Int,
    val firstFullVisibleIndex: Int,
)
