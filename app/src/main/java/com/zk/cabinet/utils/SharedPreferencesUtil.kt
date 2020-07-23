package com.zk.cabinet.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import java.util.*

class SharedPreferencesUtil {
    private lateinit var sp: SharedPreferences

    companion object {
        val instance: SharedPreferencesUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SharedPreferencesUtil()
        }
    }

    fun init(context: Context) {
        sp = context.getSharedPreferences(
            SharedPreferencesUtil.Key.FileManagementCabinet.name,
            Context.MODE_PRIVATE
        )
    }

    fun removeValue(key: Key) {
        val editor = sp.edit()
        editor.remove(key.name)
        editor.apply()
    }

    fun getBoolean(key: Key, defaultValue: Boolean): Boolean {
        return sp.getBoolean(key.name, defaultValue)
    }

    fun getInt(key: Key, defaultValue: Int): Int {
        return sp.getInt(key.name, defaultValue)
    }

    fun getLong(key: Key, defaultValue: Long): Long {
        return sp.getLong(key.name, defaultValue)
    }

    fun getString(key: Key, defaultValue: String?): String? {
        return sp.getString(key.name, defaultValue)
    }

    fun commitValue(record: Record): Boolean {
        val editor = sp.edit()
        dataFilling(editor, record)
        return editor.commit()
    }

    fun applyValue(record: Record) {
        val editor = sp.edit()
        dataFilling(editor, record)
        editor.apply()
    }

    fun applyValue(records: ArrayList<Record>) {
        if (records.size <= 0) return
        val editor = sp.edit()
        dataFilling(editor, records)
        editor.apply()
    }

    private fun dataFilling(editor: Editor, records: ArrayList<Record>) {
        for (record in records) {
            dataFilling(editor, record)
        }
    }

    private fun dataFilling(editor: Editor, record: Record) {
        when (record.type) {
            Record.Type.TypeInt -> editor.putInt(record.key.name, record.intValue)
            Record.Type.TypeBoolean -> editor.putBoolean(record.key.name, record.booleanValue)
            Record.Type.TypeString -> editor.putString(record.key.name, record.stringValue)
            Record.Type.TypeLong -> editor.putLong(record.key.name, record.longValue)
        }
    }

    class Record(var key: Key) {
        var intValue = 0
        var longValue: Long = 0
        var booleanValue = false
        lateinit var stringValue: String
        lateinit var type: Type

        constructor(key: Key, intValue: Int) : this(key) {
            this.intValue = intValue
            type = Type.TypeInt
        }

        constructor(key: Key, longValue: Long) : this(key) {
            this.longValue = longValue
            type = Type.TypeLong
        }

        constructor(key: Key, booleanValue: Boolean) : this(key) {
            this.booleanValue = booleanValue
            type = Type.TypeBoolean
        }

        constructor(key: Key, stringValue: String) : this(key) {
            this.stringValue = stringValue
            type = Type.TypeString
        }

        enum class Type {
            TypeInt,
            TypeBoolean,
            TypeString,
            TypeLong
        }
    }

    enum class Key {
        FileManagementCabinet,

        Root,                      //配置管理员账户 String
        RootPwd,                   //配置管理员密码 String

        IdTemp,
        NameTemp,
        GenderTemp,
        PhoneNumberTemp,
        LoginCodeTemp,
        RoleIdTemp,
        RoleNameTemp,
        RootMemberTemp,
        OrgCodeTemp,
        OrgNameTemp,
        OrgCabinet,

        DeviceIdTemp,

        DeviceCode,                //设备编号 String
        UnitNumber,                //单位编号 String
        UnitAddress,               //单位地址 String

        Eth0IP,                    //IP String
        Eth0SubnetMask,            //子网掩码 String
        Eth0Gateway,               //网关 String
        Eth0DNS,                   //DNS String
        Eth1IP,                    //IP String
        Eth1SubnetMask,            //子网掩码 String
        WebApiServiceIp,           //平台服务IP String
        WebApiServicePort,         //平台服务端口 Int
        CabinetServicePort,        //柜体服务端口 Int

        SoundSwitch,               //盘点结束后读出盘点的本数 bool

        NumberOfBoxes,             //箱体数量 String like [A] OR [B,A] OR [A,B,C]
        NumberOfBoxesSelected,     //箱体数量 ATest = 0, A = 1, A,B = 2
        NotClosedDoorAlarmTime,    //未关门报警时间 Int
        TooManyFilesNumber,        //卷宗数量过多提醒 默认40 int


        SyncInterval,              //同步间隔 Int
        Countdown,                 //倒计时时间 Int
        CalibrateTime,             //校时大于24小时
        Restart,                   //是否自动重启 bool
        RestartStartTimeHourOfDay, //自动重启开始时间 Int

        CheckDoorStatus,           //防盗机制 bool
        Calibration,               //是否闲时盘点 bool
        CalibrationTimeHourOfDay,  //闲时盘点开始时间 Int

        RestartNowForSet,          //是否做了需要重启的设置 bool
        Debug,                     //debug bool
    }
}