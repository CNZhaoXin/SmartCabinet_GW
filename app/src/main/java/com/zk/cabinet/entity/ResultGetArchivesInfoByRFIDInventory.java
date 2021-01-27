package com.zk.cabinet.entity;

import java.util.List;

/**
 * 通过RFID集合查询档案详情
 */
public class ResultGetArchivesInfoByRFIDInventory {

    private String msg;
    private int code;
    private List<SearchDossierDetailsDataInventory> data;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setData(List<SearchDossierDetailsDataInventory> data) {
        this.data = data;
    }

    public List<SearchDossierDetailsDataInventory> getData() {
        return data;
    }

}
