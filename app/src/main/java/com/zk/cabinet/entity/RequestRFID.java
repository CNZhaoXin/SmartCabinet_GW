package com.zk.cabinet.entity;

public class RequestRFID {

    public RequestRFID(String rfid, String posRfid) {
        this.rfid = rfid;
        this.posRfid = posRfid;
    }

    /** RFID */
    private String rfid;

    /** 档案柜库位编号*/
    private String posRfid;

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getPosRfid() {
        return posRfid;
    }

    public void setPosRfid(String posRfid) {
        this.posRfid = posRfid;
    }
}
