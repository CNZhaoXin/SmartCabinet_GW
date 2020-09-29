package com.zk.cabinet.service

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.zk.cabinet.activity.WarningActivity
import com.zk.cabinet.constant.SelfComm
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.db.DossierOperatingService
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.common.utils.ActivityUtil
import com.zk.common.utils.LogUtil
import com.zk.rfid.bean.LabelInfo
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.callback.FactorySettingListener
import com.zk.rfid.callback.InventoryListener
import com.zk.rfid.ur880.UR880Entrance
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class NetService : Service() {
    private lateinit var mNetHandlerThread: HandlerThread
    private lateinit var mNetHandler: NetHandler
    private var mGuideMessenger: Messenger? = null

    private lateinit var mScheduledExecutorService: ScheduledExecutorService
    private lateinit var mTaskHeartbeat: TimerTask

    companion object {
        private const val HANDLER_THREAD_NAME = "NetServiceHandlerThread"
        const val HEARTBEAT = 0x01
        const val NOTIFICATION = 0x02
    }

    override fun onBind(intent: Intent): IBinder {
        LogUtil.instance.d("NetService", "NetService onBind", true)
        mNetHandlerThread = HandlerThread(HANDLER_THREAD_NAME)
        mNetHandlerThread.start()
        mNetHandler = NetHandler(mNetHandlerThread.looper, this)

        mTaskHeartbeat = object : TimerTask() {
            override fun run() {
                val mCabinetList = DeviceService.getInstance().loadAll()
                val sb = StringBuffer()

                for ((index, device) in mCabinetList.withIndex()) {
                    if (index != mCabinetList.size - 1) {
                        sb.append(device.deviceId)
                        sb.append(",")
                    } else
                        sb.append(device.deviceId)
                }

                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.GET,
                    NetworkRequest.instance.mInventoryRequest + sb.toString(),
                    // + SharedPreferencesUtil.instance.getString(SharedPreferencesUtil.Key.DeviceCode, ""),
                    Response.Listener { response ->

                        // {"success":true,"message":"OK",
                        // "data":[{"inventoryId":"1","cabCode":"1234567801","batch":null,"inOrg":"02","status":1}
                        // ,{"inventoryId":"2","cabCode":"1234567803","batch":null,"inOrg":"02","status":1}],"dataCount":"1"}
                        Log.e("zx--获取盘点任务单--", "$response")

                        try {
                            val success = response.getBoolean("success")
                            val dataCount = response.getString("dataCount")
                            if (success && dataCount == "1") {
                                val dataJsonArray = response.getJSONArray("data")
                                val cabCodeList = ArrayList<String>()
                                val inventoryIdList = ArrayList<String>()
                                val inOrgList = ArrayList<String>()
                                for (i in 0 until dataJsonArray.length()) {
                                    val jsonObject: JSONObject = dataJsonArray.getJSONObject(i)
                                    val inventoryId = jsonObject.getString("inventoryId")
                                    val cabCode = jsonObject.getString("cabCode")
                                    val inOrg = jsonObject.getString("inOrg")
                                    if (DeviceService.getInstance()
                                            .queryByDeviceName(cabCode) != null
                                    ) {
                                        cabCodeList.add(cabCode)
                                        inventoryIdList.add(inventoryId)
                                        inOrgList.add(inOrg)
                                    }
                                }
                                if (inventoryIdList.size > 0 && ActivityUtil.isTopActivity(
                                        applicationContext,
                                        "com.zk.cabinet.activity.GuideActivity"
                                    )
                                ) {
                                    val msg = Message.obtain()
                                    msg.what = SelfComm.NET_SERVICE_INVENTORY
                                    val data = Bundle()
                                    data.putStringArrayList("cabCodeList", cabCodeList)
                                    data.putStringArrayList("inventoryIdList", inventoryIdList)
                                    data.putStringArrayList("inOrgList", inOrgList)
                                    msg.data = data
                                    mGuideMessenger?.send(msg)
                                }
                            } else {
//                                val msg = Message.obtain()
//                                msg.what = GET_OUTBOUND_FAIL
//                                msg.obj = response.getString("message")
//                                mHandler.sendMessage(msg)
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
//                            val msg = Message.obtain()
//                            msg.what = GET_OUTBOUND_FAIL
//                            msg.obj = "数据解析失败"
//                            mHandler.sendMessage(msg)
                        }
                    },
                    Response.ErrorListener { error ->
                        val msg = if (error != null)
                            if (error.networkResponse != null)
                                "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                            else
                                "errorCode: -1 VolleyError: $error"
                        else {
                            "errorCode: -1 VolleyError: 未知"
                        }
//                        val message = Message.obtain()
//                        message.what = GET_OUTBOUND_FAIL
//                        message.obj = msg
//                        mHandler.sendMessage(message)
                    })
                jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
                NetworkRequest.instance.add(jsonObjectRequest)
            }
        }
        mScheduledExecutorService = Executors.newScheduledThreadPool(3)

        /**
        command：执行线程
        initialDelay：初始化延时
        period：前一次执行结束到下一次执行开始的间隔时间（间隔执行延迟时间）
        unit：计时单位
         */
        mScheduledExecutorService.scheduleAtFixedRate(mTaskHeartbeat, 1, 1, TimeUnit.MINUTES)

        // todo 监听通道门读写器上报消息 和 发送GPIO信号
        val serverPort = mSpUtil.getInt(SharedPreferencesUtil.Key.CabinetServicePort, -1)
        if (serverPort != -1) {
            UR880Entrance.getInstance().addOnInventoryListener(mInventoryListener)
            UR880Entrance.getInstance().addOnFactorySettingListener(mFactorySettingListener)
        }

        return Messenger(mNetHandler).binder
    }

    protected var mSpUtil = SharedPreferencesUtil.instance

    override fun onDestroy() {
        super.onDestroy()
        UR880Entrance.getInstance().removeInventoryListener(mInventoryListener)
        UR880Entrance.getInstance().removeFactorySettingListener(mFactorySettingListener)
    }

    private class NetHandler(looper: Looper, netService: NetService) : Handler(looper) {
        private val mainWeakReference = WeakReference(netService)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mainWeakReference.get()!!.handleMessage(msg)
        }
    }

    private val START_INVENTORY = 0x02
    private val INVENTORY_VALUE = 0x03
    private val CANCEL_INVENTORY = 0x04
    private val END_INVENTORY = 0x05

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            SelfComm.NET_SERVICE_CONNECT -> {
                mGuideMessenger = msg.replyTo
            }

            START_INVENTORY -> {
                Log.e("zx-通道门", "开始盘点")
                Toast.makeText(this, "开始盘点", Toast.LENGTH_SHORT).show()
            }
            INVENTORY_VALUE -> {
                val labelInfo = msg.obj as LabelInfo
                // todo 不属于通道门的数据不处理 deviceList[1].deviceId 注意这个要配通道门的ID, 不然起不到过滤作用
                val deviceList = DeviceService.getInstance().loadAll()
                if (deviceList.size > 1 && deviceList[1].deviceId == labelInfo.deviceID) {

                    Log.e("zx-通道门-", "-------------labelInfo.deviceID: ${labelInfo.deviceID}")
                    Log.e(
                        "zx-通道门-",
                        "-------------labelInfo.antennaNumber: ${labelInfo.antennaNumber}"
                    )
                    Log.e("zx-通道门-", "-------------labelInfo.fastID: ${labelInfo.fastID}")
                    Log.e("zx-通道门-", "-------------labelInfo.rssi: ${labelInfo.rssi}")
                    Log.e(
                        "zx-通道门-",
                        "-------------labelInfo.operatingTime: ${labelInfo.operatingTime}"
                    )
                    Log.e("zx-通道门-", "-------------labelInfo.epcLength: ${labelInfo.epcLength}")
                    Log.e("zx-通道门-", "-------------labelInfo.epc: ${labelInfo.epc}")
                    Log.e("zx-通道门-", "-------------labelInfo.tid: ${labelInfo.tid}")
                    Log.e(
                        "zx-通道门-",
                        "-------------labelInfo.inventoryNumber: ${labelInfo.inventoryNumber}"
                    )
                    labelInfo.antennaNumber = labelInfo.antennaNumber + 1

                    // todo 发现在库状态1的档案,异常出库就报警
                    val dossier = DossierOperatingService.getInstance().queryByEPC(labelInfo.epc)
                    if (dossier != null && dossier.operatingType == 1) {
                        Log.e("zx-通道门", "准备报警")

                        // todo 报警,通道门读写器id  "204776152"
                        val deviceList = DeviceService.getInstance().loadAll()
                        if (deviceList.size > 1) {
                            // 打开报警界面
                            val intent = Intent(this, WarningActivity::class.java)
                            intent.putExtra("warningDossier",JSON.toJSONString(dossier))
                            startActivity(intent)

                            /**
                             * @param ID               读写器ID
                             * @param portNumber       引脚序号 0x00：NO1； 0x01：NO2
                             * @param electricityLevel 电平 0x00：低电平； 0x01：高电平
                             */
                            UR880Entrance.getInstance().send(
                                UR880SendInfo.Builder()
                                    .setGPOOutputStatus(deviceList[1].deviceId, 0x01, 0x01)
                                    .build()
                            )
                            UR880Entrance.getInstance().send(
                                UR880SendInfo.Builder()
                                    .setGPOOutputStatus(deviceList[1].deviceId, 0x02, 0x01)
                                    .build()
                            )
                        }

                    }

                }

            }
            CANCEL_INVENTORY -> {
                Log.e("zx-通道门", "停止盘点")
                Toast.makeText(this, "停止盘点", Toast.LENGTH_SHORT).show()
            }
            END_INVENTORY -> {
                Log.e("zx-通道门", "盘点结束")
                Toast.makeText(this, "盘点结束", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 设置GPIO口
    private val mFactorySettingListener = object : FactorySettingListener {
        override fun setGPOOutputStatusResult(result: Boolean, errorNumber: Int) {
//            /**
//             * result : 设置结果
//             * errorNumber : 错误Code
//             */
            Log.e("zx-通道门", "设置结果:$result,错误code:$errorNumber")

        }

        override fun setAntennaConfigurationResult(p0: Boolean, p1: Int) {
            TODO("Not yet implemented")
        }

        override fun deviceRestartResult(p0: Int) {
            TODO("Not yet implemented")
        }

        override fun timeSynchronizationResult(p0: Int) {
            TODO("Not yet implemented")
        }

        override fun getAntennaStandingWaveRatioResult(p0: Float, p1: Float, p2: Float, p3: Float) {
            TODO("Not yet implemented")
        }

        override fun getBuzzerStatusResult(p0: Int) {
            TODO("Not yet implemented")
        }

        override fun getGPIOutputStatusResult(
            result: Boolean,
            errorNumber: Int,
            portZeroStatus: Int,
            portOneStatus: Int,
            portTwoStatus: Int,
            portThreeStatu: Int
        ) {
            /**
             * boolean result, int errorNumber, int portZeroStatus, int portOneStatus, int portTwoStatus, int portThreeStatu
             * result         ：获取结果
             * errorNumber    ： 错误code
             * portZeroStatus ： NO1引脚电平
             * portOneStatus  ： NO2引脚电平
             */
            Log.e(
                "zx-通道门",
                "获取GPIO引脚结果:$result,错误code:$errorNumber, 引脚0:$portZeroStatus,引脚1:$portOneStatus"
            )
        }

        override fun getAntennaConfigurationResult(
            p0: Boolean,
            p1: Int,
            p2: Int,
            p3: Int,
            p4: Int,
            p5: Int,
            p6: Int,
            p7: Int,
            p8: Int,
            p9: Int,
            p10: Int,
            p11: Int,
            p12: Int,
            p13: Int,
            p14: Int,
            p15: Int,
            p16: Int,
            p17: Int
        ) {
            TODO("Not yet implemented")
        }

        override fun setBuzzerStatusResult(p0: Int) {
            TODO("Not yet implemented")
        }
    }

    private val mInventoryListener = object : InventoryListener {
        override fun startInventory(p0: Int) {
            mNetHandler.sendEmptyMessage(START_INVENTORY)
        }

        override fun inventoryValue(p0: LabelInfo?) {
            val msg = Message.obtain()
            msg.what = INVENTORY_VALUE
            msg.obj = p0
            mNetHandler.sendMessage(msg)
        }

        override fun cancel(p0: Int, p1: Int) {
            mNetHandler.sendEmptyMessage(CANCEL_INVENTORY)
        }

        override fun endInventory(p0: Int) {
            mNetHandler.sendEmptyMessage(END_INVENTORY)
        }

    }

}
