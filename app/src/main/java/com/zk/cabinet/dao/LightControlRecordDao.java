package com.zk.cabinet.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.zk.cabinet.bean.LightControlRecord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "LightControlRecord".
*/
public class LightControlRecordDao extends AbstractDao<LightControlRecord, Long> {

    public static final String TABLENAME = "LightControlRecord";

    /**
     * Properties of entity LightControlRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "ID");
        public final static Property DeviceID = new Property(1, String.class, "deviceID", false, "DeviceID");
        public final static Property Record = new Property(2, String.class, "record", false, "Record");
        public final static Property Num = new Property(3, int.class, "num", false, "Num");
    }


    public LightControlRecordDao(DaoConfig config) {
        super(config);
    }
    
    public LightControlRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LightControlRecord\" (" + //
                "\"ID\" INTEGER PRIMARY KEY ," + // 0: id
                "\"DeviceID\" TEXT NOT NULL ," + // 1: deviceID
                "\"Record\" TEXT NOT NULL ," + // 2: record
                "\"Num\" INTEGER NOT NULL );"); // 3: num
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LightControlRecord\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, LightControlRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getDeviceID());
        stmt.bindString(3, entity.getRecord());
        stmt.bindLong(4, entity.getNum());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, LightControlRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getDeviceID());
        stmt.bindString(3, entity.getRecord());
        stmt.bindLong(4, entity.getNum());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public LightControlRecord readEntity(Cursor cursor, int offset) {
        LightControlRecord entity = new LightControlRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // deviceID
            cursor.getString(offset + 2), // record
            cursor.getInt(offset + 3) // num
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, LightControlRecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDeviceID(cursor.getString(offset + 1));
        entity.setRecord(cursor.getString(offset + 2));
        entity.setNum(cursor.getInt(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(LightControlRecord entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(LightControlRecord entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(LightControlRecord entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}