package com.rudderstack.android.sample.kotlin

import android.app.Application
import com.moengage.core.LogLevel
import com.moengage.core.MoEngage
import com.moengage.core.config.LogConfig
import com.rudderlabs.android.sample.kotlin.BuildConfig
import com.rudderstack.android.integrations.moengage.MoengageIntegrationFactory
import com.rudderstack.android.sdk.core.RudderClient
import com.rudderstack.android.sdk.core.RudderConfig
import com.rudderstack.android.sdk.core.RudderLogger

class MainApplication : Application() {
    companion object {
        lateinit var rudderClient: RudderClient
    }

    override fun onCreate() {
        super.onCreate()
        // initializing Rudder SDK
        rudderClient = RudderClient.getInstance(
            this,
            BuildConfig.WRITE_KEY,
            RudderConfig.Builder()
                .withDataPlaneUrl(BuildConfig.DATA_PLANE_URL)
                .withFactory(MoengageIntegrationFactory.FACTORY)
                .withLogLevel(RudderLogger.RudderLogLevel.NONE)
                .build()
        )

        // initializing MoEngage SDK and "XXXXXXXXXXX" is the APP ID from the dashboard.
        val moEngage = MoEngage.Builder(this, BuildConfig.MOENGAGE_APP_ID)
            .configureLogs(LogConfig(LogLevel.VERBOSE, false))
            .build()
        MoEngage.initialiseDefaultInstance(moEngage)
    }
}