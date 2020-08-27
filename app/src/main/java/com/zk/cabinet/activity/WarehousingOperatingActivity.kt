package com.zk.cabinet.activity

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
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.cabinet.R
import com.zk.cabinet.adapter.DeviceAdapter
import com.zk.cabinet.adapter.DossierAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.bean.DossierEntity
import com.zk.cabinet.bean.ResultGetInStorage
import com.zk.cabinet.databinding.ActivityWarehousingOperatingBinding
import com.zk.cabinet.databinding.DialogDeviceSingleSelectWarehousingBinding
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
    private val dossierList = ArrayList<DossierEntity>()
    private lateinit var mDossierAdapter: DossierAdapter
    private var mDoorIsOpen = false
    private lateinit var mCabinet: HashMap<String, ArrayList<Int>>
    private lateinit var inStorageList: ArrayList<ResultGetInStorage.NameValuePairsBeanX.DataBean.ValuesBean>

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

    private fun getListFromInStorageListByEpc(epc: String): ArrayList<ResultGetInStorage.NameValuePairsBeanX.DataBean.ValuesBean> {
        val newList = ArrayList<ResultGetInStorage.NameValuePairsBeanX.DataBean.ValuesBean>()
        for (entity in inStorageList) {
            if (entity.nameValuePairs.rfidNum == epc) {
                newList.add(entity)
            }
        }
        Log.e("zx", "zx: 入库-相同的epc待入库档案数据:" + newList.size)
        return newList
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            OPEN_DOOR_RESULT -> {
                Log.e("zx", "开门成功")
                showToast(msg.obj.toString())
            }
            START_INVENTORY -> {
                Log.e("zx", "开始盘点")
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

                // 识别到的标签数据,根据EPC,去待入库列表里面获取要入库的相同的EPC数据(一本档案可有多本数据)
                // todo 首先得过滤掉这个人不能操作的位置的入库文档
                // todo 两种情况. 1:待入库档案指明了位置或者拥有RFID标签号,也就相当于入库位置明确,需要亮对应位置的灯 2:待入库档案未指明位置,可根据柜子权限,亮灯让他自由入库
                // todo 有位置和没有位置因可确定亮灯位置,和不可确定亮灯位置,这个要分开入库 不然 无法操作
                // val dossierOperatingList = DossierOperatingService.getInstance().queryListByEPC(labelInfo.epc)
                val dossierOperatingList = getListFromInStorageListByEpc(labelInfo.epc)

                if (dossierOperatingList.size > 0) {
                    for (dossierOperating in dossierOperatingList) {
                        var isExit = false
                        for (dossier in dossierList) {
                            if (dossier.rfidNum == dossierOperating.nameValuePairs.rfidNum && dossier.warrantNum == dossierOperating.nameValuePairs.warrantNum) {
                                dossier.cabiCode = mDevice.deviceName
                                val light =
                                    if (labelInfo.antennaNumber % 24 == 0) 24 else labelInfo.antennaNumber % 24
                                val floor =
                                    if (labelInfo.antennaNumber % 24 == 0) labelInfo.antennaNumber / 24 else (labelInfo.antennaNumber / 24 + 1)
                                dossier.floor = floor
                                dossier.light = light
                                // dossier.isSelected = true
                                isExit = true
                                break
                            }
                        }
                        if (!isExit) {
                            val dossier = DossierEntity()
                            dossier.warrantNum = dossierOperating.nameValuePairs.warrantNum
                            dossier.rfidNum = dossierOperating.nameValuePairs.rfidNum
                            dossier.warrantName = dossierOperating.nameValuePairs.warrantName
                            dossier.warrantNo = dossierOperating.nameValuePairs.warrantNo
                            dossier.warranCate = dossierOperating.nameValuePairs.warranCate
                            dossier.inStorageType = dossierOperating.nameValuePairs.inStorageType
                            dossier.warranType = dossierOperating.nameValuePairs.warranType
                            dossier.cabiCode = mDevice.deviceName
                            val light =
                                if (labelInfo.antennaNumber % 24 == 0) 24 else labelInfo.antennaNumber % 24
                            val floor =
                                if (labelInfo.antennaNumber % 24 == 0) labelInfo.antennaNumber / 24 else (labelInfo.antennaNumber / 24 + 1)
                            dossier.floor = floor
                            dossier.light = light
                            // dossier.isSelected = true
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
                Log.e("zx", "停止盘点")
                Toast.makeText(this, "停止盘点", Toast.LENGTH_SHORT).show()
            }
            END_INVENTORY -> {
                Log.e("zx", "盘点结束")
                Toast.makeText(this, "盘点结束", Toast.LENGTH_SHORT).show()
            }
            SUBMITTED_SUCCESS -> {
                Toast.makeText(this, "数据提交成功", Toast.LENGTH_SHORT).show()
                mProgressDialog.dismiss()

                val mIterator = dossierList.iterator()
                while (mIterator.hasNext()) {
                    val next = mIterator.next()
                    if (next.isSelected) {
                        mIterator.remove()
                    }
                }

                if (dossierList.size > 0) {
                    mDossierAdapter.notifyDataSetChanged()
                } else {
                    finish()
                }
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

                        // 开门亮对应柜体权限层数的灯
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

        inStorageList =
            intent.getSerializableExtra("InStorageList") as ArrayList<ResultGetInStorage.NameValuePairsBeanX.DataBean.ValuesBean>

        mDossierAdapter = DossierAdapter(this, dossierList)
        mWarehousingBinding.warehousingOperatingLv.adapter = mDossierAdapter

        val gson = Gson()
        val canOperateCabinetList = mSpUtil.getString(Key.CanOperateCabinet, "")
        val deviceList = gson.fromJson<List<Device>>(
            canOperateCabinetList,
            object : TypeToken<List<Device?>?>() {}.type
        )

        val canOperateCabinetFloor = mSpUtil.getString(Key.CanOperateCabinetFloor, "")
        mCabinet = gson.fromJson<HashMap<String, ArrayList<Int>>>(
            canOperateCabinetFloor,
            object : TypeToken<HashMap<String, ArrayList<Int>?>?>() {}.type
        )

        showSingleSelectDialog(deviceList)

        UR880Entrance.getInstance().addOnCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().addOnInventoryListener(mInventoryListener)
    }

    private var mDeviceSingleSelectDialogBinding: DialogDeviceSingleSelectWarehousingBinding? = null
    private var mSingleSelectDialog: android.app.AlertDialog? = null
    private lateinit var mDialogDeviceAdapter: DeviceAdapter

    // 柜体单选弹窗
    private fun showSingleSelectDialog(deviceList: List<Device>) {
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
                    changedObject.put("position", dossierChanged.floor.toString())
                    changedObject.put("light", dossierChanged.light.toString())
                    changedObject.put("inOrg", dossierChanged.inOrg)
                    changedObject.put(
                        "operatorId",
                        mSpUtil.getString(SharedPreferencesUtil.Key.IdTemp, "")!!
                    )
                    changedObject.put("inputId", dossierChanged.inputId)

                    orderItemsJsonArray.put(changedObject)
                }
            }
            jsonObject.put("orderItems", orderItemsJsonArray)

            Log.e("zx-入库提交参数:", "$jsonObject")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, NetworkRequest.instance.mWarehousingSubmission,
            jsonObject, Response.Listener { response ->
                try {
                    Log.e("zx-入库提交结果:", "$response")

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

        if (dossierList[position].isSelected) {
            mWarehousingBinding.btnInStorage.background =
                resources.getDrawable(R.drawable.selector_menu_green)
            mWarehousingBinding.btnInStorage.isEnabled = true
        } else {
            var hasSelect = false
            for (dossierOperating in dossierList) {
                if (dossierOperating.isSelected) {
                    hasSelect = true
                    break
                } else {
                    hasSelect = false
                }
            }

            if (hasSelect) {
                mWarehousingBinding.btnInStorage.background =
                    resources.getDrawable(R.drawable.selector_menu_green)
                mWarehousingBinding.btnInStorage.isEnabled = true
            } else {
                mWarehousingBinding.btnInStorage.background =
                    resources.getDrawable(R.drawable.shape_btn_un_enable)
                mWarehousingBinding.btnInStorage.isEnabled = false
            }
        }

        mDossierAdapter.notifyDataSetChanged()
    }

}