package com.example.imagesproject.presentation.gallery_screen.images_list;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class AdaptiveGridLayoutManager extends GridLayoutManager {

    private int columnWidth = 0;
    private boolean columnWidthChanged = true;

    public AdaptiveGridLayoutManager(@NotNull Context context, int columnWidth) {
        super(context, 1);
        setColumnWidth(columnWidth);
    }

    private void setColumnWidth(int newColumnWidth) {
        int columnWidth = this.columnWidth;
        if (newColumnWidth > 0 && newColumnWidth != columnWidth) {
            this.columnWidth = newColumnWidth;
            columnWidthChanged = true;
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int columnWidth = this.columnWidth;
        if (columnWidthChanged && columnWidth > 0) {
            int totalSpace;
            if (this.getOrientation() == VERTICAL) {
                totalSpace = this.getWidth() - this.getPaddingRight();
            } else {
                totalSpace = this.getHeight() - this.getPaddingBottom();
            }
            int spanCount = Math.max(1, totalSpace / columnWidth);
            this.setSpanCount(spanCount);
            this.columnWidthChanged = false;
        }
        super.onLayoutChildren(recycler, state);
    }
}