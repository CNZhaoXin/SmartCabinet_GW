package com.zk.cabinet.db;

import com.zk.cabinet.bean.OutboundInfo;
import com.zk.cabinet.bean.WarehousingInfo;

import java.util.ArrayList;
import java.util.List;

public class WarehousingService extends BaseService<WarehousingInfo, Long> {
    private static volatile WarehousingService instance;//单例

    private WarehousingService() {
    }

    public static WarehousingService getInstance() {
        if (instance == null) {
            synchronized (WarehousingService.class) {//保证异步处理安全操作
                if (instance == null) {
                    instance = new WarehousingService();
                }
            }
        }
        return instance;
    }

    public List<WarehousingInfo> getNullList(){
        return new ArrayList<WarehousingInfo>();
    }
}
