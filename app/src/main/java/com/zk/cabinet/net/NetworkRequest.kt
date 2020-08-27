package com.zk.cabinet.net

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hik.cabinet.net.VolleyRequest
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.cabinet.utils.SharedPreferencesUtil.Key

class NetworkRequest : VolleyRequest() {
    private val mGson: Gson = GsonBuilder().create()

    lateinit var mClientLogin: String
    lateinit var mWarehousingList: String
    lateinit var mOutboundList: String
    lateinit var mWarehousingSubmission: String
    lateinit var mOutboundSubmission: String
    lateinit var mInventoryRequest: String
    lateinit var mInventoryReport: String
    lateinit var mList: String

    companion object {
        private const val URL_HEAD = "http://"
        private const val URL_COLON = ":"
        private const val DEFAULT_URL = "127.0.0.1"
        private const val DEFAULT_PORT = 7777

        private const val CLIENT_LOGIN = "/cabinet/client/login"
        private const val WAREHOUSING = "/cabinet/godown/entry/page"
        private const val OUTBOUND = "/cabinet/delivery/order/page"
        private const val WAREHOUSING_SUBMISSION = "/cabinet/sku/store"
        private const val OUTBOUND_SUBMISSION = "/cabinet/sku/out"
        private const val INVENTORY_REQUEST = "/cabinet/inventory/order/page?cabCode="
        private const val INVENTORY_REPORT = "/cabinet/inventory/order/add"
        private const val LIST = "/cabinet/sku/list"

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
        mWarehousingList = URL_HEAD + url + URL_COLON + port + WAREHOUSING
        mOutboundList = URL_HEAD + url + URL_COLON + port + OUTBOUND
        mWarehousingSubmission = URL_HEAD + url + URL_COLON + port + WAREHOUSING_SUBMISSION
        mOutboundSubmission = URL_HEAD + url + URL_COLON + port + OUTBOUND_SUBMISSION
        mInventoryRequest = URL_HEAD + url + URL_COLON + port + INVENTORY_REQUEST
        mInventoryReport = URL_HEAD + url + URL_COLON + port + INVENTORY_REPORT
        mList = URL_HEAD + url + URL_COLON + port + LIST
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