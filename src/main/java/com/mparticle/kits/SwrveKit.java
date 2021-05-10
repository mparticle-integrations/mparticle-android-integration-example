package com.mparticle.kits;

import android.app.Application;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import android.os.Bundle;

import com.mparticle.MPEvent;
import com.mparticle.MParticle;
import com.mparticle.commerce.CommerceEvent;
import com.mparticle.commerce.Product;
import com.mparticle.consent.ConsentState;
import com.mparticle.identity.MParticleUser;
import com.swrve.sdk.Swrve;
import com.swrve.sdk.SwrveInitMode;
import com.swrve.sdk.SwrveNotificationConfig;
import com.swrve.sdk.SwrvePushServiceDefault;
import com.swrve.sdk.SwrveSDK;
import com.swrve.sdk.config.SwrveConfig;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
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
public class SwrveKit extends KitIntegration implements KitIntegration.UserAttributeListener, KitIntegration.CommerceListener, KitIntegration.EventListener, KitIntegration.PushListener, KitIntegration.IdentityListener {
    private static final String SWRVE_MPARTICLE_VERSION_NUMBER = "1.0.0";

    private void startSwrveSDK(Activity activity, long mpid) {
        SwrveSDK.start(activity, Long.toString(mpid));
        Map<String,String> version = new HashMap<String,String>();
        version.put("swrve.mparticle_android_integration_version", SWRVE_MPARTICLE_VERSION_NUMBER);
        SwrveSDK.userUpdate(version);
        SwrveSDK.sendQueuedEvents();
    }

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
        List<ReportingMessage> messageList = new LinkedList<ReportingMessage>();
        SwrveConfig config = new SwrveConfig();
        SwrveNotificationConfig notificationConfig = getNotificationConfig(settings, context);
        config.setNotificationConfig(notificationConfig);

        config.setInitMode(SwrveInitMode.MANAGED);
        // TODO when MParticleUser and userId is known, call SwrveSDK.start(activity, userId). Do not call start from application layer.

