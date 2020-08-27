package com.zk.cabinet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zk.cabinet.R
import com.zk.cabinet.bean.Cabinet

class DemoInterfaceAdapter(cabinetList: List<Cabinet>, context: Context) :
    RecyclerView.Adapter<DemoInterfaceAdapter.ViewHolder>() {
    private val mCabinetList = cabinetList
    private val mContext = context
    public var mOnItemClickListener: OnItemClickListener? = null

    class ViewHolder(arg0: View) : RecyclerView.ViewHolder(arg0) {
        lateinit var mDemoInterfaceLl: FrameLayout
        lateinit var mDemoInterfaceTv: TextView
        lateinit var mTvLineNumber: TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_demo_interface_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.mDemoInterfaceTv =
            view.findViewById(R.id.adapter_demo_interface_tv)
        viewHolder.mDemoInterfaceLl =
            view.findViewById(R.id.adapter_demo_interface_ll)
        viewHolder.mTvLineNumber =
            view.findViewById(R.id.tv_line_number)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mCabinetList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cabinet = mCabinetList[position]
        holder.mTvLineNumber.text = cabinet.floor.toString() + "-" + cabinet.position.toString()

        // 显示库存里的档案数据
        if (cabinet.isStock) {
            if (cabinet.labelInfoList != null && cabinet.labelInfoList.size > 0) {
                holder.mDemoInterfaceLl.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.shape_line_fill))
                holder.mDemoInterfaceTv.setTextColor(mContext.resources.getColor(R.color.white))
                // 这里因为一个格子只有一个RFID盒子,通过扫描EPC,永远只会显示一本档案
                // holder.mDemoInterfaceTv.text = "${cabinet.labelInfoList.size}份档案"
                if (cabinet.stockList != null)
                    holder.mDemoInterfaceTv.text = "${cabinet.stockList.size}份档案"
                else
                    holder.mDemoInterfaceTv.text = "档 案"
            } else {
                holder.mDemoInterfaceLl.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.shape_line_stock))
                holder.mDemoInterfaceTv.setTextColor(mContext.resources.getColor(R.color.white))
                holder.mDemoInterfaceTv.text = "${cabinet.stockList.size}份档案"
            }
        } else {
            if (cabinet.labelInfoList != null && cabinet.labelInfoList.size > 0) {
                holder.mDemoInterfaceLl.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.shape_line_red))
                holder.mDemoInterfaceTv.setTextColor(mContext.resources.getColor(R.color.white))
                // 这里因为一个格子只有一个RFID盒子,通过扫描EPC,永远只会显示一本档案
                // holder.mDemoInterfaceTv.text = "${cabinet.labelInfoList.size}份档案"
                if (cabinet.stockList != null)
                    holder.mDemoInterfaceTv.text = "${cabinet.stockList.size}份档案"
                else
                    holder.mDemoInterfaceTv.text = "异 常"
            } else {
                holder.mDemoInterfaceLl.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.shape_line))
                holder.mDemoInterfaceTv.setTextColor(mContext.resources.getColor(R.color.md_cyan_A400))
                holder.mDemoInterfaceTv.text = "空 余"
            }
        }

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