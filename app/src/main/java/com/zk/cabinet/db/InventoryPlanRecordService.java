package com.zk.cabinet.db;

import com.zk.cabinet.bean.InventoryPlanRecord;

public class InventoryPlanRecordService extends BaseService<InventoryPlanRecord, Long> {

    private static volatile InventoryPlanRecordService instance;//单例

    public static InventoryPlanRecordService getInstance() {
        if (instance == null) {
            synchronized (InventoryPlanRecordService.class) {//保证异步处理安全操作
                if (instance == null) {
                    instance = new InventoryPlanRecordService();
                }
            }
        }
        return instance;
    }
}
