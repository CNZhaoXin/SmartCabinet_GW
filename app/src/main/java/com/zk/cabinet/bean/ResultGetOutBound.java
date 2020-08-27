package com.zk.cabinet.bean;

import java.io.Serializable;
import java.util.List;

public class ResultGetOutBound implements Serializable {

    /**
     * nameValuePairs : {"success":true,"message":"OK","data":{"values":[{"nameValuePairs":{"warrantNum":"2018092700000539","rfidNum":"2018092700000676","warrantName":"定价公司一抵押","warrantNo":"15151511","guarNo":"0030020020012018000347","inputId":"5075","inputName":"尹诗","warranCate":"02","outStorageType":"05","warranType":"18","position":"3","light":"8","cabcode":"1234567801"}},{"nameValuePairs":{"warrantNum":"2018051900000356","rfidNum":"2018092700000670","warrantName":"234324测试2019053000001测试2019053000001","warrantNo":"234234","guarNo":"0030020010012018000278","inputId":"5075","inputName":"尹诗","warranCate":"01","outStorageType":"04","warranType":"06","position":"4","light":"7","cabcode":"1234567801"}}]}}
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
         * data : {"values":[{"nameValuePairs":{"warrantNum":"2018092700000539","rfidNum":"2018092700000676","warrantName":"定价公司一抵押","warrantNo":"15151511","guarNo":"0030020020012018000347","inputId":"5075","inputName":"尹诗","warranCate":"02","outStorageType":"05","warranType":"18","position":"3","light":"8","cabcode":"1234567801"}},{"nameValuePairs":{"warrantNum":"2018051900000356","rfidNum":"2018092700000670","warrantName":"234324测试2019053000001测试2019053000001","warrantNo":"234234","guarNo":"0030020010012018000278","inputId":"5075","inputName":"尹诗","warranCate":"01","outStorageType":"04","warranType":"06","position":"4","light":"7","cabcode":"1234567801"}}]}
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
                 * nameValuePairs : {"warrantNum":"2018092700000539","rfidNum":"2018092700000676","warrantName":"定价公司一抵押","warrantNo":"15151511","guarNo":"0030020020012018000347","inputId":"5075","inputName":"尹诗","warranCate":"02","outStorageType":"05","warranType":"18","position":"3","light":"8","cabcode":"1234567801"}
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
                     * warrantNum : 2018092700000539
                     * rfidNum : 2018092700000676
                     * warrantName : 定价公司一抵押
                     * warrantNo : 15151511
                     * guarNo : 0030020020012018000347
                     * inputId : 5075
                     * inputName : 尹诗
                     * warranCate : 02
                     * outStorageType : 05
                     * warranType : 18
                     * position : 3
                     * light : 8
                     * cabcode : 1234567801
                     */
                    private String warrantNum;
                    private String rfidNum;
                    private String warrantName;
                    private String warrantNo;
                    private String guarNo;
                    private String inputId;
                    private String inputName;
                    private String warranCate;
                    private String outStorageType;
                    private String warranType;
                    private String position;
                    private String light;
                    private String cabcode;
                    private boolean selected;

                    public boolean isSelected() {
                        return selected;
                    }

                    public void setSelected(boolean selected) {
                        this.selected = selected;
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

                    public String getOutStorageType() {
                        return outStorageType;
                    }

                    public void setOutStorageType(String outStorageType) {
                        this.outStorageType = outStorageType;
                    }

                    public String getWarranType() {
                        return warranType;
                    }

                    public void setWarranType(String warranType) {
                        this.warranType = warranType;
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

                    public String getCabcode() {
                        return cabcode;
                    }

                    public void setCabcode(String cabcode) {
                        this.cabcode = cabcode;
                    }
                }
            }
        }
    }
}
