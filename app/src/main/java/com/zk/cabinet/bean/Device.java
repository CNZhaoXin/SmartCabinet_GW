package com.zk.cabinet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "Device")
public class Device {
    @Property(nameInDb = "ID")
    @Id
    private Long id;

    @Property(nameInDb = "DeviceId")
    private String deviceId;

    @Property(nameInDb = "DeviceName")
    private String deviceName;

    @Generated(hash = 789884766)
    public Device(Long id, String deviceId, String deviceName) {
        this.id = id;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }

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

}
