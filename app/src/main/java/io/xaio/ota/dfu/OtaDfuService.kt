package io.xaio.ota.dfu

import io.xaio.ota.BuildConfig
import io.xaio.ota.MainActivity
import no.nordicsemi.android.dfu.DfuBaseService

class OtaDfuService : DfuBaseService() {
    override fun getNotificationTarget() = MainActivity::class.java

    override fun isDebug(): Boolean = BuildConfig.DEBUG
}

