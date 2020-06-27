package com.zk.cabinet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zk.cabinet.R
import com.zk.cabinet.bean.CabinetOnlineInfo

class CabinetOnlineAdapter(context: Context, cabinetOnlineList: ArrayList<CabinetOnlineInfo>) :
    RecyclerView.Adapter<CabinetOnlineAdapter.ViewHolder>() {
    private val mContext = context
    private val mCabinetOnlineList = cabinetOnlineList

    class ViewHolder(arg0: View) : RecyclerView.ViewHolder(arg0) {
        var adapterCabinetOnlineItemTv: TextView? = null
        var adapterCabinetOnlineItemIv: ImageView? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_cabinet_online_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.adapterCabinetOnlineItemTv =
            view.findViewById<TextView>(R.id.adapter_cabinet_online_item_tv)
        viewHolder.adapterCabinetOnlineItemIv =
            view.findViewById<ImageView>(R.id.adapter_cabinet_online_item_iv)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mCabinetOnlineList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cabinetOnlineInfo = mCabinetOnlineList[position]
        holder.adapterCabinetOnlineItemTv!!.text = String.format(
            mContext.resources.getString(R.string.cabinet_online_box_name),
            cabinetOnlineInfo.mCodeName
        )
        holder.adapterCabinetOnlineItemIv!!.setImageResource(if (cabinetOnlineInfo.isOnLine) R.drawable.cabinet_online else R.drawable.cabinet_offline)
    }
}