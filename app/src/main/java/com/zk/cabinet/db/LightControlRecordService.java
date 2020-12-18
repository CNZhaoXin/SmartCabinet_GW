package com.zk.cabinet.db;

import com.zk.cabinet.bean.LightControlRecord;
import com.zk.cabinet.dao.LightControlRecordDao;

import java.util.List;

public class LightControlRecordService extends BaseService<LightControlRecord, Long> {
    private static volatile LightControlRecordService instance;//单例

    public static LightControlRecordService getInstance() {
        if (instance == null) {
            synchronized (LightControlRecordService.class) {//保证异步处理安全操作
                if (instance == null) {
                    instance = new LightControlRecordService();
                }
            }
        }
        return instance;
    }

    public List<LightControlRecord> queryListByRecord(String record) {
        return query(LightControlRecordDao.Properties.Record.eq(record));
    }

    public List<LightControlRecord> queryListByDeviceID(String deviceID) {
        return query(LightControlRecordDao.Properties.DeviceID.eq(deviceID));
    }

}
