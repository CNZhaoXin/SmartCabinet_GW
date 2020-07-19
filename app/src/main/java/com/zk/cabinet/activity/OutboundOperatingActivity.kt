package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.zk.cabinet.R
import com.zk.cabinet.adapter.DossierAdapter
import com.zk.cabinet.adapter.OutboundAdapter
import com.zk.cabinet.adapter.WarehousingAdapter
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.bean.Dossier
import com.zk.cabinet.bean.DossierOperating
import com.zk.cabinet.databinding.ActivityOutboundBinding
import com.zk.cabinet.databinding.ActivityOutboundOperatingBinding
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.db.DossierOperatingService
import com.zk.common.utils.LogUtil
import com.zk.rfid.bean.LabelInfo
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.callback.CabinetInfoListener
import com.zk.rfid.callback.InventoryListener
import com.zk.rfid.ur880.UR880Entrance
import java.lang.ref.WeakReference

class OutboundOperatingActivity : TimeOffAppCompatActivity() {
    private lateinit var mOutboundBinding: ActivityOutboundOperatingBinding
    private lateinit var mHandler: OutboundOperatingHandler
    private lateinit var mDevice: Device
    private var dossierList = ArrayList<DossierOperating>()
    private lateinit var mDossierAdapter: OutboundAdapter
    private val labelInfoList = ArrayList<LabelInfo>()
    private var mFloor = -1
    private var mDoorIsOpen = false

    companion object {
        private const val OPEN_DOOR_RESULT = 0x01
        private const val START_INVENTORY = 0x02
        private const val INVENTORY_VALUE = 0x03
        private const val CANCEL_INVENTORY = 0x04
        private const val END_INVENTORY = 0x05
        private const val GET_INFRARED_AND_LOCK = 0x06

        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, OutboundOperatingActivity::class.java)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            OPEN_DOOR_RESULT -> {
                showToast(msg.obj.toString())
            }
            START_INVENTORY -> {
                Toast.makeText(this, "开始盘点", Toast.LENGTH_SHORT).show()
            }
            INVENTORY_VALUE -> {
                val labelInfo = msg.obj as LabelInfo
                labelInfoList.add(labelInfo)
                LogUtil.instance.d("-------------labelInfo.deviceID: ${labelInfo.deviceID}")
                LogUtil.instance.d("-------------labelInfo.antennaNumber: ${labelInfo.antennaNumber}")
                LogUtil.instance.d("-------------labelInfo.fastID: ${labelInfo.fastID}")
                LogUtil.instance.d("-------------labelInfo.rssi: ${labelInfo.rssi}")
                LogUtil.instance.d("-------------labelInfo.operatingTime: ${labelInfo.operatingTime}")
                LogUtil.instance.d("-------------labelInfo.epcLength: ${labelInfo.epcLength}")
                LogUtil.instance.d("-------------labelInfo.epc: ${labelInfo.epc}")
                LogUtil.instance.d("-------------labelInfo.tid: ${labelInfo.tid}")
                LogUtil.instance.d("-------------labelInfo.inventoryNumber: ${labelInfo.inventoryNumber}")
            }
            CANCEL_INVENTORY -> {
                Toast.makeText(this, "停止盘点", Toast.LENGTH_SHORT).show()
            }
            END_INVENTORY -> {
                Toast.makeText(this, "盘点结束", Toast.LENGTH_SHORT).show()

//                val floor = if (labelInfoList[0].antennaNumber % 24 == 0) labelInfoList[0].antennaNumber / 24 else (labelInfoList[0].antennaNumber / 24 + 1)
                if (dossierList.isNotEmpty()) {
                    for (dossier in dossierList) {
                        if (dossier.floor == mFloor) {
                            var isExit = false
                            for (labelInfo in labelInfoList) {
                                if (labelInfo.epc == dossier.rfidNum) {
                                    isExit = true
                                    dossier.selected = true
                                    break
                                }
                            }
                            if (!isExit) dossier.selected = false
                        }
                    }
                } else {
                    for (dossier in dossierList) {
                        if (dossier.floor == mFloor) {
                            dossier.selected = false
                        }
                    }
                }
                mDossierAdapter.notifyDataSetChanged()
                labelInfoList.clear()
            }
            GET_INFRARED_AND_LOCK -> {
                val data = msg.data
                val boxStateList = data.getIntegerArrayList("lock")
                val infraredStateList = data.getIntegerArrayList("infrared")
                if (boxStateList!!.isEmpty()){
                    if (mDoorIsOpen) {
                        mDoorIsOpen = false
                        showToast("门关闭")
                    }
                }
                else {
                    if (!mDoorIsOpen) {
                        mDoorIsOpen = true
                        showToast("门开启")
                    }
                }
                if (infraredStateList!!.isNotEmpty()) {
                    mFloor = infraredStateList[0]
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOutboundBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_outbound_operating)
        setSupportActionBar(mOutboundBinding.outboundOperatingToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mHandler = OutboundOperatingHandler(this)

        UR880Entrance.getInstance().addOnCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().addOnInventoryListener(mInventoryListener)

        dossierList = DossierOperatingService.getInstance().queryBySelected() as ArrayList<DossierOperating>
        mDevice = DeviceService.getInstance().queryByDeviceName(dossierList[0].cabinetId)
        mOutboundBinding.outboundBoxNumberTv.text = "柜体名称：${mDevice.deviceName}(${mDevice.deviceId})"

        mDossierAdapter = OutboundAdapter(this, dossierList)
        mOutboundBinding.outboundOperatingLv.adapter = mDossierAdapter

        UR880Entrance.getInstance()
            .send(UR880SendInfo.Builder().openDoor(mDevice.deviceId, 0).build())
        for (index in 1..5){
            val lights = ArrayList<Int>()
            for (dossierOperating in dossierList){
                if(dossierOperating.floor == index){
                    lights.add(dossierOperating.light)
                }
            }
            UR880Entrance.getInstance()
                .send(UR880SendInfo.Builder().turnOnLight(mDevice.deviceId, index, lights).build())
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                for (index in 1..5){
                    val lights = ArrayList<Int>()
                    UR880Entrance.getInstance()
                        .send(UR880SendInfo.Builder().turnOnLight(mDevice.deviceId, index, lights).build())
                }
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private class OutboundOperatingHandler(outboundOperatingActivity: OutboundOperatingActivity) :
        Handler() {
        private val outboundOperatingWeakReference = WeakReference(outboundOperatingActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            outboundOperatingWeakReference.get()!!.handleMessage(msg)
        }
    }

    override fun onDestroy() {
        UR880Entrance.getInstance().removeCabinetInfoListener(mCabinetInfoListener)
        UR880Entrance.getInstance().removeInventoryListener(mInventoryListener)

        super.onDestroy()
    }
}