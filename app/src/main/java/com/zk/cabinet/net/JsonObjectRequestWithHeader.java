package com.zk.cabinet.net;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blankj.utilcode.util.LogUtils;
import com.zk.cabinet.utils.SharedPreferencesUtil;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 在Volley http请求中添加请求头
 */
public class JsonObjectRequestWithHeader extends JsonObjectRequest {

    public JsonObjectRequestWithHeader(int method, String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public JsonObjectRequestWithHeader(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public JsonObjectRequestWithHeader(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new LinkedHashMap<>();
        // 自定义请求头 token:AEUHY98QIASUDH
        String token = SharedPreferencesUtil.Companion.getInstance().getString(SharedPreferencesUtil.Key.Token, "");
        LogUtils.e("Authorization:" + "Bearer " + token);
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }

}
