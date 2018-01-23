package com.ctrip.videoapplication.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.ctrip.videoapplication.R;

/**
 * Created by gefufeng on 17/8/14.
 */

public class CTTourIconFont extends AppCompatTextView {

    public CTTourIconFont(Context context) {
        super(context);
    }

    public CTTourIconFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CTTourIconFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    private void init(AttributeSet attrs) {
        TypedArray array = this.getContext().obtainStyledAttributes(attrs, R.styleable.CTTourIconFontView);
        String file = array.getString(R.styleable.CTTourIconFontView_icon_font_file);
        array.recycle();
        if (file == null){
            file = "fonts/ct_font_tour.ttf";
        }
        Typeface iconfont = Typeface.createFromAsset(getContext().getAssets(),file);
        setTypeface(iconfont);
    }
}
