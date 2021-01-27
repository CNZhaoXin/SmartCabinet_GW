package com.zk.cabinet.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
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
import com.zk.cabinet.adapter.PDAInventoryDifferenceDetailsAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.databinding.ActicityPdaInventoryDifferenceDetailsBinding
import com.zk.cabinet.entity.ResultPDAInventoryDifference
import com.zk.cabinet.net.JsonObjectRequestWithHeader
import com.zk.cabinet.net.NetworkRequest
import org.json.JSONException
import java.lang.ref.WeakReference

// 传递进来的盘点计划ID
private const val PLAN_ID = "PlanID"

/**
 * PDA-盘库差异详情
 */
class PDAInventoryDifferenceDetailsActivity : TimeOffAppCompatActivity(),
    AdapterView.OnItemClickListener,
    View.OnClickListener {
    private lateinit var mBinding: ActicityPdaInventoryDifferenceDetailsBinding
    private lateinit var mHandler: MyHandler
    private lateinit var mProgressDialog: ProgressDialog

    companion object {
        fun newIntent(
            packageContext: Context,
            planId: String,
        ): Intent {
            val intent = Intent(packageContext, PDAInventoryDifferenceDetailsActivity::class.java)
            intent.putExtra(PLAN_ID, planId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.acticity_pda_inventory_difference_details)
        mBinding.onClickListener = this
        mBinding.onItemClickListener = this
        mHandler = MyHandler(this)

        // 开启界面自动关闭
        isAutoFinish = true
        timerStart()

        // 初始化ProcessDialog
        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)

        // 初始化 下拉刷新,上拉加载
        val classicsHeader = ClassicsHeader(this)
        classicsHeader.setTextSizeTitle(26f)
        classicsHeader.setTextSizeTime(26f)
        classicsHeader.setDrawableSize(22f)
        classicsHeader.setAccentColorId(R.color.white)
        mBinding.refreshLayout.setRefreshHeader(classicsHeader)

        val classicsFooter = ClassicsFooter(this)
        classicsFooter.setTextSizeTitle(26f)
        classicsFooter.setDrawableSize(18f)
        classicsFooter.setAccentColorId(R.color.white)
        mBinding.refreshLayout.setRefreshFooter(classicsFooter)

        mBinding.refreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshlayout: RefreshLayout) {
                pageNumber = 1
                getInventoryDifferenceByPlanId(true)
            }
        })
        mBinding.refreshLayout.setOnLoadMoreListener { refreshlayout ->
            pageNumber++
            getInventoryDifferenceByPlanId(false)
        }

        // 初始化列表数据
        initAdapter()
        mProgressDialog.setMessage("正在获取盘库差异列表...")
        mProgressDialog.show()
        loadListHandler.postDelayed(loadListRunnable, 1000)
    }

    private var pageNumber = 1

    private val loadListHandler = Handler()
    private val loadListRunnable = Runnable {
        run {
            pageNumber = 1
            getInventoryDifferenceByPlanId(true)
        }
    }

    private lateinit var mAdapter: PDAInventoryDifferenceDetailsAdapter
    private var mList = ArrayList<ResultPDAInventoryDifference.DataBean.RowsBean>()

    private fun initAdapter() {
        mAdapter = PDAInventoryDifferenceDetailsAdapter(this, mList)
        mBinding.listView.adapter = mAdapter
    }

    /**
     *  18. 根据盘库计划id获取盘库差异信息
     *  接口地址：get /api/pad/getInventoryDifferenceByPlanId
     */
    private fun getInventoryDifferenceByPlanId(isRefresh: Boolean) {
        val requestUrl =
            NetworkRequest.instance.mGetInventoryDifferenceByPlanId + "?pageSize=10&pageNum=" + pageNumber + "&planId=" + intent.getStringExtra(
                PLAN_ID
            )
        LogUtils.e("根据盘库计划id获取盘库差异信息-requestUrl:$requestUrl")

        val jsonObjectRequest = JsonObjectRequestWithHeader(
            Request.Method.GET, requestUrl, { response ->
                LogUtils.e("根据盘库计划id获取盘库差异信息-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val result: ResultPDAInventoryDifference =
                                JSON.parseObject(
                                    "$response",
                                    ResultPDAInventoryDifference::class.java
                                )

                            val data = result.data
                            if (data != null) {
                                val dataList = data.rows
                                if (dataList != null && dataList.size > 0) {
                                    if (isRefresh) {
                                        mList =
                                            dataList as ArrayList<ResultPDAInventoryDifference.DataBean.RowsBean>
                                        mBinding.refreshLayout.finishRefresh(1000 /*,false*/) // 传入false表示刷新失败
                                    } else {
                                        mList.addAll(dataList)
                                        mBinding.refreshLayout.finishLoadMore(1000 /*,false*/) // 传入false表示加载失败
                                    }

                                    mAdapter.setList(mList)
                                    mAdapter.notifyDataSetChanged()
                                    mBinding.llNoData.visibility = View.GONE
                                    mProgressDialog.dismiss()
                                } else {
                                    mProgressDialog.dismiss()

                                    if (isRefresh) {
                                        // 这里要设置一张无数据的空图片
                                        showWarningToast("暂无盘库差异数据")
                                        mBinding.llNoData.visibility = View.VISIBLE
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
                        showWarningToast("根据盘库计划id获取盘库差异信息-请求失败")
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
                    showErrorToast("根据盘库计划id获取盘库差异信息-请求失败")
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
                mProgressDialog.setMessage("正在获取盘库差异列表...")
                mProgressDialog.show()
                loadListHandler.postDelayed(loadListRunnable, 1000)
            }
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
        }
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mBinding.tvCountdown.text = millisUntilFinished.toString()
    }

    private fun sendDelayMessage(msgWhat: Int, msgObj: String) {
        val message = Message.obtain()
        message.what = msgWhat
        message.obj = msgObj
        mHandler.sendMessageDelayed(message, 1000)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    private class MyHandler(activity: PDAInventoryDifferenceDetailsActivity) :
        Handler() {
        private val weakReference = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            weakReference.get()!!.handleMessage(msg)
        }
    }

}