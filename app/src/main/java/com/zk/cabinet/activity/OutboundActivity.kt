package com.zk.cabinet.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.cabinet.R
import com.zk.cabinet.adapter.OutboundAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.bean.DossierOperating
import com.zk.cabinet.bean.ResultGetOutBound
import com.zk.cabinet.databinding.ActivityOutboundBinding
import com.zk.cabinet.db.DossierOperatingService
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import org.json.JSONException
import java.lang.ref.WeakReference

class OutboundActivity : TimeOffAppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemClickListener {
    private lateinit var mOutboundBinding: ActivityOutboundBinding

    private lateinit var mHandler: OutboundHandler
    private lateinit var mProgressDialog: ProgressDialog

    private var mOutboundList = ArrayList<DossierOperating>()
    private lateinit var mOutboundAdapter: OutboundAdapter

    companion object {
        private const val GET_OUTBOUND_SUCCESS = 0x01
        private const val GET_OUTBOUND_FAIL = 0x02
        private const val GET_OUTBOUND_NO_DATA = 0x03

        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, OutboundActivity::class.java)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            GET_OUTBOUND_SUCCESS -> {
                mProgressDialog.dismiss()
                mOutboundList = msg.obj as ArrayList<DossierOperating>
                mOutboundAdapter.setList(mOutboundList)
                mOutboundAdapter.notifyDataSetChanged()
            }
            GET_OUTBOUND_NO_DATA -> {
                mProgressDialog.dismiss()
                Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_SHORT).show()
                finish()
            }
            GET_OUTBOUND_FAIL -> {
                mProgressDialog.dismiss()
                Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOutboundBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_outbound)

        mOutboundBinding.onClickListener = this
        mOutboundBinding.onItemClickListener = this

        mHandler = OutboundHandler(this)

        val name = mSpUtil.getString(SharedPreferencesUtil.Key.NameTemp, "xxx")
        mOutboundBinding.tvOperator.text = name

        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)

        mProgressDialog.setMessage("正在获取待出库档案列表...")
        mProgressDialog.show()

        // todo 随机选2份档案 产生取档列表
        val dossierList = DossierOperatingService.getInstance().loadAll()
        // (数据类型)(最小值+Math.random()*(最大值-最小值+1))
        val random1 = (1 + Math.random() * (dossierList.size - 1 - 1 + 1)).toInt()
        val random2 = random1 + 1

        val randomDossierList = ArrayList<DossierOperating>()
        randomDossierList.add(dossierList[random1])
        randomDossierList.add(dossierList[random2])

        mOutboundAdapter = OutboundAdapter(this, randomDossierList)
        mOutboundBinding.outboundLv.adapter = mOutboundAdapter

        val msg = Message.obtain()
        msg.what = GET_OUTBOUND_SUCCESS
        msg.obj = randomDossierList
        mHandler.sendMessageDelayed(msg, 800)

    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mOutboundBinding.outboundCountdownTv.text = millisUntilFinished.toString()
    }

    private class OutboundHandler(outboundActivity: OutboundActivity) : Handler() {
        private val outboundWeakReference = WeakReference(outboundActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            outboundWeakReference.get()!!.handleMessage(msg)
        }
    }

    private fun getOutbound() {
        mOutboundList.clear()

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            "${NetworkRequest.instance.mOutboundList}?orgCode=${mSpUtil.getString(
                SharedPreferencesUtil.Key.OrgCodeTemp, ""
            )!!}",
            Response.Listener { response ->
                try {
                    Log.e("获取出库列表-请求结果：", Gson().toJson(response));
                    val resultGetOutBound = JSON.parseObject<ResultGetOutBound>(
                        Gson().toJson(response),
                        ResultGetOutBound::class.java
                    )
                    if (resultGetOutBound.nameValuePairs.isSuccess) {
                        val values = resultGetOutBound.nameValuePairs.data.values
                        if (resultGetOutBound.nameValuePairs.dataCount == "1" && values.size > 0) {
                            // 剔除不属于我的柜子权限的出库条目
                            val canOperateCabinetList =
                                mSpUtil.getString(SharedPreferencesUtil.Key.CanOperateCabinet, "")
                            val gson = Gson()
                            val deviceList = gson.fromJson<List<Device>>(
                                canOperateCabinetList,
                                object : TypeToken<List<Device?>?>() {}.type
                            )
                            val deviceSet = HashSet<String>()
                            for (device in deviceList) {
                                deviceSet.add(device.deviceName)
                            }

                            var mCanOutboundList =
                                ArrayList<ResultGetOutBound.NameValuePairsBeanX.DataBean.ValuesBean>()
                            for (value in values) {
                                if (deviceSet.contains(value.nameValuePairs.cabcode)) {
                                    mCanOutboundList.add(value)
                                }
                            }

                            if (mCanOutboundList.size > 0) {
                                val msg = Message.obtain()
                                msg.what = GET_OUTBOUND_SUCCESS
                                msg.obj = mCanOutboundList
                                mHandler.sendMessageDelayed(msg, 800)
                            } else {
                                val msg = Message.obtain()
                                msg.what = GET_OUTBOUND_NO_DATA
                                msg.obj = "没有需要出库的档案"
                                mHandler.sendMessageDelayed(msg, 800)
                            }
                        } else {
                            val msg = Message.obtain()
                            msg.what = GET_OUTBOUND_NO_DATA
                            msg.obj = "没有需要出库的档案"
                            mHandler.sendMessageDelayed(msg, 800)
                        }

                    } else {
                        val msg = Message.obtain()
                        msg.what = GET_OUTBOUND_FAIL
                        msg.obj = resultGetOutBound.nameValuePairs.message
                        mHandler.sendMessageDelayed(msg, 800)
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    val msg = Message.obtain()
                    msg.what = GET_OUTBOUND_FAIL
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
                message.what = GET_OUTBOUND_FAIL
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
                mProgressDialog.setMessage("正在开柜，请稍后...")
                if (!mProgressDialog.isShowing) mProgressDialog.show()

                // 传递被选择的数据
                var selectList = ArrayList<DossierOperating>()
                for (select in mOutboundList) {
                    if (select.selected) {
                        selectList.add(select)
                    }
                }

                intentActivity(
                    OutboundOperatingActivity.newIntent(this).putExtra("OutBoundList", selectList)
                )
            }

            R.id.btn_back -> {
                finish()
            }
        }
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mOutboundList[position].selected = !mOutboundList[position].selected

        if (mOutboundList[position].selected) {
            var device: String? = null
            var isOK = true
            for (entity in mOutboundList) {
                if (entity.selected) {
                    if (device == null) {
                        device = entity.cabcode
                    } else {
                        if (device != entity.cabcode) {
                            isOK = false
                            break
                        }
                    }
                }
            }

            if (isOK) {
                mOutboundBinding.btnOpenDoor.background =
                    resources.getDrawable(R.drawable.selector_menu_green)
                mOutboundBinding.btnOpenDoor.isEnabled = true
            } else {
                showToast("请选中相同的柜体档案操作！")
                mOutboundBinding.btnOpenDoor.background =
                    resources.getDrawable(R.drawable.shape_btn_un_enable)
                mOutboundBinding.btnOpenDoor.isEnabled = false
            }
        } else {
            var hasSelect = false
            for (dossierOperating in mOutboundList) {
                if (dossierOperating.selected) {
                    hasSelect = true
                    break
                } else {
                    hasSelect = false
                }
            }

            if (hasSelect) {
                var device: String? = null
                var isOK = true
                for (entity in mOutboundList) {
                    if (entity.selected) {
                        if (device == null) {
                            device = entity.cabcode
                        } else {
                            if (device != entity.cabcode) {
                                isOK = false
                                break
                            }
                        }
                    }
                }

                if (isOK) {
                    mOutboundBinding.btnOpenDoor.background =
                        resources.getDrawable(R.drawable.selector_menu_green)
                    mOutboundBinding.btnOpenDoor.isEnabled = true
                } else {
                    showToast("请选中相同的柜体档案操作！")
                    mOutboundBinding.btnOpenDoor.background =
                        resources.getDrawable(R.drawable.shape_btn_un_enable)
                    mOutboundBinding.btnOpenDoor.isEnabled = false
                }
            } else {
                mOutboundBinding.btnOpenDoor.background =
                    resources.getDrawable(R.drawable.shape_btn_un_enable)
                mOutboundBinding.btnOpenDoor.isEnabled = false
            }
        }

        mOutboundAdapter.notifyDataSetChanged()

    }
}