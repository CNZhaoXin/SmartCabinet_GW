package com.zk.cabinet.entity;

import java.util.List;

public class RequestPostBind {

    /**
     * Post 入库绑定
     * 支持多条数据一起提交，每条数据格式：
     * 参数名 类型 描述
     * posRFID string 库位id
     * rfid string 档案rfid
     */
    // bindType string
    // 业务类型：1-入库；2-移库
    private String bindType;

    private List<RequestPostBindData> data;

    public void setData(List<RequestPostBindData> data) {
        this.data = data;
    }

    public List<RequestPostBindData> getData() {
        return data;
    }

    public String getBindType() {
        return bindType;
    }

    public void setBindType(String bindType) {
        this.bindType = bindType;
    }
}
