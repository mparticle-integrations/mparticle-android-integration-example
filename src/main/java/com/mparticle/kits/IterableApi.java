package com.mparticle.kits;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David Truong dt@iterable.com.
 */

public class IterableApi {
    private static Pattern deeplinkPattern = Pattern.compile("/a/[A-Za-z0-9]+");

    /**
     * Tracks a click on the uri if it is an iterable link.
     * @param uri the
     * @param onCallback Calls the callback handler with the destination location
     *                   or the original url if it is not a interable link.
     */
    public static void getAndTrackDeeplink(String uri, IterableHelper.IterableActionHandler onCallback) {
        if (uri != null) {
            Matcher m = deeplinkPattern.matcher(uri);
            if (m.find( )) {
                IterableDeeplinkApiRequest request = new IterableDeeplinkApiRequest(uri, onCallback);
                new IterableRequest().execute(request);
            } else {
                onCallback.execute(uri);
            }
        } else {
            onCallback.execute(null);
        }
    }

}
