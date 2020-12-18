package com.zk.cabinet.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 档案查询结果
 */
public class ResultSearchArchivesInfo {

    private String msg;
    private int code;
    private Data data;

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

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public class Data {
        private int total;
        private ArrayList<FileDetailsData> rows;
        private int code;
        private String msg;

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotal() {
            return total;
        }

        public void setRows(ArrayList<FileDetailsData> rows) {
            this.rows = rows;
        }

        public List<FileDetailsData> getRows() {
            return rows;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

    }
}
