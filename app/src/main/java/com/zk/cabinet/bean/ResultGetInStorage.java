package com.zk.cabinet.bean;

import java.io.Serializable;
import java.util.List;

public class ResultGetInStorage implements Serializable {

    /**
     * nameValuePairs : {"success":true,"message":"OK","data":{"values":[{"nameValuePairs":{"id":"1","warrantNum":"123456","rfidNum":"10000002","warrantName":"权证名称-测试01","warrantNo":"234567","inputId":"1","inputName":"x","inOrg":"x","warranCate":"1","inStorageType":"1","warranType":"2"}},{"nameValuePairs":{"id":"2","warrantNum":"123457","rfidNum":"10000002","warrantName":"权证名称-测试01","warrantNo":"234568","warranCate":"2","inStorageType":"3","warranType":"9"}},{"nameValuePairs":{"id":"3","warrantNum":"123456","rfidNum":"10000002","warrantName":"权证名称-测试01","warrantNo":"234567","inputId":"1","inputName":"x","inOrg":"x","warranCate":"1","inStorageType":"1","warranType":"2"}},{"nameValuePairs":{"id":"4","warrantNum":"123457","rfidNum":"10000002","warrantName":"权证名称-测试02","warrantNo":"234568","warranCate":"2","inStorageType":"3","warranType":"9"}},{"nameValuePairs":{"id":"5","warrantNum":"123458","rfidNum":"10000003","warrantName":"权证名称-测试03","warrantNo":"234568","warranCate":"2","inStorageType":"3","warranType":"9"}}]}}
     */
    private NameValuePairsBeanX nameValuePairs;

    public NameValuePairsBeanX getNameValuePairs() {
        return nameValuePairs;
    }

    public void setNameValuePairs(NameValuePairsBeanX nameValuePairs) {
        this.nameValuePairs = nameValuePairs;
    }

    public static class NameValuePairsBeanX implements Serializable {
        /**
         * success : true
         * message : OK
         * data : {"values":[{"nameValuePairs":{"id":"1","warrantNum":"123456","rfidNum":"10000002","warrantName":"权证名称-测试01","warrantNo":"234567","inputId":"1","inputName":"x","inOrg":"x","warranCate":"1","inStorageType":"1","warranType":"2"}},{"nameValuePairs":{"id":"2","warrantNum":"123457","rfidNum":"10000002","warrantName":"权证名称-测试01","warrantNo":"234568","warranCate":"2","inStorageType":"3","warranType":"9"}},{"nameValuePairs":{"id":"3","warrantNum":"123456","rfidNum":"10000002","warrantName":"权证名称-测试01","warrantNo":"234567","inputId":"1","inputName":"x","inOrg":"x","warranCate":"1","inStorageType":"1","warranType":"2"}},{"nameValuePairs":{"id":"4","warrantNum":"123457","rfidNum":"10000002","warrantName":"权证名称-测试02","warrantNo":"234568","warranCate":"2","inStorageType":"3","warranType":"9"}},{"nameValuePairs":{"id":"5","warrantNum":"123458","rfidNum":"10000003","warrantName":"权证名称-测试03","warrantNo":"234568","warranCate":"2","inStorageType":"3","warranType":"9"}}]}
         */
        private boolean success;
        private String message;
        private DataBean data;

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

        public static class DataBean implements Serializable {
            private List<ValuesBean> values;

            public List<ValuesBean> getValues() {
                return values;
            }

            public void setValues(List<ValuesBean> values) {
                this.values = values;
            }

            public static class ValuesBean implements Serializable {
                /**
                 * nameValuePairs : {"id":"1","warrantNum":"123456","rfidNum":"10000002","warrantName":"权证名称-测试01","warrantNo":"234567","inputId":"1","inputName":"x","inOrg":"x","warranCate":"1","inStorageType":"1","warranType":"2"}
                 */

                private NameValuePairsBean nameValuePairs;

                public NameValuePairsBean getNameValuePairs() {
                    return nameValuePairs;
                }

                public void setNameValuePairs(NameValuePairsBean nameValuePairs) {
                    this.nameValuePairs = nameValuePairs;
                }

                public static class NameValuePairsBean implements Serializable {
                    /**
                     * id : 1
                     * warrantNum : 123456
                     * rfidNum : 10000002
                     * warrantName : 权证名称-测试01
                     * warrantNo : 234567
                     * inputId : 1
                     * inputName : x
                     * inOrg : x
                     * warranCate : 1
                     * inStorageType : 1
                     * warranType : 2
                     *
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
                }
            }
        }
    }
}
