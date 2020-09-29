package com.zk.cabinet.faceServer.resultBean;

public class ResultSuccess {

    /**
     * operator : VerifyPush
     * info : {"PersonID":3,"CreateTime":"2020-09-15T17:19:41","Similarity1":94.279579,"Similarity2":0,"VerifyStatus":1,"VerfyType":1,"PersonType":0,"Name":"赵鑫","Gender":0,"Nation":1,"CardType":0,"IdCard":"123456","Birthday":"2000-01-01","Telnum":" ","Native":" ","Address":" ","Notes":" ","MjCardFrom":0,"DeviceID":1434281,"MjCardNo":1,"Tempvalid":0,"CustomizeID":0,"PersonUUID":" ","ValidBegin":"0000-00-00T00:00:00","ValidEnd":"0000-00-00T00:00:00","Sendintime":1}
     */

    private String operator;
    private InfoBean info;

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

    public static class InfoBean {
        /**
         * PersonID : 3
         * CreateTime : 2020-09-15T17:19:41
         * Similarity1 : 94.279579
         * Similarity2 : 0
         * VerifyStatus : 1
         * VerfyType : 1
         * PersonType : 0
         * Name : 赵鑫
         * Gender : 0
         * Nation : 1
         * CardType : 0
         * IdCard : 123456
         * Birthday : 2000-01-01
         * Telnum :
         * Native :
         * Address :
         * Notes :
         * MjCardFrom : 0
         * DeviceID : 1434281
         * MjCardNo : 1
         * Tempvalid : 0
         * CustomizeID : 0
         * PersonUUID :
         * ValidBegin : 0000-00-00T00:00:00
         * ValidEnd : 0000-00-00T00:00:00
         * Sendintime : 1
         */

        private int PersonID;
        private String CreateTime;
        private double Similarity1;
        private int Similarity2;
        private int VerifyStatus;
        private int VerfyType;
        private int PersonType;
        private String Name;
        private int Gender;
        private int Nation;
        private int CardType;
        private String IdCard;
        private String Birthday;
        private String Telnum;
        private String Native;
        private String Address;
        private String Notes;
        private int MjCardFrom;
        private int DeviceID;
        private int MjCardNo;
        private int Tempvalid;
        private int CustomizeID;
        private String PersonUUID;
        private String ValidBegin;
        private String ValidEnd;
        private int Sendintime;

        public int getPersonID() {
            return PersonID;
        }

        public void setPersonID(int PersonID) {
            this.PersonID = PersonID;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }

        public double getSimilarity1() {
            return Similarity1;
        }

        public void setSimilarity1(double Similarity1) {
            this.Similarity1 = Similarity1;
        }

        public int getSimilarity2() {
            return Similarity2;
        }

        public void setSimilarity2(int Similarity2) {
            this.Similarity2 = Similarity2;
        }

        public int getVerifyStatus() {
            return VerifyStatus;
        }

        public void setVerifyStatus(int VerifyStatus) {
            this.VerifyStatus = VerifyStatus;
        }

        public int getVerfyType() {
            return VerfyType;
        }

        public void setVerfyType(int VerfyType) {
            this.VerfyType = VerfyType;
        }

        public int getPersonType() {
            return PersonType;
        }

        public void setPersonType(int PersonType) {
            this.PersonType = PersonType;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public int getGender() {
            return Gender;
        }

        public void setGender(int Gender) {
            this.Gender = Gender;
        }

        public int getNation() {
            return Nation;
        }

        public void setNation(int Nation) {
            this.Nation = Nation;
        }

        public int getCardType() {
            return CardType;
        }

        public void setCardType(int CardType) {
            this.CardType = CardType;
        }

        public String getIdCard() {
            return IdCard;
        }

        public void setIdCard(String IdCard) {
            this.IdCard = IdCard;
        }

        public String getBirthday() {
            return Birthday;
        }

        public void setBirthday(String Birthday) {
            this.Birthday = Birthday;
        }

        public String getTelnum() {
            return Telnum;
        }

        public void setTelnum(String Telnum) {
            this.Telnum = Telnum;
        }

        public String getNative() {
            return Native;
        }

        public void setNative(String Native) {
            this.Native = Native;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String Address) {
            this.Address = Address;
        }

        public String getNotes() {
            return Notes;
        }

        public void setNotes(String Notes) {
            this.Notes = Notes;
        }

        public int getMjCardFrom() {
            return MjCardFrom;
        }

        public void setMjCardFrom(int MjCardFrom) {
            this.MjCardFrom = MjCardFrom;
        }

        public int getDeviceID() {
            return DeviceID;
        }

        public void setDeviceID(int DeviceID) {
            this.DeviceID = DeviceID;
        }

        public int getMjCardNo() {
            return MjCardNo;
        }

        public void setMjCardNo(int MjCardNo) {
            this.MjCardNo = MjCardNo;
        }

        public int getTempvalid() {
            return Tempvalid;
        }

        public void setTempvalid(int Tempvalid) {
            this.Tempvalid = Tempvalid;
        }

        public int getCustomizeID() {
            return CustomizeID;
        }

        public void setCustomizeID(int CustomizeID) {
            this.CustomizeID = CustomizeID;
        }

        public String getPersonUUID() {
            return PersonUUID;
        }

        public void setPersonUUID(String PersonUUID) {
            this.PersonUUID = PersonUUID;
        }

        public String getValidBegin() {
            return ValidBegin;
        }

        public void setValidBegin(String ValidBegin) {
            this.ValidBegin = ValidBegin;
        }

        public String getValidEnd() {
            return ValidEnd;
        }

        public void setValidEnd(String ValidEnd) {
            this.ValidEnd = ValidEnd;
        }

        public int getSendintime() {
            return Sendintime;
        }

        public void setSendintime(int Sendintime) {
            this.Sendintime = Sendintime;
        }
    }
}
