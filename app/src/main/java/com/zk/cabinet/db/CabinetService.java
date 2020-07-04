package com.zk.cabinet.db;

import com.zk.cabinet.bean.Cabinet;
import com.zk.cabinet.dao.CabinetDao;

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

    public Cabinet queryEqCellID (int cellID) {
        List<Cabinet> list = query(CabinetDao.Properties.CellId.eq(cellID));
        Cabinet cabinet = null;
        if (list != null && list.size() > 0) {
            cabinet = list.get(0);
        }
        return cabinet;
    }

    public List<Cabinet> queryEqCellName (String cellName) {
        return query(CabinetDao.Properties.CellName.eq(cellName));
    }


    /**
     * 新建列表
     * @param cellNames "A,B,D,C"
     */
    public void buildCabinet(String cellNames) {
        String[] cellNameList = cellNames.split(",");
        for (String cellName : cellNameList) {
            if (cellName.equals("A")) {
                buildMain();
            } else {
                buildDeputy(cellName);
            }
        }
    }

    private void buildMain() {
        Cabinet[] cabinets = new Cabinet[11];
        for (int i = 0; i < 11; i++) {
            cabinets[i] = new Cabinet();
            cabinets[i].setId(null);
            cabinets[i].setCellName("A");
            cabinets[i].setSignBroken(0);
            if (i < 1) {
                cabinets[i].setCellId(i + 1);
                cabinets[i].setCellCode(i + 1);
                cabinets[i].setProportion(1);
            } else if (i == 1) {
                cabinets[i].setCellId(0);
                cabinets[i].setCellCode(0);
                cabinets[i].setProportion(2);
            } else {
                cabinets[i].setCellId(i);
                cabinets[i].setCellCode(i);
                cabinets[i].setProportion(1);
            }
        }
        insert(cabinets);
    }

    private void buildDeputy(String cellName) {
        int firstCellId = cellName.toCharArray()[0];
        firstCellId = (firstCellId - 66) * 12 + 10 + 1;
        Cabinet[] cabinets = new Cabinet[12];
        for (int i = 0; i < 12; i++) {
            cabinets[i] = new Cabinet();
            cabinets[i].setId(null);
            cabinets[i].setCellId(firstCellId + i);
            cabinets[i].setCellName(cellName);
            cabinets[i].setCellCode(i + 1);
            cabinets[i].setProportion(1);
            cabinets[i].setSignBroken(0);
        }
        insert(cabinets);
    }
}
