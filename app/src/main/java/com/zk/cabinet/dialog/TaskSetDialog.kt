package com.zk.cabinet.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.TextUtils
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.common.utils.RegularExpressionUtil

class TaskSetDialog(title: Int, inputListener: InputListener) :
    DialogFragment(), DialogInterface.OnClickListener {
    var mSwitch: Boolean? = null
    var mHourOfDay: Int? = null
    private val mInputListener = inputListener
    private lateinit var mBuilder: AlertDialog.Builder

    private val mTitle = title
    private lateinit var mDialogTaskSetSwitch: Switch
    private lateinit var mDialogTaskSetStartTimeSp: Spinner


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_task_settings, null)
        mDialogTaskSetSwitch = view.findViewById(R.id.dialog_task_set_switch)
        mDialogTaskSetStartTimeSp = view.findViewById(R.id.dialog_task_set_start_time_sp)
        mDialogTaskSetSwitch.isChecked = mSwitch!!
        mDialogTaskSetStartTimeSp.setSelection(mHourOfDay!!)
        mDialogTaskSetSwitch.setOnCheckedChangeListener { _, isChecked ->
            mDialogTaskSetSwitch.setText(
                if (isChecked) R.string.turn_on_automatic_task else R.string.turn_off_automatic_task
            )
        }

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
            switch: Boolean,
            hourOfDay: Int
        )
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                mInputListener.onInputComplete(
                    mDialogTaskSetSwitch.isChecked,
                    mDialogTaskSetStartTimeSp.selectedItemPosition
                )
            }
        }
    }

}