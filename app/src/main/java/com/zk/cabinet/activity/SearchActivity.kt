package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
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
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.blankj.utilcode.util.LogUtils
import com.zk.cabinet.R
import com.zk.cabinet.adapter.SearchDossierDetailsAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.databinding.ActivitySearchBinding
import com.zk.cabinet.entity.ResultGetAllCodeType
import com.zk.cabinet.entity.ResultSearchArchivesInfo
import com.zk.cabinet.entity.SearchDossierDetailsData
import com.zk.cabinet.net.NetworkRequest
import org.json.JSONException

/**
 * 档案搜索界面
 */
class SearchActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivitySearchBinding
    private lateinit var mSearchDossierDetailsAdapter: SearchDossierDetailsAdapter
    private var searchList = ArrayList<SearchDossierDetailsData>()
    private var mTypeCode = ""
    private var mTypeName = "所有分类"
    private var mSNum = "" // 流水号
    private var mSearchContent = "" // 关键字内容

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
        // 获取所有档案类型
        getAllCodeType()
    }

    private fun initSearchView() {
        // 修改SearchView字体大小
        val editText = mBinding.searchView.findViewById<EditText>(R.id.search_src_text)
        editText.textSize = 20f

        mSearchDossierDetailsAdapter = SearchDossierDetailsAdapter(this, searchList)
        mBinding.listView.adapter = mSearchDossierDetailsAdapter

        mBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(queryText: String?): Boolean {
                mSearchContent = queryText!!

                if (!TextUtils.isEmpty(queryText)) {
                    searchFile()
                } else {
                    if (mBinding.tvSNum.visibility == View.GONE || TextUtils.isEmpty(mSNum)) {
                        searchList.clear()
                        mSearchDossierDetailsAdapter.setList(searchList)
                        mSearchDossierDetailsAdapter.notifyDataSetChanged()
                    } else {
                        if (!TextUtils.isEmpty(mSNum))
                            searchFile()
                    }
                }
                return true
            }

            override fun onQueryTextSubmit(queryText: String?): Boolean {
                mSearchContent = queryText!!

                if (!TextUtils.isEmpty(queryText)) {
                    searchFile()
                } else {
                    if (mBinding.tvSNum.visibility == View.GONE || TextUtils.isEmpty(mSNum)) {
                        searchList.clear()
                        mSearchDossierDetailsAdapter.setList(searchList)
                        mSearchDossierDetailsAdapter.notifyDataSetChanged()
                    } else {
                        if (!TextUtils.isEmpty(mSNum))
                            searchFile()
                    }
                }
                return true
            }
        })

        mBinding.tvSNum.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                queryText: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                mSNum = queryText.toString()

                if (!TextUtils.isEmpty(queryText.toString())) {
                    searchFile()
                } else {
                    if (TextUtils.isEmpty(mSearchContent)) {
                        searchList.clear()
                        mSearchDossierDetailsAdapter.setList(searchList)
                        mSearchDossierDetailsAdapter.notifyDataSetChanged()
                    } else {
                        searchFile()
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        mBinding.listView.setOnItemClickListener { adapterView, view, position, l ->
            // 点击查看档案详情，不需要这个
        }
    }

    /**
     * 关键字搜索档案并显示结果
     */
    private fun searchFile() {
        /*   pageNum int 每页数量，默认20
             pageSize int 页码，第一页为 1，默认为1
             startDate_q Date 入库查询开始时间
             endDate_q Date 入库查询结束时间
             houseId string 所属档案室id
             archivesId string 所属档案柜
             searchContent string搜索内容,可模糊查询档案名称、关键字
             rfid string 档案 rfid
             archivesStatus int 档案状态
             typeCode 档案类型
          */

        val requestUrl =
            NetworkRequest.instance.mSearchArchivesInfo + "?pageNum=1&pageSize=100&searchContent=" + mSearchContent + "&codeType=" + mTypeCode + "&sNum=" + mSNum
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

                            searchList =
                                resultSearchArchivesInfo.data.rows as ArrayList<SearchDossierDetailsData>
                            mSearchDossierDetailsAdapter.setList(searchList)
                            mSearchDossierDetailsAdapter.notifyDataSetChanged()
                            if (searchList.isEmpty()) {
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

    // 档案类型选择器
    private var optionsDossierTypePickerView: OptionsPickerView<*>? = null

    /**
     * 获取所有档案类型
     */
    private fun getAllCodeType() {
        val requestUrl = NetworkRequest.instance.mGetAllCodeType
        LogUtils.e("获取所有档案类型-请求URL:", "$requestUrl")

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            requestUrl,
            { response ->
                LogUtils.e("获取所有档案类型-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val result: ResultGetAllCodeType =
                                JSON.parseObject(
                                    "$response",
                                    ResultGetAllCodeType::class.java
                                )

                            LogUtils.e("获取所有档案类型-返回结果:", JSON.toJSONString(result))

                            val dataList = result.data
                            if (dataList != null && dataList.size > 0) {
                                val data = dataList[0]
                                val info = data.info
                                val subList = data.subList
                                if (info != null && subList != null && subList.size > 0) {
                                    // 说明是有数据的
                                    // 创建档案类型选择器
                                    createTypePickerView(data)
                                    // 默认选择第一个,选择 会计档案-财务凭证 还需要能输入流水号查询
                                    mTypeName = info.typeName
                                    mBinding.tvType.text = mTypeName
                                    mTypeCode = ""
                                    mBinding.tvSNum.visibility = View.GONE
                                    mBinding.tvSNum.isEnabled = false
                                    mBinding.tvSNum.setText("")
                                    mSNum = ""
                                } else {
                                    // 说明没有数据
                                    showWarningToast("暂无可选档案类型")
                                }
                            } else {
                                showWarningToast("暂无可选档案类型")
                            }
                        } else {
                            showWarningToast(response.getString("msg"))
                        }
                    } else {
                        showWarningToast("获取所有档案类型-失败")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showErrorToast("获取所有档案类型-失败")
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

    val options1Items: ArrayList<ResultGetAllCodeType.Data.SubList.Info> = ArrayList()
    val options2Items: ArrayList<ArrayList<ResultGetAllCodeType.Data.SubList.Info>> = ArrayList()

    /**
     * 创建档案类型选择器
     */
    private fun createTypePickerView(date: ResultGetAllCodeType.Data) {
        optionsDossierTypePickerView = OptionsPickerBuilder(
            this
        ) { options1, options2, options3, v -> //返回的分别是三个级别的选中位置
            if (options1Items.size > 0) {
                mTypeCode = options1Items[options1].typeCode
                mTypeName = options1Items[options1].typeName

                if (options2Items[options1].size > 0
                    && !TextUtils.isEmpty(options2Items[options1][options2].typeName)
                ) {
                    mTypeCode = options2Items[options1][options2].typeCode
                    mTypeName = options2Items[options1][options2].typeName
                }
            }

            mBinding.tvType.text = mTypeName

            // showNormalToast(mTypeName + "（${mTypeCode}）")
            showNormalToast(mTypeName)

            // 这个顺序不要变动，就放在这个位置，不然会有小问题
            if (mTypeCode == "0") {
                mTypeCode = ""
            }

            // 财务凭证 009001
            if (mTypeCode == "009001") {
                mBinding.tvSNum.visibility = View.VISIBLE
                mBinding.tvSNum.isEnabled = true
            } else {
                mBinding.tvSNum.visibility = View.GONE
                mBinding.tvSNum.isEnabled = false
                mBinding.tvSNum.setText("")
                mSNum = ""
            }

            if (!TextUtils.isEmpty(mSearchContent) || !TextUtils.isEmpty(mSNum))
                searchFile()

        }.setOptionsSelectChangeListener { options1, options2, options3 -> }
            .setSubmitText("确定") //确定按钮文字
            .setCancelText("取消") //取消按钮文字
            .setTitleText("档案类型") //标题
            .setSubCalSize(30) //确定和取消文字大小
            .setTitleSize(34) //标题文字大小
            .setContentTextSize(30) //滚轮文字大小
            .setTitleColor(resources.getColor(R.color.gray_deep))//标题文字颜色
            .setSubmitColor(resources.getColor(R.color.md_teal_A400))//确定按钮文字颜色
            .setCancelColor(resources.getColor(R.color.colorDGH))//取消按钮文字颜色
            .setCyclic(false, false, false)// 循环与否
            .setLineSpacingMultiplier(2.5f) // 可通过调整条目的比例，从而影响调整弹窗高度
            // .setTitleBgColor(0xFF333333)//标题背景颜色 Night mode
            // .setBgColor(0xFF000000)//滚轮背景颜色 Night mode
            // .setLinkage(false)//设置是否联动，默认true
            // .setLabels("省", "市", "区")//设置选择的三级单位
            // .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            // .setSelectOptions(0,0,0)  // 设置默认选中项
            // .setOutSideCancelable(true)// 点击外部dismiss default true
            // .isDialog(true)// 是否显示为对话框样式
            // .isRestoreItem(true) // 切换时是否还原，设置默认选中第一项。
            .build<Any>()

        // 只有两级，所有分类选项添加到第一级的第一个选项
        val info1 = date.info
        val infoNew = ResultGetAllCodeType.Data.SubList.Info()
        infoNew.typeName = info1.typeName
        infoNew.typeCode = ""
        options1Items.add(infoNew)
        val arrayEmpty: ArrayList<ResultGetAllCodeType.Data.SubList.Info> = ArrayList()
        val infoEmpty = ResultGetAllCodeType.Data.SubList.Info()
        infoEmpty.typeName = ""
        arrayEmpty.add(infoEmpty)
        options2Items.add(arrayEmpty)
        // 第一级
        val subList = date.subList
        for (i in subList.indices) {
            val dataBean = subList[i]
            if (dataBean != null) {
                val info2 = dataBean.info
                if (info2 != null) {
                    options1Items.add(info2)
                }

                // 第二级
                val subSubList = dataBean.subList
                if (subSubList != null && subSubList.size > 0) {
                    // 如果有第二级，那就加一个头部
                    val arrayList3: ArrayList<ResultGetAllCodeType.Data.SubList.Info> = ArrayList()
                    arrayList3.add(dataBean.info)

                    for (j in subSubList.indices) {
                        val subListBean = subSubList[j]
                        if (subListBean != null) {
                            if (subListBean.info != null) {
                                val info3 = subListBean.info
                                arrayList3.add(info3)
                            }
                        }
                    }

                    options2Items.add(arrayList3)
                } else {
                    // 如果第二级没有选项，加个空选项
                    val arrayEmpty1: ArrayList<ResultGetAllCodeType.Data.SubList.Info> = ArrayList()
                    val infoEmpty1 = ResultGetAllCodeType.Data.SubList.Info()
                    infoEmpty1.typeName = ""
                    arrayEmpty1.add(infoEmpty1)
                    options2Items.add(arrayEmpty1)
                }
            }

        }

        optionsDossierTypePickerView!!.setPicker(
            options1Items as ArrayList<Nothing>,
            options2Items as ArrayList<Nothing>
        )
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

            // 档案类型选择
            R.id.tv_type -> {
                if (optionsDossierTypePickerView == null) {
                    showWarningToast("暂无可选档案类型")
                } else {
                    optionsDossierTypePickerView!!.show()
                }
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