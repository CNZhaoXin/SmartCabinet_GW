package com.zk.cabinet.entity;

import java.io.Serializable;
import java.util.List;

public class ResultGetHouseList implements Serializable {

//    {
//        "msg": "操作成功",
//            "code": 200,
//            "data": [
//        {
//            "id": "67d30f032fbe4847946e011812b9b1c5",
//                "code": "y0001",
//                "houseNo": "001",
//                "name": "一号档案室"
//        }]
//    }

    private String msg;
    private int code;
    private List<ResultGetHouseList.DataBean> data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        private String id;
        private String code;
        private String houseNo;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getHouseNo() {
            return houseNo;
        }

        public void setHouseNo(String houseNo) {
            this.houseNo = houseNo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
