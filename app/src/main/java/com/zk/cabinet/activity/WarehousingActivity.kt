package com.zk.cabinet.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MenuItem
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
import com.zk.cabinet.bean.WarehousingInfo
import com.zk.cabinet.databinding.ActivityWarehousingBinding
import com.zk.cabinet.db.WarehousingService
import com.zk.cabinet.net.NetworkRequest
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

private const val GET_WAREHOUSING_SUCCESS = 0x01
private const val GET_WAREHOUSING_FAIL = 0x02

class WarehousingActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mWarehousingBinding: ActivityWarehousingBinding
    private lateinit var mHandler: WarehousingHandler
    private lateinit var mProgressSyncUserDialog: ProgressDialog

    private var mWarehousingList = ArrayList<WarehousingInfo>()
    private lateinit var mWarehousingAdapter: WarehousingAdapter

    companion object {
        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, WarehousingActivity::class.java)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            GET_WAREHOUSING_SUCCESS -> {
                mProgressSyncUserDialog.dismiss()
                mWarehousingList = msg.obj as ArrayList<WarehousingInfo>
                mWarehousingAdapter.notifyDataSetChanged()
            }
            GET_WAREHOUSING_FAIL -> {
                mProgressSyncUserDialog.dismiss()
                Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWarehousingBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_warehousing)
        setSupportActionBar(mWarehousingBinding.warehousingToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mWarehousingBinding.onClickListener = this

        mHandler = WarehousingHandler(this)


        val tools = WarehousingInfo()
        tools.warrantNum = "warrantNum"
        tools.rfidNum = "rfidNum"
        tools.warrantName = "warrantName"
        tools.warrantNo = "warrantNo"
        tools.warranCate = "warranCate"
        tools.inStorageType = 1
        tools.warranType = 1
        mWarehousingList.add(tools)

        mWarehousingAdapter = WarehousingAdapter(this, mWarehousingList)
        mWarehousingBinding.warehousingLv.adapter = mWarehousingAdapter

        mProgressSyncUserDialog = ProgressDialog(this)
        getWarehousing()
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mWarehousingBinding.warehousingCountdownTv.text = millisUntilFinished.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private class WarehousingHandler(warehousingActivity: WarehousingActivity) : Handler() {
        private val warehousingWeakReference = WeakReference(warehousingActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            warehousingWeakReference.get()!!.handleMessage(msg)
        }
    }

    private fun getWarehousing() {
        mProgressSyncUserDialog.setTitle("获取入库列表")
        mProgressSyncUserDialog.setMessage("正在获取入库列表，请稍后......")
        if (!mProgressSyncUserDialog.isShowing) mProgressSyncUserDialog.show()

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            NetworkRequest.instance.mClientLogin,
            Response.Listener { response ->
                try {
                    WarehousingService.getInstance().deleteAll()

                    val werehousingList = WarehousingService.getInstance().nullList
                    val success = response.getBoolean("success")
                    if (success) {
                        val dataJsonArray = response.getJSONArray("data")
                        for (i in 0 until dataJsonArray.length()) {
                            val jsonObject: JSONObject = dataJsonArray.getJSONObject(i)
                            val tools = WarehousingInfo()
                            tools.warrantNum = jsonObject.getString("warrantNum")
                            tools.rfidNum = jsonObject.getString("rfidNum")
                            tools.warrantName = jsonObject.getString("warrantName")
                            tools.warrantNo = jsonObject.getString("warrantNo")
                            tools.warranCate = jsonObject.getString("warranCate")
                            tools.inStorageType = jsonObject.getInt("inStorageType")
                            tools.warranType = jsonObject.getInt("warranType")

                            werehousingList.add(tools)
                        }
                        WarehousingService.getInstance().insertOrReplace(werehousingList)
                        val msg = Message.obtain()
                        msg.what = GET_WAREHOUSING_SUCCESS
                        msg.obj = werehousingList
                        mHandler.sendMessage(msg)
                    } else {
                        val msg = Message.obtain()
                        msg.what = GET_WAREHOUSING_FAIL
                        msg.obj = response.getString("message")
                        mHandler.sendMessage(msg)
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    val msg = Message.obtain()
                    msg.what = GET_WAREHOUSING_FAIL
                    msg.obj = "数据解析失败。"
                    mHandler.sendMessage(msg)
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
                mHandler.sendMessage(message)
            })
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.warehousing_out_btn -> {
                intentActivity(WarehousingOperatingActivity.newIntent(this))
            }
        }
    }
}