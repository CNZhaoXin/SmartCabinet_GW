package com.zk.cabinet.db;

import android.content.Context;

import com.zk.cabinet.dao.DaoMaster;
import com.zk.cabinet.dao.DaoSession;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;
import org.greenrobot.greendao.identityscope.IdentityScopeType;

/**
 * Created by WPC on 2018/1/11.
 */

public class DBHelper {

    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private static volatile DBHelper instance; // 单例

    private DBHelper() {
    }

    public static DBHelper getInstance() {
        if (instance == null) {
            synchronized (DBHelper.class) { // 保证异步处理安全操作
                if (instance == null) {
                    instance = new DBHelper();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        if (instance != null) {
            //此处devOpenHelper为自动生成开发所使用，发布版本需自定义
//            DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context.getApplicationContext(), "FileCabinet", null);

            DatabaseOpenHelper databaseOpenHelper = new DaoMaster.OpenHelper(context.getApplicationContext(), "FileManagementCabinet") {
                @Override
                public void onCreate(Database db) {
                    super.onCreate(db);
                }

                @Override
                public void onUpgrade(Database db, int oldVersion, int newVersion) {
                }
            };

            mDaoMaster = new DaoMaster(databaseOpenHelper.getWritableDatabase());
            mDaoSession = mDaoMaster.newSession(IdentityScopeType.None);

            // 创建数据库表
            // 档案柜/一体机 读写器设备表，档案架灯控板设备表 初始化
            DeviceService.getInstance().init(mDaoSession, mDaoSession.getDeviceDao());
            // 灯控记录表初始化
            LightControlRecordService.getInstance().init(mDaoSession, mDaoSession.getLightControlRecordDao());
            // 盘库计划表初始化
            InventoryPlanRecordService.getInstance().init(mDaoSession, mDaoSession.getInventoryPlanRecordDao());

            // 暂没用到
            // CabinetService.getInstance().init(mDaoSession, mDaoSession.getCabinetDao());
            // UserService.getInstance().init(mDaoSession, mDaoSession.getUserDao());
            // DossierService.getInstance().init(mDaoSession, mDaoSession.getDossierDao());
            // DossierOperatingService.getInstance().init(mDaoSession, mDaoSession.getDossierOperatingDao());
        }
    }

    /**
     * 清空所有数据库表数据
     */
    public void clear() {
        // 档案柜/一体机 读写器设备表/ 档案架灯控板设备表(暂时用的都是同一个,最好是分开,用不同的表)
        DeviceService.getInstance().deleteAll();
        // 清空灯控记录表
        LightControlRecordService.getInstance().deleteAll();
        // 盘库计划表
        InventoryPlanRecordService.getInstance().deleteAll();

        // 暂没用到
//        CabinetService.getInstance().deleteAll();
//        UserService.getInstance().deleteAll();
//        DossierOperatingService.getInstance().deleteAll();
//        DossierService.getInstance().deleteAll();
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

}
