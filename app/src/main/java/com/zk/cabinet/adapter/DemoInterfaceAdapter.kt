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
import com.zk.cabinet.db.DossierOperatingService

class DemoInterfaceAdapter(cabinetList: List<Cabinet>, context: Context) :
    RecyclerView.Adapter<DemoInterfaceAdapter.ViewHolder>() {
    private val mCabinetList = cabinetList
    private val mContext = context
    public var mOnItemClickListener: OnItemClickListener? = null
    public var mOnItemLongClickListener: OnItemLongClickListener? = null
    public var mErrorListener: ErrorListener? = null

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
            // 显示库存里面,并且扫描到的数据
            if (cabinet.labelInfoList != null && cabinet.labelInfoList.size > 0) {
                holder.mDemoInterfaceLl.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.ic_dossier_scan))
                holder.mDemoInterfaceTv.setTextColor(mContext.resources.getColor(R.color.black))
                // 这里因为一个格子只有一个RFID盒子,通过扫描EPC,永远只会显示一本档案
                // holder.mDemoInterfaceTv.text = "${cabinet.labelInfoList.size}份档案"
                val dossier =
                    DossierOperatingService.getInstance().queryByEPC(cabinet.labelInfoList[0].epc)
                if (dossier != null) {
                    holder.mDemoInterfaceTv.text = dossier.inputName
                } else {
                    holder.mDemoInterfaceTv.text = "档 案"
                }
            } else { // 显示库存里面的
                holder.mDemoInterfaceLl.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.ic_dossier_normal))
                holder.mDemoInterfaceTv.setTextColor(mContext.resources.getColor(R.color.black))

                if (cabinet.stockList != null && cabinet.stockList.size > 0) {
                    val dossier = DossierOperatingService.getInstance()
                        .queryByEPC(cabinet.stockList[0].rfidNum)
                    if (dossier != null) {
                        holder.mDemoInterfaceTv.text = dossier.inputName
                    } else {
                        holder.mDemoInterfaceTv.text = "档 案"
                    }
                } else {
                    holder.mDemoInterfaceTv.text = "档 案"
                }

            }
        } else {
            if (cabinet.labelInfoList != null && cabinet.labelInfoList.size > 0) {
                holder.mDemoInterfaceLl.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.ic_dossier_error))
                holder.mDemoInterfaceTv.setTextColor(mContext.resources.getColor(R.color.md_red_A200))
                // 这里因为一个格子只有一个RFID盒子,通过扫描EPC,永远只会显示一本档案
                // holder.mDemoInterfaceTv.text = "${cabinet.labelInfoList.size}份档案"
                if (cabinet.stockList != null) {
                    holder.mDemoInterfaceTv.text = "档 案"
                } else {
                    holder.mDemoInterfaceTv.text = "异 常"
                    // todo
                    cabinet.setError(true)
                    mErrorListener!!.errorSize()
                }
            } else {
                holder.mDemoInterfaceLl.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.ic_dossier_empty))
                holder.mDemoInterfaceTv.setTextColor(mContext.resources.getColor(R.color.gray_dossier))
                holder.mDemoInterfaceTv.text = "空 余"
            }
        }

        // todo 档案选中状态
        if (cabinet.isSelect) {
            holder.mDemoInterfaceLl.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.ic_dossier_select))
            holder.mDemoInterfaceTv.setTextColor(mContext.resources.getColor(R.color.md_teal_A400)) // md_cyan_A400
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

        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener {
                val positionTemp: Int = holder.adapterPosition
                if (positionTemp != -1) {
                    // 当ViewHolder处于FLAG_REMOVED 等状态时会返回NO_POSITION-1
                    mOnItemLongClickListener!!.onItemLongClick(positionTemp)
                }
                true
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }

    interface ErrorListener {
        fun errorSize()
    }
}