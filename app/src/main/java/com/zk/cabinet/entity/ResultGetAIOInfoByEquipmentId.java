package com.zk.cabinet.entity;

public class ResultGetAIOInfoByEquipmentId {

 /*   {
            "msg":"操作成功",
            "code":200,
            "data":{
                "id":"f457c9b3e6f240dfb5a4f73652f7a942",
                "houseId":"6af9340a0df84fceb051f3e04d537764",
                "houseCode":"KF008",
                "houseName":"8号测试档案室",
                "name":"测试1号一体机",
                "masterSize":"1",
                "equipmentId":"0556",
                "code":"0556",
                "ip":"0556",
                "port":556,
                "deviceId":null
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
        private String id;
        private String houseId;
        private String houseCode;
        private String houseName;
        private String name;
        private String masterSize;
        private String equipmentId;
        private String code;
        private String ip;
        private int port;
        private String deviceId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getHouseId() {
            return houseId;
        }

        public void setHouseId(String houseId) {
            this.houseId = houseId;
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMasterSize() {
            return masterSize;
        }

        public void setMasterSize(String masterSize) {
            this.masterSize = masterSize;
        }

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

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }
    }
}
