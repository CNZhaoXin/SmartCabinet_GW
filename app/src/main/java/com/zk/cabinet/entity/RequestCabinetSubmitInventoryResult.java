package com.zk.cabinet.entity;

import java.util.List;

public class RequestCabinetSubmitInventoryResult {

    private String planId; // 盘库计划id
    private String houseCode; //库房编号
    private String cabinetEquipmentId; // 档案柜设备id
    private List<RequestRFID> inventoryRfids; // 档案rfid集合

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getHouseCode() {
        return houseCode;
    }

    public void setHouseCode(String houseCode) {
        this.houseCode = houseCode;
    }

    public String getCabinetEquipmentId() {
        return cabinetEquipmentId;
    }

    public void setCabinetEquipmentId(String cabinetEquipmentId) {
        this.cabinetEquipmentId = cabinetEquipmentId;
    }

    public List<RequestRFID> getInventoryRfids() {
        return inventoryRfids;
    }

    public void setInventoryRfids(List<RequestRFID> inventoryRfids) {
        this.inventoryRfids = inventoryRfids;
    }
}
