# What is RudderStack?

[RudderStack](https://rudderstack.com/) is a **customer data pipeline** tool for collecting, routing and processing data from your websites, apps, cloud tools, and data warehouse.

With RudderStack, you can build customer data pipelines that connect your whole customer data stack and then make them smarter by triggering enrichment and activation in customer tools based on analysis in your data warehouse. Its easy-to-use SDKs and event source integrations, Cloud Extract integrations, transformations, and expansive library of destination and warehouse integrations makes building customer data pipelines for both event streaming and cloud-to-warehouse ELT simple.

| Try **RudderStack Cloud Free** - a no time limit, no credit card required, completely free tier of [RudderStack Cloud](https://resources.rudderstack.com/rudderstack-cloud). Click [here](https://app.rudderlabs.com/signup?type=freetrial) to start building a smarter customer data pipeline today, with RudderStack Cloud Free. |
|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

Questions? Please join our [Slack channel](https://resources.rudderstack.com/join-rudderstack-slack) or read about us on [Product Hunt](https://www.producthunt.com/posts/rudderstack).

## Integrating MoEngage with RudderStack Android SDK

1. Add [MoEngage](https://www.moengage.com) as a destination in the [Dashboard](https://app.rudderstack.com/) and define `apiKey`.

2. Open your project level ```build.gradle``` file, and add the following lines of code:

```groovy
// For Push Notification
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.google.gms:google-services:4.3.14'
    }
}
```

3. Also, add the below plugin in your app level ```build.gradle``` file:

```groovy
// For Push Notification
apply plugin: 'com.google.gms.google-services'
```

3.Add the following under ```dependencies``` section:

```groovy
// RudderStack Android and MoEngage SDKs
implementation 'com.rudderstack.android.sdk:core:[1.0,2.0)'
implementation 'com.rudderstack.android.integration:moengage:2.0.0'

// For MoEngage core SDK initialisation
implementation("com.moengage:moe-android-sdk:12.5.04")
```

4. Initialize the RudderStack SDK in the Application class's onCreate() method, as shown:

```kotlin
val rudderClient: RudderClient = RudderClient.getInstance(
    this,
    "<WRITE_KEY>",
    RudderConfig.Builder()
        .withDataPlaneUrl("<DATA_PLANE_URL>")
        .withFactory(MoengageIntegrationFactory.FACTORY)
        .build()
)
```

5. Initialize the MoEngage SDK in the Application class's onCreate() method:

```kotlin
// initializing MoEngage SDK and "XXXXXXXXXXX" is the APP ID from the dashboard.
val moEngage = MoEngage.Builder(this, "XXXXXXXXXXX")
    .configureLogs(LogConfig(LogLevel.VERBOSE, false))
    .build()
MoEngage.initialiseDefaultInstance(moEngage)
```

## Send Events

Follow the steps from the [RudderStack Android SDK](https://github.com/rudderlabs/rudder-sdk-android).

## Contact Us

If you come across any issues while configuring or using this integration, feel free to start a conversation on our [Slack](https://resources.rudderstack.com/join-rudderstack-slack) channel. We will be happy to help you.
