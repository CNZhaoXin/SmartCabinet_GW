package com.zk.cabinet.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import com.zk.cabinet.R
import java.util.*
import kotlin.collections.ArrayList

class MediaPlayerUtil {
    private lateinit var mContext: Context
    private lateinit var mediaPlayer: MediaPlayer
    private var mMediaPlayerList = Collections.synchronizedList(ArrayList<Int>())
    private val mNumberList = ArrayList<Int>()
    var mSoundSwitch = false

    companion object {
        val instance: MediaPlayerUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MediaPlayerUtil()
        }
    }

    init {
        mNumberList.add(R.raw.zero)
        mNumberList.add(R.raw.one)
        mNumberList.add(R.raw.two)
        mNumberList.add(R.raw.three)
        mNumberList.add(R.raw.four)
        mNumberList.add(R.raw.five)
        mNumberList.add(R.raw.six)
        mNumberList.add(R.raw.seven)
        mNumberList.add(R.raw.eight)
        mNumberList.add(R.raw.nine)
        mNumberList.add(R.raw.ten)
        mNumberList.add(R.raw.hundred)
    }

    fun init(context: Context, soundSwitch: Boolean) {
        this.mContext = context
        this.mSoundSwitch = soundSwitch
    }

    /**
     * 报数（只做了0-999的报数，如果有超过999的请重新修改）
     *
     * @param takeNumber 取出的本数
     * @param saveNumber 存入的本数
     */
    fun reportNumber(takeNumber: Int, saveNumber: Int, isTooManyFiles: Boolean) {
        if (mSoundSwitch && takeNumber < 1000 && saveNumber < 1000) {
            if (saveNumber != 0 || takeNumber != 0) {
                mMediaPlayerList.add(R.raw.operation)
                if (saveNumber != 0) generateSaveNumber(saveNumber)
                if (takeNumber != 0) generateTakeNumber(takeNumber)
            } else {
                mMediaPlayerList.add(R.raw.nochange)
            }
            if (isTooManyFiles) mMediaPlayerList.add(R.raw.too_many_files)
            mediaPlayerListPlay()
        }
    }

    private fun generateTakeNumber(takeNumberVal: Int) {
        var takeNumber = takeNumberVal
        mMediaPlayerList.add(R.raw.out)
        val singleDigit: Int
        var tenDigit = 0
        var hundredDigits = 0
        if (takeNumber >= 100) {
            hundredDigits = takeNumber / 100
            takeNumber %= 100
        }
        if (takeNumber >= 10) {
            tenDigit = takeNumber / 10
            takeNumber %= 10
        }
        singleDigit = takeNumber
        if (hundredDigits > 0) {
            mMediaPlayerList.add(mNumberList[hundredDigits])
            mMediaPlayerList.add(mNumberList[11])
        }
        if (tenDigit > 0) {
            mMediaPlayerList.add(mNumberList[tenDigit])
            mMediaPlayerList.add(mNumberList[10])
        } else if (hundredDigits > 0 && singleDigit > 0) {
            mMediaPlayerList.add(mNumberList[0])
        }
        if (singleDigit > 0) {
            mMediaPlayerList.add(mNumberList[singleDigit])
        }
        mMediaPlayerList.add(R.raw.book)
    }

    private fun generateSaveNumber(saveNumberVal: Int) {
        var saveNumber = saveNumberVal
        mMediaPlayerList.add(R.raw.deposit)
        val singleDigit: Int
        var tenDigit = 0
        var hundredDigits = 0
        if (saveNumber >= 100) {
            hundredDigits = saveNumber / 100
            saveNumber %= 100
        }
        if (saveNumber >= 10) {
            tenDigit = saveNumber / 10
            saveNumber %= 10
        }
        singleDigit = saveNumber
        if (hundredDigits > 0) {
            mMediaPlayerList.add(mNumberList[hundredDigits])
            mMediaPlayerList.add(mNumberList[11])
        }
        if (tenDigit > 0) {
            mMediaPlayerList.add(mNumberList[tenDigit])
            mMediaPlayerList.add(mNumberList[10])
        } else if (hundredDigits > 0 && singleDigit > 0) {
            mMediaPlayerList.add(mNumberList[0])
        }
        if (singleDigit > 0) {
            mMediaPlayerList.add(mNumberList[singleDigit])
        }
        mMediaPlayerList.add(R.raw.book)
    }

    fun stopReportNumber() {
        mMediaPlayerList.clear()
        mediaPlayer.release()
    }

    private fun mediaPlayerListPlay() {
        mediaPlayer = MediaPlayer.create(mContext, mMediaPlayerList.removeAt(0))
        mediaPlayer.setOnCompletionListener(onCompletionListener)
        mediaPlayer.setOnPreparedListener { mediaPlayer.start() }
    }

    private val onCompletionListener = OnCompletionListener {
        mediaPlayer.release()
        if (mMediaPlayerList.size > 0) {
            mediaPlayerListPlay()
        }
    }
}