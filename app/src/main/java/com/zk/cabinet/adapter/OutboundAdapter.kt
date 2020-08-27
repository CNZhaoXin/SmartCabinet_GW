package com.zk.cabinet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.zk.cabinet.R
import com.zk.cabinet.bean.ResultGetOutBound
import com.zk.cabinet.constant.SelfComm

class OutboundAdapter(
    context: Context,
    dossierList: ArrayList<ResultGetOutBound.NameValuePairsBeanX.DataBean.ValuesBean>
) :
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
                view.findViewById(R.id.adapter_outbound_iv),
                view.findViewById(R.id.adapter_outbound_number_tv),
                view.findViewById(R.id.adapter_outbound_rfid_tv),
                view.findViewById(R.id.adapter_outbound_name_tv),
                view.findViewById(R.id.adapter_outbound_no_tv),
                view.findViewById(R.id.adapter_outbound_cate_tv),
                view.findViewById(R.id.tv_warranType),
                view.findViewById(R.id.adapter_outbound_type_tv),
                view.findViewById(R.id.adapter_outbound_position_tv)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.mAdapterOutboundNumberTv.text = dossier.nameValuePairs.warrantNum
        viewHolder.mAdapterOutboundRfidTv.text = dossier.nameValuePairs.rfidNum
        viewHolder.mAdapterOutboundNameTv.text = dossier.nameValuePairs.warrantName
        viewHolder.mAdapterOutboundNoTv.text = dossier.nameValuePairs.warrantNo
        viewHolder.mAdapterOutboundCateTv.text =
            SelfComm.WARRANT_CATE[dossier.nameValuePairs.warranCate]
        viewHolder.tv_warranType.text = SelfComm.WARRANT_TYPE[dossier.nameValuePairs.warranType]
        viewHolder.mAdapterOutboundTypeTv.text =
            SelfComm.OUT_OPERATING_TYPE[dossier.nameValuePairs.outStorageType]
        viewHolder.mAdapterOutboundPositionTv.text =
            "${dossier.nameValuePairs.cabcode} - ${dossier.nameValuePairs.position}-${dossier.nameValuePairs.light}"

        if (dossier.nameValuePairs.isSelected) {
            viewHolder.mAdapterOutboundIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_check))
        } else {
            viewHolder.mAdapterOutboundIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_check_no))
        }
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
        var mAdapterOutboundIv: ImageView,
        var mAdapterOutboundNumberTv: TextView,
        var mAdapterOutboundRfidTv: TextView,
        var mAdapterOutboundNameTv: TextView,
        var mAdapterOutboundNoTv: TextView,
        var mAdapterOutboundCateTv: TextView,
        var tv_warranType: TextView,
        var mAdapterOutboundTypeTv: TextView,
        var mAdapterOutboundPositionTv: TextView
    )

    fun setList(dossierList: ArrayList<ResultGetOutBound.NameValuePairsBeanX.DataBean.ValuesBean>) {
        mDossierList = dossierList
    }
}