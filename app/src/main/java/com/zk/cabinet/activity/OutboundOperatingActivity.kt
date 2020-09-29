package com.zk.cabinet.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.alibaba.fastjson.JSON
import com.zk.cabinet.R
import com.zk.cabinet.adapter.OutboundAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.bean.DossierOperating
import com.zk.cabinet.databinding.ActivityOutboundOperatingBinding
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.db.DossierOperatingService
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.rfid.bean.LabelInfo
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.callback.CabinetInfoListener
import com.zk.rfid.callback.InventoryListener
import com.zk.rfid.ur880.UR880Entrance
import java.lang.ref.WeakReference

class OutboundOperatingActivity : TimeOffAppCompatActivity(), AdapterView.OnItemClickListener,
    View.OnClickListener {
    private lateinit var mOutboundBinding: ActivityOutboundOperatingBinding
    private lateinit var mHandler: OutboundOperatingHandler
    private lateinit var mProgressDialog: ProgressDialog
    private var mDevice: Device? = null
    private var outBoundList = ArrayList<DossierOperating>()
    private lateinit var mDossierAdapter: OutboundAdapter
    private val labelInfoList = ArrayList<LabelInfo>()
    private var mFloor = -1

    companion object {
        private const val OPEN_DOOR_RESULT = 0x01
        private const val START_INVENTORY = 0x02
        private const val INVENTORY_VALUE = 0x03
        private const val CANCEL_INVENTORY = 0x04
        private const val END_INVENTORY = 0x05
        private const val GET_INFRARED_AND_LOCK = 0x06
        private const val SUBMITTED_SUCCESS = 0x07
        private const val SUBMITTED_FAIL = 0x08

        private const val GET_OUTBOUND_SUCCESS = 0x09
        private const val FINISH = 0x10

        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, OutboundOperatingActivity::class.java)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            GET_OUTBOUND_SUCCESS -> {
                mProgressDialog.dismiss()
                outBoundList = msg.obj as ArrayList<DossierOperating>
                mDossierAdapter.setList(outBoundList)
                mDossierAdapter.notifyDataSetChanged()

                // todo 取档时进入直接先开一次柜门
                UR880Entrance.getInstance().send(
                    UR880SendInfo.Builder().openDoor(mDevice!!.deviceId, 0).build()
                )
            }

            GET_INFRARED_AND_LOCK -> {
                val data = msg.data
                val boxStateList = data.getIntegerArrayList("lock")
                val infraredStateList = data.getIntegerArrayList("infrared")

                Log.e("zx-出库操作-boxStateList", "$boxStateList")
                Log.e("zx-出库操作-infraredState", "$infraredStateList")

                // 这里只要红外被触发都会被调用, 哪层的红外被触发 [1,2,3,4,5], 但是多层的时候不返回数据,只有一层不触发才返回数据
                if (infraredStateList!!.isNotEmpty()) {
                    mFloor = infraredStateList[0]
                }

                // 这里只要红外被触发都会被调用, 门开的状态 boxStateList: [1] , 门关闭的状态 boxStateList: []
                if (boxStateList!!.isEmpty()) {
                    isAutoFinish = true
                    timerStart()
                    Log.e("zx-出库操作-", "门关闭-开启界面倒计时")

                    speek("柜门已关闭")
                }
            }
            OPEN_DOOR_RESULT -> {
                if (mProgressDialog.isShowing)
                    mProgressDialog.dismiss()

                // showToast(msg.obj.toString())
                Log.e("zx-出库操作-", "门开启-关闭界面倒计时-出库的档案进行开灯")

                // 门开启后倒计时关闭
                isAutoFinish = false
                timerCancel()

                // 亮出库列表数据的相应灯位
                for (floor in 1..5) {
                    val lights = ArrayList<Int>()
                    for (dossierOperating in outBoundList) {
                        if (dossierOperating.floor == floor) {
                            lights.add(dossierOperating.light)
                            speek("柜门已开启,您要取的人事档案,${dossierOperating.inputName},在${dossierOperating.floor}层${dossierOperating.light}号")
                        }
                    }
                    UR880Entrance.getInstance().send(
                        UR880SendInfo.Builder().turnOnLight(mDevice!!.deviceId, floor, lights)
                            .build()
                    )
                }

            }
            START_INVENTORY -> {
                speek("正在盘点,请稍后")
                // Toast.makeText(this, "开始盘点", Toast.LENGTH_SHORT).show()
            }
            INVENTORY_VALUE -> {
                val labelInfo = msg.obj as LabelInfo
                labelInfoList.add(labelInfo)
                Log.e("zx-出库-", "-------------labelInfo.deviceID: ${labelInfo.deviceID}")
                Log.e("zx-出库-", "-------------labelInfo.antennaNumber: ${labelInfo.antennaNumber}")
                Log.e("zx-出库-", "-------------labelInfo.fastID: ${labelInfo.fastID}")
                Log.e("zx-出库-", "-------------labelInfo.rssi: ${labelInfo.rssi}")
                Log.e("zx-出库-", "-------------labelInfo.operatingTime: ${labelInfo.operatingTime}")
                Log.e("zx-出库-", "-------------labelInfo.epcLength: ${labelInfo.epcLength}")
                Log.e("zx-出库-", "-------------labelInfo.epc: ${labelInfo.epc}")
                Log.e("zx-出库-", "-------------labelInfo.tid: ${labelInfo.tid}")
                Log.e(
                    "zx-出库-",
                    "-------------labelInfo.inventoryNumber: ${labelInfo.inventoryNumber}"
                )
            }
            CANCEL_INVENTORY -> {
                // Toast.makeText(this, "停止盘点", Toast.LENGTH_SHORT).show()
            }
            END_INVENTORY -> {
                // Toast.makeText(this, "盘点结束", Toast.LENGTH_SHORT).show()

                // 出库,一般操作是先拿出来,然后会触发红外,触发读写器进行读写,此时应该判断哪个RFID不在了,不在才进行勾选,只要发现不在就勾选,在发现在的话不处理,因为可能已经拿了一个盒子里其中一本又放回去了
                for (dossier in outBoundList) {
                    if (dossier.floor == mFloor) { // 是否是当前触发的层
                        var isExit = false
                        for (labelInfo in labelInfoList) {
                            if (labelInfo.epc == dossier.rfidNum) {
                                // 扫描到要取的档案还在柜中
                                isExit = true
                                // dossier.nameValuePairs.isSelected = false
                                // todo 有多本档案要取得时候会有问题,先不管本档案,一本档案的操作ok就行
                                // speek("您要取的人事档案,${dossier.inputName},在${dossier.floor}层${dossier.light}号")
                                speek("您取错档案了,请取亮灯位置的档案")
                                break
                            }
                        }
                        if (!isExit) { // 没扫描到,说明远离天线了,可以判定为拿出来了
                            dossier.selected = true
                            speek("您已取档,请提交并关闭柜门")
                        }
                    }
                }

                var hasSelect = false
                for (dossier in outBoundList) {
                    if (dossier.selected) {
                        hasSelect = true
                        break
                    } else {
                        hasSelect = false
                    }
                }

                if (hasSelect) {
                    mOutboundBinding.btnOutStorage.background = resources.getDrawable(R.drawable.selector_menu_green)
                    mOutboundBinding.btnOutStorage.isEnabled = true
                } else {
                    mOutboundBinding.btnOutStorage.background = resources.getDrawable(R.drawable.shape_btn_un_enable)
                    mOutboundBinding.btnOutStorage.isEnabled = false
                }

                Log.e("zx-出库操作-盘点到的标签数据-", JSON.toJSONString(labelInfoList))
                mDossierAdapter.notifyDataSetChanged()
                labelInfoList.clear()
            }

            SUBMITTED_SUCCESS -> {
                // Toast.makeText(this, "出库成功", Toast.LENGTH_SHORT).show()
                speek("取档完成,请确认柜门已关闭")

                val message = Message.obtain()
                message.what = FINISH
                mHandler.sendMessageDelayed(message, 3000)

//                val mIterator = outBoundList.iterator()
//                while (mIterator.hasNext()) {
//                    val next = mIterator.next()
//                    if (next.selected) {
//                        mIterator.remove()
//                    }
//                }
//
//                if (outBoundList.size > 0) {
//                    mDossierAdapter.notifyDataSetChanged()
//                } else {
//                    finish()
//                }
            }
            SUBMITTED_FAIL -> {
                // Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_SHORT).show()
                mProgressDialog.dismiss()
            }

            FINISH -> {
                mProgressDialog.dismiss()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOutboundBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_outbound_operating)
        mOutboundBinding.onClickListener = this
        mOutboundBinding.onItemClickListener = this

        mHandler = OutboundOperatingHandler(this)

        val name = mSpUtil.getString(SharedPreferencesUtil.Key.NameTemp, "xxx")
        mOutboundBinding.tvOperator.text = name

        val deviceList = DeviceService.getInstance().loadAll()
        if (deviceList.size > 0) {
            mDevice = deviceList[0]
        }

        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)

        mProgressDialog.setMessage("正在获取取档列表...")
        mProgressDialog.show()

        val selectDossierList =
            DossierOperatingService.getInstance().queryBySelected() as ArrayList<DossierOperating>

        if (selectDossierList != null && selectDossierList.size > 0) { // 从预览界面过来
            // 把状态改过来先
            for (select in selectDossierList) {
                select.selected = false
            }
            mDossierAdapter = OutboundAdapter(this, selectDossierList)
            mOutboundBinding.outboundOperatingLv.adapter = mDossierAdapter

            val msg = Message.obtain()
            msg.what = GET_OUTBOUND_SUCCESS
            msg.obj = selectDossierList
            mHandler.sendMessageDelayed(msg, 800)
        } else { // 手动进入
            // todo 随机选1份档案 生成取档列表,并显示出来,此时状态为1 在库状态 (只在2层里面随机)
            val dossierList = DossierOperatingService.getInstance().loadAll()
            // 只能从入库状态档案里面生成取档列表,比如已经出库了,就不能在随机生成了
            val canOutDossierList = ArrayList<DossierOperating>()
            for (dossier in dossierList) {
                if (dossier.operatingType == 1 && dossier.floor == 2) {
                    canOutDossierList.add(dossier)
                }
            }
            // (数据类型)(最小值+Math.random()*(最大值-最小值+1))
            val random1 = (1 + Math.random() * (canOutDossierList.size)).toInt()
            val randomDossierList = ArrayList<DossierOperating>()
            randomDossierList.add(canOutDossierList[random1 - 1])

            mDossierAdapter = OutboundAdapter(this, randomDossierList)
            mOutboundBinding.outboundOperatingLv.adapter = mDossierAdapter

            val msg = Message.obtain()
            msg.what = GET_OUTBOUND_SUCCESS
            msg.obj = randomDossierList
            mHandler.sendMessageDelayed(msg, 800)
        }

        UR880Entrance.getInstance().addOnCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().addOnInventoryListener(mInventoryListener)
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
            val message = Message.obtain()
            message.obj = "开门成功"
            message.what = OPEN_DOOR_RESULT
            mHandler.sendMessage(message)
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

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mOutboundBinding.outboundOperatingCountdownTv.text = millisUntilFinished.toString()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // todo
            R.id.btn_open_door -> {
                mProgressDialog.setMessage("正在开柜，请稍后...")
                if (!mProgressDialog.isShowing) mProgressDialog.show()

                UR880Entrance.getInstance().send(
                    UR880SendInfo.Builder().openDoor(mDevice!!.deviceId, 0).build()
                )

            }

            R.id.btn_out_storage -> {
                outboundSubmission()
            }

            R.id.btn_back -> {
                finish()
            }
        }
    }

    private class OutboundOperatingHandler(outboundOperatingActivity: OutboundOperatingActivity) :
        Handler() {
        private val outboundOperatingWeakReference = WeakReference(outboundOperatingActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            outboundOperatingWeakReference.get()!!.handleMessage(msg)
        }
    }

    private fun outboundSubmission() {
        mProgressDialog.setMessage("正在取档，请稍后...")
        if (!mProgressDialog.isShowing) mProgressDialog.show()

        for (dossierChanged in outBoundList) {
            if (dossierChanged.selected) {
                // 档案选择状态改掉
                dossierChanged.selected = false
                // todo 更改成出库状态2
                dossierChanged.operatingType = 2
//                // recordList\.add\(Record\(Key\.LoginCodeTemp\, user\.userCode\)\) \/\/ zx
//                val userCode = mSpUtil.getString(SharedPreferencesUtil.Key.LoginCodeTemp, "")
//                dossierChanged.warrantNum = userCode
                // todo 保存被借阅的档案记录
                DossierOperatingService.getInstance().update(dossierChanged)
            }
        }

        val msg = Message.obtain()
        msg.what = SUBMITTED_SUCCESS
        mHandler.sendMessageDelayed(msg, 800)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        outBoundList[position].selected = !outBoundList[position].selected
//
//        if (outBoundList[position].selected) {
//            mOutboundBinding.btnOutStorage.background =
//                resources.getDrawable(R.drawable.selector_menu_green)
//            mOutboundBinding.btnOutStorage.isEnabled = true
//        } else {
//            var hasSelect = false
//            for (dossierOperating in outBoundList) {
//                if (dossierOperating.selected) {
//                    hasSelect = true
//                    break
//                } else {
//                    hasSelect = false
//                }
//            }
//
//            if (hasSelect) {
//                mOutboundBinding.btnOutStorage.background =
//                    resources.getDrawable(R.drawable.selector_menu_green)
//                mOutboundBinding.btnOutStorage.isEnabled = true
//            } else {
//                mOutboundBinding.btnOutStorage.background =
//                    resources.getDrawable(R.drawable.shape_btn_un_enable)
//                mOutboundBinding.btnOutStorage.isEnabled = false
//            }
//        }
//
//        mDossierAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        UR880Entrance.getInstance().removeCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().removeInventoryListener(mInventoryListener)

        super.onDestroy()
    }
}