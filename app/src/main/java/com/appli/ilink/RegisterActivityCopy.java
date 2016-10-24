package com.appli.ilink;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivityCopy extends AppCompatActivity implements View.OnClickListener, LocationListener {

    // LISTE PAYS
    private String e_pays = "Gabon";
    private Spinner ListPays = null;
    private String[] paysItem;

    // LISTE RESEAU
    private String e_reseau;
    private Spinner ListReseau = null;
    private String[] reseauItem;

    private static final String REGISTER_URL = "https://ilink-app.com/app/";

    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_NETWORK = "network";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_MEMBER_CODE = "member";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_ACTIVE = "active";
    public static final String KEY_TAG = "tag";


    private EditText firstname;
    private EditText lastname;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordRepeat;
    private EditText editTextPhone;
    private EditText editTextMember;
    private TextView locatedText;
    private TextView latitudeText;
    private TextView longitudeText;
    private ImageView locatedImage;

    private Button buttonRegister;
    private LocationManager locationManager;
    private String latitude = null;
    private String longitude = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPasswordRepeat = (EditText) findViewById(R.id.editTextPasswordRepeat);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextMember = (EditText) findViewById(R.id.editTextMemberCode);
        locatedText = (TextView) findViewById(R.id.textLocated);
        latitudeText = (TextView) findViewById(R.id.textLatitude);
        longitudeText = (TextView) findViewById(R.id.textLongitude);
        locatedImage = (ImageView) findViewById(R.id.imageLocated);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);



        buttonRegister.setOnClickListener(this);

        // Pays

        ListPays = (Spinner) findViewById(R.id.CountryCode);
        List<String> listePays = new ArrayList<String>();
        paysItem = getResources().getStringArray(R.array.country_code);
        final int paysLength = paysItem.length;

        for (int i = 0 ; i < paysLength ; i++) {
            listePays.add(paysItem[i]);

        }
        ArrayAdapter<String> paysAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listePays);
        //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
        paysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ListPays.setAdapter(paysAdapter);
        ListPays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                e_pays = parent.getItemAtPosition(position).toString();
                if(e_pays.equals("Burkina-Faso")){
                    reseauItem = getResources().getStringArray(R.array.network_burkina);

                } else if (e_pays.equals("Cameroun")) {
                    reseauItem = getResources().getStringArray(R.array.network_cameroun);

                } else if (e_pays.equals("France")) {
                    reseauItem = getResources().getStringArray(R.array.network_france);

                } else if (e_pays.equals("Gabon")){
                    reseauItem = getResources().getStringArray(R.array.network_gabon);
                }
                List<String> listReseau = new ArrayList<String>();
                final int reseauLength = reseauItem.length;

                for (int i = 0; i < reseauLength; i++) {
                    listReseau.add(reseauItem[i]);

                }
                ArrayAdapter<String> reseauAdapter = new ArrayAdapter<String>(RegisterActivityCopy.this, android.R.layout.simple_spinner_item, listReseau);
                //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
                reseauAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ListReseau.setAdapter(reseauAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                e_pays = paysItem[0].toString();
            }

        });

        // Reseau


        ListReseau = (Spinner) findViewById(R.id.Network);
        List<String> listReseau = new ArrayList<String>();
        reseauItem = getResources().getStringArray(R.array.network_gabon);
        final int reseauLength = reseauItem.length;

        for (int i = 0; i < reseauLength; i++) {
            listReseau.add(reseauItem[i]);

        }
        ArrayAdapter<String> reseauAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listReseau);
        //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
        reseauAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ListReseau.setAdapter(reseauAdapter);
        ListReseau.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                e_reseau = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                e_reseau = reseauItem[0].toString();

            }

        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public void onClick(View v) {
        if (v == buttonRegister) {
            registerUser();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

        //Obtention de la référence du service
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //Si le GPS est disponible, on s'y abonne
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            abonnementGPS();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();

        //On appelle la méthode pour se désabonner
        desabonnementGPS();
    }

    /**
     * Méthode permettant de s'abonner à la localisation par GPS.
     */
    public void abonnementGPS() {
        //On s'abonne
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
    }

    /**
     * Méthode permettant de se désabonner de la localisation par GPS.
     */
    public void desabonnementGPS() {
        //Si le GPS est disponible, on s'y abonne
        locationManager.removeUpdates(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLocationChanged(final Location location) {
        //Fetching email from shared preferences
        SharedPreferences sharedPreferences = this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");
        //On affiche dans un Toat la nouvelle Localisation
        final StringBuilder msg = new StringBuilder("latitude : ");
        msg.append(location.getLatitude());
        msg.append("; longitude : ");
        msg.append(location.getLongitude());

        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show();

        if (latitude != "0" && longitude != "0") {
            locatedText.setText("Vous avez été localisé");
            locatedImage.setImageResource(R.drawable.gps_located);
            latitudeText.setText(latitude);
            longitudeText.setText(longitude);
        }


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProviderDisabled(final String provider) {
        //Si le GPS est désactivé on se désabonne
        if ("gps".equals(provider)) {
            desabonnementGPS();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProviderEnabled(final String provider) {
        //Si le GPS est activé on s'abonne
        if ("gps".equals(provider)) {
            abonnementGPS();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {
    }


    private void registerUser() {
        final String prenom = firstname.getText().toString().trim();
        final String nom = lastname.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();

        final String phone = editTextPhone.getText().toString().trim();
        final String member_code = editTextMember.getText().toString().trim();

        // Test values
        if ((!lastname.getText().toString().equals("")) && (!editTextPassword.getText().toString().equals("")) && (!firstname.getText().toString().equals("")) && (!editTextPhone.getText().toString().equals("")) && (!editTextEmail.getText().toString().equals("")) && (!editTextMember.getText().toString().equals(""))) {
            if (editTextPhone.getText().toString().length() > 4) {


                if (editTextPassword.getText().toString().length() == editTextPasswordRepeat.getText().toString().length()) {

                    if (latitude != null && longitude != null) {


                        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();
                                        Toast.makeText(RegisterActivityCopy.this, "Enregistrement reussi!", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(RegisterActivityCopy.this, LoginActivity.class);
                                        startActivity(i);

                                        finish();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(RegisterActivityCopy.this, "Impossible de se connecter au serveur : " + error.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put(KEY_FIRSTNAME, prenom);
                                params.put(KEY_LASTNAME, nom);
                                params.put(KEY_PASSWORD, password);
                                params.put(KEY_EMAIL, email);
                                params.put(KEY_PHONE, phone);
                                params.put(KEY_NETWORK, e_reseau);
                                params.put(KEY_MEMBER_CODE, member_code);
                                params.put(KEY_LATITUDE, latitude);
                                params.put(KEY_LONGITUDE, longitude);
                                params.put(KEY_COUNTRY, e_pays);
                                params.put(KEY_CATEGORY, "utilisateur");
                                params.put(KEY_ACTIVE, "non");
                                params.put(KEY_TAG, "register");
                                return params;
                            }


                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(this);
                        requestQueue.add(stringRequest);

                    } else {
                        Toast.makeText(RegisterActivityCopy.this, "Pas encore localise!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Les mots de passe entres ne correspondent pas. Essayez de nouveau!", Toast.LENGTH_SHORT).show();

                }

            } else {
                Toast.makeText(getApplicationContext(),
                        "Le pseudonyme doit être au minimum de 5 caractères", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Un ou plusieurs champs sont vides", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Register Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.appli.ilink/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Register Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.appli.ilink/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
