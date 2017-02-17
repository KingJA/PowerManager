package com.kingja.power.util;

/**
 * Description：TODO
 * Create Time：2017/2/14 16:25
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class ByteUtil {


    public static byte[] hexStrToByte(String hex) {
        int l = hex.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer
                    .valueOf(hex.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }
    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    public static int hexStr2Dec(String hex) {
        return  Integer.valueOf(hex,16);
    }
    public static String byte2hex(byte [] buffer){
        String h = "";
        for(int i = 0; i < buffer.length; i++){
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if(temp.length() == 1){
                temp = "0" + temp;
            }
            h = h + temp;
        }
        return h;
    }
}
