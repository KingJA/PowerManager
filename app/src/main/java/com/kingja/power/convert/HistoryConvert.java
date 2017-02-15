package com.kingja.power.convert;

import android.util.Log;

import com.kingja.power.util.ByteUtil;

/**
 * Description：TODO
 * Create Time：2017/2/15 10:29
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class HistoryConvert {
    private static String TAG = "HeartConvert";
//    历史充电次数（2Byte）+历史充电总时间(4Byte)+历史放电次数（2Byte）+历史放电总时间(4Byte)+历史行驶总公里数(2Byte)+满放时间(2Byte)

    //历史充电次数（2Byte）
    public static String getChargerCount(String hexStr) {
        return ByteUtil.hexStr2Dec(hexStr.substring(0, 4)) + "";
    }

    //历史充电总时间(4Byte)
    public static String getChargerTime(String hexStr) {
        return getDayHourTime(ByteUtil.hexStr2Dec(hexStr.substring(4, 12)));
    }

    //历史放电次数（2Byte）
    public static String getDischargerCount(String hexStr) {
        return ByteUtil.hexStr2Dec(hexStr.substring(12, 16)) + "";
    }

    //历史放电总时间(4Byte)
    public static String getDischargerTime(String hexStr) {
        return getDayHourTime(ByteUtil.hexStr2Dec(hexStr.substring(16, 24)));
    }

    //历史行驶总公里数(2Byte)
    public static String getTotleKilometre(String hexStr) {
        return ByteUtil.hexStr2Dec(hexStr.substring(24, 28)) + "";
    }

    //满放时间(2Byte)
    public static String getFullTime(String hexStr) {
        return ByteUtil.hexStr2Dec(hexStr.substring(28, 32)) + "";
    }

    private static String getDayHourTime(int mins) {
        String result = "--";
        if (mins ==0) {//0
        } else if (mins>0&&mins < 60) {//1小时内
            result = result + "分钟";
        } else if (mins >= 60 && mins < 60 * 24) {//一天内
            result = (mins / 60) + "小时" + (mins % 60) + "分钟";
        } else {//超过一天
            result = (mins / (60 * 24)) + "天" + (mins % (60 * 24) / 60) + "小时";
        }
        return result;
    }

}
