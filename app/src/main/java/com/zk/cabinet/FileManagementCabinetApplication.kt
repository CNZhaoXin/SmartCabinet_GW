package com.zk.cabinet

import android.app.Application
import com.zk.cabinet.bean.User
import com.zk.cabinet.db.*
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.common.utils.LogUtil

/**
 * 1号柜
 * EPC:12340007 位置:1234567801-2-1
 * EPC:10000002 位置:1234567801-2-3
 * EPC:10000003 位置:1234567801-2-5
 * EPC:20000002 位置:1234567801-2-7
 * EPC:50000008 位置:1234567801-2-9
 *
 * 3号柜
 * EPC:50000009 位置:1234567803-2-3
 * EPC:60000003 位置:1234567803-2-5
 * EPC:60000012 位置:1234567803-2-7
 */
class FileManagementCabinetApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 初始化Bugly
        // CrashReport.initCrashReport(applicationContext, "49af892fc9", true)

        //初始化数据库
        DBHelper.getInstance().init(this)
        //初始化SharedPreferencesUtil
        val spUtil = SharedPreferencesUtil.instance
        spUtil.init(this)
        //初始化日志打印
        LogUtil.instance.init(this)
        val isDebug = spUtil.getBoolean(Key.Debug, false)
        LogUtil.instance.logSwitch = isDebug
        LogUtil.instance.d(
            "FileCabinetApplication",
            "FileCabinetApplication onCreate：执行一系列的初始化操作",
            true
        )
        // todo 初始化假用户
        if (UserService.getInstance().count() == 0.toLong()) {
            val newUserZX = User()
            newUserZX.uuId = "111111" // todo 人脸ID, 要在人脸web地址中录入人脸的时候配置
            newUserZX.userCode = "zx"
            newUserZX.password = "zx"
            newUserZX.userName = "赵鑫"
            newUserZX.modifyTime = "管理员" // 角色名称
            UserService.getInstance().insert(newUserZX)

            val newUserWxh = User()
            newUserWxh.uuId = "222222" // todo 人脸ID, 要在人脸web地址中录入人脸的时候配置
            newUserWxh.userCode = "wxh"
            newUserWxh.password = "wxh"
            newUserWxh.userName = "吴笑慧"
            newUserWxh.modifyTime = "管理员" // 角色名称
            UserService.getInstance().insert(newUserWxh)

            val newUserDhw = User()
            newUserDhw.uuId = "666666" // todo 人脸ID, 要在人脸web地址中录入人脸的时候配置
            newUserDhw.userCode = "dhw"
            newUserDhw.password = "dhw"
            newUserDhw.userName = "杜红伟"
            newUserDhw.modifyTime = "管理员" // 角色名称
            UserService.getInstance().insert(newUserDhw)

            val newUserJfz = User()
            newUserJfz.uuId = "888888" // todo 人脸ID, 要在人脸web地址中录入人脸的时候配置
            newUserJfz.userCode = "jfz"
            newUserJfz.password = "jfz"
            newUserJfz.userName = "金芳祝"
            newUserJfz.modifyTime = "管理员" // 角色名称
            UserService.getInstance().insert(newUserJfz)
        }

        // 初始化单个柜体 5*24
        if (CabinetService.getInstance().count() == 0.toLong()) {
            CabinetService.getInstance().mainBuild()
        }

        // todo 初始化柜子里的假档案数据
        val deviceList = DeviceService.getInstance().loadAll()
        if (deviceList.size > 0 && DossierOperatingService.getInstance().count() == 0.toLong()) {
            DossierOperatingService.getInstance().mainBuild()
        }

        // 初始化盘点单据任务
        NetworkRequest.instance.init(this)
    }
}