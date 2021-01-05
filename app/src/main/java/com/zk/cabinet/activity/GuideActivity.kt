package com.zk.cabinet.activity

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.blankj.utilcode.util.*
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.lztek.toolkit.Lztek
import com.romainpiel.shimmer.Shimmer
import com.romainpiel.shimmer.ShimmerTextView
import com.zk.cabinet.R
import com.zk.cabinet.adapter.CabinetOnlineAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.constant.SelfComm
import com.zk.cabinet.databinding.ActivityGuideBinding
import com.zk.cabinet.databinding.DialogLoginBinding
import com.zk.cabinet.databinding.DialogSettingBinding
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.db.InventoryPlanRecordService
import com.zk.cabinet.entity.*
import com.zk.cabinet.faceServer.FaceRecognitionHttpServer
import com.zk.cabinet.faceServer.FaceRecognitionListener
import com.zk.cabinet.faceServer.resultBean.ResultSuccess
import com.zk.cabinet.helper.CardSerialPortHelper
import com.zk.cabinet.helper.LightsSerialPortHelper
import com.zk.cabinet.helper.MQTTHelper
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.DecimalFormatUtil
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.cabinet.utils.SharedPreferencesUtil.Record
import com.zk.common.utils.ActivityUtil
import com.zk.rfid.bean.DeviceInformation
import com.zk.rfid.callback.DeviceInformationListener
import com.zk.rfid.ur880.UR880Entrance
import kotlinx.android.synthetic.main.dialog_card.*
import org.json.JSONException
import org.json.JSONObject
import java.io.PrintWriter
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

private const val DEVICE_REGISTERED = 0x01
private const val DEVICE_HEARTBEAT = 0x02
private const val DEVICE_REMOVED = 0x03
private const val LOGIN_BY_PWD_SUCCESS = 0x04
private const val LOGIN_BY_PWD_ERROR = 0x05
private const val CARD_LOGIN_SUCCESS = 0x06
private const val CARD_LOGIN_ERROR = 0x07
private const val LOGIN_BY_FACE_SUCCESS = 0x08
private const val LOGIN_BY_FACE_ERROR = 0x09
private const val LOGIN_BY_FACE_REGIST = 0x10
private const val LOGIN_BY_FACE_NO_REGIST = 0x11

// 请求获取 库存信息和温湿度数据 间隔时间(单位分钟)
private const val INTERVAL_MINUTES_TIME = 5L

// 请求获取 App版本信息 间隔时间(单位分钟)
private const val GET_APP_VERSION_MINUTES_TIME = 10L

// 开始安装app后,延迟重启App的时间,10S,安装时间够了
private const val DELAY_RESTART_APP_TIME = 8 * 1000L

// 管理员密码,刘强的员工号
private const val ADMIN_PASSWORD = "30185435"

// 调试人员密码
private const val ADMIN_PASSWORD_ADMIN = "wbl123"

class GuideActivity : TimeOffAppCompatActivity(), OnClickListener, View.OnLongClickListener {
    private lateinit var mGuideBinding: ActivityGuideBinding
    private lateinit var mHandler: MainHandler
    private lateinit var mScheduledExecutorService: ScheduledExecutorService
    private lateinit var mTimerTask: TimerTask
    private lateinit var mTimerTaskGetAppVersion: TimerTask
    private lateinit var mScheduledExecutorServiceGetAppVersion: ScheduledExecutorService

    // 库房选择器
    private var optionsHousePickerView: OptionsPickerView<*>? = null

    // 柜体在线情况
    private val mCabinetOnlineList = ArrayList<CabinetOnlineInfo>()
    private lateinit var mCabinetOnlineAdapter: CabinetOnlineAdapter

    // 需要打开的读写器串口列表
    private val mDeviceInformationList = ArrayList<DeviceInformation>()

    private var mDialogLoginBinding: DialogLoginBinding? = null
    private var mDialogLogin: AlertDialog? = null
    private var mFaceDialogBinding: ViewDataBinding? = null
    private var mFaceDialog: AlertDialog? = null
    private var mCardDialogBinding: ViewDataBinding? = null
    private var mCardDialog: AlertDialog? = null
    private var mSettingDialogBinding: DialogSettingBinding? = null
    private var mSettingDialog: AlertDialog? = null

    private var deviceName: String = ""

    private lateinit var mProgressDialog: ProgressDialog

    companion object {
        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, GuideActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        isAutoFinish = false
        super.onCreate(savedInstanceState)
        mGuideBinding = DataBindingUtil.setContentView(this, R.layout.activity_guide)
        mGuideBinding.onClickListener = this
        mHandler = MainHandler(this)
        // 初始化dialog
        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setMessage("正在登录...")
        mProgressDialog.setCancelable(false)
        // 显示选择的设备类型
        deviceName = mSpUtil.getString(Key.DeviceName, "").toString()
        mGuideBinding.tvSelectDevice.text = deviceName
        // 根据不同设备类型初始化操作
        init(deviceName)
    }

    /**
     * 定时检查更新APP-TimerTask
     */
    private fun timerTaskGetAppVersion() {
        // 定时根据 请求获取最新App版本信息
        mTimerTaskGetAppVersion = object : TimerTask() {
            override fun run() {
                // 1.判断当前是否在首页(判断当前是否是GuideActivity)
                if (ActivityUtils.getTopActivity().componentName.className == "com.zk.cabinet.activity.GuideActivity") {
                    LogUtils.e("获取最新App版本信息-当前是首页,准备获取")
                    getAppVersion()
                }
            }
        }
        // 一个线程的并行任务线程池
        mScheduledExecutorServiceGetAppVersion = Executors.newScheduledThreadPool(3)
        /**
        command：执行线程
        initialDelay：初始化延时
        period：前一次执行结束到下一次执行开始的间隔时间（间隔执行延迟时间）
        unit：计时单位
         */
        mScheduledExecutorServiceGetAppVersion.scheduleAtFixedRate(
            mTimerTaskGetAppVersion,
            0,
            GET_APP_VERSION_MINUTES_TIME,
            TimeUnit.MINUTES
        )
    }

