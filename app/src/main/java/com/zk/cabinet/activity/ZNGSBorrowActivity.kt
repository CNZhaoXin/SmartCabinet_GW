package com.zk.cabinet.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.alibaba.fastjson.JSON
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.blankj.utilcode.util.LogUtils
import com.zk.cabinet.R
import com.zk.cabinet.adapter.ZNGSBorrowAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.LightControlRecord
import com.zk.cabinet.constant.SelfComm
import com.zk.cabinet.databinding.ActivityZngsBorrowBinding
import com.zk.cabinet.databinding.DialogEntryFinishBinding
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.db.LightControlRecordService
import com.zk.cabinet.entity.RequestArchivesBorrow
import com.zk.cabinet.entity.ResultGetToBorrowList
import com.zk.cabinet.net.JsonObjectRequestWithHeader
import com.zk.cabinet.net.NetworkRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.rfid.bean.LabelInfo
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.callback.CabinetInfoListener
import com.zk.rfid.callback.InventoryListener
import com.zk.rfid.ur880.UR880Entrance
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

/**
 * 智能组柜-借阅
 */
class ZNGSBorrowActivity : TimeOffAppCompatActivity(), AdapterView.OnItemClickListener,
    View.OnClickListener {
    private lateinit var mBinding: ActivityZngsBorrowBinding
    private lateinit var mHandler: MyHandler

    private lateinit var mProgressDialog: ProgressDialog
    private val labelInfoList = ArrayList<LabelInfo>()
    private var mFloor = -1

    companion object {
        private const val START_INVENTORY = 0x01
        private const val INVENTORY_VALUE = 0x02
        private const val CANCEL_INVENTORY = 0x03
        private const val END_INVENTORY = 0x04

        private const val OPEN_DOOR_RESULT = 0x05
        private const val GET_INFRARED_AND_LOCK = 0x06

        private const val SUBMIT_SUCCESS = 0x07
        private const val SUBMIT_ERROR = 0x08

        private const val OPEN_LIGHT_RESULT = 0x09

        private const val FINISH_ACTIVITY = 0x10

        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, ZNGSBorrowActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_zngs_borrow)
        mBinding.onClickListener = this
        mBinding.onItemClickListener = this
        mHandler = MyHandler(this)

        // 初始化Dialog
        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)
        // 显示操作人
        val name = mSpUtil.getString(SharedPreferencesUtil.Key.NameTemp, "xxx")
        mBinding.tvOperator.text = name
        // 开启界面自动关闭
        isAutoFinish = true
        timerStart()
        // 初始化列表数据
        initAdapter()
        // 获取待借阅列表数据
        mProgressDialog.setMessage("正在获取待借阅档案列表...")
        mProgressDialog.show()
        loadListHandler.postDelayed(loadListRunnable, 1000)

        UR880Entrance.getInstance().addOnCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().addOnInventoryListener(mInventoryListener)

        // 1413的测试柜子固件不一样,要调用开门
