package com.zk.cabinet

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport
import com.zk.cabinet.db.CabinetService
import com.zk.cabinet.db.DBHelper
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
        CrashReport.initCrashReport(applicationContext, "49af892fc9", true)

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
        // 初始化单个柜体 5*24
        if (CabinetService.getInstance().count() == 0.toLong()) {
            CabinetService.getInstance().mainBuild()
        }
        // 初始化盘点单据任务
        NetworkRequest.instance.init(this)
    }
}