package com.zk.cabinet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.zk.cabinet.R
import com.zk.cabinet.bean.DossierOperating

class QueryAdapter(
    context: Context,
    dossierList: ArrayList<DossierOperating>
) :
    BaseAdapter() {
    private val mContext = context
    private var mDossierList = dossierList
    private val mLayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder? = null
        var view = convertView
        val dossier = mDossierList[position]
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.adapter_out_storage_item, null)
            viewHolder = QueryAdapter.ViewHolder(
                view.findViewById(R.id.iv_checked),
                view.findViewById(R.id.iv_user),
                view.findViewById(R.id.tv_user_name),
                view.findViewById(R.id.tv_sex),
                view.findViewById(R.id.tv_birthday),
                view.findViewById(R.id.tv_cabicode),
                view.findViewById(R.id.tv_position)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val resId =
            mContext.resources.getIdentifier(dossier.warrantName, "drawable", mContext.packageName)
        viewHolder.iv_user.setImageResource(resId)

        viewHolder.tv_user_name.text = dossier.inputName
        viewHolder.tv_sex.text = dossier.quarNo
        viewHolder.tv_birthday.text = dossier.warrantNo
        viewHolder.tv_cabicode.text = dossier.cabcode
        viewHolder.tv_position.text = "${dossier.floor}层${dossier.light}号"

        if (dossier.selected) {
            viewHolder.iv_checked.setImageDrawable(mContext.getDrawable(R.mipmap.ic_check))
        } else {
            viewHolder.iv_checked.setImageDrawable(mContext.getDrawable(R.mipmap.ic_check_no))
        }
        return view!!
    }

    override fun getItem(position: Int): Any {
        return mDossierList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mDossierList.size
    }

    private class ViewHolder(
        var iv_checked: ImageView,
        var iv_user: ImageView,
        var tv_user_name: TextView,
        var tv_sex: TextView,
        var tv_birthday: TextView,
        var tv_cabicode: TextView,
        var tv_position: TextView

    )

    fun setList(dossierList: ArrayList<DossierOperating>) {
        mDossierList = dossierList
    }
}