package com.zk.cabinet.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.zk.cabinet.bean.User;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "User".
 */
public class UserDao extends AbstractDao<User, Long> {

    public static final String TABLENAME = "User";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "ID");
        public final static Property UuId = new Property(1, String.class, "uuId", false, "uuId");
        public final static Property UserCode = new Property(2, String.class, "userCode", false, "UserCode");
        public final static Property UserName = new Property(3, String.class, "userName", false, "UserName");
        public final static Property Cabinet = new Property(4, String.class, "cabinet", false, "Cabinet");
        public final static Property UserType = new Property(5, int.class, "userType", false, "UserType");
        public final static Property Password = new Property(6, String.class, "password", false, "Password");
        public final static Property CardID = new Property(7, String.class, "cardID", false, "CardID");
        public final static Property FingerPrint = new Property(8, byte[].class, "fingerPrint", false, "FingerPrint");
        public final static Property FaceInfo = new Property(9, String.class, "faceInfo", false, "FaceInfo");
        public final static Property ModifyTime = new Property(10, String.class, "modifyTime", false, "ModifyTime");
    }


    public UserDao(DaoConfig config) {
        super(config);
    }

    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"User\" (" + //
                "\"ID\" INTEGER PRIMARY KEY ," + // 0: id
                "\"uuId\" TEXT," + // 1: uuId
                "\"UserCode\" TEXT," + // 2: userCode
                "\"UserName\" TEXT," + // 3: userName
                "\"Cabinet\" TEXT," + // 4: cabinet
                "\"UserType\" INTEGER NOT NULL ," + // 5: userType
                "\"Password\" TEXT," + // 6: password
                "\"CardID\" TEXT," + // 7: cardID
                "\"FingerPrint\" BLOB," + // 8: fingerPrint
                "\"FaceInfo\" TEXT," + // 9: faceInfo
                "\"ModifyTime\" TEXT);"); // 10: modifyTime
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"User\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, User entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        String uuId = entity.getUuId();
        if (uuId != null) {
            stmt.bindString(2, uuId);
        }

        String userCode = entity.getUserCode();
        if (userCode != null) {
            stmt.bindString(3, userCode);
        }

        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(4, userName);
        }

        String cabinet = entity.getCabinet();
        if (cabinet != null) {
            stmt.bindString(5, cabinet);
        }
        stmt.bindLong(6, entity.getUserType());

        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(7, password);
        }

        String cardID = entity.getCardID();
        if (cardID != null) {
            stmt.bindString(8, cardID);
        }

        byte[] fingerPrint = entity.getFingerPrint();
        if (fingerPrint != null) {
            stmt.bindBlob(9, fingerPrint);
        }

        String faceInfo = entity.getFaceInfo();
        if (faceInfo != null) {
            stmt.bindString(10, faceInfo);
        }

        String modifyTime = entity.getModifyTime();
        if (modifyTime != null) {
            stmt.bindString(11, modifyTime);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        String uuId = entity.getUuId();
        if (uuId != null) {
            stmt.bindString(2, uuId);
        }

        String userCode = entity.getUserCode();
        if (userCode != null) {
            stmt.bindString(3, userCode);
        }

        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(4, userName);
        }

        String cabinet = entity.getCabinet();
        if (cabinet != null) {
            stmt.bindString(5, cabinet);
        }
        stmt.bindLong(6, entity.getUserType());

        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(7, password);
        }

        String cardID = entity.getCardID();
        if (cardID != null) {
            stmt.bindString(8, cardID);
        }

        byte[] fingerPrint = entity.getFingerPrint();
        if (fingerPrint != null) {
            stmt.bindBlob(9, fingerPrint);
        }

        String faceInfo = entity.getFaceInfo();
        if (faceInfo != null) {
            stmt.bindString(10, faceInfo);
        }

        String modifyTime = entity.getModifyTime();
        if (modifyTime != null) {
            stmt.bindString(11, modifyTime);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // uuId
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // userCode
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // userName
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // cabinet
                cursor.getInt(offset + 5), // userType
                cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // password
                cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // cardID
                cursor.isNull(offset + 8) ? null : cursor.getBlob(offset + 8), // fingerPrint
                cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // faceInfo
                cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10) // modifyTime
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUuId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUserCode(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setUserName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCabinet(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setUserType(cursor.getInt(offset + 5));
        entity.setPassword(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setCardID(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setFingerPrint(cursor.isNull(offset + 8) ? null : cursor.getBlob(offset + 8));
        entity.setFaceInfo(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setModifyTime(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
    }

    @Override
    protected final Long updateKeyAfterInsert(User entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    @Override
    public Long getKey(User entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(User entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }

}
