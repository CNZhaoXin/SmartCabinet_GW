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
import com.zk.cabinet.adapter.OutboundAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.DossierOperating
import com.zk.cabinet.databinding.ActivityOutboundBinding
import com.zk.cabinet.db.DossierOperatingService
import com.zk.cabinet.net.NetworkRequest
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

class OutboundActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mOutboundBinding: ActivityOutboundBinding

    private lateinit var mHandler: OutboundHandler
    private lateinit var mProgressSyncUserDialog: ProgressDialog

    private var mOutboundList = ArrayList<DossierOperating>()
    private lateinit var mOutboundAdapter: OutboundAdapter

    companion object {
        private const val GET_OUTBOUND_SUCCESS = 0x01
        private const val GET_OUTBOUND_FAIL = 0x02

        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, OutboundActivity::class.java)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            GET_OUTBOUND_SUCCESS -> {
                mProgressSyncUserDialog.dismiss()
                mOutboundList = msg.obj as ArrayList<DossierOperating>
                mOutboundAdapter.setList(mOutboundList)
                mOutboundAdapter.notifyDataSetChanged()
            }
            GET_OUTBOUND_FAIL -> {
                mProgressSyncUserDialog.dismiss()
                Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOutboundBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_outbound)
        setSupportActionBar(mOutboundBinding.outboundToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mHandler = OutboundHandler(this)

        mOutboundAdapter = OutboundAdapter(this, mOutboundList)
        mOutboundBinding.outboundLv.adapter = mOutboundAdapter

        mProgressSyncUserDialog = ProgressDialog(this)
        getOutbound()
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mOutboundBinding.outboundCountdownTv.text = millisUntilFinished.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private class OutboundHandler(outboundActivity: OutboundActivity) : Handler() {
        private val outboundWeakReference = WeakReference(outboundActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            outboundWeakReference.get()!!.handleMessage(msg)
        }
    }

    private fun getOutbound() {
        mProgressSyncUserDialog.setTitle("获取入库列表")
        mProgressSyncUserDialog.setMessage("正在获取入库列表，请稍后......")
        if (!mProgressSyncUserDialog.isShowing) mProgressSyncUserDialog.show()

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            NetworkRequest.instance.mOutboundList,
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
                            tools.operatingType = jsonObject.getInt("outStorageType")
                            tools.warranType = jsonObject.getInt("warranType")
                            tools.cabinetId = jsonObject.getString("position")
                            val light = jsonObject.getInt("light")
                            tools.floor = light / 25
                            tools.light = light % 24

                            werehousingList.add(tools)
                        }
                        DossierOperatingService.getInstance().insertOrReplace(werehousingList)
                        val msg = Message.obtain()
                        msg.what = GET_OUTBOUND_SUCCESS
                        msg.obj = werehousingList
                        mHandler.sendMessage(msg)
                    } else {
                        val msg = Message.obtain()
                        msg.what = GET_OUTBOUND_FAIL
                        msg.obj = response.getString("message")
                        mHandler.sendMessage(msg)
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    val msg = Message.obtain()
                    msg.what = GET_OUTBOUND_FAIL
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
                message.what = GET_OUTBOUND_FAIL
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
            R.id.outbound_out_btn -> {
                intentActivity(WarehousingOperatingActivity.newIntent(this))
            }
        }
    }

}