package com.zk.cabinet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.zk.cabinet.R
import com.zk.cabinet.entity.ResultGetPosInfoByCabinetEquipmentId

class ZNGInventoryDossierDetailsAdapter(
    context: Context,
    list: List<ResultGetPosInfoByCabinetEquipmentId.DataBean.ArchivesListBean>,
    isInventory: Boolean
) :
    BaseAdapter() {
    private val mContext = context
    private var mList = list
    private val mLayoutInflater = LayoutInflater.from(context)
    private var mIsInventory = isInventory

    fun setIsInventory(isInventory: Boolean) {
        mIsInventory = isInventory
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder? = null
        var view = convertView
        val entity = mList[position]
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.adapter_zng_inventory_dossier_details, null)
            viewHolder = ViewHolder(
                view.findViewById(R.id.ll),
                view.findViewById(R.id.tv_file_name),
                view.findViewById(R.id.tv_status),
                view.findViewById(R.id.tv_epc),
                view.findViewById(R.id.tv_position),
                view.findViewById(R.id.iv_checked)
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

        // 档案柜型
        var tv_cabinetType = ""
        val cabinetType = entity.cabinetType
        if ("1" == cabinetType) {
            tv_cabinetType = "档案组架"
        } else if ("2" == cabinetType) {
            tv_cabinetType = "档案组柜"
        } else if ("3" == cabinetType) {
            tv_cabinetType = "档案单柜"
        }

        // 档案位置:档案室名称-柜子名称-层号库位(灯)
        viewHolder.tv_position.text =
            "${entity.houseName}-${entity.cabinetName}-${entity.rowNo}层${entity.numNo}号库位 (" + entity.lampList.joinToString() + "灯)"

        // 盘点前是否是在库位中的档案([在库/借阅审批中/待借阅]状态的)
        if (entity.isInStockStatus) {
            viewHolder.ll.setBackgroundColor(mContext.resources.getColor(R.color.white))

            if (mIsInventory) {
                if (entity.isInStocked) {
                    viewHolder.iv_checked.visibility = View.VISIBLE
                    viewHolder.iv_checked.setImageResource(R.mipmap.ic_confirm)
                } else {
                    viewHolder.iv_checked.visibility = View.VISIBLE
                    viewHolder.iv_checked.setImageResource(R.mipmap.ic_uncofirm)
                }
            } else {
                viewHolder.iv_checked.visibility = View.GONE
            }
        } else {
            viewHolder.ll.setBackgroundColor(mContext.resources.getColor(R.color.color_list_unable))
            viewHolder.iv_checked.visibility = View.GONE
        }

        return view!!
    }

    override fun getItem(position: Int): Any {
        return mList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mList.size
    }

    private class ViewHolder(
        var ll: LinearLayout,
        var tv_file_name: TextView,
        var tv_status: TextView,
        var tv_epc: TextView,
        var tv_position: TextView,
        var iv_checked: ImageView,
    )

    fun setList(list: ArrayList<ResultGetPosInfoByCabinetEquipmentId.DataBean.ArchivesListBean>) {
        mList = list
    }
}