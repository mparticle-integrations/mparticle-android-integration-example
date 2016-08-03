package com.mparticle.kits;

import android.content.Context;

import com.apptimize.Apptimize;
import com.mparticle.MPEvent;
import com.mparticle.MParticle;
import com.mparticle.commerce.CommerceEvent;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ApptimizeKit extends KitIntegration implements KitIntegration.AttributeListener, KitIntegration.EventListener {

    public static final String APP_KEY = "appKey";
    public static final String ALIAS_KEY = "mparticleAlias";
    public static final String CUSTOMER_ID_KEY = "mparticleCustomerId";
    public static final String VIEWED_EVENT_FORMAT = "Viewed %s Screen";

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        String appKey = getSettings().get(APP_KEY);
        Apptimize.setup(getContext(), appKey);
        return null;
    }

    @Override
    public String getName() {
        return "Apptimize";
    }

    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        Apptimize.disable();
        List<ReportingMessage> messageList = new LinkedList<>();
        messageList.add(
                new ReportingMessage(this, ReportingMessage.MessageType.OPT_OUT, System.currentTimeMillis(), null)
                        .setOptOut(optedOut)
        );
        return messageList;
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
        for (Map.Entry<String, String> entry : attributes.entrySet()){
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
        if (identityType.equals(MParticle.IdentityType.Alias)) {
            Apptimize.setUserAttribute(ALIAS_KEY, id);
        } else if (identityType.equals(MParticle.IdentityType.CustomerId)) {
            Apptimize.setUserAttribute(CUSTOMER_ID_KEY, id);
        }
    }

    @Override
    public void removeUserIdentity(MParticle.IdentityType identityType) {
        if (identityType.equals(MParticle.IdentityType.Alias)) {
            Apptimize.clearUserAttribute(ALIAS_KEY);
        } else if (identityType.equals(MParticle.IdentityType.CustomerId)) {
            Apptimize.clearUserAttribute(CUSTOMER_ID_KEY);
        }
    }

    @Override
    public List<ReportingMessage> logout() {
        // Apptimize does not consider the user to have changed when they logout
        return null;
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
        List<ReportingMessage> messageList = new LinkedList<>();
        messageList.add(ReportingMessage.fromEvent(this, mpEvent));
        return messageList;
    }

    @Override
    public List<ReportingMessage> logScreen(String screenName, Map<String, String> eventAttributes) {
        String event = String.format(VIEWED_EVENT_FORMAT, screenName);
        Apptimize.track(event);
        List<ReportingMessage> messageList = new LinkedList<>();
        messageList.add(
                new ReportingMessage(this, ReportingMessage.MessageType.SCREEN_VIEW, System.currentTimeMillis(), eventAttributes)
                        .setScreenName(screenName)
        );
        return messageList;
    }
}