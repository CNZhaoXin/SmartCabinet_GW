package com.zk.cabinet.activity

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.databinding.DataBindingUtil
import com.alibaba.fastjson.JSON
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.DossierOperating
import com.zk.cabinet.databinding.ActivityWarning2Binding
import com.zk.cabinet.db.DeviceService
import com.zk.rfid.bean.UR880SendInfo
import com.zk.rfid.ur880.UR880Entrance
import java.lang.ref.WeakReference

class WarningActivity : TimeOffAppCompatActivity(), View.OnClickListener {

    private lateinit var mGuideBinding: ActivityWarning2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGuideBinding = DataBindingUtil.setContentView(this, R.layout.activity_warning2)
        mGuideBinding.onClickListener = this
        mHandler = MyHandler(this)
        val warningDossierStr = intent.getStringExtra("warningDossier")
        val warningDossier = JSON.parseObject(warningDossierStr, DossierOperating::class.java)

//        Log.e("zx-报警", warningDossier.inputName)
//        val msg = Message.obtain()
//        msg.what = SPEEK
//        msg.obj = warningDossier
//        mHandler.sendMessage(msg)
    }

    private lateinit var mHandler: MyHandler

    private class MyHandler(mActivity: WarningActivity) :
        Handler() {
        private val outboundOperatingWeakReference = WeakReference(mActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            outboundOperatingWeakReference.get()!!.handleMessage(msg)
        }
    }

    companion object {
        private const val SPEEK = 0x01
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            SPEEK -> {
                val warningDossier = msg.obj as DossierOperating
                speek("人事档案 ${warningDossier.inputName} 异常出库,请处理")
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_warning -> { // 解除报警
                val deviceList = DeviceService.getInstance().loadAll()
                if (deviceList.size > 1) {
                    /**
                     * @param ID               读写器ID
                     * @param portNumber       引脚序号 0x00：NO1； 0x01：NO2
                     * @param electricityLevel 电平 0x00：低电平； 0x01：高电平
                     */
                    UR880Entrance.getInstance().send(
                        UR880SendInfo.Builder()
                            .setGPOOutputStatus(deviceList[1].deviceId, 0x01, 0x00)
                            .build()
                    )
                    UR880Entrance.getInstance().send(
                        UR880SendInfo.Builder()
                            .setGPOOutputStatus(deviceList[1].deviceId, 0x02, 0x00)
                            .build()
                    )
                }
                finish()
            }
        }
    }

}