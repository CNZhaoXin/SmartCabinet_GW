package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ResourceUtils
import com.romainpiel.shimmer.Shimmer
import com.romainpiel.shimmer.ShimmerTextView
import com.zk.cabinet.R
import com.zk.cabinet.adapter.DeviceSelectAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.constant.SelfComm
import com.zk.cabinet.databinding.ActivitySelectDeviceBinding
import com.zk.cabinet.entity.DeviceMenu
import com.zk.cabinet.utils.SharedPreferencesUtil
import java.io.PrintWriter

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
        // 安装语音引擎包, 延迟1秒，等画面渲染好
        installKDXFApkHandler.postDelayed(installKDXFApkRunnable, 1000)
    }

    private val installKDXFApkHandler = Handler()
    private val installKDXFApkRunnable = Runnable {
        run {
            // 安装科大讯飞语音引擎包3.0
            installKDXFApk()
        }
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

        // 手持机PDA
        mMenuList.add(
            DeviceMenu(
                SelfComm.DEVICE_NAME[5].toString(),
                R.mipmap.ic_device_pda,
                R.drawable.selector_menu_orange
            )
        )

        // 通道门屏
        mMenuList.add(
            DeviceMenu(
                SelfComm.DEVICE_NAME[6].toString(),
                R.mipmap.ic_tdm,
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
        finish()
    }

    /**
     * 静默安装 科大讯飞语音引擎包3.0.apk
     */
    private fun installKDXFApk() {
        // 1.判断 科大讯飞语音引擎包3.0.apk 是否已经安装
        if (AppUtils.isAppInstalled("com.iflytek.speechcloud")) {
            LogUtils.e("安装科大讯飞语音引擎包3.0-语音包已安装")
        } else {
            // 未安装的话,复制安装包,并静默安装
            val filePath = PathUtils.getExternalAppDownloadPath() + "/科大讯飞语音引擎包3.0.apk"
            val result = ResourceUtils.copyFileFromAssets("kdxf.apk", filePath)
            // /storage/emulated/0/Android/data/com.gw.cabinet/files/Download/科大讯飞语音引擎包3.0.apk
            LogUtils.e("安装科大讯飞语音引擎包3.0-将Assets目录下kdxf.apk文件复制到$filePath")

            if (result) {
                // 复制成功
                LogUtils.e("安装科大讯飞语音引擎包3.0-文件复制成功")
                // PDA需要手动安装语言包，其他都是同一个厂家设备都已经root,都能静默安装
                if (AppUtils.isAppRoot()) {
                    // 静默安装方式,设备必须root，有root权限才能静默安装
                    val installResult = suInstallApp(filePath)
                    if (installResult) {
                        showSuccessToast("语音引擎包安装成功")
                        // 跳转到文字转语音设置界面
                        val intent = Intent()
                        intent.action = "com.android.settings.TTS_SETTINGS"
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
                        showSuccessToast("语音引擎包安装失败")
                    }
                } else {
                    // 手动安装方式
                    AppUtils.installApp(filePath)
                }
            } else {
                showSuccessToast("语音引擎包复制失败")
                LogUtils.e("安装科大讯飞语音引擎包3.0-文件复制失败")
            }
        }
    }

    /**
     * 静默安装Apk
     * 设备必须已破解(获得ROOT权限)
     */
    private fun suInstallApp(apkPath: String): Boolean {
        LogUtils.e("安装科大讯飞语音引擎包3.0-静默安装-发送安装指令:apkPath", apkPath)

        var printWriter: PrintWriter? = null
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec("su")
            printWriter = PrintWriter(process.outputStream)
            printWriter.println("chmod 777 $apkPath")
            printWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib")
            printWriter.println("pm install -r $apkPath")
            // printWriter.println("exit");
            printWriter.flush()
            printWriter.close()

            // 当在进行新版本安装时,这句会把程序卡住/如是覆盖安装会把程序kill掉,所以在这句之后的逻辑不会继续往下执行，所以重启的操作要放到process.waitFor();之前
            val value = process.waitFor()
            LogUtils.e("安装科大讯飞语音引擎包3.0-静默安装-结果：${value == 0}")
            return value == 0
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            LogUtils.e("安装科大讯飞语音引擎包3.0-静默安装-安装Apk出现异常", e.message)
        } finally {
            process?.destroy()
        }
        return false
    }
}
