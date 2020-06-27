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

class Eth0Dialog(title: Int, inputListener: InputListener) :
    DialogFragment(), DialogInterface.OnClickListener {
    var mEthIP: String? = null
    var mEthSubnetMask: String? = null
    var mEthGateway: String? = null
    var mEthDNS: String? = null
    private val mInputListener = inputListener
    private lateinit var mBuilder: AlertDialog.Builder

    private val mTitle = title
    private lateinit var mDialogEthIpEdt: EditText
    private lateinit var mDialogEthSubnetMaskEdt: EditText
    private lateinit var mDialogEthGatewayEdt: EditText
    private lateinit var mDialogEthDnsEdt: EditText


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_eth_0_set, null)
        mDialogEthIpEdt = view.findViewById<AutoCompleteTextView>(R.id.dialog_eth_ip_edt)
        mDialogEthSubnetMaskEdt =
            view.findViewById<AutoCompleteTextView>(R.id.dialog_eth_subnet_mask_edt)
        mDialogEthGatewayEdt = view.findViewById<AutoCompleteTextView>(R.id.dialog_eth_gateway_edt)
        mDialogEthDnsEdt = view.findViewById<AutoCompleteTextView>(R.id.dialog_eth_dns_edt)
        mDialogEthIpEdt.setText(mEthIP)
        if (!TextUtils.isEmpty(mEthIP)) mDialogEthIpEdt.setSelection(mEthIP!!.length)
        mDialogEthSubnetMaskEdt.setText(mEthSubnetMask)
        mDialogEthGatewayEdt.setText(mEthGateway)
        mDialogEthDnsEdt.setText(mEthDNS)

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
            ethIP: String,
            ethSubnetMask: String,
            ethGateway: String,
            ethDNS: String
        )
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                val ethIP = mDialogEthIpEdt.text.toString().trim()
                val ethSubnetMask = mDialogEthSubnetMaskEdt.text.toString().trim()
                val ethGateway = mDialogEthGatewayEdt.text.toString().trim()
                val ethDNS = mDialogEthDnsEdt.text.toString().trim()
                if (TextUtils.isEmpty(ethIP) || TextUtils.isEmpty(ethSubnetMask) || TextUtils.isEmpty(
                        ethGateway
                    ) || TextUtils.isEmpty(ethDNS)
                ) {
                    Toast.makeText(activity, R.string.fill_complete, Toast.LENGTH_SHORT).show()
                    return
                }
                if (!RegularExpressionUtil.isIp(ethIP) || !RegularExpressionUtil.isSubnetMask(
                        ethSubnetMask
                    ) || !RegularExpressionUtil.isIp(ethGateway) || !RegularExpressionUtil.isIp(
                        ethDNS
                    )
                ) {
                    Toast.makeText(activity, R.string.misconfiguration, Toast.LENGTH_SHORT).show()
                    return
                }
                mInputListener.onInputComplete(ethIP, ethSubnetMask, ethGateway, ethDNS)
            }
        }
    }

}