package com.rudderstack.android.sample.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rudderlabs.android.sample.kotlin.R
import com.rudderstack.android.sdk.core.RudderProperty
import com.rudderstack.android.sdk.core.RudderTraits
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MainApplication.rudderClient.screen(localClassName)

        val property = RudderProperty()
        property.put("key_1", "val_1")
        property.put("key_2", "val_2")
        MainApplication.rudderClient.track("Event with Property", property)
        MainApplication.rudderClient.track("Event without Property")
        MainApplication.rudderClient.identify(
            "sample_user_id",
            RudderTraits()
                .putEmail("sample@gmail.com")
                .putFirstName("Foo")
                .putLastName("Bar")
                .putName("Foo Bar"), null
        )
    }
}
