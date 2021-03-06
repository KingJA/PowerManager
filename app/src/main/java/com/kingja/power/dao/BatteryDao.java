package com.kingja.power.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.kingja.power.greenbean.Battery;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BATTERY".
*/
public class BatteryDao extends AbstractDao<Battery, Long> {

    public static final String TABLENAME = "BATTERY";

    /**
     * Properties of entity Battery.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Mac = new Property(1, String.class, "mac", false, "MAC");
        public final static Property DeviceType = new Property(2, String.class, "deviceType", false, "DEVICE_TYPE");
        public final static Property DeviceId = new Property(3, String.class, "deviceId", false, "DEVICE_ID");
        public final static Property ProduceTime = new Property(4, String.class, "produceTime", false, "PRODUCE_TIME");
        public final static Property ProducterNo = new Property(5, String.class, "producterNo", false, "PRODUCTER_NO");
        public final static Property CreateTime = new Property(6, String.class, "createTime", false, "CREATE_TIME");
    };


    public BatteryDao(DaoConfig config) {
        super(config);
    }
    
    public BatteryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BATTERY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"MAC\" TEXT," + // 1: mac
                "\"DEVICE_TYPE\" TEXT," + // 2: deviceType
                "\"DEVICE_ID\" TEXT," + // 3: deviceId
                "\"PRODUCE_TIME\" TEXT," + // 4: produceTime
                "\"PRODUCTER_NO\" TEXT," + // 5: producterNo
                "\"CREATE_TIME\" TEXT);"); // 6: createTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BATTERY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Battery entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(2, mac);
        }
 
        String deviceType = entity.getDeviceType();
        if (deviceType != null) {
            stmt.bindString(3, deviceType);
        }
 
        String deviceId = entity.getDeviceId();
        if (deviceId != null) {
            stmt.bindString(4, deviceId);
        }
 
        String produceTime = entity.getProduceTime();
        if (produceTime != null) {
            stmt.bindString(5, produceTime);
        }
 
        String producterNo = entity.getProducterNo();
        if (producterNo != null) {
            stmt.bindString(6, producterNo);
        }
 
        String createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindString(7, createTime);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Battery entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(2, mac);
        }
 
        String deviceType = entity.getDeviceType();
        if (deviceType != null) {
            stmt.bindString(3, deviceType);
        }
 
        String deviceId = entity.getDeviceId();
        if (deviceId != null) {
            stmt.bindString(4, deviceId);
        }
 
        String produceTime = entity.getProduceTime();
        if (produceTime != null) {
            stmt.bindString(5, produceTime);
        }
 
        String producterNo = entity.getProducterNo();
        if (producterNo != null) {
            stmt.bindString(6, producterNo);
        }
 
        String createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindString(7, createTime);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Battery readEntity(Cursor cursor, int offset) {
        Battery entity = new Battery( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // mac
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // deviceType
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // deviceId
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // produceTime
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // producterNo
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // createTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Battery entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMac(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDeviceType(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDeviceId(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setProduceTime(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setProducterNo(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCreateTime(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Battery entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Battery entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
