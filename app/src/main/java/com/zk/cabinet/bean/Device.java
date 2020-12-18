package com.zk.cabinet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

@Entity(nameInDb = "Device")
public class Device {
    @Property(nameInDb = "ID")
    @Id
    private Long id;

    @Property(nameInDb = "DeviceId")
    private String deviceId;

    @Property(nameInDb = "DeviceName")
    private String deviceName;

    @Property(nameInDb = "LightControlBoardId")
    private String lightControlBoardId;

    @Generated(hash = 1769929908)
    public Device(Long id, String deviceId, String deviceName,
            String lightControlBoardId) {
        this.id = id;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.lightControlBoardId = lightControlBoardId;
    }

    @Transient
    private boolean selected;

    @Generated(hash = 1469582394)
    public Device() {
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

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getLightControlBoardId() {
        return this.lightControlBoardId;
    }

    public void setLightControlBoardId(String lightControlBoardId) {
        this.lightControlBoardId = lightControlBoardId;
    }
}
