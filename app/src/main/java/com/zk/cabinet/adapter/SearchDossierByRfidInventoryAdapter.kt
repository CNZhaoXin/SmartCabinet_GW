package com.zk.cabinet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.zk.cabinet.R
import com.zk.cabinet.entity.SearchDossierDetailsDataInventory

class SearchDossierByRfidInventoryAdapter(
    context: Context,
    searchSearchDossierList: ArrayList<SearchDossierDetailsDataInventory>
) :
    BaseAdapter() {
    private val mContext = context
    private var mSearchFileList = searchSearchDossierList
    private val mLayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder? = null
        var view = convertView
        val entity = mSearchFileList[position]
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.adapter_file_item_instorage, null)
            viewHolder = SearchDossierByRfidInventoryAdapter.ViewHolder(
                view.findViewById(R.id.tv_archivesCode),
                view.findViewById(R.id.tv_archivesNo),
                view.findViewById(R.id.tv_file_name),
                view.findViewById(R.id.tv_status),
                view.findViewById(R.id.tv_epc),
                view.findViewById(R.id.ll_archivesCode),
                view.findViewById(R.id.ll_archivesNo),
                view.findViewById(R.id.ll_file_name),
                view.findViewById(R.id.ll_status)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        // 档案epc
        viewHolder.tv_epc.text = entity.rfid

        if (entity.isHasData) {
            viewHolder.tv_epc.setTextColor(mContext.resources.getColor(R.color.black))
            // 档案编号（文号）
            viewHolder.ll_archivesCode.visibility = View.VISIBLE
            viewHolder.tv_archivesCode.text = entity.archivesCode
            // 档案号
            viewHolder.ll_archivesNo.visibility = View.VISIBLE
            viewHolder.tv_archivesNo.text = entity.archivesNo
            // 档案名称
            viewHolder.ll_file_name.visibility = View.VISIBLE
            viewHolder.tv_file_name.text = entity.archivesName
            // 档案状态 待入库0,在库10,借阅审批中50,待借阅100,待归还200,异常9000
            viewHolder.ll_status.visibility = View.VISIBLE
            val archivesStatus = entity.archivesStatus
            if (archivesStatus == 0) {
                viewHolder.tv_status.text = "待入库"
                viewHolder.tv_status.setTextColor(mContext.resources.getColor(R.color.colorDRK))
            } else if (archivesStatus == 10) {
                viewHolder.tv_status.text = "在库"
                viewHolder.tv_status.setTextColor(mContext.resources.getColor(R.color.colorZK))
            } else if (archivesStatus == 50) {
                viewHolder.tv_status.text = "借阅审批中"
                viewHolder.tv_status.setTextColor(mContext.resources.getColor(R.color.colorJYSPZ))
            } else if (archivesStatus == 100) {
                viewHolder.tv_status.text = "待借阅"
                viewHolder.tv_status.setTextColor(mContext.resources.getColor(R.color.colorDJY))
            } else if (archivesStatus == 200) {
                viewHolder.tv_status.text = "待归还"
                viewHolder.tv_status.setTextColor(mContext.resources.getColor(R.color.colorDGH))
            } else if (archivesStatus == 9000) {
                viewHolder.tv_status.text = "异常"
                viewHolder.tv_status.setTextColor(mContext.resources.getColor(R.color.colorYC))
            } else {
                viewHolder.tv_status.text = "xxxxx"
                viewHolder.tv_status.setTextColor(mContext.resources.getColor(R.color.colorYC))
            }
        } else {
            viewHolder.tv_epc.setTextColor(mContext.resources.getColor(R.color.colorYC))
            viewHolder.ll_archivesCode.visibility = View.GONE
            viewHolder.ll_archivesNo.visibility = View.GONE
            viewHolder.ll_file_name.visibility = View.GONE
            viewHolder.ll_status.visibility = View.GONE
        }

        return view!!
    }

    override fun getItem(position: Int): Any {
        return mSearchFileList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mSearchFileList.size
    }

    private class ViewHolder(
        var tv_archivesCode: TextView,
        var tv_archivesNo: TextView,
        var tv_file_name: TextView,
        var tv_status: TextView,
        var tv_epc: TextView,
        var ll_archivesCode: LinearLayout,
        var ll_archivesNo: LinearLayout,
        var ll_file_name: LinearLayout,
        var ll_status: LinearLayout
    )

    fun setList(searchSearchDossierList: ArrayList<SearchDossierDetailsDataInventory>) {
        mSearchFileList = searchSearchDossierList
    }
}