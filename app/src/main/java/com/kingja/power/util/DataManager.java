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
    private static final String DEVICE_ID = "DEVICE_ID";
    private static final String MILEAGE = "MILEAGE";
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

    public static String getDeviceId() {
        return (String) SpUtils.get(DEVICE_ID, EMPTY);
    }

    public static int getMileage() {
        return (Integer) SpUtils.get(MILEAGE, 100);
    }


    /*================================PUT================================*/

    public static void putServiceUUID(String data) {
        SpUtils.put(SERVICE_UUID, data);
    }

    public static void putWriteUUID(String data) {
        SpUtils.put(WRITE_UUID, data);
    }

    public static void putReadUUID(String data) {
        SpUtils.put(READ_UUID, data);
    }

    public static void putMacAddress(String data) {
        SpUtils.put(MAC_ADDRESS, data);
    }

    public static void putDeviceId(String data) {
        SpUtils.put(DEVICE_ID, data);
    }

    public static void putMileage(int mileage) {
        SpUtils.put(MILEAGE, mileage);
    }


}
