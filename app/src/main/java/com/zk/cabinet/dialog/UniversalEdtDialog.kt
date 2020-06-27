package com.zk.cabinet.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.TextUtils
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity

class UniversalEdtDialog(title: Int, inputListener: InputListener) :
    DialogFragment(), DialogInterface.OnClickListener {
    var mMessage: String? = null
    var mInputType: Int? = null
    private val mInputListener = inputListener
    private val mTitle = title
    private lateinit var mBuilder: AlertDialog.Builder
    private lateinit var mDialogUniversalTv: AutoCompleteTextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_universal_edt, null)
        mDialogUniversalTv = view.findViewById<AutoCompleteTextView>(R.id.dialog_universal_tv)
        mDialogUniversalTv.inputType = mInputType!!
        mBuilder.setView(view)
            .setTitle(mTitle)
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.sure), this)
        if (mMessage != null && mMessage != getString(R.string.null_prompt)) {
            mDialogUniversalTv.setText(mMessage)
            mDialogUniversalTv.setSelection(mMessage!!.length)
        }
        (activity as TimeOffAppCompatActivity).timerCancel()
        return mBuilder.create()
    }

    override fun onDestroy() {
        (activity as TimeOffAppCompatActivity).timerStart()
        super.onDestroy()
    }

    interface InputListener {
        fun onInputComplete(
            input: String
        )
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                val input = mDialogUniversalTv.text.toString().trim()
                if (!TextUtils.isEmpty(input)) {
                    mInputListener.onInputComplete(input)
                } else {
                    Toast.makeText(activity, R.string.fill_complete, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}