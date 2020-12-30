package com.zk.cabinet.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Process
import android.provider.Settings
import android.text.InputType
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android_serialport_api.SerialPortFinder
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.constant.SelfComm
import com.zk.cabinet.databinding.ActivitySystemSettingsBinding
import com.zk.cabinet.db.DBHelper
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.dialog.*
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.cabinet.utils.SharedPreferencesUtil.Record
import com.zk.common.utils.ActivityUtil
import com.zk.common.utils.AppVersionUtil
import com.zk.common.utils.LogUtil
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.ur880.UR880Entrance
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class SystemSettingsActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mSystemSettingsBinding: ActivitySystemSettingsBinding
    private lateinit var mHandler: SystemSettingsHandler

    private var isShowRestartNowForSet = false                               // 是否显示应为设置修而重启

    private lateinit var mDeviceCode: String                                 //设备编号
    private lateinit var mUnitNumber: String                                 //单位编号
    private lateinit var mUnitAddress: String                                //单位地址
    private var mDeviceCodeDialog: UniversalEdtDialog? = null
    private var mUnitNumberDialog: UniversalEdtDialog? = null
    private var mUnitAddressDialog: UniversalEdtDialog? = null

    private lateinit var mEth0IP: String                                     //以太网0IP
    private lateinit var mEth0SubnetMask: String                             //以太网0子网掩码
    private lateinit var mEth0Gateway: String                                //以太网0网关
    private lateinit var mEth0DNS: String                                    //以太网0dns
    private lateinit var mEth1IP: String                                     //以太网1IP
    private lateinit var mEth1SubnetMask: String                             //以太网1子网掩码

    private var mCabinetServicePort by Delegates.notNull<Int>()          //本地柜体netty端口
    private var mFaceServicePort by Delegates.notNull<Int>()          // 人脸设备服务端口
    private var mEth0SetDialog: Eth0Dialog? = null
    private var mEth1SetDialog: Eth1Dialog? = null

    // WEB服务器地址配置Dialog
    private var mWebApiServiceDialog: WebApiServiceDialog? = null
    private lateinit var mWebApiServiceIp: String                            //webAPI
    private var mWebApiServicePort by Delegates.notNull<Int>()           //webAPI端口

    // MQTT服务器地址配置Dialog
    private var mMQTTServiceDialog: MQTTServiceDialog? = null
    private lateinit var mMQTTServiceIp: String
    private var mMQTTServicePort by Delegates.notNull<Int>()

    // 人脸设备服务端口Dialog
    private var mFaceServicePortDialog: UniversalEdtDialog? = null

    private var mCabinetServicePortDialog: UniversalEdtDialog? = null

    // 操作屏设备ID/一体机设备ID dialog
    private var mEquipmentIdDialog: UniversalEdtDialog? = null
    private lateinit var mEquipmentId: String        // 操作屏设备ID/一体机设备ID

    // 通道门读写器设备ID
    private var mTdmDeviceIdDialog: UniversalEdtDialog? = null
    private lateinit var mTdmDeviceId: String

    private lateinit var mDAJArrays: Array<String>                     //档案架数量
    private var mDAJNumberSelected by Delegates.notNull<Int>()

    private lateinit var mLightsSerialSelected: String // 选择的灯控串口
    private lateinit var mYTJDxqSerialSelected: String // 选择的一体机读写器串口
    private lateinit var mSKQSerialSelected: String // 选择的刷卡设备串口

    private lateinit var mNumberBoxesItems: Array<String>                     //档案柜数量
    private var mNumberBoxesItemSelected by Delegates.notNull<Int>()
    private lateinit var mAlarmTimeItems: Array<String>                       //未关门吗报警时间
    private lateinit var mAlarmTimeValueItems: IntArray
    private var mAlarmTimeItemSelected by Delegates.notNull<Int>()
    private lateinit var mTooManyFilesNumbers: Array<String>                  //卷宗数量过多提醒
    private var mTooManyFilesNumberSelected by Delegates.notNull<Int>()

    private lateinit var mSynchronisedTimes: Array<String>                    //自动同步周期
    private lateinit var mSynchronisedTimeValues: IntArray
    private var mSynchronisedTimeSelected by Delegates.notNull<Int>()
    private lateinit var mCountdownItems: Array<String>                        //自动返回主界面
    private lateinit var mCountdownItemValues: IntArray
    private var mCountdownSelected by Delegates.notNull<Int>()
    private var mRestart: Boolean = true                                       //是否自动重启
    private var mRestartStartTimeHourOfDay by Delegates.notNull<Int>()
    private var mRestartTaskDialog: TaskSetDialog? = null
    private var mSoundSwitch: Boolean = true                                   //提示音

    private var mCheckDoorStatus: Boolean = true                               //防盗机制
    private var mCalibration: Boolean = true                                   //闲时校准
    private var mCalibrationTimeHourOfDay by Delegates.notNull<Int>()
    private var mCalibrationTaskDialog: TaskSetDialog? = null

    private var mDebug: Boolean = false                                        //debug bool
    private var mPingProgressDialog: ProgressDialog? = null
    private var mPingDialog: PingDialog? = null

    companion object {
        private const val SYSTEM_SETTING_PING_START = 0x01
        private const val SYSTEM_SETTING_PING_RESULT = 0x02

        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, SystemSettingsActivity::class.java)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            SYSTEM_SETTING_PING_START -> {
                if (mPingProgressDialog == null) {
                    mPingProgressDialog = ProgressDialog(this)
                    mPingProgressDialog!!.setTitle(R.string.title_network_test)
                }
                mPingProgressDialog!!.setMessage(
                    String.format(
                        resources.getString(R.string.ping_message),
                        msg.obj.toString()
                    )
                )
                mPingProgressDialog!!.show()
            }
            SYSTEM_SETTING_PING_RESULT -> {
                if (mPingProgressDialog != null && mPingProgressDialog!!.isShowing) mPingProgressDialog!!.dismiss()
                Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSystemSettingsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_system_settings)

        mSystemSettingsBinding.onClickListener = this
        setSupportActionBar(mSystemSettingsBinding.systemSettingToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mHandler = SystemSettingsHandler(this)

        initDate()
        initView()

    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mSystemSettingsBinding.systemSettingCountdownTv.text = millisUntilFinished.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initDate() {
        isShowRestartNowForSet = mSpUtil.getBoolean(Key.RestartNowForSet, false)

        mDeviceCode = mSpUtil.getString(Key.DeviceCode, resources.getString(R.string.null_prompt))!!
        mUnitNumber =
            mSpUtil.getString(Key.OrgCodeTemp, resources.getString(R.string.null_prompt))!!
        mUnitAddress =
            mSpUtil.getString(Key.UnitAddress, resources.getString(R.string.null_prompt))!!

        // WEB服务器配置
//        mWebApiServiceIp = mSpUtil.getString(Key.WebApiServiceIp, resources.getString(R.string.air))!!
        mWebApiServiceIp =
            mSpUtil.getString(Key.WebApiServiceIp, resources.getString(R.string.air))!!
        mWebApiServicePort = mSpUtil.getInt(Key.WebApiServicePort, -1)
        // MQTT服务器配置
        mMQTTServiceIp = mSpUtil.getString(Key.MQTTServiceIp, resources.getString(R.string.air))!!
        mMQTTServicePort = mSpUtil.getInt(Key.MQTTServicePort, -1)


        mEth0IP = mSpUtil.getString(Key.Eth0IP, resources.getString(R.string.air))!!
        mEth0SubnetMask = mSpUtil.getString(Key.Eth0SubnetMask, resources.getString(R.string.air))!!
        mEth0Gateway = mSpUtil.getString(Key.Eth0Gateway, resources.getString(R.string.air))!!
        mEth0DNS = mSpUtil.getString(Key.Eth0DNS, resources.getString(R.string.air))!!
        mEth1IP = mSpUtil.getString(Key.Eth1IP, resources.getString(R.string.air))!!
        mEth1SubnetMask = mSpUtil.getString(Key.Eth1SubnetMask, resources.getString(R.string.air))!!


        // 操作屏设备ID/读写器设备ID
        mEquipmentId = mSpUtil.getString(Key.EquipmentId, "")!!
        // 通道门读写器设备ID
        mTdmDeviceId = mSpUtil.getString(Key.TdmDeviceId, "")!!
        // 刷卡设备选择的串口
        mSKQSerialSelected = mSpUtil.getString(Key.SKQSerialSelected, "")!!
        // 读写器配置
        // 一体机读写器选择的串口
        mYTJDxqSerialSelected = mSpUtil.getString(Key.YTJDxqSerialSelected, "")!!
        // 档案架配置
        mDAJArrays = resources.getStringArray(R.array.dialog_number_of_daj)
        // 选择配置档案架数量
        mDAJNumberSelected = mSpUtil.getInt(Key.DAJNumberSelected, -1)
        // 灯控选择的串口
        mLightsSerialSelected = mSpUtil.getString(Key.LightsSerialSelected, "")!!
        // 档案柜配置
        val deviceName = mSpUtil.getString(Key.DeviceName, "").toString()
        // 档案组柜(可添加10个档案柜)档案单柜（只能添加一个档案柜）
        mNumberBoxesItemSelected = mSpUtil.getInt(Key.NumberOfBoxesSelected, -1)
        if (SelfComm.DEVICE_NAME[2].equals(deviceName)) {
            mNumberBoxesItems = resources.getStringArray(R.array.dialog_number_of_boxes_array)
        } else if (SelfComm.DEVICE_NAME[3].equals(deviceName)) {
            mNumberBoxesItems = resources.getStringArray(R.array.dialog_number_of_cabinet_single)
        }

        // 柜体端口
        mCabinetServicePort = mSpUtil.getInt(Key.CabinetServicePort, 7880)
        // 人脸设备服务端口
        mFaceServicePort = mSpUtil.getInt(Key.FaceServicePort, 8080)


        mAlarmTimeItems = resources.getStringArray(R.array.dialog_not_closed_door_alarm_time_array)
        mAlarmTimeValueItems =
            resources.getIntArray(R.array.dialog_not_closed_door_alarm_time_value_array)
        mAlarmTimeItemSelected = mSpUtil.getInt(Key.NotClosedDoorAlarmTime, 1)
        for (indices in mAlarmTimeValueItems.indices) {
            if (mAlarmTimeItemSelected == mAlarmTimeValueItems[indices]) {
                mAlarmTimeItemSelected = indices
                break
            }
        }
        mTooManyFilesNumbers = resources.getStringArray(R.array.too_many_files_number_array)
        mTooManyFilesNumberSelected = mSpUtil.getInt(Key.TooManyFilesNumber, 40)
        for (indices in mTooManyFilesNumbers.indices) {
            if (mTooManyFilesNumberSelected == mTooManyFilesNumbers[indices].toInt()) {
                mTooManyFilesNumberSelected = indices
                break
            }
        }

        mSynchronisedTimes = resources.getStringArray(R.array.synchronised_time_array)
        mSynchronisedTimeValues =
            resources.getIntArray(R.array.synchronised_time_value_array)
        mSynchronisedTimeSelected = mSpUtil.getInt(Key.SyncInterval, 15)
        for (indices in mSynchronisedTimeValues.indices) {
            if (mSynchronisedTimeSelected == mSynchronisedTimeValues[indices].toInt()) {
                mSynchronisedTimeSelected = indices
                break
            }
        }

        // 自动返回主界面时间
        mCountdownItems = resources.getStringArray(R.array.countdown_array)
        mCountdownItemValues = resources.getIntArray(R.array.countdown_value_array)
        // 自动返回主界面时间设置，点这里
        mCountdownSelected = mCountdown
        for (indices in mCountdownItemValues.indices) {
            if (mCountdownSelected == mCountdownItemValues[indices].toInt()) {
                mCountdownSelected = indices
                break
            }
        }
        mRestart = mSpUtil.getBoolean(Key.Restart, true)
        mRestartStartTimeHourOfDay = mSpUtil.getInt(Key.RestartStartTimeHourOfDay, 22)
        mSoundSwitch = mSpUtil.getBoolean(Key.SoundSwitch, true)

        mCheckDoorStatus = mSpUtil.getBoolean(Key.CheckDoorStatus, true)
        mCalibration = mSpUtil.getBoolean(Key.Calibration, true)
        mCalibrationTimeHourOfDay = mSpUtil.getInt(Key.CalibrationTimeHourOfDay, 1)

        mDebug = mSpUtil.getBoolean(Key.Debug, false)
    }

    private fun initView() {
//        SelfComm.DEVICE_NAME[1] = "档案组架"
//        SelfComm.DEVICE_NAME[2] = "档案组柜"
//        SelfComm.DEVICE_NAME[3] = "档案单柜"
//        SelfComm.DEVICE_NAME[4] = "一体机"
//        SelfComm.DEVICE_NAME[5] = "PDA"

        // 根据不同设备类型显示不同的配置
        val deviceName = mSpUtil.getString(Key.DeviceName, "").toString()
        // 档案组架 1
        if (SelfComm.DEVICE_NAME[1].equals(deviceName)) {
            // 隐藏一体机配置
            mSystemSettingsBinding.cvYtjSetting.visibility = View.GONE
            // 隐藏档案柜配置
            mSystemSettingsBinding.cvDagSetting.visibility = View.GONE
            // 隐藏刷卡设备配置
            mSystemSettingsBinding.cvSkqSetting.visibility = View.GONE
            // 隐藏人脸设备配置
            mSystemSettingsBinding.cvFaceSetting.visibility = View.GONE
            // 隐藏通道门配置
            mSystemSettingsBinding.cvTdmSetting.visibility = View.GONE
        }

        // 档案组柜 2
        if (SelfComm.DEVICE_NAME[2].equals(deviceName)) {
            // 隐藏一体机配置
            mSystemSettingsBinding.cvYtjSetting.visibility = View.GONE
            // 隐藏档案架配置
            mSystemSettingsBinding.cvDajSetting.visibility = View.GONE
            // 隐藏刷卡设备配置
            mSystemSettingsBinding.cvSkqSetting.visibility = View.GONE
            // 隐藏通道门配置
            mSystemSettingsBinding.cvTdmSetting.visibility = View.GONE
        }

        // 档案单柜 3
        if (SelfComm.DEVICE_NAME[3].equals(deviceName)) {
            // 隐藏一体机配置
            mSystemSettingsBinding.cvYtjSetting.visibility = View.GONE
            // 隐藏档案架配置
            mSystemSettingsBinding.cvDajSetting.visibility = View.GONE
            // 隐藏刷卡设备配置
            mSystemSettingsBinding.cvSkqSetting.visibility = View.GONE
            // 显示打开柜门
            mSystemSettingsBinding.systemSettingOpenDoor.visibility = View.VISIBLE

            // 隐藏组大灯灯控串口设置 和 灯控调试
            mSystemSettingsBinding.systemSettingLightSerialZg.visibility = View.GONE
            mSystemSettingsBinding.systemSettingLightSerialDebugZg.visibility = View.GONE

            // 隐藏通道门配置
            mSystemSettingsBinding.cvTdmSetting.visibility = View.GONE
        }

        // 一体机 4
        if (SelfComm.DEVICE_NAME[4].equals(deviceName)) {
            // 隐藏MQTT服务
            mSystemSettingsBinding.systemSettingMqtt.visibility = View.GONE
            // 隐藏档案架配置
            mSystemSettingsBinding.cvDajSetting.visibility = View.GONE
            // 隐藏档案柜配置
            mSystemSettingsBinding.cvDagSetting.visibility = View.GONE
            // 隐藏操作屏设备ID配置
            mSystemSettingsBinding.cvEquipmentIdSetting.visibility = View.GONE

            // 隐藏通道门配置
            mSystemSettingsBinding.cvTdmSetting.visibility = View.GONE
        }

        // PDA 5
        if (SelfComm.DEVICE_NAME[5].equals(deviceName)) {
            // 隐藏MQTT服务
            mSystemSettingsBinding.systemSettingMqtt.visibility = View.GONE
            // 隐藏一体机配置
            mSystemSettingsBinding.cvYtjSetting.visibility = View.GONE
            // 隐藏档案架配置
            mSystemSettingsBinding.cvDajSetting.visibility = View.GONE
            // 隐藏档案柜配置
            mSystemSettingsBinding.cvDagSetting.visibility = View.GONE
            // 隐藏操作屏设备ID配置
            mSystemSettingsBinding.cvEquipmentIdSetting.visibility = View.GONE
            // 隐藏刷卡设备配置
            mSystemSettingsBinding.cvSkqSetting.visibility = View.GONE
            // 隐藏人脸设备配置
            mSystemSettingsBinding.cvFaceSetting.visibility = View.GONE
            // 隐藏Android文件管理器
            mSystemSettingsBinding.systemSettingFileManagerSb.visibility = View.GONE
            // 隐藏通道门配置
            mSystemSettingsBinding.cvTdmSetting.visibility = View.GONE
        }

        // 通道门 6
        if (SelfComm.DEVICE_NAME[6].equals(deviceName)) {
            // 隐藏操作屏设备ID配置
            mSystemSettingsBinding.cvEquipmentIdSetting.visibility = View.GONE
            // 隐藏一体机配置
            mSystemSettingsBinding.cvYtjSetting.visibility = View.GONE
            // 隐藏档案架配置
            mSystemSettingsBinding.cvDajSetting.visibility = View.GONE
            // 隐藏档案柜配置
            mSystemSettingsBinding.cvDagSetting.visibility = View.GONE
            // 隐藏刷卡设备配置
            mSystemSettingsBinding.cvSkqSetting.visibility = View.GONE
            // 隐藏人脸设备配置
            mSystemSettingsBinding.cvFaceSetting.visibility = View.GONE
        }

        // 所选设备类型
        mSystemSettingsBinding.systemSettingDeviceType.setCaptionText(
            deviceName
        )

        mSystemSettingsBinding.systemSettingRestartLl.visibility =
            if (isShowRestartNowForSet) View.VISIBLE else View.GONE

        mSystemSettingsBinding.systemSettingDeviceCodeSb.setCaptionText(mDeviceCode)
        mSystemSettingsBinding.systemSettingUnitNumberSb.setCaptionText(mUnitNumber)
        mSystemSettingsBinding.systemSettingUnitAddressSb.setCaptionText(mUnitAddress)

        mSystemSettingsBinding.systemSettingEthernetSb.setCaptionText(
            if (TextUtils.isEmpty(mEth0IP)) resources.getString(
                R.string.null_prompt
            ) else String.format(
                resources.getString(R.string.eth_0_caption_text),
                mEth0IP,
                mEth0SubnetMask,
                mEth0Gateway,
                mEth0DNS
            )
        )
        mSystemSettingsBinding.systemSettingInternalEthernetSb.setCaptionText(
            if (TextUtils.isEmpty(
                    mEth1IP
                )
            ) resources.getString(R.string.null_prompt) else String.format(
                resources.getString(R.string.eth_1_caption_text),
                mEth1IP,
                mEth1SubnetMask
            )
        )

        // WEB服务器设置
        mSystemSettingsBinding.systemSettingWebApiServiceSb.setCaptionText(
            if (mWebApiServicePort == -1) resources.getString(
                R.string.null_prompt
            )
            else String.format(
                resources.getString(R.string.web_api_caption_text),
                mWebApiServiceIp,
                mWebApiServicePort
            )
        )
        // MQTT服务器设置
        mSystemSettingsBinding.systemSettingMqtt.setCaptionText(
            if (mMQTTServicePort == -1) resources.getString(
                R.string.null_prompt
            )
            else String.format(
                resources.getString(R.string.web_api_caption_text),
                mMQTTServiceIp,
                mMQTTServicePort
            )
        )

        // 柜体端口
        mSystemSettingsBinding.systemSettingCabinetServicePortSb.setCaptionText(
            // 默认7880
//            if (mCabinetServicePort == -1) resources.getString(
//                R.string.null_prompt
//            ) else String.format(
//                resources.getString(R.string.cabinet_port_caption_text),
//                mCabinetServicePort
//            )

            String.format(
                resources.getString(R.string.cabinet_port_caption_text),
                mCabinetServicePort
            )
        )
        // 人脸设备服务端口
        mSystemSettingsBinding.systemSettingFacePort.setCaptionText(
            // 默认8080
            String.format(
                resources.getString(R.string.cabinet_port_caption_text),
                mFaceServicePort
            )
        )

        // 通道门读写器设备ID
        mSystemSettingsBinding.systemSettingTdmDeviceId.setCaptionText(
            if (TextUtils.isEmpty(mTdmDeviceId)) {
                "未配置通道门读写器设备ID!"
            } else String.format(
                resources.getString(R.string.cabinet_port_caption_text),
                mTdmDeviceId
            )
        )

        // 操作屏设备ID
        mSystemSettingsBinding.systemSettingEquipmentId.setCaptionText(
            if (TextUtils.isEmpty(mEquipmentId)) {
                "未配置操作屏设备ID!"
            } else String.format(
                resources.getString(R.string.cabinet_port_caption_text),
                mEquipmentId
            )
        )

        // 一体机设备ID
        mSystemSettingsBinding.systemSettingEquipmentIdYtj.setCaptionText(
            if (TextUtils.isEmpty(mEquipmentId)) {
                "未配置一体机设备ID!"
            } else String.format(
                resources.getString(R.string.cabinet_port_caption_text),
                mEquipmentId
            )
        )

        // 一体机 读写器串口配置
        mSystemSettingsBinding.systemSettingYtjSerial.setCaptionText(
            if (!TextUtils.isEmpty(mYTJDxqSerialSelected)) {
                "读写器串口:$mYTJDxqSerialSelected"
            } else {
                "未设置读写器串口!"
            }
        )

        // 刷卡设备串口配置
        mSystemSettingsBinding.systemSettingSkqSerial.setCaptionText(
            if (!TextUtils.isEmpty(mSKQSerialSelected)) {
                "刷卡设备串口:$mSKQSerialSelected"
            } else {
                "未设置刷卡设备串口!"
            }
        )

        // 档案组架-灯控串口配置
        mSystemSettingsBinding.systemSettingLightSerial.setCaptionText(
            if (!TextUtils.isEmpty(mLightsSerialSelected)) {
                "灯控串口:$mLightsSerialSelected"
            } else {
                "未设置灯控串口!"
            }
        )

        // 档案组柜-组大灯灯控串口配置
        mSystemSettingsBinding.systemSettingLightSerialZg.setCaptionText(
            if (!TextUtils.isEmpty(mLightsSerialSelected)) {
                "组大灯灯控串口:$mLightsSerialSelected"
            } else {
                "未设置组大灯灯控串口!"
            }
        )

        // 档案柜数量
        mSystemSettingsBinding.systemSettingNumberOfBoxesSb.setCaptionText(
            if (mNumberBoxesItemSelected == -1) {
                "未添加档案柜!"
            } else {
                String.format(
                    resources.getString(R.string.number_of_boxes_caption_text),
                    mNumberBoxesItems[mNumberBoxesItemSelected]
                )
            }

        )
        mSystemSettingsBinding.systemSettingNotClosedDoorAlarmTimeSb.setCaptionText(
            String.format(
                resources.getString(R.string.not_closed_door_alarm_time_caption_text),
                mAlarmTimeItems[mAlarmTimeItemSelected]
            )
        )
        mSystemSettingsBinding.systemSettingTooManyFilesNumberSb.setCaptionText(
            String.format(
                resources.getString(R.string.too_many_files_number),
                mTooManyFilesNumbers[mTooManyFilesNumberSelected]
            )
        )

        mSystemSettingsBinding.systemSettingSynchronisedTimeSb.setCaptionText(
            String.format(
                resources.getString(R.string.synchronised_time_caption_text),
                mSynchronisedTimes[mSynchronisedTimeSelected]
            )
        )
        mSystemSettingsBinding.systemSettingCountdownSb.setCaptionText(
            String.format(
                resources.getString(R.string.countdown_caption_text),
                mCountdownItems[mCountdownSelected]
            )
        )
        mSystemSettingsBinding.systemSettingRestartSb.setCaptionText(
            if (!mRestart) resources.getString(R.string.turn_off_automatic_task)
            else String.format(
                resources.getString(R.string.automatic_restart_caption_text),
                mRestartStartTimeHourOfDay
            )
        )
        mSystemSettingsBinding.systemSettingSoundSwitchSb.setChecked(mSoundSwitch)
        mSystemSettingsBinding.systemSettingSoundSwitchSb.setCaptionText(
            if (mSoundSwitch) getString(R.string.sound_switch_on) else
                getString(R.string.sound_switch_off)
        )

        mSystemSettingsBinding.systemSettingAntiTheftSb.setChecked(mCheckDoorStatus)
        mSystemSettingsBinding.systemSettingAntiTheftSb.setCaptionText(
            if (mCheckDoorStatus) getString(R.string.anti_theft_mechanism_on) else
                getString(R.string.anti_theft_mechanism_off)
        )
        mSystemSettingsBinding.systemSettingCalibrationIdlerSb.setCaptionText(
            if (!mCalibration) resources.getString(R.string.turn_off_automatic_task)
            else String.format(
                resources.getString(R.string.calibration_idler_caption_text),
                mCalibrationTimeHourOfDay
            )
        )

        mSystemSettingsBinding.systemSettingAppVersionSb.setCaptionText(
            AppVersionUtil.appVersionNameForShow(
                this
            )
        )

        mSystemSettingsBinding.systemSettingDebugSv.setChecked(mDebug)
        mSystemSettingsBinding.systemSettingDebugSv.setCaptionText(
            if (mDebug) getString(R.string.debug_on) else getString(R.string.debug_off)
        )
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.system_setting_device_code_sb -> {
                if (mDeviceCodeDialog == null) {
                    mDeviceCodeDialog =
                        UniversalEdtDialog(R.string.device_id,
                            object : UniversalEdtDialog.InputListener {
                                override fun onInputComplete(input: String) {
                                    if (mDeviceCode != input) {
                                        mDeviceCode = input
                                        mSpUtil.applyValue(
                                            Record(
                                                Key.DeviceCode,
                                                mDeviceCode
                                            )
                                        )
                                        mSystemSettingsBinding.systemSettingDeviceCodeSb.setCaptionText(
                                            mDeviceCode
                                        )
                                    }
                                }
                            })
                    mDeviceCodeDialog!!.mInputType = InputType.TYPE_CLASS_TEXT
                }
                mDeviceCodeDialog!!.mMessage = mDeviceCode
                mDeviceCodeDialog!!.show(supportFragmentManager, "DeviceCode")
            }
            R.id.system_setting_unit_number_sb -> {
                if (mUnitNumberDialog == null) {
                    mUnitNumberDialog =
                        UniversalEdtDialog(R.string.unit_number,
                            object : UniversalEdtDialog.InputListener {
                                override fun onInputComplete(input: String) {
                                    if (mUnitNumber != input) {
                                        mUnitNumber = input
                                        mSpUtil.applyValue(
                                            Record(
                                                Key.UnitNumber,
                                                mUnitNumber
                                            )
                                        )
                                        mSystemSettingsBinding.systemSettingUnitNumberSb.setCaptionText(
                                            mUnitNumber
                                        )
                                    }
                                }
                            })
                    mUnitNumberDialog!!.mInputType = InputType.TYPE_CLASS_TEXT
                }
                mUnitNumberDialog!!.mMessage = mUnitNumber
                mUnitNumberDialog!!.show(supportFragmentManager, "UnitNumber")
            }
            R.id.system_setting_unit_address_sb -> {
                if (mUnitAddressDialog == null) {
                    mUnitAddressDialog =
                        UniversalEdtDialog(R.string.unit_address,
                            object : UniversalEdtDialog.InputListener {
                                override fun onInputComplete(input: String) {
                                    if (mUnitAddress != input) {
                                        mUnitAddress = input
                                        mSpUtil.applyValue(
                                            Record(
                                                Key.UnitAddress,
                                                mUnitAddress
                                            )
                                        )
                                        mSystemSettingsBinding.systemSettingUnitAddressSb.setCaptionText(
                                            mUnitAddress
                                        )
                                    }
                                }
                            })
                    mUnitAddressDialog!!.mInputType = InputType.TYPE_CLASS_TEXT
                }
                mUnitAddressDialog!!.mMessage = mUnitAddress
                mUnitAddressDialog!!.show(supportFragmentManager, "UnitAddress")
            }


            R.id.system_setting_ethernet_sb -> {
                if (mEth0SetDialog == null) {
                    mEth0SetDialog =
                        Eth0Dialog(R.string.device_ethernet, object : Eth0Dialog.InputListener {
                            override fun onInputComplete(
                                ethIP: String,
                                ethSubnetMask: String,
                                ethGateway: String,
                                ethDNS: String
                            ) {
                                mEth0IP = ethIP
                                mEth0SubnetMask = ethSubnetMask
                                mEth0Gateway = ethGateway
                                mEth0DNS = ethDNS
                                val eth0SetList = ArrayList<Record>()
                                eth0SetList.add(Record(Key.Eth0IP, mEth0IP))
                                eth0SetList.add(Record(Key.Eth0SubnetMask, mEth0SubnetMask))
                                eth0SetList.add(Record(Key.Eth0Gateway, mEth0Gateway))
                                eth0SetList.add(Record(Key.Eth0DNS, mEth0DNS))
                                mSpUtil.applyValue(eth0SetList)
                                mSystemSettingsBinding.systemSettingEthernetSb.setCaptionText(
                                    String.format(
                                        resources.getString(R.string.eth_0_caption_text),
                                        mEth0IP,
                                        mEth0SubnetMask,
                                        mEth0Gateway,
                                        mEth0DNS
                                    )
                                )
//                                SmdtUtil.instance
//                                    .setEthIPAddress(
//                                        mEth0IP,
//                                        mEth0SubnetMask,
//                                        mEth0Gateway,
//                                        mEth0DNS
//                                    )
                                showRestartNowForSet()
                            }
                        })
                }
                mEth0SetDialog!!.mEthIP = mEth0IP
                mEth0SetDialog!!.mEthSubnetMask = mEth0SubnetMask
                mEth0SetDialog!!.mEthGateway = mEth0Gateway
                mEth0SetDialog!!.mEthDNS = mEth0DNS
                mEth0SetDialog!!.show(supportFragmentManager, "Ethernet")
            }
            R.id.system_setting_internal_ethernet_sb -> {
                if (mEth1SetDialog == null) {
                    mEth1SetDialog =
                        Eth1Dialog(
                            R.string.device_internal_ethernet,
                            object : Eth1Dialog.InputListener {
                                override fun onInputComplete(
                                    ethIP: String,
                                    ethSubnetMask: String
                                ) {
                                    mEth1IP = ethIP
                                    mEth1SubnetMask = ethSubnetMask
                                    val eth1SetList = ArrayList<Record>()
                                    eth1SetList.add(Record(Key.Eth1IP, mEth1IP))
                                    eth1SetList.add(Record(Key.Eth1SubnetMask, mEth1SubnetMask))
                                    mSpUtil.applyValue(eth1SetList)
                                    mSystemSettingsBinding.systemSettingInternalEthernetSb.setCaptionText(
                                        String.format(
                                            resources.getString(R.string.eth_1_caption_text),
                                            mEth1IP,
                                            mEth1SubnetMask
                                        )
                                    )
//                                    SmdtUtil.instance
//                                        .configEthernet1(
//                                            true,
//                                            mEth1IP,
//                                            mEth1SubnetMask
//                                        )
                                    showRestartNowForSet()
                                }
                            })
                }
                mEth1SetDialog!!.mEthIP = mEth1IP
                mEth1SetDialog!!.mEthSubnetMask = mEth1SubnetMask
                mEth1SetDialog!!.show(supportFragmentManager, "InternalEthernet")
            }

            // 设备类型
            R.id.system_setting_device_type -> {
                // 是否切换设备类型?
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.device_type_change_title))
                    .setMessage(R.string.device_type_change_msg)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setPositiveButton(getString(R.string.sure)) { _, _ ->
                        // 清空SP数据
                        mSpUtil.clear()
                        // 清空数据库数据
                        DBHelper.getInstance().clear()
                        // 重启APP至设备选择界面
                        restartApp2()
                    }
                    .show()
            }

            // 档案单柜-打开柜门
            R.id.system_setting_open_door -> {
                val deviceList = DeviceService.getInstance().loadAll()
                if (deviceList != null && deviceList.size > 0) {
                    val device = deviceList[0]
                    if (device != null) {
                        UR880Entrance.getInstance()
                            .send(UR880SendInfo.Builder().openDoor(device.deviceId, 0x00).build())
                        LogUtils.e("读写器设备ID:" + device.deviceId)
                    }
                } else {
                    showWarningToast("请先添加档案柜，保证档案柜读写器已在线")
                }
            }

            // 本机固定IP配置
            R.id.system_setting_this_machine_ip -> {
                // 打开系统网络设置
                NetworkUtils.openWirelessSettings()
            }

            // web服务器配置
            R.id.system_setting_web_api_service_sb -> {
                if (mWebApiServiceDialog == null) {
                    mWebApiServiceDialog =
                        WebApiServiceDialog(
                            R.string.web_api_service,
                            object : WebApiServiceDialog.InputListener {
                                override fun onInputComplete(
                                    webApiServiceIp: String,
                                    webApiServicePort: Int
                                ) {
                                    mWebApiServiceIp = webApiServiceIp
                                    mWebApiServicePort = webApiServicePort
                                    NetworkRequest.instance.configModify(
                                        mWebApiServiceIp,
                                        mWebApiServicePort
                                    )
                                    val webApiServiceList = ArrayList<Record>()
                                    webApiServiceList.add(
                                        Record(
                                            Key.WebApiServiceIp,
                                            mWebApiServiceIp
                                        )
                                    )
                                    webApiServiceList.add(
                                        Record(
                                            Key.WebApiServicePort,
                                            mWebApiServicePort
                                        )
                                    )
                                    mSpUtil.applyValue(webApiServiceList)
                                    mSystemSettingsBinding.systemSettingWebApiServiceSb.setCaptionText(
                                        String.format(
                                            resources.getString(R.string.web_api_caption_text),
                                            mWebApiServiceIp,
                                            mWebApiServicePort
                                        )
                                    )
                                }
                            })
                }
                mWebApiServiceDialog!!.mWebApiServiceIp = mWebApiServiceIp
                mWebApiServiceDialog!!.mWebApiServicePort = mWebApiServicePort
                mWebApiServiceDialog!!.show(supportFragmentManager, "WebApiService")
            }

            // MQTT服务器配置
            R.id.system_setting_mqtt -> {
                if (mMQTTServiceDialog == null) {
                    mMQTTServiceDialog =
                        MQTTServiceDialog(
                            R.string.title_mqtt,
                            object : MQTTServiceDialog.InputListener {
                                override fun onInputComplete(
                                    mqttIP: String,
                                    mqttPort: Int
                                ) {
                                    mMQTTServiceIp = mqttIP
                                    mMQTTServicePort = mqttPort

                                    NetworkRequest.instance.configModifyMQTT(
                                        mMQTTServiceIp,
                                        mMQTTServicePort
                                    )
                                    val mqttServiceList = ArrayList<Record>()
                                    mqttServiceList.add(
                                        Record(
                                            Key.MQTTServiceIp,
                                            mMQTTServiceIp
                                        )
                                    )
                                    mqttServiceList.add(
                                        Record(
                                            Key.MQTTServicePort,
                                            mMQTTServicePort
                                        )
                                    )
                                    mSpUtil.applyValue(mqttServiceList)

                                    mSystemSettingsBinding.systemSettingMqtt.setCaptionText(
                                        String.format(
                                            resources.getString(R.string.web_api_caption_text),
                                            mMQTTServiceIp,
                                            mMQTTServicePort
                                        )
                                    )
                                }
                            })
                }
                mMQTTServiceDialog!!.mMQTTServiceIp = mMQTTServiceIp
                mMQTTServiceDialog!!.mMQTTServicePort = mMQTTServicePort
                mMQTTServiceDialog!!.show(supportFragmentManager, "MQTTService")
            }

            // 人脸设备服务端口配置
            R.id.system_setting_face_port -> {
                if (mFaceServicePortDialog == null) {
                    mFaceServicePortDialog =
                        UniversalEdtDialog(R.string.face_port,
                            object : UniversalEdtDialog.InputListener {
                                override fun onInputComplete(input: String) {
                                    if (mFaceServicePort != input.trim().toInt()) {
                                        // Socket编程中，IP+端口号就是套接字,端口号是由16比特进行编号，范围是0-65535
                                        // ^[1-9]$|(^[1-9][0-9]$)|(^[1-9][0-9][0-9]$)|(^[1-9][0-9][0-9][0-9]$)|(^[1-6][0-5][0-5][0-3][0-5]$)
                                        // 理应是0-65535.但是Netty sdk中封装可能限制了只能4位数-65535,经测试,有的4位数也不行比如1000,不然init Netty会报错 java.net.SocketException: Permission denied,崩溃
                                        val regex =
                                            Regex("(^[1-9][0-9][0-9][0-9]\$)|(^[1-6][0-5][0-5][0-3][0-5]\$)")
                                        if (!input.trim().matches(regex)) {
                                            showErrorToast("端口输入不符合")
                                        } else {
                                            mFaceServicePort = input.trim().toInt()
                                            // commit()方法是同步执行,有返回值，apply()方法是异步执行,没有返回值
                                            mSpUtil.commitValue(
                                                Record(
                                                    Key.FaceServicePort,
                                                    mFaceServicePort
                                                )
                                            )
                                            mSystemSettingsBinding.systemSettingFacePort.setCaptionText(
                                                String.format(
                                                    resources.getString(R.string.cabinet_port_caption_text),
                                                    mFaceServicePort
                                                )
                                            )
                                            restartApp()
                                        }
                                    }
                                }
                            })
                    mFaceServicePortDialog!!.mInputType = InputType.TYPE_CLASS_NUMBER
                }
                mFaceServicePortDialog!!.mMessage = mFaceServicePort.toString()
                mFaceServicePortDialog!!.show(supportFragmentManager, "FaceServicePort")
            }
            // 人脸设备调试界面
            R.id.system_setting_face_debug -> {
                // 进入人脸设备调试界面
                var intent: Intent = Intent(this, FaceDebugActivity::class.java)
                startActivity(intent)
            }

            // 柜体端口配置
            R.id.system_setting_cabinet_service_port_sb -> {
                if (mCabinetServicePortDialog == null) {
                    mCabinetServicePortDialog =
                        UniversalEdtDialog(R.string.cabinet_service_port,
                            object : UniversalEdtDialog.InputListener {
                                override fun onInputComplete(input: String) {
                                    if (mCabinetServicePort != input.trim().toInt()) {
                                        // Socket编程中，IP+端口号就是套接字,端口号是由16比特进行编号，范围是0-65535
                                        // ^[1-9]$|(^[1-9][0-9]$)|(^[1-9][0-9][0-9]$)|(^[1-9][0-9][0-9][0-9]$)|(^[1-6][0-5][0-5][0-3][0-5]$)
                                        // 理应是0-65535.但是Netty sdk中封装可能限制了只能4位数-65535,经测试,有的4位数也不行比如1000,不然init Netty会报错 java.net.SocketException: Permission denied,崩溃
                                        val regex =
                                            Regex("(^[1-9][0-9][0-9][0-9]\$)|(^[1-6][0-5][0-5][0-3][0-5]\$)")
                                        if (!input.trim().matches(regex)) {
                                            showErrorToast("端口输入不符合")
                                        } else {
                                            mCabinetServicePort = input.trim().toInt()
                                            // commit()方法是同步执行,有返回值，apply()方法是异步执行,没有返回值
                                            mSpUtil.commitValue(
                                                Record(
                                                    Key.CabinetServicePort,
                                                    mCabinetServicePort
                                                )
                                            )
                                            mSystemSettingsBinding.systemSettingCabinetServicePortSb.setCaptionText(
                                                String.format(
                                                    resources.getString(R.string.cabinet_port_caption_text),
                                                    mCabinetServicePort
                                                )
                                            )
                                            restartApp()
                                            // showRestartNowForSet()
                                        }
                                    }
                                }
                            })
                    mCabinetServicePortDialog!!.mInputType = InputType.TYPE_CLASS_NUMBER
                }
                mCabinetServicePortDialog!!.mMessage = mCabinetServicePort.toString()
                mCabinetServicePortDialog!!.show(supportFragmentManager, "CabinetServicePort")
            }

            // 通道门读写器设备ID配置 mTdmDeviceId
            R.id.system_setting_tdm_device_id -> {
                if (mTdmDeviceIdDialog == null) {
                    mTdmDeviceIdDialog =
                        UniversalEdtDialog(R.string.tdm_device_id,
                            object : UniversalEdtDialog.InputListener {
                                override fun onInputComplete(input: String) {
                                    if (mTdmDeviceId != input.trim()) {
                                        mTdmDeviceId = input.trim()
                                        // commit()方法是同步执行,有返回值，apply()方法是异步执行,没有返回值
                                        mSpUtil.commitValue(
                                            Record(
                                                Key.TdmDeviceId,
                                                mTdmDeviceId
                                            )
                                        )
                                        mSystemSettingsBinding.systemSettingTdmDeviceId.setCaptionText(
                                            String.format(
                                                resources.getString(R.string.cabinet_port_caption_text),
                                                mTdmDeviceId
                                            )
                                        )
                                        restartApp()
                                    }
                                }
                            })
                    mTdmDeviceIdDialog!!.mInputType = InputType.TYPE_CLASS_TEXT
                }
                mTdmDeviceIdDialog!!.mMessage = mTdmDeviceId
                mTdmDeviceIdDialog!!.show(supportFragmentManager, "TdmDeviceId")
            }

            // 一体机配置
            // 一体机设备ID配置/sp字段与操作屏设备ID配置一致 mEquipmentId
            R.id.system_setting_equipment_id_ytj -> {
                if (mEquipmentIdDialog == null) {
                    mEquipmentIdDialog =
                        UniversalEdtDialog(R.string.equipment_id_ytj,
                            object : UniversalEdtDialog.InputListener {
                                override fun onInputComplete(input: String) {
                                    if (mEquipmentId != input.trim()) {
                                        mEquipmentId = input.trim()
                                        // commit()方法是同步执行,有返回值，apply()方法是异步执行,没有返回值
                                        mSpUtil.commitValue(
                                            Record(
                                                Key.EquipmentId,
                                                mEquipmentId
                                            )
                                        )
                                        mSystemSettingsBinding.systemSettingEquipmentIdYtj.setCaptionText(
                                            String.format(
                                                resources.getString(R.string.cabinet_port_caption_text),
                                                mEquipmentId
                                            )
                                        )
                                        restartApp()
                                    }
                                }
                            })
                    mEquipmentIdDialog!!.mInputType = InputType.TYPE_CLASS_TEXT
                }
                mEquipmentIdDialog!!.mMessage = mEquipmentId
                mEquipmentIdDialog!!.show(supportFragmentManager, "EquipmentId")
            }

            // 操作屏设备ID配置/sp字段与一体机设备ID配置一致 mEquipmentId
            R.id.system_setting_equipment_id -> {
                if (mEquipmentIdDialog == null) {
                    mEquipmentIdDialog =
                        UniversalEdtDialog(R.string.equipment_id,
                            object : UniversalEdtDialog.InputListener {
                                override fun onInputComplete(input: String) {
                                    if (mEquipmentId != input.trim()) {
                                        mEquipmentId = input.trim()
                                        // commit()方法是同步执行,有返回值，apply()方法是异步执行,没有返回值
                                        mSpUtil.commitValue(
                                            Record(
                                                Key.EquipmentId,
                                                mEquipmentId
                                            )
                                        )
                                        mSystemSettingsBinding.systemSettingEquipmentId.setCaptionText(
                                            String.format(
                                                resources.getString(R.string.cabinet_port_caption_text),
                                                mEquipmentId
                                            )
                                        )
                                        restartApp()
                                    }
                                }
                            })
                    mEquipmentIdDialog!!.mInputType = InputType.TYPE_CLASS_TEXT
                }
                mEquipmentIdDialog!!.mMessage = mEquipmentId
                mEquipmentIdDialog!!.show(supportFragmentManager, "EquipmentId")
            }

            // 一体机串口配置
            R.id.system_setting_ytj_serial -> {
                var serialPortFinder = SerialPortFinder()
                val allDevices = serialPortFinder.allDevices
                val allDevicesPath = serialPortFinder.allDevicesPath
                LogUtils.e("串口列表:", allDevices, allDevicesPath)
                // [/dev/ttyS8, /dev/ttyS7, /dev/ttyS6, /dev/ttyS5, /dev/ttyS3, /dev/ttyS0, /dev/ttyS2, /dev/ttyS4, /dev/ttyS1]

                // 一体机读写器选择的串口
                mYTJDxqSerialSelected = mSpUtil.getString(Key.YTJDxqSerialSelected, "")!!

                var selectSerialIndex = -1
                for ((index, e) in allDevicesPath.withIndex()) {
                    if (e == mYTJDxqSerialSelected) {
                        selectSerialIndex = index
                    }
                }

                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.ytj_serial_setting))
                    .setSingleChoiceItems(
                        allDevicesPath, selectSerialIndex
                    ) { dialogInterface, i ->
                        dialogInterface.dismiss()
                        showSuccessToast("选择的串口:" + allDevicesPath[i])
                        LogUtils.e("选择的串口:", allDevicesPath[i])

                        // 一体机读写器选择的串口
                        mSystemSettingsBinding.systemSettingYtjSerial.setCaptionText(
                            "读写器串口:" + allDevicesPath[i]
                        )

                        mSpUtil.commitValue(
                            Record(
                                Key.YTJDxqSerialSelected,
                                allDevicesPath[i]
                            )
                        )

                        restartApp()
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
            // 一体机读写器调试界面
            R.id.system_setting_ytj_serial_debug -> {
                // 进入一体机读写器调试界面
                val mYTJDxqSerialSelected = mSpUtil.getString(Key.YTJDxqSerialSelected, "")!!
                if (TextUtils.isEmpty(mYTJDxqSerialSelected)) {
                    showWarningToast("请先配置读写器串口")
                    return
                }

                var intent: Intent = Intent(this, YTJSerialDebugActivity::class.java)
                startActivity(intent)
            }

            // 刷卡设备串口配置
            R.id.system_setting_skq_serial -> {
                var serialPortFinder = SerialPortFinder()
                val allDevices = serialPortFinder.allDevices
                val allDevicesPath = serialPortFinder.allDevicesPath
                LogUtils.e("串口列表:", allDevices, allDevicesPath)
                // [/dev/ttyS8, /dev/ttyS7, /dev/ttyS6, /dev/ttyS5, /dev/ttyS3, /dev/ttyS0, /dev/ttyS2, /dev/ttyS4, /dev/ttyS1]

                // 一体机读写器选择的串口
                mSKQSerialSelected = mSpUtil.getString(Key.SKQSerialSelected, "")!!

                var selectSerialIndex = -1
                for ((index, e) in allDevicesPath.withIndex()) {
                    if (e == mSKQSerialSelected) {
                        selectSerialIndex = index
                    }
                }

                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.skq_serial_setting))
                    .setSingleChoiceItems(
                        allDevicesPath, selectSerialIndex
                    ) { dialogInterface, i ->
                        dialogInterface.dismiss()
                        showSuccessToast("选择的串口:" + allDevicesPath[i])
                        LogUtils.e("选择的串口:", allDevicesPath[i])

                        // 一体机读写器选择的串口
                        mSystemSettingsBinding.systemSettingSkqSerial.setCaptionText(
                            "刷卡设备串口:" + allDevicesPath[i]
                        )

                        mSpUtil.commitValue(
                            Record(
                                Key.SKQSerialSelected,
                                allDevicesPath[i]
                            )
                        )

                        restartApp()
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
            // 刷卡设备调试界面
            R.id.system_setting_skq_serial_debug -> {
                // 进入刷卡设备调试界面
                val mSKQSerialSelected = mSpUtil.getString(Key.SKQSerialSelected, "")!!
                if (TextUtils.isEmpty(mSKQSerialSelected)) {
                    showWarningToast("请先配置刷卡设备串口")
                    return
                }

                var intent: Intent = Intent(this, SKQSerialDebugActivity::class.java)
                startActivity(intent)
            }

            // 档案架配置
            // 添加档案架
            R.id.system_setting_daj -> {
                val mEquipmentId = mSpUtil.getString(Key.EquipmentId, "")!!
                if (TextUtils.isEmpty(mEquipmentId)) {
                    showWarningToast("请先配置操作屏设备ID")
                } else {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.title_daj_setting))
                        .setSingleChoiceItems(
                            mDAJArrays, mDAJNumberSelected
                        ) { dialogInterface, i ->
                            dialogInterface.dismiss()

                            var intent: Intent = Intent(this, DAJSettingActivity::class.java)
                            intent.putExtra("DAJNumberSelected", i)
                            startActivity(intent)
                        }
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show()
                }

            }
            // 档案组架-灯控串口设置
            R.id.system_setting_light_serial -> {
                var serialPortFinder = SerialPortFinder()
                val allDevices = serialPortFinder.allDevices
                val allDevicesPath = serialPortFinder.allDevicesPath
                LogUtils.e("串口列表:", allDevices, allDevicesPath)
                // [/dev/ttyS8, /dev/ttyS7, /dev/ttyS6, /dev/ttyS5, /dev/ttyS3, /dev/ttyS0, /dev/ttyS2, /dev/ttyS4, /dev/ttyS1]

                // 灯控选择的串口
                mLightsSerialSelected = mSpUtil.getString(Key.LightsSerialSelected, "")!!
                var selectLightSerialIndex = -1
                for ((index, e) in allDevicesPath.withIndex()) {
                    if (e == mLightsSerialSelected) {
                        selectLightSerialIndex = index
                    }
                }

                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.light_serial_setting))
                    .setSingleChoiceItems(
                        allDevicesPath, selectLightSerialIndex
                    ) { dialogInterface, i ->
                        dialogInterface.dismiss()
                        showSuccessToast("选择的串口:" + allDevicesPath[i])
                        LogUtils.e("选择的串口:", allDevicesPath[i])

                        // 灯控串口配置
                        mSystemSettingsBinding.systemSettingLightSerial.setCaptionText(
                            "灯控串口:" + allDevicesPath[i]
                        )

                        mSpUtil.applyValue(
                            Record(
                                Key.LightsSerialSelected,
                                allDevicesPath[i]
                            )
                        )
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
            // 档案组架-灯控调试界面
            R.id.system_setting_light_serial_debug -> {
                // 进入灯控调试界面
                val deviceList = DeviceService.getInstance().loadAll()
                val mLightsSerialSelected = mSpUtil.getString(Key.LightsSerialSelected, "")!!
                if (deviceList.size == 0) {
                    showWarningToast("请先添加档案架")
                    return
                }
                if (TextUtils.isEmpty(mLightsSerialSelected)) {
                    showWarningToast("请先配置灯控串口")
                    return
                }

                var intent: Intent = Intent(this, DAJLightDebugActivity::class.java)
                startActivity(intent)
            }

            // 档案柜配置
            // 添加档案柜
            R.id.system_setting_number_of_boxes_sb -> {
                val mEquipmentId = mSpUtil.getString(Key.EquipmentId, "")!!
                if (TextUtils.isEmpty(mEquipmentId)) {
                    showWarningToast("请先配置操作屏设备ID")
                } else {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.number_of_boxes))
                        .setSingleChoiceItems(
                            mNumberBoxesItems, mNumberBoxesItemSelected
                        ) { dialogInterface, i ->
                            dialogInterface.dismiss()

                            var intent: Intent =
                                Intent(this, DAGSettingActivity::class.java)
                            intent.putExtra("NumberOfBoxesSelected", i)
                            startActivity(intent)
                        }
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show()
                }
            }
            // 档案组柜-组大灯灯控串口设置
            R.id.system_setting_light_serial_zg -> {
                var serialPortFinder = SerialPortFinder()
                val allDevices = serialPortFinder.allDevices
                val allDevicesPath = serialPortFinder.allDevicesPath
                LogUtils.e("串口列表:", allDevices, allDevicesPath)
                // [/dev/ttyS8, /dev/ttyS7, /dev/ttyS6, /dev/ttyS5, /dev/ttyS3, /dev/ttyS0, /dev/ttyS2, /dev/ttyS4, /dev/ttyS1]

                // 灯控选择的串口
                mLightsSerialSelected = mSpUtil.getString(Key.LightsSerialSelected, "")!!
                var selectLightSerialIndex = -1
                for ((index, e) in allDevicesPath.withIndex()) {
                    if (e == mLightsSerialSelected) {
                        selectLightSerialIndex = index
                    }
                }

                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.light_serial_setting))
                    .setSingleChoiceItems(
                        allDevicesPath, selectLightSerialIndex
                    ) { dialogInterface, i ->
                        dialogInterface.dismiss()
                        showSuccessToast("选择的串口:" + allDevicesPath[i])
                        LogUtils.e("选择的串口:", allDevicesPath[i])

                        // 灯控串口配置
                        mSystemSettingsBinding.systemSettingLightSerialZg.setCaptionText(
                            "组大灯灯控串口:" + allDevicesPath[i]
                        )

                        mSpUtil.applyValue(
                            Record(
                                Key.LightsSerialSelected,
                                allDevicesPath[i]
                            )
                        )
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }

            // 档案组柜-组大灯灯控调试界面
            R.id.system_setting_light_serial_debug_zg -> {
                // 进入灯控调试界面
                val mLightsSerialSelected = mSpUtil.getString(Key.LightsSerialSelected, "")!!
                if (TextUtils.isEmpty(mLightsSerialSelected)) {
                    showWarningToast("请先配置组大灯灯控串口")
                    return
                }

                var intent: Intent = Intent(this, DAGLightDebugActivity::class.java)
                startActivity(intent)
            }


            R.id.system_setting_not_closed_door_alarm_time_sb -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.not_closed_door_alarm_time))
                    .setSingleChoiceItems(
                        mAlarmTimeItems, mAlarmTimeItemSelected
                    ) { dialogInterface, i ->
                        mAlarmTimeItemSelected = i
                        mSpUtil.applyValue(
                            Record(
                                Key.NotClosedDoorAlarmTime,
                                mAlarmTimeValueItems[mAlarmTimeItemSelected]
                            )
                        )
                        mSystemSettingsBinding.systemSettingNotClosedDoorAlarmTimeSb.setCaptionText(
                            String.format(
                                resources.getString(R.string.not_closed_door_alarm_time_caption_text),
                                mAlarmTimeItems[mAlarmTimeItemSelected]
                            )
                        )
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
            R.id.system_setting_too_many_files_number_sb -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.title_too_many_files))
                    .setSingleChoiceItems(
                        mTooManyFilesNumbers,
                        mTooManyFilesNumberSelected
                    ) { dialogInterface, i ->
                        mTooManyFilesNumberSelected = i
                        mSpUtil.applyValue(
                            Record(
                                Key.TooManyFilesNumber,
                                mTooManyFilesNumbers[mTooManyFilesNumberSelected].toInt()
                            )
                        )
                        mSystemSettingsBinding.systemSettingTooManyFilesNumberSb.setCaptionText(
                            String.format(
                                resources.getString(R.string.too_many_files_number),
                                mTooManyFilesNumbers[mTooManyFilesNumberSelected]
                            )
                        )
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }


            R.id.system_setting_synchronised_time_sb -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.synchronised_time))
                    .setSingleChoiceItems(
                        mSynchronisedTimes, mSynchronisedTimeSelected
                    ) { dialogInterface, i ->
                        mSynchronisedTimeSelected = i
                        mSpUtil.applyValue(
                            Record(
                                Key.SyncInterval,
                                mSynchronisedTimeValues[mSynchronisedTimeSelected]
                            )
                        )
                        mSystemSettingsBinding.systemSettingSynchronisedTimeSb.setCaptionText(
                            String.format(
                                resources.getString(R.string.synchronised_time_caption_text),
                                mSynchronisedTimes[mSynchronisedTimeSelected]
                            )
                        )
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
            R.id.system_setting_countdown_sb -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.countdown))
                    .setSingleChoiceItems(
                        mCountdownItems, mCountdownSelected
                    ) { dialogInterface, i ->
                        mCountdownSelected = i
                        mSpUtil.applyValue(
                            Record(
                                Key.Countdown,
                                mCountdownItemValues[mCountdownSelected]
                            )
                        )
                        mSystemSettingsBinding.systemSettingCountdownSb.setCaptionText(
                            String.format(
                                resources.getString(R.string.countdown_caption_text),
                                mCountdownItems[mCountdownSelected]
                            )
                        )
                        mCountdown = mCountdownItemValues[mCountdownSelected]
                        initTime()
                        timerStart()
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
            R.id.system_setting_date_and_time_sb -> {
                val calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog(
                    this,
                    OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        val month = monthOfYear + 1
                        val timePickerDialog = TimePickerDialog(
                            this,
                            OnTimeSetListener { _, i, i1 ->
//                                SmdtUtil.instance.setTime(
//                                    applicationContext,
//                                    year,
//                                    month,
//                                    dayOfMonth,
//                                    i,
//                                    i1
//                                )
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        )
                        timePickerDialog.show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            }
            R.id.system_setting_restart_sb -> {
                if (mRestartTaskDialog == null) {
                    mRestartTaskDialog = TaskSetDialog(R.string.automatic_restart,
                        object : TaskSetDialog.InputListener {
                            override fun onInputComplete(
                                switch: Boolean,
                                hourOfDay: Int
                            ) {
                                mRestart = switch
                                mRestartStartTimeHourOfDay = hourOfDay
                                val restartSetList = ArrayList<Record>()
                                restartSetList.add(Record(Key.Restart, mRestart))
                                restartSetList.add(
                                    Record(
                                        Key.RestartStartTimeHourOfDay,
                                        mRestartStartTimeHourOfDay
                                    )
                                )
                                mSpUtil.applyValue(restartSetList)
                                mSystemSettingsBinding.systemSettingRestartSb.setCaptionText(
                                    if (!mRestart) resources.getString(R.string.turn_off_automatic_task)
                                    else String.format(
                                        resources.getString(R.string.automatic_restart_caption_text),
                                        mRestartStartTimeHourOfDay
                                    )
                                )
                                showRestartNowForSet()
                            }
                        })
                }
                mRestartTaskDialog!!.mSwitch = mRestart
                mRestartTaskDialog!!.mHourOfDay = mRestartStartTimeHourOfDay
                mRestartTaskDialog!!.show(supportFragmentManager, "RestartTask")
            }
            R.id.system_setting_restart_device_sb -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.title_restart_now))
                    .setMessage(getString(R.string.restart_now_sure))
                    .setPositiveButton(getString(R.string.sure)) { _, _ ->
                        // SmdtUtil.instance.reboot()
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }

            R.id.system_setting_anti_theft_sb -> {
                mCheckDoorStatus = !mSystemSettingsBinding.systemSettingAntiTheftSb.getChecked()
                mSystemSettingsBinding.systemSettingAntiTheftSb.setChecked(mCheckDoorStatus)
                mSystemSettingsBinding.systemSettingAntiTheftSb.setCaptionText(
                    if (mCheckDoorStatus) getString(R.string.anti_theft_mechanism_on) else
                        getString(R.string.anti_theft_mechanism_off)
                )
                mSpUtil.applyValue(Record(Key.CheckDoorStatus, mCheckDoorStatus))
            }
            R.id.system_setting_calibration_idler_sb -> {
                if (mCalibrationTaskDialog == null) {
                    mCalibrationTaskDialog = TaskSetDialog(R.string.title_calibration_idler,
                        object : TaskSetDialog.InputListener {
                            override fun onInputComplete(
                                switch: Boolean,
                                hourOfDay: Int
                            ) {
                                mCalibration = switch
                                mCalibrationTimeHourOfDay = hourOfDay
                                val calibrationSetList = ArrayList<Record>()
                                calibrationSetList.add(Record(Key.Calibration, mCalibration))
                                calibrationSetList.add(
                                    Record(
                                        Key.CalibrationTimeHourOfDay,
                                        mCalibrationTimeHourOfDay
                                    )
                                )
                                mSpUtil.applyValue(calibrationSetList)
                                mSystemSettingsBinding.systemSettingCalibrationIdlerSb.setCaptionText(
                                    if (!mCalibration) resources.getString(R.string.turn_off_automatic_task)
                                    else String.format(
                                        resources.getString(R.string.calibration_idler_caption_text),
                                        mCalibrationTimeHourOfDay
                                    )
                                )
                                showRestartNowForSet()
                            }
                        })
                }
                mCalibrationTaskDialog!!.mSwitch = mCalibration
                mCalibrationTaskDialog!!.mHourOfDay = mCalibrationTimeHourOfDay
                mCalibrationTaskDialog!!.show(supportFragmentManager, "RestartTask")
            }

            R.id.system_setting_app_version_sb -> {

            }
            R.id.system_setting_update_from_u_disk_sb -> {
//                val file = File("/mnt/usb_storage/USB_DISK2/udisk0/NewFileCabinet.apk")
//                if (file.exists()) {
//                    object : Thread() {
//                        override fun run() {
////                            SmdtUtil.instance.silentInstall(
////                                "/mnt/usb_storage/USB_DISK2/udisk0/FileCabinet.apk",
////                                applicationContext
////                            )
//                        }
//                    }.start()
//                } else showToast(getText(R.string.file_not_exist))
            }
            R.id.system_setting_uninstall_app_sb -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.title_uninstall_app))
                    .setMessage(R.string.uninstall_app_prompt)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setPositiveButton(
                        getString(R.string.sure)
                    ) { _, _ ->
//                        SmdtUtil.instance.setDefaultLauncherState(true)
                        if (ActivityUtil.getLauncherPackageName(this@SystemSettingsActivity)
                                .equals("android")
                        ) {
                            // 关闭看门狗
                            ActivityUtil.uninstallApk(
                                this@SystemSettingsActivity, "com.zk.cabinet"
                            )
                        } else {
                            showWarningToast(
                                getString(R.string.uninstall_app_fail)
                            )
                        }
                    }
                    .show()
            }

            // 进入系统设置
            R.id.system_setting_display_sb -> {
                startActivity(Intent(Settings.ACTION_SETTINGS))
//                AlertDialog.Builder(this)
//                    .setTitle(getString(R.string.title_display_bar))
//                    .setMessage(R.string.display_bar_prompt)
//                    .setNegativeButton(getString(R.string.cancel), null)
//                    .setPositiveButton(getString(R.string.sure)) { _, _ ->
////                        SmdtUtil.instance.setStatusBar(this@SystemSettingsActivity, true)
//                        startActivity(Intent(Settings.ACTION_SETTINGS))
//                    }
//                    .show()
            }

            // 文件管理器
            R.id.system_setting_file_manager_sb -> {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)

                // 档案柜配置
                val deviceName = mSpUtil.getString(Key.DeviceName, "").toString()

                // 6.0.2 Android系统 ES文件浏览器
