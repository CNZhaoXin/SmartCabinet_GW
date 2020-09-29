package com.zk.cabinet.db;

import com.zk.cabinet.bean.Device;
import com.zk.cabinet.bean.DossierOperating;
import com.zk.cabinet.dao.DossierOperatingDao;

import java.util.ArrayList;
import java.util.List;

public class DossierOperatingService extends BaseService<DossierOperating, Long> {

    private static volatile DossierOperatingService instance;//单例

    public static DossierOperatingService getInstance() {
        if (instance == null) {
            synchronized (DossierOperatingService.class) {//保证异步处理安全操作
                if (instance == null) {
                    instance = new DossierOperatingService();
                }
            }
        }
        return instance;
    }


    public List<DossierOperating> getNullList() {
        return new ArrayList<>();
    }

    public List<DossierOperating> queryListByEPC(String epc) {

        return query(DossierOperatingDao.Properties.RfidNum.eq(epc));
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

    public List<DossierOperating> queryByLoginCode(String loginCode) {
        return query(DossierOperatingDao.Properties.InputId.eq(loginCode));
    }

    public List<DossierOperating> queryByWarrantNum(String warrantNum) {
        return query(DossierOperatingDao.Properties.WarrantNum.eq(warrantNum));
    }

    /**
     * 1号柜
     * EPC:12340007 位置:1234567801-2-1
     * EPC:10000002 位置:1234567801-2-3
     * EPC:10000003 位置:1234567801-2-5
     * EPC:20000002 位置:1234567801-2-7
     * EPC:50000008 位置:1234567801-2-9
     * <p>
     * 3号柜
     * EPC:50000009 位置:1234567803-2-3
     * EPC:60000003 位置:1234567803-2-5
     * EPC:60000012 位置:1234567803-2-7
     */
    public void mainBuild() {
        // todo 人造30本档案
        String deviceName = "";
        List<Device> devices = DeviceService.getInstance().loadAll();
        if (devices.size() > 0) {
            deviceName = devices.get(0).getDeviceName();
        }

        // 40本档案,一层8本档案
        // 第一层
        // 档案 1-6 男
        DossierOperating d1_6 = new DossierOperating();
        d1_6.setFloor(1);
        d1_6.setLight(6);
        d1_6.setRfidNum("50000001");
        d1_6.setQuarNo("男");
        d1_6.setWarrantName("man1");
        d1_6.setWarrantNum("a"); // 查询档案用的关键字
        d1_6.setInputName("艾冬冬");
        d1_6.setSelected(false);
        d1_6.setOperatingType(1); // 1 入库状态 2 出库状态
        d1_6.setInputId(""); // loginCode
        d1_6.setWarrantNo("1991.1.1");
        d1_6.setCabcode(deviceName);
        insert(d1_6);

        // 档案 1-8 女
        DossierOperating d1_8 = new DossierOperating();
        d1_8.setFloor(1);
        d1_8.setLight(8);
        d1_8.setRfidNum("50000002");
        d1_8.setQuarNo("女");
        d1_8.setWarrantName("faleman1");
        d1_8.setWarrantNum("b"); // 查询档案用的关键字
        d1_8.setInputName("巴贝尔");
        d1_8.setSelected(false);
        d1_8.setOperatingType(1); // 1 入库状态 2 出库状态
        d1_8.setInputId(""); // loginCode
        d1_8.setWarrantNo("1992.2.2");
        d1_8.setCabcode(deviceName);
        insert(d1_8);

        // 档案 1-10 男
        DossierOperating d1_10 = new DossierOperating();
        d1_10.setFloor(1);
        d1_10.setLight(10);
        d1_10.setRfidNum("50000003");
        d1_10.setQuarNo("男");
        d1_10.setWarrantName("man2");
        d1_10.setWarrantNum("c"); // 查询档案用的关键字
        d1_10.setInputName("陈晨");
        d1_10.setSelected(false);
        d1_10.setOperatingType(1); // 1 入库状态 2 出库状态
        d1_10.setInputId(""); // loginCode
        d1_10.setWarrantNo("1993.3.3");
        d1_10.setCabcode(deviceName);
        insert(d1_10);

        // 档案 1-12 女
        DossierOperating d1_12 = new DossierOperating();
        d1_12.setFloor(1);
        d1_12.setLight(12);
        d1_12.setRfidNum("50000004");
        d1_12.setQuarNo("女");
        d1_12.setWarrantName("faleman2");
        d1_12.setWarrantNum("d"); // 查询档案用的关键字
        d1_12.setInputName("戴思");
        d1_12.setSelected(false);
        d1_12.setOperatingType(1); // 1 入库状态 2 出库状态
        d1_12.setInputId(""); // loginCode
        d1_12.setWarrantNo("1994.4.4");
        d1_12.setCabcode(deviceName);
        insert(d1_12);

        // 档案 1-14 男
        DossierOperating d1_14 = new DossierOperating();
        d1_14.setFloor(1);
        d1_14.setLight(14);
        d1_14.setRfidNum("50000005");
        d1_14.setQuarNo("男");
        d1_14.setWarrantName("man3");
        d1_14.setWarrantNum("e"); // 查询档案用的关键字
        d1_14.setInputName("鄂思蜀");
        d1_14.setSelected(false);
        d1_14.setOperatingType(1); // 1 入库状态 2 出库状态
        d1_14.setInputId(""); // loginCode
        d1_14.setWarrantNo("1995.5.5");
        d1_14.setCabcode(deviceName);
        insert(d1_14);

        // 档案 1-16 女
        DossierOperating d1_16 = new DossierOperating();
        d1_16.setFloor(1);
        d1_16.setLight(16);
        d1_16.setRfidNum("50000006");
        d1_16.setQuarNo("女");
        d1_16.setWarrantName("faleman3");
        d1_16.setWarrantNum("f"); // 查询档案用的关键字
        d1_16.setInputName("冯思思");
        d1_16.setSelected(false);
        d1_16.setOperatingType(1); // 1 入库状态 2 出库状态
        d1_16.setInputId(""); // loginCode
        d1_16.setWarrantNo("1996.6.6");
        d1_16.setCabcode(deviceName);
        insert(d1_16);

        // 档案 1-18 男
        DossierOperating d1_18 = new DossierOperating();
        d1_18.setFloor(1);
        d1_18.setLight(18);
        d1_18.setRfidNum("50000007");
        d1_18.setQuarNo("男");
        d1_18.setWarrantName("man4");
        d1_18.setWarrantNum("g"); // 查询档案用的关键字
        d1_18.setInputName("高天啸");
        d1_18.setSelected(false);
        d1_18.setOperatingType(1); // 1 入库状态 2 出库状态
        d1_18.setInputId(""); // loginCode
        d1_18.setWarrantNo("1997.7.7");
        d1_18.setCabcode(deviceName);
        insert(d1_18);

        // 档案 1-20 女
        DossierOperating d1_20 = new DossierOperating();
        d1_20.setFloor(1);
        d1_20.setLight(20);
        d1_20.setRfidNum("50000008");
        d1_20.setQuarNo("女");
        d1_20.setWarrantName("faleman4");
        d1_20.setWarrantNum("h"); // 查询档案用的关键字
        d1_20.setInputName("花明明");
        d1_20.setSelected(false);
        d1_20.setOperatingType(1); // 1 入库状态 2 出库状态
        d1_20.setInputId(""); // loginCode
        d1_20.setWarrantNo("1998.8.8");
        d1_20.setCabcode(deviceName);
        insert(d1_20);

        // todo 第二层
        // 档案 2-6 男
        DossierOperating d2_6 = new DossierOperating();
        d2_6.setFloor(2);
        d2_6.setLight(6);
        d2_6.setRfidNum("50000009");
        d2_6.setQuarNo("男");
        d2_6.setWarrantName("man1");
        d2_6.setWarrantNum("i"); // 查询档案用的关键字
        d2_6.setInputName("艾斯");
        d2_6.setSelected(false);
        d2_6.setOperatingType(1); // 1 入库状态 2 出库状态
        d2_6.setInputId(""); // loginCode
        d2_6.setWarrantNo("1991.1.1");
        d2_6.setCabcode(deviceName);
        insert(d2_6);

        // 档案 2-8 女
        DossierOperating d2_8 = new DossierOperating();
        d2_8.setFloor(2);
        d2_8.setLight(8);
        d2_8.setRfidNum("50000010");
        d2_8.setQuarNo("女");
        d2_8.setWarrantName("faleman1");
        d2_8.setWarrantNum("j"); // 查询档案用的关键字
        d2_8.setInputName("季程琳");
        d2_8.setSelected(false);
        d2_8.setOperatingType(1); // 1 入库状态 2 出库状态
        d2_8.setInputId(""); // loginCode
        d2_8.setWarrantNo("1992.2.2");
        d2_8.setCabcode(deviceName);
        insert(d2_8);

        // 档案 2-10 男
        DossierOperating d2_10 = new DossierOperating();
        d2_10.setFloor(2);
        d2_10.setLight(10);
        d2_10.setRfidNum("50000011");
        d2_10.setQuarNo("男");
        d2_10.setWarrantName("man2");
        d2_10.setWarrantNum("k"); // 查询档案用的关键字
        d2_10.setInputName("科东");
        d2_10.setSelected(false);
        d2_10.setOperatingType(1); // 1 入库状态 2 出库状态
        d2_10.setInputId(""); // loginCode
        d2_10.setWarrantNo("1993.3.3");
        d2_10.setCabcode(deviceName);
        insert(d2_10);

        // 档案 2-12 女
        DossierOperating d2_12 = new DossierOperating();
        d2_12.setFloor(2);
        d2_12.setLight(12);
        d2_12.setRfidNum("50000012");
        d2_12.setQuarNo("女");
        d2_12.setWarrantName("faleman2");
        d2_12.setWarrantNum("l"); // 查询档案用的关键字
        d2_12.setInputName("林萱萱");
        d2_12.setSelected(false);
        d2_12.setOperatingType(1); // 1 入库状态 2 出库状态
        d2_12.setInputId(""); // loginCode
        d2_12.setWarrantNo("1994.4.4");
        d2_12.setCabcode(deviceName);
        insert(d2_12);

        // 档案 2-14 男
        DossierOperating d2_14 = new DossierOperating();
        d2_14.setFloor(2);
        d2_14.setLight(14);
        d2_14.setRfidNum("50000013");
        d2_14.setQuarNo("男");
        d2_14.setWarrantName("man3");
        d2_14.setWarrantNum("m"); // 查询档案用的关键字
        d2_14.setInputName("马浩");
        d2_14.setSelected(false);
        d2_14.setOperatingType(1); // 1 入库状态 2 出库状态
        d2_14.setInputId(""); // loginCode
        d2_14.setWarrantNo("1995.5.5");
        d2_14.setCabcode(deviceName);
        insert(d2_14);

        // 档案 2-16 女
        DossierOperating d2_16 = new DossierOperating();
        d2_16.setFloor(2);
        d2_16.setLight(16);
        d2_16.setRfidNum("50000014");
        d2_16.setQuarNo("女");
        d2_16.setWarrantName("faleman3");
        d2_16.setWarrantNum("n"); // 查询档案用的关键字
        d2_16.setInputName("宁珊珊");
        d2_16.setSelected(false);
        d2_16.setOperatingType(1); // 1 入库状态 2 出库状态
        d2_16.setInputId(""); // loginCode
        d2_16.setWarrantNo("1996.6.6");
        d2_16.setCabcode(deviceName);
        insert(d2_16);

        // 档案 2-18 男
        DossierOperating d2_18 = new DossierOperating();
        d2_18.setFloor(2);
        d2_18.setLight(18);
        d2_18.setRfidNum("50000015");
        d2_18.setQuarNo("男");
        d2_18.setWarrantName("man4");
        d2_18.setWarrantNum("o"); // 查询档案用的关键字
        d2_18.setInputName("欧志豪");
        d2_18.setSelected(false);
        d2_18.setOperatingType(1); // 1 入库状态 2 出库状态
        d2_18.setInputId(""); // loginCode
        d2_18.setWarrantNo("1997.7.7");
        d2_18.setCabcode(deviceName);
        insert(d2_18);

        // 档案 2-20 女
        DossierOperating d2_20 = new DossierOperating();
        d2_20.setFloor(2);
        d2_20.setLight(20);
        d2_20.setRfidNum("50000016");
        d2_20.setQuarNo("女");
        d2_20.setWarrantName("faleman4");
        d2_20.setWarrantNum("p"); // 查询档案用的关键字
        d2_20.setInputName("彭璇");
        d2_20.setSelected(false);
        d2_20.setOperatingType(1); // 1 入库状态 2 出库状态
        d2_20.setInputId(""); // loginCode
        d2_20.setWarrantNo("1998.8.8");
        d2_20.setCabcode(deviceName);
        insert(d2_20);


        // todo 第三层
        // 档案 3-6 男
        DossierOperating d3_6 = new DossierOperating();
        d3_6.setFloor(3);
        d3_6.setLight(6);
        d3_6.setRfidNum("50000017");
        d3_6.setQuarNo("男");
        d3_6.setWarrantName("man1");
        d3_6.setWarrantNum("q"); // 查询档案用的关键字
        d3_6.setInputName("琼紫儿");
        d3_6.setSelected(false);
        d3_6.setOperatingType(1); // 1 入库状态 2 出库状态
        d3_6.setInputId(""); // loginCode
        d3_6.setWarrantNo("1991.1.1");
        d3_6.setCabcode(deviceName);
        insert(d3_6);

        // 档案 3-8 女
        DossierOperating d3_8 = new DossierOperating();
        d3_8.setFloor(3);
        d3_8.setLight(8);
        d3_8.setRfidNum("50000018");
        d3_8.setQuarNo("女");
        d3_8.setWarrantName("faleman1");
        d3_8.setWarrantNum("r"); // 查询档案用的关键字
        d3_8.setInputName("任苒");
        d3_8.setSelected(false);
        d3_8.setOperatingType(1); // 1 入库状态 2 出库状态
        d3_8.setInputId(""); // loginCode
        d3_8.setWarrantNo("1992.2.2");
        d3_8.setCabcode(deviceName);
        insert(d3_8);

        // 档案 3-10 男
        DossierOperating d3_10 = new DossierOperating();
        d3_10.setFloor(3);
        d3_10.setLight(10);
        d3_10.setRfidNum("50000019");
        d3_10.setQuarNo("男");
        d3_10.setWarrantName("man2");
        d3_10.setWarrantNum("s"); // 查询档案用的关键字
        d3_10.setInputName("商寅");
        d3_10.setSelected(false);
        d3_10.setOperatingType(1); // 1 入库状态 2 出库状态
        d3_10.setInputId(""); // loginCode
        d3_10.setWarrantNo("1993.3.3");
        d3_10.setCabcode(deviceName);
        insert(d3_10);

        // 档案 3-12 女
        DossierOperating d3_12 = new DossierOperating();
        d3_12.setFloor(3);
        d3_12.setLight(12);
        d3_12.setRfidNum("50000020");
        d3_12.setQuarNo("女");
        d3_12.setWarrantName("faleman2");
        d3_12.setWarrantNum("t"); // 查询档案用的关键字
        d3_12.setInputName("田甜");
        d3_12.setSelected(false);
        d3_12.setOperatingType(1); // 1 入库状态 2 出库状态
        d3_12.setInputId(""); // loginCode
        d3_12.setWarrantNo("1994.4.4");
        d3_12.setCabcode(deviceName);
        insert(d3_12);

        // 档案 3-14 男
        DossierOperating d3_14 = new DossierOperating();
        d3_14.setFloor(3);
        d3_14.setLight(14);
        d3_14.setRfidNum("50000021");
        d3_14.setQuarNo("男");
        d3_14.setWarrantName("man3");
        d3_14.setWarrantNum("u"); // 查询档案用的关键字
        d3_14.setInputName("幽岚风");
        d3_14.setSelected(false);
        d3_14.setOperatingType(1); // 1 入库状态 2 出库状态
        d3_14.setInputId(""); // loginCode
        d3_14.setWarrantNo("1995.5.5");
        d3_14.setCabcode(deviceName);
        insert(d3_14);

        // 档案 3-16 女
        DossierOperating d3_16 = new DossierOperating();
        d3_16.setFloor(3);
        d3_16.setLight(16);
        d3_16.setRfidNum("50000022");
        d3_16.setQuarNo("女");
        d3_16.setWarrantName("faleman3");
        d3_16.setWarrantNum("v"); // 查询档案用的关键字
        d3_16.setInputName("卫政");
        d3_16.setSelected(false);
        d3_16.setOperatingType(1); // 1 入库状态 2 出库状态
        d3_16.setInputId(""); // loginCode
        d3_16.setWarrantNo("1996.6.6");
        d3_16.setCabcode(deviceName);
        insert(d3_16);

        // 档案 3-18 男
        DossierOperating d3_18 = new DossierOperating();
        d3_18.setFloor(3);
        d3_18.setLight(18);
        d3_18.setRfidNum("50000023");
        d3_18.setQuarNo("男");
        d3_18.setWarrantName("man4");
        d3_18.setWarrantNum("w"); // 查询档案用的关键字
        d3_18.setInputName("王亮");
        d3_18.setSelected(false);
        d3_18.setOperatingType(1); // 1 入库状态 2 出库状态
        d3_18.setInputId(""); // loginCode
        d3_18.setWarrantNo("1997.7.7");
        d3_18.setCabcode(deviceName);
        insert(d3_18);

        // 档案 3-20 女
        DossierOperating d3_20 = new DossierOperating();
        d3_20.setFloor(3);
        d3_20.setLight(20);
        d3_20.setRfidNum("50000024");
        d3_20.setQuarNo("女");
        d3_20.setWarrantName("faleman4");
        d3_20.setWarrantNum("x"); // 查询档案用的关键字
        d3_20.setInputName("萧梦然");
        d3_20.setSelected(false);
        d3_20.setOperatingType(1); // 1 入库状态 2 出库状态
        d3_20.setInputId(""); // loginCode
        d3_20.setWarrantNo("1998.8.8");
        d3_20.setCabcode(deviceName);
        insert(d3_20);

        // todo 第四层
        // 档案 4-6 男
        DossierOperating d4_6 = new DossierOperating();
        d4_6.setFloor(4);
        d4_6.setLight(6);
        d4_6.setRfidNum("50000025");
        d4_6.setQuarNo("男");
        d4_6.setWarrantName("man1");
        d4_6.setWarrantNum("y"); // 查询档案用的关键字
        d4_6.setInputName("余元亮");
        d4_6.setSelected(false);
        d4_6.setOperatingType(1); // 1 入库状态 2 出库状态
        d4_6.setInputId(""); // loginCode
        d4_6.setWarrantNo("1991.1.1");
        d4_6.setCabcode(deviceName);
        insert(d4_6);

        // 档案 4-8 女
        DossierOperating d4_8 = new DossierOperating();
        d4_8.setFloor(4);
        d4_8.setLight(8);
        d4_8.setRfidNum("50000026");
        d4_8.setQuarNo("女");
        d4_8.setWarrantName("faleman1");
        d4_8.setWarrantNum("z"); // 查询档案用的关键字
        d4_8.setInputName("朱薇");
        d4_8.setSelected(false);
        d4_8.setOperatingType(1); // 1 入库状态 2 出库状态
        d4_8.setInputId(""); // loginCode
        d4_8.setWarrantNo("1992.2.2");
        d4_8.setCabcode(deviceName);
        insert(d4_8);

        // 档案 4-10 男
        DossierOperating d4_10 = new DossierOperating();
        d4_10.setFloor(4);
        d4_10.setLight(10);
        d4_10.setRfidNum("50000027");
        d4_10.setQuarNo("男");
        d4_10.setWarrantName("man2");
        d4_10.setWarrantNum("fxy"); // 查询档案用的关键字
        d4_10.setInputName("方心远");
        d4_10.setSelected(false);
        d4_10.setOperatingType(1); // 1 入库状态 2 出库状态
        d4_10.setInputId(""); // loginCode
        d4_10.setWarrantNo("1993.3.3");
        d4_10.setCabcode(deviceName);
        insert(d4_10);

        // 档案 4-12 女
        DossierOperating d4_12 = new DossierOperating();
        d4_12.setFloor(4);
        d4_12.setLight(12);
        d4_12.setRfidNum("50000028");
        d4_12.setQuarNo("女");
        d4_12.setWarrantName("faleman2");
        d4_12.setWarrantNum("xrm"); // 查询档案用的关键字
        d4_12.setInputName("熊芮美");
        d4_12.setSelected(false);
        d4_12.setOperatingType(1); // 1 入库状态 2 出库状态
        d4_12.setInputId(""); // loginCode
        d4_12.setWarrantNo("1994.4.4");
        d4_12.setCabcode(deviceName);
        insert(d4_12);

        // 档案 4-14 男
        DossierOperating d4_14 = new DossierOperating();
        d4_14.setFloor(4);
        d4_14.setLight(14);
        d4_14.setRfidNum("50000029");
        d4_14.setQuarNo("男");
        d4_14.setWarrantName("man3");
        d4_14.setWarrantNum("xk"); // 查询档案用的关键字
        d4_14.setInputName("许鲲");
        d4_14.setSelected(false);
        d4_14.setOperatingType(1); // 1 入库状态 2 出库状态
        d4_14.setInputId(""); // loginCode
        d4_14.setWarrantNo("1995.5.5");
        d4_14.setCabcode(deviceName);
        insert(d4_14);

        // 档案 4-16 女
        DossierOperating d4_16 = new DossierOperating();
        d4_16.setFloor(4);
        d4_16.setLight(16);
        d4_16.setRfidNum("50000030");
        d4_16.setQuarNo("女");
        d4_16.setWarrantName("faleman3");
        d4_16.setWarrantNum("lxt"); // 查询档案用的关键字
        d4_16.setInputName("罗欣彤");
        d4_16.setSelected(false);
        d4_16.setOperatingType(1); // 1 入库状态 2 出库状态
        d4_16.setInputId(""); // loginCode
        d4_16.setWarrantNo("1996.6.6");
        d4_16.setCabcode(deviceName);
        insert(d4_16);

        // 档案 4-18 男
        DossierOperating d4_18 = new DossierOperating();
        d4_18.setFloor(4);
        d4_18.setLight(18);
        d4_18.setRfidNum("50000031");
        d4_18.setQuarNo("男");
        d4_18.setWarrantName("man4");
        d4_18.setWarrantNum("hrz"); // 查询档案用的关键字
        d4_18.setInputName("何锐阵");
        d4_18.setSelected(false);
        d4_18.setOperatingType(1); // 1 入库状态 2 出库状态
        d4_18.setInputId(""); // loginCode
        d4_18.setWarrantNo("1997.7.7");
        d4_18.setCabcode(deviceName);
        insert(d4_18);

        // 档案 4-20 女
        DossierOperating d4_20 = new DossierOperating();
        d4_20.setFloor(4);
        d4_20.setLight(20);
        d4_20.setRfidNum("50000032");
        d4_20.setQuarNo("女");
        d4_20.setWarrantName("faleman4");
        d4_20.setWarrantNum("sh"); // 查询档案用的关键字
        d4_20.setInputName("苏画");
        d4_20.setSelected(false);
        d4_20.setOperatingType(1); // 1 入库状态 2 出库状态
        d4_20.setInputId(""); // loginCode
        d4_20.setWarrantNo("1998.8.8");
        d4_20.setCabcode(deviceName);
        insert(d4_20);

        // todo 第五层
        // 档案 5-6 男
        DossierOperating d5_6 = new DossierOperating();
        d5_6.setFloor(5);
        d5_6.setLight(6);
        d5_6.setRfidNum("50000033");
        d5_6.setQuarNo("男");
        d5_6.setWarrantName("man1");
        d5_6.setWarrantNum("hy"); // 查询档案用的关键字
        d5_6.setInputName("黄炎");
        d5_6.setSelected(false);
        d5_6.setOperatingType(1); // 1 入库状态 2 出库状态
        d5_6.setInputId(""); // loginCode
        d5_6.setWarrantNo("1991.1.1");
        d5_6.setCabcode(deviceName);
        insert(d5_6);

        // 档案 5-8 女
        DossierOperating d5_8 = new DossierOperating();
        d5_8.setFloor(5);
        d5_8.setLight(8);
        d5_8.setRfidNum("50000034");
        d5_8.setQuarNo("女");
        d5_8.setWarrantName("faleman1");
        d5_8.setWarrantNum("cnz"); // 查询档案用的关键字
        d5_8.setInputName("程凝竹");
        d5_8.setSelected(false);
        d5_8.setOperatingType(1); // 1 入库状态 2 出库状态
        d5_8.setInputId(""); // loginCode
        d5_8.setWarrantNo("1992.2.2");
        d5_8.setCabcode(deviceName);
        insert(d5_8);

        // 档案 5-10 男
        DossierOperating d5_10 = new DossierOperating();
        d5_10.setFloor(5);
        d5_10.setLight(10);
        d5_10.setRfidNum("50000035");
        d5_10.setQuarNo("男");
        d5_10.setWarrantName("man2");
        d5_10.setWarrantNum("pxl"); // 查询档案用的关键字
        d5_10.setInputName("潘星阑");
        d5_10.setSelected(false);
        d5_10.setOperatingType(1); // 1 入库状态 2 出库状态
        d5_10.setInputId(""); // loginCode
        d5_10.setWarrantNo("1993.3.3");
        d5_10.setCabcode(deviceName);
        insert(d5_10);

        // 档案 5-12 女
        DossierOperating d5_12 = new DossierOperating();
        d5_12.setFloor(5);
        d5_12.setLight(12);
        d5_12.setRfidNum("50000036");
        d5_12.setQuarNo("女");
        d5_12.setWarrantName("faleman2");
        d5_12.setWarrantNum("ly"); // 查询档案用的关键字
        d5_12.setInputName("龙媱");
        d5_12.setSelected(false);
        d5_12.setOperatingType(1); // 1 入库状态 2 出库状态
        d5_12.setInputId(""); // loginCode
        d5_12.setWarrantNo("1994.4.4");
        d5_12.setCabcode(deviceName);
        insert(d5_12);

        // 档案 5-14 男
        DossierOperating d5_14 = new DossierOperating();
        d5_14.setFloor(5);
        d5_14.setLight(14);
        d5_14.setRfidNum("50000037");
        d5_14.setQuarNo("男");
        d5_14.setWarrantName("man3");
        d5_14.setWarrantNum("chz"); // 查询档案用的关键字
        d5_14.setInputName("崔鸿哲");
        d5_14.setSelected(false);
        d5_14.setOperatingType(1); // 1 入库状态 2 出库状态
        d5_14.setInputId(""); // loginCode
        d5_14.setWarrantNo("1995.5.5");
        d5_14.setCabcode(deviceName);
        insert(d5_14);

        // 档案 5-16 女
        DossierOperating d5_16 = new DossierOperating();
        d5_16.setFloor(5);
        d5_16.setLight(16);
        d5_16.setRfidNum("50000038");
        d5_16.setQuarNo("女");
        d5_16.setWarrantName("faleman3");
        d5_16.setWarrantNum("dmy"); // 查询档案用的关键字
        d5_16.setInputName("丁妙颜");
        d5_16.setSelected(false);
        d5_16.setOperatingType(1); // 1 入库状态 2 出库状态
        d5_16.setInputId(""); // loginCode
        d5_16.setWarrantNo("1996.6.6");
        d5_16.setCabcode(deviceName);
        insert(d5_16);

        // 档案 5-18 男
        DossierOperating d5_18 = new DossierOperating();
        d5_18.setFloor(5);
        d5_18.setLight(18);
        d5_18.setRfidNum("50000039");
        d5_18.setQuarNo("男");
        d5_18.setWarrantName("man4");
        d5_18.setWarrantNum("fw"); // 查询档案用的关键字
        d5_18.setInputName("冯兴");
        d5_18.setSelected(false);
        d5_18.setOperatingType(1); // 1 入库状态 2 出库状态
        d5_18.setInputId(""); // loginCode
        d5_18.setWarrantNo("1997.7.7");
        d5_18.setCabcode(deviceName);
        insert(d5_18);

        // 档案 5-20 女
        DossierOperating d5_20 = new DossierOperating();
        d5_20.setFloor(5);
        d5_20.setLight(20);
        d5_20.setRfidNum("50000040");
        d5_20.setQuarNo("女");
        d5_20.setWarrantName("faleman4");
        d5_20.setWarrantNum("ly"); // 查询档案用的关键字
        d5_20.setInputName("罗芸");
        d5_20.setSelected(false);
        d5_20.setOperatingType(1); // 1 入库状态 2 出库状态
        d5_20.setInputId(""); // loginCode
        d5_20.setWarrantNo("1998.8.8");
        d5_20.setCabcode(deviceName);
        insert(d5_20);
    }
}
