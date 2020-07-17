package com.zk.cabinet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "DossierOperating")
public class DossierOperating {
    //本地id
    @Property(nameInDb = "ID")
    @Id
    private Long id;

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

    @Property(nameInDb = "OperatingType")
    private int operatingType;

    @Property(nameInDb = "WarranType")
    private int warranType;

    @Property(nameInDb = "CabinetId")
    private String cabinetId;

    @Property(nameInDb = "Floor")
    private int floor;

    @Property(nameInDb = "Light")
    private int light;

    @Property(nameInDb = "Selected")
    private boolean selected;

    @Generated(hash = 1839932352)
    public DossierOperating(Long id, String warrantNum, String rfidNum,
            String warrantName, String warrantNo, String warranCate,
            int operatingType, int warranType, String cabinetId, int floor,
            int light, boolean selected) {
        this.id = id;
        this.warrantNum = warrantNum;
        this.rfidNum = rfidNum;
        this.warrantName = warrantName;
        this.warrantNo = warrantNo;
        this.warranCate = warranCate;
        this.operatingType = operatingType;
        this.warranType = warranType;
        this.cabinetId = cabinetId;
        this.floor = floor;
        this.light = light;
        this.selected = selected;
    }

    @Generated(hash = 1883903289)
    public DossierOperating() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
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

    public int getOperatingType() {
        return this.operatingType;
    }

    public void setOperatingType(int operatingType) {
        this.operatingType = operatingType;
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

    public boolean getSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
