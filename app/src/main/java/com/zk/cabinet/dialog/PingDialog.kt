package com.zk.cabinet.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.TextUtils
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.common.utils.LogUtil
import com.zk.common.utils.PingUtil
import com.zk.common.utils.RegularExpressionUtil

class PingDialog(pingResultListener: PingResultListener) :
    DialogFragment(), DialogInterface.OnClickListener {
    private val mPingResultListener = pingResultListener
    private lateinit var mBuilder: AlertDialog.Builder

    var mPingIpAddress: String? = null

    private lateinit var mDialogPingIpAddressEdt: EditText
    private lateinit var mDialogPingCountEdt: EditText
    private lateinit var mDialogPingWaitTimeEdt: EditText


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_ping, null)
        mDialogPingIpAddressEdt = view.findViewById(R.id.dialog_ping_ip_address_edt)
        mDialogPingCountEdt = view.findViewById(R.id.dialog_ping_count_edt)
        mDialogPingWaitTimeEdt = view.findViewById(R.id.dialog_ping_wait_time_edt)
        if (mPingIpAddress == null) mPingIpAddress = "www.baidu.com"
        mDialogPingIpAddressEdt.setText(mPingIpAddress)
        mDialogPingIpAddressEdt.setSelection(mPingIpAddress!!.length)

        mBuilder.setView(view)
            .setTitle(R.string.title_network_test)
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.sure), this)

        (activity as TimeOffAppCompatActivity).timerCancel()
        return mBuilder.create()
    }

    override fun onDestroy() {
        (activity as TimeOffAppCompatActivity).timerStart()
        super.onDestroy()
    }

    interface PingResultListener {
        fun onPingStart(address: String)

        fun onPingResult(result: String)
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                val pingIpAddress = mDialogPingIpAddressEdt.text.toString().trim()
                val pingCountStr = mDialogPingCountEdt.text.toString().trim()
                val pingWaitTimeStr = mDialogPingWaitTimeEdt.text.toString().trim()

                if (TextUtils.isEmpty(pingIpAddress) || TextUtils.isEmpty(pingCountStr) ||
                    TextUtils.isEmpty(pingWaitTimeStr)
                ) {
                    Toast.makeText(activity, R.string.fill_complete, Toast.LENGTH_SHORT).show()
                    return
                }
                val pingCount = pingCountStr.toInt()
                val pingWaitTime = pingWaitTimeStr.toInt()
                mPingResultListener.onPingStart(pingIpAddress)
                Thread(Runnable {
                    var pingEntity = PingUtil.PingEntity(
                        activity!!,
                        pingIpAddress, pingCount, pingWaitTime
                    )
                    pingEntity = PingUtil().ping(pingEntity)
                    val result =
                        "pingTime:${pingEntity.pingTime}\nresult:${pingEntity.result}\n${pingEntity.resultBuffer}"
                    mPingResultListener.onPingResult(result)
                    LogUtil.instance.d("ping result：", result)
                }).start()
            }
        }
    }

}