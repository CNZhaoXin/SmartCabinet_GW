package com.zk.cabinet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "LightControlRecord")
public class LightControlRecord {
    @Property(nameInDb = "ID")
    @Id
    private Long id;

    // 档案架:灯控板ID-层-灯, 档案柜:读写器设备ID
    @Property(nameInDb = "DeviceID")
    @NotNull
    private String deviceID;

    // 档案架:灯控板ID-层-灯, 档案柜:读写器设备ID-层-灯
    @Property(nameInDb = "Record")
    @NotNull
    private String record;

    // 亮灯次数累计和
    @Property(nameInDb = "Num")
    @NotNull
    private int num;

    @Generated(hash = 221477505)
    public LightControlRecord(Long id, @NotNull String deviceID, @NotNull String record, int num) {
        this.id = id;
        this.deviceID = deviceID;
        this.record = record;
        this.num = num;
    }

    @Generated(hash = 1254462100)
    public LightControlRecord() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
