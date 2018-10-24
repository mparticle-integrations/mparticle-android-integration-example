## Responsys Kit Integration



This repository contains the [Responsys](https://docs.oracle.com/cloud/latest/marketingcs_gs/OMCFB/) integration for the [mParticle Android SDK](https://github.com/mParticle/mparticle-android-sdk).

### Adding the integration

1. For adding the Responsys SDK, refer to  the [Step-by-Step guide](https://docs.oracle.com/cloud/latest/marketingcs_gs/OMCFB/android/step-by-step/) and follow steps [1.1] through [3.4] and step [4.5].
2. Add the kit dependency to your app's build.gradle:
    ```groovy
    dependencies {
        compile 'com.mparticle:android-responsys-kit:5+'
    }
    ```
3. Follow the mParticle Android SDK [quick-start](https://github.com/mParticle/mparticle-android-sdk), then rebuild and launch your app, and verify that you see `"Responsys Kit detected"` in the output of `adb logcat`.

4. Reference mParticle's integration docs below to enable the integration.
5. To enable additional features of the Responsys SDK like In-App Messaging, Notification Preferences etc., follow the respective guides available [here](https://docs.oracle.com/cloud/latest/marketingcs_gs/OMCFB/android/).


### Documentation

[Responsys integration](http://docs.mparticle.com/?java#Responsys)

### License

[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)  
