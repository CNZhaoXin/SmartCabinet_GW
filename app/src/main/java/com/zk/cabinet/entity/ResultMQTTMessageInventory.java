package com.zk.cabinet.entity;

import java.util.List;

/**
 * MQTT 自动盘库计划 消息
 */
public class ResultMQTTMessageInventory {

   /* {
        "msgType": "5",
            "data": {
        "id": "122180b1deb84c36b18f2b2500d1aad8",
                "inventoryType": "1",
                "houseCode": null,
                "houseName": "8号测试档案室",
                "planTime": "2020-12-10 12:06",
                "doTime": null,
                "archivesNumOriginal": null,
                "archivesNumNow": null,
                "errNum": null,
                "operatorName": null,
                "remark": "啊实打实多",
                "cabineBeanList": [{
            "attributeNo": "203334452",
                    "equipmentId": "204776152",
                    "cabinetType": "2",
                    "attributeName": "档案组柜(赵-1411-9.86)",
                    "houseCode": null,
                    "houseName": null,
                    "masterEquipmentId": "3",
                    "rowNum": 5,
                    "lampNum": 24,
                    "archivesAllowPos": 10,
                    "usedNum": null,
                    "freeNum": null
        }, {
            "attributeNo": "204742930",
                    "equipmentId": "204742930",
                    "cabinetType": "2",
                    "attributeName": "档案组柜(赵-1413-9.91)",
                    "houseCode": null,
                    "houseName": null,
                    "masterEquipmentId": "12id",
                    "rowNum": 5,
                    "lampNum": 24,
                    "archivesAllowPos": 10,
                    "usedNum": null,
                    "freeNum": null
        }]
    }
    }*/

    private String msgType;
    private DataBean data;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String id;
        private String inventoryType;
        private String houseCode;
        private String houseName;
        private String planTime;
        private Object doTime;
        private Object archivesNumOriginal;
        private Object archivesNumNow;
        private Object errNum;
        private Object operatorName;
        private String remark;
        private List<CabineBeanListBean> cabineBeanList;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getInventoryType() {
            return inventoryType;
        }

        public void setInventoryType(String inventoryType) {
            this.inventoryType = inventoryType;
        }

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

        public String getPlanTime() {
            return planTime;
        }

        public void setPlanTime(String planTime) {
            this.planTime = planTime;
        }

        public Object getDoTime() {
            return doTime;
        }

        public void setDoTime(Object doTime) {
            this.doTime = doTime;
        }

        public Object getArchivesNumOriginal() {
            return archivesNumOriginal;
        }

        public void setArchivesNumOriginal(Object archivesNumOriginal) {
            this.archivesNumOriginal = archivesNumOriginal;
        }

        public Object getArchivesNumNow() {
            return archivesNumNow;
        }

        public void setArchivesNumNow(Object archivesNumNow) {
            this.archivesNumNow = archivesNumNow;
        }

        public Object getErrNum() {
            return errNum;
        }

        public void setErrNum(Object errNum) {
            this.errNum = errNum;
        }

        public Object getOperatorName() {
            return operatorName;
        }

        public void setOperatorName(Object operatorName) {
            this.operatorName = operatorName;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public List<CabineBeanListBean> getCabineBeanList() {
            return cabineBeanList;
        }

        public void setCabineBeanList(List<CabineBeanListBean> cabineBeanList) {
            this.cabineBeanList = cabineBeanList;
        }

        public static class CabineBeanListBean {
            private String attributeNo;
            private String equipmentId;
            private String cabinetType;
            private String attributeName;
            private Object houseCode;
            private Object houseName;
            private String masterEquipmentId;
            private int rowNum;
            private int lampNum;
            private int archivesAllowPos;
            private Object usedNum;
            private Object freeNum;

            public String getAttributeNo() {
                return attributeNo;
            }

            public void setAttributeNo(String attributeNo) {
                this.attributeNo = attributeNo;
            }

            public String getEquipmentId() {
                return equipmentId;
            }

            public void setEquipmentId(String equipmentId) {
                this.equipmentId = equipmentId;
            }

            public String getCabinetType() {
                return cabinetType;
            }

            public void setCabinetType(String cabinetType) {
                this.cabinetType = cabinetType;
            }

            public String getAttributeName() {
                return attributeName;
            }

            public void setAttributeName(String attributeName) {
                this.attributeName = attributeName;
            }

            public Object getHouseCode() {
                return houseCode;
            }

            public void setHouseCode(Object houseCode) {
                this.houseCode = houseCode;
            }

            public Object getHouseName() {
                return houseName;
            }

            public void setHouseName(Object houseName) {
                this.houseName = houseName;
            }

            public String getMasterEquipmentId() {
                return masterEquipmentId;
            }

            public void setMasterEquipmentId(String masterEquipmentId) {
                this.masterEquipmentId = masterEquipmentId;
            }

            public int getRowNum() {
                return rowNum;
            }

            public void setRowNum(int rowNum) {
                this.rowNum = rowNum;
            }

            public int getLampNum() {
                return lampNum;
            }

            public void setLampNum(int lampNum) {
                this.lampNum = lampNum;
            }

            public int getArchivesAllowPos() {
                return archivesAllowPos;
            }

            public void setArchivesAllowPos(int archivesAllowPos) {
                this.archivesAllowPos = archivesAllowPos;
            }

            public Object getUsedNum() {
                return usedNum;
            }

            public void setUsedNum(Object usedNum) {
                this.usedNum = usedNum;
            }

            public Object getFreeNum() {
                return freeNum;
            }

            public void setFreeNum(Object freeNum) {
                this.freeNum = freeNum;
            }
        }
    }
}
