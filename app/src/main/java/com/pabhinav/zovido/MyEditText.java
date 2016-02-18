package com.pabhinav.zovido;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * @author pabhinav
 */
public class MyEditText extends EditText implements View.OnClickListener, View.OnFocusChangeListener {

    private Context mContext;
    private MyEditTextHeading myEditTextHeading;
    private AttributeSet attributeSet;
    private int myEditTextHeadingId;
    private int underlineViewId;
    private View underlineView;

    public MyEditText(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        attributeSet = attrs;
        mContext = context;
        init();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attributeSet = attrs;
        mContext = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        attributeSet = attrs;
        mContext = context;
        init();
    }

    private void init(){

        TypedArray a = mContext.obtainStyledAttributes(attributeSet, R.styleable.MyEditText);
        myEditTextHeadingId = a.getResourceId(R.styleable.MyEditText_headingText, -1);
        underlineViewId = a.getResourceId(R.styleable.MyEditText_underline,-1);
        a.recycle();

        this.setOnClickListener(this);
        this.setOnFocusChangeListener(this);
        this.setHintTextColor(mContext.getResources().getColor(R.color.hint_grey));
        this.setTextColor(mContext.getResources().getColor(android.R.color.black));
    }

    public MyEditTextHeading getMyEditTextHeading() {
        return myEditTextHeading;
    }

    public View getUnderlineView() {
        return underlineView;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (myEditTextHeadingId != -1) {
            myEditTextHeading = (MyEditTextHeading)getRootView().findViewById(myEditTextHeadingId);
        }

        if(underlineViewId != -1){
            underlineView = getRootView().findViewById(underlineViewId);
        }
    }

    @Override
    public void onClick(View v) {
        if(myEditTextHeading != null){
            myEditTextHeading.setTypeface(null, Typeface.BOLD);
            myEditTextHeading.setTextColor(getResources().getColor(R.color.blue_light));
        }
        if(underlineView != null){
            underlineView.setBackgroundColor(getResources().getColor(R.color.blue_light));
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            if(myEditTextHeading != null){
                myEditTextHeading.setTypeface(null, Typeface.BOLD);
                myEditTextHeading.setTextColor(getResources().getColor(R.color.blue_light));
            }
            if(underlineView != null){
                underlineView.setBackgroundColor(getResources().getColor(R.color.blue_light));
            }
        } else {
            if(myEditTextHeading != null){
                myEditTextHeading.setTypeface(null, Typeface.NORMAL);
                myEditTextHeading.setTextColor(getResources().getColor(R.color.heading_grey));
            }
            if(underlineView != null){
                underlineView.setBackgroundColor(getResources().getColor(R.color.heading_grey));
            }
        }
    }
}
