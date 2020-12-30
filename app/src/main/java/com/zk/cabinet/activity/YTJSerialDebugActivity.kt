package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.LogUtils
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.databinding.ActivityYtjSerialDebugBinding
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.rfid.bean.DeviceInformation
import com.zk.rfid.bean.LabelInfo
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.callback.DeviceInformationListener
import com.zk.rfid.callback.InventoryListener
import com.zk.rfid.ur880.UR880Entrance
import java.lang.ref.WeakReference

/**
 * 一体机-读写器测试界面
 */
class YTJSerialDebugActivity : TimeOffAppCompatActivity(), AdapterView.OnItemClickListener,
    View.OnClickListener {
    private lateinit var mBinding: ActivityYtjSerialDebugBinding
    private lateinit var mHandler: MyHandler
    private var labelInfo = LabelInfo()

    companion object {
        private const val DEVICE_REGISTERED = 0x01
        private const val DEVICE_REMOVED = 0x02

        private const val START_INVENTORY = 0x03
        private const val INVENTORY_VALUE = 0x04
        private const val CANCEL_INVENTORY = 0x05
        private const val END_INVENTORY = 0x06

        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, YTJSerialDebugActivity::class.java)
        }
    }

    private val mDeviceInformationList = ArrayList<DeviceInformation>()  // 需要打开的读写器串口列表

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_ytj_serial_debug)
        mBinding.onClickListener = this
        mBinding.onItemClickListener = this
        mHandler = MyHandler(this)

        // 关闭自动倒计时
        isAutoFinish = false

        // 显示选择的读写器串口
        val port = mSpUtil.getString(SharedPreferencesUtil.Key.YTJDxqSerialSelected, "")
        mBinding.tvSerialPort.text = port

        val deviceInformation = DeviceInformation();
        deviceInformation.deviceSerialPath = port;
        deviceInformation.deviceSerialBaudRate = "115200";
        mDeviceInformationList.add(deviceInformation)

        val init = UR880Entrance.getInstance()
            .init(UR880Entrance.CONNECTION_SERIAL, null, mDeviceInformationList)

        UR880Entrance.getInstance().addOnDeviceInformationListener(mDeviceInformationListener)
        UR880Entrance.getInstance().addOnInventoryListener(mInventoryListener)
        UR880Entrance.getInstance().connect()
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            // todo 添加一体机的读写器设备
            DEVICE_REGISTERED -> {
                // 前来注册的读写器设备ID
                val deviceID = msg.obj.toString()
                // 一体机始终只连接来注册的那唯一一个设备,先清空掉原来设备,成功注册了就添加新的
                DeviceService.getInstance().deleteAll()
                val device = Device()
                device.deviceName = "RFID扫描设备"
                device.deviceId = deviceID
                DeviceService.getInstance().insert(device)
                LogUtils.e("添加一体机读写器-读写器设备ID:$deviceID")

                mBinding.btnScan.background = resources.getDrawable(R.drawable.selector_menu_orange)
                mBinding.btnScan.isEnabled = true
            }

            START_INVENTORY -> {
                showWarningToast("正在扫描,请稍后...")
                speek("正在扫描,请稍后...")
                // Toast.makeText(this, "开始盘点", Toast.LENGTH_SHORT).show()
            }
            INVENTORY_VALUE -> {
                labelInfo = msg.obj as LabelInfo
                // labelInfoList.add(labelInfo)
                Log.e("zx-一体机读写器串口调试-", "-------------labelInfo.deviceID: ${labelInfo.deviceID}")
                Log.e(
                    "zx-一体机读写器串口调试-",
                    "-------------labelInfo.antennaNumber: ${labelInfo.antennaNumber}"
                )
                Log.e("zx-一体机读写器串口调试-", "-------------labelInfo.fastID: ${labelInfo.fastID}")
                Log.e("zx-一体机读写器串口调试-", "-------------labelInfo.rssi: ${labelInfo.rssi}")
                Log.e(
                    "zx-一体机读写器串口调试-",
                    "-------------labelInfo.operatingTime: ${labelInfo.operatingTime}"
                )
                Log.e("zx-一体机读写器串口调试-", "-------------labelInfo.epcLength: ${labelInfo.epcLength}")
                Log.e("zx-一体机读写器串口调试-", "-------------labelInfo.epc: ${labelInfo.epc}")
                Log.e("zx-一体机读写器串口调试-", "-------------labelInfo.tid: ${labelInfo.tid}")
                Log.e(
                    "zx-一体机读写器串口调试-",
                    "-------------labelInfo.inventoryNumber: ${labelInfo.inventoryNumber}"
                )

            }
            CANCEL_INVENTORY -> {
                // Toast.makeText(this, "停止盘点", Toast.LENGTH_SHORT).show()
            }
            END_INVENTORY -> {
                // Toast.makeText(this, "盘点结束", Toast.LENGTH_SHORT).show()
                mBinding.tvScanEpc.text = labelInfo.epc
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> {
                finish()
            }

            R.id.btn_scan -> {
                val deviceList = DeviceService.getInstance().loadAll()
                if (deviceList.size != 0) {
                    LogUtils.e("读写器设备ID:" + deviceList[0].deviceId)

                    // 开启单次扫描
                    /*
                     * ID : 读写器ID，注册回调里有这个ID
                     * fastId : 0x01 启用FastID功能 0x00 不启用FastID功能（该功能目前未启用，传个0就好了）
                     * antennaNumber: 天线号 (0-3)，一共4根天线 (接一根天线就是0)
                     * inventoryType ; 0x00 非连续盘点 0x01 连续盘点
                     */
                    UR880Entrance.getInstance().send(
                        UR880SendInfo.Builder().inventory(deviceList[0].deviceId, 0x00, 0x00, 0x00)
                            .build()
                    )
                }
            }
        }
    }

    private fun sendDelayMessage(msgWhat: Int, msgObj: String) {
        val message = Message.obtain()
        message.what = msgWhat
        message.obj = msgObj
        mHandler.sendMessageDelayed(message, 1000)
    }

    private class MyHandler(activity: YTJSerialDebugActivity) :
        Handler() {
        private val weakReference = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            weakReference.get()!!.handleMessage(msg)
        }
    }

    private val mInventoryListener = object : InventoryListener {
        override fun startInventory(p0: Int) {
            mHandler.sendEmptyMessage(START_INVENTORY)
        }

        override fun inventoryValue(p0: LabelInfo?) {
            val msg = Message.obtain()
            msg.what = INVENTORY_VALUE
            msg.obj = p0
            mHandler.sendMessage(msg)
        }

        override fun cancel(p0: Int, p1: Int) {
            mHandler.sendEmptyMessage(CANCEL_INVENTORY)
        }

        override fun endInventory(p0: Int) {
            mHandler.sendEmptyMessage(END_INVENTORY)
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

    override fun onDestroy() {
        UR880Entrance.getInstance().removeDeviceInformationListener(mDeviceInformationListener)
        UR880Entrance.getInstance().removeInventoryListener(mInventoryListener)
        super.onDestroy()
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
    }
}