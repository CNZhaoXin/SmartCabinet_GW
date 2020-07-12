package com.zk.cabinet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "Warehousing")
public class WarehousingInfo {
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

    @Property(nameInDb = "InStorageType")
    private int inStorageType;

    @Property(nameInDb = "WarranType")
    private int warranType;

    @Generated(hash = 379715415)
    public WarehousingInfo(long id, String warrantNum, String rfidNum,
            String warrantName, String warrantNo, String warranCate,
            int inStorageType, int warranType) {
        this.id = id;
        this.warrantNum = warrantNum;
        this.rfidNum = rfidNum;
        this.warrantName = warrantName;
        this.warrantNo = warrantNo;
        this.warranCate = warranCate;
        this.inStorageType = inStorageType;
        this.warranType = warranType;
    }

    @Generated(hash = 166881216)
    public WarehousingInfo() {
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

    public int getInStorageType() {
        return this.inStorageType;
    }

    public void setInStorageType(int inStorageType) {
        this.inStorageType = inStorageType;
    }

    public int getWarranType() {
        return this.warranType;
    }

    public void setWarranType(int warranType) {
        this.warranType = warranType;
    }
}
