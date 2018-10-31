## Responsys Kit Integration



This repository contains the [Responsys](https://docs.oracle.com/cloud/latest/marketingcs_gs/OMCFB/) integration for the [mParticle Android SDK](https://github.com/mParticle/mparticle-android-sdk).

### Adding the integration
1. The Responsys Kit is available via Maven Central. For adding the Responsys SDK, refer to  the [Step-by-Step guide](https://docs.oracle.com/cloud/latest/marketingcs_gs/OMCFB/android/step-by-step/) and follow steps [1.1] through [3.4] and step [4.5].

2.  Follow **Step [2.2]** to download your app's **pushio_config.json** file - this does not need to be added to your app, but you will need its contents to create the Responsys Connection in your mParticle workspace. 

3. Follow **Step [4.1]** to ensure your AndroidManifest.xml permits your app to register for push. Follow [mParticle's push registration guide](https://docs.mparticle.com/developers/sdk/android/push-notifications#register-for-push-notifications) to ensure you're properly registering for push with mParticle.

4. Add the kit dependency to your app's build.gradle:
    ```
    dependencies {
    		compile 'com.mparticle:android-responsys-kit:5+'
    }
    ```
5. Reference mParticle's integration docs below to enable the Responsys Connection.
6. Once enabled, rebuild and launch your app, and verify that you see `"Responsys Kit detected"` in the output of `adb logcat`.
7. To enable additional features of the Responsys SDK like In-App Messaging, Notification Preferences etc., follow the respective guides available [here](https://docs.oracle.com/cloud/latest/marketingcs_gs/OMCFB/android/).


### Documentation

[Responsys integration](http://docs.mparticle.com/?java#Responsys)

### License

[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)