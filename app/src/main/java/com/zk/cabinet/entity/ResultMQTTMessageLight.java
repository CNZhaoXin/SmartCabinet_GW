package com.zk.cabinet.entity;

import java.util.List;

/**
 * MQTT 亮灯/灭灯 消息
 */
public class ResultMQTTMessageLight {
        /*{
        "msgType": "2",
            "data": [{
        "masterid": "1e2126706243406cb1dc02a390a785a9",
                "equipmentId": "20201126",
                "name": "组架操作屏1(赵)",
                "cabinetBeanList": [{
            "equipmentId": "20201126-1",
                    "id": "7f1f0af86c044381a23e1d1bdb658f61",
                    "attributeNo": "20201126-1",
                    "attributeName": "组架操作屏1(赵)-档案组架1",
                    "cabinetType": null,
                    "rowNum": 5,
                    "lampNum": 24,
                    "dataList": [{
                "houseCode": null,
                        "houseName": null,
                        "cabinetId": "7f1f0af86c044381a23e1d1bdb658f61",
                        "attributeNo": null,
                        "attributeName": null,
                        "id": "2d9e22c8846c41a0a368f83c086c6f77",
                        "rfid": "KF008-20201126-1-3-1",
                        "rowNo": 3,
                        "numNo": 1,
                        "lampList": [1],
                "archivesList": null
            }, {
                "houseCode": null,
                        "houseName": null,
                        "cabinetId": "7f1f0af86c044381a23e1d1bdb658f61",
                        "attributeNo": null,
                        "attributeName": null,
                        "id": "39d66571e2324d4ca14287601cafda3b",
                        "rfid": "KF008-20201126-1-2-2",
                        "rowNo": 2,
                        "numNo": 2,
                        "lampList": [2],
                "archivesList": null
            }, {
                "houseCode": null,
                        "houseName": null,
                        "cabinetId": "7f1f0af86c044381a23e1d1bdb658f61",
                        "attributeNo": null,
                        "attributeName": null,
                        "id": "95cd556ca9fe438fb74c9035ede299a4",
                        "rfid": "KF008-20201126-1-1-1",
                        "rowNo": 1,
                        "numNo": 1,
                        "lampList": [1],
                "archivesList": null
            }, {
                "houseCode": null,
                        "houseName": null,
                        "cabinetId": "7f1f0af86c044381a23e1d1bdb658f61",
                        "attributeNo": null,
                        "attributeName": null,
                        "id": "b7a181da314242048e8aa917274ce961",
                        "rfid": "KF008-20201126-1-2-1",
                        "rowNo": 2,
                        "numNo": 1,
                        "lampList": [1],
                "archivesList": null
            }]
        }]
    }]
    }*/

    private String msgType;
    private List<DataBean> data;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String masterid;
        private String equipmentId;
        private String name;
        private List<CabinetBeanListBean> cabinetBeanList;

        public String getMasterid() {
            return masterid;
        }

        public void setMasterid(String masterid) {
            this.masterid = masterid;
        }

        public String getEquipmentId() {
            return equipmentId;
        }

        public void setEquipmentId(String equipmentId) {
            this.equipmentId = equipmentId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<CabinetBeanListBean> getCabinetBeanList() {
            return cabinetBeanList;
        }

        public void setCabinetBeanList(List<CabinetBeanListBean> cabinetBeanList) {
            this.cabinetBeanList = cabinetBeanList;
        }

        public static class CabinetBeanListBean {
            private String equipmentId;
            private String id;
            private String attributeNo;
            private String attributeName;
            private Object cabinetType;
            private int rowNum;
            private int lampNum;
            private List<DataListBean> dataList;

            public String getEquipmentId() {
                return equipmentId;
            }

            public void setEquipmentId(String equipmentId) {
                this.equipmentId = equipmentId;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getAttributeNo() {
                return attributeNo;
            }

            public void setAttributeNo(String attributeNo) {
                this.attributeNo = attributeNo;
            }

            public String getAttributeName() {
                return attributeName;
            }

            public void setAttributeName(String attributeName) {
                this.attributeName = attributeName;
            }

            public Object getCabinetType() {
                return cabinetType;
            }

            public void setCabinetType(Object cabinetType) {
                this.cabinetType = cabinetType;
            }

            public int getRowNum() {
                return rowNum;
            }

            public void setRowNum(int rowNum) {
                this.rowNum = rowNum;
            }

            public int getLampNum() {
                return lampNum;
            }

            public void setLampNum(int lampNum) {
                this.lampNum = lampNum;
            }

            public List<DataListBean> getDataList() {
                return dataList;
            }

            public void setDataList(List<DataListBean> dataList) {
                this.dataList = dataList;
            }

            public static class DataListBean {
                private Object houseCode;
                private Object houseName;
                private String cabinetId;
                private Object attributeNo;
                private Object attributeName;
                private String id;
                private String rfid;
                private int rowNo;
                private int numNo;
                private Object archivesList;
                private String masterName; // 操作屏设备名称
                private List<Integer> lampList;

                public String getMasterName() {
                    return masterName;
                }

                public void setMasterName(String masterName) {
                    this.masterName = masterName;
                }

                public Object getHouseCode() {
                    return houseCode;
                }

                public void setHouseCode(Object houseCode) {
                    this.houseCode = houseCode;
                }

                public Object getHouseName() {
                    return houseName;
                }

                public void setHouseName(Object houseName) {
                    this.houseName = houseName;
                }

                public String getCabinetId() {
                    return cabinetId;
                }

                public void setCabinetId(String cabinetId) {
                    this.cabinetId = cabinetId;
                }

                public Object getAttributeNo() {
                    return attributeNo;
                }

                public void setAttributeNo(Object attributeNo) {
                    this.attributeNo = attributeNo;
                }

                public Object getAttributeName() {
                    return attributeName;
                }

                public void setAttributeName(Object attributeName) {
                    this.attributeName = attributeName;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getRfid() {
                    return rfid;
                }

                public void setRfid(String rfid) {
                    this.rfid = rfid;
                }

                public int getRowNo() {
                    return rowNo;
                }

                public void setRowNo(int rowNo) {
                    this.rowNo = rowNo;
                }

                public int getNumNo() {
                    return numNo;
                }

                public void setNumNo(int numNo) {
                    this.numNo = numNo;
                }

                public Object getArchivesList() {
                    return archivesList;
                }

                public void setArchivesList(Object archivesList) {
                    this.archivesList = archivesList;
                }

                public List<Integer> getLampList() {
                    return lampList;
                }

                public void setLampList(List<Integer> lampList) {
                    this.lampList = lampList;
                }
            }
        }
    }
}
