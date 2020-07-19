package com.zk.cabinet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.zk.cabinet.R
import com.zk.cabinet.bean.Dossier
import com.zk.cabinet.bean.DossierOperating
import com.zk.cabinet.constant.SelfComm

class DossierAdapter(context: Context, dossierList: List<Dossier>) :
    BaseAdapter() {
    private val mContext = context
    private var mDossierList = dossierList
    private val mLayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder? = null
        var view = convertView
        val dossier = mDossierList[position]
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.adapter_outbound_item, null)
            viewHolder = ViewHolder(
                view.findViewById(R.id.adapter_outbound_ll),
                view.findViewById(R.id.adapter_outbound_number_tv),
                view.findViewById(R.id.adapter_outbound_rfid_tv),
                view.findViewById(R.id.adapter_outbound_name_tv),
                view.findViewById(R.id.adapter_outbound_no_tv),
                view.findViewById(R.id.adapter_outbound_cate_tv),
                view.findViewById(R.id.adapter_outbound_type_tv),
                view.findViewById(R.id.adapter_outbound_position_tv)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.mAdapterOutboundNumberTv.text = dossier.warrantNum
        viewHolder.mAdapterOutboundRfidTv.text = dossier.rfidNum
        viewHolder.mAdapterOutboundNameTv.text = dossier.warrantName
        viewHolder.mAdapterOutboundNoTv.text = dossier.warrantNo
        viewHolder.mAdapterOutboundCateTv.text = dossier.warranCate
        viewHolder.mAdapterOutboundTypeTv.text = SelfComm.OPERATING_TYPE[dossier.operatingType]
        viewHolder.mAdapterOutboundPositionTv.text = "${dossier.cabinetId} - ${dossier.floor} -${dossier.light}"

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
        var mAdapterOutboundLl: LinearLayout,
        var mAdapterOutboundNumberTv: TextView,
        var mAdapterOutboundRfidTv: TextView,
        var mAdapterOutboundNameTv: TextView,
        var mAdapterOutboundNoTv: TextView,
        var mAdapterOutboundCateTv: TextView,
        var mAdapterOutboundTypeTv: TextView,
        var mAdapterOutboundPositionTv: TextView
    )

    fun setList(dossierList: List<Dossier>) {
        mDossierList = dossierList
    }
}