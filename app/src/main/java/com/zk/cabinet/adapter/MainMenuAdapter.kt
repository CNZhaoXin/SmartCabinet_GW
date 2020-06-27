package com.zk.cabinet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.zk.cabinet.R
import com.zk.cabinet.bean.MainMenuInfo

class MainMenuAdapter(context: Context, list: ArrayList<MainMenuInfo>) : BaseAdapter() {
    private val mList = list
    private val mLayoutInflater = LayoutInflater.from(context)
    private val mContext: Context = context

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        var view = convertView
        val mainMenuInfo = mList[position]
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.adapter_main_menu_item, view)
            viewHolder = ViewHolder(
                (view.findViewById(R.id.adapter_main_menu_item_iv) as ImageView),
                (view.findViewById(R.id.adapter_main_menu_item_tv) as TextView)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.adapter_main_menu_item_tv.text = mainMenuInfo.mImageName
        viewHolder.adapter_main_menu_item_iv.setBackgroundResource(mainMenuInfo.mImageUrl)

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
        var adapter_main_menu_item_iv: ImageView,
        var adapter_main_menu_item_tv: TextView
    )

}