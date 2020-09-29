package com.zk.cabinet.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
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
import com.zk.cabinet.db.DossierOperatingService
import com.zk.cabinet.db.UserService
import com.zk.cabinet.faceServer.FaceRecognitionHttpServer
import com.zk.cabinet.faceServer.FaceRecognitionListener
import com.zk.cabinet.faceServer.resultBean.ResultSuccess
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.service.NetService
import com.zk.cabinet.utils.FingerprintParsingLibrary
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.cabinet.utils.SharedPreferencesUtil.Record
import com.zk.common.utils.ActivityUtil
import com.zk.rfid.callback.DeviceInformationListener
import com.zk.rfid.ur880.UR880Entrance
import kotlinx.android.synthetic.main.dialog_face.*
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
private const val LOGIN_BY_FACE = 0x09
private const val LOGIN_BY_FACE_NO_REGIST = 0x10

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

    private var mFaceDialogBinding: ViewDataBinding? = null
    private var mFaceDialog: AlertDialog? = null

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
                speek("登录成功，欢迎：${msg.obj}")
                intentActivity(MainMenuActivity.newIntent(this))
            }
            LOGIN_BY_FACE -> {
                dismissFaceDialog()
                showToast("登录成功，欢迎：${msg.obj}")
                speek("登录成功，欢迎：${msg.obj}")
                intentActivity(MainMenuActivity.newIntent(this))
                isHandle = false
            }
            LOGIN_BY_FACE_NO_REGIST -> {
                dismissFaceDialog()
                showToast("${msg.obj}")
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
        createPieChart()
        // 开启人脸识别Http服务器
        startFaceServer()
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

    /**
     * 创建圆饼图
     */
    private fun createPieChart() {
        mGuideBinding.pieChart.setUsePercentValues(true)

        mGuideBinding.pieChart.getDescription().setEnabled(false)
        mGuideBinding.pieChart.setDragDecelerationFrictionCoef(0.95f)
        // 非线型
//        mGuideBinding.pieChart.setExtraOffsets(5, 10, 5, 5);
        // 线型
        mGuideBinding.pieChart.setExtraOffsets(20f, 3f, 20f, 0f)
        val tfLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf")
        mGuideBinding.pieChart.setCenterTextTypeface(tfLight)
        mGuideBinding.pieChart.setCenterText(
            generateCenterSpannableText(
                "总库存",
                "120"
            )
        )
        mGuideBinding.pieChart.setDrawHoleEnabled(true)
        mGuideBinding.pieChart.setHoleColor(Color.WHITE)
        mGuideBinding.pieChart.setTransparentCircleColor(Color.WHITE)
        mGuideBinding.pieChart.setTransparentCircleAlpha(110)
        mGuideBinding.pieChart.setHoleRadius(58f)
        mGuideBinding.pieChart.setTransparentCircleRadius(61f)
        mGuideBinding.pieChart.setDrawCenterText(true)
        mGuideBinding.pieChart.setRotationAngle(0f)
        // enable rotation of the mGuideBinding.pieChart by touch
        mGuideBinding.pieChart.setRotationEnabled(true)
        mGuideBinding.pieChart.setHighlightPerTapEnabled(true)
        mGuideBinding.pieChart.setCenterTextSize(24f)

        // mGuideBinding.pieChart.setUnit(" €");
        // mGuideBinding.pieChart.setDrawUnitsInmGuideBinding.pieChart(true);

        // add a selection listener
        mGuideBinding.pieChart.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(
                e: Entry,
                h: Highlight
            ) {
                if (e == null) {
                    return
                } else {
                    mGuideBinding.pieChart.setCenterText(
                        generateCenterSpannableText(
                            "总库存",
                            "120"
//                            e.data as String
                        )
                    )
                }
            }

            override fun onNothingSelected() {}
        })
        mGuideBinding.pieChart.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);
        val l: Legend = mGuideBinding.pieChart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.textSize = 24f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        // entry label styling
        mGuideBinding.pieChart.setEntryLabelColor(Color.BLACK)
        val tfRegular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf")
        mGuideBinding.pieChart.setEntryLabelTypeface(tfRegular)
        mGuideBinding.pieChart.setEntryLabelTextSize(24f)

        // 设置圆饼图数据
        setData()
    }

    private fun generateCenterSpannableText(
        charName: String,
        orgName: String
//        data: String
    ): SpannableString? {
//        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        val s = SpannableString(
            """
                $charName
                $orgName
            """.trimIndent()
        )
        s.setSpan(RelativeSizeSpan(1.5f), 0, charName.length, 0)
        //        s.setSpan(new StyleSpan(Typeface.NORMAL), charName.length(), s.length() - 15, 0);
