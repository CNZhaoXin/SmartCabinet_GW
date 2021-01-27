package com.zk.cabinet.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.zk.cabinet.bean.InventoryPlanRecord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "InventoryPlanRecord".
*/
public class InventoryPlanRecordDao extends AbstractDao<InventoryPlanRecord, Long> {

    public static final String TABLENAME = "InventoryPlanRecord";

    /**
     * Properties of entity InventoryPlanRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "ID");
        public final static Property PlanID = new Property(1, String.class, "planID", false, "PlanID");
        public final static Property HouseCode = new Property(2, String.class, "houseCode", false, "HouseCode");
        public final static Property DeviceList = new Property(3, String.class, "deviceList", false, "DeviceList");
    }


    public InventoryPlanRecordDao(DaoConfig config) {
        super(config);
    }
    
    public InventoryPlanRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"InventoryPlanRecord\" (" + //
                "\"ID\" INTEGER PRIMARY KEY ," + // 0: id
                "\"PlanID\" TEXT NOT NULL ," + // 1: planID
                "\"HouseCode\" TEXT NOT NULL ," + // 2: houseCode
                "\"DeviceList\" TEXT NOT NULL );"); // 3: deviceList
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"InventoryPlanRecord\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, InventoryPlanRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getPlanID());
        stmt.bindString(3, entity.getHouseCode());
        stmt.bindString(4, entity.getDeviceList());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, InventoryPlanRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getPlanID());
        stmt.bindString(3, entity.getHouseCode());
        stmt.bindString(4, entity.getDeviceList());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public InventoryPlanRecord readEntity(Cursor cursor, int offset) {
        InventoryPlanRecord entity = new InventoryPlanRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // planID
            cursor.getString(offset + 2), // houseCode
            cursor.getString(offset + 3) // deviceList
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, InventoryPlanRecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPlanID(cursor.getString(offset + 1));
        entity.setHouseCode(cursor.getString(offset + 2));
        entity.setDeviceList(cursor.getString(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(InventoryPlanRecord entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(InventoryPlanRecord entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(InventoryPlanRecord entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}