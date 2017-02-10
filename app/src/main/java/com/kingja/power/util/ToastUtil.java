package com.kingja.power.util;

import android.widget.Toast;

import com.kingja.power.base.BaseApplication;

/**
 * Description：TODO
 * Create Time：2016/8/4 17:10
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class ToastUtil {
   private static Toast mToast;

    public static void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(BaseApplication.getContext(), "", Toast.LENGTH_LONG);
        }
        mToast.setText(msg);
        mToast.show();
    }
}
