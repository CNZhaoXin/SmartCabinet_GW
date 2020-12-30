package com.zk.cabinet.entity;

import java.util.List;

/**
 * MQTT 异常出库档案信息 消息
 */
public class ResultMQTTMessageErrorOutStorage {

//    {
//        "msgType": "6",
//            "data": {
//                "doorEquipmentId": "203338714",
//                "archivesList": [{
//                    "houseCode": "3",
//                    "houseNo": "3",
//                    "houseName": "综合档案室",
//                    "cabinetId": "36c9f35668c342e08ebd846ed2450e37",
//                    "cabinetName": "4排1列A面",
//                    "archivesId": "6fe0f9a273574fd68a70bdd598378ed0",
//                    "borrowerDeptName": null,
//                    "borrowerName": null,
//                    "archivesName": "48-北京智芯微电子科技有限公司",
//                    "archivesCode": null,
//                    "archivesNo": "48",
//                    "secrecyLevel": "50",
//                    "rfid": "200401000000000000014448",
//                    "rowNo": 2,
//                    "numNo": 3,
//                    "archivesStatus": 9000,
//                    "lampList": [3],
//                    "cabinetType": "1",
//                    "masterEquipmentId": "3-4",
//                    "masterName": "第4排",
//                    "cabinetEquipmentId": null
//        }]
//    }
//    }

    private String msgType;
    private Data data;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String doorEquipmentId;
        private List<ArchivesList> archivesList;

        public String getDoorEquipmentId() {
            return doorEquipmentId;
        }

        public void setDoorEquipmentId(String doorEquipmentId) {
            this.doorEquipmentId = doorEquipmentId;
        }

        public List<ArchivesList> getArchivesList() {
            return archivesList;
        }

        public void setArchivesList(List<ArchivesList> archivesList) {
            this.archivesList = archivesList;
        }

        public static class ArchivesList {
            private String houseCode;
            private String houseNo;
            private String houseName;
            private String cabinetId;
            private String cabinetName;
            private String archivesId;
            private String borrowerDeptName;
            private String borrowerName;
            private String archivesName;
            private String archivesCode;
            private String archivesNo;
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

            public String getArchivesNo() {
                return archivesNo;
            }

            public void setArchivesNo(String archivesNo) {
                this.archivesNo = archivesNo;
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
