package com.zk.cabinet.application

import android.app.Application
import android.view.Gravity
import com.blankj.utilcode.util.*
import com.zk.cabinet.db.DBHelper
import com.zk.cabinet.db.LightControlRecordService
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.common.utils.LogUtil
import java.io.PrintWriter

class App : Application() {

    companion object {
        val instance: App by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            App()
        }
    }

    override fun onCreate() {
        super.onCreate()

        // 初始化Bugly, 需要根据包名重新申请id
        // CrashReport.initCrashReport(applicationContext, "49af892fc9", true)

        // 初始化SharedPreferencesUtil
        val spUtil = SharedPreferencesUtil.instance
        spUtil.init(this)
        // 初始化请求地址URL
        NetworkRequest.instance.init(this)
        // 初始化数据库
        DBHelper.getInstance().init(this)
        // 应用重启清空灯控记录表
        LightControlRecordService.getInstance().deleteAll()

        // 初始化Toast
        initToastUtil()
        // 初始化日志打印(默认打开)
        LogUtil.instance.init(this)
        val isDebug = spUtil.getBoolean(Key.Debug, true)
        // LogUtil.instance.logSwitch = isDebug
        LogUtil.instance.logSwitch = true // 日志打开
        LogUtil.instance.d(
            "App",
            "App onCreate：执行一系列的初始化操作",
            true
        )

    }

    /**
     * 初始化ToastUtil
     */
    private fun initToastUtil() {
//        setGravity     : 设置吐司位置
//        setBgColor     : 设置背景颜色
//        setBgResource  : 设置背景资源
//        setMsgColor    : 设置消息颜色
//        setMsgTextSize : 设置消息字体大小
//        showShort      : 显示短时吐司
//        showLong       : 显示长时吐司
//        showCustomShort: 显示短时自定义吐司
//        showCustomLong : 显示长时自定义吐司
//        cancel         : 取消吐司显示
        ToastUtils.setGravity(Gravity.TOP, 0, 0)
        ToastUtils.setMsgTextSize(28)
    }
}