//        s.setSpan(new ForegroundColorSpan(Color.GRAY), charName.length(), s.length() - 15, 0);
        s.setSpan(RelativeSizeSpan(2.5f), charName.length, s.length, 0)
        //        s.setSpan(new StyleSpan(Typeface.ITALIC), charName.length(), s.length(), 0);
        s.setSpan(
            ForegroundColorSpan(ColorTemplate.getHoloBlue()),
            charName.length,
            s.length,
            0
        )
        s.setSpan(RelativeSizeSpan(1.1f), s.length, s.length, 0)
        return s
    }

    /**
     * 一共5种选项5种颜色
     * 在库 出库 在车 下车 报废
     */
    val COLORS = intArrayOf(
        Color.rgb(0, 229, 255),  // 本柜已存
        Color.rgb(29, 233, 182)  // 本柜剩余,绿色
    )

    private fun setData() {
        val entries = java.util.ArrayList<PieEntry>()
        val colors = java.util.ArrayList<Int>()
        var totalNumber = 0

        val doaasierList = DossierOperatingService.getInstance().loadAll()

        val pieEntryZaiKu = PieEntry(
            0.toFloat(),
            "已存(" + doaasierList.size + ")",
            "已存(" + doaasierList.size + ")"
        )
        colors.add(COLORS[0])
        val pieEntryChuKu = PieEntry(
            0.toFloat(),
            "已存(" + (120 - doaasierList.size) + ")",
            "剩余(" + (120 - doaasierList.size) + ")"
        )
        colors.add(COLORS[1])

        pieEntryZaiKu.label = "已存(" + doaasierList.size + ")"
        pieEntryZaiKu.y = doaasierList.size.toFloat()
        pieEntryZaiKu.data = "已存(" + doaasierList.size + ")"
        entries.add(pieEntryZaiKu)

        pieEntryChuKu.label = "剩余(" + (120 - doaasierList.size) + ")"
        pieEntryChuKu.y = (120 - doaasierList.size).toFloat()
        pieEntryChuKu.data = "剩余(" + (120 - doaasierList.size) + ")"
        entries.add(pieEntryChuKu)

        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        dataSet.colors = colors
        dataSet.setSelectionShift(0f);

        // 非线型
//        PieData data = new PieData(dataSet);
//        data.setValueFormatter(new PercentFormatter(chart));
//        data.setValueTextSize(14f);
//        data.setValueTextColor(Color.WHITE);
//        data.setValueTypeface(tfLight);
//        chart.setData(data);
        // 非线型

        // 线型
        //dataSet.setSelectionShift(0f);

        // 非线型
//        val data = PieData(dataSet);
//        data.setValueFormatter(PercentFormatter(mGuideBinding.pieChart));
//        data.setValueTextSize(24f);
//        data.setValueTextColor(Color.WHITE);
//        val tfLight  = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf")
//        data.setValueTypeface(tfLight);
//        mGuideBinding.pieChart.data = data;
        // 非线型

        // 线型
        dataSet.valueLinePart1OffsetPercentage = 80f
        dataSet.valueLinePart1Length = 0.2f
        dataSet.valueLinePart2Length = 0.4f
        dataSet.isUsingSliceColorAsValueLineColor = false
//         dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //         dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(mGuideBinding.pieChart))
        data.setValueTextSize(24f)
        data.setValueTextColor(Color.WHITE)
        val tfLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf")
        data.setValueTypeface(tfLight)
        mGuideBinding.pieChart.data = data
        // 线型

        // undo all highlights
        mGuideBinding.pieChart.highlightValues(null)
        mGuideBinding.pieChart.invalidate()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.lav_pwd_login -> {
                showLoginDialog()
            }
            R.id.lav_face_login -> {
                showFaceDialog()
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

        if (mFaceRecognitionHttpServer.isAlive) {
            mFaceRecognitionHttpServer.stop();
            Log.e("人脸识别服务", "关闭服务");
        }

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

    private lateinit var mFaceRecognitionHttpServer: FaceRecognitionHttpServer

    // 人脸识别弹窗
    private fun showFaceDialog() {
        if (mFaceDialog == null) {
            mFaceDialog = AlertDialog.Builder(this).create()
            mFaceDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_face, null,
                false
            )
            mFaceDialog!!.setView(mFaceDialogBinding!!.root)
            mFaceDialog!!.setCancelable(true)
            mFaceDialog!!.setOnCancelListener {
                mFaceDialog!!.lottieAnimationView.cancelAnimation()
                mFaceDialog!!.dismiss()
            }
        }
        mFaceDialog!!.show()
        mFaceDialog!!.lottieAnimationView.playAnimation()

        val window = mFaceDialog!!.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        window!!.setLayout(
            resources.displayMetrics.widthPixels * 4 / 5,
            resources.displayMetrics.heightPixels * 2 / 5
        )
    }

    //关闭人脸弹窗
    private fun dismissFaceDialog() {
        if (mFaceDialog != null && mFaceDialog!!.isShowing) {
            mFaceDialog!!.lottieAnimationView.cancelAnimation()
            mFaceDialog!!.dismiss()
        }
    }

    private var isHandle = false

    private fun startFaceServer() {
        try {
            mFaceRecognitionHttpServer =
                FaceRecognitionHttpServer(8080, object : FaceRecognitionListener {
                    override fun success(result: String) {
                        Log.e("人脸识别服务", "收到信息 success")
                        if (!isHandle && mFaceDialog!!.isShowing && ActivityUtil.isTopActivity(
                                applicationContext, "com.zk.cabinet.activity.GuideActivity"
                            )
                        ) {
                            isHandle = true
                            // 登录
                            val resultSuccess = JSON.parseObject(result, ResultSuccess::class.java)

                            //  newUserZX.uuId = "123456" // todo 人脸ID, 要在人脸web地址中录入人脸的时候配置
                            val user =
                                UserService.getInstance().queryByUserUuId(resultSuccess.info.idCard)
                            if (user != null) {
                                // 把当前登录的人的 信息保存 存起来
                                val recordList = ArrayList<Record>()
                                recordList.add(Record(Key.LoginCodeTemp, user.userCode)) // zx
                                recordList.add(Record(Key.NameTemp, user.userName)) // 赵鑫
                                recordList.add(Record(Key.RoleNameTemp, user.modifyTime)) // 普通员工
                                mSpUtil.applyValue(recordList)

                                val msg = Message.obtain()
                                msg.what = LOGIN_BY_FACE
                                msg.obj = user.userName
                                mHandler.sendMessageDelayed(msg, 0)
                            } else {
                                val msg = Message.obtain()
                                msg.what = LOGIN_BY_PWD_FAIL
                                msg.obj = "用户名或密码错误"
                                mHandler.sendMessageDelayed(msg, 1000)
                            }
                        }
                    }

                    override fun noRegister(result: String) {
                        Log.e("人脸识别服务", "收到信息 noRegister")
                        // 提示人脸未注册 todo 还需语音提示
                        if (mFaceDialog!!.isShowing && ActivityUtil.isTopActivity(
                                applicationContext, "com.zk.cabinet.activity.GuideActivity"
                            )
                        ) {
                            val msg = Message.obtain()
                            msg.what = LOGIN_BY_FACE_NO_REGIST
                            msg.obj = "人脸未注册"
                            mHandler.sendMessageDelayed(msg, 0)
                        }
                    }

                    override fun heart(result: String) {
                        Log.e("人脸识别服务", "收到信息 heart")
                    }
                })

            // 启动人连识别web服务
            if (!mFaceRecognitionHttpServer.isAlive) {
                mFaceRecognitionHttpServer.start()
            }
            Log.e("人脸识别服务", "人脸识别服务-开启")
        } catch (e: Exception) {
            mFaceRecognitionHttpServer.stop()
            Log.e("人脸识别服务", "人脸识别服务-开启失败. e = $e")
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
            mDialogLogin!!.setCancelable(true)
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

    // todo 新的单机版本用户名密码登录
    private fun login(userCode: String, pwd: String) {
        mProgressDialog.show()

        val user = UserService.getInstance().queryByUserCode(userCode)
        if (user != null) {
            // 把当前登录的人的 信息保存 存起来
            val recordList = ArrayList<Record>()
            recordList.add(Record(Key.LoginCodeTemp, user.userCode)) // zx
            recordList.add(Record(Key.NameTemp, user.userName)) // 赵鑫
            recordList.add(Record(Key.RoleNameTemp, user.modifyTime)) // 普通员工
            mSpUtil.applyValue(recordList)

            val msg = Message.obtain()
            msg.what = LOGIN_BY_PWD_SUCCESS
            msg.obj = user.userName
            mHandler.sendMessageDelayed(msg, 1000)
        } else {
            val msg = Message.obtain()
            msg.what = LOGIN_BY_PWD_FAIL
            msg.obj = "用户名或密码错误"
            mHandler.sendMessageDelayed(msg, 1000)
        }
    }

    private fun loginOld(user: String, pwd: String) {
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
