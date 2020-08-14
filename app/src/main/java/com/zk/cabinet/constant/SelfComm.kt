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

    val OPERATING_TYPE = HashMap<Int, String>()

    init {
        FILE_STATUS[FILE_STATUS_LEAVE_THE_CABINET] = "离柜"
        FILE_STATUS[FILE_STATUS_IN_THE_CABINET] = "在柜"

        OPERATING_TYPE[1] = "正常入库"
        OPERATING_TYPE[2] = "借用归还"
        OPERATING_TYPE[3] = "置换入库"
        OPERATING_TYPE[4] = "正常出库"
        OPERATING_TYPE[5] = "置换出库"
        OPERATING_TYPE[6] = "提前出库"
        OPERATING_TYPE[7] = "借用出库"

    }
}