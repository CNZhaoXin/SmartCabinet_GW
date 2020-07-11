package com.zk.cabinet.net

import com.android.volley.Response
import com.android.volley.VolleyError

class ResponseErrorListener : Response.ErrorListener {
    var mResponseListener: NetworkRequest.ResponseListener? = null

    override fun onErrorResponse(error: VolleyError?) {
        if (mResponseListener != null) {
            if (error != null)
                if (error.networkResponse != null)
                    mResponseListener!!.onError(
                        error.networkResponse.statusCode,
                        "VolleyError: $error"
                    )
                else
                    mResponseListener!!.onError(-1, "VolleyError: $error")
            else
                mResponseListener!!.onError(-1, "VolleyError: 未知")
        }
    }
}