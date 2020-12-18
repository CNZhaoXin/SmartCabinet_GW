package com.zk.cabinet.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.blankj.utilcode.util.LogUtils
import com.zk.cabinet.R
import com.zk.cabinet.adapter.PDAInventorAdapter
import com.zk.cabinet.databinding.FragmentPdaInventoryBinding
import com.zk.cabinet.entity.RequestSubmitInventoryResult
import com.zk.cabinet.entity.ResultGetNoStartInventoryPlan
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

    private lateinit var mAdapter: PDAInventorAdapter
    private var mList = ArrayList<String>()

    private fun initAdapter() {
        mAdapter = PDAInventorAdapter(requireActivity(), mList)
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
            mBinding.btnStartInventory.setBackgroundResource(R.drawable.selector_menu_orange)
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
                // todo 根据EPC查询档案,时时查询的话对服务器消耗太大,先只显示epc
                val epc = msg.obj as String
                mList.add(epc)

                // 去重
                val hashSet = HashSet(mList)
                mList.clear()
                mList.addAll(hashSet)
                mAdapter.notifyDataSetChanged()
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
            // 开始盘库
            R.id.btn_start_inventory -> {
                PDAUhfHelper.getInstance().isStopInventory = false
                PDAUhfHelper.getInstance().startInventoryRepeat(entity.equipmentId)
            }

            // 停止盘库
            R.id.btn_stop_inventory -> {
                PDAUhfHelper.getInstance().setStopInventory(true)
            }

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
        if (mList.size < 0) {
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
        val array = mList.toArray(arrayOfNulls<String>(mList.size)) as Array<String>
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
                            mList.clear()
                            mAdapter.notifyDataSetChanged()
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

}