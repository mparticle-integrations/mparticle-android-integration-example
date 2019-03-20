package com.mparticle.kits;

import android.content.Context;

import com.foursquare.pilgrim.LogLevel;
import com.foursquare.pilgrim.PilgrimSdk;
import com.foursquare.pilgrim.PilgrimUserInfo;
import com.mparticle.MParticle;
import com.mparticle.consent.ConsentState;
import com.mparticle.identity.MParticleUser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This is the PilgrimSdk mParticle kit , used to extend the functionality of mParticle SDK and allow control of
 * the PilgimSdk; Mapping analogous public mParticle APIs into PilgrimSdk's API.
 * <p>
 * <p>
 * In addition to this file, you also will need to edit:
 * - ./build.gradle (as explained above)
 * - ./README.md
 */
final public class PilgrimSdkKit extends KitIntegration implements KitIntegration.UserAttributeListener, KitIntegration.IdentityListener {

    /**
     * Name of the kit
     */
    private static final String KIT_NAME = "PilgrimSdkKit";

    /**
     * Key used to get the Pilgrim Sdk api key from mParticle's settings response
     */
    static final String SDK_KEY = "pilgrim_sdk_key";

    /**
     * Key used to get the Pilgrim Sdk secret from mParticle's settings response
     */
    static final String SDK_SECRET = "pilgrim_sdk_secret";

    /**
     * Key used to get the Pilgrim Sdk enable  configuration enabling flag from mParticle's settings response
     */
    private static final String SDK_ENABLE_PERSISTENT_LOGS = "pilgrim_persistent_logs";

    private static final String MPARTILE_USER_ID = "mParticleUserId";


    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        String key = settings.get(SDK_KEY);
        if (KitUtils.isEmpty(key)) {
            throw new IllegalArgumentException("PilgrimSdk key is empty.");
        }

        String secret = settings.get(SDK_SECRET);
        if (KitUtils.isEmpty(secret)) {
            throw new IllegalArgumentException("PilgrimSdk secret is empty.");
        }

        PilgrimSdk.Builder builder = new PilgrimSdk.Builder(context)
                .consumer(key, secret)
                .logLevel(LogLevel.DEBUG);

        if (KitUtils.parseBooleanSetting(settings, SDK_ENABLE_PERSISTENT_LOGS, false)) {
            builder.enableDebugLogs();
        }

        // Configure with our starter
        PilgrimSdk.with(builder);

        List<ReportingMessage> messageList = new LinkedList<>();
        // Can we add messages to track if initialized/started successfully in attributes
        messageList.add(new ReportingMessage(this, ReportingMessage.MessageType.APP_STATE_TRANSITION, System.currentTimeMillis(), null));
        return messageList;
    }

    @Override
    public String getName() {
        return KIT_NAME;
    }

    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        if (optedOut) {
            List<ReportingMessage> messageList = new LinkedList<>();
            PilgrimSdk.stop(getContext());
            messageList.add(new ReportingMessage(this, ReportingMessage.MessageType.OPT_OUT, System.currentTimeMillis(), null));
            return messageList;
        }
        return Collections.emptyList();
    }

    /**
     * Returns ether last set user information, or a new
     * PilgrimUserInfo instance.
     */
    private PilgrimUserInfo getUserInfo() {
        PilgrimUserInfo info = PilgrimSdk.get().getUserInfo();
        // can be null if it hasn't been set previously
        if (info == null) {
            info = new PilgrimUserInfo();
        }
        return info;
    }

    /* Section: User attribute set */
    @Override
    public void onSetAllUserAttributes(Map<String, String> attributes, Map<String, List<String>> attributeLists, FilteredMParticleUser filteredMParticleUser) {
        // NOTE: attributeList not supported
        // Is this ID correct?
        updateUser(filteredMParticleUser);
        PilgrimUserInfo info = getUserInfo();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            info.put(entry.getKey(), entry.getValue());
        }
        PilgrimSdk.get().setUserInfo(info);
    }

    @Override
    public void onIncrementUserAttribute(String s, int i, String s1, FilteredMParticleUser filteredMParticleUser) {
        // Ignored,  Not supported atm
    }

    @Override
    public void onRemoveUserAttribute(String s, FilteredMParticleUser filteredMParticleUser) {
        PilgrimUserInfo info = getUserInfo();
        info.remove(s);
    }

    @Override
    public void onSetUserAttribute(String s, Object o, FilteredMParticleUser filteredMParticleUser) {
        PilgrimUserInfo info = getUserInfo();
        info.put(s, o.toString());
        // update
        PilgrimSdk.get().setUserInfo(info);
    }

    @Override
    public void onSetUserTag(String s, FilteredMParticleUser filteredMParticleUser) {
        // Ignored, not supported atm.
    }

    @Override
    public boolean supportsAttributeLists() {
        // We don't support attribute lists
        // the PilgrimSdk currently only
        // allows single k => value attributes.
        return false;
    }

    @Override
    public void onSetUserAttributeList(String s, List<String> list, FilteredMParticleUser filteredMParticleUser) {
        // Ignored, not supported
    }

    @Override
    public void onConsentStateUpdated(ConsentState consentState, ConsentState consentState1, FilteredMParticleUser filteredMParticleUser) {
        // Ignore?
    }

    @Override
    public void onIdentifyCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
        updateUser(mParticleUser);
    }

    @Override
    public void onLoginCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
        updateUser(mParticleUser);
    }

    @Override
    public void onModifyCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
        updateUser(mParticleUser);
    }

    private void updateUser(MParticleUser mParticleUser) {
        PilgrimUserInfo info = getUserInfo();
        String mParticleUserId = String.valueOf(mParticleUser.getId());
        // only update if it's not null
        String customerId = mParticleUser.getUserIdentities().get(MParticle.IdentityType.CustomerId);
        if (customerId != null) {
            // only update if it's not null
            info.setUserId(customerId);
        }
        info.put(MPARTILE_USER_ID , mParticleUserId);
        PilgrimSdk.get().setUserInfo(info);
    }

    @Override
    public void onUserIdentified(MParticleUser mParticleUser) {
        // ignored, not used
    }

    @Override
    public void onLogoutCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
        // ignored, not used
    }
}