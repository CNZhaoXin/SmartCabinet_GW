package com.zk.cabinet.entity;

import java.util.List;

public class ResultGetToBorrowList {

    /**
     * msg : 操作成功
     * code : 200
     * data : [{"cabinetId":"50c119b2c8204ca48917d927b80545c2","cabinetName":"8号测试柜","archivesId":"abd81a3461bd4086962229255c04db52","borrowerDeptName":"柜子科技","borrowerName":"admin","archivesName":"销售合同-购电卡四川-江苏林洋能源10.4万元半导体","rfid":"214324","rowNo":1,"numNo":16,"archivesStatus":100,"lampList":[16]},{"cabinetId":"50c119b2c8204ca48917d927b80545c2","cabinetName":"8号测试柜","archivesId":"4e666a6b4c554984a81744f7e0a90b41","borrowerDeptName":"柜子科技","borrowerName":"admin","archivesName":"销售合同-面向对象终端芯片-南京能瑞自动化60万元-半导体","rfid":null,"rowNo":1,"numNo":16,"archivesStatus":100,"lampList":[16]}]
     */
    private String msg;
    private int code;
    private List<DataBean> data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * cabinetId : 50c119b2c8204ca48917d927b80545c2
         * cabinetName : 8号测试柜
         * archivesId : abd81a3461bd4086962229255c04db52
         * borrowerDeptName : 柜子科技
         * borrowerName : admin
         * archivesName : 销售合同-购电卡四川-江苏林洋能源10.4万元半导体
         * rfid : 214324
         * rowNo : 1
         * numNo : 16
         * archivesStatus : 100
         * lampList : [16]
         * cabinetType: "1" // 档案组架 档案组柜 档案单柜 123
         */
        private String cabinetId;
        private String cabinetName;
        private String archivesId;
        private String borrowerDeptName;
        private String borrowerName;
        private String archivesName;
        private String rfid;
        private String masterEquipmentId; // 操作屏ID
        private String cabinetType;
        private String cabinetEquipmentId; // 档案柜ID
        private int rowNo;
        private int numNo;
        private int archivesStatus;
        private String masterName; // 操作屏设备名称
        private List<Integer> lampList;
        private boolean isSelect;

        public String getMasterName() {
            return masterName;
        }

        public void setMasterName(String masterName) {
            this.masterName = masterName;
        }

        public String getMasterEquipmentId() {
            return masterEquipmentId;
        }

        public void setMasterEquipmentId(String masterEquipmentId) {
            this.masterEquipmentId = masterEquipmentId;
        }

        public String getCabinetEquipmentId() {
            return cabinetEquipmentId;
        }


        public void setCabinetEquipmentId(String cabinetEquipmentId) {
            this.cabinetEquipmentId = cabinetEquipmentId;
        }

        public String getCabinetType() {
            return cabinetType;
        }

        public void setCabinetType(String cabinetType) {
            this.cabinetType = cabinetType;
        }

        public String getCabinetId() {
            return cabinetId;
        }

        public void setCabinetId(String cabinetId) {
            this.cabinetId = cabinetId;
        }

        public String getCabinetName() {
            return cabinetName;
        }

        public void setCabinetName(String cabinetName) {
            this.cabinetName = cabinetName;
        }

        public String getArchivesId() {
            return archivesId;
        }

        public void setArchivesId(String archivesId) {
            this.archivesId = archivesId;
        }

        public String getBorrowerDeptName() {
            return borrowerDeptName;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        public void setBorrowerDeptName(String borrowerDeptName) {
            this.borrowerDeptName = borrowerDeptName;
        }

        public String getBorrowerName() {
            return borrowerName;
        }

        public void setBorrowerName(String borrowerName) {
            this.borrowerName = borrowerName;
        }

        public String getArchivesName() {
            return archivesName;
        }

        public void setArchivesName(String archivesName) {
            this.archivesName = archivesName;
        }

        public String getRfid() {
            return rfid;
        }

        public void setRfid(String rfid) {
            this.rfid = rfid;
        }

        public int getRowNo() {
            return rowNo;
        }

        public void setRowNo(int rowNo) {
            this.rowNo = rowNo;
        }

        public int getNumNo() {
            return numNo;
        }

        public void setNumNo(int numNo) {
            this.numNo = numNo;
        }

        public int getArchivesStatus() {
            return archivesStatus;
        }

        public void setArchivesStatus(int archivesStatus) {
            this.archivesStatus = archivesStatus;
        }

        public List<Integer> getLampList() {
            return lampList;
        }

        public void setLampList(List<Integer> lampList) {
            this.lampList = lampList;
        }
    }
}
