package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.databinding.ActivityWarehousingOperatingBinding
import java.lang.ref.WeakReference

class WarehousingOperatingActivity : TimeOffAppCompatActivity() {
    private lateinit var mWarehousingBinding: ActivityWarehousingOperatingBinding
    private lateinit var mHandler: WarehousingOperatingHandler

    companion object {
        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, WarehousingOperatingActivity::class.java)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWarehousingBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_warehousing_operating)
        setSupportActionBar(mWarehousingBinding.warehousingOperatingToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mHandler = WarehousingOperatingHandler(this)

    }


    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mWarehousingBinding.warehousingOperatingCountdownTv.text = millisUntilFinished.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private class WarehousingOperatingHandler(warehousingOperatingActivity: WarehousingOperatingActivity) : Handler() {
        private val warehousingOperatingWeakReference = WeakReference(warehousingOperatingActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            warehousingOperatingWeakReference.get()!!.handleMessage(msg)
        }
    }

}