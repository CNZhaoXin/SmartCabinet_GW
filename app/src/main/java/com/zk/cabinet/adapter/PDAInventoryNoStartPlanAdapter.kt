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
import com.zk.cabinet.entity.ResultGetNoStartInventoryPlan

class PDAInventoryNoStartPlanAdapter(
    context: Context,
    list: List<ResultGetNoStartInventoryPlan.DataBean>
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
            view = mLayoutInflater.inflate(R.layout.adapter_pda_inventory_no_start_plan, null)
            viewHolder = PDAInventoryNoStartPlanAdapter.ViewHolder(
                view.findViewById(R.id.cardView),
                view.findViewById(R.id.tv_id),
                view.findViewById(R.id.tv_inventoryType),
                view.findViewById(R.id.tv_house_name),
                view.findViewById(R.id.tv_planTime),
                view.findViewById(R.id.iv_arrow)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.tv_id.text = entity.id

        when (entity.inventoryType) {
            "1" -> {
                viewHolder.tv_inventoryType.text = "自动盘库"
                viewHolder.iv_arrow.visibility = View.INVISIBLE
                viewHolder.cardView.setCardBackgroundColor(mContext.resources.getColor(R.color.gray_light))
            }
            "2" -> {
                viewHolder.tv_inventoryType.text = "PDA盘库"
                viewHolder.iv_arrow.visibility = View.VISIBLE
                viewHolder.cardView.setCardBackgroundColor(mContext.resources.getColor(R.color.white))
            }
        }

        viewHolder.tv_house_name.text = entity.houseName
        viewHolder.tv_planTime.text = entity.planTime

        return view!!
    }

    fun setList(list: List<ResultGetNoStartInventoryPlan.DataBean>) {
        mList = list
    }

    private class ViewHolder(
        var cardView: CardView,
        var tv_id: TextView,
        var tv_inventoryType: TextView,
        var tv_house_name: TextView,
        var tv_planTime: TextView,
        var iv_arrow: ImageView,
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