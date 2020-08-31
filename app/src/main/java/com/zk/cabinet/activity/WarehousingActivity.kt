package com.zk.cabinet.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.zk.cabinet.R
import com.zk.cabinet.adapter.WarehousingAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.ResultGetInStorage
import com.zk.cabinet.databinding.ActivityWarehousingBinding
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import org.json.JSONException
import java.lang.ref.WeakReference

private const val GET_WAREHOUSING_SUCCESS = 0x01
private const val GET_WAREHOUSING_FAIL = 0x02
private const val GET_WAREHOUSING_NO_DATA = 0x03

class WarehousingActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mWarehousingBinding: ActivityWarehousingBinding
    private lateinit var mHandler: WarehousingHandler
    private lateinit var mProgressDialog: ProgressDialog

    private var mWarehousingList =
        ArrayList<ResultGetInStorage.NameValuePairsBeanX.DataBean.ValuesBean>()
    private lateinit var mWarehousingAdapter: WarehousingAdapter

    companion object {
        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, WarehousingActivity::class.java)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            GET_WAREHOUSING_SUCCESS -> {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()

                mWarehousingList =
                    msg.obj as ArrayList<ResultGetInStorage.NameValuePairsBeanX.DataBean.ValuesBean>

                // 测试,自己加数据,可以模拟各种情况
//                for ((index, entity) in mWarehousingList.withIndex()) {
//                    if (index == 0 ||index == 1 ||index == 2 ||index == 3 || index == 4) {
//                        entity.nameValuePairs.cabCode = ""
//                        entity.nameValuePairs.position = ""
//                        entity.nameValuePairs.light = ""
//                    }
//                }

                mWarehousingAdapter.setList(mWarehousingList)
                mWarehousingAdapter.notifyDataSetChanged()
            }
            GET_WAREHOUSING_FAIL -> {
                mProgressDialog.dismiss()
                Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_SHORT).show()
                finish()
            }
            GET_WAREHOUSING_NO_DATA -> {
                mProgressDialog.dismiss()
                Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWarehousingBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_warehousing)

        mWarehousingBinding.onClickListener = this

        val name = mSpUtil.getString(SharedPreferencesUtil.Key.NameTemp, "xxx")
        mWarehousingBinding.tvOperator.text = name

        mHandler = WarehousingHandler(this)

        mWarehousingAdapter = WarehousingAdapter(this, mWarehousingList)
        mWarehousingBinding.warehousingLv.adapter = mWarehousingAdapter

        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)

        mProgressDialog.setMessage("正在获取待入库档案列表...")
        mProgressDialog.show()
        handler.post(runnable)
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mWarehousingBinding.warehousingCountdownTv.text = millisUntilFinished.toString()
    }

    private class WarehousingHandler(warehousingActivity: WarehousingActivity) : Handler() {
        private val warehousingWeakReference = WeakReference(warehousingActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            warehousingWeakReference.get()!!.handleMessage(msg)
        }
    }

    private fun getWarehousing() {
        mWarehousingList.clear()

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            "${NetworkRequest.instance.mWarehousingList}?orgCode=${mSpUtil.getString(
                Key.OrgCodeTemp, ""
            )!!}",
            Response.Listener { response ->
                try {
                    Log.e("获取入库列表-请求结果:", Gson().toJson(response))
                    val resultGetInStorage = JSON.parseObject<ResultGetInStorage>(
                        Gson().toJson(response),
                        ResultGetInStorage::class.java
                    )
                    if (resultGetInStorage.nameValuePairs.isSuccess) {
                        val values = resultGetInStorage.nameValuePairs.data.values
                        if (values.size > 0) {
                            val msg = Message.obtain()
                            msg.what = GET_WAREHOUSING_SUCCESS
                            msg.obj = values
                            mHandler.sendMessageDelayed(msg, 800)
                        } else {
                            val msg = Message.obtain()
                            msg.what = GET_WAREHOUSING_NO_DATA
                            msg.obj = "没有需要入库的档案"
                            mHandler.sendMessageDelayed(msg, 800)
                        }
                    } else {
                        val msg = Message.obtain()
                        msg.what = GET_WAREHOUSING_FAIL
                        msg.obj = resultGetInStorage.nameValuePairs.message
                        mHandler.sendMessageDelayed(msg, 800)
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    val msg = Message.obtain()
                    msg.what = GET_WAREHOUSING_FAIL
                    msg.obj = "数据解析失败"
                    mHandler.sendMessageDelayed(msg, 800)
                }
            },
            Response.ErrorListener { error ->
                val msg = if (error != null)
                    if (error.networkResponse != null)
                        "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                    else
                        "errorCode: -1 VolleyError: $error"
                else {
                    "errorCode: -1 VolleyError: 未知"
                }
                val message = Message.obtain()
                message.what = GET_WAREHOUSING_FAIL
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_open_door -> {
                intentActivity(
                    WarehousingOperatingActivity.newIntent(this)
                        .putExtra("InStorageList", mWarehousingList)
                )
            }

            R.id.btn_back -> {
                finish()
            }
        }
    }

    private val handler = Handler()
    private val runnable = Runnable {
        run {
            getWarehousing()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mProgressDialog.setMessage("正在获取待入库档案列表...")
        mProgressDialog.show()
        handler.postDelayed(runnable, 2000)
    }
}