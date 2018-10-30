## FollowAnalytics Kit Integration

This repository contains the [FollowAnalytics](https://www.followanalytics.com/) integration for the [mParticle Android SDK](https://github.com/mParticle/mparticle-android-sdk).

### Adding the integration

1. Add the kit dependency to your app's build.gradle:

    ```groovy
    dependencies {
        compile 'com.mparticle:android-followanalytics-kit:5+'
    }
    ```
2. Follow the mParticle Android SDK [quick-start](https://github.com/mParticle/mparticle-android-sdk), then rebuild and launch your app, and verify that you see `"FollowAnalytics detected"` in the output of `adb logcat`.
3. Reference mParticle's integration docs below to enable the integration.
4. Refer to FollowAnalytics's integration doc below for more features.
5. Check out the example app below.

### Documentation

[FollowAnalytics integration](http://docs.mparticle.com/?java#followanalytics)

### Example App

[FollowAnalytics Android mParticle Example](https://github.com/followanalytics/mparticle-android-integration-demo)

### License

[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)