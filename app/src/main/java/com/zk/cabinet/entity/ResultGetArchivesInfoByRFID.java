package com.zk.cabinet.entity;

import java.util.List;

/**
 * 通过RFID集合查询档案详情
 */
public class ResultGetArchivesInfoByRFID {

    private String msg;
    private int code;
    private List<SearchDossierDetailsData> data;

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

    public void setData(List<SearchDossierDetailsData> data) {
        this.data = data;
    }

    public List<SearchDossierDetailsData> getData() {
        return data;
    }

}
