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
 * PDA-档案管理
 */
class PDADossierMenuActivity : TimeOffAppCompatActivity(), AdapterView.OnItemClickListener {
    private lateinit var mBinding: ActivityMainMenuBinding
    private val mMenuList: ArrayList<MainMenuInfo> = ArrayList()
    private lateinit var mMenuAdapter: MainMenuAdapter

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, PDADossierMenuActivity::class.java)
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
                SelfComm.FUNCTION_TYPE[11],
                R.mipmap.ic_return,
                "入 库",
                R.drawable.selector_menu_blue_normal
            )
        )
        mMenuList.add(
            MainMenuInfo(
                SelfComm.FUNCTION_TYPE[12],
                R.mipmap.ic_yiku,
                "移 库",
                R.drawable.selector_menu_green_normal
            )
        )
        mMenuList.add(
            MainMenuInfo(
                SelfComm.FUNCTION_TYPE[13],
                R.mipmap.ic_inventory,
                "盘 库",
                R.drawable.selector_menu_orange_normal
            )
        )

        // 初始化title
        initShimmerTitlePDA()

        mMenuAdapter = MainMenuAdapter(this, mMenuList)
        mBinding.mainMenuGv.adapter = mMenuAdapter
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (mMenuList[position].mFunctionType) {
            // PDA-入库
            SelfComm.FUNCTION_TYPE[11] -> {
                intentActivity(PDAInStorageActivity.newIntent(this))
            }
            // PDA-移库
            SelfComm.FUNCTION_TYPE[12] -> {
                intentActivity(PDAMoveStorageActivity.newIntent(this))
            }
            // PDA-盘库
            SelfComm.FUNCTION_TYPE[13] -> {
                intentActivity(PDAInventoryActivity.newIntent(this))
            }
        }
    }
}
