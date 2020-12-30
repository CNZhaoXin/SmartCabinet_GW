package com.zk.cabinet.entity;

import com.contrarywind.interfaces.IPickerViewData;

import java.util.List;

public class ResultGetAllCodeType {

    private String msg;
    private int code;
    private List<Data> data;

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

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {
        private int archivesNum;
        private Info info;
        private List<SubList> subList;

        public int getArchivesNum() {
            return archivesNum;
        }

        public void setArchivesNum(int archivesNum) {
            this.archivesNum = archivesNum;
        }

        public Info getInfo() {
            return info;
        }

        public void setInfo(Info info) {
            this.info = info;
        }

        public List<SubList> getSubList() {
            return subList;
        }

        public void setSubList(List<SubList> subList) {
            this.subList = subList;
        }

        public static class Info implements IPickerViewData {
            private Object searchValue;
            private Object createBy;
            private Object createTime;
            private Object updateBy;
            private Object updateTime;
            private Object remark;
            private Object dataScope;
            private Params params;
            private Object id;
            private String typeName;
            private String typeCode;
            private Object parentCode;
            private Object sortValue;
            private Object dataStatus;
            private Object creator;
            private Object creatorName;
            private Object created;
            private Object modifier;
            private Object modifierName;
            private Object modified;
            private Object ccol1;
            private Object ccol2;
            private Object ccol3;
            private Object dcol1;
            private Object dcol2;
            private Object datecol1;
            private Object typeData;

            @Override
            public String getPickerViewText() {
                return typeName;
            }

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

            public Params getParams() {
                return params;
            }

            public void setParams(Params params) {
                this.params = params;
            }

            public Object getId() {
                return id;
            }

            public void setId(Object id) {
                this.id = id;
            }

            public String getTypeName() {
                return typeName;
            }

            public void setTypeName(String typeName) {
                this.typeName = typeName;
            }

            public String getTypeCode() {
                return typeCode;
            }

            public void setTypeCode(String typeCode) {
                this.typeCode = typeCode;
            }

            public Object getParentCode() {
                return parentCode;
            }

            public void setParentCode(Object parentCode) {
                this.parentCode = parentCode;
            }

            public Object getSortValue() {
                return sortValue;
            }

            public void setSortValue(Object sortValue) {
                this.sortValue = sortValue;
            }

            public Object getDataStatus() {
                return dataStatus;
            }

            public void setDataStatus(Object dataStatus) {
                this.dataStatus = dataStatus;
            }

            public Object getCreator() {
                return creator;
            }

            public void setCreator(Object creator) {
                this.creator = creator;
            }

            public Object getCreatorName() {
                return creatorName;
            }

            public void setCreatorName(Object creatorName) {
                this.creatorName = creatorName;
            }

            public Object getCreated() {
                return created;
            }

            public void setCreated(Object created) {
                this.created = created;
            }

            public Object getModifier() {
                return modifier;
            }

            public void setModifier(Object modifier) {
                this.modifier = modifier;
            }

            public Object getModifierName() {
                return modifierName;
            }

            public void setModifierName(Object modifierName) {
                this.modifierName = modifierName;
            }

            public Object getModified() {
                return modified;
            }

            public void setModified(Object modified) {
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

            public Object getTypeData() {
                return typeData;
            }

            public void setTypeData(Object typeData) {
                this.typeData = typeData;
            }

            public static class Params {
            }
        }

        public static class SubList {
            private int archivesNum;
            private Info info;
            private List<SubList> subList;

            public int getArchivesNum() {
                return archivesNum;
            }

            public void setArchivesNum(int archivesNum) {
                this.archivesNum = archivesNum;
            }

            public Info getInfo() {
                return info;
            }

            public void setInfo(Info info) {
                this.info = info;
            }

            public List<SubList> getSubList() {
                return subList;
            }

            public void setSubList(List<SubList> subList) {
                this.subList = subList;
            }

            public static class Info implements IPickerViewData {
                private Object searchValue;
                private Object createBy;
                private Object createTime;
                private Object updateBy;
                private Object updateTime;
                private Object remark;
                private Object dataScope;
                private Params params;
                private String id;
                private String typeName;
                private String typeCode;
                private String parentCode;
                private Object sortValue;
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

                @Override
                public String getPickerViewText() {
                    return typeName;
                }

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

                public Params getParams() {
                    return params;
                }

                public void setParams(Params params) {
                    this.params = params;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getTypeName() {
                    return typeName;
                }

                public void setTypeName(String typeName) {
                    this.typeName = typeName;
                }

                public String getTypeCode() {
                    return typeCode;
                }

                public void setTypeCode(String typeCode) {
                    this.typeCode = typeCode;
                }

                public String getParentCode() {
                    return parentCode;
                }

                public void setParentCode(String parentCode) {
                    this.parentCode = parentCode;
                }

                public Object getSortValue() {
                    return sortValue;
                }

                public void setSortValue(Object sortValue) {
                    this.sortValue = sortValue;
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

                public static class Params {
                }
            }

        }
    }
}