package com.zk.cabinet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.zk.cabinet.R
import com.zk.cabinet.entity.ResultGetBoxListByPosCode

/**
 * PDA-档案盒
 */
class PDADossierBoxAdapter(
    context: Context,
    list: ArrayList<ResultGetBoxListByPosCode.DataBean.BoxListBean>
) :
    BaseAdapter() {
    private val mContext = context
    private var mList = list
    private val mLayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder? = null
        var view = convertView
        val entity = mList[position]
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.adapter_pda_dossier_box, null)
            viewHolder = PDADossierBoxAdapter.ViewHolder(
                view.findViewById(R.id.tv_boxStatus),
                view.findViewById(R.id.tv_boxId),
                view.findViewById(R.id.tv_boxCode),
                view.findViewById(R.id.tv_boxType),
                view.findViewById(R.id.tv_position),
                view.findViewById(R.id.tv_storageNo)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        // 档案盒状态: 未绑定 0 已绑定 100 已解绑 200
        val boxStatus = entity.boxStatus
        if ("0" == boxStatus) {
            viewHolder.tv_boxStatus.text = "未绑定"
            viewHolder.tv_boxStatus.setTextColor(mContext.resources.getColor(R.color.colorDRK))
        } else if ("100" == boxStatus) {
            viewHolder.tv_boxStatus.text = "已绑定"
            viewHolder.tv_boxStatus.setTextColor(mContext.resources.getColor(R.color.colorZK))
        } else if ("200" == boxStatus) {
            viewHolder.tv_boxStatus.text = "已解绑"
            viewHolder.tv_boxStatus.setTextColor(mContext.resources.getColor(R.color.colorDGH))
        } else {
            viewHolder.tv_boxStatus.text = "xxx"
            viewHolder.tv_boxStatus.setTextColor(mContext.resources.getColor(R.color.colorYC))
        }
        // 盒ID(盒二维码)
        viewHolder.tv_boxId.text = entity.boxDataId
        // 盒号
        viewHolder.tv_boxCode.text = entity.boxCode
        // 盒类型
        // 档案盒类型: 5cm/5 10cm/10
        if ("5" == entity.boxType) {
            viewHolder.tv_boxType.text = "5cm"
        } else if ("10" == entity.boxType) {
            viewHolder.tv_boxType.text = "10cm"
        }

        // 位置
        val posInfo = entity.posInfo
        if (posInfo != null) {
            // 档案室
            val baseArchivesHouse = posInfo.baseArchivesHouse
            // 档案柜
            val baseArchivesCabinet = posInfo.baseArchivesCabinet
            // 灯位
            val lampList = posInfo.lampList
            if (baseArchivesHouse != null && baseArchivesCabinet != null && lampList != null) {
                // 档案位置:档案室名称-柜子名称-x层x号库位(灯)
                viewHolder.tv_position.text =
                    "${baseArchivesHouse.name}-${baseArchivesCabinet.attributeName}-${entity.rowNo}层${entity.numNo}号库位 (" + lampList.joinToString() + "灯)"
            }
        }

        // 库位号
        viewHolder.tv_storageNo.text = entity.posCode

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
        var tv_boxStatus: TextView,
        var tv_boxId: TextView,
        var tv_boxCode: TextView,
        var tv_boxType: TextView,
        var tv_position: TextView,
        var tv_storageNo: TextView
    )

    fun setList(list: ArrayList<ResultGetBoxListByPosCode.DataBean.BoxListBean>) {
        mList = list
    }
}