package com.appli.ilink;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class ActivateActivity extends AppCompatActivity {
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_VALIDATE = "validate";
    public static final String KEY_TAG = "tag";
    EditText editTextValidation;
    String validation;
    Button btnValidate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_activate);
        editTextValidation = (EditText) findViewById(R.id.editTextValidation);
        btnValidate = (Button) findViewById(R.id.btnValidate);

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences sharedPreferences = ActivateActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                final String validation_code = sharedPreferences.getString(Config.VALIDATION_CODE_SHARED_PREF, "Not Available");
                validation = editTextValidation.getText().toString();
                if (validation_code.equalsIgnoreCase(validation)) {

                    //Creating editor to store values to shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    //Adding values to editor

                    editor.putString(Config.VALIDATION_SHARED_PREF, "oui");


                    //Saving values to editor
                    editor.commit();

                    try {
                        validateUser();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(ActivateActivity.this, "Code de Validation incorrect", Toast.LENGTH_LONG).show();

                }

            }
        });

    }



    public void validateUser ()throws JSONException{

        String tag_json_arry = "json_array_req";



        String url = "http://ilink-app.com/app/select/validation_simple.php";

        final SharedPreferences sharedPreferences = this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String phone = sharedPreferences.getString(Config.PHONE_SHARED_PREF, "Not Available");



        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.equalsIgnoreCase(Config.LOGIN_SUCCESS)){
                            final SharedPreferences sharedPreferences = ActivateActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            final String category = sharedPreferences.getString(Config.CATEGORY_SHARED_PREF, "Not Available");
                            if(category.equalsIgnoreCase("utilisateur")){

                                Intent i = new Intent(ActivateActivity.this, MapsActivity.class);
                                startActivity(i);
                                finish();

                            } else if (category.equalsIgnoreCase("super")) {
                                Intent i = new Intent(ActivateActivity.this, SuperviseurHomeActivity.class);
                                startActivity(i);
                                finish();

                            } else if (category.equalsIgnoreCase("hyper")) {
                                Intent i = new Intent(ActivateActivity.this, HyperviseurHomeActivity.class);
                                startActivity(i);
                                finish();

                            } else if (category.equalsIgnoreCase("geolocated")) {
                                Intent i = new Intent(ActivateActivity.this, HomeActivity.class);
                                startActivity(i);
                                finish();

                            } else {
                                Toast.makeText(ActivateActivity.this, "Impossible de vous connecter. Veuillez redemarrer l'application", Toast.LENGTH_LONG).show();
                            }


                        } else {
                            Toast.makeText(ActivateActivity.this, "Impossible de mettre a jour votre compte", Toast.LENGTH_LONG).show();


                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ActivateActivity.this,"Impossible de se connecter au serveur :" +error.toString(), Toast.LENGTH_LONG).show();

                        //nomLabel.setText(error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_PHONE, phone);
                params.put(KEY_VALIDATE, "oui");
                return params;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
