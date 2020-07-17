package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.databinding.ActivityCabinetConfigurationBinding
import com.zk.cabinet.db.CabinetService
import com.zk.cabinet.db.DeviceService
import kotlin.properties.Delegates

class CabinetConfigurationActivity : TimeOffAppCompatActivity() {
    private lateinit var mActivityCabinetConfigurationBinding: ActivityCabinetConfigurationBinding

    private var mNumberBoxesItemSelected by Delegates.notNull<Int>()
    private lateinit var mDeviceId: String

    private val mCabinetList = ArrayList<Device>()

    companion object {
        private const val NUMBER_OF_BOXES_SELECTED = "NumberOfBoxesSelected"
        private const val DEVICE_ID = "DeviceId"

        fun newInstance(
            packageContext: Context,
            deviceId: String,
            numberOfBoxesSelected: Int
        ): Intent {
            val intent = Intent(packageContext, CabinetConfigurationActivity::class.java)
            intent.putExtra(DEVICE_ID, deviceId)
            intent.putExtra(NUMBER_OF_BOXES_SELECTED, numberOfBoxesSelected)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityCabinetConfigurationBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_cabinet_configuration)
        setSupportActionBar(mActivityCabinetConfigurationBinding.cabinetConfigToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mNumberBoxesItemSelected = intent.getIntExtra(NUMBER_OF_BOXES_SELECTED, 1)
        mDeviceId = intent.getStringExtra(DEVICE_ID)!!

        mCabinetList.add(Device(null, null, "${mDeviceId}01"))
        mActivityCabinetConfigurationBinding.cabinetConfig01Tv.text = "${mDeviceId}01"

        if (mNumberBoxesItemSelected >= 2) {
            mCabinetList.add(Device(null, null, "${mDeviceId}02"))
            mActivityCabinetConfigurationBinding.cabinetConfig02Tv.text = "${mDeviceId}02"
            mActivityCabinetConfigurationBinding.cabinetConfig02Ll.visibility = View.VISIBLE
        }
        if (mNumberBoxesItemSelected >= 3) {
            mCabinetList.add(Device(null, null, "${mDeviceId}03"))
            mActivityCabinetConfigurationBinding.cabinetConfig03Tv.text = "${mDeviceId}03"
            mActivityCabinetConfigurationBinding.cabinetConfig03Ll.visibility = View.VISIBLE
        }
        if (mNumberBoxesItemSelected >= 4) {
            mCabinetList.add(Device(null, null, "${mDeviceId}04"))
            mActivityCabinetConfigurationBinding.cabinetConfig04Tv.text = "${mDeviceId}04"
            mActivityCabinetConfigurationBinding.cabinetConfig04Ll.visibility = View.VISIBLE
        }
        if (mNumberBoxesItemSelected >= 5) {
            mCabinetList.add(Device(null, null, "${mDeviceId}05"))
            mActivityCabinetConfigurationBinding.cabinetConfig05Tv.text = "${mDeviceId}05"
            mActivityCabinetConfigurationBinding.cabinetConfig05Ll.visibility = View.VISIBLE
        }
        if (mNumberBoxesItemSelected >= 6) {
            mCabinetList.add(Device(null, null, "${mDeviceId}06"))
            mActivityCabinetConfigurationBinding.cabinetConfig06Tv.text = "${mDeviceId}06"
            mActivityCabinetConfigurationBinding.cabinetConfig06Ll.visibility = View.VISIBLE
        }
        if (mNumberBoxesItemSelected >= 7) {
            mCabinetList.add(Device(null, null, "${mDeviceId}07"))
            mActivityCabinetConfigurationBinding.cabinetConfig07Tv.text = "${mDeviceId}07"
            mActivityCabinetConfigurationBinding.cabinetConfig07Ll.visibility = View.VISIBLE
        }

        DeviceService.getInstance().deleteAll()

    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mActivityCabinetConfigurationBinding.cabinetConfigCountdownTv.text =
            millisUntilFinished.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                var isFinish = true
                val device01 =
                    mActivityCabinetConfigurationBinding.cabinetConfig01Edt.text.toString().trim()
                if (device01.isNotEmpty()) mCabinetList[0].deviceId = device01
                else isFinish = false
                if (mNumberBoxesItemSelected >= 2) {
                    val device02 =
                        mActivityCabinetConfigurationBinding.cabinetConfig02Edt.text.toString()
                            .trim()
                    if (device02.isNotEmpty()) mCabinetList[1].deviceId = device02
                    else isFinish = false
                }
                if (mNumberBoxesItemSelected >= 3) {
                    val device03 =
                        mActivityCabinetConfigurationBinding.cabinetConfig03Edt.text.toString()
                            .trim()
                    if (device03.isNotEmpty()) mCabinetList[2].deviceId = device03
                    else isFinish = false
                }
                if (mNumberBoxesItemSelected >= 4) {
                    val device04 =
                        mActivityCabinetConfigurationBinding.cabinetConfig04Edt.text.toString()
                            .trim()
                    if (device04.isNotEmpty()) mCabinetList[3].deviceId = device04
                    else isFinish = false
                }
                if (mNumberBoxesItemSelected >= 5) {
                    val device05 =
                        mActivityCabinetConfigurationBinding.cabinetConfig05Edt.text.toString()
                            .trim()
                    if (device05.isNotEmpty()) mCabinetList[4].deviceId = device05
                    else isFinish = false
                }
                if (mNumberBoxesItemSelected >= 6) {
                    val device06 =
                        mActivityCabinetConfigurationBinding.cabinetConfig06Edt.text.toString()
                            .trim()
                    if (device06.isNotEmpty()) mCabinetList[5].deviceId = device06
                    else isFinish = false
                }
                if (mNumberBoxesItemSelected >= 7) {
                    val device07 =
                        mActivityCabinetConfigurationBinding.cabinetConfig07Edt.text.toString()
                            .trim()
                    if (device07.isNotEmpty()) mCabinetList[6].deviceId = device07
                    else isFinish = false
                }
                if (isFinish) {
                    DeviceService.getInstance().insertOrReplace(mCabinetList)
                    finish()
                } else {
                    Toast.makeText(this, "请填写完整", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}