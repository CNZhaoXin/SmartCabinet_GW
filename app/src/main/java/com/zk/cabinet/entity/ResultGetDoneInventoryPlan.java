package com.zk.cabinet.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 获取已生成盘库结果的盘库计划信息
 * 接口地址: /api/pad/getDoneInventoryPlan
 */
public class ResultGetDoneInventoryPlan implements Serializable {


    /**
     * msg : 操作成功
     * code : 200
     * data : {"total":2,"rows":[{"id":"3124d94b0241400e904e7b0363b4d72d","inventoryType":"2","houseCode":"KF001","houseName":"1号档案室","planTime":"2020-10-30 11:40","doTime":"2020-11-02 09:11:49","archivesNumOriginal":9,"archivesNumNow":6,"errNum":8,"operatorName":"admin","remark":"fffffffff","cabineBeanList":null},{"id":"229407a708ba440f9f986b55c26f57da","inventoryType":"2","houseCode":"KF001","houseName":"1号档案室","planTime":"2020-10-30 14:50","doTime":"2020-10-30 19:46:31","archivesNumOriginal":0,"archivesNumNow":6,"errNum":3,"operatorName":"admin","remark":"盘库fffff","cabineBeanList":null}],"code":200,"msg":"查询成功"}
     */

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

    public static class DataBean implements Serializable {
        /**
         * total : 2
         * rows : [{"id":"3124d94b0241400e904e7b0363b4d72d","inventoryType":"2","houseCode":"KF001","houseName":"1号档案室","planTime":"2020-10-30 11:40","doTime":"2020-11-02 09:11:49","archivesNumOriginal":9,"archivesNumNow":6,"errNum":8,"operatorName":"admin","remark":"fffffffff","cabineBeanList":null},{"id":"229407a708ba440f9f986b55c26f57da","inventoryType":"2","houseCode":"KF001","houseName":"1号档案室","planTime":"2020-10-30 14:50","doTime":"2020-10-30 19:46:31","archivesNumOriginal":0,"archivesNumNow":6,"errNum":3,"operatorName":"admin","remark":"盘库fffff","cabineBeanList":null}]
         * code : 200
         * msg : 查询成功
         */
        private int total;
        private int code;
        private String msg;
        private List<RowsBean> rows;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<RowsBean> getRows() {
            return rows;
        }

        public void setRows(List<RowsBean> rows) {
            this.rows = rows;
        }

        public static class RowsBean implements Serializable {
            /**
             * id : 3124d94b0241400e904e7b0363b4d72d
             * inventoryType : 2
             * houseCode : KF001
             * houseName : 1号档案室
             * planTime : 2020-10-30 11:40
             * doTime : 2020-11-02 09:11:49
             * archivesNumOriginal : 9
             * archivesNumNow : 6
             * errNum : 8
             * operatorName : admin
             * remark : fffffffff
             * cabineBeanList : null
             */

            private String id;
            private String inventoryType;
            private String houseCode;
            private String houseName;
            private String planTime;
            private String doTime;
            private int archivesNumOriginal;
            private int archivesNumNow;
            private int errNum;
            private String operatorName;
            private String remark;
            private Object cabineBeanList;

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

            public String getDoTime() {
                return doTime;
            }

            public void setDoTime(String doTime) {
                this.doTime = doTime;
            }

            public int getArchivesNumOriginal() {
                return archivesNumOriginal;
            }

            public void setArchivesNumOriginal(int archivesNumOriginal) {
                this.archivesNumOriginal = archivesNumOriginal;
            }

            public int getArchivesNumNow() {
                return archivesNumNow;
            }

            public void setArchivesNumNow(int archivesNumNow) {
                this.archivesNumNow = archivesNumNow;
            }

            public int getErrNum() {
                return errNum;
            }

            public void setErrNum(int errNum) {
                this.errNum = errNum;
            }

            public String getOperatorName() {
                return operatorName;
            }

            public void setOperatorName(String operatorName) {
                this.operatorName = operatorName;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public Object getCabineBeanList() {
                return cabineBeanList;
            }

            public void setCabineBeanList(Object cabineBeanList) {
                this.cabineBeanList = cabineBeanList;
            }
        }
    }
}
