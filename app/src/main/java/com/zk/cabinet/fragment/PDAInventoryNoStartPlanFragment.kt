package com.zk.cabinet.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.blankj.utilcode.util.LogUtils
import com.zk.cabinet.R
import com.zk.cabinet.activity.PDAInventoryOperatorActivity
import com.zk.cabinet.adapter.PDAInventoryNoStartPlanAdapter
import com.zk.cabinet.databinding.FragmentPdaInventoryNoStartPlanBinding
import com.zk.cabinet.entity.ResultGetNoStartInventoryPlan
import com.zk.cabinet.net.JsonObjectRequestWithHeader
import com.zk.cabinet.net.NetworkRequest
import org.json.JSONException


/**
 * PDA-待盘库计划
 */
class PDAInventoryNoStartPlanFragment : BaseFragment(), View.OnClickListener,
    AdapterView.OnItemClickListener {

    private lateinit var mBinding: FragmentPdaInventoryNoStartPlanBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_pda_inventory_no_start_plan,
            container,
            false
        )

        initAdapter()
        mBinding.loadingView.smoothToShow()
        getNoStartInventoryPlan()
        mBinding.onClickListener = this
        mBinding.onItemClickListener = this
        return mBinding.root
    }

    private lateinit var mAdapter: PDAInventoryNoStartPlanAdapter
    private var mList = ArrayList<ResultGetNoStartInventoryPlan.DataBean>()

    private fun initAdapter() {
        mAdapter = PDAInventoryNoStartPlanAdapter(requireActivity(), mList)
        mBinding.listView.adapter = mAdapter
    }

    /**
     * 获取未盘库的盘库计划信息列表
     */
    private fun getNoStartInventoryPlan() {
        // http://118.25.102.226:11001/api/pad/getNoStartInventoryPlan
        val requestUrl = NetworkRequest.instance.mGetNoStartInventoryPlan
        LogUtils.e("获取未盘库的盘库计划信息-requestUrl:$requestUrl")

        val jsonObjectRequest = JsonObjectRequestWithHeader(
            Request.Method.GET, requestUrl, { response ->
                LogUtils.e("获取未盘库的盘库计划信息-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val result: ResultGetNoStartInventoryPlan =
                                JSON.parseObject(
                                    "$response",
                                    ResultGetNoStartInventoryPlan::class.java
                                )

                            val dataList = result.data
                            if (dataList != null && dataList.size > 0) {
                                mList =
                                    dataList as ArrayList<ResultGetNoStartInventoryPlan.DataBean>
                                mAdapter.setList(dataList)
                                mAdapter.notifyDataSetChanged()
                                mBinding.llNoData.visibility = GONE
                                mBinding.loadingView.smoothToHide()
                            } else {
                                // 这里要设置一张无数据的空图片
                                showWarningToast("暂无盘库计划信息")
                                mBinding.llNoData.visibility = VISIBLE
                                mBinding.loadingView.smoothToHide()
                            }
                        } else {
                            showWarningToast(response.getString("msg"))
                            mBinding.loadingView.smoothToHide()
                        }
                    } else {
                        showWarningToast("获取未盘库的盘库计划信息-请求失败")
                        mBinding.loadingView.smoothToHide()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showErrorToast("获取未盘库的盘库计划信息-请求失败")
                    mBinding.loadingView.smoothToHide()
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
                mBinding.loadingView.smoothToHide()
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
            R.id.ll_no_data -> {
                mBinding.loadingView.smoothToShow()
                mBinding.llNoData.visibility = GONE
                getNoStartInventoryPlan()
            }
        }
    }

    // ListView的
    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val dataEntity = mList[p2]
        if (dataEntity.inventoryType == "2") { // PDA盘库
            // 打开盘库操作界面,传递要盘库的柜子数据
            val bundle = Bundle()
            bundle.putSerializable("entity", dataEntity)
            val intent = Intent()
            intent.putExtras(bundle)
            intent.setClass(requireContext(), PDAInventoryOperatorActivity::class.java)
            startActivity(intent)
        }

    }
}