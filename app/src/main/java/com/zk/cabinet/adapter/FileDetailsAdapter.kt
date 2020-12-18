package com.zk.cabinet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.zk.cabinet.R
import com.zk.cabinet.entity.FileDetailsData

class FileDetailsAdapter(
    context: Context,
    searchFileList: List<FileDetailsData>
) :
    BaseAdapter() {
    private val mContext = context
    private var mSearchFileList = searchFileList
    private val mLayoutInflater = LayoutInflater.from(context)

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder? = null
        var view = convertView
        val entity = mSearchFileList[position]
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.adapter_file_item, null)
            viewHolder = FileDetailsAdapter.ViewHolder(
                view.findViewById(R.id.tv_file_name),
                view.findViewById(R.id.tv_status),
                view.findViewById(R.id.tv_epc),
                view.findViewById(R.id.tv_position)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        // 档案名称
        viewHolder.tv_file_name.text = entity.archivesName
        // 档案状态 待入库0,在库10,借阅审批中50,待借阅100,待归还200,异常9000
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
        // 档案epc
        viewHolder.tv_epc.text = entity.rfid
        // 档案柜类型
        var tv_cabinetType = ""
        val cabinetType = entity.cabinetType
        if ("1" == cabinetType) {
            tv_cabinetType = "档案组架"
        } else if ("2" == cabinetType) {
            tv_cabinetType = "档案组柜"
        } else if ("3" == cabinetType) {
            tv_cabinetType = "档案单柜"
        }
        // 档案位置:操作屏-柜子-层-号库位(灯)
        viewHolder.tv_position.text =
            "${entity.masterName}-${entity.cabinetName}-${entity.rowNo}层-${entity.numNo}号库位 (" + entity.lampList.joinToString() + "灯)"

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
        var tv_file_name: TextView,
        var tv_status: TextView,
        var tv_epc: TextView,
        var tv_position: TextView,
    )

    fun setList(searchFileList: List<FileDetailsData>) {
        mSearchFileList = searchFileList
    }
}