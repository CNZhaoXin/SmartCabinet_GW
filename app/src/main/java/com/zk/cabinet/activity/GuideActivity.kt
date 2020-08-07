package com.zk.cabinet.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.zk.cabinet.R
import com.zk.cabinet.adapter.CabinetOnlineAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.CabinetOnlineInfo
import com.zk.cabinet.bean.User
import com.zk.cabinet.callback.FingerprintVerifyListener
import com.zk.cabinet.constant.SelfComm
import com.zk.cabinet.databinding.ActivityGuideBinding
import com.zk.cabinet.databinding.DialogLoginBinding
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.service.NetService
import com.zk.cabinet.utils.FingerprintParsingLibrary
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.cabinet.utils.SharedPreferencesUtil.Record
import com.zk.common.utils.LogUtil
import com.zk.rfid.callback.DeviceInformationListener
import com.zk.rfid.ur880.UR880Entrance
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference


private const val DEVICE_REGISTERED = 0x02
private const val DEVICE_HEARTBEAT = 0x03
private const val DEVICE_REMOVED = 0x04
private const val LOGIN_BY_PWD_SUCCESS = 0x05
private const val LOGIN_BY_PWD_FAIL = 0x06
private const val FINGER_LOGIN_SUCCESS = 0x07
private const val FINGER_LOGIN_ERROR = 0x08

class GuideActivity : TimeOffAppCompatActivity(), OnClickListener, View.OnLongClickListener {
    private lateinit var mGuideBinding: ActivityGuideBinding
    private lateinit var mHandler: MainHandler

    private val mCabinetOnlineList = ArrayList<CabinetOnlineInfo>()  //柜体在线情况
    private lateinit var mCabinetOnlineAdapter: CabinetOnlineAdapter //柜体在线情况

    private var mDialogLoginBinding: DialogLoginBinding? = null
    private var mDialogLogin: AlertDialog? = null

    private lateinit var mProgressSyncUserDialog: ProgressDialog

