package com.zk.cabinet

import android.app.Application
import com.zk.cabinet.bean.User
import com.zk.cabinet.db.CabinetService
import com.zk.cabinet.db.DBHelper
import com.zk.cabinet.db.UserService
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.common.utils.LogUtil

class FileManagementCabinetApplication: Application() {

    override fun onCreate() {
        super.onCreate()
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
            "FileCabinetApplication onCreate：执行一系列的初始化操作。",
            true
        )
        if (CabinetService.getInstance().count() == 0.toLong()){
            CabinetService.getInstance().mainBuild()
        }
        if (UserService.getInstance().count() == 0.toLong()){
            val user = User()
            user.userCode = "admin"
            user.userName = "admin"
            user.userType = 1
            user.password = "admin"
            UserService.getInstance().insert(user)
        }
        NetworkRequest.instance.init(this)
    }
}