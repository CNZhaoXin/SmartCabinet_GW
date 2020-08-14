package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.zk.cabinet.R
import com.zk.cabinet.adapter.MainMenuAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.MainMenuInfo
import com.zk.cabinet.databinding.ActivityMainMenuBinding
import com.zk.cabinet.utils.SharedPreferencesUtil

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
        mMainMenuBinding.onItemClickListener = this

        val name = mSpUtil.getString(SharedPreferencesUtil.Key.NameTemp, "xxx")
        mMainMenuBinding.tvOperator.text = name

        init()
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mMainMenuBinding.mainMenuCountdownTv.text = millisUntilFinished.toString()
    }

    private fun init() {
        mMenuList.add(
            MainMenuInfo(
                R.mipmap.ic_in_storage,
                getString(R.string.warehousing),
                R.drawable.selector_menu_blue
            )
        )
        mMenuList.add(
            MainMenuInfo(
                R.mipmap.ic_out_storage,
                getString(R.string.outbound),
                R.drawable.selector_menu_blue
            )
        )
        mMenuList.add(
            MainMenuInfo(
                R.mipmap.ic_inventory_storage,
                getString(R.string.inventory_storage),
                R.drawable.selector_menu_green
            )
        )
        mMenuList.add(
            MainMenuInfo(
                R.mipmap.ic_fingerprint,
                getString(R.string.personnel_management),
                R.drawable.selector_menu_green
            )
        )
        mMenuList.add(
            MainMenuInfo(
                R.mipmap.ic_back,
                getString(R.string.logout),
                R.drawable.selector_menu_red
            )
        )
        mMenuList.add(
            MainMenuInfo(
                R.mipmap.ic_system_setting,
                getString(R.string.system_settings),
                R.drawable.selector_menu_orange
            )
        )

        mMenuAdapter = MainMenuAdapter(this, mMenuList)
        mMainMenuBinding.mainMenuGv.adapter = mMenuAdapter
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (mMenuList[position].mImage) {
            R.mipmap.ic_in_storage -> {
                intentActivity(WarehousingActivity.newIntent(this))
            }
            R.mipmap.ic_out_storage -> {
                intentActivity(OutboundActivity.newIntent(this))
            }
            R.mipmap.ic_inventory_storage -> {
                intentActivity(
                    DemoInterfaceActivity.newIntent(
                        packageContext = this,
                        isAutomatic = false
                    )
                )
            }
            R.mipmap.ic_fingerprint -> {
                intentActivity(
                    Intent(
                        this@MainMenuActivity,
                        PersonnelManagementActivity::class.java
                    )
                )
            }
            R.mipmap.ic_system_setting -> {
                intentActivity(SystemSettingsActivity.newIntent(this))
            }
            R.mipmap.ic_back -> {
                finish()
            }

        }
    }
}