    companion object {

    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            DEVICE_REGISTERED, DEVICE_REMOVED -> {
                val deviceID = msg.obj.toString()

//                if (SelfComm.ONLINE_DEVICE.contains(deviceID)){
//                    if (msg.what == DEVICE_REMOVED) SelfComm.ONLINE_DEVICE.remove(deviceID)
//                } else {
//                    if (msg.what == DEVICE_REGISTERED) SelfComm.ONLINE_DEVICE.add(deviceID)
//                }

                for (cabinetOnlineInfo in mCabinetOnlineList) {
                    if (cabinetOnlineInfo.mCode == deviceID) {
                        cabinetOnlineInfo.isOnLine = msg.what == DEVICE_REGISTERED
                        break
                    }
                }
                mCabinetOnlineAdapter.notifyDataSetChanged()
            }
            LOGIN_BY_PWD_SUCCESS -> {
                mProgressSyncUserDialog.dismiss()
                showToast("登录成功，欢迎：${msg.obj}")
                intentActivity(MainMenuActivity.newIntent(this))
            }
            LOGIN_BY_PWD_FAIL -> {
                mProgressSyncUserDialog.dismiss()
                Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_SHORT).show()
            }
            SelfComm.NET_SERVICE_INVENTORY -> {
                val data = msg.data
                val cabCodeList: ArrayList<String> = data.getStringArrayList("cabCodeList")!!
                val inventoryId: ArrayList<String> = data.getStringArrayList("inventoryIdList")!!
                intentActivity(DemoInterfaceActivity.newIntent(this, true, cabCodeList, inventoryId))
            }
            FINGER_LOGIN_SUCCESS -> {
                if (!mProgressSyncUserDialog.isShowing){
                    val user = msg.obj as User
                    login(user.userCode, user.password)
                }
            }
            FINGER_LOGIN_ERROR -> {
                showToast(msg.obj.toString())
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
        mProgressSyncUserDialog = ProgressDialog(this)
        mProgressSyncUserDialog.setTitle("登录")
        mProgressSyncUserDialog.setMessage("正在登录，请稍后......")
        mProgressSyncUserDialog.setCancelable(false)

        val deviceList = DeviceService.getInstance().loadAll()
        if(deviceList != null && deviceList.size > 0) {
            for (device in deviceList) {
                mCabinetOnlineList.add( CabinetOnlineInfo(
                    device.deviceId,
                    device.deviceName, false))
            }
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mGuideBinding.guideCabinetOnlineStatusRv.layoutManager = layoutManager
        mCabinetOnlineAdapter = CabinetOnlineAdapter(this, mCabinetOnlineList)
        mGuideBinding.guideCabinetOnlineStatusRv.adapter = mCabinetOnlineAdapter

        UR880Entrance.getInstance().init(UR880Entrance.CONNECTION_TCP_IP, 7880, null)
        UR880Entrance.getInstance().addOnDeviceInformationListener(mDeviceInformationListener)
        UR880Entrance.getInstance().connect()

        val netServiceIntent = Intent(this, NetService::class.java)
        val netServiceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {

            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val mNetMessenger = Messenger(service)

                val message = Message.obtain()
                message.what = SelfComm.NET_SERVICE_CONNECT
                message.replyTo = Messenger(mHandler)
                mNetMessenger.send(message)
            }
        }
        bindService(netServiceIntent, netServiceConnection, BIND_AUTO_CREATE)

        FingerprintParsingLibrary.getInstance().init(this)
        FingerprintParsingLibrary.getInstance().onFingerprintVerifyListener(mFingerprintVerifyListener)
        FingerprintParsingLibrary.getInstance().setFingerprintVerify(true)
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
                    login(userCode, pwd)

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
                intentActivity(SystemSettingsActivity.newIntent(this))
            }
        }
        return false
    }

    override fun intentActivity(intent: Intent?) {
        dismissLoginDialog()
        FingerprintParsingLibrary.getInstance().setFingerprintVerify(false)
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
        FingerprintParsingLibrary.getInstance().close()
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

    private fun login(user: String, pwd: String) {
        mProgressSyncUserDialog.show()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("username", user)
            jsonObject.put("password", pwd)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, NetworkRequest.instance.mClientLogin,
            jsonObject, Response.Listener { response ->
                try {
                    val success = response!!.getBoolean("success")
                    if (success) {
                        val data = response.getJSONObject("data")
                        val recordList = ArrayList<Record>()
                        recordList.add(Record(Key.IdTemp, data.getString("id")))
                        recordList.add(Record(Key.NameTemp, data.getString("name")))
                        recordList.add(Record(Key.GenderTemp, data.getString("gender")))
                        recordList.add(Record(Key.PhoneNumberTemp, data.getString("phoneNumber")))
                        recordList.add(Record(Key.LoginCodeTemp, data.getString("loginCode")))
                        recordList.add(Record(Key.RoleIdTemp, data.getString("roleId")))
                        recordList.add(Record(Key.RoleNameTemp, data.getString("roleName")))
                        recordList.add(Record(Key.RootMemberTemp, data.getString("rootMember")))
                        recordList.add(Record(Key.OrgCodeTemp, data.getString("orgCode")))
                        recordList.add(Record(Key.OrgNameTemp, data.getString("orgName")))
                        val orgList = data.getJSONArray("orgList")
                        recordList.add(Record(Key.OrgCabinet, (orgList[0] as JSONObject).getString("orgCabinet")))
                        mSpUtil.applyValue(recordList)

                        val msg = Message.obtain()
                        msg.what = LOGIN_BY_PWD_SUCCESS
                        msg.obj = data.getString("name")
                        mHandler.sendMessage(msg)
                    } else {
                        val msg = Message.obtain()
                        msg.what = LOGIN_BY_PWD_FAIL
                        msg.obj = "用户名或密码错误"
                        mHandler.sendMessage(msg)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    val msg = Message.obtain()
                    msg.what = LOGIN_BY_PWD_FAIL
                    msg.obj = "数据解析失败。"
                    mHandler.sendMessage(msg)
                }
            }, Response.ErrorListener { error ->
                val msg = if (error != null)
                    if (error.networkResponse != null)
                        "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                    else
                        "errorCode: -1 VolleyError: $error"
                else {
                    "errorCode: -1 VolleyError: 未知"
                }
                val message = Message.obtain()
                message.what = LOGIN_BY_PWD_FAIL
                message.obj = msg
                mHandler.sendMessage(message)
            })
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        FingerprintParsingLibrary.getInstance().setFingerprintVerify(true)
    }

    private val mFingerprintVerifyListener = object : FingerprintVerifyListener {
        override fun fingerprintVerify(result: Boolean, user: User?) {
            if (result) {
                val msg = Message.obtain()
                msg.what = FINGER_LOGIN_SUCCESS
                msg.obj = user
                mHandler.sendMessage(msg)
            } else {
                val msg = Message.obtain()
                msg.what = FINGER_LOGIN_ERROR
                msg.obj = "该指纹不存在。"
                mHandler.sendMessage(msg)
            }
        }
    }
}
