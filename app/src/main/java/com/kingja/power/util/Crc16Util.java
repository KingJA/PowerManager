package com.kingja.power.util;

/**
 * Description：2个字节校验码低位在前
 * Create Time：2017/2/17 16:47
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class Crc16Util {
    public static int alex_crc16(byte[] buf) {
        int c, crc = 0xFFFF;
        for (int i = 0; i < buf.length; i++) {
            c = buf[i] & 0x00FF;
            crc ^= c;
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x0001) != 0) {
                    crc >>= 1;
                    crc ^= 0xA001;
                } else
                    crc >>= 1;
            }
        }
        return (crc);
    }

    public static byte[] int2Dbyte(int res) {
        byte[] targets = new byte[2];
        targets[0] = (byte) ((res >> 8) & 0xff);
        targets[1] = (byte) (res & 0xff);
        return targets;
    }

    public static int byte2int(byte[] res) {
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00);
        return targets;
    }

    public static byte[] hexString2Bytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer
                    .valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }

    public static String getCrc16Code(String content) {
        byte[] bytes = hexString2Bytes(content);
        int hexInt = alex_crc16(bytes);//高位在前校验码
        return String.format("%04x", byte2int(int2Dbyte(hexInt)));

    }

    public static String fixHex(String hex, int size){
        StringBuffer sb = new StringBuffer(hex);
        int len = sb.length();
        for (int i = 0; i < size - len; i++){
            sb.insert(0, '0');
        }
        return sb.toString();

    }
}
