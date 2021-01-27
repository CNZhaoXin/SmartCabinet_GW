package com.zk.cabinet.entity;

import java.util.List;

public class ResultPDAInventoryDifference {

    private String msg;
    private int code;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int total;
        private int code;
        private String msg;
        private List<RowsBean> rows;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<RowsBean> getRows() {
            return rows;
        }

        public void setRows(List<RowsBean> rows) {
            this.rows = rows;
        }

        public static class RowsBean {
            private String houseIdOld;
            private String houseNameOld;
            private String houseCodeOld;
            private String cabinetIdOld;
            private String cabinetAttributeNoOld;
            private String cabinetNameOld;
            private String posIdOld;
            private int posRowNoOld;
            private int posNumNoOld;
            private String posRfidOld;
            private String houseIdNow;
            private String houseNameNow;
            private String houseCodeNow;
            private String cabinetIdNow;
            private String cabinetAttributeNoNow;
            private String cabinetNameNow;
            private String posIdNow;
            private int posRowNoNow;
            private int posNumNoNow;
            private String posRfidNow;
            private String archivesCode;
            private String codeType;
            private String rfid;
            private String archivesName;
            private String archivesNo;
            private int archivesStatus;

            public String getHouseIdOld() {
                return houseIdOld;
            }

            public void setHouseIdOld(String houseIdOld) {
                this.houseIdOld = houseIdOld;
            }

            public String getHouseNameOld() {
                return houseNameOld;
            }

            public void setHouseNameOld(String houseNameOld) {
                this.houseNameOld = houseNameOld;
            }

            public String getHouseCodeOld() {
                return houseCodeOld;
            }

            public void setHouseCodeOld(String houseCodeOld) {
                this.houseCodeOld = houseCodeOld;
            }

            public String getCabinetIdOld() {
                return cabinetIdOld;
            }

            public void setCabinetIdOld(String cabinetIdOld) {
                this.cabinetIdOld = cabinetIdOld;
            }

            public String getCabinetAttributeNoOld() {
                return cabinetAttributeNoOld;
            }

            public void setCabinetAttributeNoOld(String cabinetAttributeNoOld) {
                this.cabinetAttributeNoOld = cabinetAttributeNoOld;
            }

            public String getCabinetNameOld() {
                return cabinetNameOld;
            }

            public void setCabinetNameOld(String cabinetNameOld) {
                this.cabinetNameOld = cabinetNameOld;
            }

            public String getPosIdOld() {
                return posIdOld;
            }

            public void setPosIdOld(String posIdOld) {
                this.posIdOld = posIdOld;
            }

            public int getPosRowNoOld() {
                return posRowNoOld;
            }

            public void setPosRowNoOld(int posRowNoOld) {
                this.posRowNoOld = posRowNoOld;
            }

            public int getPosNumNoOld() {
                return posNumNoOld;
            }

            public void setPosNumNoOld(int posNumNoOld) {
                this.posNumNoOld = posNumNoOld;
            }

            public String getPosRfidOld() {
                return posRfidOld;
            }

            public void setPosRfidOld(String posRfidOld) {
                this.posRfidOld = posRfidOld;
            }

            public String getHouseIdNow() {
                return houseIdNow;
            }

            public void setHouseIdNow(String houseIdNow) {
                this.houseIdNow = houseIdNow;
            }

            public String getHouseNameNow() {
                return houseNameNow;
            }

            public void setHouseNameNow(String houseNameNow) {
                this.houseNameNow = houseNameNow;
            }

            public String getHouseCodeNow() {
                return houseCodeNow;
            }

            public void setHouseCodeNow(String houseCodeNow) {
                this.houseCodeNow = houseCodeNow;
            }

            public String getCabinetIdNow() {
                return cabinetIdNow;
            }

            public void setCabinetIdNow(String cabinetIdNow) {
                this.cabinetIdNow = cabinetIdNow;
            }

            public String getCabinetAttributeNoNow() {
                return cabinetAttributeNoNow;
            }

            public void setCabinetAttributeNoNow(String cabinetAttributeNoNow) {
                this.cabinetAttributeNoNow = cabinetAttributeNoNow;
            }

            public String getCabinetNameNow() {
                return cabinetNameNow;
            }

            public void setCabinetNameNow(String cabinetNameNow) {
                this.cabinetNameNow = cabinetNameNow;
            }

            public String getPosIdNow() {
                return posIdNow;
            }

            public void setPosIdNow(String posIdNow) {
                this.posIdNow = posIdNow;
            }

            public int getPosRowNoNow() {
                return posRowNoNow;
            }

            public void setPosRowNoNow(int posRowNoNow) {
                this.posRowNoNow = posRowNoNow;
            }

            public int getPosNumNoNow() {
                return posNumNoNow;
            }

            public void setPosNumNoNow(int posNumNoNow) {
                this.posNumNoNow = posNumNoNow;
            }

            public String getPosRfidNow() {
                return posRfidNow;
            }

            public void setPosRfidNow(String posRfidNow) {
                this.posRfidNow = posRfidNow;
            }

            public String getArchivesCode() {
                return archivesCode;
            }

            public void setArchivesCode(String archivesCode) {
                this.archivesCode = archivesCode;
            }

            public String getCodeType() {
                return codeType;
            }

            public void setCodeType(String codeType) {
                this.codeType = codeType;
            }

            public String getRfid() {
                return rfid;
            }

            public void setRfid(String rfid) {
                this.rfid = rfid;
            }

            public String getArchivesName() {
                return archivesName;
            }

            public void setArchivesName(String archivesName) {
                this.archivesName = archivesName;
            }

            public String getArchivesNo() {
                return archivesNo;
            }

            public void setArchivesNo(String archivesNo) {
                this.archivesNo = archivesNo;
            }

            public int getArchivesStatus() {
                return archivesStatus;
            }

            public void setArchivesStatus(int archivesStatus) {
                this.archivesStatus = archivesStatus;
            }
        }
    }
}
