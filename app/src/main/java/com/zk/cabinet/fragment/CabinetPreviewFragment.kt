package com.zk.cabinet.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.blankj.utilcode.util.LogUtils
import com.zk.cabinet.R
import com.zk.cabinet.adapter.ZNGInventoryAdapter
import com.zk.cabinet.adapter.ZNGInventoryDossierDetailsAdapter
import com.zk.cabinet.constant.SelfComm
import com.zk.cabinet.databinding.DialogDossierDetailsBinding
import com.zk.cabinet.databinding.FragmentCabinetPreviewBinding
import com.zk.cabinet.entity.ResultGetPosInfoByCabinetEquipmentId
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.rfid.bean.LabelInfo
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.callback.InventoryListener
import com.zk.rfid.ur880.UR880Entrance
import org.json.JSONException
import java.lang.ref.WeakReference

/**
 * 档案组架-预览界面
 * 档案组柜/档案单柜-预览界面(单个柜子的手动盘库界面,手动盘库相互不影响)
 */
class CabinetPreviewFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentCabinetPreviewBinding
    private lateinit var mHandler: MyHandler

    private lateinit var mDeviceID: String
    private lateinit var mDeviceName: String
    private lateinit var mSelectDeviceName: String
    private val labelInfoList = ArrayList<LabelInfo>()

    companion object {
        // 预览Activity传递过来的设备ID和设备名称
        private const val DEVICE_ID = "DeviceID"
        private const val DEVICE_NAME = "DeviceName"

        private const val START_INVENTORY = 0x01
        private const val INVENTORY_VALUE = 0x02
        private const val CANCEL_INVENTORY = 0x03
        private const val END_INVENTORY = 0x04

        @JvmStatic
        fun newInstance(deviceID: String, deviceName: String) =
            CabinetPreviewFragment().apply {
                arguments = Bundle().apply {
                    putString(DEVICE_ID, deviceID)
                    putString(DEVICE_NAME, deviceName)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mDeviceID = it.getString(DEVICE_ID, "")
            mDeviceName = it.getString(DEVICE_NAME, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_cabinet_preview,
            container,
            false
        )
        mBinding.onClickListener = this
        mHandler = MyHandler(this)

        // 初始化ProgressDialog
        mProgressDialog = ProgressDialog(requireContext(), R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)

        // 获取当前的设备类型
        mSelectDeviceName =
            SharedPreferencesUtil.instance.getString(SharedPreferencesUtil.Key.DeviceName, "")
                .toString()

        if (mSelectDeviceName == SelfComm.DEVICE_NAME[1]) {
            // 档案组架 隐藏盘点按钮
            mBinding.btnStartInventory.visibility = View.GONE
        }

        // 获取库位列表数据
        mProgressDialog.setMessage("正在获取『 $mDeviceName 』库位数据...")
        mProgressDialog.show()
        loadListHandler.postDelayed(loadListRunnable, 1000)

        return mBinding.root
    }

    private val loadListHandler = Handler()
    private val loadListRunnable = Runnable {
        run {
            getPosInfoByCabinetEquipmentId(mDeviceID)
        }
    }

    private class MyHandler(t: CabinetPreviewFragment) : Handler() {
        private val mainWeakReference = WeakReference(t)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mainWeakReference.get()!!.handleMessage(msg)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            START_INVENTORY -> {
                mProgressDialog.setMessage("『 $mDeviceName 』正在盘点第『 ${curFloor + 1} 』层...")
            }
            INVENTORY_VALUE -> {
                val labelInfo = msg.obj as LabelInfo
                Log.e("档案柜盘库", "labelInfo.deviceID: ${labelInfo.deviceID}")
                Log.e("档案柜盘库", "labelInfo.antennaNumber: ${labelInfo.antennaNumber}")
                Log.e("档案柜盘库", "labelInfo.fastID: ${labelInfo.fastID}")
                Log.e("档案柜盘库", "labelInfo.rssi: ${labelInfo.rssi}")
                Log.e("档案柜盘库", "labelInfo.operatingTime: ${labelInfo.operatingTime}")
                Log.e("档案柜盘库", "labelInfo.epcLength: ${labelInfo.epcLength}")
                Log.e("档案柜盘库", "labelInfo.epc: ${labelInfo.epc}")
                Log.e("档案柜盘库", "labelInfo.tid: ${labelInfo.tid}")
                Log.e("档案柜盘库", "labelInfo.inventoryNumber: ${labelInfo.inventoryNumber}")
                labelInfoList.add(labelInfo)
            }
            CANCEL_INVENTORY -> { // 停止盘点
            }
            END_INVENTORY -> { // 盘点结束
                // 某柜某层扫描结束后EPC编码集合与数据列表中数据EPC比对(这里是主动发起的盘点扫描)
                // 遍历扫描到的标签数据集合,可能会存在多个柜子的(经过测试一次只会上报一个柜子的数据)
                val deviceScanHashSet = HashSet<String>()
                for (labelInfo in labelInfoList) {
                    deviceScanHashSet.add(labelInfo.deviceID)
                }
                LogUtils.e(
                    "档案柜盘库-触发扫描的读写器设备IDS:",
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
                            if (entity.cabinetEquipmentId == mDeviceID
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
                                        labelInfo.antennaNumber = labelInfo.antennaNumber + 1
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
                                        "档案柜-预览-盘库-盘点到的标签数据-inStockList",
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
                // 移除因柜子掉线不在回调导致Dialog一直显示无法关闭从而去自动关闭盘库界面的runnable
                finishHandler.removeCallbacks(finishRunnable)

                curFloor++
                if (curFloor < 5) {
                    // 柜子一层盘点时间限制为60S,假如60S内没有结束一层盘点,说明读写器掉线了,需要关闭界面
                    finishHandler.postDelayed(finishRunnable, 1000 * 60)

                    SharedPreferencesUtil.instance.commitValue(
                        SharedPreferencesUtil.Record(
                            SharedPreferencesUtil.Key.InventoryDeviceId,
                            mDeviceID
                        )
                    )
                    UR880Entrance.getInstance().send(
                        UR880SendInfo.Builder().inventory(mDeviceID, 0, curFloor, 0).build()
                    )

                } else {
                    // 5层盘点结束
                    if (mProgressDialog.isShowing)
                        mProgressDialog.dismiss()
                }

                LogUtils.e("档案组-预览-盘库-盘点到的标签数据", JSON.toJSONString(labelInfoList))
                mAdapter.setIsInventory(true)
                mAdapter.notifyDataSetChanged()
                labelInfoList.clear()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // 点击暂无数据,重新加载
            R.id.ll_no_data -> {
                mBinding.llNoData.visibility = View.GONE
                mProgressDialog.setMessage("正在获取『 $mDeviceName 』库位数据...")
                mProgressDialog.show()
                loadListHandler.postDelayed(loadListRunnable, 1000)
            }

            // 开始盘库
            R.id.btn_start_inventory -> {
                mAdapter.setIsInventory(false)
                mAdapter.notifyDataSetChanged()

                startInventory()
            }

        }
    }

    // 当前正在盘点的柜层
    private var curFloor = 0

    // 是否开启过盘点
    private var isInventoryed = false

    /**
     * 开启盘点
     */
    private fun startInventory() {
        isInventoryed = true

        mProgressDialog.setMessage("准备开始盘点，请稍后...")
        mProgressDialog.show()
        // 柜子一层盘点时间限制为60S,假如60S内没有结束一层盘点,说明读写器掉线了,需要关闭界面
        finishHandler.postDelayed(finishRunnable, 1000 * 60)

        curFloor = 0
        // 开始盘点
        SharedPreferencesUtil.instance.commitValue(
            SharedPreferencesUtil.Record(
                SharedPreferencesUtil.Key.InventoryDeviceId,
                mDeviceID
            )
        )
        UR880Entrance.getInstance()
            .send(UR880SendInfo.Builder().inventory(mDeviceID, 0, curFloor, 0).build())
        // 停止盘点
        // UR880Entrance.getInstance().send(UR880SendInfo.Builder().cancel(mDevice.deviceId).build())
    }

    private val finishHandler = Handler()
    private val finishRunnable = Runnable {
        LogUtils.e("手动盘库-柜子掉线,不倒计时关闭界面,只做提醒就好了")
        showWarningToast("『 $mDeviceName 』已掉线,无法继续盘点")
        speek("${mDeviceName}已掉线,无法继续盘点")
        if (mProgressDialog.isShowing)
            mProgressDialog.dismiss()
    }

    private lateinit var mProgressDialog: ProgressDialog
    private fun sendDelayMessage(msgWhat: Int, msgObj: String) {
        val message = Message.obtain()
        message.what = msgWhat
        message.obj = msgObj
        mHandler.sendMessageDelayed(message, 1000)
    }

    private lateinit var mAdapter: ZNGInventoryAdapter
    private lateinit var mCabinetList: List<ResultGetPosInfoByCabinetEquipmentId.DataBean>
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
    private fun getPosInfoByCabinetEquipmentId(equipmentId: String) {
        val requestUrl =
            NetworkRequest.instance.mGetPosInfoByCabinetEquipmentId + "?equipmentId=" + equipmentId
        LogUtils.e("根据档案柜设备id获取库位信息-requestUrl:$requestUrl")

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, requestUrl, { response ->
                LogUtils.e("根据档案柜设备id获取库位信息-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val result: ResultGetPosInfoByCabinetEquipmentId = JSON.parseObject(
                                "$response", ResultGetPosInfoByCabinetEquipmentId::class.java
                            )
                            mCabinetList = result.data
                            if (mCabinetList.isNotEmpty()) {
                                mProgressDialog.dismiss()
                                mBinding.llNoData.visibility = View.GONE

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
                                mAdapter = ZNGInventoryAdapter(mCabinetList, activity!!, false)
                                val manager = GridLayoutManager(
                                    activity!!,
                                    24, // todo 这个数值是柜子每层可存的档案数,正式得改为15,应该弄成可以配置的
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
                                mAdapter.mOnItemClickListener = object :
                                    ZNGInventoryAdapter.OnItemClickListener {
                                    override fun onItemClick(position: Int) {
                                        // 点击展示库存档案数据详情
                                        val mArchivesList = mCabinetList[position].archivesList
                                        if (mArchivesList != null && mArchivesList.size > 0) {
                                            showDossierDetailsDialog(
                                                mCabinetList[position],
                                                mArchivesList
                                            )
                                        } else {
                                            showWarningToast("『 ${mCabinetList[position].rowNo}-${mCabinetList[position].numNo} 』号库位暂无档案存入")
                                            speek("${mCabinetList[position].rowNo}杠${mCabinetList[position].numNo}号库位暂无档案存入")
                                        }
                                    }
                                }

                                // 档案组柜2/档案单柜3 才进行读写器的初始化,添加读写器盘点监听
                                if (mSelectDeviceName == SelfComm.DEVICE_NAME[2] || mSelectDeviceName == SelfComm.DEVICE_NAME[3]) {
                                    // 盘点按钮可用
                                    mBinding.btnStartInventory.isEnabled = true
                                    mBinding.btnStartInventory.setBackgroundDrawable(
                                        activity!!.getDrawable(
                                            R.drawable.selector_menu_green_normal
                                        )
                                    )

                                    UR880Entrance.getInstance()
                                        .addOnInventoryListener(mInventoryListener)
                                }

                            } else {
                                // 这里要设置一张无数据的空图片
                                mProgressDialog.dismiss()
                                mBinding.tvNoDate.text = "暂无库位数据"
                                mBinding.llNoData.visibility = View.VISIBLE
                                showWarningToast("『 $mDeviceName 』暂未生成库位")
                                speek("${mDeviceName}暂未生成库位")
                            }
                        } else {
                            // 这里要设置一张无数据的空图片
                            mProgressDialog.dismiss()
                            mBinding.tvNoDate.text = "暂无库位数据"
                            mBinding.llNoData.visibility = View.VISIBLE
                            showErrorToast(response.getString("msg"))
                            speek("${mDeviceName}获取库位信息-请求失败")
                        }
                    } else {
                        // 这里要设置一张无数据的空图片
                        mProgressDialog.dismiss()
                        mBinding.tvNoDate.text = "暂无库位数据"
                        mBinding.llNoData.visibility = View.VISIBLE
                        showErrorToast("『 $mDeviceName 』获取库位信息-请求失败")
                        speek("${mDeviceName}获取库位信息-请求失败")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    // 这里要设置一张无数据的空图片
                    mProgressDialog.dismiss()
                    mBinding.tvNoDate.text = "暂无库位数据"
                    mBinding.llNoData.visibility = View.VISIBLE
                    showErrorToast("『 $mDeviceName 』获取库位信息-请求失败")
                    speek("${mDeviceName}获取库位信息-请求失败")
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
                // 这里要设置一张无数据的空图片
                mProgressDialog.dismiss()
                mBinding.tvNoDate.text = "暂无库位数据"
                mBinding.llNoData.visibility = View.VISIBLE
                showErrorToast(msg)
                speek("${mDeviceName}获取库位信息-请求失败")
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
    private lateinit var mDossierDetailsAdapter: ZNGInventoryDossierDetailsAdapter

    // 档案详情Dialog
    private fun showDossierDetailsDialog(
        entity: ResultGetPosInfoByCabinetEquipmentId.DataBean,
        mArchivesList: List<ResultGetPosInfoByCabinetEquipmentId.DataBean.ArchivesListBean>
    ) {
        if (mDossierDetailsDialog == null) {
            mDossierDetailsDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.dialog_dossier_details,
                null,
                false
            )

            mDossierDetailsDialog = AlertDialog.Builder(activity)
                .setCancelable(true)
                .setView(mDossierDetailsDialogBinding!!.root)
                .create()
        }
        mDossierDetailsDialog!!.show()

        // 盘点扫描后在库位中的档案集合(实在库档案)
        val isInStockedList =
            ArrayList<ResultGetPosInfoByCabinetEquipmentId.DataBean.ArchivesListBean>()
        // 盘点扫描后在不在库位中的档案集合(判定为异常档案)
        val isNoInStockedList =
            ArrayList<ResultGetPosInfoByCabinetEquipmentId.DataBean.ArchivesListBean>()
        for (archive in mArchivesList) {
            if (archive.isInStockStatus) {
                if (archive.isInStocked) {
                    isInStockedList.add(archive)
                } else {
                    isNoInStockedList.add(archive)
                }
            }
        }

        if (isInventoryed) {
            mDossierDetailsDialogBinding!!.tvTitle.text =
                "『 ${entity.rowNo}-${entity.numNo}号库位（已存：${mArchivesList.size}" +
                        "，应在库：${isInStockedList.size + isNoInStockedList.size}" +
                        "，实在库：${isInStockedList.size} " +
                        "，缺：${isNoInStockedList.size}）』"
            speek(
                "${entity.rowNo}杠${entity.numNo}号库位已存${mArchivesList.size}份档案" +
                        ",应在库${isInStockedList.size + isNoInStockedList.size}份档案" +
                        ",实在库${isInStockedList.size}份档案" +
                        ",缺${isNoInStockedList.size}份档案"
            )
        } else {
            mDossierDetailsDialogBinding!!.tvTitle.text =
                "『 ${entity.rowNo}-${entity.numNo}号库位（已存：${mArchivesList.size}" +
                        "，应在库：${isInStockedList.size + isNoInStockedList.size}）』"
            speek(
                "${entity.rowNo}杠${entity.numNo}号库位已存${mArchivesList.size}份档案" +
                        ",应在库${isInStockedList.size + isNoInStockedList.size}份档案"
            )
        }

        if (isInventoryed)
            mDossierDetailsAdapter =
                ZNGInventoryDossierDetailsAdapter(activity!!, mArchivesList, true)
        else
            mDossierDetailsAdapter =
                ZNGInventoryDossierDetailsAdapter(activity!!, mArchivesList, false)

        mDossierDetailsDialogBinding!!.listView.adapter = mDossierDetailsAdapter
        mDossierDetailsDialogBinding!!.listView.descendantFocusability =
            ViewGroup.FOCUS_BLOCK_DESCENDANTS

        mDossierDetailsDialogBinding!!.setOnClickListener { view: View? ->
            if (view!!.id == R.id.btn_cancel) {
                mDossierDetailsDialog!!.dismiss()
            }
        }

        val window = mDossierDetailsDialog!!.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        window!!.setLayout(
            resources.displayMetrics.widthPixels * 7 / 10,
            resources.displayMetrics.heightPixels * 7 / 10
        )

    }

    private val mInventoryListener = object : InventoryListener {
        override fun startInventory(p0: Int) {
            val inventoryDeviceId = SharedPreferencesUtil.instance.getString(
                SharedPreferencesUtil.Key.InventoryDeviceId,
                ""
            )
            if (inventoryDeviceId == mDeviceID) {
                LogUtils.e("mInventoryListener-startInventory:$p0")
                mHandler.sendEmptyMessage(START_INVENTORY)
            }
        }

        override fun inventoryValue(lebel: LabelInfo?) {
            val inventoryDeviceId = SharedPreferencesUtil.instance.getString(
                SharedPreferencesUtil.Key.InventoryDeviceId,
                ""
            )
            if (inventoryDeviceId == mDeviceID && lebel!!.deviceID.toString() == mDeviceID) {
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
            if (inventoryDeviceId == mDeviceID) {
                LogUtils.e("mInventoryListener-cancel:$p0")
                mHandler.sendEmptyMessage(CANCEL_INVENTORY)
            }
        }

        override fun endInventory(p0: Int) {
            val inventoryDeviceId = SharedPreferencesUtil.instance.getString(
                SharedPreferencesUtil.Key.InventoryDeviceId,
                ""
            )
            if (inventoryDeviceId == mDeviceID) {
                LogUtils.e("mInventoryListener-endInventory:$p0")
                mHandler.sendEmptyMessage(END_INVENTORY)
            }
        }

    }

    override fun onDestroy() {
        // 档案组柜2/档案单柜3 才进行读写器的初始化
        if (mSelectDeviceName == SelfComm.DEVICE_NAME[2] || mSelectDeviceName == SelfComm.DEVICE_NAME[3])
            UR880Entrance.getInstance().removeInventoryListener(mInventoryListener)
        super.onDestroy()
    }

}