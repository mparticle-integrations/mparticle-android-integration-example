package com.mparticle.kits;

import android.content.Context;

import com.foursquare.pilgrim.LogLevel;
import com.foursquare.pilgrim.PilgrimSdk;
import com.foursquare.pilgrim.PilgrimUserInfo;
import com.mparticle.MParticle;
import com.mparticle.consent.ConsentState;
import com.mparticle.identity.MParticleUser;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This is the PilgrimSDK mParticle kit , used to extend the functionality of mParticle SDK and allow control of
 * the PilgimSDK; Mapping analogous public mParticle APIs into PilgrimSDK's API.
 * <p>
 * <p>
 * In addition to this file, you also will need to edit:
 * - ./build.gradle (as explained above)
 * - ./README.md
 */
final public class PilgrimSDKKit extends KitIntegration implements KitIntegration.UserAttributeListener, KitIntegration.IdentityListener {

    /**
     * Name of the kit
     */
    private static final String KIT_NAME = "PilgrimSDKKit";

    /**
     * Key used to get the api key from mParticle's settings response
     */
    private static final String PILGRIM_SDK_KEY = "pilgrim_sdk_key";

    /**
     * Key used to get the secret from mParticle's settings response
     */
    private static final String PILGRIM_SDK_SECRET = "pilgrim_sdk_secret";


    private static final String ALLOW_DEBUG_OUTPUT = "enableDebug";

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        // We need to try and keep the context reference
        // for us to try and stop the SDK when requested.
        String key = settings.get(PILGRIM_SDK_KEY);
        if (KitUtils.isEmpty(key)) {
            throw new IllegalArgumentException("PilgrimSDK key is empty.");
        }

        String secret = settings.get(PILGRIM_SDK_SECRET);
        if (KitUtils.isEmpty(secret)) {
            throw new IllegalArgumentException("PilgrimSDK secret is empty.");
        }

        PilgrimSdk.Builder builder = new PilgrimSdk.Builder(context)
                .consumer(key, secret)
                .logLevel(LogLevel.ERROR);

        if (KitUtils.parseBooleanSetting(settings, ALLOW_DEBUG_OUTPUT, false)) {
            builder.enableDebugLogs();
        }

        // Configure with our starter
        PilgrimSdk.with(builder);
        // ----
        PilgrimSdk.start(context);

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
        List<ReportingMessage> messageList = new LinkedList<>();
        PilgrimSdk.stop(getContext());
        messageList.add(new ReportingMessage(this, ReportingMessage.MessageType.OPT_OUT, System.currentTimeMillis(), null));
        return messageList;
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
        updateUser(filteredMParticleUser.mpUser);
        PilgrimUserInfo info = getUserInfo();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            info.put(entry.getKey(), entry.getValue());
        }
        PilgrimSdk.get().setUserInfo(info);
    }


    @Override
    public void onIncrementUserAttribute(String s, String s1, FilteredMParticleUser filteredMParticleUser) {
        // Ignored,  Not supported atm
    }

    @Override
    public void onRemoveUserAttribute(String s, FilteredMParticleUser filteredMParticleUser) {
        PilgrimUserInfo info = getUserInfo();
        // Do we want to log if it doesn't exist?
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
        String customerId = mParticleUser.getUserIdentities().get(MParticle.IdentityType.CustomerId);
        if (customerId != null) {
            // only update if it's not null
            info.setUserId(customerId);
        }
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