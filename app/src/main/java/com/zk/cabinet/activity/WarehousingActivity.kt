package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.databinding.ActivityWarehousingBinding

class WarehousingActivity : TimeOffAppCompatActivity() {
    private lateinit var mWarehousingBinding: ActivityWarehousingBinding

    companion object {
        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, WarehousingActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWarehousingBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_warehousing)
        setSupportActionBar(mWarehousingBinding.warehousingToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mWarehousingBinding.warehousingCountdownTv.text = millisUntilFinished.toString()
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