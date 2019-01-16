## PilgrimSDK Kit Integration

This repository contains the [Pilgrim SDK](https://developer.foursquare.com/docs/pilgrim-sdk/quickstart#android) integration for [mParticle Android SDK](https://github.com/mParticle/mparticle-android-sdk).

## Example App
This repository contains an [Example App](https://github.com/foursquare/mparticle-android-integration-example/tree/feature/pilgrim-sdk-integration/example) showing how to implement mParticle and PilgrimSDK

### Adding the integration

1. Add the kit dependency to your app's build.gradle:
    [See a full build.gradle example here](https://github.com/foursquare/mparticle-android-integration-example/blob/feature/pilgrim-sdk-integration/example/build.gradle)

    ```groovy
    dependencies {
        api 'com.mparticle:android-pilgrimsdk-kit:5+'
    }
    ```


2. Follow the mParticle Android SDK [quick-start](https://github.com/mParticle/mparticle-android-sdk), then rebuild and launch your app, and verify that you see `"PilgrimSdkKit detected"` in the output of `adb logcat`.

3. Reference mParticle's integration docs below to enable the integration.

### Documentation

[Pilgrim SDK](https://developer.foursquare.com/docs/pilgrim-sdk/quickstart#android)

### License

[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
