package com.rokolabs.mparticle.sample;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mparticle.MParticle;

public class EventsActivity extends AppCompatActivity {

    private EditText eventNameField;
    private Button sendEventButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_activity);

        eventNameField = (EditText) findViewById(R.id.event_name);
        sendEventButton = (Button) findViewById(R.id.send_event);
        sendEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = eventNameField.getText().toString();
                MParticle.getInstance().logEvent(eventName, MParticle.EventType.Other);
            }
        });
    }
}
