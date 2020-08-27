package com.zk.cabinet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.zk.cabinet.R
import com.zk.cabinet.bean.ResultGetList
import com.zk.cabinet.bean.ResultGetOutBound
import com.zk.cabinet.constant.SelfComm

class DialogDossierDetailsAdapter(
    context: Context,
    stockList:ArrayList<ResultGetList.DataBean>
) :
    BaseAdapter() {
    private val mContext = context
    private var mDossierList = stockList
    private val mLayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder? = null
        var view = convertView
        val dossier = mDossierList[position]
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.adapter_dossier_details_item, null)
            viewHolder = ViewHolder(
                view.findViewById(R.id.adapter_outbound_number_tv),
                view.findViewById(R.id.adapter_outbound_rfid_tv),
                view.findViewById(R.id.adapter_outbound_name_tv),
                view.findViewById(R.id.adapter_outbound_no_tv),
                view.findViewById(R.id.adapter_outbound_cate_tv),
                view.findViewById(R.id.tv_position)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.mAdapterOutboundNumberTv.text = dossier.warrantNum
        viewHolder.mAdapterOutboundRfidTv.text = dossier.rfidNum
        viewHolder.mAdapterOutboundNameTv.text = dossier.warrantName
        viewHolder.mAdapterOutboundNoTv.text = dossier.warrantNo
        viewHolder.mAdapterOutboundCateTv.text = SelfComm.WARRANT_CATE[dossier.warranCate]
        viewHolder.mTvPosition.text = "${dossier.cabCode} - ${dossier.position}-${dossier.light}"

        return view!!
    }

    override fun getItem(position: Int): Any {
        return mDossierList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mDossierList.size
    }

    private class ViewHolder(
        var mAdapterOutboundNumberTv: TextView,
        var mAdapterOutboundRfidTv: TextView,
        var mAdapterOutboundNameTv: TextView,
        var mAdapterOutboundNoTv: TextView,
        var mAdapterOutboundCateTv: TextView,
        var mTvPosition: TextView
    )

    fun setList(dossierList : ArrayList<ResultGetList.DataBean>) {
        mDossierList = dossierList
    }
}