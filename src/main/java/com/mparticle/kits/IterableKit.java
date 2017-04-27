package com.mparticle.kits;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.mparticle.DeepLinkResult;

import java.util.List;
import java.util.Map;

/**
 *
 * This is an mParticle kit, used to extend the functionality of mParticle SDK. Most Kits are wrappers/adapters
 * to a 3rd party SDK, primarily used to map analogous public mParticle APIs onto a 3rd-party API/platform.
 */
public class IterableKit extends KitIntegration implements KitIntegration.ActivityListener {

   String deeplinkUrl;

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        return null;
    }

    @Override
    public String getName() {
        return "Iterable";
    }

    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        ReportingMessage optOutMessage = new ReportingMessage(this, ReportingMessage.MessageType.OPT_OUT, System.currentTimeMillis(), null);
        return null;
    }

    @Override
    public void checkForDeepLink() {
        IterableHelper.IterableActionHandler clickCallback = new IterableHelper.IterableActionHandler(){
            @Override
            public void execute(String result) {
                DeepLinkResult deepLinkResult = new DeepLinkResult().setLink(result);
                getKitManager().onResult(deepLinkResult);
            }
        };

        IterableAPI.getAndTrackDeeplink(deeplinkUrl, clickCallback);
        deeplinkUrl = null;
    }

    @Override
    public List<ReportingMessage> onActivityCreated(Activity activity, Bundle bundle) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityStarted(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityResumed(Activity activity) {
        deeplinkUrl = activity.getIntent().getDataString();
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityPaused(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityStopped(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityDestroyed(Activity activity) {
        return null;
    }
}