    /**
     * 请求获取最新App版本信息
     *  /busi/otherAppupdate/getLastInfo
     */
    private fun getAppVersion() {
        val requestUrl = NetworkRequest.instance.mGetLastInfo
        LogUtils.e("获取最新App版本信息-requestUrl:$requestUrl")

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, requestUrl, { response ->
                LogUtils.e("获取最新App版本信息-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val result: ResultGetAppInfo = JSON.parseObject(
                                "$response", ResultGetAppInfo::class.java
                            )
                            val data = result.data
                            if (data != null) {
                                // data.versionName 版本名称
                                // data.version 版本号

                                // 获取最新版本号和当前App版本号
                                val curAppVersionCode = AppUtils.getAppVersionCode()
                                LogUtils.e(
                                    "获取最新App版本信息-获取到的最新版本号",
                                    data.version,
                                    "当前版本号",
                                    curAppVersionCode
                                )

                                // 1.判断获取最新的版本号是否比当前版本高
                                if (data.version.toInt() > curAppVersionCode) {
                                    // 2.判断要下载的文件URL是否存在,是否是apk文件
                                    val fileInfo = data.fileInfo
                                    if (fileInfo != null && !TextUtils.isEmpty(fileInfo.url) && fileInfo.url.endsWith(
                                            ".apk",
                                            true
                                        )
                                    ) {
                                        // 下载的文件存储路径: /storage/emulated/0/Android/data/package/files/Download
                                        val downloadPath = PathUtils.getExternalAppDownloadPath()
                                        LogUtils.e(
                                            "获取最新App版本信息-准备下载新版APK文件-文件下载后存储路径",
                                            downloadPath
                                        )

                                        // 开启APP更新文件下载
                                        // 初始化FileDownloader
                                        FileDownloader.setup(this)
                                        FileDownloader.getImpl()
                                            .create(fileInfo.url)
                                            // .create("http://ppgbucket.oss-cn-hangzhou.aliyuncs.com/SmartCabinet_GW_v1.0.2_release.apk")
                                            .setPath(downloadPath, true)
                                            .setListener(object : FileDownloadListener() {
                                                override fun pending(
                                                    task: BaseDownloadTask?,
                                                    soFarBytes: Int,
                                                    totalBytes: Int
                                                ) {
                                                    LogUtils.e(
                                                        "获取最新App版本信息-文件下载:pending",
                                                        soFarBytes,
                                                        totalBytes
                                                    )
                                                }

                                                override fun connected(
                                                    task: BaseDownloadTask?,
                                                    etag: String?,
                                                    isContinue: Boolean,
                                                    soFarBytes: Int,
                                                    totalBytes: Int
                                                ) {
                                                    super.connected(
                                                        task,
                                                        etag,
                                                        isContinue,
                                                        soFarBytes,
                                                        totalBytes
                                                    )
                                                    LogUtils.e(
                                                        "获取最新App版本信息-文件下载:connected",
                                                        soFarBytes,
                                                        totalBytes
                                                    )
                                                }

                                                override fun progress(
                                                    task: BaseDownloadTask?,
                                                    soFarBytes: Int,
                                                    totalBytes: Int
                                                ) {
                                                    LogUtils.e(
                                                        "获取最新App版本信息-文件下载:progress",
                                                        soFarBytes,
                                                        totalBytes
                                                    )
                                                }

                                                override fun blockComplete(task: BaseDownloadTask?) {
                                                    super.blockComplete(task)
                                                    LogUtils.e("获取最新App版本信息-文件下载:blockComplete")
                                                }

                                                override fun retry(
                                                    task: BaseDownloadTask?,
                                                    ex: Throwable?,
                                                    retryingTimes: Int,
                                                    soFarBytes: Int
                                                ) {
                                                    super.retry(
                                                        task,
                                                        ex,
                                                        retryingTimes,
                                                        soFarBytes
                                                    )
                                                    LogUtils.e(
                                                        "获取最新App版本信息-文件下载:retry",
                                                        soFarBytes
                                                    )
                                                }

                                                override fun completed(task: BaseDownloadTask?) {
                                                    LogUtils.e(
                                                        "获取最新App版本信息-文件下载-完成:completed",
                                                        task!!.targetFilePath
                                                    )

                                                    // 下载时间要好几分钟,这时候还要加判断是否在主页面,才能安装
                                                    // 假如此时有人操作了怎么办,那就判断还是不是在主页面，因为下载是要时间的，这个时间不能保证没有人在操作大屏
                                                    // 假如此时有自动盘库消息来了怎么办，如果先静默升级，那么应用进程会被kill掉，是不会去盘库的，
                                                    // 相当于收到消息会作废，丢失，那就不管了，等升级完成再重新创建盘点计划就好，这两种情况同时碰到的几率非常小
                                                    if (ActivityUtils.getTopActivity().componentName.className == "com.zk.cabinet.activity.GuideActivity") {
                                                        // 下载完成安装应用
                                                        // 如果设备Root了,静默安装,否则普通安装
                                                        if (AppUtils.isAppRoot()) {
                                                            // 档案组架1/档案组柜2/档案单柜3/一体机4-静默安装自动升级,设备已root过
                                                            // 1.静默安装 : 安装完成,设备 DELAY_RESTART_APP_TIME 毫秒后重启APP
                                                            // 用 /storage/emulated/0/Android/data/2_v1.0.2.apk 测试静默安装重启APP
                                                            // suInstallApp("/storage/emulated/0/Android/data/2_v1.0.2.apk", DELAY_RESTART_APP_TIME)
                                                            mProgressDialog.setMessage("正在更新应用,更新完成后将重启应用,请稍后...")
                                                            mProgressDialog.show()

                                                            val result = suInstallApp(
                                                                task.targetFilePath,
                                                                DELAY_RESTART_APP_TIME
                                                            )
                                                            if (result) {
                                                                if (mProgressDialog != null) {
                                                                    mProgressDialog.dismiss()
                                                                }
                                                            }
                                                        } else {
                                                            // PDA没有root过，所以是手动安装
                                                            // 2.手动点击安装,安装完成会显示系统安装APK完成后的Dialog,完成/打开
                                                            AppUtils.installApp(task.targetFilePath)
                                                        }

                                                    } else {
                                                        LogUtils.e(
                                                            "获取最新App版本信息-文件下载完成-不是主页面不安装新应用"
                                                        )
                                                    }
                                                }

                                                override fun paused(
                                                    task: BaseDownloadTask?,
                                                    soFarBytes: Int,
                                                    totalBytes: Int
                                                ) {
                                                    LogUtils.e(
                                                        "获取最新App版本信息-文件下载:progress",
                                                        soFarBytes,
                                                        totalBytes
                                                    )
                                                }

                                                override fun error(
                                                    task: BaseDownloadTask?,
                                                    e: Throwable?
                                                ) {
                                                    LogUtils.e(
                                                        "获取最新App版本信息-文件下载:error",
                                                        e.toString()
                                                    )
                                                }

                                                override fun warn(task: BaseDownloadTask?) {
                                                    LogUtils.e("获取最新App版本信息-文件下载:warn")
                                                }
                                            }).start()

                                    } else {
                                        LogUtils.e(
                                            "获取最新App版本信息-文件下载URL不存在 或 不是.apk文件",
                                        )
                                    }
                                } else {
                                    // 不是新版本无需更新
                                    LogUtils.e("获取最新App版本信息-当前已是最新版本-无需下载APK文件进行更新")
                                }
                            } else {
                                LogUtils.e("获取最新App版本信息-数据为空")
                            }
                        } else {
                            speek("获取最新版本信息-请求失败")
                            showErrorToast(response.getString("msg"))
                        }
                    } else {
                        speek("获取最新版本信息-请求失败")
                        showErrorToast("获取最新App版本信息-请求失败")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    speek("获取最新版本信息-请求失败")
                    showErrorToast("获取最新App版本信息-请求失败")
                }
            },
            { error ->
                val msg =
                    if (error != null)
                        if (error.networkResponse != null)
                            "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                        else
                            "errorCode: -1 VolleyError: $error"
                    else {
                        "errorCode: -1 VolleyError: 未知"
                    }
                speek("获取最新版本信息-请求失败")
                showErrorToast(msg)
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    private fun init(deviceName: String) {
        // SelfComm.DEVICE_NAME[1] = "档案组架"
        // SelfComm.DEVICE_NAME[2] = "档案组柜"
        // SelfComm.DEVICE_NAME[3] = "档案单柜"
        // SelfComm.DEVICE_NAME[4] = "一体机"
        // SelfComm.DEVICE_NAME[5] = "PDA"
        // SelfComm.DEVICE_NAME[6] = "通道门"

        // 档案组架 1
        if (SelfComm.DEVICE_NAME[1].equals(deviceName)) {
            // 定时根据操作屏设备ID获取库房号然后获取 库存信息和温湿度信息
            mTimerTask = object : TimerTask() {
                override fun run() {
                    val equipmentId = mSpUtil.getString(Key.EquipmentId, "")
                    if (TextUtils.isEmpty(equipmentId)) {
                        showWarningToast("请先设置操作屏设备ID")
                    } else {
                        getCabineMasterByEquipmentId(equipmentId!!)
                    }
                }
            }
            // 一个线程的并行任务线程池
            mScheduledExecutorService = Executors.newScheduledThreadPool(3)
            /**
            command：执行线程
            initialDelay：初始化延时
            period：前一次执行结束到下一次执行开始的间隔时间（间隔执行延迟时间）
            unit：计时单位
             */
            mScheduledExecutorService.scheduleAtFixedRate(
                mTimerTask,
                0,
                INTERVAL_MINUTES_TIME,
                TimeUnit.MINUTES
            )

            // 初始化MQTT
            initMQTT()
            // 档案组架隐藏人脸登录和刷卡登录
            mGuideBinding.lavFaceLogin.visibility = GONE
            mGuideBinding.lavCardLogin.visibility = GONE
            // 开启定时检查更新APP-TimerTask
            timerTaskGetAppVersion()
        }

        // 档案组柜 2
        if (SelfComm.DEVICE_NAME[2].equals(deviceName)) {
            // 定时根据操作屏设备ID获取库房号然后获取 库存信息和温湿度信息
            mTimerTask = object : TimerTask() {
                override fun run() {
                    val equipmentId = mSpUtil.getString(Key.EquipmentId, "")
                    if (TextUtils.isEmpty(equipmentId)) {
                        showWarningToast("请先设置操作屏设备ID")
                    } else {
                        getCabineMasterByEquipmentId(equipmentId!!)
                    }
                }
            }
            mScheduledExecutorService = Executors.newScheduledThreadPool(3)
            mScheduledExecutorService.scheduleAtFixedRate(
                mTimerTask,
                0,
                INTERVAL_MINUTES_TIME,
                TimeUnit.MINUTES
            )

            // 初始化MQTT
            initMQTT()
            // 配置柜子/连接柜子(TcpIp通信)
            initUR800ByTcpIp()
            // 档案组架隐藏刷卡登录
            mGuideBinding.lavCardLogin.visibility = GONE
            // 开启定时检查更新APP-TimerTask
            timerTaskGetAppVersion()
        }

        // 档案单柜 3
        if (SelfComm.DEVICE_NAME[3].equals(deviceName)) {
            // 定时根据操作屏设备ID获取库房号然后获取 库存信息和温湿度信息
            mTimerTask = object : TimerTask() {
                override fun run() {
                    val equipmentId = mSpUtil.getString(Key.EquipmentId, "")
                    if (TextUtils.isEmpty(equipmentId)) {
                        showWarningToast("请先设置操作屏设备ID")
                    } else {
                        getCabineMasterByEquipmentId(equipmentId!!)
                    }
                }
            }
            mScheduledExecutorService = Executors.newScheduledThreadPool(3)
            mScheduledExecutorService.scheduleAtFixedRate(
                mTimerTask,
                0,
                INTERVAL_MINUTES_TIME,
                TimeUnit.MINUTES
            )

            // 初始化MQTT
            initMQTT()
            // 配置柜子/连接柜子(TcpIp通信)
            initUR800ByTcpIp()
            // 档案组架隐藏刷卡登录
            mGuideBinding.lavCardLogin.visibility = GONE
            // 开启定时检查更新APP-TimerTask
            timerTaskGetAppVersion()
        }

        // 一体机 4
        if (SelfComm.DEVICE_NAME[4].equals(deviceName)) {
            // 定时根据一体机设备ID获取库房号然后获取 库存信息和温湿度信息
            mTimerTask = object : TimerTask() {
                override fun run() {
                    val equipmentId = mSpUtil.getString(Key.EquipmentId, "")
                    if (TextUtils.isEmpty(equipmentId)) {
                        showWarningToast("请先设置一体机设备ID")
                    } else {
                        getAIOInfoByEquipmentId(equipmentId!!)
                    }
                }
            }
            mScheduledExecutorService = Executors.newScheduledThreadPool(3)
            mScheduledExecutorService.scheduleAtFixedRate(
                mTimerTask,
                0,
                INTERVAL_MINUTES_TIME,
                TimeUnit.MINUTES
            )

            // 初始化读写器(串口需要配置)
            initUR800BySerialPort()
            // 开启定时检查更新APP-TimerTask
            timerTaskGetAppVersion()
        }

        // PDA 5
        if (SelfComm.DEVICE_NAME[5].equals(deviceName)) {
            // 初始化title
            initShimmerTitlePDA()
            // 获取库房信息
            getHouseList()
            // PDA隐藏人脸登录和刷卡登录
            mGuideBinding.lavFaceLogin.visibility = GONE
            mGuideBinding.lavCardLogin.visibility = GONE
            // PDA开启定时检查更新APP-TimerTask，手动安装
            timerTaskGetAppVersion()
        }

        // 通道门 6
        if (SelfComm.DEVICE_NAME[6].equals(deviceName)) {
            // 通道门显示时间
            mGuideBinding.tcDayTdm.visibility = VISIBLE
            mGuideBinding.tcHourTdm.visibility = VISIBLE
            // 初始化MQTT
            initMQTT()
            // 开启定时检查更新APP-TimerTask
            timerTaskGetAppVersion()

            // 隐藏3种登录选项
            mGuideBinding.llLogin.visibility = GONE
            // 隐藏搜索框
            mGuideBinding.tvSearch.visibility = GONE
            // 隐藏库存饼图
            mGuideBinding.pieChart.visibility = GONE
            // 隐藏title
            mGuideBinding.stvChineseTitle.visibility = GONE
            mGuideBinding.stvEnglishTitle.visibility = GONE
            // 隐藏左上角时间
            mGuideBinding.llTime.visibility = GONE
            // 隐藏温湿度
            mGuideBinding.llWsd.visibility = GONE
        }

    }

    override fun onResume() {
        super.onResume()
        if (SelfComm.DEVICE_NAME[1].equals(deviceName)
            || SelfComm.DEVICE_NAME[2].equals(deviceName)
            || SelfComm.DEVICE_NAME[3].equals(deviceName)
            || SelfComm.DEVICE_NAME[4].equals(deviceName)
            || SelfComm.DEVICE_NAME[6].equals(deviceName)
        ) {
            // 隐藏导航栏(只适用于广州透晶技术公司的屏幕)
            Lztek.create(this).hideNavigationBar()
        }


        // 档案组架1-档案组柜2-打开灯控串口
        if (SelfComm.DEVICE_NAME[1].equals(deviceName)
            || SelfComm.DEVICE_NAME[2].equals(deviceName)
        ) {
            openLightSerialPort()
        }

        // 档案组柜/档案单柜/一体机-开启人脸识别Http服务器
        if (SelfComm.DEVICE_NAME[2].equals(deviceName)
            || SelfComm.DEVICE_NAME[3].equals(deviceName)
            || SelfComm.DEVICE_NAME[4].equals(deviceName)
        ) {
            startFaceServer()
        }

        // 档案组柜/档案单柜-进入或者回到首页 查询是否有盘库计划，有的话需要执行计划
        if (SelfComm.DEVICE_NAME[2].equals(deviceName)
            || SelfComm.DEVICE_NAME[3].equals(deviceName)
        ) {
            val inventoryPlanList = InventoryPlanRecordService.getInstance().loadAll()
            if (inventoryPlanList != null && inventoryPlanList.size > 0) {
                LogUtils.e(
                    "自动盘库-当前是主页/或回到首页-保存的待盘库计划列表", JSON.toJSONString(inventoryPlanList)
                )
                val inventoryPlan = inventoryPlanList[0]
                // 打开档案柜自动盘点界面,传递自动盘库所需数据
                val intent = Intent(this, ZNGAutoInventoryActivity::class.java)
                intent.putExtra(ZNGAutoInventoryActivity.PLAN_ID, inventoryPlan.planID)
                intent.putExtra(ZNGAutoInventoryActivity.HOUSE_CODE, inventoryPlan.houseCode)
                intent.putExtra(
                    ZNGAutoInventoryActivity.EQUIPMENT_ID_LIST,
                    inventoryPlan.deviceList
                )
                ActivityUtils.startActivity(intent)
                // 去执行该条计划后，清除表中该盘库计划，无需关心是否盘库成功提交成功，只要去打开界面执行了就算是做了这个操作
                InventoryPlanRecordService.getInstance().delete(inventoryPlan)
                LogUtils.e(
                    "自动盘库-当前是主页/或回到首页-打开自动盘库界面进行盘库,然后删除该计划", JSON.toJSONString(inventoryPlan)
                )
                // 打印删除后的盘库计划列表
                LogUtils.e(
                    "自动盘库-当前是主页/或回到首页-保存的待盘库计划列表,删除后的",
                    JSON.toJSONString(InventoryPlanRecordService.getInstance().loadAll())
                )
            } else {
                LogUtils.e(
                    "自动盘库-当前是主页/或回到首页-暂无保存的待盘库计划"
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // 档案组柜/档案单柜/一体机
        if (SelfComm.DEVICE_NAME[2].equals(deviceName)
            || SelfComm.DEVICE_NAME[3].equals(deviceName)
            || SelfComm.DEVICE_NAME[4].equals(deviceName)
        ) {
            // 关闭人脸识别Http服务器, 不用的时候关闭
            if (mFaceRecognitionHttpServer.isAlive) {
                mFaceRecognitionHttpServer.stop()
                LogUtils.e("关闭人脸识别服务")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        SelfComm.DEVICE_NAME[1] = "档案组架"
//        SelfComm.DEVICE_NAME[2] = "档案组柜"
//        SelfComm.DEVICE_NAME[3] = "档案单柜"
//        SelfComm.DEVICE_NAME[4] = "一体机"
//        SelfComm.DEVICE_NAME[5] = "PDA"
//        SelfComm.DEVICE_NAME[6] = "通道门"

        // 1档案组架 17寸屏 竖屏
        if (SelfComm.DEVICE_NAME[1].equals(deviceName)) {
            // 关闭定时获取库存的线程
            if (mScheduledExecutorService != null) {
                mScheduledExecutorService.shutdownNow()
                LogUtils.e("关闭定时获取库存的线程")
            }
            // 关闭定时获取版本号的线程
            if (mScheduledExecutorServiceGetAppVersion != null) {
                mScheduledExecutorServiceGetAppVersion.shutdownNow()
                LogUtils.e("关闭定时获取版本号的线程")
            }

            // 关闭灯控串口
            LightsSerialPortHelper.getInstance().close()
            // 关闭MQTT
            MQTTHelper.getInstance().closeMQTT()
        }

        // 2档案组柜 17寸屏 竖屏
        if (SelfComm.DEVICE_NAME[2].equals(deviceName)) {
            // 关闭定时获取库存的线程
            if (mScheduledExecutorService != null) {
                mScheduledExecutorService.shutdownNow()
                LogUtils.e("关闭定时获取库存的线程")
            }
            // 关闭定时获取版本号的线程
            if (mScheduledExecutorServiceGetAppVersion != null) {
                mScheduledExecutorServiceGetAppVersion.shutdownNow()
                LogUtils.e("关闭定时获取版本号的线程")
            }

            val deviceList = DeviceService.getInstance().loadAll()
            if (deviceList != null && deviceList.size > 0) {
                UR880Entrance.getInstance()
                    .removeDeviceInformationListener(mDeviceInformationListener)
            }

            // 关闭灯控串口
            LightsSerialPortHelper.getInstance().close()
            // 关闭MQTT
            MQTTHelper.getInstance().closeMQTT()
        }

        // 3档案单柜 15寸屏 竖屏
        if (SelfComm.DEVICE_NAME[3].equals(deviceName)) {
            // 关闭定时获取库存的线程
            if (mScheduledExecutorService != null) {
                mScheduledExecutorService.shutdownNow()
                LogUtils.e("关闭定时获取库存的线程")
            }
            // 关闭定时获取版本号的线程
            if (mScheduledExecutorServiceGetAppVersion != null) {
                mScheduledExecutorServiceGetAppVersion.shutdownNow()
                LogUtils.e("关闭定时获取版本号的线程")
            }

            val deviceList = DeviceService.getInstance().loadAll()
            if (deviceList != null && deviceList.size > 0) {
                UR880Entrance.getInstance()
                    .removeDeviceInformationListener(mDeviceInformationListener)
                UR880Entrance.getInstance().disConnect()
            }

            // 关闭MQTT
            MQTTHelper.getInstance().closeMQTT()
        }

        // 4一体机 17寸屏 横屏
        if (SelfComm.DEVICE_NAME[4].equals(deviceName)) {
            // 关闭定时获取库存的线程
            if (mScheduledExecutorService != null) {
                mScheduledExecutorService.shutdownNow()
                LogUtils.e("关闭定时获取库存的线程")
            }
            // 关闭定时获取版本号的线程
            if (mScheduledExecutorServiceGetAppVersion != null) {
                mScheduledExecutorServiceGetAppVersion.shutdownNow()
                LogUtils.e("关闭定时获取版本号的线程")
            }

            val port = mSpUtil.getString(SharedPreferencesUtil.Key.YTJDxqSerialSelected, "")
            if (!TextUtils.isEmpty(port)) {
                UR880Entrance.getInstance()
                    .removeDeviceInformationListener(mDeviceInformationListener)
                UR880Entrance.getInstance().disConnect()
            }
        }

        // PDA 5
        if (SelfComm.DEVICE_NAME[5].equals(deviceName)) {
            // 关闭定时获取版本号的线程
            if (mScheduledExecutorServiceGetAppVersion != null) {
                mScheduledExecutorServiceGetAppVersion.shutdownNow()
                LogUtils.e("关闭定时获取版本号的线程")
            }
        }

        // 通道门 6
        if (SelfComm.DEVICE_NAME[6].equals(deviceName)) {
            // 关闭定时获取版本号的线程
            if (mScheduledExecutorServiceGetAppVersion != null) {
                mScheduledExecutorServiceGetAppVersion.shutdownNow()
                LogUtils.e("关闭定时获取版本号的线程")
            }
            // 关闭MQTT
            MQTTHelper.getInstance().closeMQTT()
        }
    }

    /**
     * 26.根据一体机设备id获取一体机信息
     * 接口地址：get /api/pad/getAIOInfoByEquipmentId
     */
    private fun getAIOInfoByEquipmentId(equipmentId: String) {
        val requestUrl =
            NetworkRequest.instance.mGetAIOInfoByEquipmentId + "?equipmentId=" + equipmentId
        LogUtils.e("根据一体机设备id获取一体机信息-请求URL:", requestUrl)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, requestUrl,
            { response ->
                LogUtils.e("根据一体机设备id获取一体机信息-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val result: ResultGetAIOInfoByEquipmentId =
                                JSON.parseObject(
                                    "$response",
                                    ResultGetAIOInfoByEquipmentId::class.java
                                )

                            val data = result.data
                            if (data != null) {
                                // 根据HouseCode获取库存数据
                                if (data.houseCode != null && !TextUtils.isEmpty(data.houseCode)) {
                                    // 根据HouseCode获取库存数据
                                    getCapital(data.houseCode, data.houseName, "")
                                    // 根据HouseCode获取温湿度数据
                                    getHumitureByHouseId(data.houseCode)
                                    // 初始化Title
                                    initShimmerTitle("${data.houseName}")
                                    // 保存档案室名称
                                    mSpUtil.applyValue(
                                        Record(
                                            Key.HouseName,
                                            "${data.houseName}"
                                        )
                                    )
                                }
                            } else {
                                showWarningToast("根据一体机设备id获取一体机信息-为空")
                            }
                        } else {
                            showWarningToast(response.getString("msg"))
                        }
                    } else {
                        showWarningToast("根据一体机设备id获取一体机信息-失败")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showErrorToast("根据一体机设备id获取一体机信息-失败")
                }
            },
            { error ->
                val msg = if (error != null)
                    if (error.networkResponse != null)
                        "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                    else
                        "errorCode: -1 VolleyError: $error"
                else {
                    "errorCode: -1 VolleyError: 未知"
                }
                showErrorToast(msg)
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    /**
     * 14.根据操作屏设备id获取操作屏及其档案柜信息
     * 接口地址：get /api/pad/getCabineMasterByEquipmentId
     * // 档案组架/档案组柜/档案单柜 调用
     */
    private fun getCabineMasterByEquipmentId(equipmentId: String) {
        val requestUrl =
            NetworkRequest.instance.mGetCabineMasterByEquipmentId + "?equipmentId=" + equipmentId
        LogUtils.e("根据操作屏设备id获取操作屏及其档案柜信息-请求URL:", requestUrl)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, requestUrl,
            { response ->
                LogUtils.e("根据操作屏设备id获取操作屏及其档案柜信息-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val result: ResultGetCaineMasterByEquipmentId =
                                JSON.parseObject(
                                    "$response",
                                    ResultGetCaineMasterByEquipmentId::class.java
                                )

                            val data = result.data
                            if (data != null) {
                                if (data.houseCode != null && !TextUtils.isEmpty(data.houseCode)) {
                                    // 根据HouseCode获取库存数据
                                    getCapital(data.houseCode, data.name, equipmentId)
                                    // 根据HouseCode获取温湿度数据
                                    getHumitureByHouseId(data.houseCode)
                                    // 显示档案室名称
                                    // 初始化Title
                                    initShimmerTitle("${data.houseName}")
                                    // 保存档案室名称
                                    mSpUtil.applyValue(
                                        Record(
                                            Key.HouseName,
                                            "${data.houseName}"
                                        )
                                    )
                                }
                            } else {
                                showWarningToast("根据操作屏设备id获取操作屏及其档案柜信息-为空")
                            }
                        } else {
                            showWarningToast(response.getString("msg"))
                        }
                    } else {
                        showWarningToast("根据操作屏设备id获取操作屏及其档案柜信息-失败")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showErrorToast("根据操作屏设备id获取操作屏及其档案柜信息-失败")
                }
            },
            { error ->
                val msg = if (error != null)
                    if (error.networkResponse != null)
                        "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                    else
                        "errorCode: -1 VolleyError: $error"
                else {
                    "errorCode: -1 VolleyError: 未知"
                }
                showErrorToast(msg)
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    /**
     * 根据库房id获取当前温湿度
     * /statistics/getHumitureByHouseId?houserId=
     */
    private fun getHumitureByHouseId(houserId: String) {
        val requestURL = NetworkRequest.instance.mGetHumitureByHouseId + "?houserId=" + houserId
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, requestURL,
            { response ->
                LogUtils.e("根据库房id获取当前温湿度-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            // 设置温湿度, 格式: 温度:28°C 湿度:50%
                            // {"msg":"操作成功","code":200,"data":{"temperatureValue":21.5,"humidityValue":56.3}}
                            val result: ResultGetHumitureByHouseId =
                                JSON.parseObject(
                                    "$response",
                                    ResultGetHumitureByHouseId::class.java
                                )

                            val data = result.data
                            if (data != null) {
                                if (data.temperatureValue != null) {
                                    mGuideBinding.tvTemperature.text =
                                        data.temperatureValue.toString() + "°C"
                                }
                                if (data.humidityValue != null) {
                                    mGuideBinding.tvHumidity.text =
                                        data.humidityValue.toString() + "%"
                                }
                            } else {
                                showWarningToast("暂无温湿度数据")
                            }

                        } else {
                            showWarningToast(response.getString("msg"))
                        }
                    } else {
                        showWarningToast("暂无温湿度数据")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showErrorToast("暂无温湿度数据")
                }
            },
            { error ->
                val msg = if (error != null)
                    if (error.networkResponse != null)
                        "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                    else
                        "errorCode: -1 VolleyError: $error"
                else {
                    "errorCode: -1 VolleyError: 未知"
                }
                showErrorToast(msg)
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    /**
     * 获取所有库房,PDA要通过选择库房来查看当前库房的库存情况
     */
    private fun getHouseList() {
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            NetworkRequest.instance.mGetHouseList,
            { response ->
                LogUtils.e("获取所有库房-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val resultGetHouseList: ResultGetHouseList =
                                JSON.parseObject(
                                    "$response",
                                    ResultGetHouseList::class.java
                                )

                            val dataList = resultGetHouseList.data
                            if (dataList != null && dataList.size > 0) {
                                // 创建库房选择器
                                createOrgPickerView(dataList)
                                // 默认选择第一个
                                mGuideBinding.tvHouse.visibility = View.VISIBLE
                                val dataBean = dataList[0]
                                val houseName: String = dataBean.name
                                mGuideBinding.tvHouse.text = houseName
                                showNormalToast(houseName)

                                val houseCode: String = dataBean.code
                                // 根据HouseCode获取库存数据
                                getCapital(houseCode, houseName, "")
                                // 根据HouseCode获取温湿度数据
                                getHumitureByHouseId(houseCode)
                            } else {
                                showWarningToast("暂无档案室可选择")
                                finish()
                            }

                        } else {
                            showWarningToast(response.getString("msg"))
                        }
                    } else {
                        showWarningToast("暂无档案室可选择")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showErrorToast("暂无档案室可选择")
                }
            },
            { error ->
                val msg = if (error != null)
                    if (error.networkResponse != null)
                        "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                    else
                        "errorCode: -1 VolleyError: $error"
                else {
                    "errorCode: -1 VolleyError: 未知"
                }
                showErrorToast(msg)
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    /**
     * 创建库房选择器
     */
    private fun createOrgPickerView(ds: List<ResultGetHouseList.DataBean>) {
        optionsHousePickerView = OptionsPickerBuilder(
            this
        ) { options1, options2, options3, v -> //返回的分别是三个级别的选中位置
            val dataBean = ds[options1]
            val houseName: String = dataBean.name
            mGuideBinding.tvHouse.text = houseName
            showNormalToast(houseName)

            val houseCode: String = dataBean.code
            // 根据HouseCode获取库存数据
            getCapital(houseCode, houseName, "")
            // 根据HouseCode获取温湿度数据
            getHumitureByHouseId(houseCode)

        }.setOptionsSelectChangeListener { options1, options2, options3 -> }
            .setSubmitText("确定") //确定按钮文字
            .setCancelText("取消") //取消按钮文字
            .setTitleText("选择库房") //标题
            .setSubCalSize(38) //确定和取消文字大小
            .setTitleSize(40) //标题文字大小
            .setContentTextSize(38) //滚轮文字大小
            .setTitleColor(resources.getColor(R.color.gray_deep))//标题文字颜色
            .setSubmitColor(resources.getColor(R.color.md_teal_A400))//确定按钮文字颜色
            .setCancelColor(resources.getColor(R.color.colorDGH))//取消按钮文字颜色
            .setCyclic(false, false, false)// 循环与否
            .setLineSpacingMultiplier(2.5f) // 可通过调整条目的比例，从而影响调整弹窗高度
            // .setTitleBgColor(0xFF333333)//标题背景颜色 Night mode
            // .setBgColor(0xFF000000)//滚轮背景颜色 Night mode
            // .setLinkage(false)//设置是否联动，默认true
            // .setLabels("省", "市", "区")//设置选择的三级单位
            // .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            // .setSelectOptions(0,0,0)  // 设置默认选中项
            // .setOutSideCancelable(true)// 点击外部dismiss default true
            // .isDialog(true)// 是否显示为对话框样式
            // .isRestoreItem(true) // 切换时是否还原，设置默认选中第一项。
            .build<Any>()

        val options1ItemsString = java.util.ArrayList<String>()
        for (i in ds.indices) {
            val dataBean = ds[i]
            val houseName: String = dataBean.name
            options1ItemsString.add(houseName)
        }
        optionsHousePickerView!!.setPicker(options1ItemsString as List<Nothing>?)
    }

    /**
     * 17.获取库房库存信息
     * 如果equipmentId为空，返回整个库房的库存信息；
     * 如果equipmentId不为空，返回该操作屏对应的所有档案柜的累加库存信息；
     * 接口地址：  get /api/pad/getCapital/{houseCode}/{equipmentId}
     */
    private fun getCapital(houseCode: String, name: String, equipmentId: String) {
        val requestUrl: String = if (TextUtils.isEmpty(equipmentId)) {
            NetworkRequest.instance.mGetCapital + "/" + houseCode
        } else {
            NetworkRequest.instance.mGetCapital + "/" + houseCode + "/" + equipmentId
        }
        LogUtils.e("获取库房库存信息-请求URL:", requestUrl)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, requestUrl,
            { response ->
                LogUtils.e("获取库房库存信息-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val result: ResultGetCapital =
                                JSON.parseObject(
                                    "$response",
                                    ResultGetCapital::class.java
                                )

                            val data = result.data
                            if (data != null) {
                                /**
                                 * PDA:先获取库房个数,多库房的话可供选择库房展示 库房总容量,库房剩余量, 需要多做个库房选择
                                 * 一体机:显示 库房已存量,库房剩余量
                                 * 档案组架/档案组柜/档案单柜: 显示当前档案组架/组柜/单柜的 已存量,剩余量
                                 */
                                // 初始化库存圆饼图( PDA-900 / 其他-650)

                                // mGuideBinding.pieChart.centerText =
                                // generateCenterSpannableText("库房名称", "库房已存", yc, "库房剩余", sy)
                                // 档案组架1
                                if (SelfComm.DEVICE_NAME[1].equals(deviceName)) {
                                    // 显示操作屏库存
                                    createPieChart(
                                        650,
                                        name, // 操作屏名称
                                        "本组架已存",
                                        data.cabinetUsed,
                                        "本组架剩余",
                                        data.cabinetFree
                                    )
                                }
                                // 档案组柜2
                                if (SelfComm.DEVICE_NAME[2].equals(deviceName)) {
                                    // 显示操作屏库存
                                    createPieChart(
                                        650,
                                        name, // 操作屏名称
                                        "本组柜已存",
                                        data.cabinetUsed,
                                        "本组柜剩余",
                                        data.cabinetFree
                                    )
                                }
                                // 档案单柜3 10.1寸屏
                                if (SelfComm.DEVICE_NAME[3].equals(deviceName)) {
                                    // 显示操作屏库存
                                    createPieChart(
                                        650,
                                        name, // 操作屏名称
                                        "本柜已存",
                                        data.cabinetUsed,
                                        "本柜剩余",
                                        data.cabinetFree
                                    )
                                }

                                // 一体机4
                                if (SelfComm.DEVICE_NAME[4].equals(deviceName)) {
                                    // 显示一体机所属的库房库存
                                    createPieChart(
                                        700,
                                        name, // 库房名称
                                        "档案室已存",
                                        data.houseCapacity - data.houseFree,
                                        "档案室剩余",
                                        data.houseFree,
                                    )
                                }

                                // PDA 5
                                if (SelfComm.DEVICE_NAME[5].equals(deviceName)) {
                                    // 显示库房库存
                                    createPieChart(
                                        900,
                                        name, // 库房名称
                                        "档案室已存",
                                        data.houseCapacity - data.houseFree,
                                        "档案室剩余",
                                        data.houseFree,
                                    )
                                }

                            } else {
                                showWarningToast("获取库房库存信息-为空")
                            }
                        } else {
                            showWarningToast(response.getString("msg"))
                        }
                    } else {
                        showWarningToast("获取库房库存信息-失败")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showErrorToast("获取库房库存信息-失败")
                }
            },
            { error ->
                val msg = if (error != null)
                    if (error.networkResponse != null)
                        "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                    else
                        "errorCode: -1 VolleyError: $error"
                else {
                    "errorCode: -1 VolleyError: 未知"
                }
                showErrorToast(msg)
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    /**
     * 创建圆饼图
     *  // 档案室总容量
    data.houseCapacity
    // 档案室剩余量
    data.houseFree
    // 当前柜已存
    data.cabinetUsed
    // 当前柜剩余
    data.cabinetFree
     */
    private fun createPieChart(
        pieChartHeight: Int,
        title: String,
        subTitle1: String,
        value1: Int,
        subTitle2: String,
        value2: Int,
    ) {
        val layoutParams = mGuideBinding.pieChart.layoutParams
        layoutParams.height = pieChartHeight
        mGuideBinding.pieChart.layoutParams = layoutParams

        mGuideBinding.pieChart.description.isEnabled = false
        mGuideBinding.pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        mGuideBinding.pieChart.dragDecelerationFrictionCoef = 0.95f

        // 是否显示中间字体圆
        mGuideBinding.pieChart.isDrawHoleEnabled = true
        // 设置中间字体圆大小
        mGuideBinding.pieChart.holeRadius = 70f
        // 设置中间字体圆颜色
        mGuideBinding.pieChart.setHoleColor(Color.WHITE)
        // 设置阴影内圆大小
        mGuideBinding.pieChart.transparentCircleRadius = 73f
        // 设置阴影内圆颜色
        mGuideBinding.pieChart.setTransparentCircleColor(Color.WHITE)
        mGuideBinding.pieChart.setTransparentCircleAlpha(110)
        // 设置中间字
        mGuideBinding.pieChart.setDrawCenterText(true)
        val tf = Typeface.createFromAsset(assets, "fonts/OpenSans-ExtraBold.ttf")
        mGuideBinding.pieChart.setCenterTextTypeface(tf)

        // mGuideBinding.pieChart.centerText =
        // generateCenterSpannableText("库房名称", "库房已存", yc, "库房剩余", sy)
        val yc = DecimalFormatUtil.formatMoney(value1.toDouble())
        val sy = DecimalFormatUtil.formatMoney(value2.toDouble())
        mGuideBinding.pieChart.centerText =
            generateCenterSpannableText(title, subTitle1, yc, subTitle2, sy)

        mGuideBinding.pieChart.rotationAngle = 0f
        // enable rotation of the  mGuideBinding.pieChart by touch
        // enable rotation of the chart by touch
        mGuideBinding.pieChart.isRotationEnabled = true
        mGuideBinding.pieChart.isHighlightPerTapEnabled = true

        //  mGuideBinding.pieChart.setUnit(" €");
        //  mGuideBinding.pieChart.setDrawUnitsInChart(true);

        // add a selection listener
        //  mGuideBinding.pieChart.setOnChartValueSelectedListener(this)

        mGuideBinding.pieChart.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);

        val l: Legend = mGuideBinding.pieChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f
        l.isEnabled = false;

        // entry label styling
        // 数据字体颜色
        mGuideBinding.pieChart.setEntryLabelColor(Color.WHITE)
        mGuideBinding.pieChart.setEntryLabelTypeface(tf)
        mGuideBinding.pieChart.setEntryLabelTextSize(14f)

        val percentYC = (value1.toFloat() / (value1 + value2) * 100)
        val percentSY = (value2.toFloat() / (value1 + value2) * 100)

        LogUtils.e("已存：", percentYC, "剩余:", percentSY)
        setPieCharData(percentYC, percentSY)
    }

    private fun generateCenterSpannableText(
        storageName: String,
        t1: String,
        v1: String,
        t2: String,
        v2: String
    ): SpannableString? {
        val s = SpannableString(storageName + "\n" + t1 + "\n" + v1 + "\n" + t2 + "\n" + v2)

        // Typeface.ITALIC 斜体
        // 第1个参数-库房名称
        if (SelfComm.DEVICE_NAME[1].equals(deviceName) || SelfComm.DEVICE_NAME[2].equals(deviceName)) { // 档案组架1/档案组柜2
            s.setSpan(RelativeSizeSpan(4.5f), 0, storageName.length, 0)
        } else if (SelfComm.DEVICE_NAME[3].equals(deviceName)) { // 档案单柜3
            s.setSpan(RelativeSizeSpan(5.0f), 0, storageName.length, 0)
        } else if (SelfComm.DEVICE_NAME[4].equals(deviceName)) { // 一体机4
            s.setSpan(RelativeSizeSpan(3.0f), 0, storageName.length, 0)
        } else if (SelfComm.DEVICE_NAME[5].equals(deviceName)) { // PDA5
            s.setSpan(RelativeSizeSpan(5.0f), 0, storageName.length, 0)
        }

        s.setSpan(StyleSpan(Typeface.BOLD), 0, storageName.length, 0)
        s.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_white_zt_gray)),
            0,
            storageName.length,
            0
        )

        // 第2个参数-库房已存/本柜已存
        s.setSpan(
            RelativeSizeSpan(1.5f),
            storageName.length,
            storageName.length + t1.length + 1,
            0
        )
        s.setSpan(
            StyleSpan(Typeface.NORMAL),
            storageName.length,
            storageName.length + t1.length + 1,
            0
        )
        s.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_white_zt)),
            storageName.length,
            storageName.length + t1.length + 1,
            0
        )

        // 第3个参数-已存值
        s.setSpan(
            RelativeSizeSpan(3.0f),
            storageName.length + t1.length + 1,
            storageName.length + t1.length + v1.length + 2,
            0
        )
        s.setSpan(
            StyleSpan(Typeface.ITALIC),
            storageName.length + t1.length + 1,
            storageName.length + t1.length + v1.length + 2,
            0
        )
        s.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.md_teal_A700)),
            storageName.length + t1.length + 1,
            storageName.length + t1.length + v1.length + 2,
            0
        )

        // 第4个参数-库房剩余/本柜剩余
        s.setSpan(
            RelativeSizeSpan(1.5f),
            storageName.length + t1.length + v1.length + 2,
            storageName.length + t1.length + v1.length + t2.length + 3,
            0
        )
        s.setSpan(
            StyleSpan(Typeface.NORMAL),
            storageName.length + t1.length + v1.length + 2,
            storageName.length + t1.length + v1.length + t2.length + 3,
            0
        )
        s.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_white_zt)),
            storageName.length + t1.length + v1.length + 2,
            storageName.length + t1.length + v1.length + t2.length + 3,
            0
        )

        // 第5个参数-剩余值
        s.setSpan(
            RelativeSizeSpan(3.5f),
            storageName.length + t1.length + v1.length + t2.length + 3,
            storageName.length + t1.length + v1.length + t2.length + v2.length + 4,
            0
        )
        s.setSpan(
            StyleSpan(Typeface.ITALIC),
            storageName.length + t1.length + v1.length + t2.length + 3,
            storageName.length + t1.length + v1.length + t2.length + v2.length + 4,
            0
        )
        s.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.md_teal_A700)),
            storageName.length + t1.length + v1.length + t2.length + 3,
            storageName.length + t1.length + v1.length + t2.length + v2.length + 4,
            0
        )

        return s
    }

    private fun setPieCharData(percentYC: Float, percentSY: Float) {
        val entries: ArrayList<PieEntry> = ArrayList()
        val colors: ArrayList<Int> = ArrayList()

        // 已存量 = 库房容量-库房剩余
        entries.add(PieEntry(percentYC, "已存"))
        colors.add(resources.getColor(R.color.color_white_zt))
        // 剩余量/库房剩余
        entries.add(PieEntry(percentSY, "剩余"))
        colors.add(resources.getColor(R.color.md_teal_A700)) // md_teal_A700

        val dataSet = PieDataSet(entries, "Election Results")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        // 百分数显示字体大小
        data.setValueTextSize(18f)
        data.setValueTextColor(Color.WHITE)

        val tf = Typeface.createFromAsset(assets, "fonts/OpenSans-ExtraBold.ttf")
        data.setValueTypeface(tf)
        mGuideBinding.pieChart.data = data

        // undo all highlights
        mGuideBinding.pieChart.highlightValues(null)

        // 按百分比显示,这个参数设置没屌用
        // mGuideBinding.pieChart.setUsePercentValues(true)

        // 刷新圆饼图
        mGuideBinding.pieChart.invalidate()

    }

    class PercentFormatter() : ValueFormatter() {
        var mFormat: DecimalFormat
        private var pieChart: PieChart? = null

        // Can be used to remove percent signs if the chart isn't in percent mode
        constructor(pieChart: PieChart?) : this() {
            this.pieChart = pieChart
        }

        override fun getFormattedValue(value: Float): String {
            return mFormat.format(value.toDouble()) + " %"
        }

        override fun getPieLabel(value: Float, pieEntry: PieEntry): String {
            return if (pieChart != null && pieChart!!.isUsePercentValuesEnabled) {
                // Converted to percent
                getFormattedValue(value)
            } else {
                // raw value, skip percent sign
                // mFormat.format(value.toDouble())
                getFormattedValue(value)
            }
        }

        init {
            mFormat = DecimalFormat("###,###,##0.0")
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            DEVICE_REGISTERED, DEVICE_REMOVED -> {
                // 前来注册的读写器设备ID
                val deviceID = msg.obj.toString()
                // 一体机读写器设备注册需要保存设备ID P0
                if (SelfComm.DEVICE_NAME[4].equals(deviceName)) {
                    val layoutManager = LinearLayoutManager(this)
                    layoutManager.orientation = LinearLayoutManager.VERTICAL
                    mGuideBinding.guideCabinetOnlineStatusRv.layoutManager = layoutManager
                    mCabinetOnlineAdapter = CabinetOnlineAdapter(this, mCabinetOnlineList)
                    mGuideBinding.guideCabinetOnlineStatusRv.adapter = mCabinetOnlineAdapter

                    // 一体机始终只连接来注册的那唯一一个设备,先清空掉,来注册就添加进去
                    DeviceService.getInstance().deleteAll()

                    val device = Device()
                    // device.deviceName = mDeviceInformationList[0].deviceSerialPath + "-" + mDeviceInformationList[0].deviceSerialBaudRate
                    device.deviceName = "RFID扫描设备"
                    device.deviceId = deviceID
                    DeviceService.getInstance().insert(device)
                    LogUtils.e("添加一体机读写器-读写器设备ID:$deviceID")

                    mCabinetOnlineList.clear()
                    mCabinetOnlineList.add(
                        CabinetOnlineInfo(
                            device.deviceId,
                            device.deviceName,
                            msg.what == DEVICE_REGISTERED
                        )
                    )

                    mCabinetOnlineAdapter.notifyDataSetChanged()
                }

                // 档案组柜2 / 档案单柜3
                if (SelfComm.DEVICE_NAME[2].equals(deviceName)
                    || SelfComm.DEVICE_NAME[3].equals(deviceName)
                ) {

//                if (SelfComm.ONLINE_DEVICE.contains(deviceID)){
//                    if (msg.what == DEVICE_REMOVED) SelfComm.ONLINE_DEVICE.remove(deviceID)
//                } else {
//                    if (msg.what == DEVICE_REGISTERED) SelfComm.ONLINE_DEVICE.add(deviceID)
//                }

                    // 档案组柜/档案单柜
                    for (cabinetOnlineInfo in mCabinetOnlineList) {
                        if (cabinetOnlineInfo.mCode == deviceID) { // 设置界面配置的读写器设备ID
                            cabinetOnlineInfo.isOnLine = msg.what == DEVICE_REGISTERED
                            break
                        }
                    }
                    mCabinetOnlineAdapter.notifyDataSetChanged()
                }

            }

            LOGIN_BY_PWD_SUCCESS -> {
                mProgressDialog.dismiss()
                showSuccessToast("登录成功，${msg.obj}")
                speek("登录成功，${msg.obj}")
                intentActivity(MainMenuActivity.newIntent(this))
            }
            LOGIN_BY_PWD_ERROR -> {
                mProgressDialog.dismiss()
                showErrorToast(msg.obj.toString())
            }
            CARD_LOGIN_SUCCESS -> {
                mProgressDialog.dismiss()
                showSuccessToast("登录成功，${msg.obj}")
                speek("登录成功，${msg.obj}")
                intentActivity(MainMenuActivity.newIntent(this))
            }
            CARD_LOGIN_ERROR -> {
                mProgressDialog.dismiss()
                showErrorToast(msg.obj.toString())
            }
            LOGIN_BY_FACE_SUCCESS -> {
                isFaceHandle = false

                mProgressDialog.dismiss()
                showSuccessToast("登录成功，${msg.obj}")
                speek("登录成功，${msg.obj}")
                intentActivity(MainMenuActivity.newIntent(this))
            }
            LOGIN_BY_FACE_ERROR -> {
                isFaceHandle = false

                mProgressDialog.dismiss()
                showErrorToast(msg.obj.toString())
            }
            LOGIN_BY_FACE_REGIST -> {
                // 当前在主页面,且人脸Dialog显示的前提下,进行登录
                if (mFaceDialog != null && mFaceDialog!!.isShowing && ActivityUtil.isTopActivity(
                        applicationContext, "com.zk.cabinet.activity.GuideActivity"
                    )
                ) {
                    // 关闭人脸弹窗
                    mFaceDialog!!.lottieAnimationView.cancelAnimation()
                    mFaceDialog!!.dismiss()

                    if (!isFaceHandle) {
                        isFaceHandle = true
                        loginByIdCard(msg.obj.toString())
                    } else {
                        LogUtils.e("人脸识别服务-正在登录中")
                    }
                }
            }
            LOGIN_BY_FACE_NO_REGIST -> {
                if (mFaceDialog != null && mFaceDialog!!.isShowing && ActivityUtil.isTopActivity(
                        applicationContext, "com.zk.cabinet.activity.GuideActivity"
                    )
                ) {
                    // 关闭人脸弹窗
                    mFaceDialog!!.lottieAnimationView.cancelAnimation()
                    mFaceDialog!!.dismiss()

                    showWarningToast("${msg.obj}")
                    speek("${msg.obj}")
                }
            }

        }
    }

    private fun initShimmerTitle(houseName: String) {
        // 隐藏英文显示
        mGuideBinding.stvEnglishTitle.visibility = View.GONE
        // 设置字体
        val tf = Typeface.createFromAsset(assets, "fonts/OpenSans-ExtraBold.ttf")
        mGuideBinding.stvChineseTitle.text = houseName

        mGuideBinding.stvChineseTitle.typeface = tf
        mGuideBinding.stvEnglishTitle.typeface = tf
        // 开启动效
        val shimmer = Shimmer()
        shimmer.duration = 5000
        shimmer.direction = Shimmer.ANIMATION_DIRECTION_LTR
        shimmer.start<ShimmerTextView>(mGuideBinding.stvChineseTitle)
        shimmer.start<ShimmerTextView>(mGuideBinding.stvEnglishTitle)
    }

    private fun initShimmerTitlePDA() {
        // 设置字体
        val tf = Typeface.createFromAsset(assets, "fonts/OpenSans-ExtraBold.ttf")
        mGuideBinding.stvChineseTitle.typeface = tf
        mGuideBinding.stvEnglishTitle.typeface = tf
        // 开启动效
        val shimmer = Shimmer()
        shimmer.duration = 5000
        shimmer.direction = Shimmer.ANIMATION_DIRECTION_LTR
        shimmer.start<ShimmerTextView>(mGuideBinding.stvChineseTitle)
        shimmer.start<ShimmerTextView>(mGuideBinding.stvEnglishTitle)
    }

    /**
     * 打开档案组架灯控串口
     * 灯控串口是需要先配置的
     */
    private fun openLightSerialPort() {
        val port = mSpUtil.getString(Key.LightsSerialSelected, "")
        if (!TextUtils.isEmpty(port)) {
            LightsSerialPortHelper.getInstance().close()
            LightsSerialPortHelper.getInstance().open(port)
            LogUtils.e("打开灯控串口:$port")
            // showSuccessToast("打开灯控串口:$port")
            // 测试档案架亮大灯
            // LightsSerialPortHelper.getInstance().openBigLight(1)
        } else {
            showErrorToast("灯控串口未配置")
        }
    }

    /**
     * 初始化MQTT
     */
    private fun initMQTT() {
        MQTTHelper.getInstance().connectMQTT(this)
    }

    /**
     * 档案单柜/档案组柜
     */
    private fun initUR800ByTcpIp() {
        val deviceList = DeviceService.getInstance().loadAll()
        if (deviceList != null && deviceList.size > 0) {
            for (device in deviceList) {
                mCabinetOnlineList.add(
                    CabinetOnlineInfo(
                        device.deviceId,
                        device.deviceName,
                        false
                    )
                )
            }

            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            mGuideBinding.guideCabinetOnlineStatusRv.layoutManager = layoutManager
            mCabinetOnlineAdapter = CabinetOnlineAdapter(this, mCabinetOnlineList)
            mGuideBinding.guideCabinetOnlineStatusRv.adapter = mCabinetOnlineAdapter

            // 启动自己等待读写器连接(服务器已启动)
            val serverPort = mSpUtil.getInt(Key.CabinetServicePort, 7880)
            UR880Entrance.getInstance().init(UR880Entrance.CONNECTION_TCP_IP, serverPort, null)
            UR880Entrance.getInstance()
                .addOnDeviceInformationListener(mDeviceInformationListener)
            UR880Entrance.getInstance().connect()
        }
    }

    /**
     * 一体机(串口连读写器)
     */
    private fun initUR800BySerialPort() {
        val port = mSpUtil.getString(SharedPreferencesUtil.Key.YTJDxqSerialSelected, "")
        if (!TextUtils.isEmpty(port)) {
            LogUtils.e("读写器连接的串口：$port")
            /**
             * mDeviceInformationList 需要打开的串口列表
             */
            val deviceInformation = DeviceInformation();
            deviceInformation.deviceSerialPath = port;
            deviceInformation.deviceSerialBaudRate = "115200";
            mDeviceInformationList.add(deviceInformation)

            UR880Entrance.getInstance()
                .init(UR880Entrance.CONNECTION_SERIAL, null, mDeviceInformationList);
            UR880Entrance.getInstance()
                .addOnDeviceInformationListener(mDeviceInformationListener)
            UR880Entrance.getInstance().connect()
        }
    }

    /**
     * 打开刷卡设备串口
     * /dev/ttyS1 串口需要配置和调试，才能找到是哪个串口
     */
    private fun openCardSerialPort() {
        val port = mSpUtil.getString(SharedPreferencesUtil.Key.SKQSerialSelected, "")
        if (!TextUtils.isEmpty(port)) {
            CardSerialPortHelper.getInstance().open(port)
            CardSerialPortHelper.getInstance().setDataReceivedListener { data ->
                Utils.runOnUiThread {
                    LogUtils.e("解析后卡号数据:$data")
                    if (mCardDialog != null && mCardDialog!!.isShowing) {
                        mCardDialog!!.lottieAnimationView.cancelAnimation()
                        mCardDialog!!.dismiss()

                        showNormalToast("卡号:$data")
                        // 刷卡登录
                        loginByCardNo(data)
                    }
                }
            }
        } else {
            showWarningToast("请先配置刷卡器串口")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.lav_pwd_login -> {
                // todo 测试档案柜亮大灯
//                val lights = ArrayList<Int>()
//                val deviceList = DeviceService.getInstance().loadAll()
//                if (deviceList.size > 0) {
//                    val deviceId = deviceList[0].deviceId
//                    lights.add(1)
//                    UR880Entrance.getInstance().send(
//                        UR880SendInfo.Builder().turnOnLight(deviceId, 6, lights).build()
//                    )
//                    LogUtils.e("亮大灯")
//
//                    val lights1 = ArrayList<Int>()
//                    for (index in 1..24 step 1) {
//                        lights1.add(index)
//                    }
//                    UR880Entrance.getInstance().send(
//                        UR880SendInfo.Builder().turnOnLight(deviceId, 1, lights1).build()
//                    )
//                    LogUtils.e("亮第一层灯")
//                }

                showLoginDialog()
            }
            R.id.lav_face_login -> {
                // todo 测试档案柜灭全灯
//                val deviceList = DeviceService.getInstance().loadAll()
//                if (deviceList.size > 0) {
//                    val deviceId = deviceList[0].deviceId
//                    val lights = ArrayList<Int>()
//                    lights.add(2)
//                    UR880Entrance.getInstance().send(
//                        UR880SendInfo.Builder().turnOnLight(deviceId, 6, lights).build()
//                    )
//                    LogUtils.e("全灭")
//                }

                showFaceDialog()
            }
            R.id.lav_card_login -> {
                showCardDialog()
            }

            //登录弹窗的确认按钮
            R.id.dialog_other_login_sure_btn -> {
                val userCode =
                    mDialogLoginBinding!!.dialogOtherLoginAccountEdt.text.toString().trim()
                val pwd = mDialogLoginBinding!!.dialogOtherLoginPwdEdt.text.toString().trim()
                if (!TextUtils.isEmpty(userCode) && !TextUtils.isEmpty(pwd)) {
                    loginByPwd(userCode, pwd)
                } else {
                    showWarningToast(resources.getString(R.string.fill_complete))
                }
            }
            //登录弹窗的取消按钮
            R.id.dialog_other_login_dismiss_btn -> {
                dismissLoginDialog()
            }

            // 设置
            R.id.ll_setting -> {
                showSettingDialog()
            }

            // 设置输入管理员密码弹窗的确认按钮
            R.id.dialog_setting_confirm -> {
                val adminPassword =
                    mSettingDialogBinding!!.eidtAdminPassword.text.toString().trim()
                if (!TextUtils.isEmpty(adminPassword)) {
                    if (adminPassword == ADMIN_PASSWORD || adminPassword == ADMIN_PASSWORD_ADMIN) {
                        dismissSettingDialog()
                        intentActivity(SystemSettingsActivity.newIntent(this))
                    } else {
                        showErrorToast("管理员密码错误，请重新输入")
                    }
                } else {
                    showWarningToast("请输入管理员密码")
                }
            }
            // 设置输入管理员密码弹窗的取消按钮
            R.id.dialog_setting_dismiss -> {
                dismissSettingDialog()
            }

            // 档案查询
            R.id.tv_search -> {
                intentActivity(SearchActivity.newIntent(this))
            }

            // PDA-选择档案室
            R.id.tv_house -> {
                if (optionsHousePickerView == null) {
                    showWarningToast("暂无档案室可选择")
                } else {
                    optionsHousePickerView!!.show()
                }
            }

        }
    }

    // 进入设置的管理员密码弹窗
    private fun showSettingDialog() {
        if (mSettingDialog == null) {
            mSettingDialog = AlertDialog.Builder(this).create()
            mSettingDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.dialog_setting,
                null,
                false
            )
            mSettingDialogBinding!!.onClickListener = this
            mSettingDialog!!.setView(mSettingDialogBinding!!.root)
            mSettingDialog!!.setCancelable(true)
        }
        mSettingDialogBinding!!.eidtAdminPassword.text = null
        mSettingDialogBinding!!.eidtAdminPassword.isFocusable = true
        mSettingDialogBinding!!.eidtAdminPassword.isFocusableInTouchMode = true
        mSettingDialogBinding!!.eidtAdminPassword.requestFocus()
        mSettingDialog!!.show()

        val window = mSettingDialog!!.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        // 一体机4 横屏显示需要适配
        if (SelfComm.DEVICE_NAME[4].equals(deviceName)) {
            window!!.setLayout(
                resources.displayMetrics.widthPixels * 3 / 5,
                resources.displayMetrics.heightPixels * 3 / 5
            )
        } else {
            window!!.setLayout(
                resources.displayMetrics.widthPixels * 2 / 3,
                resources.displayMetrics.heightPixels * 2 / 5
            )
        }
    }

    override fun onLongClick(v: View?): Boolean {
        when (v?.id) {
            // 登录确认按钮长按进入设置
//            R.id.dialog_other_login_sure_btn -> {
//                intentActivity(SystemSettingsActivity.newIntent(this))
//            }
        }
        return false
    }

    override fun intentActivity(intent: Intent?) {
        dismissLoginDialog()
        // FingerprintParsingLibrary.getInstance().setFingerprintVerify(false)
        super.intentActivity(intent)
    }

    private class MainHandler(guideActivity: GuideActivity) : Handler() {
        private val mainWeakReference = WeakReference(guideActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mainWeakReference.get()!!.handleMessage(msg)
        }
    }

    private val mDeviceInformationListener = object : DeviceInformationListener {
        override fun heartbeat(p0: String?) {
            Log.w("zx-设备-心跳-", "heartbeat -----p0: $p0")
        }

        override fun versionInformation(p0: String?, p1: String?, p2: String?) {
            Log.e("zx-设备-信息-", "versionInformation -----p0: $p0 ---p1: $p1 ---p2: $p2")
        }

        override fun registered(p0: String?, p1: String?, p2: String?) {
            Log.e("zx-设备-注册-", "registered -----p0: $p0 ---p1: $p1 ---p2: $p2")
            val message = Message.obtain()
            message.what = DEVICE_REGISTERED
            message.obj = p0
            mHandler.sendMessage(message)
        }

        override fun removed(p0: String?) {
            Log.e("zx-设备-移除-", "removed -----p0: $p0 ")
            val message = Message.obtain()
            message.what = DEVICE_REMOVED
            message.obj = p0
            mHandler.sendMessage(message)
        }

    }

    // 刷卡登录弹窗
    private fun showCardDialog() {
        if (mCardDialog == null) {
            mCardDialog = AlertDialog.Builder(this).create()
            mCardDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_card, null,
                false
            )
            mCardDialog!!.setView(mCardDialogBinding!!.root)
            mCardDialog!!.setCancelable(true)
            mCardDialog!!.setOnCancelListener {
                mCardDialog!!.lottieAnimationView.cancelAnimation()
                mCardDialog!!.dismiss()
                CardSerialPortHelper.getInstance().close()
            }
        }
        mCardDialog!!.show()
        mCardDialog!!.lottieAnimationView.playAnimation()

        val window = mCardDialog!!.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        // 一体机4 横屏显示需要适配
        if (SelfComm.DEVICE_NAME[4].equals(deviceName)) {
            window!!.setLayout(
                resources.displayMetrics.widthPixels * 1 / 2,
                resources.displayMetrics.heightPixels * 3 / 5
            )
        } else {
            window!!.setLayout(
                resources.displayMetrics.widthPixels * 4 / 5,
                resources.displayMetrics.heightPixels * 2 / 5
            )
        }

        openCardSerialPort()
    }

    private lateinit var mFaceRecognitionHttpServer: FaceRecognitionHttpServer

    // 人脸识别弹窗
    private fun showFaceDialog() {
        if (mFaceDialog == null) {
            mFaceDialog = AlertDialog.Builder(this).create()
            mFaceDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_face, null,
                false
            )
            mFaceDialog!!.setView(mFaceDialogBinding!!.root)
            mFaceDialog!!.setCancelable(true)
            mFaceDialog!!.setOnCancelListener {
                mFaceDialog!!.lottieAnimationView.cancelAnimation()
                mFaceDialog!!.dismiss()
            }
        }
        mFaceDialog!!.show()
        mFaceDialog!!.lottieAnimationView.playAnimation()

        val window = mFaceDialog!!.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        // 一体机4 横屏显示需要适配
        if (SelfComm.DEVICE_NAME[4].equals(deviceName)) {
            window!!.setLayout(
                resources.displayMetrics.widthPixels * 1 / 2,
                resources.displayMetrics.heightPixels * 3 / 5
            )
        } else {
            window!!.setLayout(
                resources.displayMetrics.widthPixels * 4 / 5,
                resources.displayMetrics.heightPixels * 2 / 5
            )
        }
    }

    // 人脸识别是否在处理
    private var isFaceHandle = false

    /**
     * 开启人脸识别服务
     */
    private fun startFaceServer() {
        val faceServerPort = mSpUtil.getInt(Key.FaceServicePort, 8080)
        try {
            mFaceRecognitionHttpServer =
                FaceRecognitionHttpServer(faceServerPort, object : FaceRecognitionListener {
                    override fun success(result: String) {
                        // 人脸已注册 ,获取人脸对应的身份证号, 要在人脸web地址中录入人脸的时候配置
                        val resultSuccess = JSON.parseObject(result, ResultSuccess::class.java)
                        LogUtils.e("人脸识别服务-人脸认证成功", "识别人脸对应的身份证号码:" + resultSuccess.info.idCard)

                        val msg = Message.obtain()
                        msg.what = LOGIN_BY_FACE_REGIST
                        msg.obj = resultSuccess.info.idCard
                        mHandler.sendMessageDelayed(msg, 0)
                    }

                    override fun noRegister(result: String) {
                        LogUtils.e("人脸识别服务", "人脸未注册 noRegister")
                        // 提示人脸未注册
                        val msg = Message.obtain()
                        msg.what = LOGIN_BY_FACE_NO_REGIST
                        msg.obj = "人脸未注册"
                        mHandler.sendMessageDelayed(msg, 0)
                    }

                    override fun heart(result: String) {
                        LogUtils.e("人脸识别服务", "心跳信息 heart")
                    }
                })

            // 启动人连识别web服务
            if (!mFaceRecognitionHttpServer.isAlive) {
                mFaceRecognitionHttpServer.start()
            }

            LogUtils.e(
                "人脸识别服务",
                "人脸识别服务本机服务器-开启成功-IP:" + NetworkUtils.getIPAddress(true) + ",port=" + faceServerPort
            )
        } catch (e: Exception) {
            if (mFaceRecognitionHttpServer.isAlive)
                mFaceRecognitionHttpServer.stop()
            LogUtils.e("人脸识别服务", "人脸识别服务本机服务器-开启失败. e = $e")
            showErrorToast("人脸识别服务本机服务器-开启失败. e = $e")
        }
    }

    // 登录弹窗
    private fun showLoginDialog() {
        if (mDialogLogin == null) {
            mDialogLogin = AlertDialog.Builder(this).create()
            mDialogLoginBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.dialog_login,
                null,
                false
            )
            mDialogLoginBinding!!.onClickListener = this
            mDialogLoginBinding!!.onLongClickListener = this
            mDialogLogin!!.setView(mDialogLoginBinding!!.root)
            mDialogLogin!!.setCancelable(true)
        }
        mDialogLoginBinding!!.dialogOtherLoginAccountEdt.text = null
        mDialogLoginBinding!!.dialogOtherLoginPwdEdt.text = null
        mDialogLoginBinding!!.dialogOtherLoginAccountEdt.isFocusable = true
        mDialogLoginBinding!!.dialogOtherLoginAccountEdt.isFocusableInTouchMode = true
        mDialogLoginBinding!!.dialogOtherLoginAccountEdt.requestFocus()
        mDialogLogin!!.show()

        val window = mDialogLogin!!.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        // 一体机4 横屏显示需要适配
        if (SelfComm.DEVICE_NAME[4].equals(deviceName)) {
            window!!.setLayout(
                resources.displayMetrics.widthPixels * 3 / 5,
                resources.displayMetrics.heightPixels * 3 / 5
            )
        } else {
            window!!.setLayout(
                resources.displayMetrics.widthPixels * 2 / 3,
                resources.displayMetrics.heightPixels * 2 / 5
            )
        }
    }

    // 关闭登录弹窗
    private fun dismissLoginDialog() {
        if (mDialogLogin != null && mDialogLogin!!.isShowing) mDialogLogin!!.dismiss()
    }

    // 关闭设置弹窗
    private fun dismissSettingDialog() {
        if (mSettingDialog != null && mSettingDialog!!.isShowing) mSettingDialog!!.dismiss()
    }

    /**
     * 用户名密码登录
     */
    private fun loginByPwd(user: String, pwd: String) {
        mProgressDialog.show()

        val jsonObject = JSONObject()
        try {
//            jsonObject.put("username", "admin")
//            jsonObject.put("password", "admin123")
            jsonObject.put("username", user.trim())
            jsonObject.put("password", pwd.trim())

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, NetworkRequest.instance.mClientLogin,
            jsonObject, { response ->
                LogUtils.e("登录-返回:$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val mRequestResult: ResultApiLogin =
                                JSON.parseObject(
                                    "$response",
                                    ResultApiLogin::class.java
                                )

                            val data = mRequestResult.data
                            if (data != null) {
                                // 保存token
                                mSpUtil.applyValue(
                                    Record(
                                        Key.Token, data.token
                                    )
                                )
                                // 保存用户名
                                mSpUtil.applyValue(
                                    Record(
                                        Key.NameTemp, data.user.nickName
                                    )
                                )

                                val msg = Message.obtain()
                                msg.what = LOGIN_BY_PWD_SUCCESS
                                msg.obj = data.user.nickName
                                mHandler.sendMessageDelayed(msg, 1000)
                            }

                        } else {
                            val msg = Message.obtain()
                            msg.what = LOGIN_BY_PWD_ERROR
                            msg.obj = response!!.getString("msg")
                            mHandler.sendMessageDelayed(msg, 1000)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    val msg = Message.obtain()
                    msg.what = LOGIN_BY_PWD_ERROR
                    msg.obj = "数据解析失败"
                    mHandler.sendMessageDelayed(msg, 1000)
                }
            }, { error ->
                val msg = if (error != null)
                    if (error.networkResponse != null)
                        "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                    else
                        "errorCode: -1 VolleyError: $error"
                else {
                    "errorCode: -1 VolleyError: 未知"
                }
                val message = Message.obtain()
                message.what = LOGIN_BY_PWD_ERROR
                message.obj = msg
                mHandler.sendMessageDelayed(message, 1000)
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    /**
     * 刷卡登录, 用户卡号绑定身份证号码, 通过卡号找身份证号码即找到用户
     * 21. 根据卡号直接登录
     * 接口地址：Post /loginByCardNo
     */
    private fun loginByCardNo(cardNo: String) {
        mProgressDialog.show()

        val jsonObject = JSONObject()
        try {
            jsonObject.put("dataValue", cardNo.trim())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, NetworkRequest.instance.mLoginByCardNo,
            jsonObject, { response ->
                LogUtils.e("登录-返回:$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val mRequestResult: ResultApiLogin =
                                JSON.parseObject(
                                    "$response",
                                    ResultApiLogin::class.java
                                )

                            val data = mRequestResult.data
                            if (data != null) {
                                // 保存token
                                mSpUtil.applyValue(
                                    Record(
                                        Key.Token, data.token
                                    )
                                )
                                // 保存用户名
                                mSpUtil.applyValue(
                                    Record(
                                        Key.NameTemp, data.user.nickName
                                    )
                                )

                                val msg = Message.obtain()
                                msg.what = CARD_LOGIN_SUCCESS
                                msg.obj = data.user.nickName
                                mHandler.sendMessageDelayed(msg, 1000)
                            } else {
                                val msg = Message.obtain()
                                msg.what = CARD_LOGIN_ERROR
                                msg.obj = response!!.getString("msg")
                                mHandler.sendMessageDelayed(msg, 1000)
                            }

                        } else {
                            val msg = Message.obtain()
                            msg.what = CARD_LOGIN_ERROR
                            msg.obj = response!!.getString("msg")
                            mHandler.sendMessageDelayed(msg, 1000)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    val msg = Message.obtain()
                    msg.what = CARD_LOGIN_ERROR
                    msg.obj = "数据解析失败"
                    mHandler.sendMessageDelayed(msg, 1000)
                }
            }, { error ->
                val msg = if (error != null)
                    if (error.networkResponse != null)
                        "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                    else
                        "errorCode: -1 VolleyError: $error"
                else {
                    "errorCode: -1 VolleyError: 未知"
                }
                val message = Message.obtain()
                message.what = CARD_LOGIN_ERROR
                message.obj = msg
                mHandler.sendMessageDelayed(message, 1000)
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    /**
     *  人脸登录, 根据人脸识别后拿到的身份证号登录
     *  21. 根据身份证号码直接登录
     *接口地址：  Post    /loginByIdCard
     */
    private fun loginByIdCard(idcard: String) {
        mProgressDialog.show()

        val jsonObject = JSONObject()
        try {
            jsonObject.put("dataValue", idcard.trim())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, NetworkRequest.instance.mLoginByIdCard,
            jsonObject, { response ->
                LogUtils.e("登录-返回:$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val mRequestResult: ResultApiLogin =
                                JSON.parseObject(
                                    "$response",
                                    ResultApiLogin::class.java
                                )

                            val data = mRequestResult.data
                            if (data != null) {
                                // 保存token
                                mSpUtil.applyValue(
                                    Record(
                                        Key.Token, data.token
                                    )
                                )
                                // 保存用户名
                                mSpUtil.applyValue(
                                    Record(
                                        Key.NameTemp, data.user.nickName
                                    )
                                )

                                val msg = Message.obtain()
                                msg.what = LOGIN_BY_FACE_SUCCESS
                                msg.obj = data.user.nickName
                                mHandler.sendMessageDelayed(msg, 1000)
                            } else {
                                val msg = Message.obtain()
                                msg.what = LOGIN_BY_FACE_ERROR
                                msg.obj = response!!.getString("msg")
                                mHandler.sendMessageDelayed(msg, 1000)
                            }

                        } else {
                            val msg = Message.obtain()
                            msg.what = LOGIN_BY_FACE_ERROR
                            msg.obj = response!!.getString("msg")
                            mHandler.sendMessageDelayed(msg, 1000)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    val msg = Message.obtain()
                    msg.what = LOGIN_BY_FACE_ERROR
                    msg.obj = "数据解析失败"
                    mHandler.sendMessageDelayed(msg, 1000)
                }
            }, { error ->
                val msg = if (error != null)
                    if (error.networkResponse != null)
                        "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                    else
                        "errorCode: -1 VolleyError: $error"
                else {
                    "errorCode: -1 VolleyError: 未知"
                }
                val message = Message.obtain()
                message.what = LOGIN_BY_FACE_ERROR
                message.obj = msg
                mHandler.sendMessageDelayed(message, 1000)
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    /**
     * 静默安装Apk并重启APP
     * 设备必须已破解(获得ROOT权限)
     */
    private fun suInstallApp(apkPath: String, delayTime: Long): Boolean {
        LogUtils.e("获取最新App版本信息-静默安装-发送安装指令:apkPath", apkPath, "延迟时间(毫秒)", delayTime)

        var printWriter: PrintWriter? = null
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec("su")
            printWriter = PrintWriter(process.outputStream)
            printWriter.println("chmod 777 $apkPath")
            printWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib")
            printWriter.println("pm install -r $apkPath")
            // printWriter.println("exit");
            printWriter.flush()
            printWriter.close()

            // 通过AlarmManager来实现重启APP
            LogUtils.e("获取最新App版本信息-静默安装-${delayTime}毫秒后重启APP")
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            val restartIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            val mgr: AlarmManager = this.getSystemService(ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 6.0及以上
                mgr.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + delayTime,
                    restartIntent
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 4.4及以上
                mgr.setExact(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + delayTime,
                    restartIntent
                )
            }

            // 当在进行新版本安装时,这句会把程序kill掉,所以在这句之后的逻辑不会继续往下执行，所以重启的操作要放到process.waitFor();之前
            val value = process.waitFor()
            LogUtils.e("获取最新App版本信息-静默安装-结果返回值：$value")

            return value == 0
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            LogUtils.e("获取最新App版本信息-静默安装-安装Apk出现异常", e.message)
        } finally {
            process?.destroy()
        }
        return false
    }

}

