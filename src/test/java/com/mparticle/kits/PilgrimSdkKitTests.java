package com.mparticle.kits;


import android.content.Context;

import com.foursquare.pilgrim.PilgrimSdk;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.internal.verification.VerificationModeFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PilgrimSdkKitTests {

    private KitIntegration getKit() {
        return new PilgrimSdkKit();
    }

    @Test
    public void testGetName() {
        String name = getKit().getName();
        assertTrue(name != null && name.length() > 0);
    }

    /**
     * Kit *should* throw an exception when they're initialized with the wrong settings.
     */
    @Test
    public void testOnKitCreate() {
        Exception e = null;
        try {
            KitIntegration kit = getKit();
            Map<String, String> settings = new HashMap<>();
            settings.put("fake setting", "fake");
            kit.onKitCreate(settings, Mockito.mock(Context.class));
        } catch (Exception ex) {
            e = ex;
        }
        assertNotNull(e);
    }

    @Test
    public void testClassName() {
        KitIntegrationFactory factory = new KitIntegrationFactory();
        Map<Integer, String> integrations = factory.getKnownIntegrations();
        String className = getKit().getClass().getName();
        for (Map.Entry<Integer, String> entry : integrations.entrySet()) {
            if (entry.getValue().equals(className)) {
                return;
            }
        }
        fail(className + " not found as a known integration.");
    }

    @Test
    public void testCorrectInitialization() {
        KitIntegration kit = getKit();
        Map<String, String> settings = new HashMap<>();
        settings.put(PilgrimSdkKit.SDK_KEY, "MyKey");
        settings.put(PilgrimSdkKit.SDK_SECRET, "MySuperSecretSecret");
        try {
            List<ReportingMessage> messageList = kit.onKitCreate(settings, Mockito.mock(Context.class));
            // We did pass one
            assertTrue("No messages were returned when initializing app", messageList.size() > 0);
            boolean appStateMessageFound = false;
            for (int i = 0; i < messageList.size(); i++) {
                ReportingMessage msg = messageList.get(i);
                if (msg.getEventTypeString().equals(ReportingMessage.MessageType.APP_STATE_TRANSITION)) {
                    appStateMessageFound = true;
                }
            }
            assertTrue("Could not find APP_STATE_STRANSITION message", appStateMessageFound);
        } catch (Exception e) {
            fail(e.getCause().toString());
        }
    }
}