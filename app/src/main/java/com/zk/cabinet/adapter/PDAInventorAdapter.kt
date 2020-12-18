package com.zk.cabinet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.zk.cabinet.R

class PDAInventorAdapter(
    context: Context,
    list: List<String>
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
            view = mLayoutInflater.inflate(R.layout.adapter_pda_inventory_item, null)
            viewHolder = PDAInventorAdapter.ViewHolder(
                view.findViewById(R.id.tv_epc)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.tv_epc.text = entity

        return view!!
    }

    fun setList(list: List<String>) {
        mList = list
    }

    private class ViewHolder(
        var tv_epc: TextView
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