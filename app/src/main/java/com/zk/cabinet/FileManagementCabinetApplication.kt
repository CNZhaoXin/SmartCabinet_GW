package com.zk.cabinet

import android.app.Application
import com.zk.cabinet.utils.SharedPreferencesUtil

class FileManagementCabinetApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPreferencesUtil.instance.init(this)
    }
}