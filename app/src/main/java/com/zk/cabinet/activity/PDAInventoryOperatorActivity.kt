package com.zk.cabinet.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.LogUtils
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.databinding.ActivityPdaInventoryOperatorBinding
import com.zk.cabinet.entity.ResultGetNoStartInventoryPlan
import com.zk.cabinet.fragment.PDAInventoryFragment
import com.zk.cabinet.pdauhf.PDAUhfHelper
import com.zk.cabinet.view.titleView.ScaleTransitionPagerTitleView
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

/**
 * PDA-盘库-操作
 */
class PDAInventoryOperatorActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityPdaInventoryOperatorBinding

    companion object {
        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, PDAInventoryOperatorActivity::class.java)
        }
    }

    lateinit var mBroadcastManager: LocalBroadcastManager
    lateinit var mReceiver: BroadcastReceiver

    private lateinit var data: ResultGetNoStartInventoryPlan.DataBean
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pda_inventory_operator)
        mBinding.onClickListener = this
        // 开启自动倒计时
        isAutoFinish = true
        timerStart()

        // 收到的数据
        data = intent.getSerializableExtra("entity") as ResultGetNoStartInventoryPlan.DataBean
        LogUtils.e("收到的数据：" + JSON.toJSONString(data))
        val cabinetBeanList = data.cabineBeanList
        initViewPager(cabinetBeanList)

        // 初始化广播
        mBroadcastManager = LocalBroadcastManager.getInstance(this)

        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.STOP_INVENTORY")
        intentFilter.addAction("android.intent.action.START_INVENTORY")

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent!!.action == "android.intent.action.STOP_INVENTORY") {
                    isAutoFinish = false
                    timerCancel()
                } else if (intent!!.action == "android.intent.action.START_INVENTORY") {
                    isAutoFinish = true
                    timerStart()
                }
            }
        }
        mBroadcastManager.registerReceiver(mReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBroadcastManager.unregisterReceiver(mReceiver)
    }

    private fun initViewPager(dataList: List<ResultGetNoStartInventoryPlan.DataBean.CabineBeanListBean>) {
        val fragmentList: java.util.ArrayList<Fragment> = java.util.ArrayList<Fragment>()
        val titleList = ArrayList<String>()

        for (entity in dataList) {
            titleList.add(entity.attributeName)
            //  给每个Fragment传递数据
            fragmentList.add(PDAInventoryFragment.newInstance(data.id, entity))
        }

        // 这个地方要注意，如果是在fragment中添加fragment需要getChildFragmentManager(),Activity中getSupportFragmentManager()
        val fragmentPagerAdapter: FragmentPagerAdapter = object : FragmentPagerAdapter(
            supportFragmentManager
        ) {
            override fun getCount(): Int {
                return fragmentList.size
            }

            override fun getItem(position: Int): Fragment {
                return fragmentList[position]
            }
        }

        mBinding.viewPager.offscreenPageLimit = fragmentList.size - 1 // 将所有Fragment一次性预加载好
        mBinding.viewPager.adapter = fragmentPagerAdapter
        initIndicator(titleList)
    }

    private fun initIndicator(titleList: ArrayList<String>) {
        mBinding.magicIndicator.setBackgroundColor(resources.getColor(R.color.white))

        val commonNavigator = CommonNavigator(this)
        commonNavigator.isAdjustMode = false // true,占满整条,false,wrapContent
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return titleList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                // Text颜色可切换,并带颜色渐变和缩放效果的指示器标题
                val titleView = ScaleTransitionPagerTitleView(context)
                // Text颜色可切换,没有颜色渐变和缩放效果的指示器标题
                // ColorFlipPagerTitleView titleView = new ColorFlipPagerTitleView(context);
                titleView.text = titleList[index]
                titleView.normalColor = resources.getColor(R.color.black)
                titleView.selectedColor = resources.getColor(R.color.colorPrimary)
                titleView.textSize = 28f
                titleView.setOnClickListener(View.OnClickListener {
                    mBinding.viewPager.currentItem = index
                })
                return titleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                indicator.lineHeight = UIUtil.dip2px(context, 2.5).toFloat()
                indicator.lineWidth = UIUtil.dip2px(context, 10.0).toFloat()
                indicator.roundRadius = UIUtil.dip2px(context, 2.0).toFloat()
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(resources.getColor(R.color.colorPrimary))
                return indicator
            }
        }
        mBinding.magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(mBinding.magicIndicator, mBinding.viewPager)
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mBinding.tvCountdown.text = millisUntilFinished.toString()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> {
                PDAUhfHelper.getInstance().release()
                finish()
            }
        }
    }

}