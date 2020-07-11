package com.hik.cabinet.net

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

abstract class VolleyRequest {
    private lateinit var requestQueue: RequestQueue

    open fun init(context: Context) {
        requestQueue = Volley.newRequestQueue(context)
    }

    open fun add(jsonObjectRequest: JsonObjectRequest?) {
        requestQueue.add(jsonObjectRequest)
    }

    open fun add(stringRequest: StringRequest?) {
        requestQueue.add(stringRequest)
    }
}