package com.kingja.power.convert;

import android.util.Log;

import com.kingja.power.util.ByteUtil;

/**
 * Description：TODO
 * Create Time：2017/2/15 10:29
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class HeartConvert {
    private static String TAG="HeartConvert";
    //    运动状态（1Byte）+电池组状态(1Byte) +电压(6Byte)+平均温度(1Byte)+未充电放置天数（1Byte）+
    // 连续未充电满次数(1Byte)+本次充电时间（2Byte）+本次放电时间（2Byte）+续航公里数（1Byte）
    //02 02 464646464646 46 46 46 4601 8201 64
    //运动状态（1Byte）
    //00001919191919190c180c0c0148011902dc
    public static String getMoveStatus(String hexStr) {
        String result = "--";

        switch (ByteUtil.hexStr2Dec(hexStr.substring(0, 2))) {
            case 0://静止
                result = "静止";
                break;
            case 1://运动
                result = "运动";
                break;
            case 2://静止确认
                result = "静止确认";
                break;
        }
        return result;
    }

    //电池组状态(1Byte)电池组状态 包括充电0 放电1， 充电2， 未充满3
    public static String getPowerStatus(String hexStr) {
        Log.e(TAG, "getMoveStatus: "+ByteUtil.hexStr2Dec(hexStr.substring(2, 4)) );
        String result = "--";
        switch (ByteUtil.hexStr2Dec(hexStr.substring(2, 4))) {
            case 0://充电
                result = "充电";
                break;
            case 1://放电
                result = "放电";
                break;
            case 2://充电
                result = "充电";
                break;
            case 3://未充满
                result = "未充满";
                break;
        }
        return result;
    }

    //电压(6Byte)
    public static String getVoltage(String hexStr) {
        int sum = 0;
        for (int i = 0; i < 6; i ++) {
            int v = ByteUtil.hexStr2Dec(hexStr.substring(4 + i, 6 + i));
            Log.e("电压", "电压 " + i + " : " + v);
            sum += v;
        }
        return sum + "";
    }

    //平均温度(1Byte)
    public static String getTemperature(String hexStr) {
        return ByteUtil.hexStr2Dec(hexStr.substring(16, 18))+"";
    }

    //未充电放置天数（1Byte）
    public static String getWithoutChargerDays(String hexStr) {
        return hexStr.substring(18, 20);
    }

    // 连续未充电满次数(1Byte)
    public static String getWithoutChargerCount(String hexStr) {
        return hexStr.substring(20, 22);
    }

    //本次充电时间（2Byte）
    public static String getCurrentChargeTime(String hexStr) {
        return hexStr.substring(22, 26);
    }

    //本次放电时间（2Byte）
    public static String getCurrentDischargeTime(String hexStr) {
        return hexStr.substring(26, 30);
    }

    //续航公里数（1Byte）
    public static String getKilometre(String hexStr) {
        return ByteUtil.hexStr2Dec(hexStr.substring(30, 32))+"";
    }
}
