package com.zk.cabinet.db;

import com.zk.cabinet.bean.DossierOperating;
import com.zk.cabinet.bean.User;
import com.zk.cabinet.dao.DossierOperatingDao;
import com.zk.cabinet.dao.UserDao;

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


    public DossierOperating queryByEPC(String epc) {
        if (epc == null) return null;

        List<DossierOperating> list = query(DossierOperatingDao.Properties.RfidNum.eq(epc));
        DossierOperating dossierOperating = null;
        if (list != null && list.size() > 0) {
            dossierOperating = list.get(0);
        }
        return dossierOperating;
    }

    public List<DossierOperating> queryBySelected() {
        return query(DossierOperatingDao.Properties.Selected.eq(true));
    }
}