//        UR880Entrance.getInstance().send(
//            UR880SendInfo.Builder().openDoor("204742930", 0x00).build()
//        )
    }

    private val loadListHandler = Handler()
    private val loadListRunnable = Runnable {
        run {
            getToBorrowList()
        }
    }

    private lateinit var mAdapter: ZNGSBorrowAdapter
    private var mList = ArrayList<ResultGetToBorrowList.DataBean>()

    private fun initAdapter() {
        mAdapter = ZNGSBorrowAdapter(this, mList)
        mBinding.listView.adapter = mAdapter
    }

    /**
     * 获取当前登录人待借的档案信息
     */
    private fun getToBorrowList() {
        val requestUrl = NetworkRequest.instance.mGetToBorrowList
        LogUtils.e("获取当前登录人待借的档案信息-requestUrl:$requestUrl")

        val jsonObjectRequest = JsonObjectRequestWithHeader(
            Request.Method.GET, requestUrl, { response ->
                LogUtils.e("获取当前登录人待借的档案信息-返回结果:", "$response")

                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            val result: ResultGetToBorrowList = JSON.parseObject(
                                "$response", ResultGetToBorrowList::class.java
                            )
                            mList = result.data as ArrayList<ResultGetToBorrowList.DataBean>
                            if (mList.size > 0) {
                                mProgressDialog.dismiss()
                                mAdapter.setList(mList)
                                mAdapter.notifyDataSetChanged()
                                mBinding.llNoData.visibility = View.GONE

                                // 过滤出属于当前档案组柜(多个柜子)的可操作的待借阅档案
                                val curCabinetDossier = ArrayList<ResultGetToBorrowList.DataBean>()
                                val deviceList = DeviceService.getInstance().loadAll()
                                for (entity in mList) {
                                    for (device in deviceList) {
                                        if (entity.cabinetEquipmentId == device!!.deviceId) {
                                            curCabinetDossier.add(entity)
                                        }
                                    }
                                }
                                // 如果有属于当前组柜(多个柜子)的档案,才能进行亮灯操作,亮灯按钮才可点击
                                if (curCabinetDossier.size > 0) {
                                    showWarningToast("该组柜有「${curCabinetDossier.size}」份档案可借阅")
                                    speek("该组柜有${curCabinetDossier.size}份档案可借阅")
                                    mBinding.btnOpenLight.isEnabled = true
                                    mBinding.btnOpenLight.setBackgroundDrawable(getDrawable(R.drawable.selector_menu_orange_normal))
                                } else {
                                    showWarningToast("该组柜暂无档案可借阅")
                                    speek("该组柜暂无档案可借阅")
                                }
                            } else {
                                // 这里要设置一张无数据的空图片
                                mProgressDialog.dismiss()
                                speek("暂无待借阅档案")
                                showWarningToast("暂无待借阅档案")
                                mBinding.tvNoDate.text = "暂无待借阅档案"
                                mBinding.llNoData.visibility = View.VISIBLE
                            }
                        } else {
                            // 这里要设置一张无数据的空图片
                            mProgressDialog.dismiss()
                            speek("获取待借阅的档案列表-请求失败")
                            showErrorToast(response.getString("msg"))
                            mBinding.tvNoDate.text = "暂无待借阅档案"
                            mBinding.llNoData.visibility = View.VISIBLE
                        }
                    } else {
                        // 这里要设置一张无数据的空图片
                        mProgressDialog.dismiss()
                        speek("获取待借阅的档案列表-请求失败")
                        showErrorToast("获取待借阅的档案列表-请求失败")
                        mBinding.tvNoDate.text = "暂无待借阅档案"
                        mBinding.llNoData.visibility = View.VISIBLE
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    // 这里要设置一张无数据的空图片
                    mProgressDialog.dismiss()
                    speek("获取待借阅的档案列表-请求失败")
                    showErrorToast("获取待借阅的档案列表-请求失败")
                    mBinding.tvNoDate.text = "暂无待借阅档案"
                    mBinding.llNoData.visibility = View.VISIBLE
                }
            },
            { error ->
                val msg =
                    if (error != null)
                        if (error.networkResponse != null)
                            "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                        else
                            "errorCode: -1 VolleyError: $error"
                    else {
                        "errorCode: -1 VolleyError: 未知"
                    }
                // 这里要设置一张无数据的空图片
                mProgressDialog.dismiss()
                speek("获取待借阅的档案列表-请求失败")
                showErrorToast(msg)
                mBinding.tvNoDate.text = "暂无待借阅档案"
                mBinding.llNoData.visibility = View.VISIBLE
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            GET_INFRARED_AND_LOCK -> {
                val data = msg.data
                val boxStateList = data.getIntegerArrayList("lock")
                val infraredStateList = data.getIntegerArrayList("infrared")

                Log.e("zx-档案组柜借阅-infraredState", "$infraredStateList")
                Log.e("zx-档案组柜借阅-boxStateList", "$boxStateList")

                // 这里只要红外被触发和门状态都会调用, 哪层的红外被触发 [1,2,3,4,5], 但是多层的时候不返回数据,只有一层不触发才返回数据
                if (infraredStateList!!.isNotEmpty()) {
                    mFloor = infraredStateList[0]
                }

                // 档案组柜无门，无需判断状态
                // 这里只要红外被触发和门状态都会调用, 门开的状态 boxStateList: [1] , 门关闭的状态 boxStateList: []
//                if (boxStateList!!.isEmpty()) {
//                }
            }
            // 档案组柜没有门,只需要亮灯
            OPEN_LIGHT_RESULT -> {
                Log.e("zx-档案组柜借阅-", "借阅的档案亮灯-关闭界面倒计时")
                showWarningToast(msg.obj.toString())
                speek(msg.obj.toString())
                mProgressDialog.dismiss()
                // 亮灯成功后亮灯按钮不可用
                mBinding.btnOpenLight.isEnabled = false
                mBinding.btnOpenLight.setBackgroundDrawable(getDrawable(R.drawable.shape_btn_un_enable))
                // 亮灯后倒计时关闭,需要时间进行档案操作
                isAutoFinish = false
                timerCancel()
            }

            START_INVENTORY -> { // 开始盘点
                mProgressDialog.setMessage("正在扫描,请稍后...")
                mProgressDialog.show()
            }
            INVENTORY_VALUE -> {
                val labelInfo = msg.obj as LabelInfo
                labelInfoList.add(labelInfo)
                Log.e("档案组柜待借阅", "labelInfo.deviceID: ${labelInfo.deviceID}")
                Log.e("档案组柜待借阅", "labelInfo.antennaNumber: ${labelInfo.antennaNumber}")
                Log.e("档案组柜待借阅", "labelInfo.fastID: ${labelInfo.fastID}")
                Log.e("档案组柜待借阅", "labelInfo.rssi: ${labelInfo.rssi}")
                Log.e("档案组柜待借阅", "labelInfo.operatingTime: ${labelInfo.operatingTime}")
                Log.e("档案组柜待借阅", "labelInfo.epcLength: ${labelInfo.epcLength}")
                Log.e("档案组柜待借阅", "labelInfo.epc: ${labelInfo.epc}")
                Log.e("档案组柜待借阅", "labelInfo.tid: ${labelInfo.tid}")
                Log.e("档案组柜待借阅", "labelInfo.inventoryNumber: ${labelInfo.inventoryNumber}")
            }
            CANCEL_INVENTORY -> {// 停止盘点
            }
            END_INVENTORY -> {
                // 某柜某层触发红外扫描结束后EPC编码集合与数据列表中数据EPC比对
                // 借阅,一般操作是先拿出来,然后会触发红外,触发读写器进行读写,此时应该判断哪个RFID不在了,不在才进行确认,只要发现不在就确认,发现在就取消确认

                // 遍历扫描到的标签数据集合,可能会存在多个柜子的(经过测试一次只会上报一个柜子的数据)
                val deviceScanHashSet = HashSet<String>()
                for (labelInfo in labelInfoList) {
                    deviceScanHashSet.add(labelInfo.deviceID)
                }
                LogUtils.e(
                    "档案组柜待借阅-触发扫描的读写器设备IDS:",
                    JSON.toJSONString(deviceScanHashSet)
                )

                // 配置的柜子所有设备ID set集合
                val deviceSettingHashSet = HashSet<String>()
                val deviceSettingList = DeviceService.getInstance().loadAll()
                for (deviceSetting in deviceSettingList) {
                    deviceSettingHashSet.add(deviceSetting.deviceId)
                }
                LogUtils.e(
                    "档案组柜待借阅-配置的读写器设备IDS:",
                    JSON.toJSONString(deviceSettingHashSet)
                )

                // 针对被识别的某柜某层的标签数据的处理 1.是否被扫描到 2.是否是同一层 3.是否是同一个位置
                // 取出的待借阅的档案
                val selectedList = ArrayList<ResultGetToBorrowList.DataBean>()
                // 未取出的待借阅的档案
                val noSelectedList = ArrayList<ResultGetToBorrowList.DataBean>()
                // 放错位置的待借阅的档案
                val errorPositionList = ArrayList<ResultGetToBorrowList.DataBean>()

                // 过滤:是否是当前触发的层的本柜的待借阅的档案,每层的数据必须要单独处理
                for (entity in mList) {
                    if (deviceScanHashSet.contains(entity.cabinetEquipmentId)
                        && deviceSettingHashSet.contains(entity.cabinetEquipmentId)
                    ) {
                        // 标记该档案EPC是否被扫描到了,扫描到说明要借阅的档案是有的
                        var isScan = false
                        // 标记该档案是否放入了正确位置
                        var isCorrectPosition = false

                        for (labelInfo in labelInfoList) {
                            if (labelInfo.epc == entity.rfid) { // 1.先判断借阅得档案RFID有没有被扫描到
                                // 扫描到了,判定为没有拿出来,还在柜中
                                isScan = true

                                if (entity.rowNo == mFloor) {
                                    // 天线号是从0开始的,且需要根据天线号进行计算层数和灯位
                                    labelInfo.antennaNumber = labelInfo.antennaNumber + 1
                                    val floor =
                                        if (labelInfo.antennaNumber % 24 == 0) labelInfo.antennaNumber / 24 else (labelInfo.antennaNumber / 24 + 1)
                                    val light =
                                        if (labelInfo.antennaNumber % 24 == 0) 24 else labelInfo.antennaNumber % 24
                                    LogUtils.e(
                                        "labelInfo.epc-" + labelInfo.epc,
                                        "labelInfo.antennaNumber:" + labelInfo.antennaNumber + ",floor:" + floor + ",light:" + light
                                    )

                                    if (floor == entity.rowNo) { // 2.再判断扫描到档案RFID的天线计算出的,层数是否和放入的档案层数一致
                                        for (lampList in entity.lampList) {  // 3.再判断扫描到档案RFID的天线计算出的,灯位是否和放入的档案灯位一致
                                            if (light == lampList) {
                                                isCorrectPosition = true
                                                break
                                            }
                                        }
                                    }
                                }

                                break
                            }
                        }

                        if (isScan) { // 档案扫描到了
                            if (isCorrectPosition) { // 档案位置正确(属于该层该灯位)
                                // 该层未取出的待借阅档案
                                entity.isSelect = false
                                noSelectedList.add(entity)
                            } else { // 档案位置不正确(2种情况, 1.属于该层,灯位不正确 2.不属于该层,灯位不正确)
                                // 场景:从原先待借阅那层拿出来,放到该层别的位置,或直接放到别的层被扫描到了
                                if (entity.rowNo == mFloor) {
                                    entity.isSelect = false
                                    errorPositionList.add(entity)
                                } else {
                                    entity.isSelect = false
                                    errorPositionList.add(entity)
                                }
                            }
                        } else { // 档案没有被扫描到,但属于该层
                            if (entity.rowNo == mFloor) {
                                // 该层取出的待借阅档案
                                entity.isSelect = true
                                selectedList.add(entity)
                            } else {
                                // 档案没有被扫描到,也不属于该层
                            }
                        }
                    }

                    // 假如这一层只有这一份待借阅档案, 然后还被拿出来,那么读写器上报的就是空的数据,什么都没有,这个时候就判定该档案被拿出了
                    if (deviceScanHashSet.isEmpty()) {
                        if (deviceSettingHashSet.contains(entity.cabinetEquipmentId) && entity.rowNo == mFloor) {
                            // 该层取出的待借阅档案
                            entity.isSelect = true
                            selectedList.add(entity)
                        }
                    }
                }

                if (selectedList.size > 0) {
                    if (noSelectedList.size > 0) {
                        if (errorPositionList.size > 0) {
                            showWarningToast(
                                "【${mFloor}】层有【${selectedList.size}】份待借阅档案已取出,还有【${noSelectedList.size}】份待借阅档案还在原库位未取出" +
                                        ",还有【${errorPositionList.size}】份待借阅档案已不在原库位,请取出并触发原库位所在层红外感应或直接放回原库位"
                            )
                            speek(
                                "${mFloor}层有${selectedList.size}份待借阅档案已取出,还有${noSelectedList.size}份待借阅档案还在原库位未取出" +
                                        ",还有${errorPositionList.size}份待借阅档案已不在原库位,请取出并触发原库位所在层红外感应或直接放回原库位"
                            )
                        } else {
                            showWarningToast("【${mFloor}】层有【${selectedList.size}】份待借阅档案已取出,还有【${noSelectedList.size}】份待借阅档案还在原库位未取出")
                            speek("${mFloor}层有${selectedList.size}份待借阅档案已取出,还有${noSelectedList.size}份待借阅档案还在原库位未取出")
                        }
                    } else {
                        if (errorPositionList.size > 0) {
                            showWarningToast("【${mFloor}】层有【${selectedList.size}】份待借阅档案已取出,还有【${errorPositionList.size}】份待借阅档案已不在原库位,请取出并触发原库位所在层红外感应或直接放回原库位")
                            speek("${mFloor}层有${selectedList.size}份待借阅档案已取出,还有${errorPositionList.size}份待借阅档案已不在原库位,请取出并触发原库位所在层红外感应或直接放回原库位")
                        } else {
                            showWarningToast("【${mFloor}】层有【${selectedList.size}】份待借阅档案已取出")
                            speek("${mFloor}层有${selectedList.size}份待借阅档案已取出")
                        }
                    }
                } else {
                    if (noSelectedList.size > 0) {
                        if (errorPositionList.size > 0) {
                            showWarningToast("【${mFloor}】层有【${noSelectedList.size}】份待借阅档案还在原库位未取出,还有【${errorPositionList.size}】份待借阅档案已不在原库位,请取出并触发原库位所在层红外感应或直接放回原库位")
                            speek("${mFloor}层有${noSelectedList.size}份待借阅档案还在原库位未取出,还有${errorPositionList.size}份待借阅档案已不在原库位,请取出并触发原库位所在层红外感应或直接放回原库位")
                        } else {
                            showWarningToast("【${mFloor}】层有【${noSelectedList.size}】份待借阅档案还在原库位未取出")
                            speek("${mFloor}层有${noSelectedList.size}份待借阅档案还在原库位未取出")
                        }

                    } else {
                        if (errorPositionList.size > 0) {
                            showWarningToast("【${mFloor}】层有【${errorPositionList.size}】份待借阅档案已不在原库位,请取出并触发原库位所在层红外感应或直接放回原库位")
                            speek("${mFloor}层有${errorPositionList.size}份待借阅档案已不在原库位,请取出并触发原库位所在层红外感应或直接放回原库位")
                        } else {
                            if (mFloor != -1) {
                                showWarningToast("【${mFloor}】层没有要取出的待借阅档案")
                                speek("${mFloor}层没有要取出的待借阅档案")
                            }
                        }
                    }
                }

                // 针对所有数据的处理
                var isHasSelect = false;
                for (entity in mList) {
                    if (entity.isSelect) {
                        isHasSelect = true
                        break
                    }
                }

                if (isHasSelect) {
                    mBinding.btnCommit.background =
                        resources.getDrawable(R.drawable.selector_menu_green_normal)
                    mBinding.btnCommit.isEnabled = true
                } else {
                    mBinding.btnCommit.background =
                        resources.getDrawable(R.drawable.shape_btn_un_enable)
                    mBinding.btnCommit.isEnabled = false
                }

                LogUtils.e("档案组柜待借阅-盘点到的标签数据-", JSON.toJSONString(labelInfoList))
                mProgressDialog.dismiss()
                mAdapter.notifyDataSetChanged()
                labelInfoList.clear()
            }

            SUBMIT_SUCCESS -> { // 借阅成功
                mProgressDialog.dismiss()
                showWarningToast("【${msg.obj}】份档案借阅成功")
                speek("${msg.obj}份档案借阅成功")

                // 由于档案柜灭灯不能指定灯位灭灯，那借阅成功就不能灭某个灯的操作，只能在界面关闭时判断哪些柜子灯打开过，就灭哪个柜子的所有灯
                // 清除掉列表中借阅成功的档案
                val iterator = mList.iterator()
                while (iterator.hasNext()) {
                    if (iterator.next().isSelect)
                        iterator.remove()
                }

                // 借阅按钮不可点击
                mBinding.btnCommit.background =
                    resources.getDrawable(R.drawable.shape_btn_un_enable)
                mBinding.btnCommit.isEnabled = false

                // 借阅成功,且没有任何待借阅条目了就主动关闭掉界面
                if (mList.size == 0) {
                    mAdapter.notifyDataSetChanged()
                    val message = Message.obtain()
                    message.what = FINISH_ACTIVITY
                    mHandler.sendMessageDelayed(message, 5000)
                } else {
                    // 亮灯后关闭倒计时，借阅成功开启倒计时
                    isAutoFinish = true
                    timerStart()
                    mAdapter.notifyDataSetChanged()
                }
            }
            SUBMIT_ERROR -> {// 借阅失败
                mProgressDialog.dismiss()
                showErrorToast("${msg.obj}")
            }

            FINISH_ACTIVITY -> {// 关闭界面
                mProgressDialog.dismiss()
                finish()
            }
        }
    }

    /**
     * 批量档案取件
     * 接口地址：Post /api/pad/archivesBorrow
     */
    private fun borrowSubmission(equipmentId: String) {
        mProgressDialog.setMessage("正在提交...")
        mProgressDialog.show()

        val requestUrl = NetworkRequest.instance.mArchivesBorrow
        LogUtils.e("借阅提交-请求URL:$requestUrl")

        // rfid string 档案rfid
        // aioId string 一体机或者智能柜操作屏id
        val requestArchivesBorrow = RequestArchivesBorrow()
        // 操作屏的设备ID
        requestArchivesBorrow.aioId = equipmentId
        // 确认借阅的档案RFID数组
        val selectList = ArrayList<String>()
        for (entity in mList) {
            if (entity.isSelect) {
                selectList.add(entity.rfid)
            }
        }
        val array = selectList.toArray(arrayOfNulls<String>(selectList.size)) as Array<String>
        requestArchivesBorrow.rfids = array

        val jsonObject = JSONObject(JSON.toJSONString(requestArchivesBorrow))
        LogUtils.e("借阅提交-请求参数:$jsonObject")

        val jsonObjectRequest = JsonObjectRequestWithHeader(
            Request.Method.POST, requestUrl, jsonObject,
            { response ->
                LogUtils.e("借阅提交-请求结果:", "$response")
                try {
                    if (response != null) {
                        val code = response.getInt("code")
                        if (200 == code) {
                            sendDelayMessage(SUBMIT_SUCCESS, array.size.toString())
                        } else {
                            sendDelayMessage(SUBMIT_ERROR, response.getString("msg"))
                        }
                    } else {
                        sendDelayMessage(SUBMIT_ERROR, "借阅提交-失败")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    sendDelayMessage(SUBMIT_ERROR, "借阅提交-失败")
                }
            },
            { error ->
                val msg = if (error != null)
                    if (error.networkResponse != null)
                        "errorCode: ${error.networkResponse.statusCode} VolleyError: $error"
                    else
                        "errorCode: -1 VolleyError: $error"
                else {
                    "errorCode: -1 VolleyError: 未知"
                }
                showErrorToast(msg)
            })

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        NetworkRequest.instance.add(jsonObjectRequest)
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mBinding.outboundOperatingCountdownTv.text = millisUntilFinished.toString()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // 点击暂无数据,重新加载
            R.id.ll_no_data -> {
                mBinding.llNoData.visibility = View.GONE
                mProgressDialog.setMessage("正在获取待借阅档案列表...")
                mProgressDialog.show()
                loadListHandler.postDelayed(loadListRunnable, 1000)
            }
            // 亮所有待借阅档案的灯
            R.id.btn_open_light -> {
                mProgressDialog.setMessage("正在亮灯，请稍后...")
                mProgressDialog.show()

                // 亮借阅档案数据的相应灯位,一层一层的亮灯(注意:这个必须一层层的处理亮灯,一次性发送指令)
                // 档案组柜存在多个柜子的档案,要亮不同柜子的灯
                // 区分档案所属的柜子,再区分某个柜子1-5层,再逐柜逐层亮灯
                val deviceIDSet = HashSet<String>()
                val deviceList = DeviceService.getInstance().loadAll()
                for (entity in mList) {
                    for (device in deviceList) {
                        if (entity.cabinetEquipmentId == device!!.deviceId) {
                            deviceIDSet.add(entity.cabinetEquipmentId)
                        }
                    }
                }
                LogUtils.e("亮灯-需要亮灯的所有柜子IDs:deviceIDSet:$deviceIDSet")

                for (deviceID in deviceIDSet) {
                    var isBigLight = false
                    for (floor in 1..5) {
                        val lightsSet = HashSet<Int>()
                        for (entity in mList) {
                            if (deviceID == entity.cabinetEquipmentId && entity.rowNo == floor) {
                                if (entity.lampList != null && entity.lampList.size > 0) {
                                    for (light in entity.lampList) {
                                        lightsSet.add(light)
                                    }
                                }
                            }
                        }

                        val lights = ArrayList(lightsSet)
                        if (lights.size > 0) {
                            // 该柜有需要亮灯的档案，就需要亮大灯
                            isBigLight = true
                            // 亮库位灯
                            UR880Entrance.getInstance().send(
                                UR880SendInfo.Builder().turnOnLight(deviceID, floor, lights).build()
                            )
                            LogUtils.e(
                                "亮灯-需要亮灯的某个柜子某层灯:",
                                deviceID,
                                floor,
                                JSON.toJSONString(lights)
                            )
                        }
                    }

                    // 这里需要亮当前柜子的大灯,同个柜子只亮一次
                    if (isBigLight) {
                        val lights = ArrayList<Int>()
                        lights.add(1)
                        UR880Entrance.getInstance().send(
                            UR880SendInfo.Builder().turnOnLight(deviceID, 6, lights).build()
                        )
                        LogUtils.e("档案组柜-借阅-亮大灯-亮大灯的柜子:", deviceID)

                        // 档案组柜-保存某个柜子的亮大灯记录，查询有没有该柜亮灯记录,没有就新增,有就记录数+1
                        val lightControlRecords =
                            LightControlRecordService.getInstance().queryListByDeviceID(deviceID)
                        if (lightControlRecords != null && lightControlRecords.size > 0) {
                            // 存在亮大灯记录,更新记录数+1
                            val lightControlRecord = lightControlRecords[0]
                            lightControlRecord.num = lightControlRecord.num + 1
                            LightControlRecordService.getInstance().update(lightControlRecord)

                            LogUtils.e("档案组柜-借阅-该柜亮灯亮大灯记录已存在,无需再添加")
                        } else {
                            // 不存在,新增一条某柜亮大灯记录
                            val bigLightRecord = LightControlRecord(null, deviceID, "", 1)
                            LightControlRecordService.getInstance().insert(bigLightRecord)

                            LogUtils.e("档案组柜-借阅-新增该柜亮大亮记录", JSON.toJSONString(bigLightRecord))
                        }

                    }
                }

                // 亮灯记录打印
                val allRecords = LightControlRecordService.getInstance().loadAll()
                if (allRecords != null && allRecords.size > 0) {
                    LogUtils.e("档案组柜-借阅-亮灯记录:", allRecords.size, JSON.toJSONString(allRecords))
                    // todo 档案组柜亮组大灯，拿第一个组柜进行亮组大灯，因为第一个组柜是接的组大灯
                    val devices = DeviceService.getInstance().loadAll()
                    if (devices != null && devices.size > 0) {
                        val device = devices[0]
                        // 亮组大灯（发送6层3号灯就是亮组大灯）
                        val lights = java.util.ArrayList<Int>()
                        lights.add(3)
                        UR880Entrance.getInstance().send(
                            UR880SendInfo.Builder().turnOnLight(device.deviceId, 6, lights)
                                .build()
                        )
                        LogUtils.e(
                            "档案组柜-借阅-亮组大灯-该档案组柜配置的第一个柜子设备ID",
                            device.deviceId
                        )
                    }
                } else {
                    LogUtils.e("档案组柜-借阅-亮灯记录: null")
                }

                sendDelayMessage(OPEN_LIGHT_RESULT, "库位已亮灯")
            }

            // 借阅提交
            R.id.btn_commit -> {
                val equipmentId = mSpUtil.getString(SharedPreferencesUtil.Key.EquipmentId, "")
                if (!TextUtils.isEmpty(equipmentId)) {
                    borrowSubmission(equipmentId!!)
                } else {
                    showErrorToast("请先配置操作屏设备ID")
                }
            }

            R.id.btn_back -> {
                // 如果已经有确认过的档案,退出需要提醒,没有就直接退出
                val selectedList = ArrayList<ResultGetToBorrowList.DataBean>()
                for (select in mList) {
                    if (select.isSelect) {
                        selectedList.add(select)
                    }
                }
                if (selectedList.size > 0) {
                    showEntryFinishDialog(selectedList)
                } else {
                    finish()
                }
            }
            // 确认关闭弹窗的取消按钮
            R.id.btn_cancel -> {
                if (mDialogFinish != null && mDialogFinish!!.isShowing)
                    mDialogFinish!!.dismiss()
            }
            // 确认关闭弹窗的确认按钮
            R.id.btn_entry -> {
                if (mDialogFinish != null && mDialogFinish!!.isShowing)
                    mDialogFinish!!.dismiss()
                finish()
            }
        }
    }

    // 确认关闭弹窗
    private var mDialogFinishBinding: DialogEntryFinishBinding? = null
    private var mDialogFinish: AlertDialog? = null

    private fun showEntryFinishDialog(selectedList: ArrayList<ResultGetToBorrowList.DataBean>) {
        if (mDialogFinish == null) {
            mDialogFinish = AlertDialog.Builder(this).create()
            mDialogFinishBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.dialog_entry_finish,
                null,
                false
            )
            mDialogFinishBinding!!.onClickListener = this
            mDialogFinish!!.setView(mDialogFinishBinding!!.root)
            mDialogFinish!!.setCancelable(true)
        }
        mDialogFinishBinding!!.tvContent.text = "您有【${selectedList.size}】份待借阅的档案需要点击借阅进行提交,确定退出界面吗?"
        mDialogFinish!!.show()

        val window = mDialogFinish!!.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        // 一体机4 横屏显示需要适配
        val deviceName = mSpUtil.getString(SharedPreferencesUtil.Key.DeviceName, "").toString()
        if (SelfComm.DEVICE_NAME[4].equals(deviceName)) {
            window!!.setLayout(
                resources.displayMetrics.widthPixels * 3 / 5,
                resources.displayMetrics.heightPixels * 3 / 5
            )
        } else {
            window!!.setLayout(
                resources.displayMetrics.widthPixels * 2 / 3,
                resources.displayMetrics.heightPixels * 2 / 5
            )
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    private fun sendDelayMessage(msgWhat: Int, msgObj: String) {
        val message = Message.obtain()
        message.what = msgWhat
        message.obj = msgObj
        mHandler.sendMessageDelayed(message, 1000)
    }

    private class MyHandler(activity: ZNGSBorrowActivity) :
        Handler() {
        private val weakReference = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            weakReference.get()!!.handleMessage(msg)
        }
    }

    private val mCabinetInfoListener = object : CabinetInfoListener {
        override fun getInfraredOrLockState(p0: ArrayList<Int>?, p1: ArrayList<Int>?) {
            val message = Message.obtain()
            val bundle = Bundle()
            bundle.putIntegerArrayList("lock", p0)
            bundle.putIntegerArrayList("infrared", p1)
            message.what = GET_INFRARED_AND_LOCK
            message.data = bundle
            mHandler.sendMessage(message)
        }

        override fun unlockResult(p0: Int) {
            // 档案组柜没有门
//            val message = Message.obtain()
//            message.obj = "开门成功"
//            message.what = OPEN_DOOR_RESULT
//            mHandler.sendMessage(message)
        }

        override fun turnOnLightResult(p0: Int) {

        }

    }

    private val mInventoryListener = object : InventoryListener {
        override fun startInventory(p0: Int) {
            mHandler.sendEmptyMessage(START_INVENTORY)
        }

        override fun inventoryValue(p0: LabelInfo?) {
            val msg = Message.obtain()
            msg.what = INVENTORY_VALUE
            msg.obj = p0
            mHandler.sendMessage(msg)
        }

        override fun cancel(p0: Int, p1: Int) {
            mHandler.sendEmptyMessage(CANCEL_INVENTORY)
        }

        override fun endInventory(p0: Int) {
            mHandler.sendEmptyMessage(END_INVENTORY)
        }
    }

    override fun onDestroy() {
        UR880Entrance.getInstance().removeCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().removeInventoryListener(mInventoryListener)

        // 档案组柜,借阅界面关闭时,亮过灯得档案柜灯全灭,只要亮过
        val lightRecordLists = LightControlRecordService.getInstance().loadAll()
        if (lightRecordLists != null && lightRecordLists.size > 0) {
            for (lightRecord in lightRecordLists) {
                val lights = ArrayList<Int>()
                lights.add(2)
                UR880Entrance.getInstance().send(
                    UR880SendInfo.Builder().turnOnLight(lightRecord.deviceID, 6, lights).build()
                )
                LogUtils.e("档案组柜-借阅-灭大灯(灭全灯)", JSON.toJSONString(lightRecord))

                lightRecord.num = lightRecord.num - 1
                if (lightRecord.num == 0) {
                    LightControlRecordService.getInstance().delete(lightRecord)
                } else {
                    LightControlRecordService.getInstance().update(lightRecord)
                }
            }

            // todo 档案组柜灭组大灯，拿第一个组柜进行灭组大灯，因为第一个组柜是接的组大灯
            val devices = DeviceService.getInstance().loadAll()
            if (devices != null && devices.size > 0) {
                val device = devices[0]
                // 灭组大灯（发送6层2号灯就是全灭当前柜子所有灯）
                val lights = java.util.ArrayList<Int>()
                lights.add(2)
                UR880Entrance.getInstance().send(
                    UR880SendInfo.Builder().turnOnLight(device.deviceId, 6, lights).build()
                )
                LogUtils.e(
                    "档案组柜-借阅-灭组大灯-该档案组柜配置的第一个柜子设备ID",
                    device.deviceId
                )
            }
        }

        // 亮灯记录打印
        val allRecords = LightControlRecordService.getInstance().loadAll()
        if (allRecords != null && allRecords.size > 0) {
            LogUtils.e("档案组柜-借阅-亮灯记录:", allRecords.size, JSON.toJSONString(allRecords))
        } else {
            LogUtils.e("档案组柜-借阅-亮灯记录: null")
        }

        super.onDestroy()
    }
}