package com.zk.cabinet.application

import android.app.Application
import android.os.Handler
import android.text.TextUtils
import android.view.Gravity
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.zk.cabinet.R
import com.zk.cabinet.constant.SelfComm
import com.zk.cabinet.db.DBHelper
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.db.LightControlRecordService
import com.zk.cabinet.helper.LightsSerialPortHelper
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.common.utils.LogUtil

class App : Application() {

    lateinit var spUtil: SharedPreferencesUtil

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
        spUtil = SharedPreferencesUtil.instance
        spUtil.init(this)
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

        // 初始化请求地址URL
        NetworkRequest.instance.init(this)
        // 初始化数据库
        DBHelper.getInstance().init(this)
        // 应用重启/断电重启 清空灯控记录表
        LightControlRecordService.getInstance().deleteAll()
        // 应用重启/断电重启 延迟灭灯
        rebootCloseLightHandler.postDelayed(rebootCloseLightRunnable, 5000)

    }

    private val rebootCloseLightHandler = Handler()
    private val rebootCloseLightRunnable = Runnable {
        run {
            rebootCloseLight()
        }
    }

    /**
     * 断电重启灭灯
     */
    private fun rebootCloseLight() {
        // 应用重启/断电重启 关闭档案组架 组大灯,单架大灯,单架层灯 ;  关闭档案组柜 组大灯
        val deviceName = spUtil.getString(Key.DeviceName, "").toString()
        if (SelfComm.DEVICE_NAME[1].equals(deviceName)) {   // 档案组架 1
            // 打开灯控串口
            openLightSerialPort()
            val settingDeviceList = DeviceService.getInstance().loadAll()
            if (settingDeviceList != null && settingDeviceList.size > 0) {
                // 关闭档案组架组大灯
                LightsSerialPortHelper.getInstance().closeGroupBigLight()
                for (device in settingDeviceList) {
                    // 关闭单个档案组架大灯
                    LightsSerialPortHelper.getInstance()
                        .closeBigLight(device.lightControlBoardId.toInt())
                    // 关闭当个档案组架所有层灯
                    LightsSerialPortHelper.getInstance()
                        .closeSingleCabinetAllLight(device.lightControlBoardId.toInt())
                }
            }

            LogUtils.d("应用重启/断电重启 关闭档案组架 组大灯,单架大灯,单架层灯")
        } else if (SelfComm.DEVICE_NAME[2].equals(deviceName)) {   // 档案组柜 2
            // 打开灯控串口
            openLightSerialPort()
            // 关闭档案组柜组大灯
            LightsSerialPortHelper.getInstance().closeGroupBigLight()
            LogUtils.d("应用重启/断电重启 关闭档案组柜 组大灯")
        }
    }

    /**
     * 打开档案组架灯控串口
     * 灯控串口是需要先配置的
     */
    private fun openLightSerialPort() {
        val port = spUtil.getString(Key.LightsSerialSelected, "")
        if (!TextUtils.isEmpty(port)) {
            LightsSerialPortHelper.getInstance().close()
            LightsSerialPortHelper.getInstance().open(port)
            LogUtils.e("打开灯控串口:$port")
        } else {
            ToastUtils.setBgColor(resources.getColor(R.color.red_primary))
            ToastUtils.setMsgColor(resources.getColor(R.color.white))
            ToastUtils.showLong("灯控串口未配置")
        }
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