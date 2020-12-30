package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.databinding.ActivityPadInventoryBinding
import com.zk.cabinet.fragment.PDADoneInventoryPlanFragment
import com.zk.cabinet.fragment.PDANoInventoryPlanFragment
import net.lucode.hackware.magicindicator.FragmentContainerHelper
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView
import java.lang.ref.WeakReference

/**
 * PDA-盘库
 */
class PDAInventoryActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityPadInventoryBinding
    private lateinit var mHandler: MyHandler

    companion object {
        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, PDAInventoryActivity::class.java)
        }
    }

    private class MyHandler(activity: PDAInventoryActivity) : Handler() {
        private val mainWeakReference = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mainWeakReference.get()!!.handleMessage(msg)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
//            BIND_SUCCESS -> {
//                mProgressDialog.dismiss()
//                showSuccessToast("${msg.obj}")
//            }
        }
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mBinding.tvCountdown.text = millisUntilFinished.toString()
    }

    override fun onClick(p0: View?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pad_inventory)
        mBinding.onClickListener = this
        mHandler = MyHandler(this)

        initData()
    }

    private fun initData() {
        initFragments()
        initMagicIndicator1()
        mFragmentContainerHelper.handlePageSelected(0, false)
        switchPages(0)
    }

    private val CHANNELS = arrayOf("盘库计划", "盘库结果")
    private val mFragments: MutableList<Fragment> = ArrayList<Fragment>()
    private val mFragmentContainerHelper = FragmentContainerHelper()

    private fun switchPages(index: Int) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        var fragment: Fragment
        var i = 0
        val j: Int = mFragments.size
        while (i < j) {
            if (i == index) {
                i++
                continue
            }
            fragment = mFragments[i]
            if (fragment.isAdded()) {
                fragmentTransaction.hide(fragment)
            }
            i++
        }
        fragment = mFragments[index]
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment)
        } else {
            fragmentTransaction.add(R.id.fragment_container, fragment)
        }
        fragmentTransaction.commitAllowingStateLoss()
    }

    private fun initFragments() {
        // 盘库计划
        val mPDAInventoryNoStartPlanFragment = PDANoInventoryPlanFragment()
        mFragments.add(mPDAInventoryNoStartPlanFragment)
        // 盘库结果
        val mPDADoneInventoryPlanFragment = PDADoneInventoryPlanFragment()
        mFragments.add(mPDADoneInventoryPlanFragment)
    }

    private fun initMagicIndicator1() {
        val magicIndicator = findViewById<View>(R.id.magic_indicator1) as MagicIndicator
        magicIndicator.setBackgroundResource(R.drawable.round_indicator_bg)
        val commonNavigator = CommonNavigator(this)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return CHANNELS.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val clipPagerTitleView = ClipPagerTitleView(context)
                clipPagerTitleView.text = CHANNELS[index]
                clipPagerTitleView.textSize = 45f
                clipPagerTitleView.textColor = resources.getColor(R.color.gray_light)
                clipPagerTitleView.clipColor = resources.getColor(R.color.white)
                clipPagerTitleView.setOnClickListener {
                    mFragmentContainerHelper.handlePageSelected(index)
                    switchPages(index)
                }
                return clipPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
//                val navigatorHeight = UIUtil.dip2px(context,80.0).toFloat()
//                val borderWidth = UIUtil.dip2px(context, 2.0).toFloat()
                val navigatorHeight = 100f
                val borderWidth = 4f
                val lineHeight = navigatorHeight - 2 * borderWidth
                indicator.lineHeight = lineHeight
                indicator.roundRadius = lineHeight / 2
                indicator.yOffset = borderWidth
                indicator.setColors(resources.getColor(R.color.colorAccent))
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator
        mFragmentContainerHelper.attachMagicIndicator(magicIndicator)
    }
}

