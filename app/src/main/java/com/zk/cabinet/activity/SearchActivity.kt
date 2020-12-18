package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.blankj.utilcode.util.LogUtils
import com.zk.cabinet.R
import com.zk.cabinet.adapter.FileDetailsAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.databinding.ActivitySearchBinding
import com.zk.cabinet.entity.FileDetailsData
import com.zk.cabinet.entity.ResultSearchArchivesInfo
import com.zk.cabinet.net.NetworkRequest
import org.json.JSONException

/**
 * 档案搜索界面
 */
class SearchActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivitySearchBinding
    private lateinit var mFileDetailsAdapter: FileDetailsAdapter
    private var searchList = ArrayList<FileDetailsData>()

    companion object {
        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, SearchActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        mBinding.onClickListener = this

        // 初始化SearchView
        initSearchView()
    }

    private fun initSearchView() {
        // 修改SearchView字体大小
        val editText = mBinding.searchView.findViewById<EditText>(R.id.search_src_text)
        editText.textSize = 20f

        mFileDetailsAdapter = FileDetailsAdapter(this, searchList)
        mBinding.listView.adapter = mFileDetailsAdapter

        mBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(queryText: String?): Boolean {
                if (queryText != null && !TextUtils.isEmpty(queryText)) {
                    searchFile(queryText)
                } else {
                    searchList.clear()
                    mFileDetailsAdapter.setList(searchList)
                    mFileDetailsAdapter.notifyDataSetChanged()
                }
                return true
            }

            override fun onQueryTextSubmit(queryText: String?): Boolean {
                if (queryText != null && !TextUtils.isEmpty(queryText)) {
                    searchFile(queryText)
                } else {
                    searchList.clear()
                    mFileDetailsAdapter.setList(searchList)
                    mFileDetailsAdapter.notifyDataSetChanged()
                }
                return true
            }
        })

        mBinding.listView.setOnItemClickListener { adapterView, view, position, l ->
            // 点击查看档案详情
        }
    }

    /**
     * 关键字搜索档案并显示结果
     */
    private fun searchFile(searchContent: String) {
        /*   pageNum int 每页数量，默认20
             pageSize int 页码，第一页为 1，默认为1
             startDate_q Date 入库查询开始时间
             endDate_q Date 入库查询结束时间
             houseId string 所属档案室id
             archivesId string 所属档案柜
             searchContent string搜索内容,可模糊查询档案名称、关键字
             rfid string 档案 rfid
             archivesStatus int 档案状态*/

        val requestUrl =
            NetworkRequest.instance.mSearchArchivesInfo + "?pageNum=1&pageSize=100&searchContent=" + searchContent
        LogUtils.e("搜索-请求参数:", requestUrl)

        //  不做分页,最多显示100条搜索结果
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            requestUrl,
            { response ->
                LogUtils.e("搜索-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {

                            val resultSearchArchivesInfo: ResultSearchArchivesInfo =
                                JSON.parseObject(
                                    "$response",
                                    ResultSearchArchivesInfo::class.java
                                )

                            val dataList = resultSearchArchivesInfo.data.rows
                            if (dataList != null && dataList.size > 0) {
                                mFileDetailsAdapter.setList(dataList)
                                mFileDetailsAdapter.notifyDataSetChanged()
                            } else {
                                showWarningToast("暂无搜索结果")
                            }

                        } else {
                            showWarningToast(response.getString("msg"))
                        }
                    } else {
                        showWarningToast("暂无搜索结果")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showErrorToast("暂无搜索结果")
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


    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mBinding.tvCountdown.text = millisUntilFinished.toString()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> {
                finish()
            }
        }
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            val view = currentFocus //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (isHideKeyboard(view, motionEvent)) { //判断用户点击的是否是输入框以外的区域
                hideKeyboard(view!!.windowToken) //收起键盘
            }
        }
        return super.dispatchTouchEvent(motionEvent)
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     */
    private fun isHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {  //判断得到的焦点控件是否包含EditText
            val origin = intArrayOf(0, 0)
            v.getLocationInWindow(origin)
            val left = origin[0]
            //得到输入框在屏幕中上下左右的位置
            val top = origin[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        // 如果焦点不是EditText则忽略
        return false
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     */
    private fun hideKeyboard(token: IBinder?) {
        if (token != null) {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                token, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

}