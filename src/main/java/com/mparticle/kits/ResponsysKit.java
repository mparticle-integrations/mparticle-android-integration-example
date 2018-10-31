package com.mparticle.kits;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.JobIntentService;

import com.mparticle.MPEvent;
import com.mparticle.MParticle;
import com.mparticle.MParticle.EventType;
import com.mparticle.commerce.CommerceEvent;
import com.mparticle.commerce.Product;
import com.mparticle.identity.MParticleUser;
import com.pushio.manager.PIOLogger;
import com.pushio.manager.PushIOConstants;
import com.pushio.manager.PushIOGCMIntentService;
import com.pushio.manager.PushIOManager;
import com.pushio.manager.exception.ValidationException;
import com.pushio.manager.preferences.PushIOPreference;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mparticle.MParticle.IdentityType.CustomerId;

public class ResponsysKit extends KitIntegration implements KitIntegration.PushListener,
        KitIntegration.EventListener,
        KitIntegration.CommerceListener,
        KitIntegration.IdentityListener {

    public static final String CUSTOM_FLAG_IAM = "Responsys.Custom.iam";
    public static final String ENGAGEMENT_METRIC_PREMIUM_CONTENT =  "ResponsysEngagementTypePremium";
    public static final String ENGAGEMENT_METRIC_INAPP_PURCHASE = "ResponsysEngagementTypePurchase";
    public static final String ENGAGEMENT_METRIC_OTHER = "ResponsysEngagementTypeOther";
    public static final String ENGAGEMENT_METRIC_SOCIAL = "ResponsysEngagementTypeSocial";

    private PushIOManager mPushIOManager;

    @Override
    public Object getInstance() {
        return mPushIOManager;
    }

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        PIOLogger.d("Responsys Kit detected");
        PIOLogger.v("RK oKC");

        final String apiKey = settings.get("apiKey");
        if (KitUtils.isEmpty(apiKey)) {
            throw new IllegalArgumentException("Responsys API Key is empty");
        }

        final String accountToken = settings.get("accountToken");
        if (KitUtils.isEmpty(accountToken)) {
            throw new IllegalArgumentException("Responsys Account Token is empty");
        }

        final String conversionUrl = settings.get("conversionUrl");
        if (KitUtils.isEmpty(conversionUrl)) {
            throw new IllegalArgumentException("Responsys Conversion Url is empty");
        }

        final String riAppId = settings.get("riAppId");
        if (KitUtils.isEmpty(riAppId)) {
            throw new IllegalArgumentException("Responsys RI App Id is empty");
        }

        String senderId = settings.get("senderId");
        if (KitUtils.isEmpty(senderId)) {
            throw new IllegalArgumentException("GCM/FCM Sender ID is empty");
        }

        mPushIOManager = PushIOManager.getInstance(context);
        if (mPushIOManager == null) {
            throw new IllegalStateException("Responsys SDK initialization failed");
        }

        PIOLogger.v("RK oKC Configuring Kit with...");
        PIOLogger.v("RK oKC apiKey: " + apiKey);
        PIOLogger.v("RK oKC accountToken: " + accountToken);
        PIOLogger.v("RK oKC senderId: " + senderId);
        PIOLogger.v("RK oKC conversionUrl: " + conversionUrl);
        PIOLogger.v("RK oKC riAppId: " + riAppId);

        boolean isSDKConfigured = mPushIOManager.configure(apiKey, accountToken, senderId, conversionUrl, riAppId);
        if (!isSDKConfigured) {
            throw new IllegalStateException("Responsys SDK configuration failed");
        }

        return null;
    }


    @Override
    public String getName() {
        return "Responsys";
    }

    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        return null;
    }

    @Override
    public List<ReportingMessage> logLtvIncrease(BigDecimal bigDecimal, BigDecimal bigDecimal1, String s, Map<String, String> map) {
        return null;
    }

    @Override
    public List<ReportingMessage> logEvent(CommerceEvent commerceEvent) {
        if (commerceEvent == null) {
            return null;
        }

        final String productAction = commerceEvent.getProductAction();
        if (KitUtils.isEmpty(productAction)) {
            return null;
        }

        PIOLogger.v("RK lE cevent pA: " + productAction);

        String responsysEvent = null;

        switch (productAction) {
            case Product.ADD_TO_CART:
                responsysEvent = "$AddedItemToCart";
                break;
            case Product.REMOVE_FROM_CART:
                responsysEvent = "$RemovedItemFromCart";
                break;
            case Product.PURCHASE:
                responsysEvent = "$PurchasedCart";
                break;
            case Product.DETAIL:
                responsysEvent = "$Browsed";
                break;
            case Product.CHECKOUT:
                responsysEvent = "$UpdatedStageOfCart";
                break;

        }

        if (!KitUtils.isEmpty(responsysEvent)) {
            for (Product product : commerceEvent.getProducts()) {
                Map<String, Object> eventProperties = new HashMap<>();
                eventProperties.put("Pid", product.getSku());
                eventProperties.put("Pc", product.getCategory());

                mPushIOManager.trackEvent(responsysEvent, eventProperties);

                if (productAction.equalsIgnoreCase(Product.PURCHASE)) {
                    mPushIOManager.trackEngagement(PushIOManager.PUSHIO_ENGAGEMENT_METRIC_PURCHASE);
                }
            }

            List<ReportingMessage> reportingMessages = new ArrayList<>();
            reportingMessages.add(ReportingMessage.fromEvent(this, commerceEvent));
            return reportingMessages;
        }

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
        if (mpEvent == null) {
            return null;
        }

        List<ReportingMessage> reportingMessages = new ArrayList<>();

        final Map<String, List<String>> customFlags = mpEvent.getCustomFlags();
        if (customFlags != null && customFlags.containsKey(CUSTOM_FLAG_IAM)) {
            mPushIOManager.trackEvent(mpEvent.getEventName());
            reportingMessages.add(ReportingMessage.fromEvent(this, mpEvent));
            return reportingMessages;
        }

        final EventType eventType = mpEvent.getEventType();
        final String eventName = mpEvent.getEventName();

        PIOLogger.v("RK lE event type: " + eventType);

        switch (eventType) {
            case Search:
                mPushIOManager.trackEvent("$Searched");
                reportingMessages.add(ReportingMessage.fromEvent(this, mpEvent));
                break;
            case Social:
                if(!KitUtils.isEmpty(eventName)) {
                    if(eventName.equalsIgnoreCase(ENGAGEMENT_METRIC_SOCIAL)) {
                        mPushIOManager.trackEngagement(PushIOManager.PUSHIO_ENGAGEMENT_METRIC_SOCIAL);
                        reportingMessages.add(ReportingMessage.fromEvent(this, mpEvent));
                    }
                }
                break;
            case UserPreference:
                Map<String, String> eventInfo = mpEvent.getInfo();
                if (eventInfo != null) {
                    for (Map.Entry<String, String> entry : eventInfo.entrySet()) {
                        try {
                            mPushIOManager.declarePreference(entry.getKey(), entry.getKey(), PushIOPreference.Type.STRING);
                            mPushIOManager.setPreference(entry.getKey(), entry.getValue());
                        } catch (ValidationException e) {
                            PIOLogger.v("RK lE ValidationException: " + e.getMessage());
                        }
                    }
                }
                break;
            case Transaction:
                if(!KitUtils.isEmpty(eventName)) {
                    if(eventName.equalsIgnoreCase(ENGAGEMENT_METRIC_INAPP_PURCHASE)) {
                        mPushIOManager.trackEngagement(PushIOManager.PUSHIO_ENGAGEMENT_METRIC_INAPP_PURCHASE);
                        reportingMessages.add(ReportingMessage.fromEvent(this, mpEvent));
                    }else if(eventName.equalsIgnoreCase(ENGAGEMENT_METRIC_PREMIUM_CONTENT)){
                        mPushIOManager.trackEngagement(PushIOManager.PUSHIO_ENGAGEMENT_METRIC_PREMIUM_CONTENT);
                        reportingMessages.add(ReportingMessage.fromEvent(this, mpEvent));
                    }
                }
                break;
            case Other:
                if(!KitUtils.isEmpty(eventName)) {
                    if (eventName.equalsIgnoreCase(ENGAGEMENT_METRIC_OTHER)) {
                        mPushIOManager.trackEngagement(PushIOManager.PUSHIO_ENGAGEMENT_METRIC_OTHER);
                        reportingMessages.add(ReportingMessage.fromEvent(this, mpEvent));
                    }
                }
                break;
        }

        return (reportingMessages.isEmpty() ? null : reportingMessages);
    }

    @Override
    public List<ReportingMessage> logScreen(String s, Map<String, String> map) {
        return null;
    }

    @Override
    public boolean willHandlePushMessage(Intent intent) {
        PIOLogger.v("RK wHPM");
        return isResponsysPush(intent);
    }

    @Override
    public void onPushMessageReceived(Context context, Intent intent) {
        PIOLogger.v("RK oPMR");
        Intent newIntent = new Intent(intent);
        newIntent.setClassName(context, PushIOGCMIntentService.class.getName());
        JobIntentService.enqueueWork(context, PushIOGCMIntentService.class,
                PushIOConstants.PIO_GCM_INTENT_SERVICE_JOB_ID,
                newIntent);
    }

    @Override
    public boolean onPushRegistration(String instanceId, String senderId) {
        PIOLogger.v("RK oPR Instance ID: " + instanceId + ", Sender ID: " + senderId);
        mPushIOManager.setDeviceToken(instanceId);
        mPushIOManager.registerApp();
        return true;
    }

    @Override
    public void onIdentifyCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
    }

    @Override
    public void onLoginCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
        PIOLogger.v("RK oLiC");
        registerUserId(mParticleUser);
    }

    @Override
    public void onLogoutCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
        PIOLogger.v("RK oLoC");
        if (mPushIOManager != null) {
            mPushIOManager.unregisterUserId();
        }
    }

    @Override
    public void onModifyCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
    }

    @Override
    public void onUserIdentified(MParticleUser mParticleUser) {
    }

    private void registerUserId(MParticleUser mParticleUser) {
        if (mPushIOManager != null) {
            final String userId = getUserId(mParticleUser.getUserIdentities());
            if (!KitUtils.isEmpty(userId)) {
                mPushIOManager.registerUserId(userId);
            }
        }
    }

    private String getUserId(Map<MParticle.IdentityType, String> identities) {
        String userId = null;
        if (identities != null && identities.containsKey(CustomerId)) {
            userId = identities.get(CustomerId);
        }
        return userId;
    }

    private boolean isResponsysPush(Intent intent) {
        return (intent != null && intent.hasExtra("ei") &&
                !KitUtils.isEmpty(intent.getStringExtra("ei")));
    }
}