package com.zk.cabinet.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class ResultGetBoxListByPosCode {
    
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
        private List<BoxListBean> boxList;
        private List<ArchivesListBean> archivesList;

        public List<BoxListBean> getBoxList() {
            return boxList;
        }

        public void setBoxList(List<BoxListBean> boxList) {
            this.boxList = boxList;
        }

        public List<ArchivesListBean> getArchivesList() {
            return archivesList;
        }

        public void setArchivesList(List<ArchivesListBean> archivesList) {
            this.archivesList = archivesList;
        }

        public static class BoxListBean {
            private Object searchValue;
            private Object createBy;
            private Object createTime;
            private Object updateBy;
            private Object updateTime;
            private Object remark;
            private Object dataScope;
            private ParamsBean params;
            private PosInfoBean posInfo;
            private String id;
            private String houseId;
            private String masteId;
            private String cabinetId;
            private String posId;
            private String posCode;
            private int rowNo;
            private int numNo;
            private String boxDataId;
            private String boxCode;
            private String boxType;
            private String boxStatus;
            private int dataStatus;
            @JSONField(name = "cr eator")
            private String _$CrEator47;// FIXME check this code
            private String creatorName;
            private String created;
            private String modifier;
            private String modifierName;
            private String modified;
            private Object ccol1;
            private Object ccol2;
            private Object ccol3;
            private Object dcol1;
            private Object dcol2;
            private Object datecol1;
            private int typeData;
            private String creator;

            public Object getSearchValue() {
                return searchValue;
            }

            public void setSearchValue(Object searchValue) {
                this.searchValue = searchValue;
            }

            public Object getCreateBy() {
                return createBy;
            }

            public void setCreateBy(Object createBy) {
                this.createBy = createBy;
            }

            public Object getCreateTime() {
                return createTime;
            }

            public void setCreateTime(Object createTime) {
                this.createTime = createTime;
            }

            public Object getUpdateBy() {
                return updateBy;
            }

            public void setUpdateBy(Object updateBy) {
                this.updateBy = updateBy;
            }

            public Object getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(Object updateTime) {
                this.updateTime = updateTime;
            }

            public Object getRemark() {
                return remark;
            }

            public void setRemark(Object remark) {
                this.remark = remark;
            }

            public Object getDataScope() {
                return dataScope;
            }

            public void setDataScope(Object dataScope) {
                this.dataScope = dataScope;
            }

            public ParamsBean getParams() {
                return params;
            }

            public void setParams(ParamsBean params) {
                this.params = params;
            }

            public PosInfoBean getPosInfo() {
                return posInfo;
            }

            public void setPosInfo(PosInfoBean posInfo) {
                this.posInfo = posInfo;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getHouseId() {
                return houseId;
            }

            public void setHouseId(String houseId) {
                this.houseId = houseId;
            }

            public String getMasteId() {
                return masteId;
            }

            public void setMasteId(String masteId) {
                this.masteId = masteId;
            }

            public String getCabinetId() {
                return cabinetId;
            }

            public void setCabinetId(String cabinetId) {
                this.cabinetId = cabinetId;
            }

            public String getPosId() {
                return posId;
            }

            public void setPosId(String posId) {
                this.posId = posId;
            }

            public String getPosCode() {
                return posCode;
            }

            public void setPosCode(String posCode) {
                this.posCode = posCode;
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

            public String getBoxDataId() {
                return boxDataId;
            }

            public void setBoxDataId(String boxDataId) {
                this.boxDataId = boxDataId;
            }

            public String getBoxCode() {
                return boxCode;
            }

            public void setBoxCode(String boxCode) {
                this.boxCode = boxCode;
            }

            public String getBoxType() {
                return boxType;
            }

            public void setBoxType(String boxType) {
                this.boxType = boxType;
            }

            public String getBoxStatus() {
                return boxStatus;
            }

            public void setBoxStatus(String boxStatus) {
                this.boxStatus = boxStatus;
            }

            public int getDataStatus() {
                return dataStatus;
            }

            public void setDataStatus(int dataStatus) {
                this.dataStatus = dataStatus;
            }

            public String get_$CrEator47() {
                return _$CrEator47;
            }

            public void set_$CrEator47(String _$CrEator47) {
                this._$CrEator47 = _$CrEator47;
            }

            public String getCreatorName() {
                return creatorName;
            }

            public void setCreatorName(String creatorName) {
                this.creatorName = creatorName;
            }

            public String getCreated() {
                return created;
            }

            public void setCreated(String created) {
                this.created = created;
            }

            public String getModifier() {
                return modifier;
            }

            public void setModifier(String modifier) {
                this.modifier = modifier;
            }

            public String getModifierName() {
                return modifierName;
            }

            public void setModifierName(String modifierName) {
                this.modifierName = modifierName;
            }

            public String getModified() {
                return modified;
            }

            public void setModified(String modified) {
                this.modified = modified;
            }

            public Object getCcol1() {
                return ccol1;
            }

            public void setCcol1(Object ccol1) {
                this.ccol1 = ccol1;
            }

            public Object getCcol2() {
                return ccol2;
            }

            public void setCcol2(Object ccol2) {
                this.ccol2 = ccol2;
            }

            public Object getCcol3() {
                return ccol3;
            }

            public void setCcol3(Object ccol3) {
                this.ccol3 = ccol3;
            }

            public Object getDcol1() {
                return dcol1;
            }

            public void setDcol1(Object dcol1) {
                this.dcol1 = dcol1;
            }

            public Object getDcol2() {
                return dcol2;
            }

            public void setDcol2(Object dcol2) {
                this.dcol2 = dcol2;
            }

            public Object getDatecol1() {
                return datecol1;
            }

            public void setDatecol1(Object datecol1) {
                this.datecol1 = datecol1;
            }

            public int getTypeData() {
                return typeData;
            }

            public void setTypeData(int typeData) {
                this.typeData = typeData;
            }

            public String getCreator() {
                return creator;
            }

            public void setCreator(String creator) {
                this.creator = creator;
            }

            public static class ParamsBean {
            }

            public static class PosInfoBean {
                private Object searchValue;
                private Object createBy;
                private Object createTime;
                private Object updateBy;
                private Object updateTime;
                private Object remark;
                private Object dataScope;
                private ParamsBean params;
                private BaseArchivesHouseBean baseArchivesHouse;
                private BaseArchivesCabinetBean baseArchivesCabinet;
                private String id;
                private String houseId;
                private String masterId;
                private String cabinetId;
                private String posCode;
                private String rfid;
                private int rowNo;
                private int numNo;
                private int lampNum;
                private int dataStatus;
                private String creator;
                private String creatorName;
                private String created;
                private String modifier;
                private String modifierName;
                private String modified;
                private Object ccol1;
                private Object ccol2;
                private Object ccol3;
                private Object dcol1;
                private Object dcol2;
                private Object datecol1;
                private int typeData;
                private List<Integer> lampList;

                public Object getSearchValue() {
                    return searchValue;
                }

                public void setSearchValue(Object searchValue) {
                    this.searchValue = searchValue;
                }

                public Object getCreateBy() {
                    return createBy;
                }

                public void setCreateBy(Object createBy) {
                    this.createBy = createBy;
                }

                public Object getCreateTime() {
                    return createTime;
                }

                public void setCreateTime(Object createTime) {
                    this.createTime = createTime;
                }

                public Object getUpdateBy() {
                    return updateBy;
                }

                public void setUpdateBy(Object updateBy) {
                    this.updateBy = updateBy;
                }

                public Object getUpdateTime() {
                    return updateTime;
                }

                public void setUpdateTime(Object updateTime) {
                    this.updateTime = updateTime;
                }

                public Object getRemark() {
                    return remark;
                }

                public void setRemark(Object remark) {
                    this.remark = remark;
                }

                public Object getDataScope() {
                    return dataScope;
                }

                public void setDataScope(Object dataScope) {
                    this.dataScope = dataScope;
                }

                public ParamsBean getParams() {
                    return params;
                }

                public void setParams(ParamsBean params) {
                    this.params = params;
                }

                public BaseArchivesHouseBean getBaseArchivesHouse() {
                    return baseArchivesHouse;
                }

                public void setBaseArchivesHouse(BaseArchivesHouseBean baseArchivesHouse) {
                    this.baseArchivesHouse = baseArchivesHouse;
                }

                public BaseArchivesCabinetBean getBaseArchivesCabinet() {
                    return baseArchivesCabinet;
                }

                public void setBaseArchivesCabinet(BaseArchivesCabinetBean baseArchivesCabinet) {
                    this.baseArchivesCabinet = baseArchivesCabinet;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getHouseId() {
                    return houseId;
                }

                public void setHouseId(String houseId) {
                    this.houseId = houseId;
                }

                public String getMasterId() {
                    return masterId;
                }

                public void setMasterId(String masterId) {
                    this.masterId = masterId;
                }

                public String getCabinetId() {
                    return cabinetId;
                }

                public void setCabinetId(String cabinetId) {
                    this.cabinetId = cabinetId;
                }

                public String getPosCode() {
                    return posCode;
                }

                public void setPosCode(String posCode) {
                    this.posCode = posCode;
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

                public int getLampNum() {
                    return lampNum;
                }

                public void setLampNum(int lampNum) {
                    this.lampNum = lampNum;
                }

                public int getDataStatus() {
                    return dataStatus;
                }

                public void setDataStatus(int dataStatus) {
                    this.dataStatus = dataStatus;
                }

                public String getCreator() {
                    return creator;
                }

                public void setCreator(String creator) {
                    this.creator = creator;
                }

                public String getCreatorName() {
                    return creatorName;
                }

                public void setCreatorName(String creatorName) {
                    this.creatorName = creatorName;
                }

                public String getCreated() {
                    return created;
                }

                public void setCreated(String created) {
                    this.created = created;
                }

                public String getModifier() {
                    return modifier;
                }

                public void setModifier(String modifier) {
                    this.modifier = modifier;
                }

                public String getModifierName() {
                    return modifierName;
                }

                public void setModifierName(String modifierName) {
                    this.modifierName = modifierName;
                }

                public String getModified() {
                    return modified;
                }

                public void setModified(String modified) {
                    this.modified = modified;
                }

                public Object getCcol1() {
                    return ccol1;
                }

                public void setCcol1(Object ccol1) {
                    this.ccol1 = ccol1;
                }

                public Object getCcol2() {
                    return ccol2;
                }

                public void setCcol2(Object ccol2) {
                    this.ccol2 = ccol2;
                }

                public Object getCcol3() {
                    return ccol3;
                }

                public void setCcol3(Object ccol3) {
                    this.ccol3 = ccol3;
                }

                public Object getDcol1() {
                    return dcol1;
                }

                public void setDcol1(Object dcol1) {
                    this.dcol1 = dcol1;
                }

                public Object getDcol2() {
                    return dcol2;
                }

                public void setDcol2(Object dcol2) {
                    this.dcol2 = dcol2;
                }

                public Object getDatecol1() {
                    return datecol1;
                }

                public void setDatecol1(Object datecol1) {
                    this.datecol1 = datecol1;
                }

                public int getTypeData() {
                    return typeData;
                }

                public void setTypeData(int typeData) {
                    this.typeData = typeData;
                }

                public List<Integer> getLampList() {
                    return lampList;
                }

                public void setLampList(List<Integer> lampList) {
                    this.lampList = lampList;
                }

                public static class ParamsBean {
                }

                public static class BaseArchivesHouseBean {
                    private Object searchValue;
                    private Object createBy;
                    private Object createTime;
                    private Object updateBy;
                    private Object updateTime;
                    private Object remark;
                    private Object dataScope;
                    private ParamsBean params;
                    private String id;
                    private String code;
                    private String houseNo;
                    private String name;
                    private int dataStatus;
                    private String creator;
                    private String creatorName;
                    private String created;
                    private String modifier;
                    private String modifierName;
                    private String modified;
                    private Object ccol1;
                    private Object ccol2;
                    private Object ccol3;
                    private Object dcol1;
                    private Object dcol2;
                    private Object datecol1;
                    private int typeData;

                    public Object getSearchValue() {
                        return searchValue;
                    }

                    public void setSearchValue(Object searchValue) {
                        this.searchValue = searchValue;
                    }

                    public Object getCreateBy() {
                        return createBy;
                    }

                    public void setCreateBy(Object createBy) {
                        this.createBy = createBy;
                    }

                    public Object getCreateTime() {
                        return createTime;
                    }

                    public void setCreateTime(Object createTime) {
                        this.createTime = createTime;
                    }

                    public Object getUpdateBy() {
                        return updateBy;
                    }

                    public void setUpdateBy(Object updateBy) {
                        this.updateBy = updateBy;
                    }

                    public Object getUpdateTime() {
                        return updateTime;
                    }

                    public void setUpdateTime(Object updateTime) {
                        this.updateTime = updateTime;
                    }

                    public Object getRemark() {
                        return remark;
                    }

                    public void setRemark(Object remark) {
                        this.remark = remark;
                    }

                    public Object getDataScope() {
                        return dataScope;
                    }

                    public void setDataScope(Object dataScope) {
                        this.dataScope = dataScope;
                    }

                    public ParamsBean getParams() {
                        return params;
                    }

                    public void setParams(ParamsBean params) {
                        this.params = params;
                    }

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

                    public int getDataStatus() {
                        return dataStatus;
                    }

                    public void setDataStatus(int dataStatus) {
                        this.dataStatus = dataStatus;
                    }

                    public String getCreator() {
                        return creator;
                    }

                    public void setCreator(String creator) {
                        this.creator = creator;
                    }

                    public String getCreatorName() {
                        return creatorName;
                    }

                    public void setCreatorName(String creatorName) {
                        this.creatorName = creatorName;
                    }

                    public String getCreated() {
                        return created;
                    }

                    public void setCreated(String created) {
                        this.created = created;
                    }

                    public String getModifier() {
                        return modifier;
                    }

                    public void setModifier(String modifier) {
                        this.modifier = modifier;
                    }

                    public String getModifierName() {
                        return modifierName;
                    }

                    public void setModifierName(String modifierName) {
                        this.modifierName = modifierName;
                    }

                    public String getModified() {
                        return modified;
                    }

                    public void setModified(String modified) {
                        this.modified = modified;
                    }

                    public Object getCcol1() {
                        return ccol1;
                    }

                    public void setCcol1(Object ccol1) {
                        this.ccol1 = ccol1;
                    }

                    public Object getCcol2() {
                        return ccol2;
                    }

                    public void setCcol2(Object ccol2) {
                        this.ccol2 = ccol2;
                    }

                    public Object getCcol3() {
                        return ccol3;
                    }

                    public void setCcol3(Object ccol3) {
                        this.ccol3 = ccol3;
                    }

                    public Object getDcol1() {
                        return dcol1;
                    }

                    public void setDcol1(Object dcol1) {
                        this.dcol1 = dcol1;
                    }

                    public Object getDcol2() {
                        return dcol2;
                    }

                    public void setDcol2(Object dcol2) {
                        this.dcol2 = dcol2;
                    }

                    public Object getDatecol1() {
                        return datecol1;
                    }

                    public void setDatecol1(Object datecol1) {
                        this.datecol1 = datecol1;
                    }

                    public int getTypeData() {
                        return typeData;
                    }

                    public void setTypeData(int typeData) {
                        this.typeData = typeData;
                    }

                    public static class ParamsBean {
                    }
                }

                public static class BaseArchivesCabinetBean {
                    private Object searchValue;
                    private Object createBy;
                    private Object createTime;
                    private Object updateBy;
                    private Object updateTime;
                    private Object remark;
                    private Object dataScope;
                    private ParamsBean params;
                    private Object cabinetTotalType;
                    private BaseArchivesHouseBean baseArchivesHouse;
                    private CabineMasterBean cabineMaster;
                    private String id;
                    private String houseId;
                    private String masterId;
                    private String cabinetType;
                    private Object cabinetAttribute;
                    private String attributeNo;
                    private String equipmentId;
                    private String attributeName;
                    private String cabinetSize;
                    private int rowNum;
                    private int lampNum;
                    private int archivesAllowPos;
                    private int capital;
                    private int dataStatus;
                    private String creator;
                    private String creatorName;
                    private String created;
                    private String modifier;
                    private String modifierName;
                    private String modified;
                    private Object ccol1;
                    private Object ccol2;
                    private Object ccol3;
                    private Object dcol1;
                    private Object dcol2;
                    private Object datecol1;
                    private int typeData;

                    public Object getSearchValue() {
                        return searchValue;
                    }

                    public void setSearchValue(Object searchValue) {
                        this.searchValue = searchValue;
                    }

                    public Object getCreateBy() {
                        return createBy;
                    }

                    public void setCreateBy(Object createBy) {
                        this.createBy = createBy;
                    }

                    public Object getCreateTime() {
                        return createTime;
                    }

                    public void setCreateTime(Object createTime) {
                        this.createTime = createTime;
                    }

                    public Object getUpdateBy() {
                        return updateBy;
                    }

                    public void setUpdateBy(Object updateBy) {
                        this.updateBy = updateBy;
                    }

                    public Object getUpdateTime() {
                        return updateTime;
                    }

                    public void setUpdateTime(Object updateTime) {
                        this.updateTime = updateTime;
                    }

                    public Object getRemark() {
                        return remark;
                    }

                    public void setRemark(Object remark) {
                        this.remark = remark;
                    }

                    public Object getDataScope() {
                        return dataScope;
                    }

                    public void setDataScope(Object dataScope) {
                        this.dataScope = dataScope;
                    }

                    public ParamsBean getParams() {
                        return params;
                    }

                    public void setParams(ParamsBean params) {
                        this.params = params;
                    }

                    public Object getCabinetTotalType() {
                        return cabinetTotalType;
                    }

                    public void setCabinetTotalType(Object cabinetTotalType) {
                        this.cabinetTotalType = cabinetTotalType;
                    }

                    public BaseArchivesHouseBean getBaseArchivesHouse() {
                        return baseArchivesHouse;
                    }

                    public void setBaseArchivesHouse(BaseArchivesHouseBean baseArchivesHouse) {
                        this.baseArchivesHouse = baseArchivesHouse;
                    }

                    public CabineMasterBean getCabineMaster() {
                        return cabineMaster;
                    }

                    public void setCabineMaster(CabineMasterBean cabineMaster) {
                        this.cabineMaster = cabineMaster;
                    }

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }

                    public String getHouseId() {
                        return houseId;
                    }

                    public void setHouseId(String houseId) {
                        this.houseId = houseId;
                    }

                    public String getMasterId() {
                        return masterId;
                    }

                    public void setMasterId(String masterId) {
                        this.masterId = masterId;
                    }

                    public String getCabinetType() {
                        return cabinetType;
                    }

                    public void setCabinetType(String cabinetType) {
                        this.cabinetType = cabinetType;
                    }

                    public Object getCabinetAttribute() {
                        return cabinetAttribute;
                    }

                    public void setCabinetAttribute(Object cabinetAttribute) {
                        this.cabinetAttribute = cabinetAttribute;
                    }

                    public String getAttributeNo() {
                        return attributeNo;
                    }

                    public void setAttributeNo(String attributeNo) {
                        this.attributeNo = attributeNo;
                    }

                    public String getEquipmentId() {
                        return equipmentId;
                    }

                    public void setEquipmentId(String equipmentId) {
                        this.equipmentId = equipmentId;
                    }

                    public String getAttributeName() {
                        return attributeName;
                    }

                    public void setAttributeName(String attributeName) {
                        this.attributeName = attributeName;
                    }

                    public String getCabinetSize() {
                        return cabinetSize;
                    }

                    public void setCabinetSize(String cabinetSize) {
                        this.cabinetSize = cabinetSize;
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

                    public int getArchivesAllowPos() {
                        return archivesAllowPos;
                    }

                    public void setArchivesAllowPos(int archivesAllowPos) {
                        this.archivesAllowPos = archivesAllowPos;
                    }

                    public int getCapital() {
                        return capital;
                    }

                    public void setCapital(int capital) {
                        this.capital = capital;
                    }

                    public int getDataStatus() {
                        return dataStatus;
                    }

                    public void setDataStatus(int dataStatus) {
                        this.dataStatus = dataStatus;
                    }

                    public String getCreator() {
                        return creator;
                    }

                    public void setCreator(String creator) {
                        this.creator = creator;
                    }

                    public String getCreatorName() {
                        return creatorName;
                    }

                    public void setCreatorName(String creatorName) {
                        this.creatorName = creatorName;
                    }

                    public String getCreated() {
                        return created;
                    }

                    public void setCreated(String created) {
                        this.created = created;
                    }

                    public String getModifier() {
                        return modifier;
                    }

                    public void setModifier(String modifier) {
                        this.modifier = modifier;
                    }

                    public String getModifierName() {
                        return modifierName;
                    }

                    public void setModifierName(String modifierName) {
                        this.modifierName = modifierName;
                    }

                    public String getModified() {
                        return modified;
                    }

                    public void setModified(String modified) {
                        this.modified = modified;
                    }

                    public Object getCcol1() {
                        return ccol1;
                    }

                    public void setCcol1(Object ccol1) {
                        this.ccol1 = ccol1;
                    }

                    public Object getCcol2() {
                        return ccol2;
                    }

                    public void setCcol2(Object ccol2) {
                        this.ccol2 = ccol2;
                    }

                    public Object getCcol3() {
                        return ccol3;
                    }

                    public void setCcol3(Object ccol3) {
                        this.ccol3 = ccol3;
                    }

                    public Object getDcol1() {
                        return dcol1;
                    }

                    public void setDcol1(Object dcol1) {
                        this.dcol1 = dcol1;
                    }

                    public Object getDcol2() {
                        return dcol2;
                    }

                    public void setDcol2(Object dcol2) {
                        this.dcol2 = dcol2;
                    }

                    public Object getDatecol1() {
                        return datecol1;
                    }

                    public void setDatecol1(Object datecol1) {
                        this.datecol1 = datecol1;
                    }

                    public int getTypeData() {
                        return typeData;
                    }

                    public void setTypeData(int typeData) {
                        this.typeData = typeData;
                    }

                    public static class ParamsBean {
                    }

                    public static class BaseArchivesHouseBean {
                        private Object searchValue;
                        private Object createBy;
                        private Object createTime;
                        private Object updateBy;
                        private Object updateTime;
                        private Object remark;
                        private Object dataScope;
                        private ParamsBean params;
                        private String id;
                        private String code;
                        private String houseNo;
                        private String name;
                        private int dataStatus;
                        private String creator;
                        private String creatorName;
                        private String created;
                        private String modifier;
                        private String modifierName;
                        private String modified;
                        private Object ccol1;
                        private Object ccol2;
                        private Object ccol3;
                        private Object dcol1;
                        private Object dcol2;
                        private Object datecol1;
                        private int typeData;

                        public Object getSearchValue() {
                            return searchValue;
                        }

                        public void setSearchValue(Object searchValue) {
                            this.searchValue = searchValue;
                        }

                        public Object getCreateBy() {
                            return createBy;
                        }

                        public void setCreateBy(Object createBy) {
                            this.createBy = createBy;
                        }

                        public Object getCreateTime() {
                            return createTime;
                        }

                        public void setCreateTime(Object createTime) {
                            this.createTime = createTime;
                        }

                        public Object getUpdateBy() {
                            return updateBy;
                        }

                        public void setUpdateBy(Object updateBy) {
                            this.updateBy = updateBy;
                        }

                        public Object getUpdateTime() {
                            return updateTime;
                        }

                        public void setUpdateTime(Object updateTime) {
                            this.updateTime = updateTime;
                        }

                        public Object getRemark() {
                            return remark;
                        }

                        public void setRemark(Object remark) {
                            this.remark = remark;
                        }

                        public Object getDataScope() {
                            return dataScope;
                        }

                        public void setDataScope(Object dataScope) {
                            this.dataScope = dataScope;
                        }

                        public ParamsBean getParams() {
                            return params;
                        }

                        public void setParams(ParamsBean params) {
                            this.params = params;
                        }

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

                        public int getDataStatus() {
                            return dataStatus;
                        }

                        public void setDataStatus(int dataStatus) {
                            this.dataStatus = dataStatus;
                        }

                        public String getCreator() {
                            return creator;
                        }

                        public void setCreator(String creator) {
                            this.creator = creator;
                        }

                        public String getCreatorName() {
                            return creatorName;
                        }

                        public void setCreatorName(String creatorName) {
                            this.creatorName = creatorName;
                        }

                        public String getCreated() {
                            return created;
                        }

                        public void setCreated(String created) {
                            this.created = created;
                        }

                        public String getModifier() {
                            return modifier;
                        }

                        public void setModifier(String modifier) {
                            this.modifier = modifier;
                        }

                        public String getModifierName() {
                            return modifierName;
                        }

                        public void setModifierName(String modifierName) {
                            this.modifierName = modifierName;
                        }

                        public String getModified() {
                            return modified;
                        }

                        public void setModified(String modified) {
                            this.modified = modified;
                        }

                        public Object getCcol1() {
                            return ccol1;
                        }

                        public void setCcol1(Object ccol1) {
                            this.ccol1 = ccol1;
                        }

                        public Object getCcol2() {
                            return ccol2;
                        }

                        public void setCcol2(Object ccol2) {
                            this.ccol2 = ccol2;
                        }

                        public Object getCcol3() {
                            return ccol3;
                        }

                        public void setCcol3(Object ccol3) {
                            this.ccol3 = ccol3;
                        }

                        public Object getDcol1() {
                            return dcol1;
                        }

                        public void setDcol1(Object dcol1) {
                            this.dcol1 = dcol1;
                        }

                        public Object getDcol2() {
                            return dcol2;
                        }

                        public void setDcol2(Object dcol2) {
                            this.dcol2 = dcol2;
                        }

                        public Object getDatecol1() {
                            return datecol1;
                        }

                        public void setDatecol1(Object datecol1) {
                            this.datecol1 = datecol1;
                        }

                        public int getTypeData() {
                            return typeData;
                        }

                        public void setTypeData(int typeData) {
                            this.typeData = typeData;
                        }

                        public static class ParamsBean {
                        }
                    }

                    public static class CabineMasterBean {
                        private Object searchValue;
                        private Object createBy;
                        private Object createTime;
                        private Object updateBy;
                        private Object updateTime;
                        private Object remark;
                        private Object dataScope;
                        private ParamsBean params;
                        private BaseArchivesHouseBean baseArchivesHouse;
                        private String id;
                        private String houseId;
                        private String name;
                        private String masterSize;
                        private String equipmentId;
                        private String faceEquipmentId;
                        private String code;
                        private String ip;
                        private Object port;
                        private Object deviceId;
                        private int dataStatus;
                        private String creator;
                        private String creatorName;
                        private String created;
                        private String modifier;
                        private String modifierName;
                        private String modified;
                        private String ccol1;
                        private Object ccol2;
                        private Object ccol3;
                        private Object dcol1;
                        private Object dcol2;
                        private Object datecol1;
                        private int typeData;

                        public Object getSearchValue() {
                            return searchValue;
                        }

                        public void setSearchValue(Object searchValue) {
                            this.searchValue = searchValue;
                        }

                        public Object getCreateBy() {
                            return createBy;
                        }

                        public void setCreateBy(Object createBy) {
                            this.createBy = createBy;
                        }

                        public Object getCreateTime() {
                            return createTime;
                        }

                        public void setCreateTime(Object createTime) {
                            this.createTime = createTime;
                        }

                        public Object getUpdateBy() {
                            return updateBy;
                        }

                        public void setUpdateBy(Object updateBy) {
                            this.updateBy = updateBy;
                        }

                        public Object getUpdateTime() {
                            return updateTime;
                        }

                        public void setUpdateTime(Object updateTime) {
                            this.updateTime = updateTime;
                        }

                        public Object getRemark() {
                            return remark;
                        }

                        public void setRemark(Object remark) {
                            this.remark = remark;
                        }

                        public Object getDataScope() {
                            return dataScope;
                        }

                        public void setDataScope(Object dataScope) {
                            this.dataScope = dataScope;
                        }

                        public ParamsBean getParams() {
                            return params;
                        }

                        public void setParams(ParamsBean params) {
                            this.params = params;
                        }

                        public BaseArchivesHouseBean getBaseArchivesHouse() {
                            return baseArchivesHouse;
                        }

                        public void setBaseArchivesHouse(BaseArchivesHouseBean baseArchivesHouse) {
                            this.baseArchivesHouse = baseArchivesHouse;
                        }

                        public String getId() {
                            return id;
                        }

                        public void setId(String id) {
                            this.id = id;
                        }

                        public String getHouseId() {
                            return houseId;
                        }

                        public void setHouseId(String houseId) {
                            this.houseId = houseId;
                        }

                        public String getName() {
                            return name;
                        }

                        public void setName(String name) {
                            this.name = name;
                        }

                        public String getMasterSize() {
                            return masterSize;
                        }

                        public void setMasterSize(String masterSize) {
                            this.masterSize = masterSize;
                        }

                        public String getEquipmentId() {
                            return equipmentId;
                        }

                        public void setEquipmentId(String equipmentId) {
                            this.equipmentId = equipmentId;
                        }

                        public String getFaceEquipmentId() {
                            return faceEquipmentId;
                        }

                        public void setFaceEquipmentId(String faceEquipmentId) {
                            this.faceEquipmentId = faceEquipmentId;
                        }

                        public String getCode() {
                            return code;
                        }

                        public void setCode(String code) {
                            this.code = code;
                        }

                        public String getIp() {
                            return ip;
                        }

                        public void setIp(String ip) {
                            this.ip = ip;
                        }

                        public Object getPort() {
                            return port;
                        }

                        public void setPort(Object port) {
                            this.port = port;
                        }

                        public Object getDeviceId() {
                            return deviceId;
                        }

                        public void setDeviceId(Object deviceId) {
                            this.deviceId = deviceId;
                        }

                        public int getDataStatus() {
                            return dataStatus;
                        }

                        public void setDataStatus(int dataStatus) {
                            this.dataStatus = dataStatus;
                        }

                        public String getCreator() {
                            return creator;
                        }

                        public void setCreator(String creator) {
                            this.creator = creator;
                        }

                        public String getCreatorName() {
                            return creatorName;
                        }

                        public void setCreatorName(String creatorName) {
                            this.creatorName = creatorName;
                        }

                        public String getCreated() {
                            return created;
                        }

                        public void setCreated(String created) {
                            this.created = created;
                        }

                        public String getModifier() {
                            return modifier;
                        }

                        public void setModifier(String modifier) {
                            this.modifier = modifier;
                        }

                        public String getModifierName() {
                            return modifierName;
                        }

                        public void setModifierName(String modifierName) {
                            this.modifierName = modifierName;
                        }

                        public String getModified() {
                            return modified;
                        }

                        public void setModified(String modified) {
                            this.modified = modified;
                        }

                        public String getCcol1() {
                            return ccol1;
                        }

                        public void setCcol1(String ccol1) {
                            this.ccol1 = ccol1;
                        }

                        public Object getCcol2() {
                            return ccol2;
                        }

                        public void setCcol2(Object ccol2) {
                            this.ccol2 = ccol2;
                        }

                        public Object getCcol3() {
                            return ccol3;
                        }

                        public void setCcol3(Object ccol3) {
                            this.ccol3 = ccol3;
                        }

                        public Object getDcol1() {
                            return dcol1;
                        }

                        public void setDcol1(Object dcol1) {
                            this.dcol1 = dcol1;
                        }

                        public Object getDcol2() {
                            return dcol2;
                        }

                        public void setDcol2(Object dcol2) {
                            this.dcol2 = dcol2;
                        }

                        public Object getDatecol1() {
                            return datecol1;
                        }

                        public void setDatecol1(Object datecol1) {
                            this.datecol1 = datecol1;
                        }

                        public int getTypeData() {
                            return typeData;
                        }

                        public void setTypeData(int typeData) {
                            this.typeData = typeData;
                        }

                        public static class ParamsBean {
                        }

                        public static class BaseArchivesHouseBean {
                            private Object searchValue;
                            private Object createBy;
                            private Object createTime;
                            private Object updateBy;
                            private Object updateTime;
                            private Object remark;
                            private Object dataScope;
                            private ParamsBean params;
                            private String id;
                            private String code;
                            private String houseNo;
                            private String name;
                            private int dataStatus;
                            private String creator;
                            private String creatorName;
                            private String created;
                            private String modifier;
                            private String modifierName;
                            private String modified;
                            private Object ccol1;
                            private Object ccol2;
                            private Object ccol3;
                            private Object dcol1;
                            private Object dcol2;
                            private Object datecol1;
                            private int typeData;

                            public Object getSearchValue() {
                                return searchValue;
                            }

                            public void setSearchValue(Object searchValue) {
                                this.searchValue = searchValue;
                            }

                            public Object getCreateBy() {
                                return createBy;
                            }

                            public void setCreateBy(Object createBy) {
                                this.createBy = createBy;
                            }

                            public Object getCreateTime() {
                                return createTime;
                            }

                            public void setCreateTime(Object createTime) {
                                this.createTime = createTime;
                            }

                            public Object getUpdateBy() {
                                return updateBy;
                            }

                            public void setUpdateBy(Object updateBy) {
                                this.updateBy = updateBy;
                            }

                            public Object getUpdateTime() {
                                return updateTime;
                            }

                            public void setUpdateTime(Object updateTime) {
                                this.updateTime = updateTime;
                            }

                            public Object getRemark() {
                                return remark;
                            }

                            public void setRemark(Object remark) {
                                this.remark = remark;
                            }

                            public Object getDataScope() {
                                return dataScope;
                            }

                            public void setDataScope(Object dataScope) {
                                this.dataScope = dataScope;
                            }

                            public ParamsBean getParams() {
                                return params;
                            }

                            public void setParams(ParamsBean params) {
                                this.params = params;
                            }

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

                            public int getDataStatus() {
                                return dataStatus;
                            }

                            public void setDataStatus(int dataStatus) {
                                this.dataStatus = dataStatus;
                            }

                            public String getCreator() {
                                return creator;
                            }

                            public void setCreator(String creator) {
                                this.creator = creator;
                            }

                            public String getCreatorName() {
                                return creatorName;
                            }

                            public void setCreatorName(String creatorName) {
                                this.creatorName = creatorName;
                            }

                            public String getCreated() {
                                return created;
                            }

                            public void setCreated(String created) {
                                this.created = created;
                            }

                            public String getModifier() {
                                return modifier;
                            }

                            public void setModifier(String modifier) {
                                this.modifier = modifier;
                            }

                            public String getModifierName() {
                                return modifierName;
                            }

                            public void setModifierName(String modifierName) {
                                this.modifierName = modifierName;
                            }

                            public String getModified() {
                                return modified;
                            }

                            public void setModified(String modified) {
                                this.modified = modified;
                            }

                            public Object getCcol1() {
                                return ccol1;
                            }

                            public void setCcol1(Object ccol1) {
                                this.ccol1 = ccol1;
                            }

                            public Object getCcol2() {
                                return ccol2;
                            }

                            public void setCcol2(Object ccol2) {
                                this.ccol2 = ccol2;
                            }

                            public Object getCcol3() {
                                return ccol3;
                            }

                            public void setCcol3(Object ccol3) {
                                this.ccol3 = ccol3;
                            }

                            public Object getDcol1() {
                                return dcol1;
                            }

                            public void setDcol1(Object dcol1) {
                                this.dcol1 = dcol1;
                            }

                            public Object getDcol2() {
                                return dcol2;
                            }

                            public void setDcol2(Object dcol2) {
                                this.dcol2 = dcol2;
                            }

                            public Object getDatecol1() {
                                return datecol1;
                            }

                            public void setDatecol1(Object datecol1) {
                                this.datecol1 = datecol1;
                            }

                            public int getTypeData() {
                                return typeData;
                            }

                            public void setTypeData(int typeData) {
                                this.typeData = typeData;
                            }

                            public static class ParamsBean {
                            }
                        }
                    }
                }
            }
        }

        public static class ArchivesListBean {
            private String houseCode;
            private String houseNo;
            private String houseName;
            private Object cabinetId;
            private String cabinetName;
            private String archivesId;
            private Object borrowerDeptName;
            private Object borrowerName;
            private String archivesName;
            private String archivesCode;
            private String archivesNo;
            private String secrecyLevel;
            private String rfid;
            private int rowNo;
            private int numNo;
            private int archivesStatus;
            private Object cabinetType;
            private String masterEquipmentId;
            private String masterName;
            private String cabinetEquipmentId;
            private String codeType;
            private List<Integer> lampList;

            public String getHouseCode() {
                return houseCode;
            }

            public void setHouseCode(String houseCode) {
                this.houseCode = houseCode;
            }

            public String getHouseNo() {
                return houseNo;
            }

            public void setHouseNo(String houseNo) {
                this.houseNo = houseNo;
            }

            public String getHouseName() {
                return houseName;
            }

            public void setHouseName(String houseName) {
                this.houseName = houseName;
            }

            public Object getCabinetId() {
                return cabinetId;
            }

            public void setCabinetId(Object cabinetId) {
                this.cabinetId = cabinetId;
            }

            public String getCabinetName() {
                return cabinetName;
            }

            public void setCabinetName(String cabinetName) {
                this.cabinetName = cabinetName;
            }

            public String getArchivesId() {
                return archivesId;
            }

            public void setArchivesId(String archivesId) {
                this.archivesId = archivesId;
            }

            public Object getBorrowerDeptName() {
                return borrowerDeptName;
            }

            public void setBorrowerDeptName(Object borrowerDeptName) {
                this.borrowerDeptName = borrowerDeptName;
            }

            public Object getBorrowerName() {
                return borrowerName;
            }

            public void setBorrowerName(Object borrowerName) {
                this.borrowerName = borrowerName;
            }

            public String getArchivesName() {
                return archivesName;
            }

            public void setArchivesName(String archivesName) {
                this.archivesName = archivesName;
            }

            public String getArchivesCode() {
                return archivesCode;
            }

            public void setArchivesCode(String archivesCode) {
                this.archivesCode = archivesCode;
            }

            public String getArchivesNo() {
                return archivesNo;
            }

            public void setArchivesNo(String archivesNo) {
                this.archivesNo = archivesNo;
            }

            public String getSecrecyLevel() {
                return secrecyLevel;
            }

            public void setSecrecyLevel(String secrecyLevel) {
                this.secrecyLevel = secrecyLevel;
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

            public int getArchivesStatus() {
                return archivesStatus;
            }

            public void setArchivesStatus(int archivesStatus) {
                this.archivesStatus = archivesStatus;
            }

            public Object getCabinetType() {
                return cabinetType;
            }

            public void setCabinetType(Object cabinetType) {
                this.cabinetType = cabinetType;
            }

            public String getMasterEquipmentId() {
                return masterEquipmentId;
            }

            public void setMasterEquipmentId(String masterEquipmentId) {
                this.masterEquipmentId = masterEquipmentId;
            }

            public String getMasterName() {
                return masterName;
            }

            public void setMasterName(String masterName) {
                this.masterName = masterName;
            }

            public String getCabinetEquipmentId() {
                return cabinetEquipmentId;
            }

            public void setCabinetEquipmentId(String cabinetEquipmentId) {
                this.cabinetEquipmentId = cabinetEquipmentId;
            }

            public String getCodeType() {
                return codeType;
            }

            public void setCodeType(String codeType) {
                this.codeType = codeType;
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
