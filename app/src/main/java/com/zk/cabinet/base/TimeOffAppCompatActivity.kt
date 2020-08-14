package com.zk.cabinet.base

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zk.cabinet.R
import com.zk.cabinet.constant.SelfComm
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.common.utils.LogUtil

open class TimeOffAppCompatActivity : AppCompatActivity() {
    protected var isShow = true        //是否可刷新页面
    protected var isAutoFinish = true  //是否需要自动关闭页面
    protected var isDialogShow = false //是否有dialog显示
    protected var mSpUtil = SharedPreferencesUtil.instance
    protected var mCountdown = -1
    private var mTimer: CountDownTimer? = null
    private var mToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        if (isAutoFinish) {
            setAutoFinish()
        }
    }

    private fun setAutoFinish() {
        val mCountdownTemp = mSpUtil.getInt(Key.Countdown, 60)
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
                    showToast("已经${mCountdown - temp}秒无人操作${temp}秒后返回主界面")
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showToast(resources.getString(R.string.no_keycode_back))
            return false
        }
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
}