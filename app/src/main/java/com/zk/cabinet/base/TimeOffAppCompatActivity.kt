package com.zk.cabinet.base

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.zk.cabinet.R
import com.zk.cabinet.constant.SelfComm
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.common.utils.LogUtil
import java.util.*

open class TimeOffAppCompatActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    protected var isShow = true        //是否可刷新页面
    protected var isAutoFinish = true  //是否需要自动关闭页面
    protected var isDialogShow = false //是否有dialog显示
    protected var mSpUtil = SharedPreferencesUtil.instance
    protected var mCountdown = -1
    private var mTimer: CountDownTimer? = null
    private var mToast: Toast? = null
    protected var textToSpeech: TextToSpeech? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 初始化tts引擎
            val result = textToSpeech!!.setLanguage(Locale.CHINA)
            // 设置参数
            textToSpeech!!.setPitch(1.2f) // 设置音调，,1.0是常规
            textToSpeech!!.setSpeechRate(1.0f) // 设定语速，1.0正常语速
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                if (AppUtils.isAppInstalled("com.iflytek.speechcloud")) {
                    showErrorToast("语音引擎包未设置，请到设置-无障碍-文字转语音中设置")
                } else {
                    showErrorToast("语音引擎包未安装")
                }
            }
        }
    }

    override fun onDestroy() {
        if (textToSpeech != null) {
            //释放资源
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
        super.onDestroy()
    }

    protected fun speek(speekText: String) {
        if (textToSpeech != null) {
            Log.e("zx-", "speek")
            textToSpeech!!.speak(speekText, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        if (isAutoFinish) {
            setAutoFinish()
        }

        if (textToSpeech == null) {
            textToSpeech = TextToSpeech(this, this)
        }
    }

    // 自动返回主界面时间设置 5分钟 300秒
    private fun setAutoFinish() {
        val mCountdownTemp = mSpUtil.getInt(Key.Countdown, 300)
        if (mCountdownTemp != mCountdown) {
            mCountdown = mCountdownTemp
            initTime()
        }
    }

    fun timerStart() {
        mTimer?.start()
    }

    fun timerCancel() {
        mTimer?.cancel()
    }

    protected fun initTime() {
        timerCancel()
        mTimer = object : CountDownTimer((mCountdown * 1000 + 500).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val temp = millisUntilFinished / 1000
                countDownTimerOnTick(temp)
                LogUtil.instance.d("无人操作，倒计时 -------------------- $temp")
                if (temp <= 10) {
                    // showWarningToast("已经${mCountdown - temp}秒无人操作,${temp}秒后返回主界面")
                    showWarningToast("${temp}秒后返回主界面")
                }
            }

            override fun onFinish() {
                if (isAutoFinish) {
                    setResult(SelfComm.COUNT_DOWN_RESULT_CODE)
                    finish()
                }
            }
        }
    }

    protected open fun countDownTimerOnTick(millisUntilFinished: Long) {}

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            //获取触摸动作，如果ACTION_UP，计时开始。
            MotionEvent.ACTION_UP -> {
                if (isAutoFinish && isShow) timerStart()
            }
            //否则其他动作计时取消
            else -> {
                if (isAutoFinish) timerCancel()
            }
        }
        return if (isDialogShow) false else super.dispatchTouchEvent(ev)
    }

    public open fun intentActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intentActivity(intent)
    }

    public open fun intentActivity(intent: Intent?) {
        startActivityForResult(intent, SelfComm.COUNT_DOWN_REQUEST_CODE)
    }

    override fun onStart() {
        super.onStart()
        isShow = true
        if (isAutoFinish && mTimer != null) {
            setAutoFinish()
            timerStart()
        }
    }

    override fun onStop() {
        super.onStop()
        isShow = false
        if (isAutoFinish) timerCancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (isAutoFinish &&
            requestCode == SelfComm.COUNT_DOWN_REQUEST_CODE &&
            resultCode == SelfComm.COUNT_DOWN_RESULT_CODE
        ) {
            setResult(SelfComm.COUNT_DOWN_RESULT_CODE)
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // 禁用回退键
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            showToast(resources.getString(R.string.no_keycode_back))
//            return false
//        }
        return super.onKeyDown(keyCode, event)
    }

    protected open fun showToast(charSequence: CharSequence?) {
//        if (mToast == null) mToast = Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT)
//        mToast!!.setText(charSequence)
//        mToast!!.show()
        mToast?.cancel()
        mToast =
            Toast.makeText(applicationContext, charSequence, Toast.LENGTH_SHORT).apply { show() }
    }

    fun showSuccessToast(showText: String) {
        ToastUtils.setBgColor(resources.getColor(R.color.green_primary))
        ToastUtils.setMsgColor(resources.getColor(R.color.white))
        ToastUtils.showLong(showText)
    }

    fun showErrorToast(showText: String) {
        ToastUtils.setBgColor(resources.getColor(R.color.red_primary))
        ToastUtils.setMsgColor(resources.getColor(R.color.white))
        ToastUtils.showLong(showText)
    }

    fun showWarningToast(showText: String) {
        ToastUtils.setBgColor(resources.getColor(R.color.md_yellow_900))
        ToastUtils.setMsgColor(resources.getColor(R.color.white))
        ToastUtils.showLong(showText)
    }

    fun showNormalToast(showText: String) {
        ToastUtils.setBgColor(resources.getColor(R.color.colorPrimary))
        ToastUtils.setMsgColor(resources.getColor(R.color.white))
        ToastUtils.showLong(showText)
    }
}