package com.zk.cabinet.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

@Entity(nameInDb = "User")
public class User {
    @Property(nameInDb = "ID")
    @Id
    private Long id;

    //平台id
    @Expose
    @SerializedName("ID")
    @Property(nameInDb = "WebID")
    @Unique
    private Long webId;

    //用户编号
    @Expose
    @SerializedName("UserCode")
    @Property(nameInDb = "UserCode")
    private String userCode;

    //用户名称
    @Expose
    @SerializedName("UserName")
    @Property(nameInDb = "UserName")
    private String userName;

    //柜子格子编号
    @Expose
    @SerializedName("Cabinet")
    @Property(nameInDb = "Cabinet")
    private String cabinet;

    //用户类型
    @Expose
    @SerializedName("UserType")
    @Property(nameInDb = "UserType")
    private int userType;

    //password
    @Expose
    @SerializedName("Password")
    @Property(nameInDb = "Password")
    private String password;

    //卡号
    @Expose
    @SerializedName("CardID")
    @Property(nameInDb = "CardID")
    private String cardID;

    //指纹信息
    @Expose
    @SerializedName("FingerPrint")
    @Property(nameInDb = "FingerPrint")
    private byte[] fingerPrint;

    //人脸信息
    @Expose
    @SerializedName("FaceInfo")
    @Property(nameInDb = "FaceInfo")
    private String faceInfo;

    //修改时间
    @Expose
    @SerializedName("ModifyTime")
    @Property(nameInDb = "ModifyTime")
    private String modifyTime;

    @Generated(hash = 1547812811)
    public User(Long id, Long webId, String userCode, String userName,
            String cabinet, int userType, String password, String cardID,
            byte[] fingerPrint, String faceInfo, String modifyTime) {
        this.id = id;
        this.webId = webId;
        this.userCode = userCode;
        this.userName = userName;
        this.cabinet = cabinet;
        this.userType = userType;
        this.password = password;
        this.cardID = cardID;
        this.fingerPrint = fingerPrint;
        this.faceInfo = faceInfo;
        this.modifyTime = modifyTime;
    }

    @Generated(hash = 586692638)
    public User() {
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

    public String getCabinet() {
        return this.cabinet;
    }

    public void setCabinet(String cabinet) {
        this.cabinet = cabinet;
    }

    public int getUserType() {
        return this.userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCardID() {
        return this.cardID;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }

    public byte[] getFingerPrint() {
        return this.fingerPrint;
    }

    public void setFingerPrint(byte[] fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    public String getFaceInfo() {
        return this.faceInfo;
    }

    public void setFaceInfo(String faceInfo) {
        this.faceInfo = faceInfo;
    }

    public String getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }


    
}
