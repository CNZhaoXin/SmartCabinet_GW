package com.zk.cabinet.entity;

public class ResultGetCapital {

    /* {
        "msg":"操作成功", "code":200, "data":{
                "houseCode":null,
                "equipmentId":null,
                "houseCapacity":52560, // 库房总容量
                "houseFree":52503,     // 库房剩余
                "cabinetUsed":57,      // 当前柜已存量
                "cabinetFree":2823     // 当前柜剩余量
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
        private String houseCode;
        private String equipmentId;
        private int houseCapacity;
        private int houseFree;
        private int cabinetUsed;
        private int cabinetFree;

        public String getHouseCode() {
            return houseCode;
        }

        public void setHouseCode(String houseCode) {
            this.houseCode = houseCode;
        }

        public String getEquipmentId() {
            return equipmentId;
        }

        public void setEquipmentId(String equipmentId) {
            this.equipmentId = equipmentId;
        }

        public int getHouseCapacity() {
            return houseCapacity;
        }

        public void setHouseCapacity(int houseCapacity) {
            this.houseCapacity = houseCapacity;
        }

        public int getHouseFree() {
            return houseFree;
        }

        public void setHouseFree(int houseFree) {
            this.houseFree = houseFree;
        }

        public int getCabinetUsed() {
            return cabinetUsed;
        }

        public void setCabinetUsed(int cabinetUsed) {
            this.cabinetUsed = cabinetUsed;
        }

        public int getCabinetFree() {
            return cabinetFree;
        }

        public void setCabinetFree(int cabinetFree) {
            this.cabinetFree = cabinetFree;
        }
    }
}
