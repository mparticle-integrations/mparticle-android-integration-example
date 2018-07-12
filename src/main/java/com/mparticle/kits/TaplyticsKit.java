package com.mparticle.kits;

import android.content.Context;

import java.util.List;
import java.util.Map;

import com.taplytics.sdk.SessionInfoRetrievedListener;
import com.taplytics.sdk.Taplytics;
import com.taplytics.sdk.TaplyticsExperimentsLoadedListener;

import org.json.JSONObject;

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


public class TaplyticsKit extends KitIntegration
        implements
        KitIntegration.AttributeListener,
        KitIntegration.EventListener,
        KitIntegration.CommerceListener,
        KitIntegration.ApplicationStateListener,
        KitIntegration.PushListener {

    /**
     * Option Keys
     */
    private static final String API_KEY = "api_key";
    private static final String LIVE_UPDATE = "liveUpdate";
    private static final String SHAKE_MENU = "shakeMenu";
    private static final String AGGRESSIVE = "aggressive";
    private static final String SESSION_MINUTES = "sessionMinutes";
    private static final String TURN_MENU = "turnMenu";
    private static final String DISABLE_BORDERS = "disableBorders";
    private static final String RETROFIT = "retrofit";

    /**
     * Event Keys
     */
    public static final String EventAppBackground = "appBackground";
    public static final String EventAppForeground = "appForeground";

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        /** TODO: Initialize your SDK here
         * This method is analogous to Application#onCreate, and will be called once per app execution.
         *
         * If for some reason you can't start your SDK (such as settings are not present), you *must* throw an Exception
         *
         * If you forward any events on startup that are analagous to any mParticle messages types, return them here
         * as ReportingMessage objects. Otherwise, return null.
         */

        startTaplytics(settings, context);

        return null;
    }

    /**
     * Report Messaging Helper methods
     */

    private ReportingMessage createReportMessage(String reportMessage) {
        return new ReportingMessage(this,
                                    reportMessage,
                                    System.currentTimeMillis(),
                                    null);
    }

    private List<ReportingMessage> createReportingMessages(ReportingMessage report) {
        return Collections.singletonList(report);
    }

    private List<ReportingMessage> createReportingMessages(String message) {
        return Collections.singletonList(createReportingMessages(message));
    }

    /**
     * Start Taplytics
     * @param settings
     * @param context
     */

    private void startTaplytics(Map<String, String> settings, Context context) {
        String apiKey = getAPIKey(settings);
        Map<String, Object> options = getOptions(settings);

        if (options != null) {
            Taplytics.startTaplytics(context, apiKey, options);
            return;
        }

        Taplytics.startTaplytics(context, apiKey);
    }

    private String getAPIKey(Map<String, String> settings) {
        final String apiKey = getSettings().get(API_KEY);
        if (TextUtils.isEmpty(apiKey)) {
            throw new IllegalArgumentException(API_KEY);
        }
        return apiKey;
    }

    /**
     * Get Taplytics options from settings
     * @param settings
     * @return
     */

    private Map<String, Object> getOptions(Map<String, String> settings) {
        Map<String, Object> options = new HashMap<>();

        addLiveUpdateOption(options, settings);
        addShakeMenuOption(options, settings);
        addAggressiveOption(options, settings);
        addSessionMinutesOption(options, settings);
        addTurnMenuOption(options, settings);
        addDisableBordersOption(options, settings);
        addRetrofitOption(options, settings);

        return options.isEmpty() ? null : options;
    }

    private void addLiveUpdateOption(Map<String, Object> options, Map<String, String> settings) {
        Boolean liveUpdate = Boolean.parseBoolean(settings.get(LIVE_UPDATE));
        options.set(liveUpdate.booleanValue());
    }

    private void addShakeMenuOption(Map<String, Object> options, Map<String, String> settings) {
        Boolean shakeMenu = Boolean.parseBoolean(settings.get(SHAKE_MENU));
        options.set(shakeMenu.booleanValue());
    }

    private void addAggressiveOption(Map<String, Object> options, Map<String, String> settings) {
        Boolean agg = Boolean.parseBoolean(settings.get(AGGRESSIVE));
        options.set(agg.booleanValue());
    }

    private void addSessionMinutesOption(Map<String, Object> options, Map<String, String> settings) {
        try {
            final Long l = Long.parseLong(settings.get(SESSION_MINUTES));
            options.set(l);
        } catch (NumberFormatException nfe) {
        }
    }

    private void addTurnMenuOption(Map<String, Object> options, Map<String, String> settings) {
        Boolean turnMenu = Boolean.parseBoolean(settings.get(TURN_MENU));
        options.set(turnMenu.booleanValue());
    }

    private void addDisableBordersOption(Map<String, Object> options, Map<String, String> settings) {
        Boolean disableBorders = Boolean.parseBoolean(settings.get(DISABLE_BORDERS));
        options.set(disableBorders.booleanValue());
    }

    private void addRetrofitOption(Map<String, Object> options, Map<String, String> settings) {
        Boolean retrofit = Boolean.parseBoolean(settings.get(RETROFIT));
        options.set(retrofit.booleanValue());
    }

    @Override
    public String getName() {
        return "Taplytics";
    }

    /**
     * AttributeListener Interface
     */

    @Override
    public void setUserAttribute(String attributeKey, String attributeValue) {
        try {
            JSONObject attr = new JSONObject();
            attr.put(attributeKey, attributeValue);
            Taplytics.setUserAttributes(attr);
        } catch (JSONException e) {

        }
    }

    @Override
    public boolean supportsAttributeLists() {
        return false;
    }

    @Override
    public void setAllUserAttributes(Map<String, String> attributes, Map<String, List<String>> attributeLists) {
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            setUserAttribute(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void setUserIdentity(MParticle.IdentityType identityType, String identity) {
        switch (identityType) {
            case CustomerId: {
                setUserAttribute("user_id", identity);
                break;
            }
        }
    }

    @Override
    public void removeUserIdentity(MParticle.IdentityType identityType) {
        setUserIdentity(identityType, null);
    }

    /**
     * CommerceListener Interface
     */

    @Override
    public List<ReportingMessage> logEvent(CommerceEvent event) {
        String eventName = event.getEventName();
        int checkoutStep = event.getCheckoutStep().intValue();
        if (eventName.isEmpty()) {
            return null;
        }
        Taplytics.logRevenue(eventName, checkoutStep);
        return createReportingMessages(ReportingMessage.MessageType.COMMERCE_EVENT);
    }

    /**
     * EventListener Interface
     */

    @Override
    public List<ReportingMessage> logEvent(MPEvent event) {
        String eventName = event.getEventName();
        Taplytics.logEvent(eventName);
        return createReportingMessages(ReportingMessage.fromEvent(this, event));
    }

    @Override
    public List<ReportingMessage> logScreen(String screenName, Map<String, String> screenAttributes) {

        return createReportingMessages(ReportingMessage.MessageType.SCREEN_VIEW);
    }

    /**
     * ApplicationStateListener Interface
     */

    @Override
    public void onApplicationForeground() {
        Taplytics.logEvent(EventAppForeground);
    }

    @Override
    public void onApplicationBackground() {
        Taplytics.logEvent(EventAppBackground);
    }

    /**
     * Set opt out for Taplytics
     * @param optedOut
     * @return
     */

    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        if (optedOut) {
            Taplytics.optOutTracking(this);
        } else {
            Taplytics.optInTracking(this);
        }
        return createReportingMessages(ReportingMessage.MessageType.OPT_OUT);
    }
}