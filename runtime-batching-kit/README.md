## Runtime Kit Registration

This repository contains the an example Kit that is registered at runtime. Previously kits needed to be
registered within the android-core module, requiring a code change, but the `MParticleOptions.configurations` property
makes it possible to register "unknown" kits via a `KitOptions` instance at runtime. Each kit still needs an "id" for the server to indicate
the kit should be initialized, but this value can be configured without the need for any code change in the SDK

example:

```
MParticleOptions.builder(context)
    .configuration(KitOptions()
        .addKit(1001, BatchingKit::class.java))
    .build()
MParticle.start(options)
```

## BatchListener (**beta**)

This sample shows a kit implementing the `KitIntegration.BatchListener` interface. This will register the kit
as a receiver for Batch instances, in the form of raw `JSONObject`s (currently, this will change as we leave beta) but
implementing the `logBatch(JSONObject` method. Batches are received after they are successfully uploaded
the the MParticle server, so each kit will receive a Batch exactly 1 time.