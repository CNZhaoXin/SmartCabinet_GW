package com.zk.cabinet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.zk.cabinet.R
import com.zk.cabinet.bean.Device

class DeviceAdapter(context: Context, deviceList: List<Device>) : BaseAdapter() {
    private val mContext = context
    private var mDeviceList = deviceList
    private val mLayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder? = null
        var view = convertView
        val device = mDeviceList[position]

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.adapter_item_single_select, null)

            viewHolder = ViewHolder(
                view.findViewById(R.id.tv_text),
                view.findViewById(R.id.iv_checked)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.mTextView.text = device.deviceName

        if (device.isSelected) {
            viewHolder.mIvChecked.setImageDrawable(mContext.getDrawable(R.mipmap.ic_check))
        } else {
            viewHolder.mIvChecked.setImageDrawable(mContext.getDrawable(R.mipmap.ic_check_no))
        }
        return view!!
    }

    override fun getItem(position: Int): Any {
        return mDeviceList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mDeviceList.size
    }

    private class ViewHolder(
        var mTextView: TextView,
        var mIvChecked: ImageView
    )

    fun setList(deviceList: List<Device>) {
        mDeviceList = deviceList
    }

}