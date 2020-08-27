package com.zk.cabinet.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.cabinet.R
import com.zk.cabinet.adapter.DemoInterfaceAdapter
import com.zk.cabinet.adapter.DeviceAdapter
import com.zk.cabinet.adapter.DialogDossierDetailsAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Cabinet
import com.zk.cabinet.bean.Device
import com.zk.cabinet.bean.ResultGetList
import com.zk.cabinet.databinding.ActivityDemoInterfaceBinding
import com.zk.cabinet.databinding.DialogDeviceSingleSelectBinding
import com.zk.cabinet.databinding.DialogDossierDetailsBinding
import com.zk.cabinet.db.CabinetService
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.common.utils.LogUtil
import com.zk.rfid.bean.LabelInfo
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.callback.InventoryListener
import com.zk.rfid.ur880.UR880Entrance
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference
import kotlin.properties.Delegates

class DemoInterfaceActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mDemoInterfaceBinding: ActivityDemoInterfaceBinding
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var mCabinetList: List<Cabinet>
    private lateinit var mDemoInterfaceAdapter: DemoInterfaceAdapter
    private lateinit var mHandler: DemoInterfaceHandler
    private lateinit var mDevice: Device
    private var floor = 1

    // private var isSubmitData = false
    private var inventoryId = "-1"
    private var isAutomatic by Delegates.notNull<Boolean>()
    private var mInventoryIdList = ArrayList<String>()
    private val mDeviceList = ArrayList<Device>()
    private var stockList = ArrayList<ResultGetList.DataBean>()

    companion object {
        private const val START_INVENTORY = 0x01
        private const val INVENTORY_VALUE = 0x02
        private const val CANCEL_INVENTORY = 0x03
        private const val END_INVENTORY = 0x04
        private const val SUBMITTED_SUCCESS = 0x05
        private const val SUBMITTED_FAIL = 0x06

        private const val GET_LIST_SUCCESS = 0x07
        private const val GET_LIST_FAIL = 0x08

        private const val AUTOMATIC = "isAutomatic"
        private const val CAB_CODE_LIST = "cabCodeList"
        private const val INVENTORY_ID = "inventoryId"
        fun newIntent(
            packageContext: Context,
            isAutomatic: Boolean,
            cabCodeList: ArrayList<String>? = null,
            inventoryId: ArrayList<String>? = null
        ): Intent {
            val intent = Intent(packageContext, DemoInterfaceActivity::class.java)
            intent.putExtra(AUTOMATIC, isAutomatic)
            intent.putExtra(CAB_CODE_LIST, cabCodeList)
            intent.putExtra(INVENTORY_ID, inventoryId)
            return intent
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            START_INVENTORY -> {
                mProgressDialog.setMessage("正在盘点第『 ${floor + 1} 』层...")
                // showToast("开始盘点${floor + 1}层")
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
                for (cabinet in mCabinetList) {
                    if (cabinet.antennaNumber == labelInfo.antennaNumber) {
                        if (cabinet.labelInfoList == null) {
                            cabinet.labelInfoList = ArrayList()
                            cabinet.labelInfoList!!.add(labelInfo)
                            mDemoInterfaceAdapter.notifyDataSetChanged()
                        } else {
                            if (!cabinet.labelInfoList.contains(labelInfo)) {
                                cabinet.labelInfoList!!.add(labelInfo)
                                mDemoInterfaceAdapter.notifyDataSetChanged()
                            }
                        }
                        break
                    }
                }
            }
            CANCEL_INVENTORY -> {
                showToast("停止盘点")
            }
            END_INVENTORY -> {
                floor++
                // showToast("${floor}层盘点结束")
                if (floor < 5) {
                    UR880Entrance.getInstance().send(
                        UR880SendInfo.Builder().inventory(mDevice.deviceId, 0, floor, 0).build()
                    )
                } else {
                    mDemoInterfaceBinding.btnInventoryStorage.isEnabled = true
                    if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                    if (isAutomatic) {
                        warehousingSubmission()
//                        isSubmitData = false
//                        if (mProgressDialog.isShowing) mProgressDialog.dismiss()
//                        // showToast(msg.obj.toString())
//
//                        if (mDeviceList.size > 0) {
//                            mDevice = mDeviceList.removeAt(0)
//                            inventoryId = mInventoryIdList.removeAt(0)
//                            // 盘点盘点单中的多个柜体
//                            mDemoInterfaceBinding.demoInterfaceBoxNumberTv.text =
//                                "${mDevice.deviceName}"
//                            // "柜体名称：${mDevice.deviceName} (${mDevice.deviceId})"
//
//                            startInventory()
//                        } else {
//                            finish()
//                        }
                    }
                }
            }
            SUBMITTED_SUCCESS -> {
//                isSubmitData = false
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                showToast(msg.obj.toString())
                if (isAutomatic) {
                    if (mDeviceList.size > 0) {
                        mDevice = mDeviceList.removeAt(0)
                        inventoryId = mInventoryIdList.removeAt(0)
                        // 盘点盘点单中的多个柜体
                        mDemoInterfaceBinding.demoInterfaceBoxNumberTv.text =
                            "${mDevice.deviceName}"
                        // "柜体名称：${mDevice.deviceName} (${mDevice.deviceId})"
                        showStockList(mDevice)
                    } else {
                        finish()
                    }
                }
            }
            SUBMITTED_FAIL -> {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                showToast(msg.obj.toString())
//                if (isAutomatic) {
                finish()
//                }
            }

            GET_LIST_FAIL -> {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                showToast(msg.obj.toString())
                finish()
            }

            GET_LIST_SUCCESS -> {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                val stockList = msg.obj as ArrayList<ResultGetList.DataBean>
                // 展示库存数据,mCabinetList开始是固定的120条格子基础数据,将库存数据根据position和light封装到格子数据中
                for (cabinet in mCabinetList) {
                    // 如果是自动盘库可能存在多个柜子,所以得先清除数据
                    if (isAutomatic && cabinet.stockList != null) {
                        cabinet.stockList.clear()
                        cabinet.stockList == null
                    }
                    for (stock in stockList) {
                        if (cabinet.floor.toString() == stock.position && cabinet.position.toString() == stock.light) {
                            if (cabinet.stockList == null) {
                                cabinet.stockList = ArrayList()
                                cabinet.isStock = true
                            }
                            cabinet.stockList.add(stock)
                        }
                    }
                }
                mDemoInterfaceAdapter.notifyDataSetChanged()

                if (isAutomatic)
                    startInventory()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDemoInterfaceBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_demo_interface)

        mDemoInterfaceBinding.onClickListener = this

        val name = mSpUtil.getString(SharedPreferencesUtil.Key.NameTemp, "xxx")
        mDemoInterfaceBinding.tvOperator.text = name

        mHandler = DemoInterfaceHandler(this)

        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)

        isAutomatic = intent.getBooleanExtra(AUTOMATIC, false)

        if (!isAutomatic) { // 手动进入盘点界面
            // 是否开启倒计时关闭
            isAutoFinish = true

            val canOperateCabinetList =
                mSpUtil.getString(SharedPreferencesUtil.Key.CanOperateCabinet, "")
            val gson = Gson()
            val deviceList = gson.fromJson<List<Device>>(
                canOperateCabinetList,
                object : TypeToken<List<Device?>?>() {}.type
            )

            showSingleSelectDialog(deviceList)
            initView()

        } else {   // 自动盘点时界面
            // 自动盘点时关闭倒计时
            isAutoFinish = false

            mDemoInterfaceBinding.tvOperatorText.visibility = View.GONE
            mDemoInterfaceBinding.llCountClock.visibility = View.GONE
            mDemoInterfaceBinding.tvOperator.visibility = View.GONE

            mDemoInterfaceBinding.btnBack.visibility = View.GONE
            mDemoInterfaceBinding.btnInventoryStorage.visibility = View.GONE
            mDemoInterfaceBinding.btnSubmit.visibility = View.GONE
            mDemoInterfaceBinding.btnOpenDoor.visibility = View.GONE
            mDemoInterfaceBinding.btnInventoryStop.visibility = View.GONE

            mInventoryIdList = intent.getStringArrayListExtra(INVENTORY_ID)!!
            val cabCodeList = intent.getStringArrayListExtra(CAB_CODE_LIST)!!
            for (deviceId in cabCodeList) {
                mDeviceList.add(DeviceService.getInstance().queryByDeviceName(deviceId))
            }
            mDevice = mDeviceList.removeAt(0)
            inventoryId = mInventoryIdList.removeAt(0)
            // mDemoInterfaceBinding.demoInterfaceBoxNumberTv.text = "柜体名称：${mDevice.deviceName}(${mDevice.deviceId})"
            mDemoInterfaceBinding.demoInterfaceBoxNumberTv.text = "${mDevice.deviceName}"

            initView()
            showStockList(mDevice)
        }
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
                // 点击要展示档案数据详情
                val mStockList = mCabinetList[position].stockList
                if (mStockList != null && mStockList.size > 0) {
                    // showToast("数据条目数:" + mCabinetList[position].stockList.size)
                    showDossierDetailsDialog(mStockList)
                } else {
                    showToast("暂无数据")
                }
            }
        }
        UR880Entrance.getInstance().addOnInventoryListener(mInventoryListener)
    }

    private var mDeviceSingleSelectDialogBinding: DialogDeviceSingleSelectBinding? = null
    private var mSingleSelectDialog: AlertDialog? = null
    private lateinit var mDialogDeviceAdapter: DeviceAdapter

    // 柜体单选弹窗
    private fun showSingleSelectDialog(deviceList: List<Device>) {
        if (mSingleSelectDialog == null) {
            mDeviceSingleSelectDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.dialog_device_single_select,
                null,
                false
            )

            mSingleSelectDialog = AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(mDeviceSingleSelectDialogBinding!!.root)
                .create()

            val window = mSingleSelectDialog!!.window
            window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        var selectPosition = 0
        mDialogDeviceAdapter = DeviceAdapter(this, deviceList)
        mDeviceSingleSelectDialogBinding!!.listView.adapter = mDialogDeviceAdapter
        mDeviceSingleSelectDialogBinding!!.listView.descendantFocusability = FOCUS_BLOCK_DESCENDANTS
        mDeviceSingleSelectDialogBinding!!.setOnItemClickListener { adapterView, view, position, id ->
            for (indices in deviceList.indices) {
                if (position != indices) {
                    deviceList[indices].isSelected = false
                } else {
                    deviceList[indices].isSelected = true
                    selectPosition = position
                    mDeviceSingleSelectDialogBinding!!.btnConfirm.isEnabled = true
                    mDeviceSingleSelectDialogBinding!!.btnConfirm.background =
                        getDrawable(R.drawable.selector_menu_green)
                }
            }
            mDialogDeviceAdapter.notifyDataSetChanged()
        }
        mDeviceSingleSelectDialogBinding!!.setOnClickListener { view: View? ->
            if (view!!.id == R.id.btn_cancel) {
                mSingleSelectDialog!!.dismiss()
                finish()
            } else if (view.id == R.id.btn_confirm) {
                //  mDemoInterfaceBinding.demoInterfaceBoxNumberTv.text = "柜体名称：${mDevice.deviceName}(${mDevice.deviceId})"
                mDevice = deviceList[selectPosition]
                mDemoInterfaceBinding.demoInterfaceBoxNumberTv.text = "${mDevice.deviceName}"
                mSingleSelectDialog!!.dismiss()
                showStockList(mDevice)
            }
        }

        mSingleSelectDialog!!.show()
    }

    /**
     * 获取柜子库存数据
     */
    private fun showStockList(mDevice: Device) {
        mProgressDialog.setMessage("正在获取库存数据，请稍后...")
        if (!mProgressDialog.isShowing) mProgressDialog.show()

        val requestUrl = "${NetworkRequest.instance.mList}?cabCode=" + mDevice.deviceName
        Log.e("zx-获取库存请求参数:", requestUrl)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            requestUrl,
            Response.Listener { response ->
                try {
                    Log.e("zx-获取库存结果:", "$response")

                    val resultGetList = JSON.parseObject<ResultGetList>(
                        "$response", ResultGetList::class.java
                    )

                    if (resultGetList.isSuccess) {
                        val stockList = resultGetList.data

                        val msg = Message.obtain()
                        msg.what = GET_LIST_SUCCESS
                        msg.obj = stockList
                        mHandler.sendMessageDelayed(msg, 800)
                    } else {
                        val msg = Message.obtain()
                        msg.what = GET_LIST_FAIL
                        if (resultGetList.message == null)
                            msg.obj = "获取库存结果错误"
                        else
                            msg.obj = resultGetList.message
                        mHandler.sendMessageDelayed(msg, 800)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    val msg = Message.obtain()
                    msg.what = GET_LIST_FAIL
                    msg.obj = "数据解析失败"
                    mHandler.sendMessageDelayed(msg, 800)
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
                val message = Message.obtain()
                message.what = GET_LIST_FAIL
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


    private var mDossierDetailsDialogBinding: DialogDossierDetailsBinding? = null
    private var mDossierDetailsDialog: AlertDialog? = null
    private lateinit var mDossierDetailsAdapter: DialogDossierDetailsAdapter

    // 档案详情Dialog
    private fun showDossierDetailsDialog(stockList: ArrayList<ResultGetList.DataBean>) {
        if (mDossierDetailsDialog == null) {
            mDossierDetailsDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.dialog_dossier_details,
                null,
                false
            )

            mDossierDetailsDialog = android.app.AlertDialog.Builder(this)
                .setCancelable(true)
                .setView(mDossierDetailsDialogBinding!!.root)
                .create()

            val window = mDossierDetailsDialog!!.window
            window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        mDossierDetailsAdapter = DialogDossierDetailsAdapter(this, stockList)
        mDossierDetailsDialogBinding!!.listView.adapter = mDossierDetailsAdapter
        mDossierDetailsDialogBinding!!.listView.descendantFocusability =
            ViewGroup.FOCUS_BLOCK_DESCENDANTS

        mDossierDetailsDialogBinding!!.setOnClickListener { view: View? ->
            if (view!!.id == R.id.btn_cancel) {
                mDossierDetailsDialog!!.dismiss()
            }
        }
        mDossierDetailsDialog!!.show()
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
            R.id.btn_back -> {
                finish()
            }
//            R.id.btn_submit -> {
//                warehousingSubmission()
//            }
            R.id.btn_inventory_storage -> {
//                if (isSubmitData) {
//                    showToast("请先提交数据！")
//                    return
//                }
                startInventory()
            }
            // 停止盘点
            R.id.btn_inventory_stop -> {
                UR880Entrance.getInstance()
                    .send(UR880SendInfo.Builder().cancel(mDevice.deviceId).build())
            }
            R.id.btn_open_door -> {
                UR880Entrance.getInstance()
                    .send(UR880SendInfo.Builder().openDoor(mDevice.deviceId, 0).build())
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

    private fun startInventory() {
        // isSubmitData = true
        mDemoInterfaceBinding.btnInventoryStorage.isEnabled = false

        mProgressDialog.setMessage("准备开始盘点，请稍后...")

        if (!mProgressDialog.isShowing) mProgressDialog.show()

        floor = 0
        for (cabinet in mCabinetList) {
            if (cabinet.labelInfoList != null) {
                cabinet.labelInfoList.clear()
                cabinet.labelInfoList == null
            }
        }
        mDemoInterfaceAdapter.notifyDataSetChanged()
        UR880Entrance.getInstance()
            .send(UR880SendInfo.Builder().inventory(mDevice.deviceId, 0, floor, 0).build())
    }

    private fun warehousingSubmission() {
//        if (!isSubmitData) {
//            showToast("无需提交数据")
//            return
//        }
        mProgressDialog.setMessage("正在提交盘库数据，请稍后...")
        if (!mProgressDialog.isShowing) mProgressDialog.show()

        val jsonObject = JSONObject()
        try {
            val orderItemsJsonArray = JSONArray()
            val inventoriesJsonArray = JSONArray()
            for (cabinet in mCabinetList) {
                if (cabinet.labelInfoList != null && cabinet.labelInfoList.size > 0) {
                    for (labelInfo in cabinet.labelInfoList) {
                        val changedObject = JSONObject()
                        changedObject.put("rfidNum", labelInfo.epc)
                        changedObject.put("cabCode", mDevice.deviceName)
                        val light =
                            if (labelInfo.antennaNumber % 24 == 0) 24 else labelInfo.antennaNumber % 24
                        val floor =
                            if (labelInfo.antennaNumber % 24 == 0) labelInfo.antennaNumber / 24 else (labelInfo.antennaNumber / 24 + 1)
                        changedObject.put("position", floor)
                        changedObject.put("light", light)
                        changedObject.put(
                            "inputOrg",
                            mSpUtil.getString(SharedPreferencesUtil.Key.OrgCodeTemp, "")!!
                        )
                        orderItemsJsonArray.put(changedObject)
                    }
                }
            }
            if (orderItemsJsonArray.length() == 0) {
                val msg = Message.obtain()
                msg.what = SUBMITTED_SUCCESS
                msg.obj = "无可提交的数据"
                mHandler.sendMessageDelayed(msg, 800)
                return
            }
            val inventories = JSONObject()
            inventories.put("inventoryId", inventoryId)
            inventories.put("orderItems", orderItemsJsonArray)
            inventoriesJsonArray.put(inventories)
            jsonObject.put("inventories", inventoriesJsonArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e("zx-提交的盘库数据:", "--------------$jsonObject")
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, NetworkRequest.instance.mInventoryReport,
            jsonObject, Response.Listener { response ->
                try {
                    LogUtil.instance.d("----------------------------$response")
                    val success = response!!.getBoolean("success")
                    if (success) {
                        val msg = Message.obtain()
                        msg.what = SUBMITTED_SUCCESS
                        msg.obj = "数据提交成功"
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

    override fun onDestroy() {
//        UR880Entrance.getInstance().removeCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().removeInventoryListener(mInventoryListener)

        super.onDestroy()
    }

}