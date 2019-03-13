package com.mparticle.kits;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;
import java.util.Map;


public class OneTrustKit extends KitIntegration {

    private final static String MP_MOBILE_CONSENT_GROUPS = "mobileConsentGroups";
    private final static String ONETRUST_PREFS = "OT_mP_Mapping";
    private Context m_context;
    
    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        
        // Retrieve mParticle --> OneTrust mapping values
        String mobileMappingValues = settings.get(MP_MOBILE_CONSENT_GROUPS);

        // Save mapping to disk. Will be retrieved by OneTrust Mobile SDK
        saveToDisk(mobileMappingValues);

        return null;
    }


    @Override
    public String getName() {
        //TODO: Replace this with your company name
        return "OneTrust";
    }



    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        //TODO: Disable or enable your SDK when a user opts out.
        //TODO: If your SDK can not be opted out of, return null
        ReportingMessage optOutMessage = new ReportingMessage(this, ReportingMessage.MessageType.OPT_OUT, System.currentTimeMillis(), null);
        return null;
    }
    
     public void saveToDisk(String mappingData){
        Context context = getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.onetrust.consent.sdk", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ONETRUST_PREFS, mappingData);
        editor.apply();
    }
}
