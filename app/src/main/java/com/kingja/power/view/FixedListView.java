package com.kingja.power.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Description：固定高度ListView
 * Create Time：2016/8/29 17:15
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class FixedListView extends ListView {
    public FixedListView(Context context) {
        super(context);
    }

    public FixedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}