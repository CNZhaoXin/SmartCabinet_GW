package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.databinding.ActivityDemoInterfaceBinding

class DemoInterfaceActivity : TimeOffAppCompatActivity() {
    private lateinit var mDemoInterfaceBinding: ActivityDemoInterfaceBinding

    companion object {
        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, DemoInterfaceActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDemoInterfaceBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo_interface)
        setSupportActionBar(mDemoInterfaceBinding.demoInterfaceToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mDemoInterfaceBinding.demoInterfaceCountdownTv.text = millisUntilFinished.toString()
    }
}