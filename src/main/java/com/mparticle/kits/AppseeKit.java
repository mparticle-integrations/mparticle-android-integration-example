package com.mparticle.kits;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import com.appsee.Appsee;
import com.mparticle.MPEvent;
import com.mparticle.MParticle;
import com.mparticle.consent.ConsentState;
import com.mparticle.identity.MParticleUser;
import com.mparticle.internal.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * This is an mParticle kit, used to extend the functionality of mParticle SDK. Most Kits are wrappers/adapters
 * to a 3rd party SDK, primarily used to map analogous public mParticle APIs onto a 3rd-party API/platform.
 *
 *
 * Follow the steps below to implement your kit:
 *
 *  - Edit ./build.gradle to add any necessary dependencies, such as your company's SDK
 *  - Rename this file/class, using your company name as the prefix, ie "AcmeKit"
 *  - View the javadocs to learn more about the KitIntegration class as well as the interfaces it defines.
 *  - Choose the additional interfaces that you need and have this class implement them,
 *    ie 'AcmeKit extends KitIntegration implements KitIntegration.PushListener'
 *
 *  In addition to this file, you also will need to edit:
 *  - ./build.gradle (as explained above)
 *  - ./README.md
 *  - ./src/main/AndroidManifest.xml
 *  - ./consumer-proguard.pro
 */
public class AppseeKit extends KitIntegration implements KitIntegration.EventListener, KitIntegration.IdentityListener, KitIntegration.UserAttributeListener {

    @Override
    protected List<ReportingMessage> onKitCreate(final Map<String, String> settings, Context context) {

        if (!settings.containsKey("apiKey")) {
            throw new RuntimeException("Error: Appsee Kit couldn't start since apiKey is missing");
        } else {

            // Run Appsee.start() only on the main thread
            if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                Appsee.setSkipStartValidation(true);
                Appsee.start(settings.get("apiKey"));
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Appsee.setSkipStartValidation(true);
                        Appsee.start(settings.get("apiKey"));
                    }
                });
            }
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
        for (MParticle.IdentityType identityType : userIdentities.keySet()) {
            if (identityType == MParticle.IdentityType.CustomerId) {
                Appsee.setUserId(userIdentities.get(identityType));
                break;
            }
        }
    }

    @Override
    public void onIncrementUserAttribute(String s, String s1, FilteredMParticleUser filteredMParticleUser) {
    }

    @Override
    public void onRemoveUserAttribute(String s, FilteredMParticleUser filteredMParticleUser) {
    }

    @Override
    public void onSetUserAttribute(String s, Object o, FilteredMParticleUser filteredMParticleUser) {
    }

    @Override
    public void onSetUserTag(String s, FilteredMParticleUser filteredMParticleUser) {
    }

    @Override
    public void onSetUserAttributeList(String s, List<String> list, FilteredMParticleUser filteredMParticleUser) {
    }

    @Override
    public void onSetAllUserAttributes(Map<String, String> map, Map<String, List<String>> map1, FilteredMParticleUser filteredMParticleUser) {
    }

    @Override
    public boolean supportsAttributeLists() {
        return false;
    }

    @Override
    public void onConsentStateUpdated(ConsentState consentState, ConsentState consentState1, FilteredMParticleUser filteredMParticleUser) {

    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void setLocation(Location location) {

        // Since getVerticalAccuracyMeters is supported only for API Level 26+, for previous versions we set the
        // vertical accuracy to be 0
        Appsee.setLocation(location.getLatitude(), location.getLongitude(), location.getAccuracy(),
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ? location.getVerticalAccuracyMeters() : 0);
    }
}