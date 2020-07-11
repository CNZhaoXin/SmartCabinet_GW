package com.hik.cabinet.net

import com.android.volley.Response
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.cabinet.net.NetworkRequest

import com.zk.common.utils.LogUtil
import org.json.JSONObject

class SyncUserResponseListener : Response.Listener<JSONObject> {
    var mResponseListener: NetworkRequest.ResponseListener? = null
    var mSyncStartTime: String? = null
    var mSyncEndTime: String? = null
    var mRecording = false
    var mGson: Gson? = null
    var mSkipRecords = 1             //当前页数
    var mTotalSkipRecords = 1        //总页数


    override fun onResponse(response: JSONObject?) {
        var resultCode = 0
        try {
            LogUtil.instance.d("-------------------------" + response.toString())
            val success = response!!.getBoolean("success")
            if (success) {
                resultCode = 200

            } else {
                if (mResponseListener != null) {
                    mResponseListener!!.onError(resultCode, response.toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (mResponseListener != null) {
                mResponseListener!!.onError(resultCode, "${e.message} \n ${response.toString()}")
                LogUtil.instance.d("人员同步", "${e.message} \n ${response.toString()}", true)
            }
        }
    }
}