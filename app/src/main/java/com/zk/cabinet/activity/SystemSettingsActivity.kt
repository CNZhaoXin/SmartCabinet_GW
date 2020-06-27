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
import android.provider.Settings
import android.text.InputType
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.constant.SelfComm
import com.zk.cabinet.databinding.ActivitySystemSettingsBinding
import com.zk.cabinet.dialog.*
import com.zk.cabinet.utils.MediaPlayerUtil
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.cabinet.utils.SharedPreferencesUtil.Record
import com.zk.common.utils.ActivityUtil
import com.zk.common.utils.AppVersionUtil
import com.zk.common.utils.LogUtil
import java.io.File
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
    private lateinit var mWebApiServiceIp: String                            //webAPI
    private var mWebApiServicePort by Delegates.notNull<Int>()           //webAPI端口
    private var mCabinetServicePort by Delegates.notNull<Int>()          //本地柜体netty端口
    private var mEth0SetDialog: Eth0Dialog? = null
    private var mEth1SetDialog: Eth1Dialog? = null
    private var mWebApiServiceDialog: WebApiServiceDialog? = null
    private var mCabinetServicePortDialog: UniversalEdtDialog? = null

    private lateinit var mNumberBoxesItems: Array<String>                     //柜体数量
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
        mUnitNumber = mSpUtil.getString(Key.UnitNumber, resources.getString(R.string.null_prompt))!!
        mUnitAddress =
            mSpUtil.getString(Key.UnitAddress, resources.getString(R.string.null_prompt))!!

        mEth0IP = mSpUtil.getString(Key.Eth0IP, resources.getString(R.string.air))!!
        mEth0SubnetMask = mSpUtil.getString(Key.Eth0SubnetMask, resources.getString(R.string.air))!!
        mEth0Gateway = mSpUtil.getString(Key.Eth0Gateway, resources.getString(R.string.air))!!
        mEth0DNS = mSpUtil.getString(Key.Eth0DNS, resources.getString(R.string.air))!!
        mEth1IP = mSpUtil.getString(Key.Eth1IP, resources.getString(R.string.air))!!
        mEth1SubnetMask = mSpUtil.getString(Key.Eth1SubnetMask, resources.getString(R.string.air))!!
        mWebApiServiceIp =
            mSpUtil.getString(Key.WebApiServiceIp, resources.getString(R.string.air))!!
        mWebApiServicePort = mSpUtil.getInt(Key.WebApiServicePort, -1)
        mCabinetServicePort = mSpUtil.getInt(Key.CabinetServicePort, 7880)

        mNumberBoxesItems = resources.getStringArray(R.array.dialog_number_of_boxes_array)
        mNumberBoxesItemSelected = mSpUtil.getInt(Key.NumberOfBoxesSelected, 1)
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
        mCountdownItems = resources.getStringArray(R.array.countdown_array)
        mCountdownItemValues =
            resources.getIntArray(R.array.countdown_value_array)
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
        mSystemSettingsBinding.systemSettingWebApiServiceSb.setCaptionText(
            if (mWebApiServicePort == -1) resources.getString(
                R.string.null_prompt
            ) else String.format(
                resources.getString(R.string.web_api_caption_text),
                mWebApiServiceIp,
                mWebApiServicePort
            )
        )
        mSystemSettingsBinding.systemSettingCabinetServicePortSb.setCaptionText(mCabinetServicePort.toString())


        mSystemSettingsBinding.systemSettingNumberOfBoxesSb.setCaptionText(
            String.format(
                resources.getString(R.string.number_of_boxes_caption_text),
                mNumberBoxesItems[mNumberBoxesItemSelected],
                mSpUtil.getString(Key.NumberOfBoxes, "A")
            )
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
                    mDeviceCodeDialog!!.mInputType = InputType.TYPE_CLASS_NUMBER
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
                    mUnitNumberDialog!!.mInputType = InputType.TYPE_CLASS_NUMBER
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
            R.id.system_setting_cabinet_service_port_sb -> {
                if (mCabinetServicePortDialog == null) {
                    mCabinetServicePortDialog =
                        UniversalEdtDialog(R.string.cabinet_service_port,
                            object : UniversalEdtDialog.InputListener {
                                override fun onInputComplete(input: String) {
                                    if (mCabinetServicePort != input.toInt()) {
                                        mCabinetServicePort = input.toInt()
                                        mSpUtil.applyValue(
                                            Record(
                                                Key.CabinetServicePort,
                                                mCabinetServicePort
                                            )
                                        )
                                        mSystemSettingsBinding.systemSettingCabinetServicePortSb.setCaptionText(
                                            mCabinetServicePort.toString()
                                        )
                                        showRestartNowForSet()
                                    }
                                }
                            })
                    mCabinetServicePortDialog!!.mInputType = InputType.TYPE_CLASS_NUMBER
                }
                mCabinetServicePortDialog!!.mMessage = mCabinetServicePort.toString()
                mCabinetServicePortDialog!!.show(supportFragmentManager, "CabinetServicePort")
            }


            R.id.system_setting_number_of_boxes_sb -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.number_of_boxes))
                    .setSingleChoiceItems(
                        mNumberBoxesItems, mNumberBoxesItemSelected
                    ) { dialogInterface, i ->
                        mNumberBoxesItemSelected = i
                        showRestartNowForSet()
                        dialogInterface.dismiss()
//                        intentActivity(
//                            CabinetConfigurationActivity.newInstance(
//                                this,
//                                mNumberBoxesItemSelected
//                            )
//                        )
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()

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
//                        SmdtUtil.instance.reboot()
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
            R.id.system_setting_sound_switch_sb -> {
                mSoundSwitch = !mSystemSettingsBinding.systemSettingSoundSwitchSb.getChecked()
                mSystemSettingsBinding.systemSettingSoundSwitchSb.setChecked(mSoundSwitch)
                mSystemSettingsBinding.systemSettingSoundSwitchSb.setCaptionText(
                    if (mSoundSwitch) getString(R.string.sound_switch_on) else
                        getString(R.string.sound_switch_off)
                )
                mSpUtil.applyValue(Record(Key.SoundSwitch, mSoundSwitch))
                MediaPlayerUtil.instance.mSoundSwitch = mSoundSwitch
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
                val file = File("/mnt/usb_storage/USB_DISK2/udisk0/NewFileCabinet.apk")
                if (file.exists()) {
                    object : Thread() {
                        override fun run() {
//                            SmdtUtil.instance.silentInstall(
//                                "/mnt/usb_storage/USB_DISK2/udisk0/FileCabinet.apk",
//                                applicationContext
//                            )
                        }
                    }.start()
                } else showToast(getText(R.string.file_not_exist))
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
                            //todo 关闭看门狗
                            ActivityUtil.uninstallApk(
                                this@SystemSettingsActivity, "com.hik.filecabinet"
                            )
                        } else {
                            showToast(
                                getString(R.string.uninstall_app_fail)
                            )
                        }
                    }
                    .show()
            }

            R.id.system_setting_display_sb -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.title_display_bar))
                    .setMessage(R.string.display_bar_prompt)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setPositiveButton(getString(R.string.sure)) { _, _ ->
//                        SmdtUtil.instance.setStatusBar(this@SystemSettingsActivity, true)
                        startActivity(Intent(Settings.ACTION_SETTINGS))
                    }
                    .show()
            }
            R.id.system_setting_file_manager_sb -> {
//                SmdtUtil.instance.setStatusBar(this@SystemSettingsActivity, true)
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                val cn =
                    ComponentName("com.android.rk", "com.android.rk.RockExplorer")
                intent.component = cn
                startActivity(intent)
            }
            R.id.system_setting_browser_sb -> {
//                SmdtUtil.instance.setStatusBar(this@SystemSettingsActivity, true)
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
    }

    private fun showRestartNowForSet() {
        if (!isShowRestartNowForSet) {
            isShowRestartNowForSet = true
            mSpUtil.applyValue(Record(Key.RestartNowForSet, isShowRestartNowForSet))
            mSystemSettingsBinding.systemSettingRestartLl.visibility = View.VISIBLE
        }
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
}