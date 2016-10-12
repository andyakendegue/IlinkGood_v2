package com.appli.ilink;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.appli.ilink.app.AppController;
import com.appli.ilink.lib.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TamponGeolocatedActivity extends AppCompatActivity {
    public static final String KEY_EMAIL = "email";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_TAG = "tag";
    private String TAG;
    public Button btnRetry;

    public TamponGeolocatedActivity() {
        this.TAG = KEY_TAG;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_tampon);
        this.btnRetry = (Button) findViewById(R.id.button_retry);
        this.btnRetry.setVisibility(View.INVISIBLE);
        this.btnRetry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    user();
                    localusers();
                } catch (JSONException e) {
                    Toast.makeText(TamponGeolocatedActivity.this, "impossible de recuperer vos informations", Toast.LENGTH_LONG).show();
                }
            }
        });

        try {
            user();
            localusers();

        } catch (JSONException e) {
            //e.printStackTrace();

            Toast.makeText(TamponGeolocatedActivity.this, "impossible de recuperer vos informations", Toast.LENGTH_LONG).show();

        }

    }





    private void user() throws JSONException {
        String tag_json_arry = "json_array_req";
        String url = "http://ilink-app.com/app/select/users.php";
        final SharedPreferences sharedPreferences = this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String phone = sharedPreferences.getString(Config.PHONE_SHARED_PREF, "Not Available");
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_PHONE, phone);
        params.put(KEY_TAG, "getuser");
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        //Toast.makeText(TamponActivity.this, "Nice!", Toast.LENGTH_LONG).show();
                        // Toast.makeText(getActivity(), "Localisation a jour!", Toast.LENGTH_LONG).show();



                        try {
                            JSONObject obj = response.getJSONObject(0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Config.EMAIL_SHARED_PREF, obj.getString("email"));
                            editor.putString(Config.LASTNAME_SHARED_PREF, obj.getString("lastname"));
                            editor.putString(Config.FIRSTNAME_SHARED_PREF, obj.getString("firstname"));
                            editor.putString(Config.PHONE_SHARED_PREF, obj.getString("phone"));
                            editor.putString(Config.CATEGORY_SHARED_PREF, obj.getString("category"));
                            editor.putString(Config.COUNTRY_CODE_SHARED_PREF, obj.getString("country_code"));
                            editor.putString(Config.NETWORK_SHARED_PREF, obj.getString("network"));
                            editor.putString(Config.MEMBER_CODE_SHARED_PREF, obj.getString("member_code"));
                            editor.putString(Config.VALIDATION_SHARED_PREF, obj.getString("active"));
                            editor.putString(Config.VALIDATION_CODE_SHARED_PREF, obj.getString("validation_code"));
                            editor.putString(Config.LATITUDE_SHARED_PREF, obj.getString("latitude"));
                            editor.putString(Config.LONGITUDE_SHARED_PREF, obj.getString("longitude"));
                            editor.putString(Config.BALANCE_SHARED_PREF, obj.getString("balance"));
                            editor.commit();
                            if (obj.getString("active").equalsIgnoreCase("non")) {
                                TamponGeolocatedActivity.this.startActivity(new Intent(TamponGeolocatedActivity.this, ActivateGeolocatedActivity.class));
                                TamponGeolocatedActivity.this.finish();
                            } else if (obj.getString("category").equalsIgnoreCase("super")) {
                                TamponGeolocatedActivity.this.startActivity(new Intent(TamponGeolocatedActivity.this, SuperviseurHomeActivity.class));
                                TamponGeolocatedActivity.this.finish();
                            } else if (obj.getString("category").equalsIgnoreCase("hyper")) {
                                TamponGeolocatedActivity.this.startActivity(new Intent(TamponGeolocatedActivity.this, HyperviseurHomeActivity.class));
                                TamponGeolocatedActivity.this.finish();
                            } else if (obj.getString("category").equalsIgnoreCase("geolocated")) {
                                TamponGeolocatedActivity.this.startActivity(new Intent(TamponGeolocatedActivity.this, HomeActivity.class));
                                TamponGeolocatedActivity.this.finish();
                            } else {
                                Toast.makeText(TamponGeolocatedActivity.this, "Impossible de vous connecter. Veuillez redemarrer l'application", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(TamponGeolocatedActivity.this, "Impossible de recuperer vos donnees", Toast.LENGTH_LONG).show();
                            TamponGeolocatedActivity.this.btnRetry.setVisibility(View.VISIBLE);
                        }


                    }
                },


                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(TamponGeolocatedActivity.this, "Impossible de se connecter au serveur", Toast.LENGTH_LONG).show();
                        TamponGeolocatedActivity.this.btnRetry.setVisibility(View.VISIBLE);
                    }
                }




        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(userRequest);

        requestQueue.add(jsObjRequest);

    }

    private void localusers() {
        String tag_json_arry = "json_array_req";




        String url = "http://ilink-app.com/app/select/locations.php";
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d(TamponGeolocatedActivity.this.TAG, response.toString());
                        DatabaseHandler db = new DatabaseHandler(TamponGeolocatedActivity.this);
                        db.resetTables();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                db.addUsers(obj.getString("firstname"), obj.getString("lastname"), obj.getString("email"), obj.getString("phone"), obj.getString("country_code"), obj.getString("network"), obj.getString("member_code"), obj.getString("code_parrain"), obj.getString("category"), obj.getString("balance"), obj.getString("latitude"), obj.getString("longitude"), obj.getString("mbre_reseau"), obj.getString("mbre_ss_reseau"), obj.getString("validation_code"), obj.getString("active"));
                            } catch (JSONException e) {
                                Toast.makeText(TamponGeolocatedActivity.this, "Impossible de recuperer les marqueurs : " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Erreur : " + error.getMessage());


            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq, tag_json_arry);

    }


}
