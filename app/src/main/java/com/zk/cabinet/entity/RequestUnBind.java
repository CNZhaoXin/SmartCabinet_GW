package com.zk.cabinet.entity;

public class RequestUnBind {

    /**
     * 28.档案盒解绑库位
     * 接口地址：  post   /busi/baseArchivesBox/unBind
     * dataValue string 档案盒ID
     */
    private String dataValue;

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }
}
