package com.zk.cabinet.constant

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object SelfComm {

    const val PROJECT_PACKAGE_NAME = "com.zk.cabinet"
    const val PROJECT__ACTIVITY = "com.zk.cabinet.activity"
    const val FILED_DOWNLOADER_PROJECT_PACKAGE__NAME = "com.zk.cabinet:filedownloader"
    const val GUIDE_ACTIVITY = "com.zk.cabinet.activity.GuideActivity"
    val ONLINE_DEVICE = Collections.synchronizedList(ArrayList<String>())

    val FILE_STATUS = HashMap<Int, String>()
    const val FILE_STATUS_IN_THE_CABINET = 2
    const val FILE_STATUS_LEAVE_THE_CABINET = 3

    const val COUNT_DOWN_REQUEST_CODE = 0x0101
    const val COUNT_DOWN_RESULT_CODE = 0x0102
    const val UPDATE_CABINET_IN_ORDER = 0x0104

    // 设备名称
    val DEVICE_NAME = HashMap<Int, String>()
    // 主页功能选项类型
    val FUNCTION_TYPE = HashMap<Int, Int>()

    // 入库类型
    val OPERATING_TYPE = HashMap<String, String>()

    // 出库类型
    val OUT_OPERATING_TYPE = HashMap<String, String>()

    init {
        DEVICE_NAME[0] = "未选择"
        DEVICE_NAME[1] = "档案组架"
        DEVICE_NAME[2] = "档案组柜"
        DEVICE_NAME[3] = "档案单柜"
        DEVICE_NAME[4] = "一体机"
        DEVICE_NAME[5] = "PDA"
        DEVICE_NAME[6] = "通道门"

        FUNCTION_TYPE[0] = 0 // "登出"
        FUNCTION_TYPE[1] = 1 // "PDA-入库"
        FUNCTION_TYPE[2] = 2 // "PDA-移库"
        FUNCTION_TYPE[3] = 3 // "PDA-盘库"
        FUNCTION_TYPE[4] = 4 // "一体机-借阅"
        FUNCTION_TYPE[5] = 5 // "一体机-归还"
        FUNCTION_TYPE[6] = 6 // "档案单柜-借阅"
        FUNCTION_TYPE[7] = 7 // "档案单柜-归还"
        FUNCTION_TYPE[8] = 8 // "档案组柜-借阅"
        FUNCTION_TYPE[9] = 9 // "档案组柜-归还"
        FUNCTION_TYPE[10] = 10 // "档案单柜/档案组柜-预览(盘库)"

        OPERATING_TYPE["01"] = "正常入库"
        OPERATING_TYPE["02"] = "借用归还"
        OPERATING_TYPE["03"] = "置换入库"

        OUT_OPERATING_TYPE["04"] = "正常出库"
        OUT_OPERATING_TYPE["05"] = "置换出库"
        OUT_OPERATING_TYPE["06"] = "提前出库"
        OUT_OPERATING_TYPE["07"] = "借用出库"

        FILE_STATUS[FILE_STATUS_LEAVE_THE_CABINET] = "离柜"
        FILE_STATUS[FILE_STATUS_IN_THE_CABINET] = "在柜"
    }
}