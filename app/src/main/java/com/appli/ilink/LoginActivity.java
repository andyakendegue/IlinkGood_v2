package com.appli.ilink;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class LoginActivity extends AppCompatActivity implements OnClickListener {
    private Button btnForgotPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private EditText editTextPassword;
    private EditText editTextPhone;
    private TextView linkSignup;
    private boolean loggedIn = false;
    private static String KEY_PHONE = "phone";
    private static String KEY_PASSWORD = "password";
    private static String KEY_TAG = "tag";

    private PhoneNumberUtil util;

    private TelephonyManager tm;
    private String networkCountryISO;
    private String SIMCountryISO;
    private MaterialDialog pDialog;

    private CoordinatorLayout clLoginSimple;
    private View sbView;
    private TextView snacktextView;
    private Snackbar snackbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        this.editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        this.editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        this.linkSignup = (TextView) findViewById(R.id.linkSignup);
        this.buttonLogin = (Button) findViewById(R.id.buttonLogin);
        this.btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
        this.buttonLogin.setOnClickListener(this);
        this.buttonRegister = (Button) findViewById(R.id.buttonRegister);
        this.clLoginSimple = (CoordinatorLayout) findViewById(R.id
                .clLoginSimple);

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

    private void login() throws NumberParseException {
        //Getting values from edit texts
        final String phone = editTextPhone.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String tag = "login";
        Phonenumber.PhoneNumber phoneNumber = null;

        // Debut concatenation numero de phone avec code pays
        if (util == null) {
            util = PhoneNumberUtil.createInstance(getApplicationContext());
        }

        tm =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        networkCountryISO = tm.getNetworkCountryIso();

        phoneNumber = util.parse(phone, networkCountryISO.toUpperCase());
        final String phoneSend = util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);

        // Fin concatenation numero de phone avec code pays


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
                            editor.putString(Config.PHONE_SHARED_PREF, phoneSend);

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
                        Toast.makeText(LoginActivity.this, "Probleme de connexion au serveur : "+error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put(KEY_PHONE, phoneSend);
                params.put(KEY_PASSWORD, password);
                params.put(KEY_TAG, tag);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void onClick(View v) {

        new UserLoginTask().execute();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new MaterialDialog.Builder(LoginActivity.this)
                    .title("Attendez svp!")
                    .content("Vérification de la connexion réseau")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();


        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                if (Util.Operations.isOnline(LoginActivity.this)) {

                    return true;
                } else {
                    hidePDialog();
                    callSnackbar("Pas de connexion internet");


                }

            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {



            try {
                hidePDialog();
                login();
            } catch (NumberParseException e) {
                hidePDialog();
                //e.printStackTrace();
                //
                callSnackbar(e.getMessage());

            }
            // Start Registering
        }

        @Override
        protected void onCancelled() {

        }
    }
    public void callSnackbar(String errorMessage) {
        snackbar = Snackbar.make(clLoginSimple, errorMessage, Snackbar.LENGTH_INDEFINITE);
        sbView = snackbar.getView();
        snacktextView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color

        snacktextView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
