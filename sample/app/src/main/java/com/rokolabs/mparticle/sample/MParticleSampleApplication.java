package com.rokolabs.mparticle.sample;

import android.app.Application;

import com.mparticle.DeepLinkError;
import com.mparticle.DeepLinkListener;
import com.mparticle.DeepLinkResult;
import com.mparticle.MParticle;

/**
 * Created by sobolev on 7/18/17.
 */

public class MParticleSampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MParticle.start(this);
        MParticle.getInstance().checkForDeepLink(new DeepLinkListener() {
            @Override
            public void onResult(DeepLinkResult deepLinkResult) {
                //TODO
                // implement logic for deeplinking to this Activity when user already has app installed
            }

            @Override
            public void onError(DeepLinkError deepLinkError) {
                //TODO
                // implement error handling
            }
        });
    }
}
