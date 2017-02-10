package com.kingja.power.util;

import android.view.View;

import java.util.Calendar;

/**
 * Description：防多击
 * Create Time：2016/8/29 10:20
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public abstract class NoDoubleClickListener implements View.OnClickListener {
    private long lastClickTime;
    private static final long MIN_CLICK_DELAY_TIME = 500;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }else{
            ToastUtil.showToast("您操作过于频繁...");
        }
    }

    /**
     * 子类实现抽象方法
     */
    public abstract void onNoDoubleClick(View v);
}
