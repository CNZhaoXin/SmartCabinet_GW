package com.zk.cabinet.entity;

public class ResultGetHumitureByHouseId {

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
        private double temperatureValue;
        private double humidityValue;

        public double getTemperatureValue() {
            return temperatureValue;
        }

        public void setTemperatureValue(double temperatureValue) {
            this.temperatureValue = temperatureValue;
        }

        public double getHumidityValue() {
            return humidityValue;
        }

        public void setHumidityValue(double humidityValue) {
            this.humidityValue = humidityValue;
        }
    }
}
