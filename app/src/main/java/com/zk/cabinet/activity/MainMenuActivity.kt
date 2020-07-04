package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.zk.cabinet.R
import com.zk.cabinet.activity.SystemSettingsActivity
import com.zk.cabinet.adapter.MainMenuAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.MainMenuInfo
import com.zk.cabinet.databinding.ActivityMainMenuBinding

class MainMenuActivity : TimeOffAppCompatActivity(), AdapterView.OnItemClickListener {
    private lateinit var mMainMenuBinding: ActivityMainMenuBinding
    private val mPurview = 0 //【普通用户：0】；【管理员：1】；【配置管理员：2 】
    private val mMenuList: ArrayList<MainMenuInfo> = ArrayList()
    private lateinit var mMenuAdapter: MainMenuAdapter

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, MainMenuActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainMenuBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_menu)
        setSupportActionBar(mMainMenuBinding.mainMenuToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mMainMenuBinding.onItemClickListener = this
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mMainMenuBinding.mainMenuCountdownTv.text = millisUntilFinished.toString()
    }

    private fun init(){
        mMenuList.add(MainMenuInfo(R.drawable.menu_cabinet, getString(R.string.access_operation)))
        mMenuList.add(MainMenuInfo(R.drawable.menu_system_settings, getString(R.string.system_settings)))
        mMenuAdapter = MainMenuAdapter(this, mMenuList)
        mMainMenuBinding.mainMenuGv.adapter = mMenuAdapter

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(mMenuList[position].mImageUrl){
            R.drawable.menu_cabinet ->{
                intentActivity(DemoInterfaceActivity.newIntent(this))
            }
            R.drawable.menu_system_settings ->{
                intentActivity(SystemSettingsActivity.newIntent(this))
            }
        }
    }
}
