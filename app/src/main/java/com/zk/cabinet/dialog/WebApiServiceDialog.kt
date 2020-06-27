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
import com.zk.common.utils.RegularExpressionUtil

class WebApiServiceDialog(title: Int, inputListener: InputListener) :
    DialogFragment(), DialogInterface.OnClickListener {
    var mWebApiServiceIp: String? = null
    var mWebApiServicePort: Int? = null
    private val mInputListener = inputListener
    private lateinit var mBuilder: AlertDialog.Builder

    private val mTitle = title
    private lateinit var mDialogWebApiIpEdt: EditText
    private lateinit var mDialogWebApiPortEdt: EditText


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_web_api_set, null)
        mDialogWebApiIpEdt = view.findViewById<AutoCompleteTextView>(R.id.dialog_web_api_ip_edt)
        mDialogWebApiPortEdt = view.findViewById<AutoCompleteTextView>(R.id.dialog_web_api_port_edt)
        mDialogWebApiIpEdt.setText(mWebApiServiceIp)
        if (!TextUtils.isEmpty(mWebApiServiceIp)) mDialogWebApiIpEdt.setSelection(mWebApiServiceIp!!.length)
        mDialogWebApiPortEdt.setText(if (mWebApiServicePort != -1) mWebApiServicePort.toString() else null)

        mBuilder.setView(view)
            .setTitle(mTitle)
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.sure), this)

        (activity as TimeOffAppCompatActivity).timerCancel()
        return mBuilder.create()
    }

    override fun onDestroy() {
        (activity as TimeOffAppCompatActivity).timerStart()
        super.onDestroy()
    }

    interface InputListener {
        fun onInputComplete(
            webApiServiceIp: String,
            webApiServicePort: Int
        )
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                val webApiServiceIp = mDialogWebApiIpEdt.text.toString().trim()
                val webApiServicePortStr = mDialogWebApiPortEdt.text.toString().trim()
                if (TextUtils.isEmpty(webApiServiceIp) || TextUtils.isEmpty(webApiServicePortStr)
                ) {
                    Toast.makeText(activity, R.string.fill_complete, Toast.LENGTH_SHORT).show()
                    return
                }
                if (!RegularExpressionUtil.isIp(webApiServiceIp)) {
                    Toast.makeText(activity, R.string.misconfiguration, Toast.LENGTH_SHORT).show()
                    return
                }
                mInputListener.onInputComplete(webApiServiceIp, webApiServicePortStr.toInt())
            }
        }
    }

}