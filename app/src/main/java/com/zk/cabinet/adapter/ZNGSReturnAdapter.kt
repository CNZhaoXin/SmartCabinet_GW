package com.zk.cabinet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.zk.cabinet.R
import com.zk.cabinet.bean.Device
import com.zk.cabinet.db.DeviceService
import com.zk.cabinet.entity.ResultGetToReturnList

class ZNGSReturnAdapter(
    context: Context,
    list: List<ResultGetToReturnList.DataBean>
) :
    BaseAdapter() {
    private val mContext = context
    private var mList = list
    private val mLayoutInflater = LayoutInflater.from(context)
    private var mDevice: Device? = null

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder? = null
        var view = convertView
        val entity = mList[position]
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.adapter_zngs_return, null)
            viewHolder = ZNGSReturnAdapter.ViewHolder(
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
        // 档案状态 待入库0,在库10,归还审批中50,待归还100,待归还200,异常9000
        val archivesStatus = entity.archivesStatus
        if (archivesStatus == 0) {
            viewHolder.tv_status.text = "待入库"
            viewHolder.tv_status.setTextColor(mContext.resources.getColor(R.color.colorDRK))
        } else if (archivesStatus == 10) {
            viewHolder.tv_status.text = "在库"
            viewHolder.tv_status.setTextColor(mContext.resources.getColor(R.color.colorZK))
        } else if (archivesStatus == 50) {
            viewHolder.tv_status.text = "归还审批中"
            viewHolder.tv_status.setTextColor(mContext.resources.getColor(R.color.colorJYSPZ))
        } else if (archivesStatus == 100) {
            viewHolder.tv_status.text = "待归还"
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
            viewHolder.iv_checked.visibility = View.GONE
            viewHolder.ll.setBackgroundColor(mContext.resources.getColor(R.color.color_list_unable))
            viewHolder.tv_position.setTextColor(mContext.resources.getColor(R.color.red_primary))
            viewHolder.tv_position.text = "该档案需前往「 一体机 」登录归还"
        } else if ("2" == cabinetType) {
            // 获取档案组柜配置的读写器设备ID，判断待归还的档案是档案组柜中某个柜子的档案,才可以操作
            val deviceList = DeviceService.getInstance().loadAll()
            var isExit = false
            for (device in deviceList) {
                if (isExit)
                    break

                if (device.deviceId == entity.cabinetEquipmentId) {
                    isExit = true
                    break
                }
            }

            tv_cabinetType = "档案组柜"
            if (isExit) {
                viewHolder.iv_checked.visibility = View.GONE
                viewHolder.ll.setBackgroundColor(mContext.resources.getColor(R.color.white))
                viewHolder.tv_position.setTextColor(mContext.resources.getColor(R.color.black))
                // 档案位置:操作屏-柜子-层-号库位(灯)
                viewHolder.tv_position.text =
                    "${entity.masterName}-${entity.cabinetName}-${entity.rowNo}层-${entity.numNo}号库位 (" + entity.lampList.joinToString() + "灯)"
            } else {
                viewHolder.iv_checked.visibility = View.GONE
                viewHolder.ll.setBackgroundColor(mContext.resources.getColor(R.color.color_list_unable))
                viewHolder.tv_position.setTextColor(mContext.resources.getColor(R.color.red_primary))
                viewHolder.tv_position.text = "该档案需前往「 ${entity.masterName} 」操作屏登录归还"
            }

        } else if ("3" == cabinetType) {
            tv_cabinetType = "档案单柜"
            viewHolder.iv_checked.visibility = View.GONE
            viewHolder.ll.setBackgroundColor(mContext.resources.getColor(R.color.color_list_unable))
            viewHolder.tv_position.setTextColor(mContext.resources.getColor(R.color.red_primary))
            viewHolder.tv_position.text = "该档案需前往「 ${entity.masterName} 」操作屏登录归还"
        }

        if (entity.isSelect) {
            viewHolder.iv_checked.visibility = View.VISIBLE
        } else {
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
        var iv_checked: ImageView
    )

    fun setList(list: List<ResultGetToReturnList.DataBean>) {
        mList = list
    }
}