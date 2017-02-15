package com.kingja.power.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 项目名称：物联网城市防控(警用版)
 * 类描述：TODO
 * 创建人：KingJA
 * 创建时间：2016/3/25 14:04
 * 修改备注：
 */
public class GoUtil {
    public static void goActivity(Activity activity, Class clazz) {
        Intent intent = new Intent(activity,
                clazz);
        activity.startActivity(intent);
    }
    public static void goActivityOutOfActivity(Context context, Class clazz) {
        Intent intent = new Intent(context,
                clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void goActivityInReceiver(Context context, Class clazz) {
        Intent intent=new Intent(context,clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void goActivityAndFinish(Activity activity, Class clazz) {
        Intent intent = new Intent(activity,
                clazz);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goActivityWithBundle(Activity activity, Class clazz, Bundle bundle) {
        Intent intent = new Intent(activity,
                clazz);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static void goActivityForResult(Activity activity, Class clazz, int requestID) {
        Intent intent = new Intent(activity,
                clazz);
        activity.startActivityForResult(intent, requestID);
    }

    public static void goActivityForResultWithBundle(Activity activity, Class clazz, Bundle bundle, int requestID) {
        Intent intent = new Intent(activity,
                clazz);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestID);
    }



}
