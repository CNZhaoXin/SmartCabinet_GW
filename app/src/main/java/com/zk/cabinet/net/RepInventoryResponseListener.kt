package com.zk.cabinet.net

import com.android.volley.Response
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.common.utils.LogUtil
import org.json.JSONObject

class RepInventoryResponseListener : Response.Listener<JSONObject> {
    var mResponseListener: NetworkRequest.ResponseListener? = null


    override fun onResponse(response: JSONObject?) {
        var resultCode = 0
        try {
            LogUtil.instance.d("-------------------------" + response.toString())
            val success = response!!.getBoolean("success")
            if (success) {
                resultCode = 200
                if (mResponseListener != null) {
                    mResponseListener!!.onSuccess()
                }

            } else {
                if (mResponseListener != null) {
                    mResponseListener!!.onError(resultCode, response.toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (mResponseListener != null) {
                mResponseListener!!.onError(resultCode, "${e.message} \n ${response.toString()}")
                LogUtil.instance.d("盘点结果数据上报", "${e.message} \n ${response.toString()}", true)
            }
        }
    }
}