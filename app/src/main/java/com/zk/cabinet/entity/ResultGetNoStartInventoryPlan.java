package com.zk.cabinet.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 获取未盘库的盘库计划信息
 * /api/pad/getNoStartInventoryPlan
 */
public class ResultGetNoStartInventoryPlan implements Serializable {

    /**
     * msg : 操作成功
     * code : 200
     * data : [{"id":"67996529c1144e3abf10eb9f73747e09","inventoryType":"1","houseCode":"KF001","houseName":"1号档案室","planTime":"2020-10-28 00:00","doTime":null,"archivesNumOriginal":null,"archivesNumNow":null,"errNum":null,"operatorName":null,"remark":null,"cabineBeanList":[{"attributeNo":null,"equipmentId":"123123123","cabinetType":"1","attributeName":"档案组架-01-01","houseCode":"KF001","houseName":"1号档案室","masterEquipmentId":"12312","rowNum":5,"lampNum":24,"archivesAllowPos":120,"usedNum":null,"freeNum":null},{"attributeNo":null,"equipmentId":"123123","cabinetType":"2","attributeName":"档案组柜-01-01","houseCode":"KF001","houseName":"1号档案室","masterEquipmentId":"11111111","rowNum":5,"lampNum":24,"archivesAllowPos":120,"usedNum":null,"freeNum":null},{"attributeNo":"203334452","equipmentId":"203334452","cabinetType":"3","attributeName":"档案单柜-01","houseCode":"KF001","houseName":"1号档案室","masterEquipmentId":"00000001","rowNum":5,"lampNum":24,"archivesAllowPos":1200,"usedNum":null,"freeNum":null},{"attributeNo":"223444","equipmentId":"99811","cabinetType":"1","attributeName":"柜子啊","houseCode":"KF001","houseName":"1号档案室","masterEquipmentId":"89t007","rowNum":6,"lampNum":15,"archivesAllowPos":null,"usedNum":null,"freeNum":null}]},{"id":"0e38c8c55fa0429f9ee93b90ddbe91f2","inventoryType":"1","houseCode":"KF001","houseName":"1号档案室","planTime":"2020-10-27 17:44","doTime":null,"archivesNumOriginal":null,"archivesNumNow":null,"errNum":null,"operatorName":null,"remark":null,"cabineBeanList":[{"attributeNo":null,"equipmentId":"123123","cabinetType":"2","attributeName":"档案组柜-01-01","houseCode":"KF001","houseName":"1号档案室","masterEquipmentId":"11111111","rowNum":5,"lampNum":24,"archivesAllowPos":120,"usedNum":null,"freeNum":null},{"attributeNo":null,"equipmentId":"123123123","cabinetType":"1","attributeName":"档案组架-01-01","houseCode":"KF001","houseName":"1号档案室","masterEquipmentId":"12312","rowNum":5,"lampNum":24,"archivesAllowPos":120,"usedNum":null,"freeNum":null},{"attributeNo":"203334452","equipmentId":"203334452","cabinetType":"3","attributeName":"档案单柜-01","houseCode":"KF001","houseName":"1号档案室","masterEquipmentId":"00000001","rowNum":5,"lampNum":24,"archivesAllowPos":1200,"usedNum":null,"freeNum":null},{"attributeNo":"223444","equipmentId":"99811","cabinetType":"1","attributeName":"柜子啊","houseCode":"KF001","houseName":"1号档案室","masterEquipmentId":"89t007","rowNum":6,"lampNum":15,"archivesAllowPos":null,"usedNum":null,"freeNum":null}]},{"id":"d9fbf4035f4f4b5e9607b6066163f068","inventoryType":"1","houseCode":"KF001","houseName":"一号档案室","planTime":"2020-09-23 18:05","doTime":null,"archivesNumOriginal":null,"archivesNumNow":null,"errNum":null,"operatorName":null,"remark":"8888","cabineBeanList":[{"attributeNo":"223444","equipmentId":"99811","cabinetType":"1","attributeName":"柜子啊","houseCode":"KF001","houseName":"1号档案室","masterEquipmentId":"89t007","rowNum":6,"lampNum":15,"archivesAllowPos":null,"usedNum":null,"freeNum":null}]}]
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

    public static class DataBean implements Serializable {
        /**
         * id : 67996529c1144e3abf10eb9f73747e09
         * inventoryType : 1
         * houseCode : KF001
         * houseName : 1号档案室
         * planTime : 2020-10-28 00:00
         * doTime : null
         * archivesNumOriginal : null
         * archivesNumNow : null
         * errNum : null
         * operatorName : null
         * remark : null
         * cabineBeanList : [{"attributeNo":null,"equipmentId":"123123123","cabinetType":"1","attributeName":"档案组架-01-01","houseCode":"KF001","houseName":"1号档案室","masterEquipmentId":"12312","rowNum":5,"lampNum":24,"archivesAllowPos":120,"usedNum":null,"freeNum":null},{"attributeNo":null,"equipmentId":"123123","cabinetType":"2","attributeName":"档案组柜-01-01","houseCode":"KF001","houseName":"1号档案室","masterEquipmentId":"11111111","rowNum":5,"lampNum":24,"archivesAllowPos":120,"usedNum":null,"freeNum":null},{"attributeNo":"203334452","equipmentId":"203334452","cabinetType":"3","attributeName":"档案单柜-01","houseCode":"KF001","houseName":"1号档案室","masterEquipmentId":"00000001","rowNum":5,"lampNum":24,"archivesAllowPos":1200,"usedNum":null,"freeNum":null},{"attributeNo":"223444","equipmentId":"99811","cabinetType":"1","attributeName":"柜子啊","houseCode":"KF001","houseName":"1号档案室","masterEquipmentId":"89t007","rowNum":6,"lampNum":15,"archivesAllowPos":null,"usedNum":null,"freeNum":null}]
         */
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
        private Object remark;
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

        public Object getRemark() {
            return remark;
        }

        public void setRemark(Object remark) {
            this.remark = remark;
        }

        public List<CabineBeanListBean> getCabineBeanList() {
            return cabineBeanList;
        }

        public void setCabineBeanList(List<CabineBeanListBean> cabineBeanList) {
            this.cabineBeanList = cabineBeanList;
        }

        public static class CabineBeanListBean implements Serializable {
            /**
             * attributeNo : null
             * equipmentId : 123123123
             * cabinetType : 1
             * attributeName : 档案组架-01-01
             * houseCode : KF001
             * houseName : 1号档案室
             * masterEquipmentId : 12312
             * rowNum : 5
             * lampNum : 24
             * archivesAllowPos : 120
             * usedNum : null
             * freeNum : null
             */

            private Object attributeNo;
            private String equipmentId;
            private String cabinetType;
            private String attributeName;
            private String houseCode;
            private String houseName;
            private String masterEquipmentId;
            private int rowNum;
            private int lampNum;
            private int archivesAllowPos;
            private Object usedNum;
            private Object freeNum;

            public Object getAttributeNo() {
                return attributeNo;
            }

            public void setAttributeNo(Object attributeNo) {
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
