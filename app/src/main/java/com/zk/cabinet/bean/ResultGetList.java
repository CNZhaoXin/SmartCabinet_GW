package com.zk.cabinet.bean;

import java.util.List;

public class ResultGetList {

    /**
     * success : true
     * message : OK
     * data : [{"warrantNum":null,"rfidNum":"12340007","warrantName":null,"warrantNo":null,"guarNo":"10000101","inputId":"1","inputName":"lixin","warranCate":null,"cabCode":"1234567801","position":"1","light":"2"},{"warrantNum":null,"rfidNum":"10000002","warrantName":null,"warrantNo":null,"guarNo":"10000102","inputId":"1","inputName":"lixin","warranCate":null,"cabCode":"1234567801","position":"3","light":"2"},{"warrantNum":null,"rfidNum":"10000003","warrantName":null,"warrantNo":null,"guarNo":"10000103","inputId":"1","inputName":"lixin","warranCate":null,"cabCode":"1234567801","position":"5","light":"2"},{"warrantNum":null,"rfidNum":"20000002","warrantName":null,"warrantNo":null,"guarNo":"10000104","inputId":"1","inputName":"lixin","warranCate":null,"cabCode":"1234567801","position":"7","light":"2"},{"warrantNum":null,"rfidNum":"50000008","warrantName":null,"warrantNo":null,"guarNo":"10000105","inputId":"1","inputName":"lixin","warranCate":null,"cabCode":"1234567801","position":"9","light":"2"},{"warrantNum":null,"rfidNum":"50000009","warrantName":null,"warrantNo":null,"guarNo":"10000301","inputId":"1","inputName":"lixin","warranCate":null,"cabCode":"1234567803","position":"3","light":"2"},{"warrantNum":null,"rfidNum":"60000003","warrantName":null,"warrantNo":null,"guarNo":"10000300","inputId":"1","inputName":"lixin","warranCate":null,"cabCode":"1234567803","position":"5","light":"2"},{"warrantNum":null,"rfidNum":"60000012","warrantName":null,"warrantNo":null,"guarNo":"10000303","inputId":"1","inputName":"lixin","warranCate":null,"cabCode":"1234567803","position":"7","light":"2"}]
     */

    private boolean success;
    private String message;
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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * warrantNum : null
         * rfidNum : 12340007
         * warrantName : null
         * warrantNo : null
         * guarNo : 10000101
         * inputId : 1
         * inputName : lixin
         * warranCate : null
         * cabCode : 1234567801
         * position : 1
         * light : 2
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