        int app_id = Integer.parseInt(settings.get("app_id"));
        String api_key = settings.get("api_key");
        // To use the EU stack, include this in your config.
        // config.setSelectedStack(SwrveStack.EU);
        SwrveSDK.createInstance( ( (Application) context.getApplicationContext() ), app_id, api_key, config);
        messageList.add(new ReportingMessage(this, ReportingMessage.MessageType.SESSION_START, System.currentTimeMillis(), null));
        return messageList;
    }

    private SwrveNotificationConfig getNotificationConfig(Map<String, String> settings, Context context) {
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("100", "Swrve default channel", NotificationManager.IMPORTANCE_DEFAULT);
            if (context.getSystemService(Context.NOTIFICATION_SERVICE) != null) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }
        }
        String packageName = context.getPackageName();
        int iconDrawableId = getResourceId("swrve_push_icon_drawable", "drawable", context);
        int iconMaterialDrawableId = getResourceId("swrve_push_icon_material_drawable", "drawable", context);
        int largeIconDrawableId = getResourceId("swrve_push_large_icon_drawable", "drawable", context);
        int accentColorResourceId = getResourceId("swrve_push_accent_color_resource", "color", context);

        Class activityClass = getActivityClass(settings, packageName, context);
        SwrveNotificationConfig.Builder notificationConfig = new SwrveNotificationConfig.Builder(iconDrawableId, iconMaterialDrawableId, channel)
                .activityClass(activityClass)
                .largeIconDrawableId(largeIconDrawableId)
                .accentColorResourceId(accentColorResourceId);
        return notificationConfig.build();
    }

    private Class getActivityClass(Map<String, String> settings, String pPackageName, Context context) {
        //String notificationActivityClassName = settings.get("notification_activity_class_name");
        int notificationActivityClassId = getResourceId("swrve_notification_activity_class_name","string", context);
        String notificationActivityClassName = context.getResources().getString(notificationActivityClassId);
        try {
            return Class.forName(notificationActivityClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getResourceId(String pVariableName, String pResourcename, Context context) 
    {
        String packageName = context.getPackageName();
        try {
            return context.getResources().getIdentifier(pVariableName, pResourcename, packageName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } 
    }

    @Override
    public Object getInstance() {
        return SwrveSDK.getInstance();
    }


    @Override
    public String getName() {
        return "Swrve";
    }

    @Override
    public void onSettingsUpdated(Map<String, String> settings) {
        //do nothing
    }



    @Override
    public List<ReportingMessage> logEvent(CommerceEvent event) {
        //TODO: handle commerce event
        List<ReportingMessage> messageList = new LinkedList<ReportingMessage>();
        String currency = "USD";
        if (event.getCurrency()!=null) {
            currency = event.getCurrency();
        }

        if (event.getProductAction()!=Product.PURCHASE) {
            //TODO: handle non-purchase commerce events (e.g. add to cart, add to wishlist, etc.)
            return null;
        }
        messageList.add(ReportingMessage.fromEvent(this,event));
        List<Product> products = event.getProducts();
        for (Product product : products) {
            int quantity = (int) product.getQuantity();
            SwrveSDK.iap(quantity, product.getSku(), product.getUnitPrice(), currency);
        }
        return messageList;
    }

    @Override
    public List<ReportingMessage> logLtvIncrease(BigDecimal valueIncreased, BigDecimal valueTotal, String eventName, Map<String, String> contextInfo) {
        //do nothing
        return null;
    }

    @Override
    public List<ReportingMessage> leaveBreadcrumb(String breadcrumb) {
        //do nothing
        return null;
    }

    @Override
    public List<ReportingMessage> logError(String message, Map<String, String> errorAttributes) {
        //do nothing
        return null;
    }

    @Override
    public List<ReportingMessage> logException(Exception exception, Map<String, String> exceptionAttributes, String message) {
        //do nothing
        return null;
    }

    @Override
    public List<ReportingMessage> logEvent(MPEvent event) {
        List<ReportingMessage> messageList = new LinkedList<ReportingMessage>();
        //TODO: handle currency_given first
        if(event.getEventType()==MParticle.EventType.Other) {
            if(event.getInfo().containsKey("given_currency") && event.getInfo().containsKey("given_amount")){
                String givenCurrency = event.getInfo().get("given_currency");
                double givenAmount = new Double(event.getInfo().get("given_amount"));
                SwrveSDK.currencyGiven(givenCurrency, givenAmount);

            }
        } else {
                SwrveSDK.event(event.getEventType().toString().toLowerCase()+"."+event.getEventName(), event.getInfo());            
        }
        messageList.add(ReportingMessage.fromEvent(this, event));
        return messageList;
    }

    @Override
    public List<ReportingMessage> logScreen(String screenName, Map<String, String> screenAttributes) {
        List<ReportingMessage> messageList = new LinkedList<ReportingMessage>();
        SwrveSDK.event("screen_view"+"."+screenName, screenAttributes);
        messageList.add(new ReportingMessage(this, ReportingMessage.MessageType.SCREEN_VIEW, System.currentTimeMillis(), screenAttributes));
        return messageList;
    }

    @Override
    public boolean willHandlePushMessage(Intent intent) {
        //determine if push is from Swrve
        Bundle extras = intent.getExtras();
        return (extras.containsKey("_p") || extras.containsKey("_sp"));
    }

    @Override
    public void onPushMessageReceived(Context context, Intent pushIntent) {
        //let Swrve SDK handle push message - if willHandlePushMessage returns true
        SwrvePushServiceDefault.handle(context, pushIntent);
    }

    @Override
    public boolean onPushRegistration(String instanceId, String senderId) {
        ((Swrve) SwrveSDK.getInstance()).setRegistrationId(instanceId);
        return true;
    }

    @Override
    public void onIdentifyCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest identityApiRequest) {

        long mpid = mParticleUser.getId();
        Activity activity = super.getCurrentActivity().get();
        if (activity!=null && !SwrveSDK.isStarted()) {
            startSwrveSDK(activity, mpid);       
        }
    }

    @Override
    public void onLoginCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest identityApiRequest) {
        long mpid = mParticleUser.getId();
        Activity activity = super.getCurrentActivity().get();
        if (activity!=null && !SwrveSDK.isStarted()) {
            startSwrveSDK(activity, mpid);       
        }
    }

    @Override
    public void onLogoutCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest identityApiRequest) {
        //do nothing
    }

    @Override
    public void onModifyCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest identityApiRequest) {
        long mpid = mParticleUser.getId();
        Activity activity = super.getCurrentActivity().get();
        if (activity!=null && !SwrveSDK.isStarted()) {
            startSwrveSDK(activity, mpid);       
        }
    }

    @Override
    public void onUserIdentified(MParticleUser mParticleUser) {
        long mpid = mParticleUser.getId();
        Activity activity = super.getCurrentActivity().get();
        if (activity!=null && !SwrveSDK.isStarted()) {
            startSwrveSDK(activity, mpid);       
        }
    }

    @Override
    public void onIncrementUserAttribute (String key, int incrementedBy, String value, FilteredMParticleUser user) {
        long mpid = user.getId();
        Activity activity = super.getCurrentActivity().get();
        if (activity!=null && !SwrveSDK.isStarted()) {
            startSwrveSDK(activity, mpid);       
        }

        Map<String,Object> attributes = user.getUserAttributes();
        Map<String,String> newAttributes = new HashMap<String,String>();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            newAttributes.put(entry.getKey(), entry.getValue().toString());
        }
        SwrveSDK.userUpdate(newAttributes);  
    }

    @Override
    public void onRemoveUserAttribute(String key, FilteredMParticleUser user) {
        long mpid = user.getId();
        Activity activity = super.getCurrentActivity().get();
        if (activity!=null && !SwrveSDK.isStarted()) {
            startSwrveSDK(activity, mpid);       
        }
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(key, "");
        SwrveSDK.userUpdate(attributes);
    }

    @Override
    public void onSetUserAttribute(String key, Object value, FilteredMParticleUser user) {
        long mpid = user.getId();
        Activity activity = super.getCurrentActivity().get();
        if (activity!=null && !SwrveSDK.isStarted()) {
            startSwrveSDK(activity, mpid);       
        }
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(key, value.toString());
        SwrveSDK.userUpdate(attributes);
    }

    @Override
    public void onSetUserAttributeList(String attributeKey, List<String> attributeValueList, FilteredMParticleUser user) {
        //do nothing
    }

    @Override
    public void onSetUserTag(String key, FilteredMParticleUser user){
        //do nothing
    }

    @Override
    public void onConsentStateUpdated(ConsentState oldState, ConsentState newState, FilteredMParticleUser user) {
        //do nothing
    }

    @Override
    public void onSetAllUserAttributes(Map<String, String> userAttributes, Map<String, List<String>> userAttributeLists, FilteredMParticleUser user) {
        long mpid = user.getId();
        Activity activity = super.getCurrentActivity().get();
        if (activity!=null && !SwrveSDK.isStarted()) {
            startSwrveSDK(activity, mpid);       
        }
        SwrveSDK.userUpdate(userAttributes); 
    }

    @Override
    public boolean supportsAttributeLists() {
        return false;
    }


    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        //TODO: Disable or enable your SDK when a user opts out.
        //TODO: If your SDK can not be opted out of, return null
        ReportingMessage optOutMessage = new ReportingMessage(this, ReportingMessage.MessageType.OPT_OUT, System.currentTimeMillis(), null);
        return null;
    }

    @Override
    protected void onKitDestroy() {
        //TODO: clear Swrve storage
    }

}