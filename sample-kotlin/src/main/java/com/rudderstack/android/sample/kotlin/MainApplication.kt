package com.rudderstack.android.sample.kotlin

import android.app.Application
import com.moengage.core.Logger
import com.moengage.core.MoEngage
import com.rudderlabs.android.sample.kotlin.R
import com.rudderstack.android.integrations.moengage.MoengageIntegrationFactory
import com.rudderstack.android.sdk.core.RudderClient
import com.rudderstack.android.sdk.core.RudderConfig
import com.rudderstack.android.sdk.core.RudderLogger

class MainApplication : Application() {
    companion object {
        private const val WRITE_KEY = "1joZmcJI0NbIqHVfDjndCnikjnx"
        private const val DATA_PLANE_URL = "http://localhost:8080"
        private const val CONTROL_PLANE_URL = "https://api.dev.rudderlabs.com"

        lateinit var rudderClient: RudderClient
    }

    override fun onCreate() {
        super.onCreate()
        // initializing Rudder SDK
        rudderClient = RudderClient.getInstance(
            this,
            WRITE_KEY,
            RudderConfig.Builder()
                .withDataPlaneUrl(DATA_PLANE_URL)
                .withControlPlaneUrl(CONTROL_PLANE_URL)
                .withFactory(MoengageIntegrationFactory.FACTORY)
                .withLogLevel(RudderLogger.RudderLogLevel.VERBOSE)
                .build()
        )

        // initializing MoEngage SDK
        val moEngage = MoEngage.Builder(this, "W6HWD4X2AR1VD37L9F0Z4OT0")
            .setLogLevel(Logger.VERBOSE)
            .redirectDataToRegion(MoEngage.DATA_REGION.REGION_DEFAULT)
            .setNotificationLargeIcon(R.drawable.ic_launcher_background)
            .setNotificationSmallIcon(R.drawable.ic_launcher_background)
            .build()
        MoEngage.initialise(moEngage)
    }
}