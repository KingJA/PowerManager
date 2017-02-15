package com.kingja.power.dao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.kingja.power.greenbean.BleInfo;

import org.greenrobot.greendao.query.QueryBuilder;

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

    /**
     * 插入一条记录
     *
     * @param bleInfo
     */
    public void insertBleInfo(BleInfo bleInfo) {
        BleInfoDao bleInfoDao = getBleInfoDao();
        bleInfoDao.insert(bleInfo);
    }


    /**
     * 查询最后一条
     *
     * @return
     */
    public BleInfo getLastBleInfo(String order) {
        BleInfoDao bleInfoDao = getBleInfoDao();
        List<BleInfo> list = bleInfoDao.queryBuilder().where(BleInfoDao.Properties.Order.eq(order)).orderDesc(BleInfoDao.Properties.Id).limit(1).list();
        return list.get(0);
    }

    /**
     * 删除所有蓝牙信息
     */
    public void deleteAllBleInfo() {
        BleInfoDao bleInfoDao = getBleInfoDao();
        bleInfoDao.deleteAll();
    }

}
