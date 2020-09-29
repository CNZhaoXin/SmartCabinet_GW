package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.zk.cabinet.R
import com.zk.cabinet.adapter.QueryAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.DossierOperating
import com.zk.cabinet.databinding.ActivityQueryBinding
import com.zk.cabinet.db.DossierOperatingService
import com.zk.cabinet.utils.SharedPreferencesUtil
import java.lang.ref.WeakReference


class QueryActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mOutboundBinding: ActivityQueryBinding
    private lateinit var mHandler: OutboundOperatingHandler
    private lateinit var mDossierAdapter: QueryAdapter
    private var queryList = ArrayList<DossierOperating>()

    companion object {
        private const val OPEN_DOOR_RESULT = 0x01

        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, QueryActivity::class.java)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOutboundBinding = DataBindingUtil.setContentView(this, R.layout.activity_query)
        mOutboundBinding.onClickListener = this

        mHandler = OutboundOperatingHandler(this)

        val name = mSpUtil.getString(SharedPreferencesUtil.Key.NameTemp, "xxx")
        mOutboundBinding.tvOperator.text = name

        val editText = mOutboundBinding.searchView.findViewById<EditText>(R.id.search_src_text)
        editText.textSize = 30f

        mDossierAdapter = QueryAdapter(this, queryList)
        mOutboundBinding.listView.adapter = mDossierAdapter

        mOutboundBinding.listView.setOnItemClickListener { adapterView, view, position, l ->
            var dossierOperating = queryList[position]
            dossierOperating.selected = !dossierOperating.selected

            // 改数据库中的数据状态
            // showToast("" + dossierOperating.inputName + "," + dossierOperating.selected)
            DossierOperatingService.getInstance().update(dossierOperating)

            if (dossierOperating.selected) {
                mOutboundBinding.btnQudang.background =
                    resources.getDrawable(R.drawable.selector_menu_green)
                mOutboundBinding.btnQudang.isEnabled = true
            } else {
                var hasSelect = false
                for (dossierOperating in queryList) {
                    if (dossierOperating.selected) {
                        hasSelect = true
                        break
                    } else {
                        hasSelect = false
                    }
                }

                if (hasSelect) {
                    mOutboundBinding.btnQudang.background =
                        resources.getDrawable(R.drawable.selector_menu_green)
                    mOutboundBinding.btnQudang.isEnabled = true
                } else {
                    mOutboundBinding.btnQudang.background =
                        resources.getDrawable(R.drawable.shape_btn_un_enable)
                    mOutboundBinding.btnQudang.isEnabled = false
                }
            }

            mDossierAdapter.notifyDataSetChanged()
        }

        mOutboundBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(queryText: String?): Boolean {
                if (queryText != null && !TextUtils.isEmpty(queryText)) {
                    queryFile(queryText)
                }
                return true
            }

            override fun onQueryTextSubmit(queryText: String?): Boolean {
                if (queryText != null && !TextUtils.isEmpty(queryText)) {
                    queryFile(queryText)
                }
                return true
            }
        })

    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        if (motionEvent.getAction() === MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
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
            return if (event.x > left && event.x < right && event.y > top && event.y < bottom
            ) {
                // 点击位置如果是EditText的区域，忽略它，不收起键盘。
                false
            } else {
                true
            }
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

    // todo 查询档案并显示查询结果
    private fun queryFile(queryText: String) {
        // todo 每次查档,先把之前选择的改为false
        val selectList = DossierOperatingService.getInstance().queryBySelected()
        for (select in selectList) {
            select.selected = false
            DossierOperatingService.getInstance().update(select)
        }

        val list = DossierOperatingService.getInstance()
            .queryByWarrantNum(queryText) as ArrayList<DossierOperating>
        queryList.clear()

        for (dossier in list) {
            if (dossier.operatingType == 1)
                queryList.add(dossier)
        }

//        showToast("" + queryList.size)

        mDossierAdapter.setList(queryList)
        mDossierAdapter.notifyDataSetChanged()
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mOutboundBinding.tvCountdown.text = millisUntilFinished.toString()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // todo 取档
            R.id.btn_qudang -> {
                intentActivity(OutboundOperatingActivity.newIntent(this))
            }

            R.id.btn_back -> {
                finish()
            }
        }
    }

    private class OutboundOperatingHandler(queryActivity: QueryActivity) :
        Handler() {
        private val weakReference = WeakReference(queryActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            weakReference.get()!!.handleMessage(msg)
        }
    }

    override fun onDestroy() {
        val selectList = DossierOperatingService.getInstance().queryBySelected()
        for (select in selectList) {
            select.selected = false
            DossierOperatingService.getInstance().update(select)
        }
        super.onDestroy()
    }

}