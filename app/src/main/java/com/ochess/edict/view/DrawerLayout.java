package com.ochess.edict.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

public class DrawerLayout extends androidx.drawerlayout.widget.DrawerLayout {
    public DrawerLayout(@NonNull Context context) {
        super(context);
    }
    public DrawerLayout(@NonNull Context context, AttributeSet attr) {
        super(context,attr);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
