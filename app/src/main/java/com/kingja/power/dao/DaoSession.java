package com.kingja.power.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.kingja.power.greenbean.BleInfo;

import com.kingja.power.dao.BleInfoDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig bleInfoDaoConfig;

    private final BleInfoDao bleInfoDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        bleInfoDaoConfig = daoConfigMap.get(BleInfoDao.class).clone();
        bleInfoDaoConfig.initIdentityScope(type);

        bleInfoDao = new BleInfoDao(bleInfoDaoConfig, this);

        registerDao(BleInfo.class, bleInfoDao);
    }
    
    public void clear() {
        bleInfoDaoConfig.getIdentityScope().clear();
    }

    public BleInfoDao getBleInfoDao() {
        return bleInfoDao;
    }

}