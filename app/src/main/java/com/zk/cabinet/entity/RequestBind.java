package com.zk.cabinet.entity;

public class RequestBind {

    /**
     * 27.档案盒绑定库位
     * 接口地址：  post   /busi/baseArchivesBox/bind
     * posCode string 库位编号
     * boxDataId string 档案盒ID
     */
    private String posCode;
    private String boxDataId;

    public String getPosCode() {
        return posCode;
    }

    public void setPosCode(String posCode) {
        this.posCode = posCode;
    }

    public String getBoxDataId() {
        return boxDataId;
    }

    public void setBoxDataId(String boxDataId) {
        this.boxDataId = boxDataId;
    }
}
