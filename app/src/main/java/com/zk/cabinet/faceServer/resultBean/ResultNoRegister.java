package com.zk.cabinet.faceServer.resultBean;

public class ResultNoRegister {

    /**
     * operator : SnapPush
     * info : {"DeviceID":1434281,"CreateTime":"2020-09-15T17:16:23","PictureType":0,"Sendintime":1}
     * SanpPic : data:image/jpeg;base64,Qk3m5QAAAAAAADYAAAAoAAAAjAAAAIwAAAABABgAAAAAAAAAAAASCwAAEgs.......
     */

    private String operator;
    private InfoBean info;
    private String SanpPic;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public String getSanpPic() {
        return SanpPic;
    }

    public void setSanpPic(String SanpPic) {
        this.SanpPic = SanpPic;
    }

    public static class InfoBean {
        /**
         * DeviceID : 1434281
         * CreateTime : 2020-09-15T17:16:23
         * PictureType : 0
         * Sendintime : 1
         */

        private int DeviceID;
        private String CreateTime;
        private int PictureType;
        private int Sendintime;

        public int getDeviceID() {
            return DeviceID;
        }

        public void setDeviceID(int DeviceID) {
            this.DeviceID = DeviceID;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }

        public int getPictureType() {
            return PictureType;
        }

        public void setPictureType(int PictureType) {
            this.PictureType = PictureType;
        }

        public int getSendintime() {
            return Sendintime;
        }

        public void setSendintime(int Sendintime) {
            this.Sendintime = Sendintime;
        }
    }
}
