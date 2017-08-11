package com.mparticle.kits;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mparticle.DeepLinkError;
import com.mparticle.DeepLinkResult;
import com.mparticle.MPEvent;
import com.mparticle.MParticle;
import com.rokolabs.sdk.RokoMobi;
import com.rokolabs.sdk.account.RokoAccount;
import com.rokolabs.sdk.analytics.Event;
import com.rokolabs.sdk.analytics.RokoLogger;
import com.rokolabs.sdk.instabot.Instabot;
import com.rokolabs.sdk.json.Json;
import com.rokolabs.sdk.links.ResponseVanityLink;
import com.rokolabs.sdk.links.RokoLinks;
import com.rokolabs.sdk.links.RokoLinks.CallbackVanityLink;
import com.rokolabs.sdk.push.PushData;
import com.rokolabs.sdk.push.PushNotificationHelper;
import com.rokolabs.sdk.push.RokoPush;
import com.rokolabs.sdk.push.RokoPushConstants;
import com.rokolabs.sdk.rokomobi.Settings;
import com.rokolabs.sdk.tools.ThreadUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class RokoMobiKit extends KitIntegration implements KitIntegration.EventListener, KitIntegration.PushListener, KitIntegration.AttributeListener, KitIntegration.ActivityListener, RokoMobiProvider {

    public static final String INSTABOT_ACTION = "com.rokolabs.mobi.INSTABOT_ACTION";
    public static final String KIT_NAME = "RokoMobi";
    public static final String RK_MSG_ID = "rkMsgId";
    public static final String DEVICE_TOKEN = "deviceToken";
    public static final String PUSH_ACTION = "com.rokolabs.mobi.PUSH_ACTION";

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        RokoMobi.start(context, new RokoMobi.CallbackStart() {
            @Override
            public void start() {
                RokoMobi.getSettings().setInstabotActivity(INSTABOT_ACTION);
                RokoMobi.getSettings().setPushActivity(PUSH_ACTION);
            }
        });
        return null;
    }

    @Override
    public Object getInstance() {
        return this;
    }

    @Override
    public Instabot instabot() {
        return new Instabot();
    }

    @Override
    public String getName() {
        return KIT_NAME;
    }

    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        return null;
    }

    @Override
    public List<ReportingMessage> leaveBreadcrumb(String s) {
        return null;
    }

    @Override
    public List<ReportingMessage> logError(String s, Map<String, String> map) {
        return null;
    }

    @Override
    public List<ReportingMessage> logException(Exception e, Map<String, String> map, String s) {
        return null;
    }

    @Override
    public List<ReportingMessage> logEvent(MPEvent mpEvent) {
        Log.e(getName(), mpEvent.getEventName());
        Event rokoEvent = new Event(mpEvent.getEventName());
        Map<String, String> eventInfo = mpEvent.getInfo();
        for (Map.Entry<String, String> entry : eventInfo.entrySet()) {
            rokoEvent.set(entry.getKey(), entry.getValue());
        }
        RokoLogger.addEvents(rokoEvent);
        return null;
    }

    @Override
    public List<ReportingMessage> logScreen(String s, Map<String, String> map) {
        return null;
    }

    @Override
    public void checkForDeepLink() {
        Activity currentActivity = getCurrentActivity().get();
        if (currentActivity == null) {
            return;
        }

        final Intent intent = currentActivity.getIntent();

        RokoLinks.getByVanityLinkCmd(currentActivity, intent, new CallbackVanityLink() {
            @Override
            public void success(final ResponseVanityLink res) {
                try {
                    DeepLinkResult deepLinkResult = new DeepLinkResult()
                            .setLink(intent.getDataString())
                            .setServiceProviderId(getConfiguration().getKitId())
                            .setParameters(new JSONObject(Json.serialize(res.data)));
                    getKitManager().onResult(deepLinkResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(final String error) {
                getKitManager().onError(new DeepLinkError()
                        .setMessage(error)
                        .setServiceProviderId(getConfiguration().getKitId()));

            }
        });
    }

    @Override
    public void setInstallReferrer(Intent intent) {
        RokoLinks.handleDeepLink(intent);
    }

    @Override
    public boolean willHandlePushMessage(Intent intent) {
        return intent != null && intent.getStringExtra(RK_MSG_ID) != null;
    }

    @Override
    public void onPushMessageReceived(final Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bundle.containsKey(RokoPushConstants.EXTRA_OVERLAY_ID)) {
                    PushNotificationHelper.pulNotificationSent(bundle.getString(RokoPushConstants.EXTRA_OVERLAY_ID));
                    PushNotificationHelper.showDefaultNotification(context, bundle);
                } else if (bundle.containsKey(RokoPushConstants.EXTRA_CONVERSATION_ID)) {
                    PushNotificationHelper.showInstabot(bundle.getString(RokoPushConstants.EXTRA_CONVERSATION_ID));
                } else {
                    PushNotificationHelper.showDefaultNotification(context, bundle);
                }
            }
        });
    }

    @Override
    public boolean onPushRegistration(String instanceId, String senderId) {
        Settings preferences = RokoMobi.getSettings();
        preferences.edit().putString(DEVICE_TOKEN, instanceId).apply();
        RokoPush.register(getContext(), instanceId);
        RokoPush.start(senderId);
        return false;
    }


    @Override
    public void setUserAttribute(String name, String value) {
        RokoAccount.setUserCustomProperty(name, value);
    }

    @Override
    public void setUserAttributeList(String s, List<String> list) {
    }

    @Override
    public boolean supportsAttributeLists() {
        return false;
    }

    @Override
    public void setAllUserAttributes(Map<String, String> map, Map<String, List<String>> map1) {
    }

    @Override
    public void removeUserAttribute(String name) {
        RokoAccount.setUserCustomProperty(name, null);
    }

    @Override
    public void setUserIdentity(MParticle.IdentityType identityType, String identityToken) {
        RokoAccount.setUser(identityToken);
    }

    @Override
    public void removeUserIdentity(MParticle.IdentityType identityType) {
        RokoAccount.logout();
    }

    @Override
    public List<ReportingMessage> logout() {
        RokoAccount.logout();
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityCreated(Activity activity, Bundle bundle) {
        Intent intent = activity.getIntent();
        if(PUSH_ACTION.equals(intent.getAction())){
            RokoPush.notificationOpened(intent, new RokoPush.CallbackNotificationOpened() {
                @Override
                public void success(PushData pushData) {

                }
            });
        }
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityStarted(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityResumed(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityPaused(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityStopped(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityDestroyed(Activity activity) {
        return null;
    }
}