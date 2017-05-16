package com.mparticle.kits;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.mparticle.DeepLinkResult;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * This is an mParticle kit, used to extend the functionality of mParticle SDK. Most Kits are wrappers/adapters
 * to a 3rd party SDK, primarily used to map analogous public mParticle APIs onto a 3rd-party API/platform.
 */
public class IterableKit extends KitIntegration implements KitIntegration.ActivityListener {

    private String deeplinkUrl;
    private Set<String> previousLinks = new HashSet<String>();

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
        return null;
    }

    @Override
    public void checkForDeepLink() {
        if(!KitUtils.isEmpty(deeplinkUrl)) {
            DeepLinkResult deepLinkResult = new DeepLinkResult().setLink(deeplinkUrl);
            deepLinkResult.setServiceProviderId(getConfiguration().getKitId());
            getKitManager().onResult(deepLinkResult);
        }
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
        String currentLink = activity.getIntent().getDataString();

        if (currentLink != null && !currentLink.isEmpty() && !previousLinks.contains(currentLink)){
            previousLinks.add(currentLink);
            IterableHelper.IterableActionHandler clickCallback = new IterableHelper.IterableActionHandler() {
                @Override
                public void execute(String result) {
                    deeplinkUrl = result;
                }
            };

            IterableApi.getAndTrackDeeplink(currentLink, clickCallback);
        }

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