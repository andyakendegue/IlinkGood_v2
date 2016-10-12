package com.appli.ilink;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ModifyProfileActivity extends AppCompatActivity {
    public TextView emailLabel;
    public TextView nomLabel;
    public TextView phoneLabel;
    public TextView prenomLabel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_modify_profile);
        this.nomLabel = (TextView) findViewById(R.id.lastnameModifyAccountView);
        this.prenomLabel = (TextView) findViewById(R.id.firstnameModifyAccountView);
        this.phoneLabel = (TextView) findViewById(R.id.phoneModifyAccountView);
        this.emailLabel = (TextView) findViewById(R.id.emailModifyAccountView);
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, 0);
        String email = sharedPreferences.getString(TamponGeolocatedActivity.KEY_EMAIL, "Not Available");
        String lastname = sharedPreferences.getString(RegisterSimpleActivity.KEY_LASTNAME, "Not Available");
        String firstname = sharedPreferences.getString(RegisterSimpleActivity.KEY_FIRSTNAME, "Not Available");
        String phone = sharedPreferences.getString(TamponGeolocatedActivity.KEY_PHONE, "Not Available");
        this.nomLabel.setText(lastname);
        this.prenomLabel.setText(firstname);
        this.phoneLabel.setText(phone);
        this.emailLabel.setText(email);
    }
}
