package com.zk.cabinet.constant

object SelfComm {
    const val CONFIG_MANAGER_DEFAULT_ACCOUNT = "Hikchina"
    const val CONFIG_MANAGER_DEFAULT_PASSWORD = "123"

    const val PROJECT_PACKAGE_NAME = "com.hik.cabinet"
    const val PROJECT__ACTIVITY = "com.hik.cabinet.activity"
    const val FILED_DOWNLOADER_PROJECT_PACKAGE__NAME = "com.hik.cabinet:filedownloader"
    const val GUIDE_ACTIVITY = "com.hik.cabinet.activity.GuideActivity"
    const val PERSONNEL_MANAGEMENT_ACTIVITY = "com.hik.cabinet.activity.PersonnelManagementActivity"
    const val BUSINESS_MESSENGER = "businessMessenger"
    const val COMMUNICATION_MESSENGER = "communicationMessenger"

    val FILE_STATUS = HashMap<Int, String>()
    const val FILE_STATUS_IN_THE_CABINET = 2
    const val FILE_STATUS_LEAVE_THE_CABINET = 3

    const val COUNT_DOWN_REQUEST_CODE = 0x0101
    const val COUNT_DOWN_RESULT_CODE = 0x0102
    const val UPDATE_NUMBER_OF_FILE_IN_BOX = 0x0103
    const val UPDATE_CABINET_IN_ORDER = 0x0104

    init {
        FILE_STATUS[FILE_STATUS_LEAVE_THE_CABINET] = "离柜"
        FILE_STATUS[FILE_STATUS_IN_THE_CABINET] = "在柜"
    }
}