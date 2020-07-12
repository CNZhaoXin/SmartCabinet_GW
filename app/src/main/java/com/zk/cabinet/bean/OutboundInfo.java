package com.zk.cabinet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "Outbound")
public class OutboundInfo {
    @Property(nameInDb = "ID")
    @Id
    private long id;

    @Property(nameInDb = "WarrantNum")
    private String warrantNum;

    @Property(nameInDb = "RfidNum")
    private String rfidNum;

    @Property(nameInDb = "WarrantName")
    private String warrantName;

    @Property(nameInDb = "WarrantNo")
    private String warrantNo;

    @Property(nameInDb = "WarranCate")
    private String warranCate;

    @Property(nameInDb = "OutStorageType")
    private int outStorageType;

    @Property(nameInDb = "WarranType")
    private int warranType;

    @Property(nameInDb = "CabinetId")
    private String cabinetId;

    @Property(nameInDb = "Floor")
    private int floor;

    @Property(nameInDb = "Light")
    private int light;

    @Generated(hash = 1292270642)
    public OutboundInfo(long id, String warrantNum, String rfidNum,
            String warrantName, String warrantNo, String warranCate,
            int outStorageType, int warranType, String cabinetId, int floor,
            int light) {
        this.id = id;
        this.warrantNum = warrantNum;
        this.rfidNum = rfidNum;
        this.warrantName = warrantName;
        this.warrantNo = warrantNo;
        this.warranCate = warranCate;
        this.outStorageType = outStorageType;
        this.warranType = warranType;
        this.cabinetId = cabinetId;
        this.floor = floor;
        this.light = light;
    }

    @Generated(hash = 1583928137)
    public OutboundInfo() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWarrantNum() {
        return this.warrantNum;
    }

    public void setWarrantNum(String warrantNum) {
        this.warrantNum = warrantNum;
    }

    public String getRfidNum() {
        return this.rfidNum;
    }

    public void setRfidNum(String rfidNum) {
        this.rfidNum = rfidNum;
    }

    public String getWarrantName() {
        return this.warrantName;
    }

    public void setWarrantName(String warrantName) {
        this.warrantName = warrantName;
    }

    public String getWarrantNo() {
        return this.warrantNo;
    }

    public void setWarrantNo(String warrantNo) {
        this.warrantNo = warrantNo;
    }

    public String getWarranCate() {
        return this.warranCate;
    }

    public void setWarranCate(String warranCate) {
        this.warranCate = warranCate;
    }

    public int getOutStorageType() {
        return this.outStorageType;
    }

    public void setOutStorageType(int outStorageType) {
        this.outStorageType = outStorageType;
    }

    public int getWarranType() {
        return this.warranType;
    }

    public void setWarranType(int warranType) {
        this.warranType = warranType;
    }

    public String getCabinetId() {
        return this.cabinetId;
    }

    public void setCabinetId(String cabinetId) {
        this.cabinetId = cabinetId;
    }

    public int getFloor() {
        return this.floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getLight() {
        return this.light;
    }

    public void setLight(int light) {
        this.light = light;
    }
}
