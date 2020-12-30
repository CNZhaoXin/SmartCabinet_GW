package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.databinding.ActivityDagLightDebugBinding
import com.zk.cabinet.fragment.LightDebugFragment
import com.zk.cabinet.helper.LightsSerialPortHelper
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.cabinet.view.titleView.ScaleTransitionPagerTitleView
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

/**
 * 档案组柜-灯控调试界面
 */
class DAGLightDebugActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityDagLightDebugBinding

    companion object {
        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, DAGLightDebugActivity::class.java)
        }
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mBinding.tvCountdown.text = millisUntilFinished.toString()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            // 档案组柜亮组大灯和档案组架一样
            R.id.btn_open_group_big_light -> {
                LightsSerialPortHelper.getInstance().openGroupBigLight()
            }

            // 档案组柜灭组大灯和档案组架一样
            R.id.btn_close_group_big_light -> {
                LightsSerialPortHelper.getInstance().closeGroupBigLight()
            }

            R.id.btn_back -> {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dag_light_debug)
        mBinding.onClickListener = this

        // 关闭自动倒计时
        isAutoFinish = false

        // 先关闭串口再重新打开
        LightsSerialPortHelper.getInstance().close()
        val port = mSpUtil.getString(SharedPreferencesUtil.Key.LightsSerialSelected, "")
        LightsSerialPortHelper.getInstance().open(port)
        mBinding.tvSerialPort.text = port

//      val deviceList = DeviceService.getInstance().loadAll()
//      initViewPager(deviceList as ArrayList<Device>)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 关闭串口
        LightsSerialPortHelper.getInstance().close()
    }

    private fun initViewPager(deviceList: ArrayList<Device>) {
        val fragmentList: java.util.ArrayList<Fragment> = java.util.ArrayList<Fragment>()
        var titleList = ArrayList<String>()

        for (entity in deviceList) {
            titleList.add(entity.deviceName)
            // 给每个Fragment传递数据
            fragmentList.add(LightDebugFragment.newInstance(entity.deviceId))
        }

        // 这个地方要注意，如果是在fragment中添加fragment需要getChildFragmentManager(),Activity中添加fragment需要getSupportFragmentManager()
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
}

