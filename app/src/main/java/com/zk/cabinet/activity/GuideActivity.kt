package com.zk.cabinet.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
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
import com.zk.cabinet.db.UserService
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.service.NetService
import com.zk.cabinet.utils.FingerprintParsingLibrary
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.cabinet.utils.SharedPreferencesUtil.Record
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

private const val SYS_SETTING_NO_SET_CABINET = 0x09
private const val WEB_NO_SET_CABINET = 0x10
private const val CABINET_NO_MATCH = 0x11

class GuideActivity : TimeOffAppCompatActivity(), OnClickListener, View.OnLongClickListener {
    private lateinit var mGuideBinding: ActivityGuideBinding
    private lateinit var mHandler: MainHandler

    private val mCabinetOnlineList = ArrayList<CabinetOnlineInfo>()  //柜体在线情况
    private lateinit var mCabinetOnlineAdapter: CabinetOnlineAdapter //柜体在线情况

    private var mDialogLoginBinding: DialogLoginBinding? = null
    private var mDialogLogin: AlertDialog? = null

    private lateinit var mProgressDialog: ProgressDialog

    companion object {}

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            DEVICE_REGISTERED, DEVICE_REMOVED -> {
                // 前来注册的读写器设备ID
                val deviceID = msg.obj.toString()
//                if (SelfComm.ONLINE_DEVICE.contains(deviceID)){
//                    if (msg.what == DEVICE_REMOVED) SelfComm.ONLINE_DEVICE.remove(deviceID)
//                } else {
//                    if (msg.what == DEVICE_REGISTERED) SelfComm.ONLINE_DEVICE.add(deviceID)
//                }
                for (cabinetOnlineInfo in mCabinetOnlineList) {
                    if (cabinetOnlineInfo.mCode == deviceID) { // 设置界面配置的读写器设备ID
                        cabinetOnlineInfo.isOnLine = msg.what == DEVICE_REGISTERED
                        break
                    }
                }
                mCabinetOnlineAdapter.notifyDataSetChanged()
            }
            LOGIN_BY_PWD_SUCCESS -> {
                mProgressDialog.dismiss()
                showToast("登录成功，欢迎：${msg.obj}")
                intentActivity(MainMenuActivity.newIntent(this))
            }
            LOGIN_BY_PWD_FAIL -> {
                mProgressDialog.dismiss()
                Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_SHORT).show()
            }
            SelfComm.NET_SERVICE_INVENTORY -> {
                // 接收盘点任务单数据
                val data = msg.data
                val cabCodeList: ArrayList<String> = data.getStringArrayList("cabCodeList")!!
                val inventoryId: ArrayList<String> = data.getStringArrayList("inventoryIdList")!!
                val inOrg: ArrayList<String> = data.getStringArrayList("inOrgList")!!

                // 拉取到盘点任务,判断设备是否在线,不在线不盘点
                if (mCabinetOnlineList.size > 0) {
                    val cabCodeListOnLine = ArrayList<String>()
                    for (device in mCabinetOnlineList) {
                        if (device.isOnLine) {
                            for (cabcode in cabCodeList) {
                                if (device.mCodeName == cabcode) {
                                    cabCodeListOnLine.add(cabcode)
                                }
                            }
                        }
                    }

                    // 有盘点单中存在的在线设备.才去盘点
                    if (cabCodeListOnLine.size > 0) {
                        intentActivity(
                            DemoInterfaceActivity.newIntent(
                                this,
                                true,
                                cabCodeListOnLine,
                                inventoryId,
                                inOrg
                            )
                        )
                    }

                    Log.e("zx-盘点任务单中需要自动盘点的在线设备-", JSON.toJSONString(cabCodeListOnLine))
                }

            }
            FINGER_LOGIN_SUCCESS -> {
                if (!mProgressDialog.isShowing) {
                    val user = msg.obj as User
                    login(user.userCode, user.password)
                }
            }
            FINGER_LOGIN_ERROR -> {
                showToast(msg.obj.toString())
            }

            SYS_SETTING_NO_SET_CABINET -> {
                mProgressDialog.dismiss()
                showToast(msg.obj.toString())
            }
            WEB_NO_SET_CABINET -> {
                mProgressDialog.dismiss()
                showToast(msg.obj.toString())
            }
            CABINET_NO_MATCH -> {
                mProgressDialog.dismiss()
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
        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setMessage("正在登录...")
        mProgressDialog.setCancelable(false)

        val deviceList = DeviceService.getInstance().loadAll()
        if (deviceList != null && deviceList.size > 0) {
            for (device in deviceList) {
                mCabinetOnlineList.add(
                    CabinetOnlineInfo(device.deviceId, device.deviceName, false)
                )
            }
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mGuideBinding.guideCabinetOnlineStatusRv.layoutManager = layoutManager
        mCabinetOnlineAdapter = CabinetOnlineAdapter(this, mCabinetOnlineList)
        mGuideBinding.guideCabinetOnlineStatusRv.adapter = mCabinetOnlineAdapter

        // 启动自己等待读写器连接(服务器已启动)
        val serverPort = mSpUtil.getInt(Key.CabinetServicePort, -1)
        if (serverPort != -1) {
            UR880Entrance.getInstance().init(UR880Entrance.CONNECTION_TCP_IP, serverPort, null)
            UR880Entrance.getInstance().addOnDeviceInformationListener(mDeviceInformationListener)
            UR880Entrance.getInstance().connect()
        }

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
        FingerprintParsingLibrary.getInstance()
            .onFingerprintVerifyListener(mFingerprintVerifyListener)
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
            Log.w("zx-设备-心跳-", "heartbeat -----p0: $p0")
        }

        override fun versionInformation(p0: String?, p1: String?, p2: String?) {
            Log.e("zx-设备-信息-", "versionInformation -----p0: $p0 ---p1: $p1 ---p2: $p2")
        }

        override fun registered(p0: String?, p1: String?, p2: String?) {
            Log.e("zx-设备-注册-", "registered -----p0: $p0 ---p1: $p1 ---p2: $p2")
            val message = Message.obtain()
            message.what = DEVICE_REGISTERED
            message.obj = p0
            mHandler.sendMessage(message)
        }

        override fun removed(p0: String?) {
            Log.e("zx-设备-移除-", "removed -----p0: $p0 ")
            val message = Message.obtain()
            message.what = DEVICE_REMOVED
            message.obj = p0
            mHandler.sendMessage(message)
        }

    }

    // 登录弹窗
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

        val window = mDialogLogin!!.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        window!!.setLayout(
            resources.displayMetrics.widthPixels * 2 / 3,
            resources.displayMetrics.heightPixels * 2 / 5
        )
    }

    //关闭登录弹窗
    private fun dismissLoginDialog() {
        if (mDialogLogin != null && mDialogLogin!!.isShowing) mDialogLogin!!.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        FingerprintParsingLibrary.getInstance().setFingerprintVerify(true)
    }

    private fun login(user: String, pwd: String) {
        mProgressDialog.show()
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
                        Log.e("zx-登录返回数据:", "$response")
                        // {"id":"136e38e922c8f17bd3477a9d6563ffa4","name":"赵鑫-档案管理员-子部门2","gender":1,"phoneNumber":"15067105195","loginCode":"zx2","roleId":"4c34d2b9a6589381b82176974210c734","roleName":"档案管理员","rootMember":false
                        // ,"orgCode":"333","orgName":"333","orgList":[{"id":"2ff342cb4b0c4293b0669e6a60deeac5","name":"子部门2","code":"333","parentOrgCode":"222","childOrgCode":"","orgCabinet":"FG2\/FG2_1,FG2\/FG2_2,FG2\/FG2_3"}]}
                        val recordList = ArrayList<Record>()
                        var id = data.getString("id")
                        var name = data.getString("name") // 用户昵称
                        var gender = data.getString("gender")
                        var phoneNumber = data.getString("phoneNumber")
                        var loginCode = data.getString("loginCode") // 登录账号
                        var roleId = data.getString("roleId") // 角色类型,注意:返回的是String类型,不一定是int类型的字符串
                        var roleName = data.getString("roleName") // 角色名称
                        var rootMember = data.getString("rootMember")
                        var orgCode = data.getString("orgCode") // 部门编号
                        var orgName = data.getString("orgName") // 部门名称
                        val orgList = data.getJSONArray("orgList")
                        var orgCabinets = (orgList[0] as JSONObject).getString("orgCabinet")

                        recordList.add(Record(Key.IdTemp, id))
                        recordList.add(Record(Key.NameTemp, name))
                        recordList.add(Record(Key.GenderTemp, gender))
                        recordList.add(Record(Key.PhoneNumberTemp, phoneNumber))
                        recordList.add(Record(Key.LoginCodeTemp, loginCode))
                        recordList.add(Record(Key.RoleIdTemp, roleId))
                        recordList.add(Record(Key.RoleNameTemp, roleName))
                        recordList.add(Record(Key.RootMemberTemp, rootMember))
                        recordList.add(Record(Key.OrgCodeTemp, orgCode))
                        recordList.add(Record(Key.OrgNameTemp, orgName))
                        recordList.add(Record(Key.OrgCabinet, orgCabinets))
                        mSpUtil.applyValue(recordList)

                        // 判断用户有哪些柜体操作权限并保存
                        // 登录时平台分配给用户的柜子权限
                        // "orgList":[{"id":"2ff342cb4b0c4293b0669e6a60deeac5","name":"子部门","code":"222","parentOrgCode":"","childOrgCode":""
                        // ,"orgCabinet":"1234567803\/1234567803_1,1234567803\/1234567803_2,1234567803\/1234567803_3,1234567803\/1234567803_4,1234567803\/1234567803_5"}]}
                        // 系统设置界面配置的柜子列表(需要和平台一致)
                        val deviceList = DeviceService.getInstance().loadAll()
                        if (deviceList.size == 0) {
                            val msg = Message.obtain()
                            msg.what = SYS_SETTING_NO_SET_CABINET
                            msg.obj = "系统设置中未配置柜体参数"
                            mHandler.sendMessageDelayed(msg, 1000)
                        } else {
                            // val orgCabinets = mSpUtil.getString(SharedPreferencesUtil.Key.OrgCabinet, "")!!
                            if (orgCabinets.isNotEmpty()) {
                                val mCanOperationCabinets = HashMap<String, ArrayList<Int>>()

                                val cabinets = orgCabinets.split(",").toTypedArray()
                                for (cabinet in cabinets) {
                                    val device =
                                        cabinet.subSequence(0, cabinet.indexOf("/", 0)).toString()
                                    val floor =
                                        cabinet.subSequence(
                                            cabinet.indexOf("_", 0) + 1,
                                            cabinet.length
                                        )
                                            .toString()
                                    if (mCanOperationCabinets.containsKey(device)) {
                                        mCanOperationCabinets.getValue(device).add(floor.toInt())
                                    } else {
                                        val a = ArrayList<Int>()
                                        a.add(floor.toInt())
                                        mCanOperationCabinets[device] = a
                                    }
                                }

                                val mIterator = deviceList.iterator()
                                while (mIterator.hasNext()) {
                                    val next = mIterator.next()
                                    if (!mCanOperationCabinets.containsKey(next.deviceName)) {
                                        mIterator.remove()
                                    }
                                }

                                if (deviceList.isEmpty()) {
                                    val msg = Message.obtain()
                                    msg.what = CABINET_NO_MATCH
                                    msg.obj = "您所在部门配置的柜体与系统设置中配置的柜体参数不匹配"
                                    mHandler.sendMessageDelayed(msg, 1000)
                                } else {
                                    // 保存当前登录人员可以操作的柜体List
                                    val deviceListJson = Gson().toJson(deviceList)
                                    mSpUtil.applyValue(
                                        Record(
                                            Key.CanOperateCabinet,
                                            deviceListJson
                                        )
                                    )

                                    // 保存当前登录人员可以操作的 柜体 + 可操作层 数据, 这个也只能是当前主柜体能操作的柜子
                                    val nameList = ArrayList<String>()
                                    for (device in deviceList) {
                                        nameList.add(device.deviceName)
                                    }

                                    val mCanOperationCabinetsNew = HashMap<String, ArrayList<Int>>()
                                    for ((key, value) in mCanOperationCabinets) {
                                        if (nameList.contains(key)) {
                                            mCanOperationCabinetsNew.put(key, value)
                                        }
                                    }

                                    val canOperateCabinetFloorJson =
                                        Gson().toJson(mCanOperationCabinetsNew)
                                    Log.e("zx-登录人员可操作的柜子+层:", canOperateCabinetFloorJson)

                                    mSpUtil.applyValue(
                                        Record(
                                            Key.CanOperateCabinetFloor,
                                            canOperateCabinetFloorJson
                                        )
                                    )

                                    // 登录成功, 存在: 更新用户信息, 不存在:创建新用户
                                    val user = UserService.getInstance().queryByUserUuId(id)
                                    if (user == null) {
                                        val newUser = User()
                                        newUser.uuId = id
                                        newUser.userCode = loginCode
                                        newUser.password = pwd
                                        UserService.getInstance().insert(newUser)
                                    } else {
                                        user.uuId = id
                                        user.userCode = loginCode
                                        user.password = pwd
                                        UserService.getInstance().update(user)
                                        FingerprintParsingLibrary.getInstance().upUserList()
                                    }

                                    val msg = Message.obtain()
                                    msg.what = LOGIN_BY_PWD_SUCCESS
                                    msg.obj = data.getString("name")
                                    mHandler.sendMessageDelayed(msg, 1000)
                                }

                            } else {
                                val msg = Message.obtain()
                                msg.what = WEB_NO_SET_CABINET
                                msg.obj = "您所在部门未给您分配任何柜体权限"
                                mHandler.sendMessageDelayed(msg, 1000)
                            }
                        }
                    } else {
                        val msg = Message.obtain()
                        msg.what = LOGIN_BY_PWD_FAIL
                        msg.obj = "用户名或密码错误"
                        mHandler.sendMessageDelayed(msg, 1000)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    val msg = Message.obtain()
                    msg.what = LOGIN_BY_PWD_FAIL
                    msg.obj = "数据解析失败"
                    mHandler.sendMessageDelayed(msg, 1000)
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
                mHandler.sendMessageDelayed(message, 1000)
            })
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    private val mFingerprintVerifyListener =
        FingerprintVerifyListener { result, user ->
            if (result) {
                val msg = Message.obtain()
                msg.what = FINGER_LOGIN_SUCCESS
                msg.obj = user
                mHandler.sendMessage(msg)
            } else {
                val msg = Message.obtain()
                msg.what = FINGER_LOGIN_ERROR
                msg.obj = "该指纹不存在"
                mHandler.sendMessage(msg)
            }
        }
}
