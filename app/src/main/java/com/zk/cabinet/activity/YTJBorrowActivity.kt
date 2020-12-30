package com.zk.cabinet.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.blankj.utilcode.util.LogUtils
import com.zk.cabinet.R
import com.zk.cabinet.adapter.YTJBorrowAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.constant.SelfComm
import com.zk.cabinet.databinding.ActivityYtjBorrowBinding
import com.zk.cabinet.databinding.DialogEntryFinishBinding
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.entity.RequestArchivesBorrow
import com.zk.cabinet.entity.RequestLightsUp
import com.zk.cabinet.entity.ResultGetToBorrowList
import com.zk.cabinet.net.JsonObjectRequestWithHeader
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.rfid.bean.LabelInfo
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.callback.InventoryListener
import com.zk.rfid.ur880.UR880Entrance
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

/**
 * 一体机-借阅
 */
class YTJBorrowActivity : TimeOffAppCompatActivity(), AdapterView.OnItemClickListener,
    View.OnClickListener {
    private lateinit var mBinding: ActivityYtjBorrowBinding
    private lateinit var mHandler: MyHandler

    private var mDevice: Device? = null
    private lateinit var mProgressDialog: ProgressDialog
    private val labelInfoList = ArrayList<LabelInfo>()

    companion object {
        private const val START_INVENTORY = 0x01
        private const val INVENTORY_VALUE = 0x02
        private const val CANCEL_INVENTORY = 0x03
        private const val END_INVENTORY = 0x04

        private const val SUBMIT_SUCCESS = 0x05
        private const val SUBMIT_ERROR = 0x06

        private const val LIGHT_UP_SUCCESS = 0x07
        private const val LIGHT_UP_ERROR = 0x08

        private const val FINISH_ACTIVITY = 0x10

        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, YTJBorrowActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_ytj_borrow)
        mBinding.onClickListener = this
        mBinding.onItemClickListener = this
        mHandler = MyHandler(this)

        // 初始化ProcessDialog
        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)
        // 显示操作人
        val name = mSpUtil.getString(SharedPreferencesUtil.Key.NameTemp, "xxx")
        mBinding.tvOperator.text = name
        // 拿到自动添加保存的读写器设备
        val deviceList = DeviceService.getInstance().loadAll()
        if (deviceList.size > 0) {
            mDevice = deviceList[0]
        }
        // 开启界面自动关闭
        isAutoFinish = true
        timerStart()
        // 初始化列表数据
        initAdapter()
        // 获取待借阅列表数据
        mProgressDialog.setMessage("正在获取待借阅档案列表...")
        mProgressDialog.show()
        loadListHandler.postDelayed(loadListRunnable, 1000)

        UR880Entrance.getInstance().addOnInventoryListener(mInventoryListener)
    }

    private val loadListHandler = Handler()
    private val loadListRunnable = Runnable {
        run {
            getToBorrowList()
        }
    }

    private lateinit var mAdapter: YTJBorrowAdapter
    private var mList = ArrayList<ResultGetToBorrowList.DataBean>()
    private var mListDAJ = ArrayList<ResultGetToBorrowList.DataBean>()

    private fun initAdapter() {
        mAdapter = YTJBorrowAdapter(this, mList)
        mBinding.listView.adapter = mAdapter
    }

    /**
     * 获取当前登录人待借的档案信息
     */
    private fun getToBorrowList() {
        val requestUrl = NetworkRequest.instance.mGetToBorrowList
        LogUtils.e("获取当前登录人待借的档案信息-requestUrl:$requestUrl")

        val jsonObjectRequest = JsonObjectRequestWithHeader(
            Request.Method.GET, requestUrl, { response ->
                LogUtils.e("获取当前登录人待借的档案信息-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val result: ResultGetToBorrowList = JSON.parseObject(
                                "$response", ResultGetToBorrowList::class.java
                            )
                            mList = result.data as ArrayList<ResultGetToBorrowList.DataBean>
                            if (mList.size > 0) {
                                mProgressDialog.dismiss()
                                mAdapter.setList(mList)
                                mAdapter.notifyDataSetChanged()
                                mBinding.llNoData.visibility = View.GONE

                                // 过滤出属于一体机可操作的档案组架的档案
                                val curCabinetDossier = ArrayList<ResultGetToBorrowList.DataBean>()
                                for (entity in mList) {
                                    if (entity.cabinetType == "1") {
                                        curCabinetDossier.add(entity)
                                    }
                                }
                                if (curCabinetDossier.size > 0) {
                                    showWarningToast("档案组架中有「${curCabinetDossier.size}」份档案可借阅")
                                    speek("档案组架中有${curCabinetDossier.size}份档案可借阅")
                                } else {
                                    showWarningToast("档案组架中暂无档案可借阅")
                                    speek("档案组架中暂无档案可借阅")
                                }

                            } else {
                                // 这里要设置一张无数据的空图片
                                mProgressDialog.dismiss()
                                speek("暂无待借阅档案")
                                showWarningToast("暂无待借阅档案")
                                mBinding.tvNoDate.text = "暂无待借阅档案"
                                mBinding.llNoData.visibility = View.VISIBLE
                            }
                        } else {
                            // 这里要设置一张无数据的空图片
                            mProgressDialog.dismiss()
                            speek("获取待借阅的档案列表-请求失败")
                            showErrorToast(response.getString("msg"))
                            mBinding.tvNoDate.text = "暂无待借阅档案"
                            mBinding.llNoData.visibility = View.VISIBLE
                        }
                    } else {
                        // 这里要设置一张无数据的空图片
                        mProgressDialog.dismiss()
                        speek("获取待借阅的档案列表-请求失败")
                        showErrorToast("获取待借阅的档案列表-请求失败")
                        mBinding.tvNoDate.text = "暂无待借阅档案"
                        mBinding.llNoData.visibility = View.VISIBLE
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    // 这里要设置一张无数据的空图片
                    mProgressDialog.dismiss()
                    speek("获取待借阅的档案列表-请求失败")
                    showErrorToast("获取待借阅的档案列表-请求失败")
                    mBinding.tvNoDate.text = "暂无待借阅档案"
                    mBinding.llNoData.visibility = View.VISIBLE
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
                speek("获取待借阅的档案列表-请求失败")
                showErrorToast(msg)
                mBinding.tvNoDate.text = "暂无待借阅档案"
                mBinding.llNoData.visibility = View.VISIBLE
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            START_INVENTORY -> { // 开始盘点
                mProgressDialog.setMessage("正在扫描,请稍后...")
                mProgressDialog.show()
            }
            INVENTORY_VALUE -> {
                val labelInfo = msg.obj as LabelInfo
                labelInfoList.add(labelInfo)
                Log.e("一体机待借阅", "labelInfo.deviceID: ${labelInfo.deviceID}")
                Log.e("一体机待借阅", "labelInfo.antennaNumber: ${labelInfo.antennaNumber}")
                Log.e("一体机待借阅", "labelInfo.fastID: ${labelInfo.fastID}")
                Log.e("一体机待借阅", "labelInfo.rssi: ${labelInfo.rssi}")
                Log.e("一体机待借阅", "labelInfo.operatingTime: ${labelInfo.operatingTime}")
                Log.e("一体机待借阅", "labelInfo.epcLength: ${labelInfo.epcLength}")
                Log.e("一体机待借阅", "labelInfo.epc: ${labelInfo.epc}")
                Log.e("一体机待借阅", "labelInfo.tid: ${labelInfo.tid}")
                Log.e("一体机待借阅", "labelInfo.inventoryNumber: ${labelInfo.inventoryNumber}")
            }
            CANCEL_INVENTORY -> { // 停止盘点
            }
            END_INVENTORY -> { // 盘点结束
                // 扫描结束后EPC编码与数据列表中数据EPC比对
                val selectedList = ArrayList<ResultGetToBorrowList.DataBean>()
                for (entity in mList) {
                    entity.isSelect = false
                    // 且是档案组架的档案才能操作,一体机只能操作档案组架的借阅和归还
                    for (labelInfo in labelInfoList) {
                        if (labelInfo.epc == entity.rfid && entity.cabinetType == "1") {
                            entity.isSelect = true
                            selectedList.add(entity)
                            break
                        }
                    }
                }

                if (selectedList.size > 0) {
                    showWarningToast("扫描到【${selectedList.size}】份待借阅档案")
                    speek("扫描到${selectedList.size}份待借阅档案")
                    mBinding.btnCommit.background =
                        resources.getDrawable(R.drawable.selector_menu_green_normal)
                    mBinding.btnCommit.isEnabled = true
                } else {
                    showWarningToast("未扫描到待借阅档案")
                    speek("未扫描到待借阅档案")
                    mBinding.btnCommit.background =
                        resources.getDrawable(R.drawable.shape_btn_un_enable)
                    mBinding.btnCommit.isEnabled = false
                }

                Log.e("一体机待借阅-盘点到的标签数据-", JSON.toJSONString(labelInfoList))
                mProgressDialog.dismiss()
                mAdapter.notifyDataSetChanged()
                labelInfoList.clear()
            }
            SUBMIT_SUCCESS -> {
                mProgressDialog.dismiss()
                showWarningToast("【${msg.obj}】份档案借阅成功")
                speek("${msg.obj}份档案借阅成功")

                // 清出掉列表中借阅成功的档案
                val iterator = mList.iterator()
                while (iterator.hasNext()) {
                    if (iterator.next().isSelect)
                        iterator.remove()
                }
                // 借阅按钮不可点击
                mBinding.btnCommit.background =
                    resources.getDrawable(R.drawable.shape_btn_un_enable)
                mBinding.btnCommit.isEnabled = false

                // 借阅成功,且没有任何待借阅条目了就主动关闭掉界面
                if (mList.size == 0) {
                    mAdapter.notifyDataSetChanged()
                    val message = Message.obtain()
                    message.what = FINISH_ACTIVITY
                    mHandler.sendMessageDelayed(message, 5000)
                } else {
                    mAdapter.notifyDataSetChanged()
                }
            }
            SUBMIT_ERROR -> {
                mProgressDialog.dismiss()
                showErrorToast("${msg.obj}")
            }

            LIGHT_UP_SUCCESS -> {
                mProgressDialog.dismiss()
                showWarningToast("【${msg.obj}】份待借阅档案,亮灯成功")
                speek("${msg.obj}份待借阅档案,亮灯成功,请尽快前往亮灯档案架取档,取回档案后扫描确认,并点击借阅")

                // 亮灯成功后,亮灯按钮就不能再使用
                mBinding.btnOpenLight.background =
                    resources.getDrawable(R.drawable.shape_btn_un_enable)
                mBinding.btnOpenLight.isEnabled = false

            }
            LIGHT_UP_ERROR -> {
                mProgressDialog.dismiss()
                showErrorToast("${msg.obj}")
            }

            FINISH_ACTIVITY -> { // 关闭界面
                mProgressDialog.dismiss()
                finish()
            }
        }
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mBinding.outboundOperatingCountdownTv.text = millisUntilFinished.toString()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // 点击暂无数据,重新加载
            R.id.ll_no_data -> {
                mBinding.llNoData.visibility = View.GONE
                mProgressDialog.setMessage("正在获取待借阅档案列表...")
                mProgressDialog.show()
                loadListHandler.postDelayed(loadListRunnable, 1000)
            }
            // 亮灯
            R.id.btn_open_light -> {
                // (此版本不做)亮灯dialog,可选档案亮灯,或者显示要确定亮灯的所有档案
                mListDAJ.clear()
                for (entity in mList) {
                    // 档案组架的档案
                    if (entity.cabinetType == "1") {
                        mListDAJ.add(entity)
                    }
                }
                if (mListDAJ.size > 0) {
                    // 亮所有的灯,既然要借阅那就亮所有好了
                    lightUp(mListDAJ)
                } else {
                    showWarningToast("暂无可亮灯的档案组架档案")
                    speek("暂无可亮灯的档案组架档案")
                }
            }

            // 开启单次扫描
            R.id.btn_scan -> {
                /*
                 * ID : 读写器ID，注册回调里有这个ID
                 * fastId : 0x01 启用FastID功能 0x00 不启用FastID功能（该功能目前未启用，传个0就好了）
                 * antennaNumber: 天线号 (0-3)，一共4根天线 (接一根天线就是0)
                 * inventoryType ; 0x00 非连续盘点 0x01 连续盘点
                 */
                LogUtils.e("读写器设备ID:" + mDevice!!.deviceId)
                UR880Entrance.getInstance().send(
                    UR880SendInfo.Builder().inventory(mDevice!!.deviceId, 0x00, 0x00, 0x00).build()
                )
            }

            // 借阅提交
            R.id.btn_commit -> {
                val equipmentId = mSpUtil.getString(SharedPreferencesUtil.Key.EquipmentId, "")
                if (!TextUtils.isEmpty(equipmentId)) {
                    borrowSubmission(equipmentId!!)
                } else {
                    showErrorToast("请先配置一体机设备ID")
                }
            }

            R.id.btn_back -> {
                // 如果已经有确认过的档案,退出需要提醒,没有就直接退出
                val selectedList = ArrayList<ResultGetToBorrowList.DataBean>()
                for (select in mList) {
                    if (select.isSelect) {
                        selectedList.add(select)
                    }
                }
                if (selectedList.size > 0) {
                    showEntryFinishDialog(selectedList)
                } else {
                    finish()
                }
            }
            // 确认关闭弹窗的取消按钮
            R.id.btn_cancel -> {
                if (mDialogFinish != null && mDialogFinish!!.isShowing)
                    mDialogFinish!!.dismiss()
            }
            // 确认关闭弹窗的确认按钮
            R.id.btn_entry -> {
                if (mDialogFinish != null && mDialogFinish!!.isShowing)
                    mDialogFinish!!.dismiss()
                finish()
            }
        }
    }

    // 确认关闭弹窗
    private var mDialogFinishBinding: DialogEntryFinishBinding? = null
    private var mDialogFinish: AlertDialog? = null

    private fun showEntryFinishDialog(selectedList: ArrayList<ResultGetToBorrowList.DataBean>) {
        if (mDialogFinish == null) {
            mDialogFinish = AlertDialog.Builder(this).create()
            mDialogFinishBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.dialog_entry_finish,
                null,
                false
            )
            mDialogFinishBinding!!.onClickListener = this
            mDialogFinish!!.setView(mDialogFinishBinding!!.root)
            mDialogFinish!!.setCancelable(true)
        }
        mDialogFinishBinding!!.tvContent.text = "您有【${selectedList.size}】份待借阅的档案需要点击借阅进行提交,确定退出界面吗?"
        mDialogFinish!!.show()

        val window = mDialogFinish!!.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        // 一体机4 横屏显示需要适配
        val deviceName = mSpUtil.getString(SharedPreferencesUtil.Key.DeviceName, "").toString()
        if (SelfComm.DEVICE_NAME[4].equals(deviceName)) {
            window!!.setLayout(
                resources.displayMetrics.widthPixels * 3 / 5,
                resources.displayMetrics.heightPixels * 3 / 5
            )
        } else {
            window!!.setLayout(
                resources.displayMetrics.widthPixels * 2 / 3,
                resources.displayMetrics.heightPixels * 2 / 5
            )
        }
    }

    /**
     *  2. 亮灯
     * 接口地址：Post /api/pad/lightUp
     * 平台收到请求后，通过MQTT下发亮灯指令；
     */
    private fun lightUp(mListDaj: ArrayList<ResultGetToBorrowList.DataBean>) {
        mProgressDialog.setMessage("【${mListDaj.size}】份待借阅档案正在亮灯...")
        mProgressDialog.show()

        val requestUrl = NetworkRequest.instance.mLightUp
        LogUtils.e("亮灯-请求URL:$requestUrl")

        val requestLightsUp = RequestLightsUp()
        // 亮灯的档案RFID数组
        val rfidList = ArrayList<String>()
        for (entity in mListDaj) {
            rfidList.add(entity.rfid)
        }
        val rfidArrays = rfidList.toArray(arrayOfNulls<String>(rfidList.size)) as Array<String>
        requestLightsUp.listValue = rfidArrays

        val jsonObject = JSONObject(JSON.toJSONString(requestLightsUp))
        LogUtils.e("亮灯-请求参数:$jsonObject")

        val jsonObjectRequest = JsonObjectRequestWithHeader(
            Request.Method.POST, requestUrl, jsonObject,
            { response ->
                LogUtils.e("亮灯-请求结果:", "$response")
                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            sendDelayMessage(LIGHT_UP_SUCCESS, rfidList.size.toString())
                        } else {
                            sendDelayMessage(LIGHT_UP_ERROR, response.getString("msg"))
                        }
                    } else {
                        sendDelayMessage(LIGHT_UP_ERROR, "亮灯请求-失败")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    sendDelayMessage(LIGHT_UP_ERROR, "亮灯请求-失败")
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
                showErrorToast(msg)
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    /**
     * 批量档案取件
    接口地址：  Post /api/pad/archivesBorrow
     */
    private fun borrowSubmission(equipmentId: String) {
        mProgressDialog.setMessage("正在提交...")
        mProgressDialog.show()

        val requestUrl = NetworkRequest.instance.mArchivesBorrow
        LogUtils.e("借阅提交-请求URL:$requestUrl")

        // rfid string 档案rfid
        // aioId string 一体机或者智能柜操作屏id
        val requestArchivesBorrow = RequestArchivesBorrow()

        // 一体机的设备ID
        requestArchivesBorrow.aioId = equipmentId
        // 勾选的档案RFID数组
        val selectList = ArrayList<String>()
        for (entity in mList) {
            if (entity.isSelect) {
                selectList.add(entity.rfid)
            }
        }
        val array = selectList.toArray(arrayOfNulls<String>(selectList.size)) as Array<String>
        requestArchivesBorrow.rfids = array

        val jsonObject = JSONObject(JSON.toJSONString(requestArchivesBorrow))
        LogUtils.e("借阅提交-请求参数:$jsonObject")

        val jsonObjectRequest = JsonObjectRequestWithHeader(
            Request.Method.POST, requestUrl, jsonObject,
            { response ->
                LogUtils.e("借阅提交-请求结果:", "$response")
                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            sendDelayMessage(SUBMIT_SUCCESS, array.size.toString())
                        } else {
                            sendDelayMessage(SUBMIT_ERROR, response.getString("msg"))
                        }
                    } else {
                        sendDelayMessage(SUBMIT_ERROR, "借阅提交-失败")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    sendDelayMessage(SUBMIT_ERROR, "借阅提交-失败")
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
                showErrorToast(msg)
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    private fun sendDelayMessage(msgWhat: Int, msgObj: String) {
        val message = Message.obtain()
        message.what = msgWhat
        message.obj = msgObj
        mHandler.sendMessageDelayed(message, 1000)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    private class MyHandler(activity: YTJBorrowActivity) :
        Handler() {
        private val weakReference = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            weakReference.get()!!.handleMessage(msg)
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

    override fun onDestroy() {
        UR880Entrance.getInstance().removeInventoryListener(mInventoryListener)
        super.onDestroy()
    }
}