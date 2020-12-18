package com.zk.cabinet.entity;

public class LightDebugEntity {
    int deviceID;
    int floor;
    int light;
    boolean isSelected;

    public LightDebugEntity(int deviceID, int floor, int light, boolean isSelected) {
        this.deviceID = deviceID;
        this.floor = floor;
        this.light = light;
        this.isSelected = isSelected;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }


    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }
}
