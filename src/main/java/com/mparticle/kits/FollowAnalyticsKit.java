package com.mparticle.kits;

import android.content.Context;

import java.util.List;
import java.util.Map;
import java.util.LinkedList;

import com.mparticle.MPEvent;
import com.mparticle.MParticle;
import com.followanalytics.FollowAnalytics;

public class FollowAnalyticsKit extends KitIntegration implements KitIntegration.EventListener,
        KitIntegration.AttributeListener {

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        return null;
    }


    @Override
    public String getName() {
        //TODO: Replace this with your company name
        return "FollowAnalytics";
    }


    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        //TODO: Disable or enable your SDK when a user opts out.
        //TODO: If your SDK can not be opted out of, return null
        ReportingMessage optOutMessage = new ReportingMessage(this, ReportingMessage.MessageType.OPT_OUT, System.currentTimeMillis(), null);
        return null;
    }

    // Events
    @Override
    public List<ReportingMessage> logScreen(String screenName, Map<String, String> eventAttributes) {
        return null;
    }

    @Override
    public List<ReportingMessage> logEvent(MPEvent event) {
        if (event.getInfo() == null) {
            FollowAnalytics.logEvent(event.getEventName());
        }else{
            FollowAnalytics.logEvent(event.getEventName(), event.getInfo());
        }
        List<ReportingMessage> messageList = new LinkedList<ReportingMessage>();
        messageList.add(ReportingMessage.fromEvent(this,event));
        return messageList;
    }

    @Override
    public List<ReportingMessage> logError(String message, Map<String, String> errorAttributes) {
        FollowAnalytics.logError(message, errorAttributes);
        return null;
    }

    @Override
    public List<ReportingMessage> logException(Exception exception, Map<String, String> exceptionAttributes, String message) {
        return null;
    }

    @Override
    public List<ReportingMessage> leaveBreadcrumb(String breadcrumb) {
        return null;
    }

    // Attributes
    @Override
    public List<ReportingMessage> logout() {
        return null;
    }

    @Override
    public void setUserAttribute(String key, String value) {
        switch(key) {
            case MParticle.UserAttributes.GENDER :
                FollowAnalytics.UserAttributes.setGender(Integer.parseInt(value));
                break;
            case MParticle.UserAttributes.COUNTRY :
                FollowAnalytics.UserAttributes.setCountry(value);
                break;
            case MParticle.UserAttributes.CITY :
                FollowAnalytics.UserAttributes.setCity(value);
                break;
            case MParticle.UserAttributes.FIRSTNAME :
                FollowAnalytics.UserAttributes.setFirstName(value);
                break;
            case MParticle.UserAttributes.LASTNAME :
                FollowAnalytics.UserAttributes.setLastName(value);
                break;
            default :
                FollowAnalytics.UserAttributes.setString(key, value);
                break;

        }
    }

    @Override
    public void removeUserAttribute(String key) {
        FollowAnalytics.UserAttributes.clear(key);
    }

    @Override
    public void setUserAttributeList(String s, List<String> list) { }

    @Override
    public void setUserIdentity(MParticle.IdentityType identityType, String id) {
        if (identityType.equals(MParticle.IdentityType.CustomerId)) {
            FollowAnalytics.setUserId(id);
        }
    }

    @Override
    public void removeUserIdentity(MParticle.IdentityType identityType) {
        if (identityType.equals(MParticle.IdentityType.CustomerId)) {
            FollowAnalytics.setUserId(null);
        }
    }

    @Override
    public void setAllUserAttributes(Map<String, String> attributes, Map<String, List<String>> attributeLists) {
        for (Map.Entry<String, String> entry : attributes.entrySet()){
            setUserAttribute(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean supportsAttributeLists() {
        return false;
    }

}