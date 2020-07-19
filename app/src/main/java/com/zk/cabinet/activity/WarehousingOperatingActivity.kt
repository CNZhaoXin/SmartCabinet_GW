package com.zk.cabinet.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.zk.cabinet.R
import com.zk.cabinet.adapter.DossierAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.bean.Dossier
import com.zk.cabinet.databinding.ActivityWarehousingOperatingBinding
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.db.DossierOperatingService
import com.zk.cabinet.net.NetworkRequest
import com.zk.common.utils.LogUtil
import com.zk.common.utils.TimeUtil
import com.zk.rfid.bean.LabelInfo
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.callback.CabinetInfoListener
import com.zk.rfid.callback.InventoryListener
import com.zk.rfid.ur880.UR880Entrance
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

class WarehousingOperatingActivity : TimeOffAppCompatActivity() {
    private lateinit var mWarehousingBinding: ActivityWarehousingOperatingBinding
    private lateinit var mHandler: WarehousingOperatingHandler
    private lateinit var mProgressSyncUserDialog: ProgressDialog
    private lateinit var mDevice: Device
    private val dossierList = ArrayList<Dossier>()
    private lateinit var mDossierAdapter: DossierAdapter
    private var mDoorIsOpen = false

    companion object {
        private const val OPEN_DOOR_RESULT = 0x01
        private const val START_INVENTORY = 0x02
        private const val INVENTORY_VALUE = 0x03
        private const val CANCEL_INVENTORY = 0x04
        private const val END_INVENTORY = 0x05
        private const val SUBMITTED_SUCCESS = 0x06
        private const val SUBMITTED_FAIL = 0x07
        private const val GET_INFRARED_AND_LOCK = 0x08

        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, WarehousingOperatingActivity::class.java)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            OPEN_DOOR_RESULT -> {
                showToast(msg.obj.toString())
            }
            START_INVENTORY -> {
                Toast.makeText(this, "开始盘点", Toast.LENGTH_SHORT).show()
            }
            INVENTORY_VALUE -> {
                val labelInfo = msg.obj as LabelInfo
                LogUtil.instance.d("-------------labelInfo.deviceID: ${labelInfo.deviceID}")
                LogUtil.instance.d("-------------labelInfo.antennaNumber: ${labelInfo.antennaNumber}")
                LogUtil.instance.d("-------------labelInfo.fastID: ${labelInfo.fastID}")
                LogUtil.instance.d("-------------labelInfo.rssi: ${labelInfo.rssi}")
                LogUtil.instance.d("-------------labelInfo.operatingTime: ${labelInfo.operatingTime}")
                LogUtil.instance.d("-------------labelInfo.epcLength: ${labelInfo.epcLength}")
                LogUtil.instance.d("-------------labelInfo.epc: ${labelInfo.epc}")
                LogUtil.instance.d("-------------labelInfo.tid: ${labelInfo.tid}")
                LogUtil.instance.d("-------------labelInfo.inventoryNumber: ${labelInfo.inventoryNumber}")

                val dossierOperating = DossierOperatingService.getInstance().queryByEPC(labelInfo.epc)
                if (dossierOperating != null){
                    var isExit = false
                    for (dossier in dossierList){
                        if (dossier.rfidNum == dossierOperating.rfidNum) {
                            dossier.cabinetId = mDevice.deviceName
                            val light = if (labelInfo.antennaNumber % 24 == 0) 24 else labelInfo.antennaNumber % 24
                            val floor = if (labelInfo.antennaNumber % 24 == 0) labelInfo.antennaNumber / 24 else (labelInfo.antennaNumber / 24 + 1)
                            dossier.floor = floor
                            dossier.light = light
                            isExit = true
                        }
                    }
                    if (!isExit) {
                        val dossier = Dossier()
                        dossier.warrantNum = dossierOperating.warrantNum
                        dossier.rfidNum = dossierOperating.rfidNum
                        dossier.warrantName = dossierOperating.warrantName
                        dossier.warrantNo = dossierOperating.warrantNo
                        dossier.warranCate = dossierOperating.warranCate
                        dossier.operatingType = dossierOperating.operatingType
                        dossier.warranType = dossierOperating.warranType
                        dossier.cabinetId = mDevice.deviceName
                        val light =
                            if (labelInfo.antennaNumber % 24 == 0) 24 else labelInfo.antennaNumber % 24
                        val floor =
                            if (labelInfo.antennaNumber % 24 == 0) labelInfo.antennaNumber / 24 else (labelInfo.antennaNumber / 24 + 1)
                        dossier.floor = floor
                        dossier.light = light
                        dossierList.add(dossier)
                    }
                    mDossierAdapter.notifyDataSetChanged()
                }

            }
            CANCEL_INVENTORY -> {
                Toast.makeText(this, "停止盘点", Toast.LENGTH_SHORT).show()
            }
            END_INVENTORY -> {
                Toast.makeText(this, "盘点结束", Toast.LENGTH_SHORT).show()
            }
            SUBMITTED_SUCCESS -> {
                Toast.makeText(this, "数据提交成功", Toast.LENGTH_SHORT).show()
                mProgressSyncUserDialog.dismiss()
                finish()
            }
            SUBMITTED_FAIL -> {
                Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_SHORT).show()
                mProgressSyncUserDialog.dismiss()
            }
            GET_INFRARED_AND_LOCK -> {
                val data = msg.data
                val boxStateList = data.getIntegerArrayList("lock")
                val infraredStateList = data.getIntegerArrayList("infrared")
                if (boxStateList!!.isEmpty()){
                    if (mDoorIsOpen) {
                        mDoorIsOpen = false
                        isAutoFinish = true
                        timerStart()
                        showToast("门关闭")
                    }
                }
                else {
                    if (!mDoorIsOpen) {
                        mDoorIsOpen = true
                        isAutoFinish = false
                        timerCancel()
                        showToast("门开启")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWarehousingBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_warehousing_operating)
        setSupportActionBar(mWarehousingBinding.warehousingOperatingToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mHandler = WarehousingOperatingHandler(this)

        mProgressSyncUserDialog = ProgressDialog(this)

        mDossierAdapter = DossierAdapter(this, dossierList)
        mWarehousingBinding.warehousingOperatingLv.adapter = mDossierAdapter

        val deviceList = DeviceService.getInstance().loadAll()
        val singleChoiceItems = arrayOfNulls<String>(deviceList.size)
        for (indices in deviceList.indices) {
            singleChoiceItems[indices] = deviceList[indices].deviceName
        }
        val itemSelected = 0
        mDevice = deviceList[itemSelected]
        mWarehousingBinding.accessingBoxNumberTv.text = "柜体名称：${mDevice.deviceName}(${mDevice.deviceId})"
        AlertDialog.Builder(this)
            .setTitle("请选择您要操作的柜子")
            .setSingleChoiceItems(
                singleChoiceItems,
                itemSelected
            ) { _, which ->
                mDevice = deviceList[which]
                mWarehousingBinding.accessingBoxNumberTv.text = "柜体名称：${mDevice.deviceName}(${mDevice.deviceId})"
            }
            .setNegativeButton(
                "开门"
            ) { _, _ ->
                UR880Entrance.getInstance()
                    .send(UR880SendInfo.Builder().openDoor(mDevice.deviceId, 0).build())
            }
            .setCancelable(false)
            .show()

        UR880Entrance.getInstance().addOnCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().addOnInventoryListener(mInventoryListener)
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
        mWarehousingBinding.warehousingOperatingCountdownTv.text = millisUntilFinished.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                warehousingSubmission()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private class WarehousingOperatingHandler(warehousingOperatingActivity: WarehousingOperatingActivity) :
        Handler() {
        private val warehousingOperatingWeakReference = WeakReference(warehousingOperatingActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            warehousingOperatingWeakReference.get()!!.handleMessage(msg)
        }
    }

    override fun onDestroy() {
        UR880Entrance.getInstance().removeCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().removeInventoryListener(mInventoryListener)

        super.onDestroy()
    }

    private fun warehousingSubmission() {
        if (dossierList.isEmpty()) {
            finish()
            return
        }
        mProgressSyncUserDialog.setTitle("提交入库列表")
        mProgressSyncUserDialog.setMessage("正在提交入库列表，请稍后......")
        if (!mProgressSyncUserDialog.isShowing) mProgressSyncUserDialog.show()
        val jsonObject = JSONObject()
        try {
            val orderItemsJsonArray = JSONArray()
            for (dossierChanged in dossierList){
                val changedObject = JSONObject()
                changedObject.put("warrantNum", dossierChanged.warrantNum)
                changedObject.put("rfidNum", dossierChanged.rfidNum)
                changedObject.put("cabCode", mDevice.deviceName)
                changedObject.put("inputDate", TimeUtil.nowTimeOfSeconds())
                changedObject.put("position", dossierChanged.floor)
                changedObject.put("light", dossierChanged.light)
                orderItemsJsonArray.put(changedObject)
            }
            jsonObject.put("orderItems", orderItemsJsonArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, NetworkRequest.instance.mWarehousingSubmission,
            jsonObject, Response.Listener { response ->
                try {
                    LogUtil.instance.d("----------------------------$response")
                    val success = response!!.getBoolean("success")
                    if (success) {
                        val msg = Message.obtain()
                        msg.what = SUBMITTED_SUCCESS
                        mHandler.sendMessage(msg)
                    } else {
                        val msg = Message.obtain()
                        msg.what = SUBMITTED_FAIL
                        msg.obj = response.getString("message")
                        mHandler.sendMessage(msg)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    val msg = Message.obtain()
                    msg.what = SUBMITTED_FAIL
                    msg.obj = "数据解析失败。"
                    mHandler.sendMessage(msg)
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
                mHandler.sendMessage(message)
            })
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

}