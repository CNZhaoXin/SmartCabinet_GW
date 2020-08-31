package com.zk.cabinet.activity

import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.bean.Device
import com.zk.cabinet.databinding.ActivityCabinetConfigurationBinding
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.utils.SharedPreferencesUtil
import kotlin.properties.Delegates


class CabinetConfigurationActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mActivityCabinetConfigurationBinding: ActivityCabinetConfigurationBinding

    private var mNumberBoxesItemSelected by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityCabinetConfigurationBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_cabinet_configuration)

        setSupportActionBar(mActivityCabinetConfigurationBinding.cabinetConfigToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mActivityCabinetConfigurationBinding.onClickListener = this

        // 拿到被选择的柜体数(0-6 7个柜体)
        mNumberBoxesItemSelected = intent.getIntExtra("NumberOfBoxesSelected", 0)
        // 显示选择的可配置的柜体数
        if (mNumberBoxesItemSelected >= 0) {
            mActivityCabinetConfigurationBinding.cabinetConfig01Ll.visibility = View.VISIBLE
            mActivityCabinetConfigurationBinding.cabinetConfig01Tv.addTextChangedListener(object :
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
                }

                override fun afterTextChanged(s: Editable) {}
            })

            mActivityCabinetConfigurationBinding.cabinetConfig01Edt.addTextChangedListener(object :
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
                }

                override fun afterTextChanged(s: Editable) {}
            })

            if (mNumberBoxesItemSelected >= 1) {
                mActivityCabinetConfigurationBinding.cabinetConfig02Ll.visibility = View.VISIBLE
                mActivityCabinetConfigurationBinding.cabinetConfig02Tv.addTextChangedListener(object :
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
                    }

                    override fun afterTextChanged(s: Editable) {}
                })

                mActivityCabinetConfigurationBinding.cabinetConfig02Edt.addTextChangedListener(
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
                        }

                        override fun afterTextChanged(s: Editable) {}
                    })

                if (mNumberBoxesItemSelected >= 2) {
                    mActivityCabinetConfigurationBinding.cabinetConfig03Ll.visibility = View.VISIBLE
                    mActivityCabinetConfigurationBinding.cabinetConfig03Tv.addTextChangedListener(
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
                            }

                            override fun afterTextChanged(s: Editable) {}
                        })

                    mActivityCabinetConfigurationBinding.cabinetConfig03Edt.addTextChangedListener(
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
                            }

                            override fun afterTextChanged(s: Editable) {}
                        })

                    if (mNumberBoxesItemSelected >= 3) {
                        mActivityCabinetConfigurationBinding.cabinetConfig04Ll.visibility =
                            View.VISIBLE
                        mActivityCabinetConfigurationBinding.cabinetConfig04Tv.addTextChangedListener(
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
                                }

                                override fun afterTextChanged(s: Editable) {}
                            })

                        mActivityCabinetConfigurationBinding.cabinetConfig04Edt.addTextChangedListener(
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
                                }

                                override fun afterTextChanged(s: Editable) {}
                            })

                        if (mNumberBoxesItemSelected >= 4) {
                            mActivityCabinetConfigurationBinding.cabinetConfig05Ll.visibility =
                                View.VISIBLE
                            mActivityCabinetConfigurationBinding.cabinetConfig05Tv.addTextChangedListener(
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
                                    }

                                    override fun afterTextChanged(s: Editable) {}
                                })

                            mActivityCabinetConfigurationBinding.cabinetConfig05Edt.addTextChangedListener(
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
                                    }

                                    override fun afterTextChanged(s: Editable) {}
                                })

                            if (mNumberBoxesItemSelected >= 5) {
                                mActivityCabinetConfigurationBinding.cabinetConfig06Ll.visibility =
                                    View.VISIBLE
                                mActivityCabinetConfigurationBinding.cabinetConfig06Tv.addTextChangedListener(
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
                                        }

                                        override fun afterTextChanged(s: Editable) {}
                                    })

                                mActivityCabinetConfigurationBinding.cabinetConfig06Edt.addTextChangedListener(
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
                                        }

                                        override fun afterTextChanged(s: Editable) {}
                                    })


                                if (mNumberBoxesItemSelected >= 6) {
                                    mActivityCabinetConfigurationBinding.cabinetConfig07Ll.visibility =
                                        View.VISIBLE
                                    mActivityCabinetConfigurationBinding.cabinetConfig07Tv.addTextChangedListener(
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
                                            }

                                            override fun afterTextChanged(s: Editable) {}
                                        })

                                    mActivityCabinetConfigurationBinding.cabinetConfig07Edt.addTextChangedListener(
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

        // 显示之前配置过的值,可能有可能没有
        val deviceList = DeviceService.getInstance().loadAll()
        if (deviceList != null && deviceList.size > 0 && mNumberBoxesItemSelected >= 0) {
            mActivityCabinetConfigurationBinding.cabinetConfig01Tv.setText(deviceList[0].deviceName)
            mActivityCabinetConfigurationBinding.cabinetConfig01Edt.setText(deviceList[0].deviceId)

            if (deviceList.size > 1 && mNumberBoxesItemSelected >= 1) {
                mActivityCabinetConfigurationBinding.cabinetConfig02Tv.setText(deviceList[1].deviceName)
                mActivityCabinetConfigurationBinding.cabinetConfig02Edt.setText(deviceList[1].deviceId)

                if (deviceList.size > 2 && mNumberBoxesItemSelected >= 2) {
                    mActivityCabinetConfigurationBinding.cabinetConfig03Tv.setText(deviceList[2].deviceName)
                    mActivityCabinetConfigurationBinding.cabinetConfig03Edt.setText(deviceList[2].deviceId)

                    if (deviceList.size > 3 && mNumberBoxesItemSelected >= 3) {
                        mActivityCabinetConfigurationBinding.cabinetConfig04Tv.setText(deviceList[3].deviceName)
                        mActivityCabinetConfigurationBinding.cabinetConfig04Edt.setText(deviceList[3].deviceId)

                        if (deviceList.size > 4 && mNumberBoxesItemSelected >= 4) {
                            mActivityCabinetConfigurationBinding.cabinetConfig05Tv.setText(
                                deviceList[4].deviceName
                            )
                            mActivityCabinetConfigurationBinding.cabinetConfig05Edt.setText(
                                deviceList[4].deviceId
                            )

                            if (deviceList.size > 5 && mNumberBoxesItemSelected >= 5) {
                                mActivityCabinetConfigurationBinding.cabinetConfig06Tv.setText(
                                    deviceList[5].deviceName
                                )
                                mActivityCabinetConfigurationBinding.cabinetConfig06Edt.setText(
                                    deviceList[5].deviceId
                                )

                                if (deviceList.size > 6 && mNumberBoxesItemSelected >= 6) {
                                    mActivityCabinetConfigurationBinding.cabinetConfig07Tv.setText(
                                        deviceList[6].deviceName
                                    )
                                    mActivityCabinetConfigurationBinding.cabinetConfig07Edt.setText(
                                        deviceList[6].deviceId
                                    )

                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private fun setBtnStatus() {
        if (mNumberBoxesItemSelected == 0) {
            if (!TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig01Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mNumberBoxesItemSelected == 1) {
            if (!TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig01Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig02Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig02Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mNumberBoxesItemSelected == 2) {
            if (!TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig01Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig02Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig02Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig03Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig03Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mNumberBoxesItemSelected == 3) {
            if (!TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig01Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig02Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig02Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig03Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig03Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig04Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig04Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mNumberBoxesItemSelected == 4) {
            if (!TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig01Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig02Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig02Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig03Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig03Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig04Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig04Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig05Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig05Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mNumberBoxesItemSelected == 5) {
            if (!TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig01Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig02Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig02Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig03Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig03Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig04Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig04Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig05Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig05Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig06Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig06Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mNumberBoxesItemSelected == 6) {
            if (!TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig01Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig02Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig02Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig03Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig03Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig04Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig04Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig05Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig05Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig06Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig06Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig07Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mActivityCabinetConfigurationBinding.cabinetConfig07Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        }

    }

    private fun setBtnEnable() {
        mActivityCabinetConfigurationBinding.btnSaveSetting.isEnabled = true
        mActivityCabinetConfigurationBinding.btnSaveSetting.setBackgroundResource(R.drawable.selector_menu_green)
    }

    private fun setBtnUnEnable() {
        mActivityCabinetConfigurationBinding.btnSaveSetting.isEnabled = false
        mActivityCabinetConfigurationBinding.btnSaveSetting.setBackgroundResource(R.drawable.shape_btn_un_enable)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_save_setting -> {
                // 保存新的配置,清空之前的配置数据,重新依次添加
                DeviceService.getInstance().deleteAll()

                val mCabinetList = ArrayList<Device>()
                if (mNumberBoxesItemSelected >= 0) {
                    val deviceName01 =
                        mActivityCabinetConfigurationBinding.cabinetConfig01Tv.text.toString()
                            .trim()
                    val deviceId01 =
                        mActivityCabinetConfigurationBinding.cabinetConfig01Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId01, deviceName01))
                }

                if (mNumberBoxesItemSelected >= 1) {
                    val deviceName02 =
                        mActivityCabinetConfigurationBinding.cabinetConfig02Tv.text.toString()
                            .trim()
                    val deviceId02 =
                        mActivityCabinetConfigurationBinding.cabinetConfig02Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId02, deviceName02))
                }

                if (mNumberBoxesItemSelected >= 2) {
                    val deviceName03 =
                        mActivityCabinetConfigurationBinding.cabinetConfig03Tv.text.toString()
                            .trim()
                    val deviceId03 =
                        mActivityCabinetConfigurationBinding.cabinetConfig03Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId03, deviceName03))
                }

                if (mNumberBoxesItemSelected >= 3) {
                    val deviceName04 =
                        mActivityCabinetConfigurationBinding.cabinetConfig04Tv.text.toString()
                            .trim()
                    val deviceId04 =
                        mActivityCabinetConfigurationBinding.cabinetConfig04Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId04, deviceName04))
                }

                if (mNumberBoxesItemSelected >= 4) {
                    val deviceName05 =
                        mActivityCabinetConfigurationBinding.cabinetConfig05Tv.text.toString()
                            .trim()
                    val deviceId05 =
                        mActivityCabinetConfigurationBinding.cabinetConfig05Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId05, deviceName05))
                }

                if (mNumberBoxesItemSelected >= 5) {
                    val deviceName06 =
                        mActivityCabinetConfigurationBinding.cabinetConfig06Tv.text.toString()
                            .trim()
                    val deviceId06 =
                        mActivityCabinetConfigurationBinding.cabinetConfig06Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId06, deviceName06))
                }

                if (mNumberBoxesItemSelected >= 6) {
                    val deviceName07 =
                        mActivityCabinetConfigurationBinding.cabinetConfig07Tv.text.toString()
                            .trim()
                    val deviceId07 =
                        mActivityCabinetConfigurationBinding.cabinetConfig07Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId07, deviceName07))
                }

                mSpUtil.applyValue(
                    SharedPreferencesUtil.Record(
                        SharedPreferencesUtil.Key.NumberOfBoxesSelected,
                        mNumberBoxesItemSelected
                    )
                )
                // 重新保存设备
                DeviceService.getInstance().insertOrReplace(mCabinetList)
                Toast.makeText(this, "成功保存柜体配置", Toast.LENGTH_SHORT).show()
                // 保存成功重启APP
                restartApp()
            }
        }
    }

    override fun countDownTimerOnTick(millisUntilFinished: Long) {
        super.countDownTimerOnTick(millisUntilFinished)
        mActivityCabinetConfigurationBinding.cabinetConfigCountdownTv.text =
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
}