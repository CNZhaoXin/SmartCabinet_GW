package com.zk.cabinet.fragment

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.zk.cabinet.R
import java.util.*

open class BaseFragment : Fragment(), TextToSpeech.OnInitListener {

    protected var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (textToSpeech == null) {
            textToSpeech = TextToSpeech(activity, this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 初始化tts引擎
            val result = textToSpeech!!.setLanguage(Locale.CHINA)
            // 设置参数
            textToSpeech!!.setPitch(1.2f) // 设置音调，,1.0是常规
            textToSpeech!!.setSpeechRate(1.0f) // 设定语速，1.0正常语速
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                if (AppUtils.isAppInstalled("com.iflytek.speechcloud")) {
                    showErrorToast("语音引擎包未设置")
                }else{
                    showErrorToast("语音引擎包未安装")
                }
            }
        }
    }

    protected fun speek(speekText: String) {
        if (textToSpeech != null) {
            Log.e("zx-", "speek")
            textToSpeech!!.speak(speekText, TextToSpeech.QUEUE_FLUSH, null)
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

    fun showSuccessToast(showText: String) {
        ToastUtils.setBgColor(resources.getColor(R.color.green_primary))
        ToastUtils.setMsgColor(resources.getColor(R.color.white))
        ToastUtils.showShort(showText)
    }

    fun showErrorToast(showText: String) {
        ToastUtils.setBgColor(resources.getColor(R.color.red_primary))
        ToastUtils.setMsgColor(resources.getColor(R.color.white))
        ToastUtils.showShort(showText)
    }

    fun showWarningToast(showText: String) {
        ToastUtils.setBgColor(resources.getColor(R.color.md_yellow_900))
        ToastUtils.setMsgColor(resources.getColor(R.color.white))
        ToastUtils.showShort(showText)
    }

    fun showNormalToast(showText: String) {
        ToastUtils.setBgColor(resources.getColor(R.color.colorPrimary))
        ToastUtils.setMsgColor(resources.getColor(R.color.white))
        ToastUtils.showShort(showText)
    }
}