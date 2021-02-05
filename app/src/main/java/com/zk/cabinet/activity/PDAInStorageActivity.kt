package com.zk.cabinet.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import androidx.databinding.DataBindingUtil
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.blankj.utilcode.util.LogUtils
import com.king.zxing.Intents
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import com.zk.cabinet.R
import com.zk.cabinet.adapter.SearchDossierByRfidAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.databinding.ActivityInStoragePdaBinding
import com.zk.cabinet.entity.RequestPostBind
import com.zk.cabinet.entity.RequestPostBindData
import com.zk.cabinet.entity.ResultGetArchivesInfoByRFID
import com.zk.cabinet.entity.SearchDossierDetailsData
import com.zk.cabinet.net.JsonObjectRequestWithHeader
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.pdauhf.PDAUhfHelper
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

private const val REQUEST_CODE_SCAN = 1;

// 消息类型:入库绑定
private const val BIND_SUCCESS = 0x01
private const val BIND_ERROR = 0x02

/**
 * PDA-入库
 */
class PDAInStorageActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityInStoragePdaBinding
    private lateinit var mHandler: MyHandler

    private lateinit var mAdapter: SearchDossierByRfidAdapter
    private var queryFileList = ArrayList<SearchDossierDetailsData>()

    companion object {
        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, PDAInStorageActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_in_storage_pda)
        mBinding.onClickListener = this

        mHandler = MyHandler(this)
        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)

        initFileAdapter()
        initPDAUhf()
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mBinding.tvCountdown.text = millisUntilFinished.toString()
    }

    private fun initFileAdapter() {
        mAdapter = SearchDossierByRfidAdapter(this, queryFileList)
        mBinding.listView.adapter = mAdapter
    }

    /**
     * 初始化PDAUhf读取
     */
    private fun initPDAUhf() {
        val isInit = PDAUhfHelper.getInstance().uhfInit()
        if (isInit) {
            // 初始化成功
            mBinding.btnScanRfid.isEnabled = true
            mBinding.btnScanRfid.setBackgroundResource(R.drawable.selector_menu_orange)
            // 初始化音频
            PDAUhfHelper.getInstance().initVoice(this)
            // 设置读取回调监听器
            PDAUhfHelper.getInstance().setReceiveListener(PDAUhfHelper.ReceiveListener {
                // todo 根据EPC查询档案,如果是 待入库/异常状态 的档案就可以入库,如果是在库状态就能移库
                showSuccessToast("EPC:$it")
                getArchivesInfoByRFID(arrayOf(it))
            })
        } else {
            mBinding.btnScanRfid.isEnabled = false
            mBinding.btnScanRfid.setBackgroundResource(R.drawable.shape_btn_un_enable)
            showErrorToast("PDA:UHF初始化失败")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_scan_barcode -> {
                AndPermission.with(this)
                    .runtime()
                    .permission(Permission.Group.STORAGE)
                    .permission(Permission.Group.CAMERA)
                    .onGranted { permissions: List<String?>? ->
                        val intent = Intent(this, CustomCaptureActivity::class.java)
                        startActivityForResult(intent, REQUEST_CODE_SCAN)
                    }
                    .onDenied { permissions: List<String?>? ->
                        // 拒绝权限直接打开设置界面进行提醒
                        val packageURI: Uri = Uri.parse("package:$packageName")
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            packageURI
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        showWarningToast("请允许相应权限")
                    }
                    .start()

            }

            R.id.btn_scan_rfid -> {
                PDAUhfHelper.getInstance().startInventoryOne()
            }

            // 入库提交
            R.id.btn_in_storage -> {
                postBind()
            }
        }
    }

    private lateinit var mProgressDialog: ProgressDialog

    /**
     * Post 入库绑定
     * 支持多条数据一起提交，每条数据格式：
     * 参数名 类型 描述
     * posRFID string 库位id
     * rfid string 档案rfid
     */
    private fun postBind() {
        val posRFID = mBinding.etBarcode.text.toString().trim()
        if (TextUtils.isEmpty(posRFID)) {
            showWarningToast("请先扫描库位二维码或手动输入库位号")
            return
        } else if (queryFileList.size == 0) {
            showWarningToast("请先扫描待入库/异常出库档案")
            return
        }

        mProgressDialog.setMessage("正在入库...")
        mProgressDialog.show()

        val requestUrl = NetworkRequest.instance.mPosBind
        LogUtils.e("入库绑定-请求URL:$requestUrl")

        val requestPostBind = RequestPostBind()
        val requestPostBindList = ArrayList<RequestPostBindData>()
        for (entity in queryFileList) {
            val requestPostBindData = RequestPostBindData()
            requestPostBindData.posRFID = posRFID
            requestPostBindData.rfid = entity.rfid
            requestPostBindList.add(requestPostBindData)
        }
        requestPostBind.bindType = "1" // 入库
        requestPostBind.data = requestPostBindList

        val jsonObject = JSONObject(JSON.toJSONString(requestPostBind))
        LogUtils.e("入库绑定-请求参数:" + JSON.toJSONString(requestPostBind))
        LogUtils.e("入库绑定-请求参数:$jsonObject")

        val jsonObjectRequest = JsonObjectRequestWithHeader(
            Request.Method.POST, requestUrl, jsonObject,
            { response ->
                LogUtils.e("入库绑定-请求结果:", "$response")
                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            sendDelayMessage(BIND_SUCCESS, "入库绑定成功")
                            queryFileList.clear()
                            mAdapter.notifyDataSetChanged()
                            mBinding.etBarcode.setText("")
                        } else {
                            sendDelayMessage(BIND_ERROR, response.getString("msg"))
                        }
                    } else {
                        sendDelayMessage(BIND_ERROR, "入库绑定-失败")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    sendDelayMessage(BIND_ERROR, "入库绑定-失败")
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

    /**
     * 根据RFID数组获取档案信息
     * rfids tring[] 档案的rfid数组
     */
    private fun getArchivesInfoByRFID(rfidArray: Array<String>) {
        /*   */
        /** 所属档案柜 *//*
        private String archivesId;
        */
        /**
         * 借阅人所在部门名称
         *//*
        private String borrowerDeptName;
        */
        /**
         * 借阅人姓名
         *//*
        private String borrowerName;
        */
        /** 档案名称 *//*
        private String archivesName;
        */
        /** RFID *//*
        private String rfid;
        */
        /** 行号 *//*
        private Integer rowNo;
        */
        /** 序号 *//*
        private Integer numNo;
        */
        /** 档案状态  100-待借阅  200-待归还*//*
        private Integer archivesStatus;
        @ApiModelProperty(value = "灯位列表")
        List<Integer> lampList = new ArrayList<>();*/

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
                            val result: ResultGetArchivesInfoByRFID =
                                JSON.parseObject(
                                    "$response",
                                    ResultGetArchivesInfoByRFID::class.java
                                )

                            val dataList = result.data
                            if (dataList != null && dataList.size > 0) {
                                for (newEntity in dataList) {
                                    // 待入库状态的档案才能入库
                                    // 档案状态:待入库0,在库10,借阅审批中50,待借阅100,待归还200,异常9000
                                    if (newEntity.archivesStatus == 0 || newEntity.archivesStatus == 9000) {
                                        if (queryFileList.size > 0) {
                                            var isExist = false
                                            for (oldEntity in queryFileList) {
                                                if (newEntity.archivesId == oldEntity.archivesId) {
                                                    isExist = true
                                                }
                                            }

                                            if (!isExist)
                                                queryFileList.add(newEntity)
                                        } else {
                                            queryFileList.add(newEntity)
                                        }
                                    }
                                }

                                mAdapter.setList(queryFileList)
                                mAdapter.notifyDataSetChanged()
                            } else {
                                showWarningToast("未查询到档案信息")
                            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CODE_SCAN -> {
                    // 扫描二维码/条码回传 数据
                    val result = data.getStringExtra(Intents.Scan.RESULT)
                    mBinding.etBarcode.setText(result)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PDAUhfHelper.getInstance().release()
    }


    private class MyHandler(activity: PDAInStorageActivity) : Handler() {
        private val mainWeakReference = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mainWeakReference.get()!!.handleMessage(msg)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            BIND_SUCCESS -> {
                mProgressDialog.dismiss()
                showSuccessToast("${msg.obj}")
            }
            BIND_ERROR -> {
                mProgressDialog.dismiss()
                showErrorToast("${msg.obj}")
            }
        }
    }
}

