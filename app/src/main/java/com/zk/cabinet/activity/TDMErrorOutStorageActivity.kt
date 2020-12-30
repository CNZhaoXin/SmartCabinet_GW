package com.zk.cabinet.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.databinding.DataBindingUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.blankj.utilcode.util.LogUtils
import com.zk.cabinet.R
import com.zk.cabinet.adapter.TDMErrorStorageAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.databinding.ActivityTdmErrorOutStorageBinding
import com.zk.cabinet.entity.ResultMQTTMessageErrorOutStorage
import java.lang.ref.WeakReference

/**
 * 通道门-异常出库界面
 */
class TDMErrorOutStorageActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityTdmErrorOutStorageBinding
    private lateinit var mHandler: MyHandler
    private var mb: BroadcastReceiver? = null

    private lateinit var mAdapter: TDMErrorStorageAdapter
    private var mArchivesList = ArrayList<ResultMQTTMessageErrorOutStorage.Data.ArchivesList>()

    companion object {
        // 传递进来的异常出库档案数据集合
        public const val ARCHIVES_LIST = "ArchivesList"

        fun newIntent(
            packageContext: Context,
            archivesList: String
        ): Intent {
            val intent = Intent(packageContext, TDMErrorOutStorageActivity::class.java)
            intent.putExtra(ARCHIVES_LIST, archivesList)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tdm_error_out_storage)
        mBinding.onClickListener = this
        mHandler = MyHandler(this)
        // 开启自动倒计时
        isAutoFinish = true
        timerStart()

        // 获取MQTT传递过来的异常出库档案数据
        val archivesListJsonArray = JSONArray.parseArray(intent.getStringExtra(ARCHIVES_LIST))
        mArchivesList =
            archivesListJsonArray.toJavaList(ResultMQTTMessageErrorOutStorage.Data.ArchivesList::class.java) as ArrayList<ResultMQTTMessageErrorOutStorage.Data.ArchivesList>

        mAdapter = TDMErrorStorageAdapter(this, mArchivesList)
        mBinding.listView.adapter = mAdapter

        mAdapter.setList(mArchivesList)
        mAdapter.notifyDataSetChanged()

        mBinding.tvNum.text = mArchivesList.size.toString()

        LogUtils.e(
            "通道门-异常出库",
            "archivesList",
            JSON.toJSONString(mArchivesList)
        )

        // 注册广播接收者
        mb = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val archivesList = JSONArray.parseArray(intent.getStringExtra(ARCHIVES_LIST))
                val newArchivesList =
                    archivesList.toJavaList(ResultMQTTMessageErrorOutStorage.Data.ArchivesList::class.java) as ArrayList<ResultMQTTMessageErrorOutStorage.Data.ArchivesList>

                val oldSize = mArchivesList.size
                // 去重
                for (newArchive in newArchivesList) {
                    var isAdd = false

                    for (oldArchive in mArchivesList) {
                        if (oldArchive.archivesCode == newArchive.archivesCode) {
                            isAdd = false
                            break
                        } else {
                            isAdd = true
                        }
                    }

                    if (isAdd) {
                        mArchivesList.add(newArchive)
                    }

                }

                if (oldSize != mArchivesList.size) {
                    mAdapter.setList(mArchivesList)
                    mAdapter.notifyDataSetChanged()
                    mBinding.tvNum.text = mArchivesList.size.toString()
                }

                LogUtils.e(
                    "通道门-异常出库-接收到广播",
                    "archivesList",
                    JSON.toJSONString(mArchivesList)
                )

                // 重新计时
                timerCancel()
                timerStart()
            }
        }
        val mif = IntentFilter("ACTION_TDM_ERROR_OUT_STORAGE")
        registerReceiver(mb, mif)
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mBinding.outboundOperatingCountdownTv.text = millisUntilFinished.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 注销广播
        unregisterReceiver(mb);
    }

    private class MyHandler(t: TDMErrorOutStorageActivity) : Handler() {
        private val mainWeakReference = WeakReference(t)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mainWeakReference.get()!!.handleMessage(msg)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
//            SUBMIT_SUCCESS -> {
//            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }

    private fun sendDelayMessage(msgWhat: Int, msgObj: String) {
        val message = Message.obtain()
        message.what = msgWhat
        message.obj = msgObj
        mHandler.sendMessageDelayed(message, 1000)
    }
}