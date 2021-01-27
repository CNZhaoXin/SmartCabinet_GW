package com.zk.cabinet.entity;

import java.util.List;

/**
 * 档案详情实体
 */
public class SearchDossierDetailsDataInventory {

/*
        {                                                                  //每条档案信息
            "cabinetId":"1231231231";                                       //档案柜id
            "cabinetName":"档案柜名称";                                      //档案柜名称
            "archivesId": "123123",                                          // 档案id
                "borrowerDeptName": "XXX部门",                                   //借阅人部门名称
                "borrowerName": "张三",                                          //借阅人姓名
                "archivesName": "一颗积合作档案",                                //档案名称
                "rfid": "123",                                                   //rfid
                "rowNo": 1,                                                      //行号
                "numNo": 2,                                                      //库位序号
                "archivesStatus": 0                                             // 档案状态
                "archivesCode": "                                            // 档案编号
                "archivesNo": ""                                        // 档案号
            "lampList": []   // 灯位列表

        }
*/

    private String cabinetId;
    private String cabinetName;
    private String archivesId;
    private String borrowerDeptName;
    private String cabinetType;
    private String borrowerName;
    private String archivesName;
    private String rfid;
    private String masterName; // 操作屏名称
    private int rowNo;
    private int numNo;
    private int archivesStatus;
    private List<Integer> lampList;
    // "库房编号"
    private String houseCode;
    // "库房房号"
    private String houseNo;
    // "库房名称"
    private String houseName;
    // 档案编号（文号）
    private String archivesCode;
    // 档案号
    private String archivesNo;
    // 标记根据EPC搜索后是否有数据
    private boolean isHasData;

    public boolean isHasData() {
        return isHasData;
    }

    public void setHasData(boolean hasData) {
        isHasData = hasData;
    }

    public String getArchivesCode() {
        return archivesCode;
    }

    public String getArchivesNo() {
        return archivesNo;
    }

    public void setArchivesCode(String archivesCode) {
        this.archivesCode = archivesCode;
    }

    public void setArchivesNo(String archivesNo) {
        this.archivesNo = archivesNo;
    }

    public String getHouseCode() {
        return houseCode;
    }

    public void setHouseCode(String houseCode) {
        this.houseCode = houseCode;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getCabinetType() {
        return cabinetType;
    }

    public void setCabinetType(String cabinetType) {
        this.cabinetType = cabinetType;
    }

    public void setCabinetId(String cabinetId) {
        this.cabinetId = cabinetId;
    }

    public String getCabinetId() {
        return cabinetId;
    }

    public void setCabinetName(String cabinetName) {
        this.cabinetName = cabinetName;
    }

    public String getCabinetName() {
        return cabinetName;
    }

    public void setArchivesId(String archivesId) {
        this.archivesId = archivesId;
    }

    public String getArchivesId() {
        return archivesId;
    }

    public void setBorrowerDeptName(String borrowerDeptName) {
        this.borrowerDeptName = borrowerDeptName;
    }

    public String getBorrowerDeptName() {
        return borrowerDeptName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setArchivesName(String archivesName) {
        this.archivesName = archivesName;
    }

    public String getArchivesName() {
        return archivesName;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRowNo(int rowNo) {
        this.rowNo = rowNo;
    }

    public int getRowNo() {
        return rowNo;
    }

    public void setNumNo(int numNo) {
        this.numNo = numNo;
    }

    public int getNumNo() {
        return numNo;
    }

    public void setArchivesStatus(int archivesStatus) {
        this.archivesStatus = archivesStatus;
    }

    public int getArchivesStatus() {
        return archivesStatus;
    }

    public void setLampList(List<Integer> lampList) {
        this.lampList = lampList;
    }

    public List<Integer> getLampList() {
        return lampList;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }
}

