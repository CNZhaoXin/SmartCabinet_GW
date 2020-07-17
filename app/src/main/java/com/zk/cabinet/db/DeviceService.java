package com.zk.cabinet.db;

import com.zk.cabinet.bean.Cabinet;
import com.zk.cabinet.bean.Device;
import com.zk.cabinet.dao.CabinetDao;

import java.util.List;

public class DeviceService extends BaseService<Device, Long> {
    private static volatile DeviceService instance;//单例

    public static DeviceService getInstance() {
        if (instance == null) {
            synchronized (DeviceService.class) {//保证异步处理安全操作
                if (instance == null) {
                    instance = new DeviceService();
                }
            }
        }
        return instance;
    }
}
