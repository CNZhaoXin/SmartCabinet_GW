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
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.zk.cabinet.R
import com.zk.cabinet.adapter.OutboundAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.bean.DossierOperating
import com.zk.cabinet.databinding.ActivityWarehousingOperatingBinding
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.db.DossierOperatingService
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.rfid.bean.LabelInfo
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.callback.CabinetInfoListener
import com.zk.rfid.callback.InventoryListener
import com.zk.rfid.ur880.UR880Entrance
import java.lang.ref.WeakReference

class WarehousingOperatingActivity : TimeOffAppCompatActivity(), AdapterView.OnItemClickListener,
    View.OnClickListener {
    private lateinit var mWarehousingBinding: ActivityWarehousingOperatingBinding
    private lateinit var mHandler: WarehousingOperatingHandler
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var mDevice: Device
    private lateinit var mOutboundAdapter: OutboundAdapter
    private var inStorageList = ArrayList<DossierOperating>()

    companion object {
        private const val OPEN_DOOR_RESULT = 0x01
        private const val START_INVENTORY = 0x02
        private const val INVENTORY_VALUE = 0x03
        private const val CANCEL_INVENTORY = 0x04
        private const val END_INVENTORY = 0x05
        private const val SUBMITTED_SUCCESS = 0x06
        private const val SUBMITTED_FAIL = 0x07
        private const val GET_INFRARED_AND_LOCK = 0x08
        private const val FINISH = 0x09

        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, WarehousingOperatingActivity::class.java)
        }
    }


    private fun handleMessage(msg: Message) {
        when (msg.what) {
            GET_INFRARED_AND_LOCK -> {
                val data = msg.data
                val boxStateList = data.getIntegerArrayList("lock")
                val infraredStateList = data.getIntegerArrayList("infrared")

                Log.e("zx-入库操作-boxStateList", "$boxStateList")
                // 这里只要红外被触发都会被调用, 门开的状态 boxStateList: [1] , 门关闭的状态 boxStateList: []
                if (boxStateList!!.isEmpty()) {
                    isAutoFinish = true
                    timerStart()
                    Log.e("zx-入库操作-", "门关闭-开启界面倒计时")
                    speek("柜门已关闭")
                }
            }
            OPEN_DOOR_RESULT -> {
                // todo 归档不亮灯,就是空位随便放,定位做好就行,与谁借的也没有关系
                speek("柜门已开启,请存入档案")

                // showToast(msg.obj.toString())
                Log.e("zx-入库操作-", "门开启-关闭界面倒计时-有位置参数的档案进行开灯-")
                // 门开启后倒计时关闭
                isAutoFinish = false
                timerCancel()
            }
            START_INVENTORY -> {
                Log.e("zx", "开始盘点")
                speek("正在盘点,请稍后")
                // Toast.makeText(this, "开始盘点", Toast.LENGTH_SHORT).show()
            }
            INVENTORY_VALUE -> {
                val labelInfo = msg.obj as LabelInfo
                Log.e("zx-入库-", "-------------labelInfo.deviceID: ${labelInfo.deviceID}")
                Log.e("zx-入库-", "-------------labelInfo.antennaNumber: ${labelInfo.antennaNumber}")
                Log.e("zx-入库-", "-------------labelInfo.fastID: ${labelInfo.fastID}")
                Log.e("zx-入库-", "-------------labelInfo.rssi: ${labelInfo.rssi}")
                Log.e("zx-入库-", "-------------labelInfo.operatingTime: ${labelInfo.operatingTime}")
                Log.e("zx-入库-", "-------------labelInfo.epcLength: ${labelInfo.epcLength}")
                Log.e("zx-入库-", "-------------labelInfo.epc: ${labelInfo.epc}")
                Log.e("zx-入库-", "-------------labelInfo.tid: ${labelInfo.tid}")
                Log.e(
                    "zx-入库-",
                    "-------------labelInfo.inventoryNumber: ${labelInfo.inventoryNumber}"
                )
                labelInfo.antennaNumber = labelInfo.antennaNumber + 1

                // todo 识别到的EPC,去库存查一下是不是库存里面的档案,状态是否是出库2的状态, 如果是的话添加到待归档列表中,不需要有勾选的按钮
                val dossierStock = DossierOperatingService.getInstance().queryByEPC(labelInfo.epc)
                if (dossierStock != null && dossierStock.operatingType == 2) {
                    var isExit = false
                    for (dossier in inStorageList) {
                        if (dossier.rfidNum == dossierStock.rfidNum) {
                            val light =
                                if (labelInfo.antennaNumber % 24 == 0) 24 else labelInfo.antennaNumber % 24
                            val floor =
                                if (labelInfo.antennaNumber % 24 == 0) labelInfo.antennaNumber / 24 else (labelInfo.antennaNumber / 24 + 1)
                            dossier.floor = floor
                            dossier.light = light
                            dossierStock.selected = true
                            isExit = true
                            speek("人事档案,${dossier.inputName},已放入${dossier.floor}层${dossier.light}号,请及时提交并关闭柜门")
                            break
                        }
                    }
                    if (!isExit) {
                        val light =
                            if (labelInfo.antennaNumber % 24 == 0) 24 else labelInfo.antennaNumber % 24
                        val floor =
                            if (labelInfo.antennaNumber % 24 == 0) labelInfo.antennaNumber / 24 else (labelInfo.antennaNumber / 24 + 1)
                        dossierStock.floor = floor
                        dossierStock.light = light
                        dossierStock.selected = true
                        inStorageList.add(dossierStock)

                        speek("人事档案,${dossierStock.inputName},已放入${dossierStock.floor}层${dossierStock.light}号,请及时提交并关闭柜门")
                    }
                    mOutboundAdapter.notifyDataSetChanged()
                }

                if (inStorageList.size > 0) {
                    mWarehousingBinding.btnInStorage.isEnabled = true
                    mWarehousingBinding.btnInStorage.setBackgroundResource(R.drawable.selector_menu_green)
                } else {
                    mWarehousingBinding.btnInStorage.isEnabled = false
                    mWarehousingBinding.btnInStorage.setBackgroundResource(R.drawable.shape_btn_un_enable)
                }

            }
            CANCEL_INVENTORY -> {
                Log.e("zx", "停止盘点")
                // Toast.makeText(this, "停止盘点", Toast.LENGTH_SHORT).show()
            }
            END_INVENTORY -> {
                Log.e("zx", "盘点结束")
                //Toast.makeText(this, "盘点结束", Toast.LENGTH_SHORT).show()

                // 这个是每次盘点完成之后都重新亮灯,亮操作过后的入库列表数据的灯
                for (index in 1..5) {
                    val lights = ArrayList<Int>()
                    for (dossier in inStorageList) {
                        if (dossier.floor == index) {
                            lights.add(dossier.light)
                            // speek("人事档案,${dossier.inputName},已放入${dossier.floor}层${dossier.light}号,请确认灯位是否正确")
                        }
                    }
                    UR880Entrance.getInstance().send(
                        UR880SendInfo.Builder().turnOnLight(mDevice.deviceId, index, lights).build()
                    )
                }
            }
            SUBMITTED_SUCCESS -> {
                // Toast.makeText(this, "数据提交成功", Toast.LENGTH_SHORT).show()
                speek("存档完成,请确认柜门已关闭")

                val message = Message.obtain()
                message.what = FINISH
                mHandler.sendMessageDelayed(message, 3000)
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
        mWarehousingBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_warehousing_operating)
        mWarehousingBinding.onClickListener = this
        mWarehousingBinding.onItemClickListener = this

        val name = mSpUtil.getString(Key.NameTemp, "xxx")
        mWarehousingBinding.tvOperator.text = name

        mHandler = WarehousingOperatingHandler(this)

        mProgressDialog = ProgressDialog(this, R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)

        // todo 演示版本读写器设备只有设置界面配置的一个
        val deviceList = DeviceService.getInstance().loadAll()
        if (deviceList.size > 0) {
            mDevice = deviceList[0]
        }

        mOutboundAdapter = OutboundAdapter(this, inStorageList)
        mWarehousingBinding.warehousingOperatingLv.adapter = mOutboundAdapter

        UR880Entrance.getInstance().addOnCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().addOnInventoryListener(mInventoryListener)

        // todo 存档时进入直接先开一次柜门
        UR880Entrance.getInstance()
            .send(UR880SendInfo.Builder().openDoor(mDevice.deviceId, 0).build())
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
        mWarehousingBinding.warehousingOperatingCountdownTv.text = millisUntilFinished.toString()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> {
                finish()
            }
            R.id.btn_open_door -> {
                UR880Entrance.getInstance()
                    .send(UR880SendInfo.Builder().openDoor(mDevice.deviceId, 0).build())
            }
            R.id.btn_in_storage -> {
                warehousingSubmission()
            }
        }
    }

    private class WarehousingOperatingHandler(warehousingOperatingActivity: WarehousingOperatingActivity) :
        Handler() {
        private val warehousingOperatingWeakReference = WeakReference(warehousingOperatingActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            warehousingOperatingWeakReference.get()!!.handleMessage(msg)
        }
    }

    // todo 归档提交
    private fun warehousingSubmission() {
        if (inStorageList != null && inStorageList.size > 0) {
            mProgressDialog.setMessage("正在存档，请稍后...")
            if (!mProgressDialog.isShowing) mProgressDialog.show()

            // todo 修改档案状态为入库状态1
            for (dossier in inStorageList) {
                dossier.operatingType = 1
                dossier.selected = false
                DossierOperatingService.getInstance().update(dossier)
            }

            val msg = Message.obtain()
            msg.what = SUBMITTED_SUCCESS
            mHandler.sendMessageDelayed(msg, 800)
        } else {
            Toast.makeText(this, "没有要存档的档案", Toast.LENGTH_LONG).show()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        dossierList[position].isSelected = !dossierList[position].isSelected
//
//        if (dossierList[position].isSelected) {
//            mWarehousingBinding.btnInStorage.background =
//                resources.getDrawable(R.drawable.selector_menu_green)
//            mWarehousingBinding.btnInStorage.isEnabled = true
//        } else {
//            var hasSelect = false
//            for (dossierOperating in dossierList) {
//                if (dossierOperating.isSelected) {
//                    hasSelect = true
//                    break
//                } else {
//                    hasSelect = false
//                }
//            }
//
//            if (hasSelect) {
//                mWarehousingBinding.btnInStorage.background =
//                    resources.getDrawable(R.drawable.selector_menu_green)
//                mWarehousingBinding.btnInStorage.isEnabled = true
//            } else {
//                mWarehousingBinding.btnInStorage.background =
//                    resources.getDrawable(R.drawable.shape_btn_un_enable)
//                mWarehousingBinding.btnInStorage.isEnabled = false
//            }
//        }
//
//        mOutboundAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        UR880Entrance.getInstance().removeCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().removeInventoryListener(mInventoryListener)
        super.onDestroy()
    }

}