package com.zk.cabinet.entity;

import java.util.List;

public class ResultGetPosInfoByCabinetEquipmentId {

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
        private String houseCode;
        private String houseName;
        private String cabinetId;
        private String attributeNo;
        private String attributeName;
        private String id;
        private String rfid;
        private int rowNo;
        private int numNo;
        private List<Integer> lampList;
        private List<ArchivesListBean> archivesList;

        public String getHouseCode() {
            return houseCode;
        }

        public void setHouseCode(String houseCode) {
            this.houseCode = houseCode;
        }

        public String getHouseName() {
            return houseName;
        }

        public void setHouseName(String houseName) {
            this.houseName = houseName;
        }

        public String getCabinetId() {
            return cabinetId;
        }

        public void setCabinetId(String cabinetId) {
            this.cabinetId = cabinetId;
        }

        public String getAttributeNo() {
            return attributeNo;
        }

        public void setAttributeNo(String attributeNo) {
            this.attributeNo = attributeNo;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public void setAttributeName(String attributeName) {
            this.attributeName = attributeName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public List<Integer> getLampList() {
            return lampList;
        }

        public void setLampList(List<Integer> lampList) {
            this.lampList = lampList;
        }

        public List<ArchivesListBean> getArchivesList() {
            return archivesList;
        }

        public void setArchivesList(List<ArchivesListBean> archivesList) {
            this.archivesList = archivesList;
        }

        public static class ArchivesListBean {
            private String cabinetId;
            private String cabinetName;
            private String archivesId;
            private String borrowerDeptName;
            private String borrowerName;
            private String archivesName;
            private String secrecyLevel;
            private String rfid;
            private int rowNo;
            private int numNo;
            private int archivesStatus;
            private String cabinetType;
            private String masterEquipmentId;
            private String masterName;
            private String cabinetEquipmentId;
            private List<Integer> lampList;
            private boolean isInStockStatus; // 是否是[在库/审批借阅中/待借阅]状态的档案(在库位中的档案)
            private boolean isInStocked;  // 盘点扫描后,该库存档案是否在库位中
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

            public String getArchivesNo() {
                return archivesNo;
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

            public boolean isInStocked() {
                return isInStocked;
            }

            public void setInStocked(boolean inStocked) {
                isInStocked = inStocked;
            }

            public boolean isInStockStatus() {
                return isInStockStatus;
            }

            public void setInStockStatus(boolean inStockStatus) {
                isInStockStatus = inStockStatus;
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

            public String getArchivesCode() {
                return archivesCode;
            }

            public void setArchivesCode(String archivesCode) {
                this.archivesCode = archivesCode;
            }

            public String getSecrecyLevel() {
                return secrecyLevel;
            }

            public void setSecrecyLevel(String secrecyLevel) {
                this.secrecyLevel = secrecyLevel;
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

            public String getCabinetType() {
                return cabinetType;
            }

            public void setCabinetType(String cabinetType) {
                this.cabinetType = cabinetType;
            }

            public String getMasterEquipmentId() {
                return masterEquipmentId;
            }

            public void setMasterEquipmentId(String masterEquipmentId) {
                this.masterEquipmentId = masterEquipmentId;
            }

            public String getMasterName() {
                return masterName;
            }

            public void setMasterName(String masterName) {
                this.masterName = masterName;
            }

            public String getCabinetEquipmentId() {
                return cabinetEquipmentId;
            }

            public void setCabinetEquipmentId(String cabinetEquipmentId) {
                this.cabinetEquipmentId = cabinetEquipmentId;
            }

            public List<Integer> getLampList() {
                return lampList;
            }

            public void setLampList(List<Integer> lampList) {
                this.lampList = lampList;
            }
        }
    }
}
