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

        // Screen Call
        MainApplication.rudderClient.screen(localClassName)
        // Track Call with Properties
        val property = RudderProperty()
        property.put("key_1", "val_1")
        property.put("duration", 28)
        property.put("allowed",true)
        MainApplication.rudderClient.track("Event with Property", property)
        // Track Call without Properties
        MainApplication.rudderClient.track("Event without Property")
        // Identify Call
        MainApplication.rudderClient.identify(
            "sample_user_id",
            RudderTraits()
                .putEmail("sample@gmail.com")
                .putFirstName("Foo")
                .putLastName("Bar")
                .putName("Foo Bar"), null
        )
        // Alias Call
        MainApplication.rudderClient.alias("new_sample_user_id")

    }
}
