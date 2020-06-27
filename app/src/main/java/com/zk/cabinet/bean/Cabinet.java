package com.zk.cabinet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

@Entity(nameInDb = "Cabinet")
public class Cabinet {
    @Property(nameInDb = "ID")
    @Id
    private Long id;

    //整个柜体的唯一值
    @Property(nameInDb = "CellID")
    @NotNull
    @Unique
    private int cellId;

    //A,B,C
    @Property(nameInDb = "CellName")
    @NotNull
    private String cellName;

    //1-12
    @Property(nameInDb = "CellCode")
    @NotNull
    private int cellCode;

    @Property(nameInDb = "Proportion")
    @NotNull
    private int proportion;

    //0：正常 1：损坏
    @Property(nameInDb = "SignBroken")
    @NotNull
    private int signBroken;

    // 箱体内部元素计数，不用于数据库对应生成
    // 初始值-1表示无权限
    @Transient
    private long elementCount;

    @Generated(hash = 845378057)
    public Cabinet(Long id, int cellId, @NotNull String cellName, int cellCode,
                   int proportion, int signBroken) {
        this.id = id;
        this.cellId = cellId;
        this.cellName = cellName;
        this.cellCode = cellCode;
        this.proportion = proportion;
        this.signBroken = signBroken;
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

    public int getProportion() {
        return this.proportion;
    }

    public void setProportion(int proportion) {
        this.proportion = proportion;
    }

    public int getSignBroken() {
        return this.signBroken;
    }

    public void setSignBroken(int signBroken) {
        this.signBroken = signBroken;
    }

    public long getElementCount() {
        return elementCount;
    }

    public void setElementCount(long elementCount) {
        this.elementCount = elementCount;
    }
}
