package com.zk.cabinet.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.blankj.utilcode.util.LogUtils
import com.zk.cabinet.R
import com.zk.cabinet.adapter.ZNGInventoryAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.databinding.ActivityZngAutoInventoryBinding
import com.zk.cabinet.entity.RequestCabinetSubmitInventoryResult
import com.zk.cabinet.entity.RequestRFID
import com.zk.cabinet.entity.ResultGetPosInfoByCabinetEquipmentId
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.rfid.bean.LabelInfo
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.callback.InventoryListener
import com.zk.rfid.ur880.UR880Entrance
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

// 柜体盘点时掉线,自动处理的时间(关闭界面/盘点下个柜体),目前30S
private var CABINET_DROPS_HANDEL_TIME = 1000 * 30L

/**
 * 档案组柜/档案单柜-自动盘库界面
 */
class ZNGAutoInventoryActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityZngAutoInventoryBinding
    private lateinit var mHandler: MyHandler
    private lateinit var mProgressDialog: ProgressDialog

    // 请求获取到的柜子的库位数据,包括库位中的档案数据
    private var mCabinetList = ArrayList<ResultGetPosInfoByCabinetEquipmentId.DataBean>()

    private var mAdapter: ZNGInventoryAdapter? = null

    // 需要盘库的柜体设备集合
    private var mDeviceList = ArrayList<Device>()

    // 盘库计划ID
    private lateinit var mPlanID: String

    // 盘库计划档案室编码
    private lateinit var mHouseCode: String

    // 当前正在盘库的柜体设备
    private lateinit var mDevice: Device

    // 当前正在盘点的柜层
    private var curFloor = 0

    // 盘点到的标签集合
    private val labelInfoList = ArrayList<LabelInfo>()

    // 自动盘库需要提交的epcListSet集合
    private val epcListSet = HashSet<RequestRFID>()

    companion object {
        private const val START_INVENTORY = 0x01
        private const val INVENTORY_VALUE = 0x02
        private const val CANCEL_INVENTORY = 0x03
        private const val END_INVENTORY = 0x04

        private const val SUBMIT_SUCCESS = 0x05
        private const val SUBMIT_ERROR = 0x06

        // 传递进来的盘点计划ID
        public const val PLAN_ID = "PlanID"

        // 传递进来的档案室编号
        public const val HOUSE_CODE = "HouseCode"

        // 传递进来盘点的柜子ID集合
        public const val EQUIPMENT_ID_LIST = "EquipmentIdList"

        fun newIntent(
            packageContext: Context,
            planId: String,
            houseCode: String,
            equipmentIdList: String
        ): Intent {
            val intent = Intent(packageContext, CabinetPreviewActivity::class.java)
            intent.putExtra(PLAN_ID, planId)
            intent.putExtra(HOUSE_CODE, houseCode)
            intent.putExtra(EQUIPMENT_ID_LIST, equipmentIdList)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_zng_auto_inventory)
        mBinding.onClickListener = this
        mHandler = MyHandler(this)

        // 初始化Dialog
        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)
        // 自动盘库-关闭自动倒计时
        isAutoFinish = false
        timerCancel()

        // 获取MQTT传递过来的盘库计划ID,档案室编号,需盘库的柜子设备集合
        mPlanID = intent.getStringExtra(PLAN_ID)
        mHouseCode = intent.getStringExtra(HOUSE_CODE)

        val deviceListJsonArray = JSONArray.parseArray(intent.getStringExtra(EQUIPMENT_ID_LIST))
        mDeviceList = deviceListJsonArray.toJavaList(Device::class.java) as ArrayList<Device>

        LogUtils.e(
            "档案柜-自动盘库",
            "mPlanID",
            mPlanID,
            "mHouseCode",
            mHouseCode,
            "equipmentIdList",
            JSON.toJSONString(mDeviceList)
        )

        // 当前需要盘库的柜体
        mDevice = mDeviceList.removeAt(0)
        // 显示当前盘库的档案柜名称 "柜体名称：${mDevice.deviceName}"
        mBinding.tvCabinetName.text = "『 ${mDevice.deviceName} 』"

        // 获取库位列表数据
        mProgressDialog.setMessage("正在获取『 ${mDevice.deviceName} 』库位数据...")
        mProgressDialog.show()
        loadListHandler.postDelayed(loadListRunnable, 1000)

        // 档案组柜2/档案单柜3 才进行读写器的初始化,添加读写器盘点监听,进入自动盘点界面已经做过判断
        UR880Entrance.getInstance().addOnInventoryListener(mInventoryListener)
    }

    private val loadListHandler = Handler()
    private val loadListRunnable = Runnable {
        run {
            if (mCabinetList != null) {
                mCabinetList.clear()
            }
            mAdapter?.notifyDataSetChanged()
            if (epcListSet != null) {
                epcListSet.clear()
            }
            getPosInfoByCabinetEquipmentId(mDevice)
        }
    }

    private var mZkList =
        ArrayList<ResultGetPosInfoByCabinetEquipmentId.DataBean.ArchivesListBean>()
    private var mJYSPZList =
        ArrayList<ResultGetPosInfoByCabinetEquipmentId.DataBean.ArchivesListBean>()
    private var mDJYList =
        ArrayList<ResultGetPosInfoByCabinetEquipmentId.DataBean.ArchivesListBean>()
    private var mDGHList =
        ArrayList<ResultGetPosInfoByCabinetEquipmentId.DataBean.ArchivesListBean>()
    private var mYcList =
        ArrayList<ResultGetPosInfoByCabinetEquipmentId.DataBean.ArchivesListBean>()

    /**
     * 25.根据档案柜设备id获取库位信息 (免登录)
     * 接口地址get /api/pad/getPosInfoByCabinetEquipmentId
     * equipmentId 档案柜设备id
     */
    private fun getPosInfoByCabinetEquipmentId(device: Device) {
        val requestUrl =
            NetworkRequest.instance.mGetPosInfoByCabinetEquipmentId + "?equipmentId=" + device.deviceId
        LogUtils.e("根据档案柜设备id获取库位信息-requestUrl:$requestUrl")

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, requestUrl, { response ->
                LogUtils.e("根据档案柜设备id获取库位信息-返回结果:", "$response")
                println("根据档案柜设备id获取库位信息-返回结果:$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val result: ResultGetPosInfoByCabinetEquipmentId = JSON.parseObject(
                                "$response", ResultGetPosInfoByCabinetEquipmentId::class.java
                            )
                            mCabinetList =
                                result.data as ArrayList<ResultGetPosInfoByCabinetEquipmentId.DataBean>

                            if (mCabinetList.isNotEmpty()) {
                                mProgressDialog.dismiss()

                                // 根据库位数据,计算总数据
                                for (entity in mCabinetList) {
                                    val archivesList = entity.archivesList
                                    if (archivesList != null && archivesList.size > 0) {
                                        for (archive in archivesList) {
                                            val archivesStatus = archive.archivesStatus
                                            if (archivesStatus == 10) { // 在库
                                                archive.isInStockStatus = true
                                                mZkList.add(archive)
                                            } else if (archivesStatus == 50) { // 借阅审批中
                                                archive.isInStockStatus = true
                                                mJYSPZList.add(archive)
                                            } else if (archivesStatus == 100) { // 待借阅
                                                archive.isInStockStatus = true
                                                mDJYList.add(archive)
                                            } else if (archivesStatus == 200) { // 待归还
                                                archive.isInStockStatus = false
                                                mDGHList.add(archive)
                                            } else if (archivesStatus == 9000) { // 异常
                                                archive.isInStockStatus = false
                                                mYcList.add(archive)
                                            } else {
                                                archive.isInStockStatus = false
                                            }
                                        }
                                    }
                                }
                                // 显示总数据
                                mBinding.tvZk.text = "${mZkList.size}"
                                mBinding.tvJyspz.text = "${mJYSPZList.size}"
                                mBinding.tvDjy.text = "${mDJYList.size}"
                                mBinding.tvDgh.text = "${mDGHList.size}"
                                mBinding.tvYc.text = "${mYcList.size}"

                                // 根据库位数据动态创建View
                                mAdapter = ZNGInventoryAdapter(mCabinetList, this, false)
                                val manager = GridLayoutManager(
                                    this,
                                    15, // todo 这个数值是柜子每层可存的档案数,正式得改为15,应该弄成可以配置的
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                manager.spanSizeLookup =
                                    object : GridLayoutManager.SpanSizeLookup() {
                                        override fun getSpanSize(position: Int): Int {
                                            return mCabinetList[position].lampList.size
                                        }
                                    }
                                mBinding.recyclerView.layoutManager = manager
                                mBinding.recyclerView.itemAnimator = DefaultItemAnimator()
                                mBinding.recyclerView.adapter = mAdapter

                                // 开启自动盘点
                                startInventory()

                            } else {
                                mProgressDialog.dismiss()

                                // 如果有下个柜子需要继续盘点下个柜子,如果没有直接退出自动盘点界面
                                if (mDeviceList.size > 0) {
                                    showWarningToast("『 ${device.deviceName} 』暂未生成库位")
                                    // speek("${device.deviceName}暂未生成库位")

                                    mDevice = mDeviceList.removeAt(0)
                                    // "柜体名称：${mDevice.deviceName}
                                    mBinding.tvCabinetName.text = "『 ${mDevice.deviceName} 』"

                                    // 获取库位列表数据
                                    mProgressDialog.setMessage("正在获取『 ${mDevice.deviceName} 』库位数据...")
                                    mProgressDialog.show()
                                    loadListHandler.postDelayed(loadListRunnable, 1000)
                                } else {
                                    showWarningToast("『 ${device.deviceName} 』暂未生成库位,盘点结束")
                                    finish()
                                }

                            }
                        } else {
                            mProgressDialog.dismiss()

                            // 如果有下个柜子需要继续盘点下个柜子,如果没有直接退出自动盘点界面
                            if (mDeviceList.size > 0) {
                                // speek("${device.deviceName}获取库位信息-请求失败")
                                showErrorToast(response.getString("msg"))

                                mDevice = mDeviceList.removeAt(0)
                                // "柜体名称：${mDevice.deviceName}
                                mBinding.tvCabinetName.text = "『 ${mDevice.deviceName} 』"

                                // 获取库位列表数据
                                mProgressDialog.setMessage("正在获取『 ${mDevice.deviceName} 』库位数据...")
                                mProgressDialog.show()
                                loadListHandler.postDelayed(loadListRunnable, 1000)
                            } else {
                                showErrorToast(response.getString("msg"))
                                finish()
                            }
                        }
                    } else {
                        mProgressDialog.dismiss()
                        // 如果有下个柜子需要继续盘点下个柜子,如果没有直接退出自动盘点界面
                        if (mDeviceList.size > 0) {
                            // speek("${device.deviceName}获取库位信息-请求失败")
                            showErrorToast("『 ${device.deviceName} 』获取库位信息-请求失败")

                            mDevice = mDeviceList.removeAt(0)
                            // "柜体名称：${mDevice.deviceName}
                            mBinding.tvCabinetName.text = "『 ${mDevice.deviceName} 』"

                            // 获取库位列表数据
                            mProgressDialog.setMessage("正在获取『 ${mDevice.deviceName} 』库位数据...")
                            mProgressDialog.show()
                            loadListHandler.postDelayed(loadListRunnable, 1000)
                        } else {
                            showErrorToast(" ${device.deviceName} 』获取库位信息-请求失败")
                            finish()
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    mProgressDialog.dismiss()
                    // 如果有下个柜子需要继续盘点下个柜子,如果没有直接退出自动盘点界面
                    if (mDeviceList.size > 0) {
                        // speek("${device.deviceName}获取库位信息-请求失败")
                        showErrorToast(" ${device.deviceName} 』获取库位信息-请求失败")

                        mDevice = mDeviceList.removeAt(0)
                        // "柜体名称：${mDevice.deviceName}
                        mBinding.tvCabinetName.text = "『 ${mDevice.deviceName} 』"

                        // 获取库位列表数据
                        mProgressDialog.setMessage("正在获取『 ${mDevice.deviceName} 』库位数据...")
                        mProgressDialog.show()
                        loadListHandler.postDelayed(loadListRunnable, 1000)
                    } else {
                        showErrorToast(" ${device.deviceName} 』获取库位信息-请求失败")
                        finish()
                    }
                }
            },
            { error ->
                val msg =
                    if (error != null)
                        if (error.networkResponse != null)
                            "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                        else
                            "errorCode: -1 VolleyError: $error"
                    else {
                        "errorCode: -1 VolleyError: 未知"
                    }
                mProgressDialog.dismiss()
                // 如果有下个柜子需要继续盘点下个柜子,如果没有直接退出自动盘点界面
                if (mDeviceList.size > 0) {
                    speek("${device.deviceName}获取库位信息-请求失败")
                    showErrorToast(msg)

                    mDevice = mDeviceList.removeAt(0)
                    // "柜体名称：${mDevice.deviceName}
                    mBinding.tvCabinetName.text = "『 ${mDevice.deviceName} 』"

                    // 获取库位列表数据
                    mProgressDialog.setMessage("正在获取『 ${mDevice.deviceName} 』库位数据...")
                    mProgressDialog.show()
                    loadListHandler.postDelayed(loadListRunnable, 1000)
                } else {
                    showErrorToast(msg)
                    finish()
                }
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    /**
     * 开启盘点
     */
    private fun startInventory() {
        mProgressDialog.setMessage("准备开始盘点，请稍后...")
        mProgressDialog.show()
        // 柜子一层盘点时间限制为CABINET_DROPS_HANDEL_TIME秒,假如CABINET_DROPS_HANDEL_TIME秒内没有结束一层盘点,说明读写器掉线了,需要关闭界面
        finishHandler.postDelayed(finishRunnable, CABINET_DROPS_HANDEL_TIME)

        curFloor = 0
        // 开始盘点
        SharedPreferencesUtil.instance.commitValue(
            SharedPreferencesUtil.Record(
                SharedPreferencesUtil.Key.InventoryDeviceId,
                mDevice.deviceId
            )
        )
        UR880Entrance.getInstance()
            .send(UR880SendInfo.Builder().inventory(mDevice.deviceId, 0, curFloor, 0).build())
        // 停止盘点
        // UR880Entrance.getInstance().send(UR880SendInfo.Builder().cancel(mDevice.deviceId).build())
    }

    private val finishHandler = Handler()
    private val finishRunnable = Runnable {
        LogUtils.e("档案柜-自动盘点-柜子掉线,需倒计时关闭界面")
        if (mProgressDialog.isShowing)
            mProgressDialog.dismiss()

        // 自动盘点时,柜子掉线处理,那么在进入自动盘点前柜子就不要过滤成在线柜子了,这个界面进行处理
        if (mDeviceList.size > 0) {
            showErrorToast("『 ${mDevice.deviceName} 』已掉线,无法继续盘点")
            // speek("${mDevice.deviceName}已掉线,无法继续盘点")

            mDevice = mDeviceList.removeAt(0)
            // "柜体名称：${mDevice.deviceName}
            mBinding.tvCabinetName.text = "『 ${mDevice.deviceName} 』"

            // 获取库位列表数据
            mProgressDialog.setMessage("正在获取『 ${mDevice.deviceName} 』库位数据...")
            mProgressDialog.show()
            loadListHandler.postDelayed(loadListRunnable, 1000)
        } else {
            showErrorToast("『 ${mDevice.deviceName} 』已掉线,无法继续盘点")
            finish()
        }
    }

    private class MyHandler(t: ZNGAutoInventoryActivity) : Handler() {
        private val mainWeakReference = WeakReference(t)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mainWeakReference.get()!!.handleMessage(msg)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            START_INVENTORY -> {
                mProgressDialog.setMessage("『 ${mDevice.deviceName} 』正在盘点第『 ${curFloor + 1} 』层...")
            }
            INVENTORY_VALUE -> {
                val labelInfo = msg.obj as LabelInfo
                Log.e("档案柜-自动盘库", "labelInfo.deviceID: ${labelInfo.deviceID}")
                Log.e("档案柜-自动盘库", "labelInfo.antennaNumber: ${labelInfo.antennaNumber}")
                Log.e("档案柜-自动盘库", "labelInfo.fastID: ${labelInfo.fastID}")
                Log.e("档案柜-自动盘库", "labelInfo.rssi: ${labelInfo.rssi}")
                Log.e("档案柜-自动盘库", "labelInfo.operatingTime: ${labelInfo.operatingTime}")
                Log.e("档案柜-自动盘库", "labelInfo.epcLength: ${labelInfo.epcLength}")
                Log.e("档案柜-自动盘库", "labelInfo.epc: ${labelInfo.epc}")
                Log.e("档案柜-自动盘库", "labelInfo.tid: ${labelInfo.tid}")
                Log.e("档案柜-自动盘库", "labelInfo.inventoryNumber: ${labelInfo.inventoryNumber}")
                labelInfoList.add(labelInfo)
            }
            CANCEL_INVENTORY -> { // 停止盘点
            }
            END_INVENTORY -> { // 盘点结束
                // 某柜某层扫描结束后EPC编码集合与数据列表中数据EPC比对(这里是主动发起的盘点扫描)
                LogUtils.e("档案柜-自动盘库-盘点到的标签数据", JSON.toJSONString(labelInfoList))

                // 遍历扫描到的标签数据集合,可能会存在多个柜子的(经过测试一次只会上报一个柜子的数据)
                val deviceScanHashSet = HashSet<String>()
                for (labelInfo in labelInfoList) {
                    // 天线号是从0开始的,且需要根据天线号进行计算层数和灯位, 库位是从1开始的
                    labelInfo.antennaNumber = labelInfo.antennaNumber + 1

                    deviceScanHashSet.add(labelInfo.deviceID)
                }
                LogUtils.e(
                    "档案柜-自动盘库-触发扫描的读写器设备IDS:",
                    JSON.toJSONString(deviceScanHashSet)
                )

                // 针对被识别的某柜某层的标签数据的处理 1.是否被扫描到 2.是否是同一层 3.是否是同一个位置
                // 在正确库位的库存档案
                val inStockList =
                    ArrayList<ResultGetPosInfoByCabinetEquipmentId.DataBean.ArchivesListBean>()
                // 不在柜/或在柜不在正确库位的库存档案
                val noInStockList =
                    ArrayList<ResultGetPosInfoByCabinetEquipmentId.DataBean.ArchivesListBean>()
                // 扫描到的多的档案(哪个库位扫描到的,要添加到哪个库位中去)
                val moreList =
                    ArrayList<ResultGetPosInfoByCabinetEquipmentId.DataBean.ArchivesListBean>()

                // 过滤:是否是当前触发的层的本柜的库存档案,每层的数据必须要单独处理
                for (stock in mCabinetList) {
                    val archivesList = stock.archivesList
                    if (archivesList != null && archivesList.size > 0) {
                        for (entity in archivesList) {
                            if (entity.cabinetEquipmentId == mDevice.deviceId
                                && deviceScanHashSet.contains(entity.cabinetEquipmentId)
                                && entity.rowNo == curFloor + 1
                            ) {
                                // 标记该档案EPC是否被扫描到了,扫描到说明库存档案是在触发层的
                                var isScan = false
                                // 标记该档案是否在正确位置
                                var isCorrectPosition = false

                                for (labelInfo in labelInfoList) {
                                    if (labelInfo.epc == entity.rfid) { // 1.先判断库存档案RFID有没有被扫描到
                                        // 扫描到了,在柜中
                                        isScan = true

                                        // 天线号是从0开始的,且需要根据天线号进行计算层数和灯位
                                        val floor =
                                            if (labelInfo.antennaNumber % 24 == 0) labelInfo.antennaNumber / 24 else (labelInfo.antennaNumber / 24 + 1)
                                        val light =
                                            if (labelInfo.antennaNumber % 24 == 0) 24 else labelInfo.antennaNumber % 24
                                        LogUtils.e(
                                            "labelInfo.epc-" + labelInfo.epc,
                                            "labelInfo.antennaNumber:" + labelInfo.antennaNumber + ",floor:" + floor + ",light:" + light
                                        )

                                        if (floor == entity.rowNo) { // 2.再判断扫描到档案RFID的天线计算出的,层数是否和放入的档案层数一致
                                            for (lampList in entity.lampList) {  // 3.再判断扫描到档案RFID的天线计算出的,灯位是否和放入的档案灯位一致
                                                if (light == lampList) {
                                                    isCorrectPosition = true
                                                    break
                                                }
                                            }
                                        }

                                        break
                                    }
                                }

                                if (isScan && isCorrectPosition) { // 库存档案被扫描到了,且库位正确,那就是在这个库位
                                    entity.isInStocked = true
                                    inStockList.add(entity)
                                    LogUtils.e(
                                        "档案柜-自动盘库-盘点到的标签数据",
                                        JSON.toJSONString(inStockList)
                                    )
                                } else { // 库存档案没有被扫描到,或者说扫描到了,但是通过扫描到的天线计算的灯位(库位)不正确. 都算不在这个库位
                                    entity.isInStocked = false
                                    noInStockList.add(entity)
                                }
                            }
                        }
                    }
                }
                // 自动盘库的需要提交的数据处理
                val inventoryDeviceId = SharedPreferencesUtil.instance.getString(
                    SharedPreferencesUtil.Key.InventoryDeviceId,
                    ""
                )

                for (label in labelInfoList) {
                    if (inventoryDeviceId == mDevice.deviceId && label.deviceID.toString() == mDevice.deviceId) {
                        // 计算扫描的EPC的库位,然后查询到这个库位编号是什么,再封装到自动盘库提交需要的EpcList集合中
                        // 天线号是从0开始的,且需要根据天线号进行计算层数和灯位
                        val floor =
                            if (label.antennaNumber % 24 == 0) label.antennaNumber / 24 else (label.antennaNumber / 24 + 1)
                        val light =
                            if (label.antennaNumber % 24 == 0) 24 else label.antennaNumber % 24

                        for (entity in mCabinetList) {
                            if (entity.rowNo == floor && entity.lampList.contains(light)) {
                                val requestRFID = RequestRFID(label.epc, entity.rfid)
                                epcListSet.add(requestRFID)
                                break
                            }
                        }
                    }
                }

                // 移除因柜子掉线不在回调导致Dialog一直显示无法关闭从而去自动关闭盘库界面的runnable
                finishHandler.removeCallbacks(finishRunnable)

                curFloor++
                if (curFloor < 5) {
                    // 柜子一层盘点时间限制为CABINET_DROPS_HANDEL_TIME秒,假如CABINET_DROPS_HANDEL_TIME秒内没有结束一层盘点,说明读写器掉线了,需要关闭界面
                    finishHandler.postDelayed(finishRunnable, CABINET_DROPS_HANDEL_TIME)

                    SharedPreferencesUtil.instance.commitValue(
                        SharedPreferencesUtil.Record(
                            SharedPreferencesUtil.Key.InventoryDeviceId,
                            mDevice.deviceId
                        )
                    )

                    mAdapter!!.setIsInventory(true)
                    mAdapter!!.notifyDataSetChanged()
                    labelInfoList.clear()

                    UR880Entrance.getInstance().send(
                        UR880SendInfo.Builder().inventory(mDevice.deviceId, 0, curFloor, 0).build()
                    )

                } else {
                    // 5层盘点结束
                    if (mProgressDialog.isShowing)
                        mProgressDialog.dismiss()
                    mAdapter!!.setIsInventory(true)
                    mAdapter!!.notifyDataSetChanged()
                    labelInfoList.clear()

                    // 自动盘库-需要提交盘库结果
                    val epcList: List<RequestRFID> = ArrayList(epcListSet)
                    submitInventoryResult(epcList)
                }
            }
            SUBMIT_SUCCESS -> { // 自动盘库数据提交成功
                mProgressDialog.dismiss()
                if (mDeviceList.size > 0) {
                    showSuccessToast("${msg.obj}")
                    // speek("${mDevice.deviceName}自动盘库数据提交成功")

                    mDevice = mDeviceList.removeAt(0)
                    // "柜体名称：${mDevice.deviceName}
                    mBinding.tvCabinetName.text = "『 ${mDevice.deviceName} 』"

                    // 获取库位列表数据
                    mProgressDialog.setMessage("正在获取『 ${mDevice.deviceName} 』库位数据...")
                    mProgressDialog.show()
                    loadListHandler.postDelayed(loadListRunnable, 1000)
                } else {
                    showSuccessToast("${msg.obj}")
                    finish()
                }
            }
            SUBMIT_ERROR -> { // 自动盘库数据提交失败
                mProgressDialog.dismiss()

                if (mDeviceList.size > 0) {
                    showErrorToast("${msg.obj}")
                    // speek("${mDevice.deviceName}自动盘库提交失败")

                    mDevice = mDeviceList.removeAt(0)
                    // "柜体名称：${mDevice.deviceName}
                    mBinding.tvCabinetName.text = "『 ${mDevice.deviceName} 』"

                    // 获取库位列表数据
                    mProgressDialog.setMessage("正在获取『 ${mDevice.deviceName} 』库位数据...")
                    mProgressDialog.show()
                    loadListHandler.postDelayed(loadListRunnable, 1000)
                } else {
                    showErrorToast("${msg.obj}")
                    finish()
                }
            }
        }
    }

    /**
     * 9.推送盘库报告（智能柜）
     * 接口地址：Post /api/pad/submitIntInventoryResult
     * 批量提交，如果有一份档案提交失败，所有数据回滚；
     * 入参（所有字段必填）：
     *  planId string 盘库计划id
     * houseCode string 库房编号
     * cabinetEquipmentId 档案柜设备id
     * inventoryRfids List<object> 档案rfid集合
     */
    private fun submitInventoryResult(epcList: List<RequestRFID>) {
        mProgressDialog.setMessage("正在提交盘库数据...")
        mProgressDialog.show()

        val requestUrl = NetworkRequest.instance.mSubmitIntInventoryResult
        LogUtils.e("档案柜-自动盘库提交-请求URL:$requestUrl")

        val requestCabinetSubmitInventoryResult = RequestCabinetSubmitInventoryResult()
        requestCabinetSubmitInventoryResult.planId = mPlanID
        requestCabinetSubmitInventoryResult.houseCode = mHouseCode
        requestCabinetSubmitInventoryResult.cabinetEquipmentId = mDevice.deviceId
        requestCabinetSubmitInventoryResult.inventoryRfids = epcList

        val jsonObject = JSONObject(JSON.toJSONString(requestCabinetSubmitInventoryResult))
        LogUtils.e("档案柜-自动盘库提交-请求参数", JSON.toJSONString(requestCabinetSubmitInventoryResult))
        println("档案柜-自动盘库提交-请求参数:" + JSON.toJSONString(requestCabinetSubmitInventoryResult))

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, requestUrl, jsonObject,
            { response ->
                LogUtils.e("【${mDevice.deviceName}】自动盘库提交-请求结果:", "$response")
                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            sendDelayMessage(SUBMIT_SUCCESS, "『 ${mDevice.deviceName} 』自动盘库数据提交成功")
                        } else {
                            sendDelayMessage(SUBMIT_ERROR, response.getString("msg"))
                        }
                    } else {
                        sendDelayMessage(SUBMIT_ERROR, "『 ${mDevice.deviceName} 』自动盘库数据提交失败")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    sendDelayMessage(SUBMIT_ERROR, "『 ${mDevice.deviceName} 』自动盘库数据提交失败")
                }
            },
            { error ->
                val msg = if (error != null)
                    if (error.networkResponse != null)
                        "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                    else
                        "errorCode: -1 VolleyError: $error"
                else {
                    "errorCode: -1 VolleyError: 未知"
                }
                sendDelayMessage(SUBMIT_ERROR, msg)
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }

    private fun sendDelayMessage(msgWhat: Int, msgObj: String) {
        val message = Message.obtain()
        message.what = msgWhat
        message.obj = msgObj
        mHandler.sendMessageDelayed(message, 1000)
    }

    private val mInventoryListener = object : InventoryListener {
        override fun startInventory(p0: Int) {
            val inventoryDeviceId = SharedPreferencesUtil.instance.getString(
                SharedPreferencesUtil.Key.InventoryDeviceId,
                ""
            )
            if (inventoryDeviceId == mDevice.deviceId) {
                LogUtils.e("mInventoryListener-startInventory:$p0")
                mHandler.sendEmptyMessage(START_INVENTORY)
            }
        }

        override fun inventoryValue(lebel: LabelInfo?) {
            val inventoryDeviceId = SharedPreferencesUtil.instance.getString(
                SharedPreferencesUtil.Key.InventoryDeviceId,
                ""
            )
            if (inventoryDeviceId == mDevice.deviceId && lebel!!.deviceID.toString() == mDevice.deviceId) {
                val msg = Message.obtain()
                msg.what = INVENTORY_VALUE
                msg.obj = lebel
                mHandler.sendMessage(msg)
            }
        }

        override fun cancel(p0: Int, p1: Int) {
            val inventoryDeviceId = SharedPreferencesUtil.instance.getString(
                SharedPreferencesUtil.Key.InventoryDeviceId,
                ""
            )
            if (inventoryDeviceId == mDevice.deviceId) {
                LogUtils.e("mInventoryListener-cancel:$p0")
                mHandler.sendEmptyMessage(CANCEL_INVENTORY)
            }
        }

        override fun endInventory(p0: Int) {
            val inventoryDeviceId = SharedPreferencesUtil.instance.getString(
                SharedPreferencesUtil.Key.InventoryDeviceId,
                ""
            )
            if (inventoryDeviceId == mDevice.deviceId) {
                LogUtils.e("mInventoryListener-endInventory:$p0")
                mHandler.sendEmptyMessage(END_INVENTORY)
            }
        }

    }

    override fun onDestroy() {
        // 档案组柜2/档案单柜3 进入自动盘点界面已经判断过
        UR880Entrance.getInstance().removeInventoryListener(mInventoryListener)
        super.onDestroy()
    }

}