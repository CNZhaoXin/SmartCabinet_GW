package com.zk.cabinet.entity;

public class ResultGetAppInfo {
    /* {
       "msg": "操作成功",
           "code": 200,
           "data": {
       "searchValue": null,
               "createBy": null,
               "createTime": null,
               "updateBy": null,
               "updateTime": null,
               "remark": null,
               "dataScope": null,
               "params": {},
       "fileInfo": {
           "searchValue": null,
                   "createBy": null,
                   "createTime": null,
                   "updateBy": null,
                   "updateTime": null,
                   "remark": null,
                   "dataScope": null,
                   "params": {},
           "id": "f4a27c0a75fd4537b722910a28c851f9",
                   "householderId": null,
                   "houseId": null,
                   "modeId": null,
                   "dataid": null,
                   "subDataid": null,
                   "attachmentSort": null,
                   "attachmentType": "other",
                   "fileLabel": null,
                   "fileName": "cb6c6c8cc4c919d56c6a6d246218ca18.png",
                   "fileShowName": "shexiangtou.png",
                   "fileSize": 2,
                   "localFilePath": "\/root\/zng\/uploadPath\/2020\/12\/16\/cb6c6c8cc4c919d56c6a6d246218ca18.png",
                   "bakServerType": null,
                   "bakServerName": null,
                   "bakAddr": null,
                   "fileHandleStatus": null,
                   "clientType": null,
                   "sourceIdentifier": null,
                   "created": "2020-12-16T10:59:25.000+0800",
                   "sortValue": null,
                   "url": "http:\/\/118.25.102.226:11002\/files\/2020\/12\/16\/cb6c6c8cc4c919d56c6a6d246218ca18.png",
                   "thumbnailImgId": null,
                   "thumbnailImgUrl": null
       },
       "id": "6a4a61d53aa2424ca68f306b5438a7a8",
               "appName": "4",
               "versionName": "2",
               "version": "3",
               "content": "1",
               "updateDate": null,
               "fileId": "f4a27c0a75fd4537b722910a28c851f9",
               "dataStatus": 1,
               "creator": "1",
               "creatorName": "admin",
               "created": "2020-12-16 11:01:14",
               "modifier": "1",
               "modifierName": "admin",
               "modified": "2020-12-16 11:01:14",
               "ccol1": null,
               "ccol2": null,
               "ccol3": null,
               "dcol1": null,
               "dcol2": null,
               "datecol1": null,
               "typeData": 1
   }
   }*/

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
        private Object searchValue;
        private Object createBy;
        private Object createTime;
        private Object updateBy;
        private Object updateTime;
        private Object remark;
        private Object dataScope;
        private ParamsBean params;
        private FileInfoBean fileInfo;
        private String id;
        private String appName;
        private String versionName;
        private String version;
        private String content;
        private Object updateDate;
        private String fileId;
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

        public FileInfoBean getFileInfo() {
            return fileInfo;
        }

        public void setFileInfo(FileInfoBean fileInfo) {
            this.fileInfo = fileInfo;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Object getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(Object updateDate) {
            this.updateDate = updateDate;
        }

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
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

        public static class FileInfoBean {
            private Object searchValue;
            private Object createBy;
            private Object createTime;
            private Object updateBy;
            private Object updateTime;
            private Object remark;
            private Object dataScope;
            private ParamsBean params;
            private String id;
            private Object householderId;
            private Object houseId;
            private Object modeId;
            private Object dataid;
            private Object subDataid;
            private Object attachmentSort;
            private String attachmentType;
            private Object fileLabel;
            private String fileName;
            private String fileShowName;
            private int fileSize;
            private String localFilePath;
            private Object bakServerType;
            private Object bakServerName;
            private Object bakAddr;
            private Object fileHandleStatus;
            private Object clientType;
            private Object sourceIdentifier;
            private String created;
            private Object sortValue;
            private String url;
            private Object thumbnailImgId;
            private Object thumbnailImgUrl;

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

            public Object getHouseholderId() {
                return householderId;
            }

            public void setHouseholderId(Object householderId) {
                this.householderId = householderId;
            }

            public Object getHouseId() {
                return houseId;
            }

            public void setHouseId(Object houseId) {
                this.houseId = houseId;
            }

            public Object getModeId() {
                return modeId;
            }

            public void setModeId(Object modeId) {
                this.modeId = modeId;
            }

            public Object getDataid() {
                return dataid;
            }

            public void setDataid(Object dataid) {
                this.dataid = dataid;
            }

            public Object getSubDataid() {
                return subDataid;
            }

            public void setSubDataid(Object subDataid) {
                this.subDataid = subDataid;
            }

            public Object getAttachmentSort() {
                return attachmentSort;
            }

            public void setAttachmentSort(Object attachmentSort) {
                this.attachmentSort = attachmentSort;
            }

            public String getAttachmentType() {
                return attachmentType;
            }

            public void setAttachmentType(String attachmentType) {
                this.attachmentType = attachmentType;
            }

            public Object getFileLabel() {
                return fileLabel;
            }

            public void setFileLabel(Object fileLabel) {
                this.fileLabel = fileLabel;
            }

            public String getFileName() {
                return fileName;
            }

            public void setFileName(String fileName) {
                this.fileName = fileName;
            }

            public String getFileShowName() {
                return fileShowName;
            }

            public void setFileShowName(String fileShowName) {
                this.fileShowName = fileShowName;
            }

            public int getFileSize() {
                return fileSize;
            }

            public void setFileSize(int fileSize) {
                this.fileSize = fileSize;
            }

            public String getLocalFilePath() {
                return localFilePath;
            }

            public void setLocalFilePath(String localFilePath) {
                this.localFilePath = localFilePath;
            }

            public Object getBakServerType() {
                return bakServerType;
            }

            public void setBakServerType(Object bakServerType) {
                this.bakServerType = bakServerType;
            }

            public Object getBakServerName() {
                return bakServerName;
            }

            public void setBakServerName(Object bakServerName) {
                this.bakServerName = bakServerName;
            }

            public Object getBakAddr() {
                return bakAddr;
            }

            public void setBakAddr(Object bakAddr) {
                this.bakAddr = bakAddr;
            }

            public Object getFileHandleStatus() {
                return fileHandleStatus;
            }

            public void setFileHandleStatus(Object fileHandleStatus) {
                this.fileHandleStatus = fileHandleStatus;
            }

            public Object getClientType() {
                return clientType;
            }

            public void setClientType(Object clientType) {
                this.clientType = clientType;
            }

            public Object getSourceIdentifier() {
                return sourceIdentifier;
            }

            public void setSourceIdentifier(Object sourceIdentifier) {
                this.sourceIdentifier = sourceIdentifier;
            }

            public String getCreated() {
                return created;
            }

            public void setCreated(String created) {
                this.created = created;
            }

            public Object getSortValue() {
                return sortValue;
            }

            public void setSortValue(Object sortValue) {
                this.sortValue = sortValue;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public Object getThumbnailImgId() {
                return thumbnailImgId;
            }

            public void setThumbnailImgId(Object thumbnailImgId) {
                this.thumbnailImgId = thumbnailImgId;
            }

            public Object getThumbnailImgUrl() {
                return thumbnailImgUrl;
            }

            public void setThumbnailImgUrl(Object thumbnailImgUrl) {
                this.thumbnailImgUrl = thumbnailImgUrl;
            }

            public static class ParamsBean {
            }
        }
    }

}
