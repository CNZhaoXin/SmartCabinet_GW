package com.zk.cabinet.bean;

import com.zk.rfid.bean.LabelInfo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.ArrayList;

@Entity(nameInDb = "Cabinet")
public class Cabinet {
    @Property(nameInDb = "ID")
    @Id
    private Long id;

    @Property(nameInDb = "DeviceId")
    private String deviceId;

    @Property(nameInDb = "Floor")
    @NotNull
    private int floor;

    @Property(nameInDb = "Position")
    @NotNull
    private int position;

    @Property(nameInDb = "Proportion")
    @NotNull
    private int proportion;

    @Transient
    private ArrayList<LabelInfo> labelInfoList;

    @Transient
    private long elementCount;

    @Generated(hash = 1062215190)
    public Cabinet(Long id, String deviceId, int floor, int position,
            int proportion) {
        this.id = id;
        this.deviceId = deviceId;
        this.floor = floor;
        this.position = position;
        this.proportion = proportion;
    }

    @Generated(hash = 456667810)
    public Cabinet() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getFloor() {
        return this.floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getProportion() {
        return this.proportion;
    }

    public void setProportion(int proportion) {
        this.proportion = proportion;
    }

    public ArrayList<LabelInfo> getLabelInfoList() {
        return labelInfoList;
    }

    public void setLabelInfoList(ArrayList<LabelInfo> labelInfoList) {
        this.labelInfoList = labelInfoList;
    }

    public long getElementCount() {
        return elementCount;
    }

    public void setElementCount(long elementCount) {
        this.elementCount = elementCount;
    }
}
