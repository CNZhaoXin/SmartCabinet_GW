package com.zk.cabinet.bean;

public class DossierEntity {

    /**
     * id : 1
     * warrantNum : 123456
     * rfidNum : 10000002
     * warrantName : 权证名称-测试01
     * warrantNo : 234567
     * inputId : 1
     * inputName : x
     * inOrg : x
     * warranCate : 1
     * inStorageType : 1
     * warranType : 2
     */
    private String id;
    private String warrantNum;
    private String rfidNum;
    private String warrantName;
    private String warrantNo;
    private String inputId;
    private String inputName;
    private String inOrg;
    private String warranCate;
    private String inStorageType;
    private String warranType;

    private String cabiCode;
    private int floor;
    private int light;
    private boolean selected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWarrantNum() {
        return warrantNum;
    }

    public void setWarrantNum(String warrantNum) {
        this.warrantNum = warrantNum;
    }

    public String getRfidNum() {
        return rfidNum;
    }

    public void setRfidNum(String rfidNum) {
        this.rfidNum = rfidNum;
    }

    public String getWarrantName() {
        return warrantName;
    }

    public void setWarrantName(String warrantName) {
        this.warrantName = warrantName;
    }

    public String getWarrantNo() {
        return warrantNo;
    }

    public void setWarrantNo(String warrantNo) {
        this.warrantNo = warrantNo;
    }

    public String getInputId() {
        return inputId;
    }

    public void setInputId(String inputId) {
        this.inputId = inputId;
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public String getInOrg() {
        return inOrg;
    }

    public void setInOrg(String inOrg) {
        this.inOrg = inOrg;
    }

    public String getWarranCate() {
        return warranCate;
    }

    public void setWarranCate(String warranCate) {
        this.warranCate = warranCate;
    }

    public String getInStorageType() {
        return inStorageType;
    }

    public void setInStorageType(String inStorageType) {
        this.inStorageType = inStorageType;
    }

    public String getWarranType() {
        return warranType;
    }

    public void setWarranType(String warranType) {
        this.warranType = warranType;
    }

    public String getCabiCode() {
        return cabiCode;
    }

    public void setCabiCode(String cabiCode) {
        this.cabiCode = cabiCode;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
