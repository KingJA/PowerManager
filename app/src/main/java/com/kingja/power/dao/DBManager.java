package com.kingja.power.dao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.kingja.power.greenbean.Battery;
import com.kingja.power.greenbean.BleInfo;
import com.kingja.power.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private final static String dbName = "KingJA_Power";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;
    private final SQLiteDatabase db;

    public DBManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        db = getWritableDatabase();
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    private BleInfoDao getBleInfoDao() {
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        return daoSession.getBleInfoDao();
    }

    private BatteryDao getBatteryDao() {
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        return daoSession.getBatteryDao();
    }

    /**
     * 插入蓝牙记录
     *
     * @param bleInfo
     */
    public void insertBleInfo(BleInfo bleInfo) {
        BleInfoDao bleInfoDao = getBleInfoDao();
        bleInfoDao.insert(bleInfo);
    }

    /**
     * 插入电池信息
     *
     * @param battery
     */
    public void insertBattery(Battery battery) {
        BatteryDao bleInfoDao = getBatteryDao();
        bleInfoDao.insert(battery);
    }


    /**
     * 查询最后一条
     *
     * @return
     */
    public BleInfo getLastBleInfo(String order) {
        BleInfo bleInfo = new BleInfo();
        BleInfoDao bleInfoDao = getBleInfoDao();
        List<BleInfo> list = bleInfoDao.queryBuilder().where(BleInfoDao.Properties.Order.eq(order)).orderDesc
                (BleInfoDao.Properties.Id).limit(1).list();
        if (list.size() > 0) {
            bleInfo = list.get(0);
        }
        return bleInfo;
    }

    /**
     * 获取绑定指定MAC的设备
     *
     * @return
     */
    public List<Battery> getBindedBatteries(String mac) {
        BatteryDao batteryDao = getBatteryDao();
        List<Battery> list = batteryDao.queryBuilder().where(BatteryDao.Properties.Mac.eq(mac)).orderAsc(BatteryDao
                .Properties.DeviceType).list();
        return list;
    }

    /**
     * 删除所有蓝牙信息
     */
    public void deleteAllBleInfo() {
        BleInfoDao bleInfoDao = getBleInfoDao();
        bleInfoDao.deleteAll();
    }

    public List<Battery> getBindedBattery(String macAddress) {
        BatteryDao batteryDao = getBatteryDao();
        List<Battery> list = batteryDao.queryBuilder().where(BatteryDao.Properties.Mac.eq(macAddress)).list();
        return list;
    }

    public String getDeviceId(String macAddress) {
        String deviceId = "";
        BatteryDao batteryDao = getBatteryDao();
        List<Battery> list = batteryDao.queryBuilder().where(BatteryDao.Properties.Mac.eq(macAddress), BatteryDao
                .Properties.DeviceType.eq(Constants.DEVICE_TYPE_MAIN)).list();
        if (list.size() > 0) {
            deviceId = list.get(0).getDeviceId();
        }
        return deviceId;
    }

    public boolean hasBinded(String deviceId) {
        BatteryDao batteryDao = getBatteryDao();
        List<Battery> list = batteryDao.queryBuilder().where(BatteryDao.Properties.DeviceId.eq(deviceId)).list();
        return list.size() > 0;
    }

    public void deleteBattery(long id) {
        BatteryDao batteryDao = getBatteryDao();
        batteryDao.deleteByKey(id);
    }

}
