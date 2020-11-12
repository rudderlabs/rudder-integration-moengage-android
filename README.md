# What is Rudder?

**Short answer:**
Rudder is an open-source Segment alternative written in Go, built for the enterprise.

**Long answer:**
Rudder is a platform for collecting, storing and routing customer event data to dozens of tools. Rudder is open-source, can run in your cloud environment (AWS, GCP, Azure or even your data-centre) and provides a powerful transformation framework to process your event data on the fly.

Released under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

## Getting Started with MoEngage Integration of Android SDK
1. Add [MoEngage](https://www.moengage.com) as a destination in the [Dashboard](https://app.rudderstack.com/) and define `apiKey`.

2. Add these lines to your ```app/build.gradle```
```
repositories {
    maven { url "https://dl.bintray.com/rudderstack/rudderstack" }
}
```
3. Add the dependency under ```dependencies```
```
// Rudder core sdk and moengage extension
implementation 'com.rudderstack.android.sdk:core:1.+'
implementation 'com.rudderstack.android.integration:moengage:1.0.0-pre.1'

// moengage core sdk
api 'com.moengage:moe-android-sdk:10.5.00'

// gson
implementation 'com.google.code.gson:gson:2.8.6'
```

## Initialize ```RudderClient```
```
val rudderClient: RudderClient = RudderClient.getInstance(
    this,
    <WRITE_KEY>,
    RudderConfig.Builder()
        .withDataPlaneUrl(<DATA_PLANE_URL>)
        .withFactory(MoengageIntegrationFactory.FACTORY)
        .build()
)
```

## Send Events
Follow the steps from [Rudder Android SDK](https://github.com/rudderlabs/rudder-sdk-android)

## Contact Us
If you come across any issues while configuring or using RudderStack, please feel free to [contact us](https://rudderstack.com/contact/) or start a conversation on our [Discord](https://discordapp.com/invite/xNEdEGw) channel. We will be happy to help you.
