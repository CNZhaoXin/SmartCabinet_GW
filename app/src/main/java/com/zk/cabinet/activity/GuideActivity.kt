package com.zk.cabinet.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.zk.cabinet.R
import com.zk.cabinet.adapter.CabinetOnlineAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.CabinetOnlineInfo
import com.zk.cabinet.databinding.ActivityGuideBinding
import com.zk.cabinet.databinding.DialogLoginBinding
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.common.utils.LogUtil
import com.zk.rfid.callback.DeviceInformationListener
import com.zk.rfid.ur880.UR880Entrance
import java.lang.ref.WeakReference


private const val DEVICE_REGISTERED = 0x02
private const val DEVICE_HEARTBEAT = 0x03
private const val DEVICE_REMOVED = 0x04

class GuideActivity : TimeOffAppCompatActivity(), OnClickListener, View.OnLongClickListener {
    private lateinit var mGuideBinding: ActivityGuideBinding
    private lateinit var mHandler: MainHandler

    private val mCabinetOnlineList = ArrayList<CabinetOnlineInfo>()  //柜体在线情况
    private lateinit var mCabinetOnlineAdapter: CabinetOnlineAdapter //柜体在线情况

    private var mDialogLoginBinding: DialogLoginBinding? = null
    private var mDialogLogin: AlertDialog? = null

    companion object {

    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            DEVICE_REGISTERED, DEVICE_REMOVED -> {
                val deviceID = msg.obj.toString()
                var isExit = false
                for (cabinetOnlineInfo in mCabinetOnlineList) {
                    if (cabinetOnlineInfo.mCodeName == deviceID) {
                        isExit = true
                        cabinetOnlineInfo.isOnLine = msg.what == DEVICE_REGISTERED
                        mCabinetOnlineAdapter.notifyDataSetChanged()
                        break
                    }
                }
                if (!isExit){
                    mCabinetOnlineList.add(CabinetOnlineInfo(0, deviceID, msg.what == DEVICE_REGISTERED))
                }
                mCabinetOnlineAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        isAutoFinish = false
        super.onCreate(savedInstanceState)
        mGuideBinding = DataBindingUtil.setContentView(this, R.layout.activity_guide)
        mGuideBinding.onClickListener = this
        mHandler = MainHandler(this)

        init()
    }

    private fun init() {
//        val cabinets: Array<String> =
//            mSpUtil.getString(Key.NumberOfBoxes, "A")!!.split(",").toTypedArray()
//        for (codeName in cabinets) {
//            mCabinetOnlineList.add(CabinetOnlineInfo(0, codeName, false))
//        }
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mGuideBinding.guideCabinetOnlineStatusRv.layoutManager = layoutManager
        mCabinetOnlineAdapter = CabinetOnlineAdapter(this, mCabinetOnlineList)
        mGuideBinding.guideCabinetOnlineStatusRv.adapter = mCabinetOnlineAdapter

        UR880Entrance.getInstance().init(UR880Entrance.CONNECTION_TCP_IP, 7880, null)
        UR880Entrance.getInstance().addOnDeviceInformationListener(mDeviceInformationListener)
        UR880Entrance.getInstance().connect()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.guide_login_rl -> {
                showLoginDialog()
            }
            //登录弹窗的取消按钮
            R.id.dialog_other_login_dismiss_btn -> {
                dismissLoginDialog()
            }
            //登录弹窗的确认按钮
            R.id.dialog_other_login_sure_btn -> {
                val userCode =
                    mDialogLoginBinding!!.dialogOtherLoginAccountEdt.text.toString().trim()
                val pwd = mDialogLoginBinding!!.dialogOtherLoginPwdEdt.text.toString().trim()
                if (!TextUtils.isEmpty(userCode) && !TextUtils.isEmpty(pwd)) {
                    //todo 登陆
                    intentActivity(MainMenuActivity.newIntent(this))
                } else {
                    showToast(resources.getString(R.string.fill_complete))
                }
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        when (v?.id) {
            //登录弹窗的确认按钮
            R.id.dialog_other_login_sure_btn -> {
//                intentActivity(MainMenuActivity.newIntent(this))
            }
        }
        return false
    }

    override fun intentActivity(intent: Intent?) {
        dismissLoginDialog()
        super.intentActivity(intent)
    }

    private class MainHandler(guideActivity: GuideActivity) : Handler() {
        private val mainWeakReference = WeakReference(guideActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mainWeakReference.get()!!.handleMessage(msg)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        UR880Entrance.getInstance().removeAllDeviceInformationListener()
        UR880Entrance.getInstance().disConnect()
    }

    private val mDeviceInformationListener = object : DeviceInformationListener {
        override fun heartbeat(p0: String?) {
            LogUtil.instance.d("heartbeat -----p0: $p0")
        }

        override fun versionInformation(p0: String?, p1: String?, p2: String?) {
            LogUtil.instance.d("versionInformation -----p0: $p0 ---p1: $p1 ---p2: $p2")
        }

        override fun registered(p0: String?, p1: String?, p2: String?) {
            LogUtil.instance.d("registered -----p0: $p0 ---p1: $p1 ---p2: $p2")
            val message = Message.obtain()
            message.what = DEVICE_REGISTERED
            message.obj = p0
            mHandler.sendMessage(message)
        }

        override fun removed(p0: String?) {
            LogUtil.instance.d("removed -----p0: $p0 ")
        }


    }

    //登录弹窗
    private fun showLoginDialog() {
        if (mDialogLogin == null) {
            mDialogLogin = AlertDialog.Builder(this).create()
            mDialogLoginBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.dialog_login,
                null,
                false
            )
            mDialogLoginBinding!!.onClickListener = this
            mDialogLoginBinding!!.onLongClickListener = this
            mDialogLogin!!.setView(mDialogLoginBinding!!.root)
            mDialogLogin!!.setCancelable(false)
        }
        mDialogLoginBinding!!.dialogOtherLoginAccountEdt.text = null
        mDialogLoginBinding!!.dialogOtherLoginPwdEdt.text = null
        mDialogLoginBinding!!.dialogOtherLoginAccountEdt.isFocusable = true
        mDialogLoginBinding!!.dialogOtherLoginAccountEdt.isFocusableInTouchMode = true
        mDialogLoginBinding!!.dialogOtherLoginAccountEdt.requestFocus()
        mDialogLogin!!.show()
    }

    //关闭登录弹窗
    private fun dismissLoginDialog() {
        if (mDialogLogin != null && mDialogLogin!!.isShowing) mDialogLogin!!.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
