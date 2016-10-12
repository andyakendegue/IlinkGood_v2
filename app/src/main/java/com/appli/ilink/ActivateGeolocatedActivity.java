package com.appli.ilink;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class ActivateGeolocatedActivity extends AppCompatActivity {
    public static final String KEY_CATEGORY = "categorie";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_MEMBER_CODE = "code_membre";
    public static final String KEY_NOMBRE_CODES = "nbre_code";
    public static final String KEY_NOMBRE_CODES_SUPERVISEUR = "nbre_code_superviseur";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_TAG = "tag";
    public static final String KEY_VALIDATE = "validate";
    Button btnValidate;
    public String category;
    String code_membre;
    EditText editTextNombreGeo;
    EditText editTextNombreMembres;
    EditText editTextValidation;
    String nbre_codes;
    String nbre_codes_geo;
    TextView textNombreGeo;
    TextView textNombreMembres;
    String validation;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView((int) R.layout.activity_activate);
        final SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, 0);
        final String validation_code = sharedPreferences.getString(Config.VALIDATION_CODE_SHARED_PREF, "Not Available");
        this.category = sharedPreferences.getString(RegisterSimpleActivity.KEY_CATEGORY, "Not Available");
        this.code_membre = sharedPreferences.getString(RegisterSimpleActivity.KEY_MEMBER_CODE, "Not Available");
        this.editTextValidation = (EditText) findViewById(R.id.editTextValidation);
        this.editTextNombreMembres = (EditText) findViewById(R.id.editTextNombreMembres);
        this.editTextNombreGeo = (EditText) findViewById(R.id.editTextNombreGeo);
        this.textNombreGeo = (TextView) findViewById(R.id.textNombreGeo);
        this.textNombreMembres = (TextView) findViewById(R.id.textNombreMembres);
        if (this.category.equalsIgnoreCase("geolocated") || this.category.equalsIgnoreCase("super")) {
            this.textNombreMembres.setVisibility(View.INVISIBLE);
            this.textNombreMembres.setHeight(0);
            this.editTextNombreMembres.setVisibility(View.INVISIBLE);
            this.editTextNombreMembres.setHeight(0);
            this.textNombreGeo.setVisibility(View.INVISIBLE);
            this.textNombreGeo.setHeight(0);
            this.editTextNombreGeo.setVisibility(View.INVISIBLE);
            this.editTextNombreGeo.setHeight(0);
        } else {
            this.textNombreMembres.setVisibility(View.VISIBLE);
            this.editTextNombreMembres.setVisibility(View.VISIBLE);
            this.textNombreGeo.setVisibility(View.VISIBLE);
            this.editTextNombreGeo.setVisibility(View.VISIBLE);
        }
        this.btnValidate = (Button) findViewById(R.id.btnValidate);
        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validation = editTextValidation.getText().toString();
                if (validation_code.equalsIgnoreCase(validation)) {

                    if (category.equalsIgnoreCase("geolocated")) {
                        nbre_codes = "0";
                        nbre_codes_geo = "0";
                    } else {
                        nbre_codes = editTextNombreMembres.getText().toString();
                        nbre_codes_geo = editTextNombreGeo.getText().toString();
                    }
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
                    Toast.makeText(ActivateGeolocatedActivity.this, "Code de Validation incorrect", Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    public void validateUser ()throws JSONException {

        String tag_json_arry = "json_array_req";


        String url = "http://ilink-app.com/app/select/validation.php";

        final SharedPreferences sharedPreferences = this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String phone = sharedPreferences.getString(Config.PHONE_SHARED_PREF, "Not Available");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();
                        //Toast.makeText(TamponActivity.this, "Nice!", Toast.LENGTH_LONG).show();
                        // Toast.makeText(getActivity(), "Localisation a jour!", Toast.LENGTH_LONG).show();
                        if (response.equalsIgnoreCase(Config.LOGIN_SUCCESS)) {
                            final SharedPreferences sharedPreferences = ActivateGeolocatedActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            final String category = sharedPreferences.getString(Config.CATEGORY_SHARED_PREF, "Not Available");
                            if (category.equalsIgnoreCase("utilisateur")) {

                                Intent i = new Intent(ActivateGeolocatedActivity.this, MapsActivity.class);
                                startActivity(i);
                                finish();

                            } else if (category.equalsIgnoreCase("super")) {
                                Intent i = new Intent(ActivateGeolocatedActivity.this, SuperviseurHomeActivity.class);
                                startActivity(i);
                                finish();

                            } else if (category.equalsIgnoreCase("hyper")) {
                                Intent i = new Intent(ActivateGeolocatedActivity.this, HyperviseurHomeActivity.class);
                                startActivity(i);
                                finish();

                            } else if (category.equalsIgnoreCase("geolocated")) {
                                Intent i = new Intent(ActivateGeolocatedActivity.this, HomeActivity.class);
                                startActivity(i);
                                finish();

                            } else {
                                Toast.makeText(ActivateGeolocatedActivity.this, "Impossible de vous connecter. Veuillez redemarrer l'application", Toast.LENGTH_LONG).show();
                            }


                        } else {
                            Toast.makeText(ActivateGeolocatedActivity.this, "Impossible de mettre a jour votre compte", Toast.LENGTH_LONG).show();


                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ActivateGeolocatedActivity.this, "Impossible de se connecter au serveur :" + error.toString(), Toast.LENGTH_LONG).show();

                        //nomLabel.setText(error.toString());
                    }
                }) {
            @Override

            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put(ActivateGeolocatedActivity.KEY_PHONE, phone);
                params.put(ActivateGeolocatedActivity.KEY_NOMBRE_CODES, ActivateGeolocatedActivity.this.nbre_codes);
                params.put(ActivateGeolocatedActivity.KEY_NOMBRE_CODES_SUPERVISEUR, ActivateGeolocatedActivity.this.nbre_codes_geo);
                params.put(ActivateGeolocatedActivity.KEY_CATEGORY, ActivateGeolocatedActivity.this.category);
                params.put(ActivateGeolocatedActivity.KEY_MEMBER_CODE, ActivateGeolocatedActivity.this.code_membre);
                params.put(ActivateGeolocatedActivity.KEY_VALIDATE, "oui");
                return params;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(userRequest);

        requestQueue.add(stringRequest);
    }
}
