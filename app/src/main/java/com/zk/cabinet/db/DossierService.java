package com.zk.cabinet.db;

import com.zk.cabinet.bean.Dossier;
import com.zk.cabinet.dao.DossierDao;

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

    public List<Dossier> queryEqCellID (int cellID) {
        return query(DossierDao.Properties.CellId.eq(cellID));
    }

    public Dossier queryEqWebId (Long webId) {
        if (webId == null) return null;

        List<Dossier> list = query(DossierDao.Properties.WebId.eq(webId));
        Dossier dossier = null;
        if (list != null && list.size() > 0) {
            dossier = list.get(0);
        }
        return dossier;
    }

    public long queryCount (String deviceCode, int cellID, String caseState) {
        return queryBuilder().where(
                DossierDao.Properties.DeviceCode.eq(deviceCode),
                DossierDao.Properties.CellId.eq(cellID),
                DossierDao.Properties.CaseState.eq(caseState)).count();
    }


    public List<Dossier> queryCellDossier (String deviceCode, int cellID, String caseState) {
        return query(
                DossierDao.Properties.DeviceCode.eq(deviceCode),
                DossierDao.Properties.CellId.eq(cellID),
                DossierDao.Properties.CaseState.eq(caseState));
    }

    public Dossier queryEqEPC (String epc) {
        List<Dossier> list = query(DossierDao.Properties.Epc.eq(epc));
        Dossier dossier = null;
        if (list != null && list.size() > 0) {
            dossier = list.get(0);
        }
        return dossier;
    }

    public long queryAllCount () {
        return queryBuilder().count();
    }

    public long queryCellDossierCount (String deviceCode, int cellID, String caseState) {
        return queryBuilder().where(
                DossierDao.Properties.DeviceCode.eq(deviceCode),
                DossierDao.Properties.CellId.eq(cellID),
                DossierDao.Properties.CaseState.eq(caseState)).count();
    }

}
