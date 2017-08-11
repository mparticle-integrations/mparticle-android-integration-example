package com.rokolabs.mparticle.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import com.rokolabs.sdk.json.Json;
import com.rokolabs.sdk.push.PushData;
import com.rokolabs.sdk.push.RokoPush;
import com.rokolabs.sdk.tools.ThreadUtils;

/**
 * Created by sobolev on 8/10/17.
 */

public class AppPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_page_activity);
        Bundle bundle = getIntent().getExtras();

        getIntent().getStringExtra("message");
        getIntent().getStringExtra("promoCode");
        getIntent().getStringExtra("promoCampaignId");
        getIntent().getStringExtra("payload");

        ((TextView) findViewById(R.id.text)).setText(
                "Message: " + bundle.getString("message") + "\n"+
                "Promo code: " + bundle.getString("promoCode", "none") + "\n"+
                "Promo campaign id: " + bundle.getString("promoCampaignId", "none") + "\n"+
                "Payload: " + bundle.getString("payload") + "\n"
        );
    }
}
