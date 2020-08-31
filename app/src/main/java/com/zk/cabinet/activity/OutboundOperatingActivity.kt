package com.zk.cabinet.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.zk.cabinet.R
import com.zk.cabinet.adapter.OutboundAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.bean.ResultGetOutBound
import com.zk.cabinet.databinding.ActivityOutboundOperatingBinding
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.rfid.bean.LabelInfo
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.callback.CabinetInfoListener
import com.zk.rfid.callback.InventoryListener
import com.zk.rfid.ur880.UR880Entrance
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

class OutboundOperatingActivity : TimeOffAppCompatActivity(), AdapterView.OnItemClickListener,
    View.OnClickListener {
    private lateinit var mOutboundBinding: ActivityOutboundOperatingBinding
    private lateinit var mHandler: OutboundOperatingHandler
    private lateinit var mProgressDialog: ProgressDialog
    private var mDevice: Device? = null
    private var outBoundList =
        ArrayList<ResultGetOutBound.NameValuePairsBeanX.DataBean.ValuesBean>()
    private lateinit var mDossierAdapter: OutboundAdapter
    private val labelInfoList = ArrayList<LabelInfo>()
    private var mFloor = -1

    companion object {
        private const val OPEN_DOOR_RESULT = 0x01
        private const val START_INVENTORY = 0x02
        private const val INVENTORY_VALUE = 0x03
        private const val CANCEL_INVENTORY = 0x04
        private const val END_INVENTORY = 0x05
        private const val GET_INFRARED_AND_LOCK = 0x06
        private const val SUBMITTED_SUCCESS = 0x07
        private const val SUBMITTED_FAIL = 0x08

        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, OutboundOperatingActivity::class.java)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            GET_INFRARED_AND_LOCK -> {
                val data = msg.data
                val boxStateList = data.getIntegerArrayList("lock")
                val infraredStateList = data.getIntegerArrayList("infrared")

                Log.e("zx-出库操作-boxStateList", "$boxStateList")
                Log.e("zx-出库操作-infraredState", "$infraredStateList")

                // 这里只要红外被触发都会被调用, 哪层的红外被触发 [1,2,3,4,5], 但是多层的时候不返回数据,只有一层不触发才返回数据
                if (infraredStateList!!.isNotEmpty()) {
                    mFloor = infraredStateList[0]
                }

                // 这里只要红外被触发都会被调用, 门开的状态 boxStateList: [1] , 门关闭的状态 boxStateList: []
                if (boxStateList!!.isEmpty()) {
                    isAutoFinish = true
                    timerStart()
                    Log.e("zx-出库操作-", "门关闭-开启界面倒计时")
                }
            }
            OPEN_DOOR_RESULT -> {
                showToast(msg.obj.toString())
                Log.e("zx-出库操作-", "门开启-关闭界面倒计时-出库的档案进行开灯-")
                // 门开启后倒计时关闭
                isAutoFinish = false
                timerCancel()

                // 亮出库列表数据的相应灯位
                for (floor in 1..5) {
                    val lights = ArrayList<Int>()
                    for (dossierOperating in outBoundList) {
                        if (dossierOperating.nameValuePairs.position != null && dossierOperating.nameValuePairs.position.toInt() == floor) {
                            if (dossierOperating.nameValuePairs.light != null)
                                lights.add(dossierOperating.nameValuePairs.light.toInt())
                        }
                    }
                    UR880Entrance.getInstance().send(
                        UR880SendInfo.Builder().turnOnLight(mDevice!!.deviceId, floor, lights)
                            .build()
                    )
                }

            }
            START_INVENTORY -> {
                Toast.makeText(this, "开始盘点", Toast.LENGTH_SHORT).show()
            }
            INVENTORY_VALUE -> {
                val labelInfo = msg.obj as LabelInfo
                labelInfoList.add(labelInfo)
                Log.e("zx-出库-", "-------------labelInfo.deviceID: ${labelInfo.deviceID}")
                Log.e("zx-出库-", "-------------labelInfo.antennaNumber: ${labelInfo.antennaNumber}")
                Log.e("zx-出库-", "-------------labelInfo.fastID: ${labelInfo.fastID}")
                Log.e("zx-出库-", "-------------labelInfo.rssi: ${labelInfo.rssi}")
                Log.e("zx-出库-", "-------------labelInfo.operatingTime: ${labelInfo.operatingTime}")
                Log.e("zx-出库-", "-------------labelInfo.epcLength: ${labelInfo.epcLength}")
                Log.e("zx-出库-", "-------------labelInfo.epc: ${labelInfo.epc}")
                Log.e("zx-出库-", "-------------labelInfo.tid: ${labelInfo.tid}")
                Log.e(
                    "zx-出库-",
                    "-------------labelInfo.inventoryNumber: ${labelInfo.inventoryNumber}"
                )
            }
            CANCEL_INVENTORY -> {
                Toast.makeText(this, "停止盘点", Toast.LENGTH_SHORT).show()
            }
            END_INVENTORY -> {
                Toast.makeText(this, "盘点结束", Toast.LENGTH_SHORT).show()

                // 出库,一般操作是先拿出来,然后会触发红外,触发读写器进行读写,此时应该判断哪个RFID不在了,不在才进行勾选,只要发现不在就勾选,在发现在不处理,因为可能已经拿了一个盒子里其中一本又放回去了
                for (dossier in outBoundList) {
                    if (dossier.nameValuePairs.position.toInt() == mFloor) { // 是否是当前触发的层
                        var isExit = false
                        for (labelInfo in labelInfoList) {
                            if (labelInfo.epc == dossier.nameValuePairs.rfidNum) {
                                isExit = true
                                // dossier.nameValuePairs.isSelected = false
                                break
                            }
                        }
                        if (!isExit) { // 没扫描到,说明原理天线了,可以判定为拿出来了
                            dossier.nameValuePairs.isSelected = true
                        }
                    }
                }

                var hasSelect = false
                for (dossier in outBoundList) {
                    if (dossier.nameValuePairs.isSelected) {
                        hasSelect = true
                        break
                    } else {
                        hasSelect = false
                    }
                }

                if (hasSelect) {
                    mOutboundBinding.btnOutStorage.background =
                        resources.getDrawable(R.drawable.selector_menu_green)
                    mOutboundBinding.btnOutStorage.isEnabled = true
                } else {
                    mOutboundBinding.btnOutStorage.background =
                        resources.getDrawable(R.drawable.shape_btn_un_enable)
                    mOutboundBinding.btnOutStorage.isEnabled = false
                }

                Log.e("zx-出库操作-盘点到的标签数据-", JSON.toJSONString(labelInfoList))
                mDossierAdapter.notifyDataSetChanged()
                labelInfoList.clear()
            }

            SUBMITTED_SUCCESS -> {
                Toast.makeText(this, "出库成功", Toast.LENGTH_SHORT).show()
                mProgressDialog.dismiss()

                val mIterator = outBoundList.iterator()
                while (mIterator.hasNext()) {
                    val next = mIterator.next()
                    if (next.nameValuePairs.isSelected) {
                        mIterator.remove()
                    }
                }

                if (outBoundList.size > 0) {
                    mDossierAdapter.notifyDataSetChanged()
                } else {
                    finish()
                }
            }
            SUBMITTED_FAIL -> {
                Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_SHORT).show()
                mProgressDialog.dismiss()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOutboundBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_outbound_operating)
        val name = mSpUtil.getString(SharedPreferencesUtil.Key.NameTemp, "xxx")
        mOutboundBinding.tvOperator.text = name

        mOutboundBinding.onClickListener = this
        mOutboundBinding.onItemClickListener = this

        mHandler = OutboundOperatingHandler(this)

        UR880Entrance.getInstance().addOnCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().addOnInventoryListener(mInventoryListener)

        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)

        outBoundList =
            intent.getSerializableExtra("OutBoundList") as ArrayList<ResultGetOutBound.NameValuePairsBeanX.DataBean.ValuesBean>
        mDevice =
            DeviceService.getInstance().queryByDeviceName(outBoundList[0].nameValuePairs.cabcode)

