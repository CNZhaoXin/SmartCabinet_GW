package com.zk.cabinet.db;

import com.zk.cabinet.bean.Cabinet;
import com.zk.cabinet.dao.CabinetDao;
import com.zk.cabinet.dao.DossierDao;

import java.util.List;

public class CabinetService extends BaseService<Cabinet, Long> {
    private static volatile CabinetService instance;//单例

    public static CabinetService getInstance() {
        if (instance == null) {
            synchronized (CabinetService.class) {//保证异步处理安全操作
                if (instance == null) {
                    instance = new CabinetService();
                }
            }
        }
        return instance;
    }

    public Cabinet queryEqFloorAndPosition(int floor, int position) {
        List<Cabinet> list = queryBuilder().where(
                CabinetDao.Properties.Floor.eq(floor),
                CabinetDao.Properties.Position.eq(position)).list();
        Cabinet cabinet = null;
        if (list != null && list.size() > 0) {
            cabinet = list.get(0);
        }
        return cabinet;
    }

    public void mainBuild() {
        Cabinet[] cabinets = new Cabinet[120];
        for (int i = 1; i < 121; i++) {
            cabinets[i - 1] = new Cabinet();
            cabinets[i - 1].setId(null);
            cabinets[i - 1].setDeviceId(null);
            cabinets[i - 1].setFloor(i % 5 == 0 ? (5) : (i % 5));
            cabinets[i - 1].setPosition(i % 5 == 0 ? (i / 5) : (i / 5 + 1));
            cabinets[i - 1].setProportion(1);
            cabinets[i - 1].setAntennaNumber((cabinets[i - 1].getFloor() - 1) * 24 +  cabinets[i - 1].getPosition());
        }
        insert(cabinets);
    }
}
