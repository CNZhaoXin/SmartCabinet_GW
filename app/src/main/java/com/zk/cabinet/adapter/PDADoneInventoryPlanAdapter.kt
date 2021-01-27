package com.zk.cabinet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.zk.cabinet.R
import com.zk.cabinet.entity.ResultGetDoneInventoryPlan

class PDADoneInventoryPlanAdapter(
    context: Context,
    list: List<ResultGetDoneInventoryPlan.DataBean.RowsBean>
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
            view = mLayoutInflater.inflate(R.layout.adapter_pda_done_inventory_plan, null)
            viewHolder = PDADoneInventoryPlanAdapter.ViewHolder(
                view.findViewById(R.id.cardView),
                view.findViewById(R.id.tv_id),
                view.findViewById(R.id.tv_inventoryType),
                view.findViewById(R.id.tv_house_name),
                view.findViewById(R.id.tv_planTime),
                view.findViewById(R.id.tv_doTime),
                view.findViewById(R.id.tv_archivesNumOriginal),
                view.findViewById(R.id.tv_archivesNumNow),
                view.findViewById(R.id.tv_errNum),
                view.findViewById(R.id.iv_arrow)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        when (entity.inventoryType) {
            "1" -> {
                viewHolder.tv_inventoryType.text = "自动盘库"
                viewHolder.iv_arrow.visibility = View.VISIBLE
                viewHolder.cardView.setCardBackgroundColor(mContext.resources.getColor(R.color.gray_light))
            }
            "2" -> {
                viewHolder.tv_inventoryType.text = "PDA盘库"
                viewHolder.iv_arrow.visibility = View.VISIBLE
                viewHolder.cardView.setCardBackgroundColor(mContext.resources.getColor(R.color.white))
            }
        }

        viewHolder.tv_id.text = entity.id
        viewHolder.tv_house_name.text = entity.houseName
        viewHolder.tv_planTime.text = entity.planTime
        viewHolder.tv_doTime.text = entity.doTime
        viewHolder.tv_archivesNumOriginal.text = entity.archivesNumOriginal.toString()
        viewHolder.tv_archivesNumNow.text = entity.archivesNumNow.toString()
        viewHolder.tv_errNum.text = entity.errNum.toString()

        return view!!
    }

    fun setList(list: List<ResultGetDoneInventoryPlan.DataBean.RowsBean>) {
        mList = list
    }

    private class ViewHolder(
        var cardView: CardView,
        var tv_id: TextView,
        var tv_inventoryType: TextView,
        var tv_house_name: TextView,
        var tv_planTime: TextView,
        var tv_doTime: TextView,
        var tv_archivesNumOriginal: TextView,
        var tv_archivesNumNow: TextView,
        var tv_errNum: TextView,
        var iv_arrow: ImageView
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