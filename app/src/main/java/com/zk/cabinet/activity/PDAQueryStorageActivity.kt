package com.zk.cabinet.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
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
import com.zk.cabinet.databinding.ActivityPdaQueryStorageBinding
import com.zk.cabinet.entity.ResultGetBoxListByPosCode
import com.zk.cabinet.net.JsonObjectRequestWithHeader
import com.zk.cabinet.net.NetworkRequest
import org.json.JSONException
import java.lang.ref.WeakReference

private const val REQUEST_CODE_SCAN = 1;

/**
 * PDA-根据 库位ID 查询该库位绑定的 档案盒/档案
 */
class PDAQueryStorageActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityPdaQueryStorageBinding
    private lateinit var mHandler: MyHandler
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var scanResult: String

    private lateinit var mDossierBoxAdapter: PDADossierBoxAdapter
    private lateinit var mDossierAdapter: PDADossierAdapter

    private var boxList = ArrayList<ResultGetBoxListByPosCode.DataBean.BoxListBean>()
    private var archivesList = ArrayList<ResultGetBoxListByPosCode.DataBean.ArchivesListBean>()

    companion object {
        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, PDAQueryStorageActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pda_query_storage)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CODE_SCAN -> {
                    // 扫描二维码/条码回传 数据
                    scanResult = data.getStringExtra(Intents.Scan.RESULT)
                    mBinding.tvBarcode.text = scanResult

                    mProgressDialog.setMessage("正在查询...")
                    mProgressDialog.show()
                    loadListHandler.postDelayed(loadListRunnable, 1000)
                }
            }
        }
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mBinding.tvCountdown.text = millisUntilFinished.toString()
    }

    private class MyHandler(activity: PDAQueryStorageActivity) : Handler() {
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
        }
    }
}

