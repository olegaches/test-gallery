package com.example.imagesproject.presentation.gallery_screen.images_list;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public boolean onAddFocusables(@NonNull RecyclerView recyclerView, @NonNull ArrayList<View> views, int direction, int focusableMode) {
        return super.onAddFocusables(recyclerView, views, direction, focusableMode);
    }

    @Override
    public boolean onRequestChildFocus(@NonNull RecyclerView parent, @NonNull RecyclerView.State state, @NonNull View child, @Nullable View focused) {
        return super.onRequestChildFocus(parent, state, child, focused);
    }

    @Override
    public void onAdapterChanged(@Nullable RecyclerView.Adapter oldAdapter, @Nullable RecyclerView.Adapter newAdapter) {
        super.onAdapterChanged(oldAdapter, newAdapter);
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
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