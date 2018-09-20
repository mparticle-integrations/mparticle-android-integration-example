package com.mparticle.kits;

import android.content.Context;

import java.util.List;
import java.util.Map;

public class FollowAnalyticsKit extends KitIntegration implements
    KitIntegration.EventListenter,
    KitIntegration.AttributeListener {

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
}