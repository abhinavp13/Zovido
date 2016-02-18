package com.pabhinav.zovido;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author pabhinav
 */
public class MyEditTextHeading extends TextView {

    private Context mContext;

    public MyEditTextHeading(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MyEditTextHeading(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MyEditTextHeading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyEditTextHeading(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
    }

    public void init(){
        this.setTextColor(mContext.getResources().getColor(R.color.heading_grey));
    }
}