//        mOutboundBinding.outboundBoxNumberTv.text = "柜体名称：${mDevice!!.deviceName}(${mDevice!!.deviceId})"
        mOutboundBinding.outboundBoxNumberTv.text = "${mDevice!!.deviceName}"

        // 将选中的待出库的条目的选中状态置为false
        for (dossier in outBoundList) {
            dossier.nameValuePairs.isSelected = false
        }

        mDossierAdapter = OutboundAdapter(this, outBoundList)
        mOutboundBinding.outboundOperatingLv.adapter = mDossierAdapter

        UR880Entrance.getInstance().send(
            UR880SendInfo.Builder().openDoor(mDevice!!.deviceId, 0).build()
        )
    }

    private val mCabinetInfoListener = object : CabinetInfoListener {
        override fun getInfraredOrLockState(p0: ArrayList<Int>?, p1: ArrayList<Int>?) {
            val message = Message.obtain()
            val bundle = Bundle()
            bundle.putIntegerArrayList("lock", p0)
            bundle.putIntegerArrayList("infrared", p1)
            message.what = GET_INFRARED_AND_LOCK
            message.data = bundle
            mHandler.sendMessage(message)
        }

        override fun unlockResult(p0: Int) {
            val message = Message.obtain()
            message.obj = "开门成功"
            message.what = OPEN_DOOR_RESULT
            mHandler.sendMessage(message)
        }

        override fun turnOnLightResult(p0: Int) {

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

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mOutboundBinding.outboundOperatingCountdownTv.text = millisUntilFinished.toString()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_out_storage -> {
                outboundSubmission()
            }

            R.id.btn_back -> {
                finish()
            }
        }
    }

    private class OutboundOperatingHandler(outboundOperatingActivity: OutboundOperatingActivity) :
        Handler() {
        private val outboundOperatingWeakReference = WeakReference(outboundOperatingActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            outboundOperatingWeakReference.get()!!.handleMessage(msg)
        }
    }

    override fun onDestroy() {
        UR880Entrance.getInstance().removeCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().removeInventoryListener(mInventoryListener)

        super.onDestroy()
    }

    private fun outboundSubmission() {
        mProgressDialog.setMessage("正在提交出库列表，请稍后...")
        if (!mProgressDialog.isShowing) mProgressDialog.show()

        val jsonObject = JSONObject()
        try {
            val orderItemsJsonArray = JSONArray()
            for (dossierChanged in outBoundList) {
                if (dossierChanged.nameValuePairs.isSelected) {
                    val changedObject = JSONObject()

                    changedObject.put("rfidNum", dossierChanged.nameValuePairs.rfidNum)
                    changedObject.put("cabCode", mDevice!!.deviceName)
                    changedObject.put(
                        "outOrg",
                        mSpUtil.getString(SharedPreferencesUtil.Key.OrgCodeTemp, "")!!
                    )
                    changedObject.put(
                        "operatorId",
                        mSpUtil.getString(SharedPreferencesUtil.Key.IdTemp, "")!!
                    )
                    changedObject.put("warrantNum", dossierChanged.nameValuePairs.warrantNum)
                    changedObject.put("inputId", dossierChanged.nameValuePairs.inputId)

                    orderItemsJsonArray.put(changedObject)
                }
            }
            jsonObject.put("orderItems", orderItemsJsonArray)

            Log.e("zx-出库提交参数:", "$jsonObject")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, NetworkRequest.instance.mOutboundSubmission,
            jsonObject, Response.Listener { response ->
                try {
                    Log.e("zx-出库提交结果:", "$response")

                    val success = response!!.getBoolean("success")
                    if (success) {
                        val msg = Message.obtain()
                        msg.what = SUBMITTED_SUCCESS
                        mHandler.sendMessageDelayed(msg, 800)
                    } else {
                        val msg = Message.obtain()
                        msg.what = SUBMITTED_FAIL
                        msg.obj = response.getString("message")
                        mHandler.sendMessageDelayed(msg, 800)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    val msg = Message.obtain()
                    msg.what = SUBMITTED_FAIL
                    msg.obj = "数据解析失败"
                    mHandler.sendMessageDelayed(msg, 800)
                }
            }, Response.ErrorListener { error ->
                val msg = if (error != null)
                    if (error.networkResponse != null)
                        "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                    else
                        "errorCode: -1 VolleyError: $error"
                else {
                    "errorCode: -1 VolleyError: 未知"
                }
                val message = Message.obtain()
                message.what = SUBMITTED_FAIL
                message.obj = msg
                mHandler.sendMessageDelayed(message, 800)
            })
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        outBoundList[position].nameValuePairs.isSelected =
            !outBoundList[position].nameValuePairs.isSelected

        if (outBoundList[position].nameValuePairs.isSelected) {
            mOutboundBinding.btnOutStorage.background =
                resources.getDrawable(R.drawable.selector_menu_green)
            mOutboundBinding.btnOutStorage.isEnabled = true
        } else {
            var hasSelect = false
            for (dossierOperating in outBoundList) {
                if (dossierOperating.nameValuePairs.isSelected) {
                    hasSelect = true
                    break
                } else {
                    hasSelect = false
                }
            }

            if (hasSelect) {
                mOutboundBinding.btnOutStorage.background =
                    resources.getDrawable(R.drawable.selector_menu_green)
                mOutboundBinding.btnOutStorage.isEnabled = true
            } else {
                mOutboundBinding.btnOutStorage.background =
                    resources.getDrawable(R.drawable.shape_btn_un_enable)
                mOutboundBinding.btnOutStorage.isEnabled = false
            }
        }

        mDossierAdapter.notifyDataSetChanged()
    }
}