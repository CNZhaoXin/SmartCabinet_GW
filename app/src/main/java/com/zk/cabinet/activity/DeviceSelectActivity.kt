package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.romainpiel.shimmer.Shimmer
import com.romainpiel.shimmer.ShimmerTextView
import com.zk.cabinet.R
import com.zk.cabinet.adapter.DeviceSelectAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.entity.DeviceMenu
import com.zk.cabinet.constant.SelfComm
import com.zk.cabinet.databinding.ActivitySelectDeviceBinding
import com.zk.cabinet.utils.SharedPreferencesUtil

/**
 * 设备选择界面
 */
class DeviceSelectActivity : TimeOffAppCompatActivity(), AdapterView.OnItemClickListener {
    private lateinit var mBinding: ActivitySelectDeviceBinding
    private val mMenuList: ArrayList<DeviceMenu> = ArrayList()
    private lateinit var mDeviceSelectAdapter: DeviceSelectAdapter

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, DeviceSelectActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_device)
        mBinding.onItemClickListener = this

        // 如果选择过了设备,就直接进入主界面
        if (!TextUtils.isEmpty(mSpUtil.getString(SharedPreferencesUtil.Key.DeviceName, ""))) {
            intentActivity(GuideActivity.newIntent(this))
            finish()
        }

        // 界面自动关闭关掉
        isAutoFinish = false
        // 初始化Title
        initShimmerTitle()
        // 初始化设备选择
        initSelectDeviceMenu()
    }

    private fun initShimmerTitle() {
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

    /**
     * 初始化设备选择
     */
    private fun initSelectDeviceMenu() {
        mMenuList.add(
            DeviceMenu(
                SelfComm.DEVICE_NAME[1].toString(),
                R.mipmap.ic_device_file_cabinet_single,
                R.drawable.selector_menu_blue
            )
        )

        mMenuList.add(
            DeviceMenu(
                SelfComm.DEVICE_NAME[2].toString(),
                R.mipmap.ic_device_file_cabinets,
                R.drawable.selector_menu_blue
            )
        )

        mMenuList.add(
            DeviceMenu(
                SelfComm.DEVICE_NAME[3].toString(),
                R.mipmap.ic_device_file_shelf,
                R.drawable.selector_menu_green
            )
        )

        mMenuList.add(
            DeviceMenu(
                SelfComm.DEVICE_NAME[4].toString(),
                R.mipmap.ic_device_yitiji,
                R.drawable.selector_menu_green
            )
        )

        // 手持机
        mMenuList.add(
            DeviceMenu(
                SelfComm.DEVICE_NAME[5].toString(),
                R.mipmap.ic_device_pda,
                R.drawable.selector_menu_orange
            )
        )

        mDeviceSelectAdapter = DeviceSelectAdapter(this, mMenuList)
        mBinding.gridView.adapter = mDeviceSelectAdapter
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // 设备选择之后,保存选择结果,进入主界面
        mSpUtil.applyValue(
            SharedPreferencesUtil.Record(
                SharedPreferencesUtil.Key.DeviceName,
                mMenuList[position].mDeviceName
            )
        )
        intentActivity(GuideActivity.newIntent(this))
    }
}
