package com.rokolabs.mparticle.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mparticle.MParticle;

public class UserActivity extends AppCompatActivity{
    private EditText userEmailField;
    private Button setUserButton;
    private EditText userPropertyNameField;
    private Button setPropertyButton;
    private EditText userPropertyValueField;
    private Button logoutButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        userEmailField = (EditText) findViewById(R.id.user_email);
        setUserButton = (Button) findViewById(R.id.set_user);
        setUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmailField.getText().toString();
                MParticle.getInstance().setUserIdentity(email, MParticle.IdentityType.Email);
            }
        });
        logoutButton = (Button) findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MParticle.getInstance().logout();
            }
        });

        userPropertyNameField = (EditText) findViewById(R.id.user_property_name);
        userPropertyValueField = (EditText) findViewById(R.id.user_property_value);
        setPropertyButton = (Button) findViewById(R.id.set_user_property);
        setPropertyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String propertyName = userPropertyNameField.getText().toString();
                String propertyValue = userPropertyValueField.getText().toString();
                MParticle.getInstance().setUserAttribute(propertyName, propertyValue);
            }
        });
    }
}
