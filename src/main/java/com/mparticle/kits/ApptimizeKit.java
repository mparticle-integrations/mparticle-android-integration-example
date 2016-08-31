package com.mparticle.kits;

import android.content.Context;
import android.text.TextUtils;

import com.apptimize.Apptimize;
import com.apptimize.ApptimizeOptions;
import com.mparticle.MPEvent;
import com.mparticle.MParticle;
import com.mparticle.commerce.CommerceEvent;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ApptimizeKit
        extends KitIntegration
        implements KitIntegration.AttributeListener,
        KitIntegration.EventListener,
        KitIntegration.CommerceListener {

    private static final String APP_MP_KEY = "appKey";
    private static final String UPDATE_METDATA_TIMEOUT_MP_KEY = "metadataTimeout";
    private static final String DEVICE_NAME_MP_KEY = "deviceName";
    private static final String DEVELOPER_MODE_DISABLED_MP_KEY = "developerModeDisabled";
    private static final String EXPLICIT_ENABLING_REQUIRED_MP_KEY = "explicitEnablingRequired";
    private static final String MULTIPROCESS_MODE_ENABLED_MP_KEY = "multiprocessModeEnabled";
    private static final String LOG_LEVEL_MP_KEY = "logLevel";
    private static final String LOGOUT_TAG = "logout";
    private static final String LTV_TAG = "ltv";
    private static final String VIEWED_EVENT_FORMAT = "screenView %s";

    private List<ReportingMessage> toMessageList(final ReportingMessage message) {
        return Collections.singletonList(message);
    }

    private ReportingMessage createReportingMessage(final String messageType) {
        return new ReportingMessage(
                this,
                messageType,
                System.currentTimeMillis(),
                null
        );
    }

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        final String appKey = getSettings().get(APP_MP_KEY);
        if (TextUtils.isEmpty(appKey)) {
            throw new IllegalArgumentException(APP_MP_KEY);
        }
        Apptimize.setup(getContext(), appKey, buildApptimizeOptions(settings));
        return null;
    }

    private ApptimizeOptions buildApptimizeOptions(final Map<String, String> settings) {
        ApptimizeOptions o = new ApptimizeOptions();
        o.setThirdPartyEventImportingEnabled(false);
        configureApptimizeUpdateMetaDataTimeout(o, settings);
        configureApptimizeDeviceName(o, settings);
        configureApptimizeDeveloperModeDisabled(o, settings);
        configureApptimizeExplicitEnablingRequired(o, settings);
        configureApptimizeMultiprocessModeEnabled(o, settings);
        configureApptimizeLogLevel(o, settings);
        return o;
    }

    private void configureApptimizeUpdateMetaDataTimeout(final ApptimizeOptions o, final Map<String, String> settings) {
        try {
            final Long l = Long.parseLong(settings.get(UPDATE_METDATA_TIMEOUT_MP_KEY));
            o.setUpdateMetadataTimeout(l);
        } catch (NumberFormatException nfe) {
        }
    }

    private void configureApptimizeDeviceName(final ApptimizeOptions o, final Map<String, String> settings) {
        final String v = settings.get(DEVICE_NAME_MP_KEY);
        o.setDeviceName(v);
    }

    private void configureApptimizeDeveloperModeDisabled(final ApptimizeOptions o, final Map<String, String> settings) {
        final Boolean b = Boolean.parseBoolean(settings.get(DEVELOPER_MODE_DISABLED_MP_KEY));
        o.setDeveloperModeDisabled(b.booleanValue());
    }

    private void configureApptimizeExplicitEnablingRequired(final ApptimizeOptions o, final Map<String, String> settings) {
        final Boolean b = Boolean.parseBoolean(settings.get(EXPLICIT_ENABLING_REQUIRED_MP_KEY));
        o.setExplicitEnablingRequired(b.booleanValue());
    }

    private void configureApptimizeMultiprocessModeEnabled(final ApptimizeOptions o, final Map<String, String> settings) {
        final Boolean b = Boolean.parseBoolean(settings.get(MULTIPROCESS_MODE_ENABLED_MP_KEY));
        o.setMultiprocessMode(b.booleanValue());
    }

    private void configureApptimizeLogLevel(final ApptimizeOptions o, final Map<String, String> settings) {
        try {
            final ApptimizeOptions.LogLevel l = ApptimizeOptions.LogLevel.valueOf(settings.get(LOG_LEVEL_MP_KEY));
            o.setLogLevel(l);
        } catch (IllegalArgumentException iae) {
        }
    }

    @Override
    public String getName() {
        return "Apptimize";
    }

    @Override
    public void setUserAttribute(String key, String value) {
        Apptimize.setUserAttribute(key, value);
    }

    @Override
    public void setUserAttributeList(String key, List<String> list) {
        // not supported
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
        // attributeLists are not supported
    }

    @Override
    public void removeUserAttribute(String key) {
        Apptimize.clearUserAttribute(key);
    }

    @Override
    public void setUserIdentity(MParticle.IdentityType identityType, String id) {
        switch (identityType) {
            case Alias:
            case CustomerId: {
                Apptimize.setPilotTargetingId(id);
                break;
            }
        }
    }

    @Override
    public void removeUserIdentity(MParticle.IdentityType identityType) {
        setUserIdentity(identityType, null);
    }

    @Override
    public List<ReportingMessage> logout() {
        Apptimize.track(LOGOUT_TAG);
        return toMessageList(ReportingMessage.logoutMessage(this));
    }

    @Override
    public List<ReportingMessage> leaveBreadcrumb(String s) {
        // not supported
        return null;
    }

    @Override
    public List<ReportingMessage> logError(String s, Map<String, String> map) {
        // not supported
        return null;
    }

    @Override
    public List<ReportingMessage> logException(Exception e, Map<String, String> map, String s) {
        // not supported
        return null;
    }

    @Override
    public List<ReportingMessage> logEvent(MPEvent mpEvent) {
        Apptimize.track(mpEvent.getEventName());
        return toMessageList(ReportingMessage.fromEvent(this, mpEvent));
    }

    @Override
    public List<ReportingMessage> logScreen(String screenName, Map<String, String> eventAttributes) {
        final String event = String.format(VIEWED_EVENT_FORMAT, screenName);
        Apptimize.track(event);
        return toMessageList(createReportingMessage(ReportingMessage.MessageType.SCREEN_VIEW).setScreenName(screenName));
    }

    @Override
    public List<ReportingMessage> logLtvIncrease(BigDecimal valueIncreased, BigDecimal valueTotal, String eventName, Map<String, String> contextInfo) {
        // match the iOS style, where only the delta is sent rather than an absolute final value.
        Apptimize.track(LTV_TAG, valueIncreased.doubleValue());
        return toMessageList(createReportingMessage(ReportingMessage.MessageType.COMMERCE_EVENT));
    }

    @Override
    public List<ReportingMessage> logEvent(CommerceEvent event) {
        // not supported.
        return null;
    }

    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        List<ReportingMessage> ret = null;
        if (optedOut) {
            Apptimize.disable();
            ret = toMessageList(createReportingMessage(ReportingMessage.MessageType.OPT_OUT).setOptOut(optedOut));
        }
        return ret;
    }
}