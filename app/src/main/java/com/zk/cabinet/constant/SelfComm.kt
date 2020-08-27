package com.zk.cabinet.constant

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object SelfComm {
    const val CONFIG_MANAGER_DEFAULT_ACCOUNT = "Hikchina"
    const val CONFIG_MANAGER_DEFAULT_PASSWORD = "123"

    const val PROJECT_PACKAGE_NAME = "com.zk.cabinet"
    const val PROJECT__ACTIVITY = "com.zk.cabinet.activity"
    const val FILED_DOWNLOADER_PROJECT_PACKAGE__NAME = "com.zk.cabinet:filedownloader"
    const val GUIDE_ACTIVITY = "com.zk.cabinet.activity.GuideActivity"

    val FILE_STATUS = HashMap<Int, String>()
    const val FILE_STATUS_IN_THE_CABINET = 2
    const val FILE_STATUS_LEAVE_THE_CABINET = 3

    const val COUNT_DOWN_REQUEST_CODE = 0x0101
    const val COUNT_DOWN_RESULT_CODE = 0x0102
    const val UPDATE_NUMBER_OF_FILE_IN_BOX = 0x0103
    const val UPDATE_CABINET_IN_ORDER = 0x0104

    val ONLINE_DEVICE = Collections.synchronizedList(ArrayList<String>())

    const val NET_SERVICE_CONNECT = 0x0202

    // 自动盘库
    const val NET_SERVICE_INVENTORY = 0x0203


    // 入库类型
    val OPERATING_TYPE = HashMap<String, String>()

    // 出库类型
    val OUT_OPERATING_TYPE = HashMap<String, String>()

    // 权证类别
    val WARRANT_CATE = HashMap<String, String>()

    // 权证类型
    val WARRANT_TYPE = HashMap<String, String>()

    init {
        FILE_STATUS[FILE_STATUS_LEAVE_THE_CABINET] = "离柜"
        FILE_STATUS[FILE_STATUS_IN_THE_CABINET] = "在柜"

        OPERATING_TYPE["01"] = "正常入库"
        OPERATING_TYPE["02"] = "借用归还"
        OPERATING_TYPE["03"] = "置换入库"

        OUT_OPERATING_TYPE["04"] = "正常出库"
        OUT_OPERATING_TYPE["05"] = "置换出库"
        OUT_OPERATING_TYPE["06"] = "提前出库"
        OUT_OPERATING_TYPE["07"] = "借用出库"

        WARRANT_CATE["01"] = "权利证明"
        WARRANT_CATE["02"] = "登记证明"

        WARRANT_TYPE["01"] = "存单"
        WARRANT_TYPE["02"] = "国债凭证"
        WARRANT_TYPE["03"] = "票据"
        WARRANT_TYPE["04"] = "保单正本、保险公司出具相关证明"
        WARRANT_TYPE["05"] = "理财协议书或理财产品合同"
        WARRANT_TYPE["06"] = "房屋产权证或不动产权证"
        WARRANT_TYPE["07"] = "土地使用权证或不动产权证"
        WARRANT_TYPE["08"] = "网签合同"
        WARRANT_TYPE["09"] = "房产收件单"
        WARRANT_TYPE["10"] = "批准文件或收费许可证"
        WARRANT_TYPE["11"] = "已经背书“质押”字样的仓单/提单"
        WARRANT_TYPE["12"] = "机动车登记证书"
        WARRANT_TYPE["13"] = "质押财产清单"
        WARRANT_TYPE["14"] = "其他原始凭证"
        WARRANT_TYPE["15"] = "贵金属出质证明书或托管证明"
        WARRANT_TYPE["16"] = "证券质押登记证明书"
        WARRANT_TYPE["17"] = "股权质押登记证明书（非上市）"
        WARRANT_TYPE["18"] = "房屋他项权利证书或不动产权登记证"
        WARRANT_TYPE["19"] = "土地他项权利证书"
        WARRANT_TYPE["20"] = "在建工程抵押登记证明"
        WARRANT_TYPE["21"] = "机器设备抵押登记证书"
        WARRANT_TYPE["22"] = "船舶抵押权登记证书"
        WARRANT_TYPE["23"] = "森林资源资产抵押登记证"
        WARRANT_TYPE["24"] = "海域使用权登记证明"
        WARRANT_TYPE["25"] = "民用航空器抵押权登记证书"
        WARRANT_TYPE["26"] = "商标专用权质押登记证"
        WARRANT_TYPE["27"] = "专利权质押合同登记通知书"
        WARRANT_TYPE["28"] = "著作权质押合同登记证"
        WARRANT_TYPE["29"] = "其他抵押或质押登记证"
        WARRANT_TYPE["30"] = "存单止付通知书"
        WARRANT_TYPE["31"] = "预告登记证"

    }
}