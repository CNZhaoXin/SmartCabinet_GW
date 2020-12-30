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


class MainMenuActivity : TimeOffAppCompatActivity(), AdapterView.OnItemClickListener {
    private lateinit var mBinding: ActivityMainMenuBinding
    private val mMenuList: ArrayList<MainMenuInfo> = ArrayList()
    private lateinit var mMenuAdapter: MainMenuAdapter

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, MainMenuActivity::class.java)
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

    private fun initShimmerTitle(houseName: String) {
        // 隐藏英文名称显示
        mBinding.stvEnglishTitle.visibility = View.GONE
        // 设置字体
        val tf = Typeface.createFromAsset(assets, "fonts/OpenSans-ExtraBold.ttf")
        mBinding.stvChineseTitle.text = houseName
        mBinding.stvChineseTitle.typeface = tf
        mBinding.stvEnglishTitle.typeface = tf
        // 开启动效
        val shimmer = Shimmer()
        shimmer.duration = 5000
        shimmer.direction = Shimmer.ANIMATION_DIRECTION_LTR
        shimmer.start<ShimmerTextView>(mBinding.stvChineseTitle)
        shimmer.start<ShimmerTextView>(mBinding.stvEnglishTitle)
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
//        SelfComm.DEVICE_NAME[0] = "未选择"
//        SelfComm.DEVICE_NAME[1] = "档案组架"
//        SelfComm.DEVICE_NAME[2] = "档案组柜"
//        SelfComm.DEVICE_NAME[3] = "档案单柜"
//        SelfComm.DEVICE_NAME[4] = "一体机"
//        SelfComm.DEVICE_NAME[5] = "PDA"
        // 根据不同设备类型初始化操作

        val deviceName = mSpUtil.getString(SharedPreferencesUtil.Key.DeviceName, "")
        // PDA
        if (deviceName.equals(SelfComm.DEVICE_NAME[5])) {
            // PDA5
            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[1],
                    R.mipmap.ic_return,
                    "入 库",
                    R.drawable.selector_menu_blue_normal
                )
            )
            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[2],
                    R.mipmap.ic_yiku,
                    "移 库",
                    R.drawable.selector_menu_green_normal
                )
            )
            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[3],
                    R.mipmap.ic_inventory,
                    "盘 库",
                    R.drawable.selector_menu_orange_normal
                )
            )

            // 初始化title
            initShimmerTitlePDA()

        } else if (deviceName.equals(SelfComm.DEVICE_NAME[4])) {
            // 一体机4
            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[4],
                    R.mipmap.ic_borrow,
                    "借 阅",
                    R.drawable.selector_menu_blue_normal
                )
            )

            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[5],
                    R.mipmap.ic_return,
                    "归 还",
                    R.drawable.selector_menu_green_normal
                )
            )

            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[0],
                    R.mipmap.ic_logout,
                    "登 出",
                    R.drawable.selector_menu_red
                )
            )

            // 初始化Title
            val houseName = mSpUtil.getString(
                SharedPreferencesUtil.Key.HouseName,
                resources.getString(R.string.title)
            )
            initShimmerTitle(houseName!!)
        } else if (deviceName.equals(SelfComm.DEVICE_NAME[3])) {
            // 档案单柜3
            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[6],
                    R.mipmap.ic_borrow,
                    "借 阅",
                    R.drawable.selector_menu_blue_normal
                )
            )

            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[7],
                    R.mipmap.ic_return,
                    "归 还",
                    R.drawable.selector_menu_green_normal
                )
            )

            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[10],
                    R.mipmap.ic_inventory,
                    "预 览",
                    R.drawable.selector_menu_orange_normal
                )
            )

            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[0],
                    R.mipmap.ic_logout,
                    "登 出",
                    R.drawable.selector_menu_red
                )
            )

            // 初始化Title
            val houseName = mSpUtil.getString(
                SharedPreferencesUtil.Key.HouseName,
                resources.getString(R.string.title)
            )
            initShimmerTitle(houseName!!)
        } else if (deviceName.equals(SelfComm.DEVICE_NAME[2])) {
            // 档案组柜2
            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[8],
                    R.mipmap.ic_borrow,
                    "借 阅",
                    R.drawable.selector_menu_blue_normal
                )
            )

            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[9],
                    R.mipmap.ic_return,
                    "归 还",
                    R.drawable.selector_menu_green_normal
                )
            )

            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[10],
                    R.mipmap.ic_inventory,
                    "预 览",
                    R.drawable.selector_menu_orange_normal
                )
            )

            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[0],
                    R.mipmap.ic_logout,
                    "登 出",
                    R.drawable.selector_menu_red
                )
            )

            // 初始化Title
            val houseName = mSpUtil.getString(
                SharedPreferencesUtil.Key.HouseName,
                resources.getString(R.string.title)
            )
            initShimmerTitle(houseName!!)

        } else if (deviceName.equals(SelfComm.DEVICE_NAME[1])) {
            // 档案组架1
            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[10],
                    R.mipmap.ic_inventory,
                    "预 览",
                    R.drawable.selector_menu_orange_normal
                )
            )

            mMenuList.add(
                MainMenuInfo(
                    SelfComm.FUNCTION_TYPE[0],
                    R.mipmap.ic_logout,
                    "登 出",
                    R.drawable.selector_menu_red
                )
            )

            // 初始化Title
            val houseName = mSpUtil.getString(
                SharedPreferencesUtil.Key.HouseName,
                resources.getString(R.string.title)
            )
            initShimmerTitle(houseName!!)
        }

        mMenuAdapter = MainMenuAdapter(this, mMenuList)
        mBinding.mainMenuGv.adapter = mMenuAdapter
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (mMenuList[position].mFunctionType) {
            // 登出
            SelfComm.FUNCTION_TYPE[0] -> {
                finish()
            }

            // PDA-入库
            SelfComm.FUNCTION_TYPE[1] -> {
                intentActivity(InStoragePDAActivity.newIntent(this))
            }
            // PDA-移库
            SelfComm.FUNCTION_TYPE[2] -> {
                intentActivity(MoveStoragePDAActivity.newIntent(this))
            }
            // PDA-盘库
            SelfComm.FUNCTION_TYPE[3] -> {
                intentActivity(PDAInventoryActivity.newIntent(this))
            }

            // 一体机-借阅
            SelfComm.FUNCTION_TYPE[4] -> {
                intentActivity(YTJBorrowActivity.newIntent(this))
            }
            // 一体机-归还
            SelfComm.FUNCTION_TYPE[5] -> {
                intentActivity(YTJReturnActivity.newIntent(this))
            }

            // 档案单柜-借阅
            SelfComm.FUNCTION_TYPE[6] -> {
                intentActivity(ZNGBorrowActivity.newIntent(this))
            }
            // 档案单柜-归还
            SelfComm.FUNCTION_TYPE[7] -> {
                intentActivity(ZNGReturnActivity.newIntent(this))
            }

            // 档案组柜-借阅
            SelfComm.FUNCTION_TYPE[8] -> {
                intentActivity(ZNGSBorrowActivity.newIntent(this))
            }
            // 档案组柜-归还
            SelfComm.FUNCTION_TYPE[9] -> {
                intentActivity(ZNGSReturnActivity.newIntent(this))
            }

            // 档案组架-预览 , /档案组柜/档案单柜-预览(手动盘库)
            SelfComm.FUNCTION_TYPE[10] -> {
                intentActivity(CabinetPreviewActivity.newIntent(this))
            }
        }
    }
}
