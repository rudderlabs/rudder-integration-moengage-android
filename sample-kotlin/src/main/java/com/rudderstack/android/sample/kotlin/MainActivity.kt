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

       /* MainApplication.rudderClient.screen(localClassName)

        val property = RudderProperty()
        property.put("key_1", "val_1")
        property.put("key_2", "val_2")
        val childProperty = RudderProperty()
        childProperty.put("key_c_1", "val_c_1")
        childProperty.put("key_c_2", "val_c_2")
        property.put("child_key", childProperty)
        MainApplication.rudderClient.track("challenge: applied points", property)
        MainApplication.rudderClient.track("article: viewed")
        MainApplication.rudderClient.identify(
            "4143",
            RudderTraits()
                .putEmail("example@gmail.com")
                .putFirstName("Foo")
                .putLastName("Bar")
                .putName("Desu"),
            null
        )*/
        val traits = RudderTraits()
        traits.putBirthday(Date())
        traits.putEmail("sai@123.com")
        traits.putFirstName("Desu")
        traits.putLastName("Sai")
        traits.putGender("m")
        traits.putPhone("8919969992")

        val address = RudderTraits.Address()
        address.putCity("Hyderabad")
        address.putCountry("India")
        traits.putAddress(address)

        traits.put("Pakka", true)
        traits.put("entha", 50)
        traits.put("ekkuva", 120.4f)
        traits.put("inkaekkuva", 1234L)
        traits.put("padam", "hello")
        traits.put("tedi", Date(System.currentTimeMillis()))

        MainApplication.rudderClient.identify("4065", traits, null)
        MainApplication.rudderClient.track(
            "Product Added",
            RudderProperty()
                .putValue("product_id", "product_001")
        )
        MainApplication.rudderClient.track("Product Purchased", RudderProperty().putValue("p_id","0025"))
        //MainApplication.rudderClient.track("account: created")
        //MainApplication.rudderClient.track("account: authenticated")
    }
}
