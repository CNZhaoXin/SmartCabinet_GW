package com.zk.cabinet.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

@Entity(nameInDb = "Dossier")
public class Dossier {
    //本地id
    @Property(nameInDb = "ID")
    @Id
    private Long id;

    //平台id
    @Expose
    @SerializedName("ID")
    @Property(nameInDb = "WebID")
    @Unique
    private Long webId;

    //案件编号
    @Expose
    @SerializedName("CaseCode")
    @Property(nameInDb = "CaseCode")
    private String caseCode;

    //案件名称
    @Expose
    @SerializedName("CaseName")
    @Property(nameInDb = "CaseName")
    private String caseName;

    //主办人ID
    @Expose
    @SerializedName("UserID")
    @Property(nameInDb = "UserID")
    private Long userID;

    //主办人Code
    @Expose
    @SerializedName("UserCode")
    @Property(nameInDb = "UserCode")
    private String userCode;

    //主办人名称
    @Expose
    @SerializedName("UserName")
    @Property(nameInDb = "UserName")
    private String userName;

    //单位编号
    @Expose
    @SerializedName("CorpCode")
    @Property(nameInDb = "CorpCode")
    private String corpCode;

    //单位名称
    @Expose
    @SerializedName("CorpName")
    @Property(nameInDb = "CorpName")
    private String corpName;

    //受理时间
    @Expose
    @SerializedName("ReceptionTime")
    @Property(nameInDb = "ReceptionTime")
    private String receptionTime;

    //修改时间
    @Expose
    @SerializedName("ModifyTime")
    @Property(nameInDb = "ModifyTime")
    private String modifyTime;

    //标签编号
    @Expose
    @SerializedName("EPC")
    @Property(nameInDb = "EPC")
    private String epc;

    //案件状态
    @Expose
    @SerializedName("CaseState")
    @Property(nameInDb = "CaseState")
    private String caseState;

    //柜子编号
    @Expose
    @SerializedName("DeviceCode")
    @Property(nameInDb = "DeviceCode")
    private String DeviceCode;

    //整个柜体的唯一值
    @SerializedName("CellID")
    @Property(nameInDb = "CellID")
    private int cellId;

    //A,B,C
    @SerializedName("CellName")
    @Property(nameInDb = "CellName")
    private String cellName;

    //1-12
    @SerializedName("CellCode")
    @Property(nameInDb = "CellCode")
    private int cellCode;

    @Generated(hash = 1928736797)
    public Dossier(Long id, Long webId, String caseCode, String caseName,
            Long userID, String userCode, String userName, String corpCode,
            String corpName, String receptionTime, String modifyTime, String epc,
            String caseState, String DeviceCode, int cellId, String cellName,
            int cellCode) {
        this.id = id;
        this.webId = webId;
        this.caseCode = caseCode;
        this.caseName = caseName;
        this.userID = userID;
        this.userCode = userCode;
        this.userName = userName;
        this.corpCode = corpCode;
        this.corpName = corpName;
        this.receptionTime = receptionTime;
        this.modifyTime = modifyTime;
        this.epc = epc;
        this.caseState = caseState;
        this.DeviceCode = DeviceCode;
        this.cellId = cellId;
        this.cellName = cellName;
        this.cellCode = cellCode;
    }

    @Generated(hash = 1494899943)
    public Dossier() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWebId() {
        return this.webId;
    }

    public void setWebId(Long webId) {
        this.webId = webId;
    }

    public String getCaseCode() {
        return this.caseCode;
    }

    public void setCaseCode(String caseCode) {
        this.caseCode = caseCode;
    }

    public String getCaseName() {
        return this.caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public Long getUserID() {
        return this.userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCorpCode() {
        return this.corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
    }

    public String getCorpName() {
        return this.corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getReceptionTime() {
        return this.receptionTime;
    }

    public void setReceptionTime(String receptionTime) {
        this.receptionTime = receptionTime;
    }

    public String getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getEpc() {
        return this.epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getCaseState() {
        return this.caseState;
    }

    public void setCaseState(String caseState) {
        this.caseState = caseState;
    }

    public String getDeviceCode() {
        return this.DeviceCode;
    }

    public void setDeviceCode(String DeviceCode) {
        this.DeviceCode = DeviceCode;
    }

    public int getCellId() {
        return this.cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public String getCellName() {
        return this.cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    public int getCellCode() {
        return this.cellCode;
    }

    public void setCellCode(int cellCode) {
        this.cellCode = cellCode;
    }

}
