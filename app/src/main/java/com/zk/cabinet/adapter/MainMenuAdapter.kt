package com.zk.cabinet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.zk.cabinet.R
import com.zk.cabinet.entity.MainMenuInfo

class MainMenuAdapter(context: Context, list: ArrayList<MainMenuInfo>) : BaseAdapter() {
    private val mContext: Context = context
    private val mList = list
    private val mLayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        var view = convertView
        val mainMenuInfo = mList[position]

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.adapter_main_menu_item, view)

            viewHolder = ViewHolder(
                view.findViewById(R.id.rl_menu_item),
                view.findViewById(R.id.iv_menu_item),
                view.findViewById(R.id.tv_menu_item)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.mImageView.setImageResource(mainMenuInfo.mImage)
        viewHolder.mTextView.text = mainMenuInfo.mText
        viewHolder.mRelativeLayout.setBackgroundResource(mainMenuInfo.mBackground)


        // 这种设置图片方法是设置 DrawableLeft,DrawableRight,DrawableTop,DrawableDown 的
//        val drawable: Drawable = mContext.resources.getDrawable(mainMenuInfo.mImage)
//        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
//        viewHolder.tv_menu_item.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

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
        var mRelativeLayout: RelativeLayout,
        var mImageView: ImageView,
        var mTextView: TextView
    )

}