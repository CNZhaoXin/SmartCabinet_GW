package com.zk.cabinet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zk.cabinet.R
import com.zk.cabinet.entity.LightDebugEntity

class LightDebugAdapter(lightList: ArrayList<LightDebugEntity>, context: Context) :
    RecyclerView.Adapter<LightDebugAdapter.ViewHolder>() {
    private val mLightList = lightList
    private val mContext = context
    public var mOnItemClickListener: OnItemClickListener? = null
    public var mOnItemLongClickListener: OnItemLongClickListener? = null

    class ViewHolder(arg0: View) : RecyclerView.ViewHolder(arg0) {
        lateinit var adapterView: LinearLayout
        lateinit var iv_light: ImageView
        lateinit var tv_position: TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_light_debug_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.adapterView =
            view.findViewById(R.id.adapterView)
        viewHolder.iv_light =
            view.findViewById(R.id.iv_light)
        viewHolder.tv_position =
            view.findViewById(R.id.tv_position)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mLightList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lightEntity = mLightList[position]
        holder.tv_position.text = lightEntity.floor.toString() + "-" + lightEntity.light

        if (lightEntity.isSelected) {
            holder.iv_light.setBackgroundDrawable(mContext.resources.getDrawable(R.mipmap.ic_light_red))
        } else {
            holder.iv_light.setBackgroundDrawable(mContext.resources.getDrawable(R.mipmap.ic_light_white))
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

}