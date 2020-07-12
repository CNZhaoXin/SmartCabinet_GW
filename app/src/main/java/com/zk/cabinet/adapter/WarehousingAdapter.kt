package com.zk.cabinet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.zk.cabinet.R
import com.zk.cabinet.bean.WarehousingInfo
import com.zk.cabinet.constant.SelfComm

class WarehousingAdapter(context: Context, dossierList: List<WarehousingInfo>) :
    BaseAdapter() {
    private val mContext = context
    private var mDossierList = dossierList
    private val mLayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder? = null
        var view = convertView
        val dossier = mDossierList[position]
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.adapter_warehousing_item, null)
            viewHolder = ViewHolder(
                view.findViewById(R.id.adapter_warehousing_number_tv),
                view.findViewById(R.id.adapter_warehousing_rfid_tv),
                view.findViewById(R.id.adapter_warehousing_name_tv),
                view.findViewById(R.id.adapter_warehousing_no_tv),
                view.findViewById(R.id.adapter_warehousing_cate_tv),
                view.findViewById(R.id.adapter_warehousing_type_tv)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.mAdapterWarehousingNumberTv.text = dossier.warrantNum
        viewHolder.mAdapterWarehousingRfidTv.text = dossier.rfidNum
        viewHolder.mAdapterWarehousingNameTv.text = dossier.warrantName
        viewHolder.mAdapterWarehousingNoTv.text = dossier.warrantNo
        viewHolder.mAdapterWarehousingCateTv.text = dossier.warranCate
        viewHolder.mAdapterWarehousingTypeTv.text = SelfComm.OPERATING_TYPE[dossier.inStorageType]

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
        var mAdapterWarehousingNumberTv: TextView,
        var mAdapterWarehousingRfidTv: TextView,
        var mAdapterWarehousingNameTv: TextView,
        var mAdapterWarehousingNoTv: TextView,
        var mAdapterWarehousingCateTv: TextView,
        var mAdapterWarehousingTypeTv: TextView
    )

    fun setList(dossierList: List<WarehousingInfo>) {
        mDossierList = dossierList
    }
}