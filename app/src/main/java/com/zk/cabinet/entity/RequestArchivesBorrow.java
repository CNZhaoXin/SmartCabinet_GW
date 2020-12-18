package com.zk.cabinet.entity;

public class RequestArchivesBorrow {

    private String aioId; // 一体机或者智能柜id
    private String[] rfids; // 档案rfid集合

    public String getAioId() {
        return aioId;
    }

    public void setAioId(String aioId) {
        this.aioId = aioId;
    }

    public String[] getRfids() {
        return rfids;
    }

    public void setRfids(String[] rfids) {
        this.rfids = rfids;
    }
}
