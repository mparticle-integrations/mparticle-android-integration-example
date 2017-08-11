package com.rokolabs.mparticle.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mparticle.DeepLinkError;
import com.mparticle.DeepLinkListener;
import com.mparticle.DeepLinkResult;
import com.mparticle.MParticle;
import com.rokolabs.sdk.analytics.Event;
import com.rokolabs.sdk.analytics.RokoLogger;
import com.rokolabs.sdk.instabot.Instabot;
import com.rokolabs.sdk.tools.RokoTools;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView) findViewById(R.id.app_version)).setText("App: v"+BuildConfig.VERSION_NAME);
        ((TextView) findViewById(R.id.sdk_version)).setText("SDK: v"+ com.rokolabs.sdk.BuildConfig.VERSION_NAME);

        ListView list = (ListView) findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, UserActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, InstabotUIActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, EventsActivity.class));
                        break;
                  }
            }
        });
    }
}
