package com.zk.cabinet.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.zk.cabinet.R
import com.zk.cabinet.adapter.WarehousingAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.DossierOperating
import com.zk.cabinet.databinding.ActivityWarehousingBinding
import com.zk.cabinet.db.DossierOperatingService
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.cabinet.utils.SharedPreferencesUtil
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

private const val GET_WAREHOUSING_SUCCESS = 0x01
private const val GET_WAREHOUSING_FAIL = 0x02

class WarehousingActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mWarehousingBinding: ActivityWarehousingBinding
    private lateinit var mHandler: WarehousingHandler
    private lateinit var mProgressDialog: ProgressDialog

    private var mWarehousingList = ArrayList<DossierOperating>()
    private lateinit var mWarehousingAdapter: WarehousingAdapter

    companion object {
        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, WarehousingActivity::class.java)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            GET_WAREHOUSING_SUCCESS -> {
                mProgressDialog.dismiss()
                mWarehousingList = msg.obj as ArrayList<DossierOperating>
                mWarehousingAdapter.setList(mWarehousingList)
                mWarehousingAdapter.notifyDataSetChanged()
            }
            GET_WAREHOUSING_FAIL -> {
                mProgressDialog.dismiss()
                Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWarehousingBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_warehousing)

        mWarehousingBinding.onClickListener = this

        mHandler = WarehousingHandler(this)

        mWarehousingAdapter = WarehousingAdapter(this, mWarehousingList)
        mWarehousingBinding.warehousingLv.adapter = mWarehousingAdapter

        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setMessage("正在获取档案列表，请稍后...")
        if (!mProgressDialog.isShowing) mProgressDialog.show()
        getWarehousing()

        val name = mSpUtil.getString(SharedPreferencesUtil.Key.NameTemp, "未知")
        mWarehousingBinding.tvOperator.text = name
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
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            "${NetworkRequest.instance.mWarehousingList}?orgCode=${mSpUtil.getString(Key.OrgCodeTemp,
                "00000000"
            )!!}",
            Response.Listener { response ->
                try {
                    DossierOperatingService.getInstance().deleteAll()

                    val werehousingList = DossierOperatingService.getInstance().nullList
                    val success = response.getBoolean("success")
                    if (success) {
                        val dataJsonArray = response.getJSONArray("data")
                        for (i in 0 until dataJsonArray.length()) {
                            val jsonObject: JSONObject = dataJsonArray.getJSONObject(i)
                            val tools = DossierOperating()
                            tools.warrantNum = jsonObject.getString("warrantNum")
                            tools.rfidNum = jsonObject.getString("rfidNum")
                            tools.warrantName = jsonObject.getString("warrantName")
                            tools.warrantNo = jsonObject.getString("warrantNo")
                            tools.warranCate = jsonObject.getString("warranCate")
                            tools.operatingType = jsonObject.getInt("inStorageType")
                            tools.warranType = jsonObject.getInt("warranType")

                            werehousingList.add(tools)
                        }
                        DossierOperatingService.getInstance().insertOrReplace(werehousingList)
                        val msg = Message.obtain()
                        msg.what = GET_WAREHOUSING_SUCCESS
                        msg.obj = werehousingList
                        mHandler.sendMessageDelayed(msg, 800)
                    } else {
                        val msg = Message.obtain()
                        msg.what = GET_WAREHOUSING_FAIL
                        msg.obj = response.getString("message")
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
                intentActivity(WarehousingOperatingActivity.newIntent(this))
            }

            R.id.btn_back -> {
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getWarehousing()
    }
}