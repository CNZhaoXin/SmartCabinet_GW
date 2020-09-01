package com.zk.cabinet.bean;

import java.io.Serializable;
import java.util.List;

public class ResultGetInStorage implements Serializable {


    /**
     * nameValuePairs : {"success":true,"message":"OK","data":{"values":[{"nameValuePairs":{"id":"10","warrantNum":"1234561","rfidNum":"12340007 ","warrantName":"权证名称-测试01_01","warrantNo":"2345671","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"1","position":"2","cabCode":"1234567801"}},{"nameValuePairs":{"id":"11","warrantNum":"1234561","rfidNum":"12340007 ","warrantName":"权证名称-测试01_02","warrantNo":"2345672","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"1","position":"2","cabCode":"1234567801"}},{"nameValuePairs":{"id":"2","warrantNum":"123456","rfidNum":"12340007 ","warrantName":"权证名称-测试01","warrantNo":"234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"1","position":"2","cabCode":"1234567801"}},{"nameValuePairs":{"id":"3","warrantNum":"1123456","rfidNum":"10000002 ","warrantName":"权证名称-测试02","warrantNo":"1234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"3","position":"2","cabCode":"1234567801"}},{"nameValuePairs":{"id":"4","warrantNum":"2123456","rfidNum":"10000003","warrantName":"权证名称-测试03","warrantNo":"2234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"5","position":"2","cabCode":"1234567801"}},{"nameValuePairs":{"id":"5","warrantNum":"3123456","rfidNum":"20000002","warrantName":"权证名称-测试04","warrantNo":"3234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"7","position":"2","cabCode":"1234567801"}},{"nameValuePairs":{"id":"6","warrantNum":"5123456","rfidNum":"50000008","warrantName":"权证名称-测试05","warrantNo":"5234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"9","position":"2","cabCode":"1234567801"}},{"nameValuePairs":{"id":"7","warrantNum":"6123456","rfidNum":"50000009","warrantName":"权证名称-测试06","warrantNo":"6234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"3","position":"2","cabCode":"1234567803"}},{"nameValuePairs":{"id":"8","warrantNum":"7123456","rfidNum":"60000003","warrantName":"权证名称-测试07","warrantNo":"7234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"5","position":"2","cabCode":"1234567803"}},{"nameValuePairs":{"id":"9","warrantNum":"8123456","rfidNum":"60000012","warrantName":"权证名称-测试08","warrantNo":"7234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"7","position":"2","cabCode":"1234567803"}}]},"dataCount":"1"}
     */

    private NameValuePairsBeanX nameValuePairs;

    public NameValuePairsBeanX getNameValuePairs() {
        return nameValuePairs;
    }

    public void setNameValuePairs(NameValuePairsBeanX nameValuePairs) {
        this.nameValuePairs = nameValuePairs;
    }

    public static class NameValuePairsBeanX implements Serializable{
        /**
         * success : true
         * message : OK
         * data : {"values":[{"nameValuePairs":{"id":"10","warrantNum":"1234561","rfidNum":"12340007 ","warrantName":"权证名称-测试01_01","warrantNo":"2345671","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"1","position":"2","cabCode":"1234567801"}},{"nameValuePairs":{"id":"11","warrantNum":"1234561","rfidNum":"12340007 ","warrantName":"权证名称-测试01_02","warrantNo":"2345672","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"1","position":"2","cabCode":"1234567801"}},{"nameValuePairs":{"id":"2","warrantNum":"123456","rfidNum":"12340007 ","warrantName":"权证名称-测试01","warrantNo":"234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"1","position":"2","cabCode":"1234567801"}},{"nameValuePairs":{"id":"3","warrantNum":"1123456","rfidNum":"10000002 ","warrantName":"权证名称-测试02","warrantNo":"1234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"3","position":"2","cabCode":"1234567801"}},{"nameValuePairs":{"id":"4","warrantNum":"2123456","rfidNum":"10000003","warrantName":"权证名称-测试03","warrantNo":"2234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"5","position":"2","cabCode":"1234567801"}},{"nameValuePairs":{"id":"5","warrantNum":"3123456","rfidNum":"20000002","warrantName":"权证名称-测试04","warrantNo":"3234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"7","position":"2","cabCode":"1234567801"}},{"nameValuePairs":{"id":"6","warrantNum":"5123456","rfidNum":"50000008","warrantName":"权证名称-测试05","warrantNo":"5234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"9","position":"2","cabCode":"1234567801"}},{"nameValuePairs":{"id":"7","warrantNum":"6123456","rfidNum":"50000009","warrantName":"权证名称-测试06","warrantNo":"6234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"3","position":"2","cabCode":"1234567803"}},{"nameValuePairs":{"id":"8","warrantNum":"7123456","rfidNum":"60000003","warrantName":"权证名称-测试07","warrantNo":"7234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"5","position":"2","cabCode":"1234567803"}},{"nameValuePairs":{"id":"9","warrantNum":"8123456","rfidNum":"60000012","warrantName":"权证名称-测试08","warrantNo":"7234567","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"7","position":"2","cabCode":"1234567803"}}]}
         * dataCount : 1
         */

