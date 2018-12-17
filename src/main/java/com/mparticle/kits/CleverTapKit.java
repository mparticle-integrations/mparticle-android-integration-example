package com.mparticle.kits;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.NotificationInfo;
import com.mparticle.MPEvent;
import com.mparticle.MParticle;
import com.mparticle.MParticle.UserAttributes;
import com.mparticle.commerce.CommerceEvent;
import com.mparticle.commerce.Product;
import com.mparticle.consent.ConsentState;
import com.mparticle.identity.MParticleUser;
import com.mparticle.internal.Logger;
import com.mparticle.internal.MPUtility;


public class CleverTapKit extends KitIntegration implements
        KitIntegration.UserAttributeListener,
        KitIntegration.CommerceListener,
        KitIntegration.EventListener,
        KitIntegration.PushListener,
        KitIntegration.IdentityListener  {

    private CleverTapAPI cl = null;
    private static final String CLEVERTAP_KEY = "CleverTap";
    private static final String ACCOUNT_ID_KEY = "AccountID";
    private static final String ACCOUNT_TOKEN_KEY = "AccountToken";
    private static final String ACCOUNT_REGION_KEY = "Region";
    private static final String PREF_KEY_HAS_SYNCED_ATTRIBUTES = "clevertap::has_synced_attributes";
    private static final String CLEVERTAPID_INTEGRATION_KEY = "clevertap_id_integration_setting";

    private static final String IDENTITY_EMAIL = "Email";
    private static final String IDENTITY_FACEBOOK = "FBID";
    private static final String IDENTITY_GOOGLE = "GPID";
    private static final String IDENTITY_IDENTITY = "Identity";

    private static final String PHONE = "Phone";
    private static final String NAME = "Name";
    private static final String BIRTHDAY = "birthday";
    private static final String DOB = "DOB";
    private static final String MALE = "M";
    private static final String FEMALE = "F";

    private static Handler handler = null;

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        String accountID = settings.get(ACCOUNT_ID_KEY);
        if (KitUtils.isEmpty(accountID)) {
            throw new IllegalArgumentException("CleverTap AccountID is empty.");
        }
        String accountToken = settings.get(ACCOUNT_TOKEN_KEY);
        if (KitUtils.isEmpty(accountToken)) {
            throw new IllegalArgumentException("CleverTap AccountToken is empty.");
        }

        String region = settings.get(ACCOUNT_REGION_KEY);
        CleverTapAPI.changeCredentials(accountID, accountToken, region);

        ActivityLifecycleCallback.register(((Application) context.getApplicationContext()));

        cl = CleverTapAPI.getDefaultInstance(getContext());

        updateIntegrationAttributes();
        return null;
    }

    /**
     * Sets the CleverTap Device ID as an mParticle integration attribute.
     * Need to poll for it as its set asynchronously within the SDK (on initial launch)
     */
    private void updateIntegrationAttributes() {
        String cleverTapID = cl.getCleverTapAttributionIdentifier();
        if (!KitUtils.isEmpty(cleverTapID)) {
            HashMap<String, String> integrationAttributes = new HashMap<String, String>(1);
            integrationAttributes.put(CLEVERTAPID_INTEGRATION_KEY, cleverTapID);
            this.setIntegrationAttributes(integrationAttributes);
        } else {
            if (handler == null) {
               handler = new Handler();
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateIntegrationAttributes();
                }
            }, 500);
        }
    }

    @Override
    public String getName() {
        return CLEVERTAP_KEY;
    }

    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        cl.setOptOut(optedOut);
        List<ReportingMessage> messages = new LinkedList<ReportingMessage>();
        messages.add(new ReportingMessage(this, ReportingMessage.MessageType.OPT_OUT, System.currentTimeMillis(), null));
        return messages;
    }

    @Override
    public void setLocation(android.location.Location location) {
        cl.setLocation(location);
    }

    @Override
    public void setInstallReferrer(android.content.Intent intent) {
        cl.pushInstallReferrer(intent);
    }

    @Override
    public List<ReportingMessage> leaveBreadcrumb(String breadcrumb) {
        return null;
    }

    @Override
    public List<ReportingMessage> logError(String message, Map<String, String> errorAttributes) {
        return null;
    }

    @Override
    public List<ReportingMessage> logException(Exception exception, Map<String, String> exceptionAttributes, String message) {
        return null;
    }

    @Override
    public List<ReportingMessage> logEvent(MPEvent event) {
        Map<String,String> info = event.getInfo();
        Map<String, Object> props = new HashMap<String, Object>(info);
        cl.pushEvent(event.getEventName(),props);
        List<ReportingMessage> messages = new LinkedList<ReportingMessage>();
        messages.add(ReportingMessage.fromEvent(this, event));
        return messages;
    }

    @Override
    public List<ReportingMessage> logScreen(String screenName, Map<String, String> screenAttributes) {
        if(screenName == null) {
            return null;
        }
        cl.recordScreen(screenName);
        List<ReportingMessage> messages = new LinkedList<ReportingMessage>();
        messages.add(new ReportingMessage(this, ReportingMessage.MessageType.SCREEN_VIEW, System.currentTimeMillis(), screenAttributes));
        return messages;
    }

    @Override
    public List<ReportingMessage> logEvent(CommerceEvent event) {
        List<ReportingMessage> messages = new LinkedList<ReportingMessage>();
        if (!KitUtils.isEmpty(event.getProductAction()) &&
                event.getProductAction().equalsIgnoreCase(Product.PURCHASE) &&
                event.getProducts().size() > 0) {

            HashMap<String, Object> details = new HashMap<>();
            ArrayList<HashMap<String, Object>> items = new ArrayList<>();

            Map<String, String> eventAttributes = new HashMap<>();
            CommerceEventUtils.extractActionAttributes(event, eventAttributes);

            for (Map.Entry<String, String> entry : eventAttributes.entrySet()) {
                details.put(entry.getKey(), entry.getValue());
            }

            String transactionId = (event.getTransactionAttributes() != null && !MPUtility.isEmpty(event.getTransactionAttributes().getId())) ? event.getTransactionAttributes().getId(): null;
            if (transactionId != null) {
                details.put("Charged ID", transactionId);
            }
            List<Product> products = event.getProducts();
            for (int i = 0; i < products.size(); i++) {
                try {
                    Product product = products.get(i);
                    HashMap<String, String> attrs = new HashMap<>();
                    CommerceEventUtils.extractProductFields(product, attrs);
                    HashMap<String, Object> item = new HashMap<String, Object>(attrs);
                    items.add(item);
                } catch (Throwable t) {
                    cl.pushError("Error handling Commerce Event product: " + t.getMessage(), 512);
                }
            }
            cl.pushChargedEvent(details, items);
            messages.add(ReportingMessage.fromEvent(this, event));
            return messages;
        }
        List<MPEvent> eventList = CommerceEventUtils.expand(event);
        for (int i = 0; i < eventList.size(); i++) {
            try {
                logEvent(eventList.get(i));
                messages.add(ReportingMessage.fromEvent(this, event));
            } catch (Exception e) {
                Logger.warning("Failed to call logCustomEvent to CleverTap kit: " + e.toString());
            }
        }
        return messages;
    }

    @Override
    public void onSetUserAttributeList(String attributeKey, List<String> attributeValueList, FilteredMParticleUser user) {
        cl.setMultiValuesForKey(attributeKey, new ArrayList<String>(attributeValueList));
    }

    @Override
    public boolean supportsAttributeLists() {
        return true;
    }

    @Override
    public void onIncrementUserAttribute (String key, String value, FilteredMParticleUser user) {
        // not supported
    }

    @Override
    public void onRemoveUserAttribute(String key, FilteredMParticleUser user) {
        if (UserAttributes.MOBILE_NUMBER.equals(key)) {
            key = PHONE;
        } else {
            if (key.startsWith("$")) {
                key = key.substring(1);
            }
        }
        cl.removeValueForKey(key);
    }

    @Override
    public void onSetUserAttribute(String key, Object value, FilteredMParticleUser user) {
        HashMap<String, Object> profile = new HashMap<>();
        if (BIRTHDAY.equals(key)) {
            key = DOB;
        } else if ("name".equals(key)) {
            key = NAME;
        } else if (UserAttributes.GENDER.equals(key)) {
            String _value = (String) value;
            if (_value.contains("fe")) {
                value = FEMALE;
            } else {
                value = MALE;
            }
        } else if (UserAttributes.MOBILE_NUMBER.equals(key)) {
            key = PHONE;
        } else {
            if (key.startsWith("$")) {
                key = key.substring(1);
            }
        }
        profile.put(key, value);
        cl.pushProfile(profile);
    }

    @Override
    public void onSetUserTag(String key, FilteredMParticleUser user) {
        // not supported
    }

    @Override
    public void onSetAllUserAttributes(Map<String, String> userAttributes, Map<String, List<String>> userAttributeLists, FilteredMParticleUser user) {
        if (!getKitPreferences().getBoolean(PREF_KEY_HAS_SYNCED_ATTRIBUTES, false)) {
            for (Map.Entry<String, String> entry : userAttributes.entrySet()) {
                onSetUserAttribute(entry.getKey(), entry.getValue(), user);
            }
            for (Map.Entry<String, List<String>> entry : userAttributeLists.entrySet()) {
                onSetUserAttributeList(entry.getKey(), entry.getValue(), user);
            }
            getKitPreferences().edit().putBoolean(PREF_KEY_HAS_SYNCED_ATTRIBUTES, true).apply();
        }
    }

    @Override
    public void onConsentStateUpdated(ConsentState oldState, ConsentState newState, FilteredMParticleUser user) {
        // not supported
    }

    @Override
    public List<ReportingMessage> logLtvIncrease(BigDecimal valueIncreased, BigDecimal valueTotal, String eventName, Map<String, String> contextInfo) {
        // not supported
        return null;
    }

    @Override
    public boolean willHandlePushMessage(Intent intent) {
        if (intent == null || intent.getExtras() == null) {
            return false;
        }
        NotificationInfo info = CleverTapAPI.getNotificationInfo(intent.getExtras());
        return info.fromCleverTap;
    }

    @Override
    public void onPushMessageReceived(Context context, Intent pushIntent) {
        if (pushIntent == null || pushIntent.getExtras() == null) {
            return;
        }
        Bundle extras = pushIntent.getExtras();
        NotificationInfo info = CleverTapAPI.getNotificationInfo(extras);
        if (info.fromCleverTap) {
            CleverTapAPI.createNotification(getContext(), extras);
        }
    }

    @Override
    public boolean onPushRegistration(String instanceId, String senderId) {
        cl.pushFcmRegistrationId(instanceId, true);
        return true;
    }

    @Override
    public void onUserIdentified(MParticleUser mParticleUser) {
        // not used
    }

    @Override
    public void onIdentifyCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
        updateUser(mParticleUser, false);
    }

    @Override
    public void onLoginCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
        updateUser(mParticleUser, true);
    }

    @Override
    public void onLogoutCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
        // not used
    }

    @Override
    public void onModifyCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
        updateUser(mParticleUser, false);
    }

    private void updateUser(MParticleUser mParticleUser, boolean isLogin) {
        HashMap<String, Object> profile = new HashMap<>();
        String customerId = mParticleUser.getUserIdentities().get(MParticle.IdentityType.CustomerId);
        String email = mParticleUser.getUserIdentities().get(MParticle.IdentityType.Email);
        String fbid = mParticleUser.getUserIdentities().get(MParticle.IdentityType.Facebook);
        String gpid = mParticleUser.getUserIdentities().get(MParticle.IdentityType.Google);

        if (customerId != null) {
            profile.put(IDENTITY_IDENTITY, customerId);
        }
        if (email != null) {
            profile.put(IDENTITY_EMAIL, email);
        }
        if (fbid != null) {
             profile.put(IDENTITY_FACEBOOK, fbid);
        }
        if (gpid != null) {
             profile.put(IDENTITY_GOOGLE, gpid);
        }

        if (profile.isEmpty()) {
            return;
        }

        if (isLogin) {
            cl.onUserLogin(profile);
        } else {
            cl.pushProfile(profile);
        }
    }
}
