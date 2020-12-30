package com.zk.cabinet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "InventoryPlanRecord")
public class InventoryPlanRecord {

    @Property(nameInDb = "ID")
    @Id
    private Long id;

    // 档案组柜/档案单柜 盘库计划ID
    @Property(nameInDb = "PlanID")
    @NotNull
    private String planID;

    // 档案室编号
    @Property(nameInDb = "HouseCode")
    @NotNull
    private String houseCode;

    // 需要盘库的柜子
    @Property(nameInDb = "DeviceList")
    @NotNull
    private String deviceList;

    @Generated(hash = 1207777241)
    public InventoryPlanRecord(Long id, @NotNull String planID,
                               @NotNull String houseCode, @NotNull String deviceList) {
        this.id = id;
        this.planID = planID;
        this.houseCode = houseCode;
        this.deviceList = deviceList;
    }

    @Generated(hash = 1646878107)
    public InventoryPlanRecord() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlanID() {
        return this.planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }

    public String getHouseCode() {
        return this.houseCode;
    }

    public void setHouseCode(String houseCode) {
        this.houseCode = houseCode;
    }

    public String getDeviceList() {
        return this.deviceList;
    }

    public void setDeviceList(String deviceList) {
        this.deviceList = deviceList;
    }

}
