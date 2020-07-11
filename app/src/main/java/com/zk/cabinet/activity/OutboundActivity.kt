package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.databinding.ActivityOutboundBinding

class OutboundActivity : TimeOffAppCompatActivity() {
    private lateinit var mOutboundBinding: ActivityOutboundBinding

    companion object {
        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, OutboundActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOutboundBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_outbound)
        setSupportActionBar(mOutboundBinding.outboundToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mOutboundBinding.outboundCountdownTv.text = millisUntilFinished.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}