package com.mparticle.kits;

import android.content.Context;

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
import com.taplytics.sdk.TaplyticsHasUserOptedOutListener;

import org.json.JSONObject;

public class TaplyticsKit extends KitIntegration
        implements
        KitIntegration.AttributeListener,
        KitIntegration.EventListener,
        KitIntegration.CommerceListener {

    /**
     * Option Keys
     */
    private static final String API_KEY = "apiKey";
    private static final String AGGRESSIVE = "TaplyticsOptionAggressive";
    private static final String USER_ID = "user_id";
    private static final String EMAIL = "email";

    /**
     * tlOptions get and set methods
     */

    private static Map<String, Object> tlOptions = new HashMap<>();

    public static Map<String, Object> getTlOptions() { return tlOptions; }

    public static void setTlOptions(Map<String, Object> options) {
        tlOptions = options;
    }

    private HashMap<String, Object> mergeOptions(Map<String, Object> tlOptions, Map<String, Object> configuration) {
        HashMap<String, Object> merged = new HashMap<>(configuration);

        if (tlOptions != null) {
            for (Map.Entry<String, Object> entry : tlOptions.entrySet()) {
                merged.put(entry.getKey(), entry.getValue());
            }
        }
        return merged;
    }

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        if (tlOptions == null) {
            tlOptions = new HashMap<>();
        }
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
        String apiKey = getAPIKey(settings);
        HashMap<String, Object> options = mergeOptions(getTlOptions(), getOptionsFromConfiguration(settings));
        options.put("delayedStartTaplytics", true);

        if (options != null) {
            Taplytics.startTaplytics(context, apiKey, options);
            return;
        }

        Taplytics.startTaplytics(context, apiKey);
    }

    private String getAPIKey(Map<String, String> settings) {
        final String apiKey = getSettings().get(API_KEY);
        if (KitUtils.isEmpty(apiKey)) {
            throw new IllegalArgumentException("Failed to initialize Taplytics SDK - an API key is required");
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
                setUserAttribute(USER_ID, identity);
                break;
            }
            case Email: {
                setUserAttribute(EMAIL, identity);
            }
        }
    }

    @Override
    public void removeUserIdentity(MParticle.IdentityType identityType) {
        setUserIdentity(identityType, null);
    }


    @Override
    public void removeUserAttribute(String attribute) {
        setUserAttribute(attribute, null);
    }

    /*
        Unsupported methods
     */
    @Override
    public List<ReportingMessage> logout() { return null; }

    @Override
    public void setUserAttributeList(String attribute, List<String> attributeValueList) { }

    /**
     * CommerceListener Interface
     */

    @Override
    public List<ReportingMessage> logEvent(CommerceEvent event) {
        if (!KitUtils.isEmpty(event.getProductAction()) &&
                event.getProductAction().equalsIgnoreCase(Product.PURCHASE)) {

            TransactionAttributes transactionAttributes = event.getTransactionAttributes();

            if (transactionAttributes == null) {
                return null;
            }

            String id = transactionAttributes.getId();
            Double revenue = transactionAttributes.getRevenue();

            if (id == null || revenue == null) {
                return null;
            }

            Taplytics.logRevenue(id, revenue);
            return createReportingMessages(ReportingMessage.MessageType.COMMERCE_EVENT);
        }
        return null;
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
     * Set opt out for Taplytics
     * @param optedOut
     * @return
     */

    @Override
    public List<ReportingMessage> setOptOut(final boolean optedOut) {
        Taplytics.hasUserOptedOutTracking(null, new TaplyticsHasUserOptedOutListener() {
            @Override
            public void hasUserOptedOutTracking(boolean hasOptedOut) {
                if (!hasOptedOut && optedOut) {
                    Taplytics.optOutUserTracking(null);
                } else if (hasOptedOut && !optedOut) {
                    Taplytics.optInUserTracking(null);
                }
            }
        });

        return createReportingMessages(ReportingMessage.MessageType.OPT_OUT);
    }
}