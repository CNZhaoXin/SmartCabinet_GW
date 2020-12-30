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
import com.zk.cabinet.entity.ResultGetPosInfoByCabinetEquipmentId

class ZNGInventoryAdapter(
    cabinetList: List<ResultGetPosInfoByCabinetEquipmentId.DataBean>,
    context: Context,
    isInventory: Boolean
) :
    RecyclerView.Adapter<ZNGInventoryAdapter.ViewHolder>() {
    private val mCabinetList = cabinetList
    private val mContext = context
    private var mIsInventory = isInventory
    public var mOnItemClickListener: OnItemClickListener? = null
    public var mOnItemLongClickListener: OnItemLongClickListener? = null
    public var mErrorListener: ErrorListener? = null

    class ViewHolder(arg0: View) : RecyclerView.ViewHolder(arg0) {
        lateinit var contentView: FrameLayout
        lateinit var tv_describe: TextView
        lateinit var tv_position: TextView
    }

    fun setIsInventory(isInventory: Boolean) {
        mIsInventory = isInventory
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_inventory_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.contentView = view.findViewById(R.id.contentView)
        viewHolder.tv_describe = view.findViewById(R.id.tv_describe)
        viewHolder.tv_position = view.findViewById(R.id.tv_position)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mCabinetList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dossier = mCabinetList[position]
        // 档案库位号
        holder.tv_position.text = "${dossier.numNo}"
        // 库位的档案集合
        val archivesList = dossier.archivesList

        // 盘点扫描后在库位中的档案集合(实在库档案)
        val isInStockedList =
            ArrayList<ResultGetPosInfoByCabinetEquipmentId.DataBean.ArchivesListBean>()
        // 盘点扫描后在不在库位中的档案集合(判定为异常档案)
        val isNoInStockedList =
            ArrayList<ResultGetPosInfoByCabinetEquipmentId.DataBean.ArchivesListBean>()

        if (archivesList != null && archivesList.size > 0) {
            for (archive in archivesList) {
                if (archive.isInStockStatus) {
                    if (archive.isInStocked) {
                        isInStockedList.add(archive)
                    } else {
                        isNoInStockedList.add(archive)
                    }
                }
            }

            if (mIsInventory && isNoInStockedList.size > 0) {
                // 盘点完缺少了档案的异常显示
                holder.tv_describe.text =
                    "在 ${isInStockedList.size} 缺 ${isNoInStockedList.size}"
                holder.contentView.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.shape_line_red))
                holder.tv_describe.setTextColor(mContext.resources.getColor(R.color.white))
            } else {
                // 有库位数据的正常显示
                holder.tv_describe.text =
                    "存 ${archivesList.size} 在 ${isInStockedList.size + isNoInStockedList.size}"

                // 档案都在库位的话显示绿色
                if (archivesList.size == isInStockedList.size + isNoInStockedList.size) {
                    holder.contentView.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.shape_line_green))
                    holder.tv_describe.setTextColor(mContext.resources.getColor(R.color.white))
                } else { // 有当前不在库位的档案显示黄色，不在库位的档案状态是异常状态，需显示红色
                    var isHasYCDossier = false
                    for (entity in archivesList) {
                        if (entity.archivesStatus == 9000) {
                            isHasYCDossier = true
                            break
                        }
                    }

                    if (isHasYCDossier) {
                        holder.contentView.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.shape_line_red))
                        holder.tv_describe.setTextColor(mContext.resources.getColor(R.color.white))
                    } else {
                        holder.contentView.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.shape_line_yellow))
                        holder.tv_describe.setTextColor(mContext.resources.getColor(R.color.white))
                    }

                }
            }

        } else {
            // 空库位显示
            holder.tv_describe.text = "空 余"
            holder.contentView.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.shape_line_white))
            holder.tv_describe.setTextColor(mContext.resources.getColor(R.color.white))
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