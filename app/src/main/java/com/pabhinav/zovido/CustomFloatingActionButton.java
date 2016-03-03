package com.pabhinav.zovido;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.ViewGroup.MarginLayoutParams;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

import static android.support.design.R.styleable.FloatingActionButton;
import static android.support.design.R.styleable.FloatingActionButton_fabSize;
import static android.support.design.R.style.Widget_Design_FloatingActionButton;
import static android.support.design.R.dimen.design_fab_size_normal;
import static android.support.design.R.dimen.design_fab_size_mini;

public class CustomFloatingActionButton extends FloatingActionButton {

    private int mSize;

    public CustomFloatingActionButton(Context context) {
        this(context, null);
    }

    public CustomFloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, FloatingActionButton, defStyleAttr,
                Widget_Design_FloatingActionButton);
        this.mSize = a.getInt(FloatingActionButton_fabSize, 0);
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (SDK_INT < LOLLIPOP) {
            int size = this.getSizeDimension();
            int offsetVertical = (h - size) / 2;
            int offsetHorizontal = (w - size) / 2;
            MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
            params.leftMargin = params.leftMargin - offsetHorizontal;
            params.rightMargin = params.rightMargin - offsetHorizontal;
            params.topMargin = params.topMargin - offsetVertical;
            params.bottomMargin = params.bottomMargin - offsetVertical;
            setLayoutParams(params);
        }
    }

    private final int getSizeDimension() {
        switch (this.mSize) {
            case 0: default: return this.getResources().getDimensionPixelSize(design_fab_size_normal);
            case 1: return this.getResources().getDimensionPixelSize(design_fab_size_mini);
        }
    }
}
