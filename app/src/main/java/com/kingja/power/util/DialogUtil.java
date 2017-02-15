package com.kingja.power.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.BaseAdapter;

import com.flyco.dialog.widget.NormalDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.kingja.power.R;

/**
 * Description：TODO
 * Create Time：2017/2/14 16:06
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class DialogUtil {
    public static NormalDialog getDoubleDialog(Context context, String title, String leftText, String rightText) {
        final NormalDialog dialog = new NormalDialog(context);
        dialog.isTitleShow(false)
                .content(title)
                .contentTextSize(15f)
                .bgColor(ContextCompat.getColor(context, R.color.bg_white))
                .cornerRadius(5)
                .contentGravity(Gravity.CENTER)
                .widthScale(0.85f)
                .contentTextColor(ContextCompat.getColor(context, R.color.font_3))
                .dividerColor(ContextCompat.getColor(context, R.color.bg_divider))
                .btnTextSize(15f, 15f)
                .btnText(leftText, rightText)
                .btnTextColor(ContextCompat.getColor(context, R.color.bg_blue), ContextCompat.getColor(context, R.color.bg_blue))
                .btnPressColor(ContextCompat.getColor(context, R.color.bg_press));
        return dialog;
    }

    public static NormalDialog getSingleDialog(Context context, String title, String confirm) {
        final NormalDialog dialog = new NormalDialog(context);
        dialog.isTitleShow(false)
                .content(title)
                .contentTextSize(15f)
                .btnNum(1)
                .bgColor(ContextCompat.getColor(context, R.color.bg_white))
                .cornerRadius(5)
                .contentGravity(Gravity.CENTER)
                .widthScale(0.85f)
                .contentTextColor(ContextCompat.getColor(context, R.color.font_3))
                .dividerColor(ContextCompat.getColor(context, R.color.bg_divider))
                .btnTextSize(15f, 15f)
                .btnText(confirm)
                .btnTextColor(ContextCompat.getColor(context, R.color.bg_blue))
                .btnPressColor(ContextCompat.getColor(context, R.color.bg_press));
        return dialog;

    }

    public static NormalListDialog getListDialog(Context context, String title, BaseAdapter adapter) {
        final NormalListDialog dialog = new NormalListDialog(context, adapter);
        dialog.title(title).layoutAnimation(null).heightScale(0.5f)
                .titleTextColor(ContextCompat.getColor(context, R.color.font_3))
                .titleBgColor(ContextCompat.getColor(context, R.color.bg_light))
                .dividerHeight(0.3f)
                .dividerColor(ContextCompat.getColor(context, R.color.bg_divider));
        return dialog;
    }
}
