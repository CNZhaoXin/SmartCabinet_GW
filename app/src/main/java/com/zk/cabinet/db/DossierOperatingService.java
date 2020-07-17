package com.zk.cabinet.db;

import com.zk.cabinet.bean.DossierOperating;

import java.util.ArrayList;
import java.util.List;

public class DossierOperatingService extends BaseService<DossierOperating, Long> {

    private static volatile DossierOperatingService instance;//单例

    public static DossierOperatingService getInstance () {
        if (instance == null) {
            synchronized (DossierOperatingService.class) {//保证异步处理安全操作
                if (instance == null) {
                    instance = new DossierOperatingService();
                }
            }
        }
        return instance;
    }


    public List<DossierOperating> getNullList(){
        return new ArrayList<>();
    }

}
