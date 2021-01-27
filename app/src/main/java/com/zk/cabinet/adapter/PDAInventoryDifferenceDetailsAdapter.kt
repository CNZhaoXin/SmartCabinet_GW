package com.zk.cabinet.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.zk.cabinet.R
import com.zk.cabinet.entity.ResultPDAInventoryDifference

class PDAInventoryDifferenceDetailsAdapter(
    context: Context,
    list: List<ResultPDAInventoryDifference.DataBean.RowsBean>
) :
    BaseAdapter() {
    private val mContext = context
    private var mList = list
    private val mLayoutInflater = LayoutInflater.from(mContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder? = null
        var view = convertView
        val entity = mList[position]
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.adapter_pda_inventory_difference_details, null)
            viewHolder = PDAInventoryDifferenceDetailsAdapter.ViewHolder(
                view.findViewById(R.id.tv_archivesCode),
                view.findViewById(R.id.tv_archivesNo),
                view.findViewById(R.id.tv_file_name),
                view.findViewById(R.id.tv_status),
                view.findViewById(R.id.tv_epc),
                view.findViewById(R.id.tv_position),
                view.findViewById(R.id.tv_position_inventory),
                view.findViewById(R.id.tv_yc_reason),
                view.findViewById(R.id.ll_archivesCode),
                view.findViewById(R.id.ll_archivesNo),
                view.findViewById(R.id.ll_file_name),
                view.findViewById(R.id.ll_status),
                view.findViewById(R.id.ll_epc),
                view.findViewById(R.id.ll_position),
                view.findViewById(R.id.ll_position_inventory),
                view.findViewById(R.id.ll_yc_reason)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        // 如果文号(档案编号)为空,就可以判定这个档案未进入系统
        if (entity.archivesCode == null || TextUtils.isEmpty(entity.archivesCode)) {
            viewHolder.ll_archivesCode.visibility = View.GONE
            viewHolder.ll_archivesNo.visibility = View.GONE
            viewHolder.ll_file_name.visibility = View.GONE
            viewHolder.ll_status.visibility = View.GONE
            viewHolder.ll_position.visibility = View.GONE
            viewHolder.ll_position_inventory.visibility = View.GONE

            // 档案epc
            viewHolder.tv_epc.text = entity.rfid
            viewHolder.tv_yc_reason.text = "未识别的标签"
        } else {
            viewHolder.ll_archivesCode.visibility = View.VISIBLE
            viewHolder.ll_archivesNo.visibility = View.VISIBLE
            viewHolder.ll_file_name.visibility = View.VISIBLE
            viewHolder.ll_status.visibility = View.VISIBLE
            viewHolder.ll_position.visibility = View.VISIBLE
            viewHolder.ll_position_inventory.visibility = View.VISIBLE

            // 档案编号（文号）
            viewHolder.tv_archivesCode.text = entity.archivesCode
            // 档案号
            viewHolder.tv_archivesNo.text = entity.archivesNo
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

            // 盘前位置:档案室名称-柜子名称-层号库位(灯)
            viewHolder.tv_position.text =
//            "${entity.houseNameOld}-${entity.cabinetNameOld}-${entity.posRowNoOld}层${entity.posNumNoOld}号库位 (" + entity.lampList.joinToString() + "灯)"
                "${entity.houseNameOld}-${entity.cabinetNameOld}-${entity.posRowNoOld}层${entity.posNumNoOld}号库位"

            // 盘后位置:档案室名称-柜子名称-层号库位(灯)
            viewHolder.tv_position_inventory.text =
//            "${entity.houseNameNow}-${entity.cabinetNameNow}-${entity.posRowNoNow}层${entity.posNumNoNow}号库位 (" + entity.lampList.joinToString() + "灯)"
                "${entity.houseNameNow}-${entity.cabinetNameNow}-${entity.posRowNoNow}层${entity.posNumNoNow}号库位"
            if (entity.posRowNoNow == 0 && entity.posNumNoNow == 0) {
                viewHolder.tv_yc_reason.text = "未盘到"
                viewHolder.ll_position_inventory.visibility = View.GONE
            } else {
                viewHolder.tv_yc_reason.text = "错位"
            }

        }

        return view!!
    }

    fun setList(list: List<ResultPDAInventoryDifference.DataBean.RowsBean>) {
        mList = list
    }

    private class ViewHolder(
        var tv_archivesCode: TextView,
        var tv_archivesNo: TextView,
        var tv_file_name: TextView,
        var tv_status: TextView,
        var tv_epc: TextView,
        var tv_position: TextView,
        var tv_position_inventory: TextView,
        var tv_yc_reason: TextView,
        var ll_archivesCode: LinearLayout,
        var ll_archivesNo: LinearLayout,
        var ll_file_name: LinearLayout,
        var ll_status: LinearLayout,
        var ll_epc: LinearLayout,
        var ll_position: LinearLayout,
        var ll_position_inventory: LinearLayout,
        var ll_yc_reason: LinearLayout
    )

    override fun getItem(position: Int): Any {
        return mList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mList.size
    }

}