//                if (SelfComm.DEVICE_NAME[3].equals(deviceName)) {
//                    val cn = ComponentName(
//                        "com.estrongs.android.pop",
//                        "com.estrongs.android.pop.view.FileExplorerActivity"
//                    )
//                    intent.component = cn
//                }

                // 档案组架1/档案组柜2/档案单柜3/一体机4/通道门6 - 7.1.2Android系统Well文件管理
                if (SelfComm.DEVICE_NAME[1].equals(deviceName)
                    || SelfComm.DEVICE_NAME[2].equals(deviceName)
                    || SelfComm.DEVICE_NAME[3].equals(deviceName)
                    || SelfComm.DEVICE_NAME[4].equals(deviceName)
                    || SelfComm.DEVICE_NAME[6].equals(deviceName)
                ) {
                    val cn = ComponentName(
                        "com.fihtdc.filemanager",
                        "com.fihtdc.filemanager.FileManager"
                    )
                    intent.component = cn
                }

                startActivity(intent)
            }

            // 浏览器
            R.id.system_setting_browser_sb -> {
                // SmdtUtil.instance.setStatusBar(this@SystemSettingsActivity, true)
                val intentBrowser = Intent(Intent.ACTION_MAIN)
                intentBrowser.action = "android.intent.action.VIEW"
                val webApi =
                    if (mWebApiServicePort == -1) "http://192.168.1.100" else "http://$mWebApiServiceIp:$mWebApiServicePort"
                val contentUrl = Uri.parse(webApi)
                intentBrowser.data = contentUrl
                startActivity(intentBrowser)
            }

            R.id.system_setting_debug_sv -> {
                mDebug = !mSystemSettingsBinding.systemSettingDebugSv.getChecked()
                mSystemSettingsBinding.systemSettingDebugSv.setChecked(mDebug)
                mSystemSettingsBinding.systemSettingDebugSv.setCaptionText(
                    if (mDebug) getString(R.string.debug_on) else getString(R.string.debug_off)
                )
                LogUtil.instance.logSwitch = mDebug
                mSpUtil.applyValue(Record(Key.Debug, mDebug))
            }

            // Ping工具
            R.id.system_setting_ping_test_sb -> {
                if (mPingDialog == null) {
                    mPingDialog =
                        PingDialog(object : PingDialog.PingResultListener {
                            override fun onPingStart(address: String) {
                                val message = Message.obtain()
                                message.what = SYSTEM_SETTING_PING_START
                                message.obj = address
                                mHandler.sendMessage(message)
                            }

                            override fun onPingResult(result: String) {
                                val message = Message.obtain()
                                message.what = SYSTEM_SETTING_PING_RESULT
                                message.obj = result
                                mHandler.sendMessage(message)
                            }
                        })
                }
                val pingIpAddress =
                    if (mWebApiServicePort == -1) "192.168.1.100" else mWebApiServiceIp
                mPingDialog!!.mPingIpAddress = pingIpAddress
                mPingDialog!!.show(supportFragmentManager, "Ping")
            }
        }
    }

    private class SystemSettingsHandler(systemSettingsActivity: SystemSettingsActivity) :
        Handler() {
        private val systemSettingsWeakReference = WeakReference(systemSettingsActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            systemSettingsWeakReference.get()!!.handleMessage(msg)
        }
    }

    override fun onResume() {
        super.onResume()
        //警告：别TM瞎改这句话，神TM知道这块破板子（双网口7.1.2的固件）只在onResume中设置生效
//        SmdtUtil.instance.setGesturesStatusBar(false)
//        SmdtUtil.instance.setStatusBar(this, false)

        // 本机固定IP配置
        mSystemSettingsBinding.systemSettingThisMachineIp.setCaptionText(
            "本机IP:" + NetworkUtils.getIPAddress(true)
        )

        // 档案架数量
        mDAJNumberSelected = mSpUtil.getInt(Key.DAJNumberSelected, -1)
        mSystemSettingsBinding.systemSettingDaj.setCaptionText(
            if (mDAJNumberSelected != -1) {
                String.format(
                    resources.getString(R.string.number_of_daj),
                    mDAJArrays[mDAJNumberSelected]
                )
            } else {
                "未添加档案架!"
            }
        )
    }

    private fun showRestartNowForSet() {
//        if (!isShowRestartNowForSet) {
//            isShowRestartNowForSet = true
//            mSpUtil.applyValue(Record(Key.RestartNowForSet, isShowRestartNowForSet))
//            mSystemSettingsBinding.systemSettingRestartLl.visibility = View.VISIBLE
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == SelfComm.UPDATE_CABINET_IN_ORDER) {
//            val numberBoxesInOrder =
//                data!!.getStringExtra(CabinetConfigurationActivity.NUMBER_OF_BOXES_IN_ORDER)
//            mSystemSettingsBinding.systemSettingNumberOfBoxesSb.setCaptionText(
//                String.format(
//                    resources.getString(R.string.number_of_boxes_caption_text),
//                    mNumberBoxesItems[mNumberBoxesItemSelected],
//                    numberBoxesInOrder
//                )
//            )
        }
    }

    private fun restartApp() {
        val intent = Intent(this, GuideActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        Process.killProcess(Process.myPid())
    }

    private fun restartApp2() {
        val intent = Intent(this, DeviceSelectActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        Process.killProcess(Process.myPid())
    }
}