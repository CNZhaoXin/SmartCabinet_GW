package com.zk.cabinet.net

import android.content.Context
import com.google.gson.*
import com.hik.cabinet.net.VolleyRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.cabinet.utils.SharedPreferencesUtil.Key
import com.zk.common.utils.LogUtil
import com.zk.common.utils.TimeUtil
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.MutableMap as MutableMap1

class NetworkRequest : VolleyRequest() {
    private val mGson: Gson = GsonBuilder().create()

    public lateinit var mClientLogin: String

    private lateinit var mUrlRepInventory: String

    companion object {
        private const val URL_HEAD = "http://"
        private const val URL_COLON = ":"
        private const val DEFAULT_URL = "127.0.0.1"
        private const val DEFAULT_PORT = 7777

        private const val CLIENT_LOGIN = "/cabinet/client/login"


        private const val REP_INVENTORY = "/api/v1/repInventory"

        // 分页同步默认单页数据量
        const val DEFAULT_PAGE_SIZE = 200

        // 默认同步时间
        const val DEFAULT_SYNC_TIME = "2017-01-01 00:00:00"

        const val DEFAULT_AUTHORIZATION =
            "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTU5NDE2ODY2NywiaWF0IjoxNTkzOTk2NjY3fQ.MHT3VBp1O3HVudJONw-lwwDQ8-XmPHj8z5-tr65HEIRyEtCKFi6ytEA4Kady2TadEsiNrBWVXtYTec0cbOLvkw"

        val instance: NetworkRequest by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkRequest()
        }
    }

    override fun init(context: Context) {
        super.init(context)

        val url: String =
            SharedPreferencesUtil.instance.getString(Key.WebApiServiceIp, DEFAULT_URL)!!
        val port: Int = SharedPreferencesUtil.instance.getInt(Key.WebApiServicePort, DEFAULT_PORT)

        configModify(url, port)
    }

    fun configModify(url: String, port: Int) {
        mClientLogin = URL_HEAD + url + URL_COLON + port + CLIENT_LOGIN
        mUrlRepInventory = URL_HEAD + url + URL_COLON + port + REP_INVENTORY

    }


//    fun repInventory(
//        responseListener: ResponseListener?,
//        corpCode: String,
//        deviceCode: String,
//        userCode: String,
//        userName: String,
//        changedDossierList: ArrayList<Dossier>
//    ) {
//        if (changedDossierList.size == 0) return
//
//        val jsonObject = JSONObject()
//        try {
//            val jsonArray = JSONArray()
//            for (dossier in changedDossierList) {
//                val changedObject = JSONObject()
//                changedObject.put("status", dossier.nowState)
//                changedObject.put("epc", dossier.epc)
//                changedObject.put("cellCode", dossier.cellCode)
//                changedObject.put("cellName", dossier.cellName)
//                changedObject.put("cellID", dossier.cellId)
//                jsonArray.put(changedObject)
//            }
//
//            jsonObject.put("corpCode", corpCode)
//            jsonObject.put("deviceCode", deviceCode)
//            jsonObject.put("userCode", userCode)
//            jsonObject.put("userName", userName)
//            jsonObject.put("data", jsonArray)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//        mRepInventoryResponseListener.mResponseListener = responseListener
//        mRepInventoryResponseErrorListener.mResponseListener = responseListener
//        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
//            Method.POST, mUrlRepInventory, jsonObject,
//            mRepInventoryResponseListener, mRepInventoryResponseErrorListener
//        ) {
//            override fun getHeaders(): MutableMap1<String, String> {
//                val headers: MutableMap1<String, String> = HashMap()
//                headers["Authorization"] = DEFAULT_AUTHORIZATION
//                return headers
//            }
//        }
//        LogUtil.instance.d("----------------------${mUrlRepInventory}---------${jsonObject.toString()}")
//        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
//                8000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//            )
//        add(jsonObjectRequest)
//
//    }



    interface ResponseListener {
        fun onSuccess()
        fun onError(resultCode: Int, resultMessage: String?)
    }

}