package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.databinding.ActivitySkqSerialDebugBinding
import com.zk.cabinet.helper.CardSerialPortHelper
import com.zk.cabinet.utils.SharedPreferencesUtil
import java.lang.ref.WeakReference

/**
 * 刷卡器测试界面
 */
class SKQSerialDebugActivity : TimeOffAppCompatActivity(), AdapterView.OnItemClickListener,
    View.OnClickListener {
    private lateinit var mBinding: ActivitySkqSerialDebugBinding
    private lateinit var mHandler: MyHandler

    companion object {
        private const val DEVICE_REGISTERED = 0x01

        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, SKQSerialDebugActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_skq_serial_debug)
        mBinding.onClickListener = this
        mBinding.onItemClickListener = this
        mHandler = MyHandler(this)

        // 关闭自动倒计时
        isAutoFinish = false

        // 显示选择的读写器串口
        val port = mSpUtil.getString(SharedPreferencesUtil.Key.SKQSerialSelected, "")
        mBinding.tvSerialPort.text = port

        CardSerialPortHelper.getInstance().open(port)
        CardSerialPortHelper.getInstance().setDataReceivedListener { data ->
            Utils.runOnUiThread {
                LogUtils.e("解析后卡号数据:$data")
                mBinding.tvScanCardId.text = data
            }
        }

    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
//            DEVICE_REGISTERED -> {
//            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> {
                finish()
            }
        }
    }

    private fun sendDelayMessage(msgWhat: Int, msgObj: String) {
        val message = Message.obtain()
        message.what = msgWhat
        message.obj = msgObj
        mHandler.sendMessageDelayed(message, 1000)
    }

    private class MyHandler(activity: SKQSerialDebugActivity) :
        Handler() {
        private val weakReference = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            weakReference.get()!!.handleMessage(msg)
        }
    }

    override fun onDestroy() {
        CardSerialPortHelper.getInstance().close()
        super.onDestroy()
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
    }
}