        private boolean success;
        private String message;
        private DataBean data;
        private String dataCount;

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

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public String getDataCount() {
            return dataCount;
        }

        public void setDataCount(String dataCount) {
            this.dataCount = dataCount;
        }

        public static class DataBean implements Serializable{
            private List<ValuesBean> values;

            public List<ValuesBean> getValues() {
                return values;
            }

            public void setValues(List<ValuesBean> values) {
                this.values = values;
            }

            public static class ValuesBean implements Serializable{
                /**
                 * nameValuePairs : {"id":"10","warrantNum":"1234561","rfidNum":"12340007 ","warrantName":"权证名称-测试01_01","warrantNo":"2345671","inputId":"1","inputName":"1","inOrg":"1","warranCate":"01","inStorageType":"01","warranType":"02","light":"1","position":"2","cabCode":"1234567801"}
                 */

                private NameValuePairsBean nameValuePairs;

                public NameValuePairsBean getNameValuePairs() {
                    return nameValuePairs;
                }

                public void setNameValuePairs(NameValuePairsBean nameValuePairs) {
                    this.nameValuePairs = nameValuePairs;
                }

                public static class NameValuePairsBean implements Serializable{
                    /**
                     * id : 10
                     * warrantNum : 1234561
                     * rfidNum : 12340007
                     * warrantName : 权证名称-测试01_01
                     * warrantNo : 2345671
                     * inputId : 1
                     * inputName : 1
                     * inOrg : 1
                     * warranCate : 01
                     * inStorageType : 01
                     * warranType : 02
                     * light : 1
                     * position : 2
                     * cabCode : 1234567801
                     */

                    private String id;
                    private String warrantNum;
                    private String rfidNum;
                    private String warrantName;
                    private String warrantNo;
                    private String inputId;
                    private String inputName;
                    private String inOrg;
                    private String warranCate;
                    private String inStorageType;
                    private String warranType;
                    private String light;
                    private String position;
                    private String cabCode;

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }

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

                    public String getInOrg() {
                        return inOrg;
                    }

                    public void setInOrg(String inOrg) {
                        this.inOrg = inOrg;
                    }

                    public String getWarranCate() {
                        return warranCate;
                    }

                    public void setWarranCate(String warranCate) {
                        this.warranCate = warranCate;
                    }

                    public String getInStorageType() {
                        return inStorageType;
                    }

                    public void setInStorageType(String inStorageType) {
                        this.inStorageType = inStorageType;
                    }

                    public String getWarranType() {
                        return warranType;
                    }

                    public void setWarranType(String warranType) {
                        this.warranType = warranType;
                    }

                    public String getLight() {
                        return light;
                    }

                    public void setLight(String light) {
                        this.light = light;
                    }

                    public String getPosition() {
                        return position;
                    }

                    public void setPosition(String position) {
                        this.position = position;
                    }

                    public String getCabCode() {
                        return cabCode;
                    }

                    public void setCabCode(String cabCode) {
                        this.cabCode = cabCode;
                    }
                }
            }
        }
    }
}
