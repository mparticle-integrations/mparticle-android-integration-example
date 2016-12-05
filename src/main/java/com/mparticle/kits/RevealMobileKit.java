package com.mparticle.kits;

import android.content.Context;

import com.mparticle.MParticle;
import com.stepleaderdigital.reveal.Reveal;

import java.util.List;
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
public class RevealMobileKit extends KitIntegration {

    public Reveal revealSDK = null;

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        //Create Reveal configuration.
        this.revealSDK = Reveal.getInstance();
        String apiKey = settings.get( "apiKey" );

        if (MParticle.getInstance().getEnvironment().equals(MParticle.Environment.Development)) {
            this.revealSDK.setDebug( true );
        }

        if ( apiKey != null ) {
            this.revealSDK.setAPIKey(apiKey);
            this.revealSDK.setServiceType( Reveal.ServiceType.PRODUCTION );
        }
        else {
            throw new IllegalArgumentException( "No API Key provided");
        }

        return null;
    }


    @Override
    public String getName() {
        return "RevealMobile";
    }

    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        return null;
    }
}