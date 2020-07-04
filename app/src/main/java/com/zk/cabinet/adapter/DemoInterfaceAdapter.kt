package com.zk.cabinet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zk.cabinet.R
import com.zk.cabinet.bean.Cabinet
import com.zk.cabinet.bean.DemoInterfaceLabelInfo

class DemoInterfaceAdapter(
    cabinetList: List<Cabinet>,
    context: Context
) :
    RecyclerView.Adapter<DemoInterfaceAdapter.ViewHolder>() {
    private val mCabinetList = cabinetList
    private val mContext = context
    public var mOnItemClickListener: OnItemClickListener? = null

    class ViewHolder(arg0: View) : RecyclerView.ViewHolder(arg0) {
        lateinit var mDemoInterfaceTv: TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_demo_interface_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.mDemoInterfaceTv =
            view.findViewById(R.id.adapter_demo_interface_tv)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mCabinetList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cabinet = mCabinetList[position]
        holder.mDemoInterfaceTv.text =
            if (cabinet.labelInfoList != null &&  cabinet.labelInfoList.size > 0) "${cabinet.labelInfoList.size}本档案" else "空格"
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener {
                val positionTemp: Int = holder.adapterPosition
                if (positionTemp != -1) {
                    // 当ViewHolder处于FLAG_REMOVED 等状态时会返回NO_POSITION-1
                    mOnItemClickListener!!.onItemClick(positionTemp)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}