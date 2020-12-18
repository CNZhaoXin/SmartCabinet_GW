package com.zk.cabinet.entity;

import java.util.List;

public class ResultGetCaineMasterByEquipmentId {

/*    {
            "msg": "操作成功",
            "code": 200,
            "data": {
                "cabineBeanList": [
                    {
                            "attributeNo": "2",
                            "equipmentId": "121",
                            "cabinetType": "3",
                            "attributeName": "8号测试柜",
                            "houseCode": "KF008",
                            "houseName": "8号测试档案室",
                            "masterEquipmentId": "12id",
                            "rowNum": 5,
                            "lampNum": 24,
                            "archivesAllowPos": 24,
                            "usedNum": 1,
                            "freeNum": 119
                    }
                 ],
                "equipmentId": "12id",
                "code": "12bh",
                "name": "8档案柜操作屏",
                "houseCode": "KF008",
                "houseName": "8号测试档案室",
                "remark": null
                     }
    }*/
    private String msg;
    private int code;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String equipmentId;
        private String code;
        private String name;
        private String houseCode;
        private String houseName;
        private String remark;
        private List<CabineBeanListBean> cabineBeanList;

        public String getEquipmentId() {
            return equipmentId;
        }

        public void setEquipmentId(String equipmentId) {
            this.equipmentId = equipmentId;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
            private String houseCode;
            private String houseName;
            private String masterEquipmentId;
            private int rowNum;
            private int lampNum;
            private int archivesAllowPos;
            private int usedNum;
            private int freeNum;

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

            public int getUsedNum() {
                return usedNum;
            }

            public void setUsedNum(int usedNum) {
                this.usedNum = usedNum;
            }

            public int getFreeNum() {
                return freeNum;
            }

            public void setFreeNum(int freeNum) {
                this.freeNum = freeNum;
            }
        }
    }
}
