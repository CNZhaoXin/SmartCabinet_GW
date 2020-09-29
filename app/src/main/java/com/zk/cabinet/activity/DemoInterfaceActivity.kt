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
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.zk.cabinet.R
import com.zk.cabinet.adapter.DemoInterfaceAdapter
import com.zk.cabinet.adapter.DialogDossierDetailsAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Cabinet
import com.zk.cabinet.bean.Device
import com.zk.cabinet.bean.DossierOperating
import com.zk.cabinet.databinding.ActivityDemoInterfaceBinding
import com.zk.cabinet.databinding.DialogDossierDetailsBinding
import com.zk.cabinet.db.CabinetService
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.db.DossierOperatingService
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.rfid.bean.LabelInfo
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.callback.InventoryListener
import com.zk.rfid.ur880.UR880Entrance
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
    private var inventoryId = "-1"
    private var inOrg = "-1"
    private var isAutomatic by Delegates.notNull<Boolean>()
    private var mInventoryIdList = ArrayList<String>()
    private val mDeviceList = ArrayList<Device>()
    private var mInOrgList = ArrayList<String>()

    companion object {
        private const val START_INVENTORY = 0x01
        private const val INVENTORY_VALUE = 0x02
        private const val CANCEL_INVENTORY = 0x03
        private const val END_INVENTORY = 0x04

        private const val GET_LIST_SUCCESS = 0x07
        private const val GET_LIST_FAIL = 0x08
        private const val GET_LIST_NO_DATA = 0x09

        private const val AUTOMATIC = "isAutomatic"
        private const val CAB_CODE_LIST = "cabCodeList"
        private const val INVENTORY_ID = "inventoryId"
        private const val IN_ORG = "inOrg"
        fun newIntent(
            packageContext: Context,
            isAutomatic: Boolean,
            cabCodeList: ArrayList<String>? = null,
            inventoryId: ArrayList<String>? = null,
            inOrg: ArrayList<String>? = null
        ): Intent {
            val intent = Intent(packageContext, DemoInterfaceActivity::class.java)
            intent.putExtra(AUTOMATIC, isAutomatic)
            intent.putExtra(CAB_CODE_LIST, cabCodeList)
            intent.putExtra(INVENTORY_ID, inventoryId)
            intent.putExtra(IN_ORG, inOrg)
            return intent
        }
    }

    private val finishHandler = Handler()
    private val finishRunnable = Runnable {
        Log.e("zx-盘点倒计时关闭界面-", "----------------------------------------------")
        if (mProgressDialog.isShowing) mProgressDialog.dismiss()
        finish()
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            START_INVENTORY -> {
                mProgressDialog.setMessage("正在盘点第『 ${floor + 1} 』层...")
            }
            INVENTORY_VALUE -> {
                val labelInfo = msg.obj as LabelInfo

                // todo 属于柜子的读写器设备上报的数据才处理,当通道门识别到了数据,盘点界面不处理
                val deviceList = DeviceService.getInstance().loadAll()
                if (deviceList.size > 0 && deviceList[0].deviceId == labelInfo.deviceID) {
                    Log.e("zx-盘点界面", "-------------labelInfo.deviceID: ${labelInfo.deviceID}")
                    Log.e(
                        "zx-盘点界面",
                        "-------------labelInfo.antennaNumber: ${labelInfo.antennaNumber}"
                    )
                    Log.e("zx-盘点界面", "-------------labelInfo.fastID: ${labelInfo.fastID}")
                    Log.e("zx-盘点界面", "-------------labelInfo.rssi: ${labelInfo.rssi}")
                    Log.e(
                        "zx-盘点界面",
                        "-------------labelInfo.operatingTime: ${labelInfo.operatingTime}"
                    )
                    Log.e("zx-盘点界面", "-------------labelInfo.epcLength: ${labelInfo.epcLength}")
                    Log.e("zx-盘点界面", "-------------labelInfo.epc: ${labelInfo.epc}")
                    Log.e("zx-盘点界面", "-------------labelInfo.tid: ${labelInfo.tid}")
                    Log.e(
                        "zx-盘点界面",
                        "-------------labelInfo.inventoryNumber: ${labelInfo.inventoryNumber}"
                    )
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
            }
            CANCEL_INVENTORY -> {
                Log.e("zx-盘点-", "取消盘点")
            }
            END_INVENTORY -> {
                finishHandler.removeCallbacks(finishRunnable)

                floor++
                Log.e("zx-盘点-", "停止盘点----floor=$floor")
                if (floor < 5) {
                    // 柜子一层盘点时间限制为10S,假如10S内没有结束一层盘点,说明读写器掉线了,需要关闭界面
                    finishHandler.postDelayed(finishRunnable, 1000 * 5)
                    UR880Entrance.getInstance().send(
                        UR880SendInfo.Builder().inventory(mDevice.deviceId, 0, floor, 0).build()
                    )
                }

                if (floor == 5) {
                    if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                }
            }
            GET_LIST_FAIL -> {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                // showToast(msg.obj.toString())
                finish()
            }

            GET_LIST_NO_DATA -> {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                // showToast(msg.obj.toString())
                finish()
            }

            GET_LIST_SUCCESS -> {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()

                // todo
                val stockDossierOperatingList = msg.obj as ArrayList<DossierOperating>

                // 展示库存数据,mCabinetList开始是固定的120条格子基础数据,将库存数据根据position和light封装到格子数据中
                for (cabinet in mCabinetList) {
                    for (stockDossierOperating in stockDossierOperatingList) {
                        if (cabinet.floor == stockDossierOperating.floor && cabinet.position == stockDossierOperating.light) {
                            if (cabinet.stockList == null) {
                                cabinet.stockList = ArrayList()
                                cabinet.isStock = true
                            }
                            cabinet.stockList.add(stockDossierOperating)
                        }
                    }
                    mDemoInterfaceAdapter.notifyDataSetChanged()
                }

                mDemoInterfaceBinding.tvNormal.text = stockDossierOperatingList.size.toString()
                mDemoInterfaceBinding.tvEmpty.text =
                    (120 - stockDossierOperatingList.size).toString()

                // todo 先盘点一次
                // startInventory()
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

        // 是否开启倒计时关闭
        isAutoFinish = true

        val deviceList = DeviceService.getInstance().loadAll()
        if (deviceList.size > 0) {
            mDevice = deviceList[0]
        } else {
            finish()
        }

        getAndShowStockList()
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
                // todo 点击进行选择档案,准备取档
                val cabinet = mCabinetList[position]
                if (cabinet.isStock) {
                    // 改数据库中的数据状态
                    val dossierOperating = cabinet.stockList[0]
                    dossierOperating.selected = !cabinet.isSelect
                    // showToast("暂无数据" + dossierOperating.inputName + "," + dossierOperating.selected)
                    DossierOperatingService.getInstance().update(dossierOperating)

                    cabinet.isSelect = !cabinet.isSelect
                    mDemoInterfaceAdapter.notifyDataSetChanged()
                } else {
                    // showToast("暂无数据")
                }

                var hasSelect = false
                for (cabinets in mCabinetList) {
                    if (cabinets.isSelect) {
                        hasSelect = true
                        break
                    }
                }

                if (hasSelect) {
                    mDemoInterfaceBinding.btnQudang.isEnabled = true
                    mDemoInterfaceBinding.btnQudang.setBackgroundResource(R.drawable.selector_menu_green)
                } else {
                    mDemoInterfaceBinding.btnQudang.isEnabled = false
                    mDemoInterfaceBinding.btnQudang.setBackgroundResource(R.drawable.shape_btn_un_enable)
                }
            }
        }

        mDemoInterfaceAdapter.mOnItemLongClickListener = object :
            DemoInterfaceAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {
                // 长按展示档案数据详情
                val mStockList = mCabinetList[position].stockList
                if (mStockList != null && mStockList.size > 0) {
                    // showToast("数据条目数:" + mCabinetList[position].stockList.size)
                    showDossierDetailsDialog(mStockList)
                } else {
                    // showToast("暂无数据")
                }
            }
        }

        mDemoInterfaceAdapter.mErrorListener = object :
            DemoInterfaceAdapter.ErrorListener {
            override fun errorSize() {
                var emptySize = 0
                for (cabinet in mCabinetList) {
                    if (cabinet.isError()) {
                        emptySize = emptySize + 1
                        mDemoInterfaceBinding.tvError.text = emptySize.toString()
                        Log.e("zx-盘点-", "停止盘点---- emptySize.toString()=$emptySize")
                    }
                }
            }
        }

        UR880Entrance.getInstance().addOnInventoryListener(mInventoryListener)
    }

    /**
     * todo 展示柜子假档案数据
     */
    private fun getAndShowStockList() {
        mProgressDialog.setMessage("正在获取柜存档案...")
        if (!mProgressDialog.isShowing) mProgressDialog.show()

        val stockDossierOperatingList = DossierOperatingService.getInstance().loadAll()
        Log.e("zx-获取库存假数据", JSON.toJSONString(stockDossierOperatingList))

        // 剔除掉出库的数据 , 只现实在库的数据
        val iterator = stockDossierOperatingList.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().operatingType == 2)
                iterator.remove()
        }

        if (stockDossierOperatingList.size > 0) {
            val msg = Message.obtain()
            msg.what = GET_LIST_SUCCESS
            msg.obj = stockDossierOperatingList
            mHandler.sendMessageDelayed(msg, 800)
        }
    }

    private var mDossierDetailsDialogBinding: DialogDossierDetailsBinding? = null
    private var mDossierDetailsDialog: AlertDialog? = null
    private lateinit var mDossierDetailsAdapter: DialogDossierDetailsAdapter

    // 档案详情Dialog
    private fun showDossierDetailsDialog(stockList: ArrayList<DossierOperating>) {
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

        val window = mDossierDetailsDialog!!.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        window!!.setLayout(
            resources.displayMetrics.widthPixels * 2 / 3,
            resources.displayMetrics.heightPixels * 2 / 5
        )
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
            R.id.btn_qudang -> { // todo 取档
                val selectList = DossierOperatingService.getInstance().queryBySelected()
                if (selectList != null && selectList.size > 0) {
                    // todo 跳转取档界面,进行取档
//                    showToast("" + selectList.size)
                    intentActivity(OutboundOperatingActivity.newIntent(this))
                    finish()
                } else {
                    // showToast("请先选择档案")
                }

            }
            R.id.btn_inventory_storage -> {
                mDemoInterfaceBinding.tvError.text = "0"
                startInventory()
            }
            // 停止盘点
            // UR880Entrance.getInstance().send(UR880SendInfo.Builder().cancel(mDevice.deviceId).build())
//            R.id.btn_open_door -> {
//                UR880Entrance.getInstance()
//                    .send(UR880SendInfo.Builder().openDoor(mDevice.deviceId, 0).build())
//            }
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
        speek("正在盘点,请稍后")
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

        // 柜子一层盘点时间限制为10S,假如10S内没有结束一层盘点,说明读写器掉线了,需要关闭界面
        finishHandler.postDelayed(finishRunnable, 1000 * 5)
        UR880Entrance.getInstance()
            .send(UR880SendInfo.Builder().inventory(mDevice.deviceId, 0, floor, 0).build())
    }

    override fun onDestroy() {
        val selectList = DossierOperatingService.getInstance().queryBySelected()
        for (select in selectList) {
            select.selected = false
            DossierOperatingService.getInstance().update(select)
        }

//        UR880Entrance.getInstance().removeCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().removeInventoryListener(mInventoryListener)
        super.onDestroy()
    }

}