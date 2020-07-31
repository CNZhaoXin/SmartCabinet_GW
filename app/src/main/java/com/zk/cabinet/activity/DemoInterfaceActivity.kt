package com.zk.cabinet.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.zk.cabinet.R
import com.zk.cabinet.adapter.DemoInterfaceAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Cabinet
import com.zk.cabinet.databinding.ActivityDemoInterfaceBinding
import com.zk.cabinet.db.CabinetService
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.common.utils.LogUtil
import com.zk.common.utils.TimeUtil
import com.zk.rfid.bean.LabelInfo
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.callback.InventoryListener
import com.zk.rfid.ur880.UR880Entrance
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

class DemoInterfaceActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mDemoInterfaceBinding: ActivityDemoInterfaceBinding
    private lateinit var mProgressSyncUserDialog: ProgressDialog
    private lateinit var mCabinetList: List<Cabinet>
    private lateinit var mDemoInterfaceAdapter: DemoInterfaceAdapter
    private lateinit var mHandler: DemoInterfaceHandler
    private lateinit var mDeviceId: String
    private var floor = 1

    companion object {
        private const val START_INVENTORY = 0x01
        private const val INVENTORY_VALUE = 0x02
        private const val CANCEL_INVENTORY = 0x03
        private const val END_INVENTORY = 0x04

        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, DemoInterfaceActivity::class.java)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
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
                labelInfo.antennaNumber = labelInfo.antennaNumber + 1
                for (cabinet in mCabinetList){
                    if (cabinet.antennaNumber == labelInfo.antennaNumber){
                        if (cabinet.labelInfoList == null){
                            cabinet.labelInfoList = ArrayList()
                            cabinet.labelInfoList!!.add(labelInfo)
                            mDemoInterfaceAdapter.notifyDataSetChanged()
                        } else {
                            if (!cabinet.labelInfoList.contains(labelInfo)){
                                cabinet.labelInfoList!!.add(labelInfo)
                                mDemoInterfaceAdapter.notifyDataSetChanged()
                            }
                        }
                        break
                    }
                }
            }
            CANCEL_INVENTORY -> {
                Toast.makeText(this, "停止盘点", Toast.LENGTH_SHORT).show()
            }
            END_INVENTORY -> {
                Toast.makeText(this, "${floor + 1}层盘点结束", Toast.LENGTH_SHORT).show()
                if(floor < 5){
                    floor++
                    UR880Entrance.getInstance()
                        .send(UR880SendInfo.Builder().inventory(mDeviceId, 0, floor, 0).build())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDemoInterfaceBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_demo_interface)
        mDemoInterfaceBinding.onClickListener = this
        setSupportActionBar(mDemoInterfaceBinding.demoInterfaceToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mHandler = DemoInterfaceHandler(this)

        mProgressSyncUserDialog = ProgressDialog(this)

        val deviceList = DeviceService.getInstance().loadAll()
        val singleChoiceItems = arrayOfNulls<String>(deviceList.size)
        for (indices in deviceList.indices) {
            singleChoiceItems[indices] = deviceList[indices].deviceName
        }
        val itemSelected = 0
        mDeviceId = deviceList[itemSelected].deviceId
        AlertDialog.Builder(this)
            .setTitle("请选择您要操作的柜子")
            .setSingleChoiceItems(
                singleChoiceItems,
                itemSelected
            ) { dialog, which ->
                mDeviceId = deviceList[which].deviceId
                dialog.cancel()
            }
            .setCancelable(false)
            .show()

        initView()
    }

    private fun initView() {
        mCabinetList = CabinetService.getInstance().loadAll()
        mDemoInterfaceAdapter = DemoInterfaceAdapter(mCabinetList, this)
        val manager = GridLayoutManager(this, 5, LinearLayoutManager.HORIZONTAL, false)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return mCabinetList[position].proportion
            }
        }
        mDemoInterfaceBinding.demoInterfaceRv.layoutManager = manager
        mDemoInterfaceBinding.demoInterfaceRv.itemAnimator = DefaultItemAnimator()
        mDemoInterfaceBinding.demoInterfaceRv.adapter = mDemoInterfaceAdapter
        mDemoInterfaceAdapter.mOnItemClickListener = object :
            DemoInterfaceAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {

                LogUtil.instance.d("点击${position}")
            }
        }
        UR880Entrance.getInstance().addOnInventoryListener(mInventoryListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mDemoInterfaceBinding.demoInterfaceCountdownTv.text = millisUntilFinished.toString()
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.demo_interface_inventory_start_btn -> {
                floor = 0
                for (cabinet in mCabinetList){
                    if (cabinet.labelInfoList != null) cabinet.labelInfoList.clear()
                }
                UR880Entrance.getInstance()
                    .send(UR880SendInfo.Builder().inventory(mDeviceId, 0, floor, 0).build())
            }
            R.id.demo_interface_inventory_stop_btn -> {
                UR880Entrance.getInstance()
                    .send(UR880SendInfo.Builder().cancel(mDeviceId).build())
            }
            R.id.demo_interface_open_door_btn -> {
                UR880Entrance.getInstance()
                    .send(UR880SendInfo.Builder().openDoor(mDeviceId, 0).build())
            }
        }
    }

    private class DemoInterfaceHandler(demoInterfaceActivity: DemoInterfaceActivity) : Handler() {
        private val demoInterfaceWeakReference = WeakReference(demoInterfaceActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            demoInterfaceWeakReference.get()!!.handleMessage(msg)
        }
    }

    private fun warehousingSubmission() {
//        mProgressSyncUserDialog.setTitle("提交盘库列表")
//        mProgressSyncUserDialog.setMessage("正在提盘入库列表，请稍后......")
//        if (!mProgressSyncUserDialog.isShowing) mProgressSyncUserDialog.show()
//        val jsonObject = JSONObject()
//        try {
//            val orderItemsJsonArray = JSONArray()
//            for (dossierChanged in dossierList) {
//                if (dossierChanged.isSelected) {
//                    val changedObject = JSONObject()
//                    changedObject.put("warrantNum", dossierChanged.warrantNum)
//                    changedObject.put("rfidNum", dossierChanged.rfidNum)
//                    changedObject.put("cabCode", mDevice.deviceName)
//                    changedObject.put("inputDate", TimeUtil.nowTimeOfSeconds())
//                    changedObject.put("position", dossierChanged.floor)
//                    changedObject.put("light", dossierChanged.light)
//                    orderItemsJsonArray.put(changedObject)
//                }
//            }
//            if (orderItemsJsonArray.length() == 0){
//                if (mProgressSyncUserDialog.isShowing) mProgressSyncUserDialog.dismiss()
//                finish()
//                return
//            }
//            jsonObject.put("orderItems", orderItemsJsonArray)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        val jsonObjectRequest = JsonObjectRequest(
//            Request.Method.POST, NetworkRequest.instance.mInventoryReport,
//            jsonObject, Response.Listener { response ->
//                try {
//                    LogUtil.instance.d("----------------------------$response")
//                    val success = response!!.getBoolean("success")
//                    if (success) {
//                        val msg = Message.obtain()
//                        msg.what = WarehousingOperatingActivity.SUBMITTED_SUCCESS
//                        mHandler.sendMessage(msg)
//                    } else {
//                        val msg = Message.obtain()
//                        msg.what = WarehousingOperatingActivity.SUBMITTED_FAIL
//                        msg.obj = response.getString("message")
//                        mHandler.sendMessage(msg)
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                    val msg = Message.obtain()
//                    msg.what = WarehousingOperatingActivity.SUBMITTED_FAIL
//                    msg.obj = "数据解析失败。"
//                    mHandler.sendMessage(msg)
//                }
//            }, Response.ErrorListener { error ->
//                val msg = if (error != null)
//                    if (error.networkResponse != null)
//                        "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
//                    else
//                        "errorCode: -1 VolleyError: $error"
//                else {
//                    "errorCode: -1 VolleyError: 未知"
//                }
//                val message = Message.obtain()
//                message.what = WarehousingOperatingActivity.SUBMITTED_FAIL
//                message.obj = msg
//                mHandler.sendMessage(message)
//            })
//        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
//            10000,
//            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        )
//        NetworkRequest.instance.add(jsonObjectRequest)
    }
}