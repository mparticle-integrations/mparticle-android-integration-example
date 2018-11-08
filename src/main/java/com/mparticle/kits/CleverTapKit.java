package com.mparticle.kits;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.Application;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.NotificationInfo;
import com.mparticle.MPEvent;
import com.mparticle.MParticle;
import com.mparticle.MParticle.UserAttributes;
import com.mparticle.commerce.CommerceEvent;
import com.mparticle.commerce.Product;
import com.mparticle.identity.MParticleUser;
import com.mparticle.internal.Logger;


public class CleverTapKit extends KitIntegration implements KitIntegration.AttributeListener, KitIntegration.CommerceListener, KitIntegration.EventListener, KitIntegration.PushListener, KitIntegration.IdentityListener  {

    private CleverTapAPI cl = null;
    private static final String CLEVERTAP_KEY = "CleverTap";
    private static final String ACCOUNT_ID_KEY = "clevertap_account_id";
    private static final String ACCOUNT_TOKEN_KEY = "clevertap_account_token";
    private static final String ACCOUNT_REGION_KEY = "region";
    private static final String PUSH_ENABLED = "push_enabled";
    private static final String PREF_KEY_HAS_SYNCED_ATTRIBUTES = "clevertap::has_synced_attributes";


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
        cl = CleverTapAPI.getDefaultInstance(getContext());

        ActivityLifecycleCallback.register(((Application) context.getApplicationContext()));
        return null;
    }


    @Override
    public String getName() {
        return CLEVERTAP_KEY;
    }

    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        return null;
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
        Map<String, Object> props = new HashMap<>();
        props.putAll(info);
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
            List<Product> products = event.getProducts();
            for (int i = 0; i < products.size(); i++) {
                try {
                    Product product = products.get(i);
                    HashMap<String, String> attrs = new HashMap<>();
                    CommerceEventUtils.extractProductFields(product, attrs);
                    HashMap<String, Object> item = new HashMap<>();
                    item.putAll(attrs);
                    items.add(item);
                } catch (Throwable t) {
                    cl.pushError("Error handling Order Completed product: " + t.getMessage(), 512);
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
    public void setUserAttribute(String key, String value) {
        HashMap<String, Object> profile = new HashMap<>();
        if ("birthday".equals(key)) {
            key = "DOB";
        } else if ("name".equals(key)) {
            key = "Name";
        } else if (UserAttributes.GENDER.equals(key)) {
            if (value.contains("fe")) {
                value = "F";
            } else {
                value = "M";
            }
        } else if (UserAttributes.MOBILE_NUMBER.equals(key)) {
            key = "Phone";
        } else {
            if (key.startsWith("$")) {
                key = key.substring(1);
            }
        }
        profile.put(key, value);
        cl.pushProfile(profile);
    }

    @Override
    public void setUserAttributeList(String key, List<String> list) {
        cl.setMultiValuesForKey(key, new ArrayList(list));
    }

    @Override
    public boolean supportsAttributeLists() {
        return true;
    }

    /**
     * This is called when the Kit is added to the mParticle SDK, typically on app-startup.
     */
    @Override
    public void setAllUserAttributes(Map<String, String> attributes, Map<String, List<String>> attributeLists) {
        if (!getKitPreferences().getBoolean(PREF_KEY_HAS_SYNCED_ATTRIBUTES, false)) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                setUserAttribute(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, List<String>> entry : attributeLists.entrySet()) {
                setUserAttributeList(entry.getKey(), entry.getValue());
            }
            getKitPreferences().edit().putBoolean(PREF_KEY_HAS_SYNCED_ATTRIBUTES, true).apply();
        }
    }

    @Override
    public void removeUserAttribute(String key) {
        if (UserAttributes.MOBILE_NUMBER.equals(key)) {
            key = "Phone";
        } else {
            if (key.startsWith("$")) {
                key = key.substring(1);
            }
        }
        cl.removeValueForKey(key);
    }

    @Override
    public void setUserIdentity(MParticle.IdentityType identityType, String identity) {
        // no-op
    }

    @Override
    public void removeUserIdentity(MParticle.IdentityType identityType) {
        // no-op
    }

    @Override
    public List<ReportingMessage> logout() {
        return null;
    }

    @Override
    public List<ReportingMessage> logLtvIncrease(BigDecimal valueIncreased, BigDecimal valueTotal, String eventName, Map<String, String> contextInfo) {
        return null;
    }

    @Override
    public boolean willHandlePushMessage(Intent intent) {
        if (!Boolean.parseBoolean(getSettings().get(PUSH_ENABLED))) {
            return false;
        }
        NotificationInfo info = CleverTapAPI.getNotificationInfo(intent.getExtras());
        return info.fromCleverTap;
    }

    @Override
    public void onPushMessageReceived(Context context, Intent pushIntent) {
        if (Boolean.parseBoolean(getSettings().get(PUSH_ENABLED))) {
            Bundle extras = pushIntent.getExtras();
            if (extras != null) {
                NotificationInfo info = CleverTapAPI.getNotificationInfo(extras);
                if (info.fromCleverTap) {
                    CleverTapAPI.createNotification(getContext(), extras);
                }
            }
        }
    }

    @Override
    public boolean onPushRegistration(String instanceId, String senderId) {
        if (Boolean.parseBoolean(getSettings().get(PUSH_ENABLED))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onUserIdentified(MParticleUser mParticleUser) {
        // no-op
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
        // no-op
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
            profile.put("Identity", customerId);
        }
        if (email != null) {
            profile.put("Email", email);
        }
        if (fbid != null) {
            profile.put("FBID", email);
        }
        if (gpid != null) {
            profile.put("GPID", email);
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