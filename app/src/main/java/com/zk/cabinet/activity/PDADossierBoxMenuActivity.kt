package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.romainpiel.shimmer.Shimmer
import com.romainpiel.shimmer.ShimmerTextView
import com.zk.cabinet.R
import com.zk.cabinet.adapter.MainMenuAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.constant.SelfComm
import com.zk.cabinet.databinding.ActivityMainMenuBinding
import com.zk.cabinet.entity.MainMenuInfo
import com.zk.cabinet.utils.SharedPreferencesUtil

/**
 * PDA-档案盒管理
 */
class PDADossierBoxMenuActivity : TimeOffAppCompatActivity(), AdapterView.OnItemClickListener {
    private lateinit var mBinding: ActivityMainMenuBinding
    private val mMenuList: ArrayList<MainMenuInfo> = ArrayList()
    private lateinit var mMenuAdapter: MainMenuAdapter

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, PDADossierBoxMenuActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_menu)
        mBinding.onItemClickListener = this

        val name = mSpUtil.getString(SharedPreferencesUtil.Key.NameTemp, "xxx")
        mBinding.tvOperator.text = name

        // 初始化功能菜单
        initFunctionMenu()
    }

    private fun initShimmerTitlePDA() {
        // 设置字体
        val tf = Typeface.createFromAsset(assets, "fonts/OpenSans-ExtraBold.ttf")
        mBinding.stvChineseTitle.typeface = tf
        mBinding.stvEnglishTitle.typeface = tf
        // 开启动效
        val shimmer = Shimmer()
        shimmer.duration = 5000
        shimmer.direction = Shimmer.ANIMATION_DIRECTION_LTR
        shimmer.start<ShimmerTextView>(mBinding.stvChineseTitle)
        shimmer.start<ShimmerTextView>(mBinding.stvEnglishTitle)
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mBinding.mainMenuCountdownTv.text = millisUntilFinished.toString()
    }

    private fun initFunctionMenu() {
        // PDA5
        mMenuList.add(
            MainMenuInfo(
                SelfComm.FUNCTION_TYPE[21],
                R.mipmap.ic_menu_bind,
                "绑 定",
                R.drawable.selector_menu_blue_normal
            )
        )
        mMenuList.add(
            MainMenuInfo(
                SelfComm.FUNCTION_TYPE[22],
                R.mipmap.ic_menu_unbind,
                "解 绑",
                R.drawable.selector_menu_green_normal
            )
        )

        // 初始化title
        initShimmerTitlePDA()

        mMenuAdapter = MainMenuAdapter(this, mMenuList)
        mBinding.mainMenuGv.adapter = mMenuAdapter
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (mMenuList[position].mFunctionType) {
            // PDA-档案盒管理-绑定
            SelfComm.FUNCTION_TYPE[21] -> {
                intentActivity(PDABindBoxActivity.newIntent(this))
            }
            // PDA-档案盒管理-解绑
            SelfComm.FUNCTION_TYPE[22] -> {
                intentActivity(PDAUnBindBoxActivity.newIntent(this))
            }
        }
    }
}
