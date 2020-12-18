package com.zk.cabinet.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.LogUtils
import com.zk.cabinet.R
import com.zk.cabinet.adapter.LightDebugAdapter
import com.zk.cabinet.databinding.FragmentLightDebugBinding
import com.zk.cabinet.entity.LightDebugEntity
import com.zk.cabinet.helper.LightsSerialPortHelper
import java.lang.ref.WeakReference

private const val ARG_PARAM_DEVICE_ID = "lightControlBoardID"

/**
 *  档案组架-灯控调试界面
 */
class LightDebugFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentLightDebugBinding

    // 灯控板ID
    private lateinit var lightControlBoardID: String
    private lateinit var mHandler: MyHandler

    companion object {
        @JvmStatic
        fun newInstance(lightControlBoardID: String) =
            LightDebugFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(
                        ARG_PARAM_DEVICE_ID,
                        lightControlBoardID
                    )
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // 档案组架设备ID: 01-01-001-1,取灯控板id 1
            val splitArray = it.getString(ARG_PARAM_DEVICE_ID, "").split("-")
            lightControlBoardID = splitArray[splitArray.size - 1]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_light_debug,
            container,
            false
        )
        mBinding.onClickListener = this
        mHandler = MyHandler(this)

        mProgressDialog = ProgressDialog(requireContext(), R.style.mLoadingDialog)
        mProgressDialog.setCancelable(false)

        initView()
        return mBinding.root
    }

    private lateinit var mLightDebugAdapter: LightDebugAdapter
    private lateinit var mLightList: ArrayList<LightDebugEntity>

    private fun initView() {
        mLightList = ArrayList<LightDebugEntity>()
        // 一个档案架5层,每层15个灯,共75个灯
        for (floor in 1..5) {
            for (light in 1..15) {
                mLightList.add(LightDebugEntity(lightControlBoardID.toInt(), floor, light, false))
            }
        }
        LogUtils.e("创建的灯号集合:" + JSON.toJSONString(mLightList))
        mLightDebugAdapter = LightDebugAdapter(mLightList, this.requireContext())

        val manager = GridLayoutManager(
            this.requireContext(),
            15,
            LinearLayoutManager.VERTICAL,
            false
        )
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // 每个item占的比列
                return 1
            }
        }
        mBinding.demoInterfaceRv.layoutManager = manager
        mBinding.demoInterfaceRv.itemAnimator = DefaultItemAnimator()
        mBinding.demoInterfaceRv.adapter = mLightDebugAdapter
        mLightDebugAdapter.mOnItemClickListener = object :
            LightDebugAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // 点击进行亮灭灯
                val lightEntity = mLightList[position]
                if (lightEntity.isSelected) {
                    lightEntity.isSelected = false
                    LightsSerialPortHelper.getInstance()
                        .closeLight(lightEntity.deviceID, lightEntity.floor, lightEntity.light)
                } else {
                    lightEntity.isSelected = true
                    LightsSerialPortHelper.getInstance()
                        .openLight(lightEntity.deviceID, lightEntity.floor, lightEntity.light)
                }

                mLightDebugAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // 亮大灯
            R.id.btn_open_big_light -> {
                LightsSerialPortHelper.getInstance().openBigLight(lightControlBoardID.toInt())
            }

            // 灭大灯
            R.id.btn_close_big_light -> {
                LightsSerialPortHelper.getInstance().closeBigLight(lightControlBoardID.toInt())
            }

            // 全亮
            R.id.btn_open_all_light -> {
                for (light in mLightList) {
                    LightsSerialPortHelper.getInstance()
                        .openLight(light.deviceID, light.floor, light.light)
                    light.isSelected = true
                }
                mLightDebugAdapter.notifyDataSetChanged()
            }

            // 全灭
            R.id.btn_close_all_light -> {
                for (light in mLightList) {
                    LightsSerialPortHelper.getInstance()
                        .closeLight(light.deviceID, light.floor, light.light)
                    light.isSelected = false
                }
                mLightDebugAdapter.notifyDataSetChanged()
            }
        }
    }

    private lateinit var mProgressDialog: ProgressDialog
    private fun sendDelayMessage(msgWhat: Int, msgObj: String) {
        val message = Message.obtain()
        message.what = msgWhat
        message.obj = msgObj
        mHandler.sendMessageDelayed(message, 1000)
    }

    private class MyHandler(t: LightDebugFragment) : Handler() {
        private val mainWeakReference = WeakReference(t)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mainWeakReference.get()!!.handleMessage(msg)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
//            SUBMIT_SUCCESS -> {
//                mProgressDialog.dismiss()
//                showSuccessToast("${msg.obj}")
//            }
        }
    }
}