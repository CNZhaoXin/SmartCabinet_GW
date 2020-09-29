package com.zk.cabinet.faceServer.resultBean;

import com.alibaba.fastjson.JSON;

public class ResultHeart {

    /**
     * operator : HeartBeat
     * info : {"DeviceID":1434281,"Time":"2020-09-15T17:16:01"}
     */
    private String operator;
    private InfoBean info;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        ResultSuccess  resultSuccess = JSON.parseObject("",ResultSuccess.class);
        this.operator = operator;
    }

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public static class InfoBean {
        /**
         * DeviceID : 1434281
         * Time : 2020-09-15T17:16:01
         */

        private int DeviceID;
        private String Time;

        public int getDeviceID() {
            return DeviceID;
        }

        public void setDeviceID(int DeviceID) {
            this.DeviceID = DeviceID;
        }

        public String getTime() {
            return Time;
        }

        public void setTime(String Time) {
            this.Time = Time;
        }
    }
}
