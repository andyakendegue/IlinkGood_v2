package com.appli.ilink;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements OnClickListener {
    private AppCompatButton btnForgotPassword;
    private AppCompatButton buttonLogin;
    private AppCompatButton buttonRegister;
    private EditText editTextPassword;
    private EditText editTextPhone;
    private TextView linkSignup;
    private boolean loggedIn = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView((int) R.layout.activity_login);
        this.editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        this.editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        this.linkSignup = (TextView) findViewById(R.id.linkSignup);
        this.buttonLogin = (AppCompatButton) findViewById(R.id.buttonLogin);
        this.btnForgotPassword = (AppCompatButton) findViewById(R.id.btnForgotPassword);
        this.buttonLogin.setOnClickListener(this);
        this.buttonRegister = (AppCompatButton) findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Getting values from edit texts
                final String phone = editTextPhone.getText().toString().trim();
                final String password = editTextPassword.getText().toString().trim();
                Intent intent = new Intent(LoginActivity.this, RegisterSimpleActivity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("password",password);
                //Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }
        });
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ForgottenPasswordSimpleActivity.class);
                startActivity(i);
            }
        });
        linkSignup.setVisibility(View.VISIBLE);

        buttonRegister.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);
        String category = sharedPreferences.getString(Config.CATEGORY_SHARED_PREF, "Aucun resultat");

        //If we will get true
        if(loggedIn){
            //We will start the Profile Activity
            //Intent intent = new Intent(LoginActivity.this, TamponActivity.class);
            //startActivity(intent);

            if (category.equalsIgnoreCase("utilisateur")) {

                Intent i = new Intent(LoginActivity.this, TamponActivity.class);
                //Intent i = new Intent(TamponActivity.this, HyperviseurHomeActivity.class);

                startActivity(i);
                finish();

            } else if (category.equalsIgnoreCase("super")) {
                Intent i = new Intent(LoginActivity.this, TamponGeolocatedActivity.class);
                startActivity(i);
                finish();

            } else if (category.equalsIgnoreCase("hyper")) {
                Intent i = new Intent(LoginActivity.this, TamponGeolocatedActivity.class);
                startActivity(i);
                finish();

            } else if (category.equalsIgnoreCase("geolocated")) {
                Intent i = new Intent(LoginActivity.this, TamponGeolocatedActivity.class);
                startActivity(i);
                finish();

            } else {
                Toast.makeText(LoginActivity.this, "Impossible de vous connecter. Veuillez redemarrer l'application", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void login(){
        //Getting values from edit texts
        final String phone = editTextPhone.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String tag = "login";

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        //if(response.equalsIgnoreCase(Config.LOGIN_SUCCESS)){
                        if(response.equalsIgnoreCase(Config.LOGIN_SUCCESS)){

                            //Creating a shared preference
                            SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                            //Creating editor to store values to shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            //Adding values to editor
                            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                            editor.putString(Config.PHONE_SHARED_PREF, phone);

                            //Saving values to editor
                            editor.commit();

                            //Starting profile activity
                            //Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            Intent intent = new Intent(LoginActivity.this, TamponActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            Toast.makeText(LoginActivity.this, "Telephone ou mot de passe invalide "+response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Probleme de connexion au serveur", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Config.KEY_PHONE, phone);
                params.put(Config.KEY_PASSWORD, password);
                params.put(Config.KEY_TAG, tag);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void onClick(View v) {
        login();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
