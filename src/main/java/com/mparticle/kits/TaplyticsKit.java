package com.mparticle.kits;

import android.content.Context;
import android.text.TextUtils;
import android.support.annotation.Keep;

import org.json.JSONException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.math.BigDecimal;

import com.mparticle.MPEvent;
import com.mparticle.MParticle;
import com.mparticle.MParticleOptions;
import com.mparticle.MParticleTask;
import com.mparticle.commerce.CommerceEvent;
import com.mparticle.commerce.Product;
import com.mparticle.commerce.TransactionAttributes;
import com.mparticle.identity.IdentityApiRequest;
import com.mparticle.identity.IdentityApiResult;

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
        KitIntegration.ApplicationStateListener {

    /**
     * Option Keys
     */
    private static final String API_KEY = "apiKey";
    private static final String AGGRESSIVE = "TaplyticsOptionAggressive";

    /**
     * Event Keys
     */
    public static final String EventAppBackground = "appBackground";
    public static final String EventAppForeground = "appForeground";

    private static Map<String, Object> tlOptions;

    public static void setTlOptions(Map<String, Object> options) {
        tlOptions = options;
    }

    private HashMap<String, Object> mergeOptions(Map<String, Object> tlOptions, Map<String, Object> configuration) {
        HashMap<String, Object> merged = new HashMap<>(configuration);
        for (Map.Entry<String, Object> entry : tlOptions.entrySet()) {
            merged.put(entry.getKey(), entry.getValue());
        }
        return merged;
    }

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {

        tlOptions = new HashMap<>();
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
        return Collections.singletonList(createReportMessage(message));
    }

    /**
     * Start Taplytics
     * @param settings
     * @param context
     */

    private void startTaplytics(Map<String, String> settings, Context context) {
        System.out.println("settings" + settings);
        String apiKey = getAPIKey(settings);
        HashMap<String, Object> options = mergeOptions(tlOptions, getOptionsFromConfiguration(settings));
        System.out.println("TLOptions" + tlOptions);
        System.out.println("merged options" + options);
        options.put("debugLogging", true);
        options.put("retroFit", true);

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

    private Map<String, Object> getOptionsFromConfiguration(Map<String, String> settings) {
        Map<String, Object> options = new HashMap<>();
        addAggressiveOption(options, settings);

        return options.isEmpty() ? null : options;
    }

    private void addAggressiveOption(Map<String, Object> options, Map<String, String> settings) {
        Boolean agg = Boolean.parseBoolean(settings.get(AGGRESSIVE));
        options.put("aggressive", agg.booleanValue());
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
    public boolean supportsAttributeLists() { return false; }

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

    /*
        Unsupported methods
     */
    @Override
    public List<ReportingMessage> logout() { return null; }

    @Override
    public void removeUserAttribute(String attribute) { }

    @Override
    public void setUserAttributeList(String attribute, List<String> attributeValueList) { }

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

    /*
        Unsupported Methods
     */

    @Override
    public List<ReportingMessage> logLtvIncrease(BigDecimal valueIncreased, BigDecimal valueTotal, String eventName, Map<String, String> contextInfo) { return null; }

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
        Taplytics.logEvent(screenName);
        return createReportingMessages(ReportingMessage.MessageType.SCREEN_VIEW);
    }

    /*
        Unsupported Methods
     */

    @Override
    public List<ReportingMessage> logException(Exception exception, Map<String, String> exceptionAttributes, String message) { return null; }

    @Override
    public List<ReportingMessage> logError(String message, Map<String, String> errorAttributes) { return null; }

    @Override
    public List<ReportingMessage> leaveBreadcrumb(String breadcrumb) { return null; }

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
            Taplytics.optOutUserTracking(null);
        } else {
            Taplytics.optInUserTracking(null);
        }
        return createReportingMessages(ReportingMessage.MessageType.OPT_OUT);
    }
}