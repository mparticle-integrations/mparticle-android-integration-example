package com.example.myapplication

import android.app.Application
import com.example.batching_kit.BatchingKit
import com.mparticle.MParticle
import com.mparticle.MParticleOptions
import com.mparticle.kits.KitOptions

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        val options = MParticleOptions.builder(this)
            .credentials("{API-KEY}", "{API-SECRET")
            .configuration(KitOptions()
                .addKit(1001, BatchingKit::class.java))
            .build()
        MParticle.start(options)
    }
}