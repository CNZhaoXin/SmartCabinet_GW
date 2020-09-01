package com.zk.cabinet.bean;

import java.io.Serializable;
import java.util.List;

public class ResultGetList implements Serializable {

    /**
     * success : true
     * message : OK
     * data : [{"warrantNum":"1234561","rfidNum":"12340007","warrantName":"测试1","warrantNo":"01","guarNo":"10000101","inputId":"1","inputName":"lixin","warranCate":"02","cabCode":"1234567801","position":"2","light":"1"},{"warrantNum":"124562","rfidNum":"10000002","warrantName":"测试2","warrantNo":"01","guarNo":"10000102","inputId":"1","inputName":"lixin","warranCate":"02","cabCode":"1234567801","position":"2","light":"3"},{"warrantNum":"1234563","rfidNum":"10000003","warrantName":"测试3","warrantNo":"01","guarNo":"10000103","inputId":"1","inputName":"lixin","warranCate":"02","cabCode":"1234567801","position":"2","light":"5"},{"warrantNum":"1234564","rfidNum":"20000002","warrantName":"测试4","warrantNo":"01","guarNo":"10000104","inputId":"1","inputName":"lixin","warranCate":"02","cabCode":"1234567801","position":"2","light":"7"},{"warrantNum":"1234565","rfidNum":"50000008","warrantName":"测试5","warrantNo":"01","guarNo":"10000105","inputId":"1","inputName":"lixin","warranCate":"02","cabCode":"1234567801","position":"2","light":"9"},{"warrantNum":"1234566","rfidNum":"50000009","warrantName":"测试6","warrantNo":"01","guarNo":"10000301","inputId":"1","inputName":"lixin","warranCate":"02","cabCode":"1234567803","position":"2","light":"3"},{"warrantNum":"12345674","rfidNum":"60000003","warrantName":"测试7","warrantNo":"01","guarNo":"10000300","inputId":"1","inputName":"lixin","warranCate":"02","cabCode":"1234567803","position":"2","light":"5"},{"warrantNum":"12345678","rfidNum":"60000012","warrantName":"测试8","warrantNo":"01","guarNo":"10000303","inputId":"1","inputName":"lixin","warranCate":"02","cabCode":"1234567803","position":"2","light":"7"},{"warrantNum":"123456782","rfidNum":"60000012","warrantName":"测试9","warrantNo":"01","guarNo":"100003031","inputId":"1","inputName":"lixin","warranCate":"02","cabCode":"1234567803","position":"2","light":"7"},{"warrantNum":"123456781","rfidNum":"60000012","warrantName":"测试10","warrantNo":"01","guarNo":"100003032","inputId":"1","inputName":"lixin","warranCate":"02","cabCode":"1234567803","position":"2","light":"7"},{"warrantNum":"1234567811","rfidNum":"60000012","warrantName":"测试11","warrantNo":"01","guarNo":"1000030321","inputId":"1","inputName":"lixin","warranCate":"02","cabCode":"1234567803","position":"2","light":"7"},{"warrantNum":"1234567811111","rfidNum":"12340007","warrantName":"测试12","warrantNo":"01","guarNo":"1000030341231","inputId":"1","inputName":"lixin","warranCate":"02","cabCode":"1234567801","position":"2","light":"1"},{"warrantNum":"12345678111444","rfidNum":"12340007","warrantName":"测试13","warrantNo":"01","guarNo":"10000303789","inputId":"1","inputName":"lixin","warranCate":"02","cabCode":"1234567801","position":"2","light":"1"},{"warrantNum":"1234567814447","rfidNum":"10000002","warrantName":"测试14","warrantNo":"01","guarNo":"100003035774","inputId":"1","inputName":"lixin","warranCate":"02","cabCode":"1234567801","position":"2","light":"3"}]
     * dataCount : 1
     */

    private boolean success;
    private String message;
    private String dataCount;
    private List<DataBean> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDataCount() {
        return dataCount;
    }

    public void setDataCount(String dataCount) {
        this.dataCount = dataCount;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable{
        /**
         * warrantNum : 1234561
         * rfidNum : 12340007
         * warrantName : 测试1
         * warrantNo : 01
         * guarNo : 10000101
         * inputId : 1
         * inputName : lixin
         * warranCate : 02
         * cabCode : 1234567801
         * position : 2
         * light : 1
         */

        private String warrantNum;
        private String rfidNum;
        private String warrantName;
        private String warrantNo;
        private String guarNo;
        private String inputId;
        private String inputName;
        private String warranCate;
        private String cabCode;
        private String position;
        private String light;

        public String getWarrantNum() {
            return warrantNum;
        }

        public void setWarrantNum(String warrantNum) {
            this.warrantNum = warrantNum;
        }

        public String getRfidNum() {
            return rfidNum;
        }

        public void setRfidNum(String rfidNum) {
            this.rfidNum = rfidNum;
        }

        public String getWarrantName() {
            return warrantName;
        }

        public void setWarrantName(String warrantName) {
            this.warrantName = warrantName;
        }

        public String getWarrantNo() {
            return warrantNo;
        }

        public void setWarrantNo(String warrantNo) {
            this.warrantNo = warrantNo;
        }

        public String getGuarNo() {
            return guarNo;
        }

        public void setGuarNo(String guarNo) {
            this.guarNo = guarNo;
        }

        public String getInputId() {
            return inputId;
        }

        public void setInputId(String inputId) {
            this.inputId = inputId;
        }

        public String getInputName() {
            return inputName;
        }

        public void setInputName(String inputName) {
            this.inputName = inputName;
        }

        public String getWarranCate() {
            return warranCate;
        }

        public void setWarranCate(String warranCate) {
            this.warranCate = warranCate;
        }

        public String getCabCode() {
            return cabCode;
        }

        public void setCabCode(String cabCode) {
            this.cabCode = cabCode;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getLight() {
            return light;
        }

        public void setLight(String light) {
            this.light = light;
        }
    }
}
