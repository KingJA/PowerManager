package com.kingja.power;

import com.kingja.power.util.HexUtil;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static android.R.attr.data;
import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    private byte[] byteArr = {0x01, 0x02, 0x23};
    private static DecimalFormat scale2 = new java.text.DecimalFormat("0.00");

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String time = simpleDateFormat.format(new Date());
        System.out.println("yy-MM-dd HH:mm:ss "+time);
//
//        Date date2 = StrToDate(time);
//
//        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyyMMddHH:mm:ss");
//        String time2 = simpleDateFormat2.format(date2);
//        System.out.println("yyyyMMddHH:mm:ss "+time2);
//        System.out.println(HexUtil.encodeHexStr(Arrays.copyOfRange(byteArr,1,2)));

//        System.out.println(dex2Hex("11"));

    }

    public static String dex2Hex(String dexStr) {
        String hex = Integer.toHexString(Integer.valueOf(dexStr));
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or ");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();

    }

    /**
     * 日期转换成字符串
     *
     * @param date
     * @return str
     */
    public static String DateToStr(Date date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        return str;
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date StrToDate(String str) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}