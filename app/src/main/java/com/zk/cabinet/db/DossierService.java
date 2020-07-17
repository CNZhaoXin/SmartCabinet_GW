package com.zk.cabinet.db;

import com.zk.cabinet.bean.Dossier;

import java.util.ArrayList;
import java.util.List;

public class DossierService extends BaseService<Dossier, Long> {

    private static volatile DossierService instance;//单例

    public static DossierService getInstance () {
        if (instance == null) {
            synchronized (DossierService.class) {//保证异步处理安全操作
                if (instance == null) {
                    instance = new DossierService();
                }
            }
        }
        return instance;
    }


    public List<Dossier> getNullList(){
        return new ArrayList<>();
    }

}
