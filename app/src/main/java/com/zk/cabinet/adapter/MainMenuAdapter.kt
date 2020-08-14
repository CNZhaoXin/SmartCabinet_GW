package com.zk.cabinet.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
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
                (view.findViewById(R.id.btn_menu_item) as TextView)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.tv_menu_item.text = mainMenuInfo.mText
        viewHolder.tv_menu_item.setBackgroundResource(mainMenuInfo.mBackground)

        val drawable: Drawable = mContext.resources.getDrawable(mainMenuInfo.mImage)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        viewHolder.tv_menu_item.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

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
        var tv_menu_item: TextView
    )

}