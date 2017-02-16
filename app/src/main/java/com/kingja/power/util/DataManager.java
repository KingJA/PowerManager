package com.kingja.power.util;

/**
 * Description：TODO
 * Create Time：2016/8/15 13:51
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class DataManager {
    private static final String SERVICE_UUID = "SERVICE_UUID";
    private static final String MAC_ADDRESS = "MAC_ADDRESS";
    private static final String WRITE_UUID = "WRITE_UUID";
    private static final String READ_UUID = "READ_UUID";
    private static final String EMPTY = "";

    /*================================GET================================*/
    public static String getMacAddress() {
        return (String) SpUtils.get(MAC_ADDRESS, EMPTY);
    }

    public static String getServiceUUID() {
        return (String) SpUtils.get(SERVICE_UUID, EMPTY);
    }

    public static String getWriteUUID() {
        return (String) SpUtils.get(WRITE_UUID, EMPTY);
    }

    public static String getReadUUID() {
        return (String) SpUtils.get(READ_UUID, EMPTY);
    }


    /*================================PUT================================*/

    public static void putServiceUUID(String token) {
        SpUtils.put(SERVICE_UUID, token);
    }

    public static void putWriteUUID(String token) {
        SpUtils.put(WRITE_UUID, token);
    }

    public static void putReadUUID(String token) {
        SpUtils.put(READ_UUID, token);
    }

    public static void putMacAddress(String token) {
        SpUtils.put(MAC_ADDRESS, token);
    }


}
