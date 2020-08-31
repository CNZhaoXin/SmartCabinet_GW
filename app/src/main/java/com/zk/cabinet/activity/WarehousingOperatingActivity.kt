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
import com.alibaba.fastjson.JSON
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
            if (epc == entity.nameValuePairs.rfidNum.trim()) {
                newList.add(entity)
            }
        }
        Log.e("zx-入库操作-", "inStorageList:" + JSON.toJSONString(inStorageList))
        Log.e("zx-入库操作-", "相同的epc待入库档案数据:" + JSON.toJSONString(newList))
        return newList
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            GET_INFRARED_AND_LOCK -> {
                val data = msg.data
                val boxStateList = data.getIntegerArrayList("lock")
                val infraredStateList = data.getIntegerArrayList("infrared")

                Log.e("zx-入库操作-boxStateList", "$boxStateList")
                // 这里只要红外被触发都会被调用, 门开的状态 boxStateList: [1] , 门关闭的状态 boxStateList: []
                if (boxStateList!!.isEmpty()) {
                    isAutoFinish = true
                    timerStart()
                    Log.e("zx-入库操作-", "门关闭-开启界面倒计时")
                }
            }
            OPEN_DOOR_RESULT -> {
                showToast(msg.obj.toString())
                Log.e("zx-入库操作-", "门开启-关闭界面倒计时-有位置参数的档案进行开灯-")
                // 门开启后倒计时关闭
                isAutoFinish = false
                timerCancel()

                // 开门亮对应柜体位置的灯,该人要有该层的操作权限,才会亮灯
                // val floors = mCabinet[mDevice.deviceName]!!
                // for (floor in floors) { // 1..5
                for (floor in 1..5) { // 1..5
                    val lights = ArrayList<Int>()
                    for (entity in inStorageList) {
                        if (entity.nameValuePairs.position != null && entity.nameValuePairs.position.isNotEmpty() && entity.nameValuePairs.position.toInt() == floor) {
                            if (entity.nameValuePairs.light != null && entity.nameValuePairs.light.isNotEmpty())
                                lights.add(entity.nameValuePairs.light.toInt())
                        }
                    }
                    UR880Entrance.getInstance().send(
                        UR880SendInfo.Builder()
                            .turnOnLight(mDevice!!.deviceId, floor, lights).build()
                    )
                }

//                for (index in floors) {  // for (index in 1..5)
//                    val lights = ArrayList<Int>()
//                    for (light in 1..24) {
//                        lights.add(light)
//                    }
//                    UR880Entrance.getInstance().send(
//                        UR880SendInfo.Builder().turnOnLight(mDevice.deviceId, index, lights).build()
//                    )
//                }

            }
            START_INVENTORY -> {
                Log.e("zx", "开始盘点")
                Toast.makeText(this, "开始盘点", Toast.LENGTH_SHORT).show()
            }
            INVENTORY_VALUE -> {
                val labelInfo = msg.obj as LabelInfo
                Log.e("zx-入库-", "-------------labelInfo.deviceID: ${labelInfo.deviceID}")
                Log.e("zx-入库-", "-------------labelInfo.antennaNumber: ${labelInfo.antennaNumber}")
                Log.e("zx-入库-", "-------------labelInfo.fastID: ${labelInfo.fastID}")
                Log.e("zx-入库-", "-------------labelInfo.rssi: ${labelInfo.rssi}")
                Log.e("zx-入库-", "-------------labelInfo.operatingTime: ${labelInfo.operatingTime}")
                Log.e("zx-入库-", "-------------labelInfo.epcLength: ${labelInfo.epcLength}")
                Log.e("zx-入库-", "-------------labelInfo.epc: ${labelInfo.epc}")
                Log.e("zx-入库-", "-------------labelInfo.tid: ${labelInfo.tid}")
                Log.e(
                    "zx-入库-",
                    "-------------labelInfo.inventoryNumber: ${labelInfo.inventoryNumber}"
                )
                labelInfo.antennaNumber = labelInfo.antennaNumber + 1

                // 首先得过滤掉这个人不能操作的位置的入库文档 (假设请求入库列表的orgCode起到过滤作用,那么该人就一定只能获取到该组织的柜子权限,对应的入库档案肯定是能操作的位置)
                // 两种情况. 1:待入库档案指明了位置或者拥有RFID标签号,也就相当于入库位置明确,需要亮对应位置的灯 2:待入库档案未指明位置,不亮灯,可根据柜子权限 文字提示可入库的层 让他自由入库
                // val dossierOperatingList = DossierOperatingService.getInstance().queryListByEPC(labelInfo.epc)

                // 识别到的标签数据,根据EPC,去待入库列表里面获取要入库的相同的EPC数据(一本档案可有多本数据)
                val dossierOperatingList = getListFromInStorageListByEpc(labelInfo.epc)

                if (dossierOperatingList.size > 0) {
                    for (dossierOperating in dossierOperatingList) {
                        var isExit = false
                        for (dossier in dossierList) {
                            // 三个参数都相同,说明识别到的是同一个文件,不然就不是同一个文件,虽然rfidNum一样,但可以有多本档案,都需要添加到集合当中
                            if (dossier.rfidNum == dossierOperating.nameValuePairs.rfidNum && dossier.warrantNum == dossierOperating.nameValuePairs.warrantNum
                                && dossier.warrantNo == dossierOperating.nameValuePairs.warrantNo
                            ) {
                                dossier.cabiCode = mDevice.deviceName
                                dossier.inOrg = dossierOperating.nameValuePairs.inOrg
                                dossier.inputId = dossierOperating.nameValuePairs.inputId
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
                            dossier.inOrg = dossierOperating.nameValuePairs.inOrg
                            dossier.inputId = dossierOperating.nameValuePairs.inputId
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
                }

            }
            CANCEL_INVENTORY -> {
                Log.e("zx", "停止盘点")
                Toast.makeText(this, "停止盘点", Toast.LENGTH_SHORT).show()
            }
            END_INVENTORY -> {
                Log.e("zx", "盘点结束")
                Toast.makeText(this, "盘点结束", Toast.LENGTH_SHORT).show()

                // 这个是每次盘点完成之后都重新亮灯,亮操作过后的入库列表数据的灯
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
            SUBMITTED_SUCCESS -> {
                Toast.makeText(this, "数据提交成功", Toast.LENGTH_SHORT).show()
                mProgressDialog.dismiss()

                val mIterator = dossierList.iterator()
                while (mIterator.hasNext()) {
                    val next = mIterator.next()
                    if (next.isSelected) {
                        mIterator.remove()

                        val mIterator1 = inStorageList.iterator()
                        while (mIterator1.hasNext()) {
                            val next1 = mIterator1.next()
                            if (next.rfidNum == next1.nameValuePairs.rfidNum
                                && next.warrantNum == next1.nameValuePairs.warrantNum
                                && next.warrantNo == next1.nameValuePairs.warrantNo
                            ) {
                                mIterator1.remove()
                                break
                            }
                        }
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
        // 可以操作的柜子,对比配置和平台分配的
        val canOperateCabinetList = mSpUtil.getString(Key.CanOperateCabinet, "")
        val deviceList = gson.fromJson<List<Device>>(
            canOperateCabinetList,
            object : TypeToken<List<Device?>?>() {}.type
        )

        // 可以操作的柜子+层数 封装,对比配置和平台分配的
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
                val floors = mCabinet[mDevice.deviceName]!!
                // 显示可操作的柜层
                mWarehousingBinding.tvOperationFloors.text = "$floors"

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
                    // 操作人ID(登录人员ID)
                    changedObject.put(
                        "operatorId",
                        mSpUtil.getString(SharedPreferencesUtil.Key.IdTemp, "")!!
                    )
                    // 权证录入人ID
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