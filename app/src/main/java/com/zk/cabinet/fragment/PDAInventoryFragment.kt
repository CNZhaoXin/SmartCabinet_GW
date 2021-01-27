package com.zk.cabinet.fragment

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.blankj.utilcode.util.LogUtils
import com.zk.cabinet.R
import com.zk.cabinet.adapter.SearchDossierByRfidInventoryAdapter
import com.zk.cabinet.databinding.FragmentPdaInventoryBinding
import com.zk.cabinet.entity.RequestSubmitInventoryResult
import com.zk.cabinet.entity.ResultGetArchivesInfoByRFIDInventory
import com.zk.cabinet.entity.ResultGetNoStartInventoryPlan
import com.zk.cabinet.entity.SearchDossierDetailsDataInventory
import com.zk.cabinet.net.JsonObjectRequestWithHeader
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.pdauhf.PDAUhfHelper
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

private const val ARG_PARAM = "entity"
private const val ARG_PARAM_PLAN_ID = "planId"
private const val AAD_LIST = 0x01

/**
 * PDA单柜盘库界面
 */
class PDAInventoryFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentPdaInventoryBinding
    private lateinit var entity: ResultGetNoStartInventoryPlan.DataBean.CabineBeanListBean
    private lateinit var planId: String
    private lateinit var mHandler: MyHandler

    companion object {
        private const val SUBMIT_ERROR = 0x03
        private const val SUBMIT_SUCCESS = 0x02

        @JvmStatic
        fun newInstance(
            planId: String,
            entity: ResultGetNoStartInventoryPlan.DataBean.CabineBeanListBean
        ) =
            PDAInventoryFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM_PLAN_ID, planId)
                    putSerializable(ARG_PARAM, entity)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            entity =
                it.getSerializable(ARG_PARAM) as ResultGetNoStartInventoryPlan.DataBean.CabineBeanListBean
            planId = it.getString(ARG_PARAM_PLAN_ID, "")
        }
    }

    lateinit var mBroadcastManager: LocalBroadcastManager
    lateinit var mReceiver: BroadcastReceiver

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 初始化广播
        mBroadcastManager = LocalBroadcastManager.getInstance(requireActivity())

        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.STOP_INVENTORY")

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val fragmentTag = intent?.getStringExtra("FragmentTag")
                if (mBinding.btnStartInventory.text == "停止盘库" && fragmentTag != tag) {
                    PDAUhfHelper.getInstance().setStopInventory(true)

                    mBinding.btnStartInventory.text = "开始盘库"
                    mBinding.btnStartInventory.setBackgroundResource(R.drawable.selector_menu_orange_normal)

                    if (mScanEpcList.size > 0) {
                        mBinding.btnCommit.isEnabled = true
                        mBinding.btnCommit.setBackgroundResource(R.drawable.selector_menu_green_normal)
                    } else {
                        mBinding.btnCommit.isEnabled = false
                        mBinding.btnCommit.setBackgroundResource(R.drawable.shape_btn_un_enable)
                    }
                }

                if (fragmentTag == tag) {
                    // 各盘点界面接收广播有延迟和顺序,需加延迟执行
                    handler.postDelayed(runnable, 500)
                }
            }
        }
        mBroadcastManager.registerReceiver(mReceiver, intentFilter)
    }

    var handler = Handler()
    var runnable = Runnable {
        PDAUhfHelper.getInstance().isStopInventory = false
        PDAUhfHelper.getInstance().startInventoryRepeat(entity.equipmentId)

        mBinding.btnStartInventory.text = "停止盘库"
        mBinding.btnStartInventory.setBackgroundResource(R.drawable.selector_menu_red)

        mBinding.btnCommit.isEnabled = false
        mBinding.btnCommit.setBackgroundResource(R.drawable.shape_btn_un_enable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBroadcastManager.unregisterReceiver(mReceiver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_pda_inventory,
            container,
            false
        )
        mBinding.onClickListener = this
        mHandler = MyHandler(this)

        mProgressDialog = ProgressDialog(requireContext(), R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)

        initAdapter()
        initPDAUhf()
        return mBinding.root
    }

    private var mScanEpcList = ArrayList<String>()
    private lateinit var mAdapter: SearchDossierByRfidInventoryAdapter
    private var queryFileList = ArrayList<SearchDossierDetailsDataInventory>()

    private fun initAdapter() {
        mAdapter = SearchDossierByRfidInventoryAdapter(requireActivity(), queryFileList)
        mBinding.listView.adapter = mAdapter
    }

    /**
     * 初始化PDAUhf读取
     */
    private fun initPDAUhf() {
        val isInit = PDAUhfHelper.getInstance().uhfInit()
        if (isInit) {
            // 初始化成功
            mBinding.btnStartInventory.isEnabled = true
            mBinding.btnStartInventory.setBackgroundResource(R.drawable.selector_menu_orange_normal)
            // 初始化音频
            PDAUhfHelper.getInstance().initVoice(requireContext())
            // 设置读取回调监听器
            PDAUhfHelper.getInstance()
                .setReceiveListener(entity.equipmentId, PDAUhfHelper.ReceiveListener {
                    val message = Message.obtain()
                    message.what = AAD_LIST
                    message.obj = it
                    mHandler.sendMessage(message)
                })
        } else {
            mBinding.btnStartInventory.isEnabled = false
            mBinding.btnStartInventory.setBackgroundResource(R.drawable.shape_btn_un_enable)
            showErrorToast("PDA:UHF初始化失败")
        }
    }

    private class MyHandler(t: PDAInventoryFragment) : Handler() {
        private val mainWeakReference = WeakReference(t)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mainWeakReference.get()!!.handleMessage(msg)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            AAD_LIST -> {
                val epc = msg.obj as String
                if (!mScanEpcList.contains(epc)) {
                    mScanEpcList.add(epc)
                    getArchivesInfoByRFID(arrayOf(epc))
                }
            }

            SUBMIT_SUCCESS -> {
                mProgressDialog.dismiss()
                showSuccessToast("${msg.obj}")
            }
            SUBMIT_ERROR -> {
                mProgressDialog.dismiss()
                showErrorToast("${msg.obj}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PDAUhfHelper.getInstance().release()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // 开始/停止 盘库
            R.id.btn_start_inventory -> {
                if (mBinding.btnStartInventory.text == "开始盘库") {
                    // 停止其他fragment的盘库,开启当前fragment的盘点
                    val intent = Intent("android.intent.action.STOP_INVENTORY")
                    intent.putExtra("FragmentTag", tag)
                    LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
                } else {
                    PDAUhfHelper.getInstance().setStopInventory(true)

                    mBinding.btnStartInventory.text = "开始盘库"
                    mBinding.btnStartInventory.setBackgroundResource(R.drawable.selector_menu_orange_normal)

                    if (mScanEpcList.size > 0) {
                        mBinding.btnCommit.isEnabled = true
                        mBinding.btnCommit.setBackgroundResource(R.drawable.selector_menu_green_normal)
                    } else {
                        mBinding.btnCommit.isEnabled = false
                        mBinding.btnCommit.setBackgroundResource(R.drawable.shape_btn_un_enable)
                    }

                    val intent = Intent("android.intent.action.START_INVENTORY")
                    intent.putExtra("FragmentTag", tag)
                    LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
                }
            }

            // 停止盘库
//            R.id.btn_stop_inventory -> {
//              PDAUhfHelper.getInstance().setStopInventory(true)
//            }

            // 盘库提交
            R.id.btn_commit -> {
                submitInventoryResult()
            }
        }
    }

    private lateinit var mProgressDialog: ProgressDialog
    private fun sendDelayMessage(msgWhat: Int, msgObj: String) {
        val message = Message.obtain()
        message.what = msgWhat
        message.obj = msgObj
        mHandler.sendMessageDelayed(message, 1000)
    }

    /**
     * 根据档案柜提交盘库结果（普通柜）
     * 接口地址：  Post /api/pad/submitInventoryResult
     * 批量提交，如果有一份档案提交失败，所有数据回滚；
     */
    private fun submitInventoryResult() {
        if (mScanEpcList.size == 0) {
            showWarningToast("没有要提交的数据")
            return
        }

        mProgressDialog.setMessage("正在提交...")
        mProgressDialog.show()

        val requestUrl = NetworkRequest.instance.mSubmitInventoryResult
        LogUtils.e("盘库提交-请求URL:$requestUrl")

        val requestSubmitInventoryResult = RequestSubmitInventoryResult()

        requestSubmitInventoryResult.planId = planId
        requestSubmitInventoryResult.houseCode = entity.houseCode
        requestSubmitInventoryResult.cabinetEquipmentId = entity.equipmentId
        val array = mScanEpcList.toArray(arrayOfNulls<String>(mScanEpcList.size)) as Array<String>
        requestSubmitInventoryResult.rfids = array

        val jsonObject = JSONObject(JSON.toJSONString(requestSubmitInventoryResult))
        // LogUtils.e("盘库提交-请求参数:" + JSON.toJSONString(requestSubmitInventoryResult))
        LogUtils.e("盘库提交-请求参数:$jsonObject")

        val jsonObjectRequest = JsonObjectRequestWithHeader(
            Request.Method.POST, requestUrl, jsonObject,
            { response ->
                LogUtils.e("盘库提交-请求结果:", "$response")
                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            sendDelayMessage(SUBMIT_SUCCESS, "盘库提交成功")
                            mScanEpcList.clear()
                            queryFileList.clear()
                            hasDataEpcNum = 0
                            noDataEpcNum = 0
                            mBinding.tvTotalDataNum.text = "0"
                            mBinding.tvHasDataEpcNum.text = "0"
                            mBinding.tvNoDataEpcNum.text = "0"
                            mAdapter.notifyDataSetChanged()

                            mBinding.btnCommit.isEnabled = false
                            mBinding.btnCommit.setBackgroundResource(R.drawable.shape_btn_un_enable)
                        } else {
                            sendDelayMessage(SUBMIT_ERROR, response.getString("msg"))
                        }
                    } else {
                        sendDelayMessage(SUBMIT_ERROR, "盘库提交-失败")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    sendDelayMessage(SUBMIT_ERROR, "盘库提交-失败")
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

    var hasDataEpcNum = 0
    var noDataEpcNum = 0

    /**
     * 根据RFID数组获取档案信息
     * rfids tring[] 档案的rfid数组
     */
    private fun getArchivesInfoByRFID(rfidArray: Array<String>) {
        val sbParameter = StringBuffer()
        for ((index, entity) in rfidArray.withIndex()) {
            if (index == 0)
                sbParameter.append("rfid=$entity")
            else
                sbParameter.append("&rfid=$entity")
        }

        val requestUrl =
            NetworkRequest.instance.mGetArchivesInfoByRFID + "?" + sbParameter.toString()
        LogUtils.e("根据RFID数组获取档案信息-requestUrl:$requestUrl")
        // http://118.25.102.226:11001/api/pad/getArchivesInfoByRFID?rfid=50000008&rfid=50000008&rfid=50000008

        val jsonObjectRequest = JsonObjectRequestWithHeader(
            Request.Method.GET, requestUrl, { response ->
                LogUtils.e("根据RFID数组获取档案信息-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val result: ResultGetArchivesInfoByRFIDInventory =
                                JSON.parseObject(
                                    "$response",
                                    ResultGetArchivesInfoByRFIDInventory::class.java
                                )

                            val dataList = result.data
                            if (dataList != null && dataList.size > 0) {
                                for (newEntity in dataList) {
                                    newEntity.isHasData = true
                                    hasDataEpcNum++
                                    queryFileList.add(newEntity)
                                }
                            } else {
                                for (epc in rfidArray) {
                                    val noDataEpc = SearchDossierDetailsDataInventory()
                                    noDataEpc.isHasData = false
                                    noDataEpc.rfid = epc
                                    noDataEpcNum++
                                    queryFileList.add(noDataEpc)
                                }
                                // showWarningToast("未查询到档案信息")
                            }

                            mAdapter.setList(queryFileList)
                            mAdapter.notifyDataSetChanged()

                            // 已识别档案
                            mBinding.tvHasDataEpcNum.text = hasDataEpcNum.toString()
                            // 未识别档案
                            mBinding.tvNoDataEpcNum.text = noDataEpcNum.toString()
                            // 总识别EPC数量
                            mBinding.tvTotalDataNum.text = mScanEpcList.size.toString()

                        } else {
                            showWarningToast(response.getString("msg"))
                        }
                    } else {
                        showWarningToast("根据RFID数组获取档案信息-请求失败")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showErrorToast("根据RFID数组获取档案信息-请求失败")
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
                showErrorToast(msg)
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

}