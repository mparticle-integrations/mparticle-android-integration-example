package com.mparticle.kits;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Async task to handle sending data to the Iterable server
 * Created by David Truong dt@iterable.com
 */
class IterableRequest extends AsyncTask<IterableDeeplinkApiRequest, Void, String> {
    static final String TAG = "IterableRequest";

    static final String LOCATION_HEADER_FIELD    = "Location";

    static final int DEFAULT_TIMEOUT_MS = 1000;   //1 seconds
    static final long RETRY_DELAY_MS = 2000;      //2 seconds
    static final int MAX_RETRY_COUNT = 5;

    int retryCount = 0;
    IterableDeeplinkApiRequest iterableApiRequest;
    boolean retryRequest;

    /**
     * Sends the given request to Iterable using a HttpUserConnection
     * Reference - http://developer.android.com/reference/java/net/HttpURLConnection.html
     * @param params
     * @return
     */
    protected String doInBackground(IterableDeeplinkApiRequest... params) {
        if (params != null && params.length > 0) {
            iterableApiRequest = params[0];
        }

        //retry immediately then retry with backoff
        if (retryCount > 2) {
            try {
                Thread.sleep(RETRY_DELAY_MS * retryCount);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String requestResult = null;
        if (iterableApiRequest != null) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(iterableApiRequest.resourcePath);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(DEFAULT_TIMEOUT_MS);
                urlConnection.setInstanceFollowRedirects(false);

                int responseCode = urlConnection.getResponseCode();
                if (responseCode >= 300 && responseCode < 400) {
                    String newUrl = urlConnection.getHeaderField(LOCATION_HEADER_FIELD);
                    requestResult = newUrl;
                } else if (responseCode >= 500) {
                    retryRequest = true;
                } else {
                    //pass back original url
                    requestResult = url.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
        return requestResult;
    }

    @Override
    protected void onPostExecute(String s) {
        if (retryRequest && retryCount <= MAX_RETRY_COUNT) {
            IterableRequest request = new IterableRequest();
            request.setRetryCount(retryCount + 1);
            request.execute(iterableApiRequest);
        } else if (iterableApiRequest.callback != null) {
            iterableApiRequest.callback.execute(s);
        }
        super.onPostExecute(s);
    }

    protected void setRetryCount(int count) {
        retryCount = count;
    }

}

/**
 *  Iterable Request object
 */
class IterableDeeplinkApiRequest {
    String resourcePath = "";

    IterableHelper.IterableActionHandler callback;

    public IterableDeeplinkApiRequest(String resourcePath, IterableHelper.IterableActionHandler callback){
        this.resourcePath = resourcePath;
        this.callback = callback;
    }
}

