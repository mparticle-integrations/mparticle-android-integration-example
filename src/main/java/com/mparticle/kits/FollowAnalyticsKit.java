package com.mparticle.kits;

import android.content.Context;

import java.util.List;
import java.util.Map;
import java.util.LinkedList;

import com.mparticle.MPEvent;
import com.mparticle.MParticle;
import com.followanalytics.FollowAnalytics;

public class FollowAnalyticsKit extends KitIntegration implements KitIntegration.EventListener {

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

}