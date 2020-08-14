package com.zk.cabinet.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.zk.cabinet.R
import com.zk.cabinet.adapter.DeviceAdapter
import com.zk.cabinet.adapter.DossierAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.bean.Dossier
import com.zk.cabinet.databinding.ActivityWarehousingOperatingBinding
import com.zk.cabinet.databinding.DialogDeviceSingleSelectWarehousingBinding
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.db.DossierOperatingService
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
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

class WarehousingOperatingActivity : TimeOffAppCompatActivity(), AdapterView.OnItemClickListener,
    View.OnClickListener {
    private lateinit var mWarehousingBinding: ActivityWarehousingOperatingBinding
    private lateinit var mHandler: WarehousingOperatingHandler
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var mDevice: Device
    private val dossierList = ArrayList<Dossier>()
    private lateinit var mDossierAdapter: DossierAdapter
    private var mDoorIsOpen = false
    private val mCabinet = HashMap<String, ArrayList<Int>>()

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
                LogUtil.instance.d("zx---开锁成功")
                showToast(msg.obj.toString())
            }
            START_INVENTORY -> {
                LogUtil.instance.d("zx---开始盘点")
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
                val dossierOperatingList =
                    DossierOperatingService.getInstance().queryListByEPC(labelInfo.epc)
                if (dossierOperatingList != null) {
                    for (dossierOperating in dossierOperatingList) {
                        var isExit = false
                        for (dossier in dossierList) {
                            if (dossier.rfidNum == dossierOperating.rfidNum && dossier.warrantNum == dossierOperating.warrantNum) {
                                dossier.cabinetId = mDevice.deviceName
                                val light =
                                    if (labelInfo.antennaNumber % 24 == 0) 24 else labelInfo.antennaNumber % 24
                                val floor =
                                    if (labelInfo.antennaNumber % 24 == 0) labelInfo.antennaNumber / 24 else (labelInfo.antennaNumber / 24 + 1)
                                dossier.floor = floor
                                dossier.light = light
                                dossier.isSelected = true
                                isExit = true
                                break
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
                            dossier.isSelected = true
                            dossierList.add(dossier)
                        }
                        mDossierAdapter.notifyDataSetChanged()

                    }

                    for (index in 1..5) {
                        val lights = ArrayList<Int>()
                        for (dossier in dossierList) {
                            if (dossier.floor == index) {
                                lights.add(dossier.light)
                            }
                        }
                        UR880Entrance.getInstance()
                            .send(
                                UR880SendInfo.Builder()
                                    .turnOnLight(mDevice.deviceId, index, lights).build()
                            )
                    }
                }

            }
            CANCEL_INVENTORY -> {
                LogUtil.instance.d("zx---停止盘点")
                Toast.makeText(this, "停止盘点", Toast.LENGTH_SHORT).show()
            }
            END_INVENTORY -> {
                LogUtil.instance.d("zx---盘点结束")
                Toast.makeText(this, "盘点结束", Toast.LENGTH_SHORT).show()
            }
            SUBMITTED_SUCCESS -> {
                Toast.makeText(this, "数据提交成功", Toast.LENGTH_SHORT).show()
                mProgressDialog.dismiss()
                finish()
            }
            SUBMITTED_FAIL -> {
                Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_SHORT).show()
                mProgressDialog.dismiss()
            }
            GET_INFRARED_AND_LOCK -> {
                val data = msg.data
                val boxStateList = data.getIntegerArrayList("lock")
                val infraredStateList = data.getIntegerArrayList("infrared")
                if (boxStateList!!.isEmpty()) {
                    if (mDoorIsOpen) {
                        mDoorIsOpen = false
                        isAutoFinish = true
                        timerStart()
                        showToast("门关闭")

//                        for (index in 1..5) {
//                            val lights = ArrayList<Int>()
//                            UR880Entrance.getInstance()
//                                .send(
//                                    UR880SendInfo.Builder()
//                                        .turnOnLight(mDevice.deviceId, index, lights).build()
//                                )
//                        }
                    }
                } else {
                    if (!mDoorIsOpen) {
                        mDoorIsOpen = true
                        isAutoFinish = false
                        timerCancel()
                        showToast("门开启")

                        val floors = mCabinet[mDevice.deviceName]!!
                        for (index in floors) {
                            val lights = ArrayList<Int>()
                            for (light in 1..24) {
                                lights.add(light)
                            }
                            UR880Entrance.getInstance()
                                .send(
                                    UR880SendInfo.Builder()
                                        .turnOnLight(mDevice.deviceId, index, lights).build()
                                )
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWarehousingBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_warehousing_operating)
        mWarehousingBinding.onClickListener = this
        mWarehousingBinding.onItemClickListener = this

        val name = mSpUtil.getString(SharedPreferencesUtil.Key.NameTemp, "xxx")
        mWarehousingBinding.tvOperator.text = name

        mHandler = WarehousingOperatingHandler(this)

        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)

        mDossierAdapter = DossierAdapter(this, dossierList)
        mWarehousingBinding.warehousingOperatingLv.adapter = mDossierAdapter

        val cabinets = mSpUtil.getString(Key.OrgCabinet, "")!!.split(",").toTypedArray()
        for (cabinet in cabinets) {
            val device = cabinet.subSequence(0, cabinet.indexOf("/", 0)).toString()
            val floor = cabinet.subSequence(cabinet.indexOf("_", 0) + 1, cabinet.length).toString()
            if (mCabinet.containsKey(device)) {
                mCabinet.getValue(device).add(floor.toInt())
            } else {
                val a = ArrayList<Int>()
                a.add(floor.toInt())
                mCabinet[device] = a
            }
        }
        val deviceList = DeviceService.getInstance().loadAll()
        val mIterator = deviceList.iterator()
        while (mIterator.hasNext()) {
            val next = mIterator.next()
            if (!mCabinet.containsKey(next.deviceName)) {
                mIterator.remove()
            }
        }
        if (deviceList.isEmpty()) {
            showToast("您无权限操作本柜体")
            finish()
            return
        }

        showSingleSelectDialog()

        UR880Entrance.getInstance().addOnCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().addOnInventoryListener(mInventoryListener)
    }

    private var mDeviceSingleSelectDialogBinding: DialogDeviceSingleSelectWarehousingBinding? = null
    private var mSingleSelectDialog: android.app.AlertDialog? = null
    private lateinit var mDialogDeviceAdapter: DeviceAdapter

    // 柜体单选弹窗
    private fun showSingleSelectDialog() {
        if (mSingleSelectDialog == null) {
            mDeviceSingleSelectDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.dialog_device_single_select_warehousing,
                null,
                false
            )

            mSingleSelectDialog = android.app.AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(mDeviceSingleSelectDialogBinding!!.root)
                .create()

            val window = mSingleSelectDialog!!.window
            window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        val deviceList = DeviceService.getInstance().loadAll()
        var selectPosition = 0

        mDialogDeviceAdapter = DeviceAdapter(this, deviceList)
        mDeviceSingleSelectDialogBinding!!.listView.adapter = mDialogDeviceAdapter
        mDeviceSingleSelectDialogBinding!!.listView.descendantFocusability =
            ViewGroup.FOCUS_BLOCK_DESCENDANTS
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
                mDevice = deviceList[selectPosition]
                mWarehousingBinding.accessingBoxNumberTv.text = "${mDevice.deviceName}"

                UR880Entrance.getInstance()
                    .send(UR880SendInfo.Builder().openDoor(mDevice.deviceId, 0).build())
                mSingleSelectDialog!!.dismiss()
            }
        }

        mSingleSelectDialog!!.show()
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

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//              warehousingSubmission()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> {
                finish()
            }
            R.id.btn_in_storage -> {
                warehousingSubmission()
            }
        }
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
        mProgressDialog.setMessage("正在进行档案入库，请稍后...")
        if (!mProgressDialog.isShowing) mProgressDialog.show()

        val jsonObject = JSONObject()
        try {
            val orderItemsJsonArray = JSONArray()
            for (dossierChanged in dossierList) {
                if (dossierChanged.isSelected) {
                    val changedObject = JSONObject()
                    changedObject.put("warrantNum", dossierChanged.warrantNum)
                    changedObject.put("rfidNum", dossierChanged.rfidNum)
                    changedObject.put("cabCode", mDevice.deviceName)
                    changedObject.put("inputDate", TimeUtil.nowTimeOfSeconds())
                    changedObject.put("position", dossierChanged.floor)
                    changedObject.put("light", dossierChanged.light)
                    orderItemsJsonArray.put(changedObject)
                }
            }
            if (orderItemsJsonArray.length() == 0) {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                finish()
                return
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
        dossierList[position].isSelected = !dossierList[position].isSelected
        mDossierAdapter.notifyDataSetChanged()
    }

}