package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.databinding.ActivityDajSettingBinding
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.utils.SharedPreferencesUtil
import kotlin.properties.Delegates

/**
 * 添加档案架
 */
class DAJSettingActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityDajSettingBinding

    private var mDAJNumberSelected by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_daj_setting)

        setSupportActionBar(mBinding.cabinetConfigToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mBinding.onClickListener = this

        // 拿到被选择的柜体数(0-9 10个档案架)
        mDAJNumberSelected = intent.getIntExtra("DAJNumberSelected", 0)
        // 操作屏设备ID
        val mEquipmentId = mSpUtil.getString(SharedPreferencesUtil.Key.EquipmentId, "")
        // 显示选择的可配置的档案架
        if (mDAJNumberSelected >= 0) {
            mBinding.cabinetConfig01Ll.visibility = View.VISIBLE
            mBinding.etCzpDeviceID01.setText(mEquipmentId)
            mBinding.etLightControlBoardID01.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    setBtnStatus()
                    if (TextUtils.isEmpty(s)) {
                        mBinding.tvDajDeviceID01.setText(s)
                    } else {
                        mBinding.tvDajDeviceID01.setText("$mEquipmentId-$s")
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })

            if (mDAJNumberSelected >= 1) {
                mBinding.cabinetConfig02Ll.visibility = View.VISIBLE
                mBinding.etCzpDeviceID02.setText(mEquipmentId)
                mBinding.etLightControlBoardID02.addTextChangedListener(
                    object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            setBtnStatus()
                            if (TextUtils.isEmpty(s)) {
                                mBinding.tvDajDeviceID02.setText(s)
                            } else {
                                mBinding.tvDajDeviceID02.setText("$mEquipmentId-$s")
                            }
                        }

                        override fun afterTextChanged(s: Editable) {}
                    })

                if (mDAJNumberSelected >= 2) {
                    mBinding.cabinetConfig03Ll.visibility = View.VISIBLE
                    mBinding.etCzpDeviceID03.setText(mEquipmentId)
                    mBinding.etLightControlBoardID03.addTextChangedListener(
                        object : TextWatcher {
                            override fun beforeTextChanged(
                                s: CharSequence,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {
                            }

                            override fun onTextChanged(
                                s: CharSequence,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                                setBtnStatus()
                                if (TextUtils.isEmpty(s)) {
                                    mBinding.tvDajDeviceID03.setText(s)
                                } else {
                                    mBinding.tvDajDeviceID03.setText("$mEquipmentId-$s")
                                }
                            }

                            override fun afterTextChanged(s: Editable) {}
                        })

                    if (mDAJNumberSelected >= 3) {
                        mBinding.cabinetConfig04Ll.visibility = View.VISIBLE
                        mBinding.etCzpDeviceID04.setText(mEquipmentId)
                        mBinding.etLightControlBoardID04.addTextChangedListener(
                            object : TextWatcher {
                                override fun beforeTextChanged(
                                    s: CharSequence,
                                    start: Int,
                                    count: Int,
                                    after: Int
                                ) {
                                }

                                override fun onTextChanged(
                                    s: CharSequence,
                                    start: Int,
                                    before: Int,
                                    count: Int
                                ) {
                                    setBtnStatus()
                                    if (TextUtils.isEmpty(s)) {
                                        mBinding.tvDajDeviceID04.setText(s)
                                    } else {
                                        mBinding.tvDajDeviceID04.setText("$mEquipmentId-$s")
                                    }
                                }

                                override fun afterTextChanged(s: Editable) {}
                            })

                        if (mDAJNumberSelected >= 4) {
                            mBinding.cabinetConfig05Ll.visibility = View.VISIBLE
                            mBinding.etCzpDeviceID05.setText(mEquipmentId)
                            mBinding.etLightControlBoardID05.addTextChangedListener(
                                object : TextWatcher {
                                    override fun beforeTextChanged(
                                        s: CharSequence,
                                        start: Int,
                                        count: Int,
                                        after: Int
                                    ) {
                                    }

                                    override fun onTextChanged(
                                        s: CharSequence,
                                        start: Int,
                                        before: Int,
                                        count: Int
                                    ) {
                                        setBtnStatus()
                                        if (TextUtils.isEmpty(s)) {
                                            mBinding.tvDajDeviceID05.setText(s)
                                        } else {
                                            mBinding.tvDajDeviceID05.setText("$mEquipmentId-$s")
                                        }
                                    }

                                    override fun afterTextChanged(s: Editable) {}
                                })

                            if (mDAJNumberSelected >= 5) {
                                mBinding.cabinetConfig06Ll.visibility = View.VISIBLE
                                mBinding.etCzpDeviceID06.setText(mEquipmentId)
                                mBinding.etLightControlBoardID06.addTextChangedListener(
                                    object : TextWatcher {
                                        override fun beforeTextChanged(
                                            s: CharSequence,
                                            start: Int,
                                            count: Int,
                                            after: Int
                                        ) {
                                        }

                                        override fun onTextChanged(
                                            s: CharSequence,
                                            start: Int,
                                            before: Int,
                                            count: Int
                                        ) {
                                            setBtnStatus()
                                            if (TextUtils.isEmpty(s)) {
                                                mBinding.tvDajDeviceID06.setText(s)
                                            } else {
                                                mBinding.tvDajDeviceID06.setText("$mEquipmentId-$s")
                                            }
                                        }

                                        override fun afterTextChanged(s: Editable) {}
                                    })


                                if (mDAJNumberSelected >= 6) {
                                    mBinding.cabinetConfig07Ll.visibility = View.VISIBLE
                                    mBinding.etCzpDeviceID07.setText(mEquipmentId)
                                    mBinding.etLightControlBoardID07.addTextChangedListener(
                                        object : TextWatcher {
                                            override fun beforeTextChanged(
                                                s: CharSequence,
                                                start: Int,
                                                count: Int,
                                                after: Int
                                            ) {
                                            }

                                            override fun onTextChanged(
                                                s: CharSequence,
                                                start: Int,
                                                before: Int,
                                                count: Int
                                            ) {
                                                setBtnStatus()
                                                if (TextUtils.isEmpty(s)) {
                                                    mBinding.tvDajDeviceID07.setText(s)
                                                } else {
                                                    mBinding.tvDajDeviceID07.setText("$mEquipmentId-$s")
                                                }
                                            }

                                            override fun afterTextChanged(s: Editable) {}
                                        })


                                    if (mDAJNumberSelected >= 7) {
                                        mBinding.cabinetConfig08Ll.visibility = View.VISIBLE
                                        mBinding.etCzpDeviceID08.setText(mEquipmentId)
                                        mBinding.etLightControlBoardID08.addTextChangedListener(
                                            object : TextWatcher {
                                                override fun beforeTextChanged(
                                                    s: CharSequence,
                                                    start: Int,
                                                    count: Int,
                                                    after: Int
                                                ) {
                                                }

                                                override fun onTextChanged(
                                                    s: CharSequence,
                                                    start: Int,
                                                    before: Int,
                                                    count: Int
                                                ) {
                                                    setBtnStatus()
                                                    if (TextUtils.isEmpty(s)) {
                                                        mBinding.tvDajDeviceID08.setText(s)
                                                    } else {
                                                        mBinding.tvDajDeviceID08.setText("$mEquipmentId-$s")
                                                    }
                                                }

                                                override fun afterTextChanged(s: Editable) {}
                                            })

                                        if (mDAJNumberSelected >= 8) {
                                            mBinding.cabinetConfig09Ll.visibility = View.VISIBLE
                                            mBinding.etCzpDeviceID09.setText(mEquipmentId)
                                            mBinding.etLightControlBoardID09.addTextChangedListener(
                                                object : TextWatcher {
                                                    override fun beforeTextChanged(
                                                        s: CharSequence,
                                                        start: Int,
                                                        count: Int,
                                                        after: Int
                                                    ) {
                                                    }

                                                    override fun onTextChanged(
                                                        s: CharSequence,
                                                        start: Int,
                                                        before: Int,
                                                        count: Int
                                                    ) {
                                                        setBtnStatus()
                                                        if (TextUtils.isEmpty(s)) {
                                                            mBinding.tvDajDeviceID09.setText(s)
                                                        } else {
                                                            mBinding.tvDajDeviceID09.setText("$mEquipmentId-$s")
                                                        }
                                                    }

                                                    override fun afterTextChanged(s: Editable) {}
                                                })

                                        }

                                        if (mDAJNumberSelected >= 9) {
                                            mBinding.cabinetConfig10Ll.visibility = View.VISIBLE
                                            mBinding.etCzpDeviceID10.setText(mEquipmentId)
                                            mBinding.etLightControlBoardID10.addTextChangedListener(
                                                object : TextWatcher {
                                                    override fun beforeTextChanged(
                                                        s: CharSequence,
                                                        start: Int,
                                                        count: Int,
                                                        after: Int
                                                    ) {
                                                    }

                                                    override fun onTextChanged(
                                                        s: CharSequence,
                                                        start: Int,
                                                        before: Int,
                                                        count: Int
                                                    ) {
                                                        setBtnStatus()
                                                        if (TextUtils.isEmpty(s)) {
                                                            mBinding.tvDajDeviceID10.setText(s)
                                                        } else {
                                                            mBinding.tvDajDeviceID10.setText("$mEquipmentId-$s")
                                                        }
                                                    }

                                                    override fun afterTextChanged(s: Editable) {}
                                                })

                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 显示之前配置过的值,可能有可能没有
        val deviceList = DeviceService.getInstance().loadAll()
        if (deviceList != null && deviceList.size > 0 && mDAJNumberSelected >= 0) {
            mBinding.etDajName01.setText(deviceList[0].deviceName)
            mBinding.etLightControlBoardID01.setText(deviceList[0].lightControlBoardId)
            mBinding.tvDajDeviceID01.setText(deviceList[0].deviceId)

            if (deviceList.size > 1 && mDAJNumberSelected >= 1) {
                mBinding.etDajName02.setText(deviceList[1].deviceName)
                mBinding.etLightControlBoardID02.setText(deviceList[1].lightControlBoardId)
                mBinding.tvDajDeviceID02.setText(deviceList[1].deviceId)

                if (deviceList.size > 2 && mDAJNumberSelected >= 2) {
                    mBinding.etDajName03.setText(deviceList[2].deviceName)
                    mBinding.etLightControlBoardID03.setText(deviceList[2].lightControlBoardId)
                    mBinding.tvDajDeviceID03.setText(deviceList[2].deviceId)

                    if (deviceList.size > 3 && mDAJNumberSelected >= 3) {
                        mBinding.etDajName04.setText(deviceList[3].deviceName)
                        mBinding.etLightControlBoardID04.setText(deviceList[3].lightControlBoardId)
                        mBinding.tvDajDeviceID04.setText(deviceList[3].deviceId)

                        if (deviceList.size > 4 && mDAJNumberSelected >= 4) {
                            mBinding.etDajName05.setText(deviceList[4].deviceName)
                            mBinding.etLightControlBoardID05.setText(deviceList[4].lightControlBoardId)
                            mBinding.tvDajDeviceID05.setText(
                                deviceList[4].deviceId
                            )

                            if (deviceList.size > 5 && mDAJNumberSelected >= 5) {
                                mBinding.etDajName06.setText(deviceList[5].deviceName)
                                mBinding.etLightControlBoardID06.setText(deviceList[5].lightControlBoardId)
                                mBinding.tvDajDeviceID06.setText(
                                    deviceList[5].deviceId
                                )

                                if (deviceList.size > 6 && mDAJNumberSelected >= 6) {
                                    mBinding.etDajName07.setText(deviceList[6].deviceName)
                                    mBinding.etLightControlBoardID07.setText(deviceList[6].lightControlBoardId)
                                    mBinding.tvDajDeviceID07.setText(
                                        deviceList[6].deviceId
                                    )

                                    if (deviceList.size > 7 && mDAJNumberSelected >= 7) {
                                        mBinding.etDajName08.setText(deviceList[7].deviceName)
                                        mBinding.etLightControlBoardID08.setText(deviceList[7].lightControlBoardId)
                                        mBinding.tvDajDeviceID08.setText(
                                            deviceList[7].deviceId
                                        )

                                        if (deviceList.size > 8 && mDAJNumberSelected >= 8) {
                                            mBinding.etDajName09.setText(deviceList[8].deviceName)
                                            mBinding.etLightControlBoardID09.setText(deviceList[8].lightControlBoardId)
                                            mBinding.tvDajDeviceID09.setText(
                                                deviceList[8].deviceId
                                            )

                                            if (deviceList.size > 9 && mDAJNumberSelected >= 9) {
                                                mBinding.etDajName10.setText(deviceList[9].deviceName)
                                                mBinding.etLightControlBoardID10.setText(deviceList[9].lightControlBoardId)
                                                mBinding.tvDajDeviceID10.setText(
                                                    deviceList[9].deviceId
                                                )

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private fun setBtnStatus() {
        if (mDAJNumberSelected == 0) {
            if (!TextUtils.isEmpty(mBinding.etLightControlBoardID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID01.text.toString().trim())
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mDAJNumberSelected == 1) {
            if (!TextUtils.isEmpty(mBinding.etLightControlBoardID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID02.text.toString().trim())
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mDAJNumberSelected == 2) {
            if (!TextUtils.isEmpty(mBinding.etLightControlBoardID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID03.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID03.text.toString().trim())
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mDAJNumberSelected == 3) {
            if (!TextUtils.isEmpty(mBinding.etLightControlBoardID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID03.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID03.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID04.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID04.text.toString().trim())
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mDAJNumberSelected == 4) {
            if (!TextUtils.isEmpty(mBinding.etLightControlBoardID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID03.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID03.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID04.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID04.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID05.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID05.text.toString().trim())
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mDAJNumberSelected == 5) {
            if (!TextUtils.isEmpty(mBinding.etLightControlBoardID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID03.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID03.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID04.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID04.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID05.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID05.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID06.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID06.text.toString().trim())
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mDAJNumberSelected == 6) {
            if (!TextUtils.isEmpty(mBinding.etLightControlBoardID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID03.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID03.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID04.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID04.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID05.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID05.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID06.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID06.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID07.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID07.text.toString().trim())
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mDAJNumberSelected == 7) {
            if (!TextUtils.isEmpty(mBinding.etLightControlBoardID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID03.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID03.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID04.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID04.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID05.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID05.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID06.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID06.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID07.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID07.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID08.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID08.text.toString().trim())
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mDAJNumberSelected == 8) {
            if (!TextUtils.isEmpty(mBinding.etLightControlBoardID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID03.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID03.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID04.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID04.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID05.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID05.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID06.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID06.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID07.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID07.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID08.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID08.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID09.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID09.text.toString().trim())
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mDAJNumberSelected == 9) {
            if (!TextUtils.isEmpty(mBinding.etLightControlBoardID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID01.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID02.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID03.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID03.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID04.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID04.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID05.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID05.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID06.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID06.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID07.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID07.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID08.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID08.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID09.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID09.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etLightControlBoardID10.text.toString().trim())
                && !TextUtils.isEmpty(mBinding.etCzpDeviceID10.text.toString().trim())
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        }

    }

    private fun setBtnEnable() {
        mBinding.btnSaveSetting.isEnabled = true
        mBinding.btnSaveSetting.setBackgroundResource(R.drawable.selector_menu_green)
    }

    private fun setBtnUnEnable() {
        mBinding.btnSaveSetting.isEnabled = false
        mBinding.btnSaveSetting.setBackgroundResource(R.drawable.shape_btn_un_enable)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_save_setting -> {
                // 保存新的配置,清空之前的所有配置数据,重新依次添加
                DeviceService.getInstance().deleteAll()

                val mCabinetList = ArrayList<Device>()
                if (mDAJNumberSelected >= 0) {
                    val deviceId01 = mBinding.tvDajDeviceID01.text.toString().trim()
                    val deviceName01 = mBinding.etDajName01.text.toString().trim()
                    val lightControlBoardId01 =
                        mBinding.etLightControlBoardID01.text.toString().trim()
                    mCabinetList.add(Device(null, deviceId01, deviceName01, lightControlBoardId01))
                }

                if (mDAJNumberSelected >= 1) {
                    val deviceId02 = mBinding.tvDajDeviceID02.text.toString().trim()
                    val deviceName02 = mBinding.etDajName02.text.toString().trim()
                    val lightControlBoardId02 =
                        mBinding.etLightControlBoardID02.text.toString().trim()
                    mCabinetList.add(Device(null, deviceId02, deviceName02, lightControlBoardId02))
                }

                if (mDAJNumberSelected >= 2) {
                    val deviceId03 = mBinding.tvDajDeviceID03.text.toString().trim()
                    val deviceName03 = mBinding.etDajName03.text.toString().trim()
                    val lightControlBoardId03 =
                        mBinding.etLightControlBoardID03.text.toString().trim()
                    mCabinetList.add(Device(null, deviceId03, deviceName03, lightControlBoardId03))
                }

                if (mDAJNumberSelected >= 3) {
                    val deviceId04 = mBinding.tvDajDeviceID04.text.toString().trim()
                    val deviceName04 = mBinding.etDajName04.text.toString().trim()
                    val lightControlBoardId04 =
                        mBinding.etLightControlBoardID04.text.toString().trim()
                    mCabinetList.add(Device(null, deviceId04, deviceName04, lightControlBoardId04))
                }

                if (mDAJNumberSelected >= 4) {
                    val deviceId05 = mBinding.tvDajDeviceID05.text.toString().trim()
                    val deviceName05 = mBinding.etDajName05.text.toString().trim()
                    val lightControlBoardId05 =
                        mBinding.etLightControlBoardID05.text.toString().trim()
                    mCabinetList.add(Device(null, deviceId05, deviceName05, lightControlBoardId05))
                }

                if (mDAJNumberSelected >= 5) {
                    val deviceId06 = mBinding.tvDajDeviceID06.text.toString().trim()
                    val deviceName06 = mBinding.etDajName06.text.toString().trim()
                    val lightControlBoardId06 =
                        mBinding.etLightControlBoardID06.text.toString().trim()
                    mCabinetList.add(Device(null, deviceId06, deviceName06, lightControlBoardId06))
                }

                if (mDAJNumberSelected >= 6) {
                    val deviceId07 = mBinding.tvDajDeviceID07.text.toString().trim()
                    val deviceName07 = mBinding.etDajName07.text.toString().trim()
                    val lightControlBoardId07 =
                        mBinding.etLightControlBoardID07.text.toString().trim()
                    mCabinetList.add(Device(null, deviceId07, deviceName07, lightControlBoardId07))
                }

                if (mDAJNumberSelected >= 7) {
                    val deviceId08 = mBinding.tvDajDeviceID08.text.toString().trim()
                    val deviceName08 = mBinding.etDajName08.text.toString().trim()
                    val lightControlBoardId08 =
                        mBinding.etLightControlBoardID08.text.toString().trim()
                    mCabinetList.add(Device(null, deviceId08, deviceName08, lightControlBoardId08))
                }

                if (mDAJNumberSelected >= 8) {
                    val deviceId09 = mBinding.tvDajDeviceID09.text.toString().trim()
                    val deviceName09 = mBinding.etDajName09.text.toString().trim()
                    val lightControlBoardId09 =
                        mBinding.etLightControlBoardID09.text.toString().trim()
                    mCabinetList.add(Device(null, deviceId09, deviceName09, lightControlBoardId09))
                }

                if (mDAJNumberSelected >= 9) {
                    val deviceId10 = mBinding.tvDajDeviceID10.text.toString().trim()
                    val deviceName10 = mBinding.etDajName10.text.toString().trim()
                    val lightControlBoardId10 =
                        mBinding.etLightControlBoardID10.text.toString().trim()
                    mCabinetList.add(Device(null, deviceId10, deviceName10, lightControlBoardId10))
                }

                mSpUtil.applyValue(
                    SharedPreferencesUtil.Record(
                        SharedPreferencesUtil.Key.DAJNumberSelected,
                        mDAJNumberSelected
                    )
                )
                // 重新保存设备
                DeviceService.getInstance().insertOrReplace(mCabinetList)
                showSuccessToast("成功添加${mCabinetList.size}个档案组架")
                finish()
                // 保存成功重启APP
                // restartApp()
            }
        }
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mBinding.cabinetConfigCountdownTv.text =
            millisUntilFinished.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun restartApp() {
        val intent = Intent(this, GuideActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        Process.killProcess(Process.myPid())
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            val view = currentFocus //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (isHideKeyboard(view, motionEvent)) { //判断用户点击的是否是输入框以外的区域
                hideKeyboard(view!!.windowToken) //收起键盘
            }
        }
        return super.dispatchTouchEvent(motionEvent)
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     */
    private fun isHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {  //判断得到的焦点控件是否包含EditText
            val origin = intArrayOf(0, 0)
            v.getLocationInWindow(origin)
            val left = origin[0]
            //得到输入框在屏幕中上下左右的位置
            val top = origin[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        // 如果焦点不是EditText则忽略
        return false
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     */
    private fun hideKeyboard(token: IBinder?) {
        if (token != null) {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                token, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}