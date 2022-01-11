# Welcome!

This repository contains a collection of Kit example to aid in the development of new kits.

## Developing Kits

#### Self Managed vs mParticle Managed Kits

Kits can either be "self managed" or "mParticle managed" depending on whether you would prefer that your organization hosts the Kit repository and manages releases or that mParticle hosts the kits in mParticle-integrations and manages release internally. 

The only difference this will make in terms of your kit implementation is:

##### For mParticle Managed Kits *only*
- you must include the `com.mparticle.kits` plugin in your root-level `build.gradle` file, within the `buildscript` block, like:
```groovy
    buildscript {
    if (!project.hasProperty('version') || project.version.equals('unspecified')) {
        project.version = '+'
    }

    repositories {
        mavenCentral()
        ...
    }
    dependencies {
        classpath 'com.mparticle:android-kit-plugin:' + project.version
    }
}
```

##### For Self Managed Kits *only*
- you must include a dependency for the mParticle Kit Base package in your kit's app-level `build.gradle` file, like: 
```groovy
    ...
    api 'com.mparticle:android-kit-base:X.X.X'
```

### Working on an existing kit

1) open an Android application
    
    - to create a new application: Android Studio -> File -> New -> New Project

2) add the Kit as a module (a OR b)
    
    a) If you want to work off an existing local copy of the Kit
        
        - File -> New -> New Module -> Import Gradle Project -> select Kit from file picker
    
    b) Clone the Kit from a remote repository
    
    - run `git clone {SSH-URL}` 
    
    - in settings.gradle add line `include :{CLONED-DIRECTORY-NAME}`
     
    > example `include ':mparticle-android-integration-adjust'` OR `include ':app', ':mparticle-android-integration-adjust'`

        
3) add a dependency in the application's `build.gradle` file (ex: `app/build.gradle`)
    
    - ```
      dependencies {
            implementation project(":{CLONED-DIRECTORY-NAME}")
            ...
      }
      ```

4) in your application module initialize MParticle as normal
      
### Working on a new kit
1) open an Android application
    
    - to create a new application: Android Studio -> File -> New -> New Project

2) decide if this is going to be a first-party (hosted and deployed by MParticle) or third-party kit
    a) **(first-party)** clone the `example-kits` repository
        
        - run `git clone git@github.com:mparticle-integrations/mparticle-android-integration-example.git`
        
        - delete the git repository: `cd  mparticle-android-integration-appboy; rm -rf .git; cd ..`
    
    b) **(third-party)** create a new Android Library module 
        
        - File -> New -> New Module -> Android Library
3) add a dependency in the application's `build.gradle` file (ex: `app/build.gradle`)
    
    - ```
      dependencies {
            implementation project(":{KIT-DIRECTORY-NAME}")
            ...
      }
      ```
4) in your application module initialize MParticle with a `KitOptions` instance rerferencing your kit:
    
    - ```kotlin
      val options = MParticleOptions.builder(context)
          .configurations(
              KitOptions()
                  .addKit({KIT-ID}, {KIT-CLASS})
          )
          ...
          .build()
      MParticle.start(options)    
    ```
   
   - {KIT-CLASS} will be your KitIntegration implementation's class reference, i.e `AdjustKit::class.java` 
