package com.zk.cabinet.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
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
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.zk.cabinet.R
import com.zk.cabinet.activity.PDAInventoryDifferenceDetailsActivity
import com.zk.cabinet.adapter.PDADoneInventoryPlanAdapter
import com.zk.cabinet.databinding.FragmentPdaDoneInventoryPlanBinding
import com.zk.cabinet.entity.ResultGetDoneInventoryPlan
import com.zk.cabinet.net.JsonObjectRequestWithHeader
import com.zk.cabinet.net.NetworkRequest
import org.json.JSONException

/**
 * PDA-盘库结果
 */
class PDADoneInventoryPlanFragment : BaseFragment(), View.OnClickListener,
    AdapterView.OnItemClickListener {

    private lateinit var mBinding: FragmentPdaDoneInventoryPlanBinding
    private lateinit var mProgressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_pda_done_inventory_plan,
            container,
            false
        )
        mBinding.onClickListener = this
        mBinding.onItemClickListener = this

        initAdapter()

        // 初始化Dialog
        mProgressDialog = ProgressDialog(activity, R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)

        // 初始化 下拉刷新,上拉加载
        val classicsHeader = ClassicsHeader(requireActivity())
        classicsHeader.setTextSizeTitle(26f)
        classicsHeader.setTextSizeTime(26f)
        classicsHeader.setDrawableSize(22f)
        classicsHeader.setAccentColorId(R.color.white)
        mBinding.refreshLayout.setRefreshHeader(classicsHeader)

        val classicsFooter = ClassicsFooter(requireActivity())
        classicsFooter.setTextSizeTitle(26f)
        classicsFooter.setDrawableSize(18f)
        classicsFooter.setAccentColorId(R.color.white)
        mBinding.refreshLayout.setRefreshFooter(classicsFooter)

        mBinding.refreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshlayout: RefreshLayout) {
                pageNumber = 1
                getDoneInventoryPlan(true)
            }
        })
        mBinding.refreshLayout.setOnLoadMoreListener { refreshlayout ->
            pageNumber++
            getDoneInventoryPlan(false)
        }

        // 正在获取盘库结果列表
        mProgressDialog.setMessage("正在获取盘库结果列表...")
        mProgressDialog.show()
        loadListHandler.postDelayed(loadListRunnable, 1000)

        return mBinding.root
    }

    private var pageNumber = 1

    private val loadListHandler = Handler()
    private val loadListRunnable = Runnable {
        run {
            pageNumber = 1
            getDoneInventoryPlan(true)
        }
    }

    private lateinit var mAdapter: PDADoneInventoryPlanAdapter
    private var mList = ArrayList<ResultGetDoneInventoryPlan.DataBean.RowsBean>()

    private fun initAdapter() {
        mAdapter = PDADoneInventoryPlanAdapter(requireActivity(), mList)
        mBinding.listView.adapter = mAdapter
    }

    /**
     * 获取已生成盘库结果的盘库计划信息(分页请求)
     */
    private fun getDoneInventoryPlan(isRefresh: Boolean) {
        val requestUrl =
            NetworkRequest.instance.metDoneInventoryPlan + "?pageSize=10&pageNum=" + pageNumber
        LogUtils.e("获取已生成盘库结果的盘库计划信息-requestUrl:$requestUrl")

        val jsonObjectRequest = JsonObjectRequestWithHeader(
            Request.Method.GET, requestUrl, { response ->
                LogUtils.e("获取已生成盘库结果的盘库计划信息-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val result: ResultGetDoneInventoryPlan =
                                JSON.parseObject(
                                    "$response",
                                    ResultGetDoneInventoryPlan::class.java
                                )

                            val data = result.data
                            if (data != null) {
                                val dataList = data.rows
                                if (dataList != null && dataList.size > 0) {
                                    if (isRefresh) {
                                        mList =
                                            dataList as ArrayList<ResultGetDoneInventoryPlan.DataBean.RowsBean>
                                        mBinding.refreshLayout.finishRefresh(1000 /*,false*/) // 传入false表示刷新失败
                                    } else {
                                        mList.addAll(dataList)
                                        mBinding.refreshLayout.finishLoadMore(1000 /*,false*/) // 传入false表示加载失败
                                    }

                                    mAdapter.setList(mList)
                                    mAdapter.notifyDataSetChanged()
                                    mBinding.llNoData.visibility = GONE
                                    mProgressDialog.dismiss()
                                } else {
                                    mProgressDialog.dismiss()

                                    if (isRefresh) {
                                        // 这里要设置一张无数据的空图片
                                        showWarningToast("暂无盘库结果数据")
                                        mBinding.llNoData.visibility = VISIBLE
                                    } else {
                                        mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                                    }
                                }
                            }

                        } else {
                            showWarningToast(response.getString("msg"))
                            mProgressDialog.dismiss()
                            if (isRefresh) {
                                mBinding.refreshLayout.finishRefresh(false) // 传入false表示刷新失败
                            } else {
                                pageNumber--
                                mBinding.refreshLayout.finishLoadMore(false) // 传入false表示加载失败
                            }
                        }
                    } else {
                        showWarningToast("获取已生成盘库结果的盘库计划信息-请求失败")
                        mProgressDialog.dismiss()
                        if (isRefresh) {
                            mBinding.refreshLayout.finishRefresh(false) // 传入false表示刷新失败
                        } else {
                            pageNumber--
                            mBinding.refreshLayout.finishLoadMore(false) // 传入false表示加载失败
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showErrorToast("获取已生成盘库结果的盘库计划信息-请求失败")
                    mProgressDialog.dismiss()
                    if (isRefresh) {
                        mBinding.refreshLayout.finishRefresh(false) // 传入false表示刷新失败
                    } else {
                        pageNumber--
                        mBinding.refreshLayout.finishLoadMore(false) // 传入false表示加载失败
                    }
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
                mProgressDialog.dismiss()
                if (isRefresh) {
                    mBinding.refreshLayout.finishRefresh(false) // 传入false表示刷新失败
                } else {
                    pageNumber--
                    mBinding.refreshLayout.finishLoadMore(false) // 传入false表示加载失败
                }
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
                mBinding.llNoData.visibility = View.GONE
                mProgressDialog.setMessage("正在获取盘库结果列表...")
                mProgressDialog.show()
                loadListHandler.postDelayed(loadListRunnable, 1000)
            }
        }
    }

    // ListView的点击事件
    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val dataEntity = mList[p2]
        // 打开盘库差异详情界面
        startActivity(
            PDAInventoryDifferenceDetailsActivity.newIntent(
                requireActivity(),
                dataEntity.id
            )
        )
    }

}