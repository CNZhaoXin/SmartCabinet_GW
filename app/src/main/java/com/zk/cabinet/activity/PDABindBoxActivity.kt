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
import com.zk.cabinet.adapter.PDADossierAdapter
import com.zk.cabinet.adapter.PDADossierBoxAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.databinding.ActivityPdaBindBoxBinding
import com.zk.cabinet.entity.RequestBind
import com.zk.cabinet.entity.ResultGetBoxListByPosCode
import com.zk.cabinet.net.JsonObjectRequestWithHeader
import com.zk.cabinet.net.NetworkRequest
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

private const val REQUEST_CODE_SCAN_POS_CODE = 1;
private const val REQUEST_CODE_SCAN_BOX_ID = 2;

// 消息类型: 档案盒绑定库位
private const val BIND_SUCCESS = 0x01
private const val BIND_ERROR = 0x02

/**
 * PDA-档案盒绑定库位
 */
class PDABindBoxActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityPdaBindBoxBinding
    private lateinit var mHandler: MyHandler
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var scanResult: String

    private lateinit var mDossierBoxAdapter: PDADossierBoxAdapter
    private lateinit var mDossierAdapter: PDADossierAdapter

    private var boxList = ArrayList<ResultGetBoxListByPosCode.DataBean.BoxListBean>()
    private var archivesList = ArrayList<ResultGetBoxListByPosCode.DataBean.ArchivesListBean>()

    companion object {
        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, PDABindBoxActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pda_bind_box)
        mBinding.onClickListener = this

        mHandler = MyHandler(this)

        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)

        initAdapter()
    }

    private fun initAdapter() {
        mDossierBoxAdapter = PDADossierBoxAdapter(this, boxList)
        mBinding.listViewDossierBox.adapter = mDossierBoxAdapter

        mDossierAdapter = PDADossierAdapter(this, archivesList)
        mBinding.listViewDossier.adapter = mDossierAdapter
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
                        startActivityForResult(intent, REQUEST_CODE_SCAN_POS_CODE)
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

            R.id.btn_scan_box_id -> {
                AndPermission.with(this)
                    .runtime()
                    .permission(Permission.Group.STORAGE)
                    .permission(Permission.Group.CAMERA)
                    .onGranted { permissions: List<String?>? ->
                        val intent = Intent(this, CustomCaptureActivity::class.java)
                        startActivityForResult(intent, REQUEST_CODE_SCAN_BOX_ID)
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

            // 档案盒绑定库位
            R.id.btn_bind -> {
                bind()
            }
        }

    }

    private val loadListHandler = Handler()
    private val loadListRunnable = Runnable {
        run {
            getBoxListByPosCode(scanResult)
        }
    }

    /**
     * 29.根据库位编号获取绑定的档案盒信息
     * 接口地址： get   /api/pad/getBoxListByPosCode
     * posCode 库位编号
     */
    private fun getBoxListByPosCode(posCode: String) {
        val requestUrl = NetworkRequest.instance.mGetBoxListByPosCode + "?posCode=" + posCode
        LogUtils.e("根据库位编号获取绑定的档案盒/档案信息-请求URL:$requestUrl")

        val jsonObjectRequest = JsonObjectRequestWithHeader(
            Request.Method.GET, requestUrl, { response ->
                LogUtils.e("根据库位编号获取绑定的档案盒/档案信息-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val result: ResultGetBoxListByPosCode =
                                JSON.parseObject("$response", ResultGetBoxListByPosCode::class.java)

                            val data = result.data
                            if (data != null) {
                                if (data.boxList != null)
                                    boxList = data.boxList as ArrayList

                                if (data.archivesList != null)
                                    archivesList = data.archivesList as ArrayList

                                mProgressDialog.dismiss()

                                // 库位绑定的档案盒集合
                                if (data.boxList != null && boxList.size > 0) {
                                    mDossierBoxAdapter.setList(boxList)
                                    mDossierBoxAdapter.notifyDataSetChanged()
                                } else {
                                    boxList.clear()
                                    mDossierBoxAdapter.notifyDataSetChanged()
                                    showWarningToast("未查询到该库位所绑定的档案盒信息")
                                }

                                // 库位绑定的档案集合
                                if (data.archivesList != null && archivesList.size > 0) {
                                    mDossierAdapter.setList(archivesList)
                                    mDossierAdapter.notifyDataSetChanged()
                                } else {
                                    archivesList.clear()
                                    mDossierAdapter.notifyDataSetChanged()
                                    showWarningToast("未查询到该库位所绑定的档案信息")
                                }
                            } else {
                                mProgressDialog.dismiss()

                                boxList.clear()
                                mDossierBoxAdapter.notifyDataSetChanged()

                                archivesList.clear()
                                mDossierAdapter.notifyDataSetChanged()

                                showWarningToast("未查询到该库位的绑定信息")
                            }
                        } else {
                            mProgressDialog.dismiss()

                            boxList.clear()
                            mDossierBoxAdapter.notifyDataSetChanged()

                            archivesList.clear()
                            mDossierAdapter.notifyDataSetChanged()

                            showErrorToast(response.getString("msg"))
                        }
                    } else {
                        mProgressDialog.dismiss()
                        boxList.clear()
                        mDossierBoxAdapter.notifyDataSetChanged()
                        archivesList.clear()
                        mDossierAdapter.notifyDataSetChanged()
                        showErrorToast("根据库位编号获取绑定的档案盒/档案信息-请求失败")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    mProgressDialog.dismiss()
                    boxList.clear()
                    mDossierBoxAdapter.notifyDataSetChanged()
                    archivesList.clear()
                    mDossierAdapter.notifyDataSetChanged()
                    showErrorToast("根据库位编号获取绑定的档案盒/档案信息-请求失败")
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
                boxList.clear()
                mDossierBoxAdapter.notifyDataSetChanged()
                archivesList.clear()
                mDossierAdapter.notifyDataSetChanged()
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
     * 27.档案盒绑定库位
     * 接口地址：  post   /busi/baseArchivesBox/bind
     * posCode string 库位编号
     * boxDataId string 档案盒ID
     */
    private fun bind() {
        val posCode = mBinding.tvPosCode.text.trim().toString()
        val boxId = mBinding.tvBoxId.text.trim().toString()
        if (TextUtils.isEmpty(posCode)) {
            showWarningToast("请先扫描库位二维码")
            return
        } else if (TextUtils.isEmpty(boxId)) {
            showWarningToast("请先扫描档案盒二维码")
            return
        }

        mProgressDialog.setMessage("正在绑定库位...")
        mProgressDialog.show()

        val requestUrl = NetworkRequest.instance.mBind
        LogUtils.e("档案盒绑定库位-请求URL:$requestUrl")

        val requestBind = RequestBind()
        requestBind.posCode = posCode
        requestBind.boxDataId = boxId

        val jsonObject = JSONObject(JSON.toJSONString(requestBind))
        LogUtils.e("档案盒绑定库位-请求参数:" + JSON.toJSONString(requestBind))
        LogUtils.e("档案盒绑定库位-请求参数:$jsonObject")

        val jsonObjectRequest = JsonObjectRequestWithHeader(
            Request.Method.POST, requestUrl, jsonObject,
            { response ->
                LogUtils.e("档案盒绑定库位-请求结果:", "$response")
                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            sendDelayMessage(BIND_SUCCESS, "档案盒绑定库位成功")
                            mBinding.tvBoxId.text = ""
                        } else {
                            sendDelayMessage(BIND_ERROR, response.getString("msg"))
                        }
                    } else {
                        sendDelayMessage(BIND_ERROR, "档案盒绑定库位-失败")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    sendDelayMessage(BIND_ERROR, "档案盒绑定库位-失败")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CODE_SCAN_POS_CODE -> { // 扫描 库位二维码 返回结果
                    // 扫描二维码/条码回传 数据
                    scanResult = data.getStringExtra(Intents.Scan.RESULT)
                    mBinding.tvPosCode.text = scanResult

                    mProgressDialog.setMessage("正在查询...")
                    mProgressDialog.show()
                    loadListHandler.postDelayed(loadListRunnable, 1000)
                }

                REQUEST_CODE_SCAN_BOX_ID -> { // 扫描 档案盒二维码 返回结果
                    // 扫描二维码/条码回传 数据
                    val scanResult = data.getStringExtra(Intents.Scan.RESULT)
                    mBinding.tvBoxId.text = scanResult
                }
            }
        }
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mBinding.tvCountdown.text = millisUntilFinished.toString()
    }

    private class MyHandler(activity: PDABindBoxActivity) : Handler() {
        private val mainWeakReference = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mainWeakReference.get()!!.handleMessage(msg)
        }
    }

    private fun sendDelayMessage(msgWhat: Int, msgObj: String) {
        val message = Message.obtain()
        message.what = msgWhat
        message.obj = msgObj
        mHandler.sendMessageDelayed(message, 1000)
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            BIND_SUCCESS -> {
                mProgressDialog.dismiss()
                showSuccessToast("${msg.obj}")

                mProgressDialog.setMessage("正在重新查询...")
                mProgressDialog.show()
                loadListHandler.postDelayed(loadListRunnable, 1000)
            }
            BIND_ERROR -> {
                mProgressDialog.dismiss()
                showErrorToast("${msg.obj}")
            }
        }
    }
}

