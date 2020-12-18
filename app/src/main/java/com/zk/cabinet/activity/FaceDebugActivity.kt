package com.zk.cabinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.zk.cabinet.R
import com.zk.cabinet.base.TimeOffAppCompatActivity
import com.zk.cabinet.databinding.ActivityFaceDebugBinding
import com.zk.cabinet.faceServer.FaceRecognitionHttpServer
import com.zk.cabinet.faceServer.FaceRecognitionListener
import com.zk.cabinet.utils.SharedPreferencesUtil
import java.lang.ref.WeakReference

/**
 * 人脸设备测试界面
 */
class FaceDebugActivity : TimeOffAppCompatActivity(), AdapterView.OnItemClickListener,
    View.OnClickListener {
    private lateinit var mBinding: ActivityFaceDebugBinding
    private lateinit var mHandler: MyHandler

    companion object {
        private const val FACE_DEVICE_MSG = 0x01

        fun newIntent(packageContext: Context?): Intent {
            return Intent(packageContext, FaceDebugActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_face_debug)
        mBinding.onClickListener = this
        mBinding.onItemClickListener = this
        mHandler = MyHandler(this)

        // 关闭自动倒计时
        isAutoFinish = false

        // 显示设置的人脸服务端口
        val faceServerPort = mSpUtil.getInt(SharedPreferencesUtil.Key.FaceServicePort, 8080)
        mBinding.tvServerPort.text = faceServerPort.toString()

        startFaceServer(faceServerPort)
    }

    private lateinit var mFaceRecognitionHttpServer: FaceRecognitionHttpServer

    /**
     * 开启人脸识别服务
     */
    private fun startFaceServer(faceServerPort: Int) {
        try {
            mFaceRecognitionHttpServer =
                FaceRecognitionHttpServer(faceServerPort, object : FaceRecognitionListener {
                    override fun success(result: String) {
                        LogUtils.e("人脸识别服务-调试界面", "人脸认证成功")
                        val msg = Message.obtain()
                        msg.what = FACE_DEVICE_MSG
                        msg.obj = "人脸认证成功数据:" + result
                        mHandler.sendMessageDelayed(msg, 0)
                    }

                    override fun noRegister(result: String) {
                        LogUtils.e("人脸识别服务-调试界面", "人脸未注册 noRegister")
                        val msg = Message.obtain()
                        msg.what = FACE_DEVICE_MSG
                        msg.obj = "人脸未注册数据:" + result
                        mHandler.sendMessageDelayed(msg, 0)
                    }

                    override fun heart(result: String) {
                        LogUtils.e("人脸识别服务-调试界面", "心跳信息 heart")
                        val msg = Message.obtain()
                        msg.what = FACE_DEVICE_MSG
                        msg.obj = "设备心跳数据:" + result
                        mHandler.sendMessageDelayed(msg, 0)
                    }
                })

            // 启动人连识别web服务
            if (!mFaceRecognitionHttpServer.isAlive) {
                mFaceRecognitionHttpServer.start()
            }

            LogUtils.e(
                "人脸识别服务-调试界面",
                "人脸识别服务本机服务器-开启成功-IP:" + NetworkUtils.getIPAddress(true) + ",port=" + faceServerPort
            )
        } catch (e: Exception) {
            mFaceRecognitionHttpServer.stop()
            LogUtils.e("人脸识别服务-调试界面", "人脸识别服务本机服务器-开启失败. e = $e")
            showErrorToast("人脸识别服务本机服务器-开启失败. e = $e")
        }
    }

    override fun onDestroy() {
        if (mFaceRecognitionHttpServer.isAlive) {
            mFaceRecognitionHttpServer.stop();
            LogUtils.e("人脸识别服务-调试界面", "关闭服务");
        }
        super.onDestroy()
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            FACE_DEVICE_MSG -> {
                mBinding.tvData.text = msg.obj.toString()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> {
                finish()
            }
        }
    }

    private fun sendDelayMessage(msgWhat: Int, msgObj: String) {
        val message = Message.obtain()
        message.what = msgWhat
        message.obj = msgObj
        mHandler.sendMessageDelayed(message, 1000)
    }

    private class MyHandler(activity: FaceDebugActivity) :
        Handler() {
        private val weakReference = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            weakReference.get()!!.handleMessage(msg)
        }
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
    }
}