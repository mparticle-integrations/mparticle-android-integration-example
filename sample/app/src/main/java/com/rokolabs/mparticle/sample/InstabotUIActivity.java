package com.rokolabs.mparticle.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mparticle.MParticle;
import com.mparticle.kits.RokoMobiKit;
import com.mparticle.kits.RokoMobiProvider;

/**
 * Created by sobolev on 7/31/17.
 */

public class InstabotUIActivity extends AppCompatActivity {
    private EditText conversationIdField;
    private Button openInstabotButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instabot_ui_activity);

        conversationIdField = (EditText) findViewById(R.id.conversation_id);
        openInstabotButton = (Button) findViewById(R.id.open_instabot);
        openInstabotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String conversationId = conversationIdField.getText().toString();
                ((RokoMobiProvider) MParticle.getInstance().getKitInstance(MParticle.ServiceProviders.ROKOMOBI)).instabot().show(conversationId);
            }
        });
    }
}
