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
import com.zk.cabinet.databinding.ActivityDagSettingBinding
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.utils.SharedPreferencesUtil
import kotlin.properties.Delegates

/**
 * 档案柜配置
 */
class DAGSettingActivity : TimeOffAppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityDagSettingBinding

    private var mNumberBoxesItemSelected by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_dag_setting)

        setSupportActionBar(mBinding.cabinetConfigToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mBinding.onClickListener = this

        // 拿到被选择的柜体数(0-9 10个柜体)
        mNumberBoxesItemSelected = intent.getIntExtra("NumberOfBoxesSelected", 0)
        // 显示选择的可配置的柜体数
        if (mNumberBoxesItemSelected >= 0) {
            mBinding.cabinetConfig01Ll.visibility = View.VISIBLE
            mBinding.cabinetConfig01Tv.addTextChangedListener(object :
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

            mBinding.cabinetConfig01Edt.addTextChangedListener(object :
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
                mBinding.cabinetConfig02Ll.visibility = View.VISIBLE
                mBinding.cabinetConfig02Tv.addTextChangedListener(object :
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

                mBinding.cabinetConfig02Edt.addTextChangedListener(
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
                    mBinding.cabinetConfig03Ll.visibility = View.VISIBLE
                    mBinding.cabinetConfig03Tv.addTextChangedListener(
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

                    mBinding.cabinetConfig03Edt.addTextChangedListener(
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
                        mBinding.cabinetConfig04Ll.visibility =
                            View.VISIBLE
                        mBinding.cabinetConfig04Tv.addTextChangedListener(
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

                        mBinding.cabinetConfig04Edt.addTextChangedListener(
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
                            mBinding.cabinetConfig05Ll.visibility =
                                View.VISIBLE
                            mBinding.cabinetConfig05Tv.addTextChangedListener(
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

                            mBinding.cabinetConfig05Edt.addTextChangedListener(
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
                                mBinding.cabinetConfig06Ll.visibility =
                                    View.VISIBLE
                                mBinding.cabinetConfig06Tv.addTextChangedListener(
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

                                mBinding.cabinetConfig06Edt.addTextChangedListener(
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
                                    mBinding.cabinetConfig07Ll.visibility =
                                        View.VISIBLE
                                    mBinding.cabinetConfig07Tv.addTextChangedListener(
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

                                    mBinding.cabinetConfig07Edt.addTextChangedListener(
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


                                    if (mNumberBoxesItemSelected >= 7) {
                                        mBinding.cabinetConfig08Ll.visibility =
                                            View.VISIBLE
                                        mBinding.cabinetConfig08Tv.addTextChangedListener(
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

                                        mBinding.cabinetConfig08Edt.addTextChangedListener(
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

                                        if (mNumberBoxesItemSelected >= 8) {
                                            mBinding.cabinetConfig09Ll.visibility =
                                                View.VISIBLE
                                            mBinding.cabinetConfig09Tv.addTextChangedListener(
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

                                            mBinding.cabinetConfig09Edt.addTextChangedListener(
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

                                        if (mNumberBoxesItemSelected >= 9) {
                                            mBinding.cabinetConfig10Ll.visibility =
                                                View.VISIBLE
                                            mBinding.cabinetConfig10Tv.addTextChangedListener(
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

                                            mBinding.cabinetConfig10Edt.addTextChangedListener(
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
            }
        }

        // 显示之前配置过的值,可能有可能没有
        val deviceList = DeviceService.getInstance().loadAll()
        if (deviceList != null && deviceList.size > 0 && mNumberBoxesItemSelected >= 0) {
            mBinding.cabinetConfig01Tv.setText(deviceList[0].deviceName)
            mBinding.cabinetConfig01Edt.setText(deviceList[0].deviceId)

            if (deviceList.size > 1 && mNumberBoxesItemSelected >= 1) {
                mBinding.cabinetConfig02Tv.setText(deviceList[1].deviceName)
                mBinding.cabinetConfig02Edt.setText(deviceList[1].deviceId)

                if (deviceList.size > 2 && mNumberBoxesItemSelected >= 2) {
                    mBinding.cabinetConfig03Tv.setText(deviceList[2].deviceName)
                    mBinding.cabinetConfig03Edt.setText(deviceList[2].deviceId)

                    if (deviceList.size > 3 && mNumberBoxesItemSelected >= 3) {
                        mBinding.cabinetConfig04Tv.setText(deviceList[3].deviceName)
                        mBinding.cabinetConfig04Edt.setText(deviceList[3].deviceId)

                        if (deviceList.size > 4 && mNumberBoxesItemSelected >= 4) {
                            mBinding.cabinetConfig05Tv.setText(
                                deviceList[4].deviceName
                            )
                            mBinding.cabinetConfig05Edt.setText(
                                deviceList[4].deviceId
                            )

                            if (deviceList.size > 5 && mNumberBoxesItemSelected >= 5) {
                                mBinding.cabinetConfig06Tv.setText(
                                    deviceList[5].deviceName
                                )
                                mBinding.cabinetConfig06Edt.setText(
                                    deviceList[5].deviceId
                                )

                                if (deviceList.size > 6 && mNumberBoxesItemSelected >= 6) {
                                    mBinding.cabinetConfig07Tv.setText(
                                        deviceList[6].deviceName
                                    )
                                    mBinding.cabinetConfig07Edt.setText(
                                        deviceList[6].deviceId
                                    )

                                    if (deviceList.size > 7 && mNumberBoxesItemSelected >= 7) {
                                        mBinding.cabinetConfig08Tv.setText(
                                            deviceList[7].deviceName
                                        )
                                        mBinding.cabinetConfig08Edt.setText(
                                            deviceList[7].deviceId
                                        )

                                        if (deviceList.size > 8 && mNumberBoxesItemSelected >= 8) {
                                            mBinding.cabinetConfig09Tv.setText(
                                                deviceList[8].deviceName
                                            )
                                            mBinding.cabinetConfig09Edt.setText(
                                                deviceList[8].deviceId
                                            )

                                            if (deviceList.size > 9 && mNumberBoxesItemSelected >= 9) {
                                                mBinding.cabinetConfig10Tv.setText(
                                                    deviceList[9].deviceName
                                                )
                                                mBinding.cabinetConfig10Edt.setText(
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
        if (mNumberBoxesItemSelected == 0) {
            if (!TextUtils.isEmpty(
                    mBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig01Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mNumberBoxesItemSelected == 1) {
            if (!TextUtils.isEmpty(
                    mBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig01Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mNumberBoxesItemSelected == 2) {
            if (!TextUtils.isEmpty(
                    mBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig01Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mNumberBoxesItemSelected == 3) {
            if (!TextUtils.isEmpty(
                    mBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig01Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig04Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig04Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mNumberBoxesItemSelected == 4) {
            if (!TextUtils.isEmpty(
                    mBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig01Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig04Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig04Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig05Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig05Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mNumberBoxesItemSelected == 5) {
            if (!TextUtils.isEmpty(
                    mBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig01Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig04Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig04Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig05Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig05Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig06Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig06Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mNumberBoxesItemSelected == 6) {
            if (!TextUtils.isEmpty(
                    mBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig01Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig04Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig04Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig05Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig05Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig06Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig06Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig07Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig07Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mNumberBoxesItemSelected == 7) {
            if (!TextUtils.isEmpty(
                    mBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig01Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig04Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig04Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig05Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig05Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig06Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig06Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig07Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig07Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig08Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig08Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mNumberBoxesItemSelected == 8) {
            if (!TextUtils.isEmpty(
                    mBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig01Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig04Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig04Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig05Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig05Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig06Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig06Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig07Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig07Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig08Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig08Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig09Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig09Edt.text.toString().trim()
                )
            ) {
                setBtnEnable()
            } else {
                setBtnUnEnable()
            }
        } else if (mNumberBoxesItemSelected == 9) {
            if (!TextUtils.isEmpty(
                    mBinding.cabinetConfig01Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig01Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig02Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig03Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig04Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig04Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig05Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig05Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig06Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig06Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig07Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig07Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig08Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig08Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig09Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig09Edt.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig10Tv.text.toString().trim()
                )
                && !TextUtils.isEmpty(
                    mBinding.cabinetConfig10Edt.text.toString().trim()
                )
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
                // 保存新的配置,清空之前的配置数据,重新依次添加
                DeviceService.getInstance().deleteAll()

                val mCabinetList = ArrayList<Device>()
                if (mNumberBoxesItemSelected >= 0) {
                    val deviceName01 =
                        mBinding.cabinetConfig01Tv.text.toString()
                            .trim()
                    val deviceId01 =
                        mBinding.cabinetConfig01Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId01, deviceName01, null))
                }

                if (mNumberBoxesItemSelected >= 1) {
                    val deviceName02 =
                        mBinding.cabinetConfig02Tv.text.toString()
                            .trim()
                    val deviceId02 =
                        mBinding.cabinetConfig02Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId02, deviceName02, null))
                }

                if (mNumberBoxesItemSelected >= 2) {
                    val deviceName03 =
                        mBinding.cabinetConfig03Tv.text.toString()
                            .trim()
                    val deviceId03 =
                        mBinding.cabinetConfig03Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId03, deviceName03, null))
                }

                if (mNumberBoxesItemSelected >= 3) {
                    val deviceName04 =
                        mBinding.cabinetConfig04Tv.text.toString()
                            .trim()
                    val deviceId04 =
                        mBinding.cabinetConfig04Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId04, deviceName04, null))
                }

                if (mNumberBoxesItemSelected >= 4) {
                    val deviceName05 =
                        mBinding.cabinetConfig05Tv.text.toString()
                            .trim()
                    val deviceId05 =
                        mBinding.cabinetConfig05Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId05, deviceName05, null))
                }

                if (mNumberBoxesItemSelected >= 5) {
                    val deviceName06 =
                        mBinding.cabinetConfig06Tv.text.toString()
                            .trim()
                    val deviceId06 =
                        mBinding.cabinetConfig06Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId06, deviceName06, null))
                }

                if (mNumberBoxesItemSelected >= 6) {
                    val deviceName07 =
                        mBinding.cabinetConfig07Tv.text.toString()
                            .trim()
                    val deviceId07 =
                        mBinding.cabinetConfig07Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId07, deviceName07, null))
                }

                if (mNumberBoxesItemSelected >= 7) {
                    val deviceName08 =
                        mBinding.cabinetConfig08Tv.text.toString()
                            .trim()
                    val deviceId08 =
                        mBinding.cabinetConfig08Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId08, deviceName08, null))
                }

                if (mNumberBoxesItemSelected >= 8) {
                    val deviceName09 =
                        mBinding.cabinetConfig09Tv.text.toString()
                            .trim()
                    val deviceId09 =
                        mBinding.cabinetConfig09Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId09, deviceName09, null))
                }

                if (mNumberBoxesItemSelected >= 9) {
                    val deviceName10 =
                        mBinding.cabinetConfig10Tv.text.toString()
                            .trim()
                    val deviceId10 =
                        mBinding.cabinetConfig10Edt.text.toString()
                            .trim()
                    mCabinetList.add(Device(null, deviceId10, deviceName10, null))
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
}