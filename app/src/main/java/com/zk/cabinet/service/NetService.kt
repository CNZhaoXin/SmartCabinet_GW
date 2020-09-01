package com.zk.cabinet.service

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.zk.cabinet.constant.SelfComm
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.net.NetworkRequest
import com.zk.common.utils.ActivityUtil
import com.zk.common.utils.LogUtil
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

    private fun handleMessage(receiveMessage: Message) {
        when (receiveMessage.what) {
            SelfComm.NET_SERVICE_CONNECT -> {
                mGuideMessenger = receiveMessage.replyTo
            }
        }
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

        return Messenger(mNetHandler).binder
    }

    private class NetHandler(looper: Looper, netService: NetService) : Handler(looper) {
        private val mainWeakReference = WeakReference(netService)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mainWeakReference.get()!!.handleMessage(msg)
        }
    }
}
