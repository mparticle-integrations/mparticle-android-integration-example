package com.mparticle.kits;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import com.appsee.Appsee;
import com.mparticle.MPEvent;
import com.mparticle.MParticle;
import com.mparticle.identity.MParticleUser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class AppseeKit extends KitIntegration implements KitIntegration.EventListener, KitIntegration.IdentityListener {

    @Override
    protected List<ReportingMessage> onKitCreate(final Map<String, String> settings, Context context) {

        if (!settings.containsKey("apiKey")) {
            throw new RuntimeException("Error: Appsee Kit couldn't start since apiKey is missing");
        } else {
            Appsee.setSkipStartValidation(true);
            Appsee.start(settings.get("apiKey"));
        }

        return null;
    }


    @Override
    public String getName() {
        return "Appsee";
    }


    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        Appsee.setOptOutStatus(optedOut);

        List<ReportingMessage> messageList = new LinkedList<>();
        messageList.add(
                new ReportingMessage(this, ReportingMessage.MessageType.OPT_OUT, System.currentTimeMillis(), null)
                        .setOptOut(optedOut)
        );
        return messageList;
    }

    @Override
    public List<ReportingMessage> leaveBreadcrumb(String s) {
        return null;
    }

    @Override
    public List<ReportingMessage> logError(String s, Map<String, String> map) {
        return null;
    }

    @Override
    public List<ReportingMessage> logException(Exception e, Map<String, String> map, String s) {
        return null;
    }

    @Override
    public List<ReportingMessage> logEvent(MPEvent mpEvent) {
        Map<String, Object> eventProps = new HashMap<>();
        eventProps.put("type", mpEvent.getEventType().name());

        String category = mpEvent.getCategory();
        if (category != null && !category.isEmpty()) {
            eventProps.put("category", category);
        }

        Map<String, String> info = mpEvent.getInfo();
        if (info != null) {

            // Remove the category (if exists) since we already fetched it from the property of the MPEvent
            if (info.containsKey("$Category")) {
                info.remove("$Category");
            }

            if (info.size() > 0) {
                eventProps.putAll(info);
            }
        }

        Appsee.addEvent(mpEvent.getEventName(), eventProps);

        List<ReportingMessage> messageList = new LinkedList<>();
        messageList.add(ReportingMessage.fromEvent(this, mpEvent));
        return messageList;
    }

    @Override
    public List<ReportingMessage> logScreen(String s, Map<String, String> map) {
        Appsee.startScreen(s);

        List<ReportingMessage> messageList = new LinkedList<>();
        messageList.add(
                new ReportingMessage(this, ReportingMessage.MessageType.SCREEN_VIEW, System.currentTimeMillis(), null)
                        .setScreenName(s));
        return messageList;
    }

    @Override
    public void onIdentifyCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {

    }

    @Override
    public void onLoginCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {

    }

    @Override
    public void onLogoutCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {

    }

    @Override
    public void onModifyCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {

    }

    @Override
    public void onUserIdentified(MParticleUser mParticleUser) {
        Map<MParticle.IdentityType, String> userIdentities = mParticleUser.getUserIdentities();
        if (userIdentities.containsKey(MParticle.IdentityType.CustomerId)) {
            Appsee.setUserId(userIdentities.get(MParticle.IdentityType.CustomerId));
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void setLocation(Location location) {

        // Since getVerticalAccuracyMeters is supported only for API Level 26+, for previous versions we set the
        // vertical accuracy to be 0
        float verticalAccuracy = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            verticalAccuracy = location.getVerticalAccuracyMeters();
        }

        Appsee.setLocation(location.getLatitude(), location.getLongitude(), location.getAccuracy(), verticalAccuracy);
    }
}