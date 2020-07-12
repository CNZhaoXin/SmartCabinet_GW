package com.zk.cabinet.db;

import com.zk.cabinet.bean.OutboundInfo;
import com.zk.cabinet.bean.User;
import com.zk.cabinet.dao.UserDao;
import com.zk.common.utils.LogUtil;

import java.util.List;

public class OutboundService extends BaseService<OutboundInfo, Long> {
    private static volatile OutboundService instance;//单例

    private OutboundService() {
    }

    public static OutboundService getInstance() {
        if (instance == null) {
            synchronized (OutboundService.class) {//保证异步处理安全操作
                if (instance == null) {
                    instance = new OutboundService();
                }
            }
        }
        return instance;
    }
}
