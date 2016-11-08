package com.appli.ilink;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appli.ilink.app.AppController;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wearable.MessageApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class RegisterActivity extends AppCompatActivity implements OnClickListener, LocationListener, ConnectionCallbacks, OnConnectionFailedListener {

    public static final String KEY_CATEGORY = "category";
    public static final String KEY_COUNTRY = "country_code";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_MEMBER_CODE = "member";
    public static final String KEY_NETWORK = "network";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_TAG = "tag";
    public static final String KEY_ACTIVE = "active";
    private static final String REGISTER_URL = "https://ilink-app.com/app/";
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private TextView ListPays;
    private TextView titleLayout;
    private Spinner ListReseau;
    private Button buttonRegister;
    private String e_pays;
    private String e_reseau;
    private EditText editTextEmail;
    private EditText editTextMember;
    private EditText editTextPassword;
    private EditText editTextPasswordRepeat;
    private EditText editTextPhone;
    private EditText firstname;
    private EditText lastname;
    private LocationManager locationManager;

    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private String latitude = "0";
    private String longitude = "0";
    private String[] paysItem;
    private String[] reseauItem;
    private String TAG = "tag";
    private String pays = "";
    private List<String> listReseau ;
    private TelephonyManager tm;
    private String networkCountryISO;
    private String SIMCountryISO;
    private PhoneNumberUtil util = null;

    private TextView latitudeText;
    private ImageView locatedImage;
    private TextView locatedText;
    private TextView longitudeText;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private SupportMapFragment mapFragment;
    private GoogleMap map;

    private CoordinatorLayout clRegister;
    private View sbView;
    private TextView snacktextView;
    private Snackbar snackbar;
    private MaterialDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        CheckEnableGPS();
        this.listReseau = new ArrayList<String>();
        this.firstname = (EditText) findViewById(R.id.firstname);
        this.lastname = (EditText) findViewById(R.id.lastname);
        this.editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        this.editTextPasswordRepeat = (EditText) findViewById(R.id.editTextPasswordRepeat);
        this.editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        this.editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        this.editTextMember = (EditText) findViewById(R.id.editTextMemberCode);
        this.locatedText = (TextView) findViewById(R.id.textLocated);
        this.latitudeText = (TextView) findViewById(R.id.textLatitude);
        this.longitudeText = (TextView) findViewById(R.id.textLongitude);
        this.locatedImage = (ImageView) findViewById(R.id.imageLocated);
        this.buttonRegister = (Button) findViewById(R.id.buttonRegister);

        this.ListPays = (TextView) findViewById(R.id.CountryCode);
        this.ListReseau = (Spinner) findViewById(R.id.Network);

        this.locatedText = (TextView) findViewById(R.id.textLocated);
        this.latitudeText = (TextView) findViewById(R.id.textLatitude);
        this.longitudeText = (TextView) findViewById(R.id.textLongitude);
        this.locatedImage = (ImageView) findViewById(R.id.imageLocated);
        this.buttonRegister.setOnClickListener(this);
        this.clRegister = (CoordinatorLayout) findViewById(R.id
                .clRegister);

            this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRegisterGeo);

            if (this.mapFragment != null) {
                this.mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        loadMap(map);
                    }
                });
            } else {
                Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
            }



        // Start display Full Country Code
        /*


        Recuperer le code iso du pays

         */
        tm =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        networkCountryISO = tm.getNetworkCountryIso();
        SIMCountryISO = tm.getSimCountryIso();

        //firstname.setText(networkCountryISO+SIMCountryISO);

        /*

        Recuperer le nom complet du pays et l'afficher

         */
        if(networkCountryISO.equalsIgnoreCase(SIMCountryISO)) {
            Locale loc = new Locale("",networkCountryISO);
            loc.getDisplayCountry();
            pays = loc.getDisplayCountry();

        }

        ListPays.setText(pays);
        getnetworkList();



        // End display Full Country Code

    }


    public void populateSpinner() {
        ArrayAdapter<String> reseauAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listReseau);
        //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
        reseauAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ListReseau.setAdapter(reseauAdapter);
        ListReseau.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //e_reseau = parent.getItemAtPosition(position).toString();
                e_reseau = String.valueOf(listReseau.get(position)) ;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //e_reseau = reseauItem[0].toString();

                e_reseau = String.valueOf(listReseau.get(0)) ;

            }

        });
    }


    public void onClick(View v) {
        if (v == this.buttonRegister) {

            new UserRegisertTask().execute();
            /*try {
                registerUser();
            } catch (NumberParseException e) {
                //e.printStackTrace();
                Toast.makeText(RegisterActivity.this, "Une erreur s'est produite :" +e.toString(), Toast.LENGTH_LONG).show();

            }
            */
        }
    }

    protected void loadMap(GoogleMap googleMap) {
        this.map = googleMap;
        if (this.map != null) {
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
                this.map.setMyLocationEnabled(true);
                this.mGoogleApiClient = new Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
                connectClient();
                return;
            }
            return;
        }
        Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
    }

    protected void connectClient() {
        if (isGooglePlayServicesAvailable() && this.mGoogleApiClient != null) {
            this.mGoogleApiClient.connect();
        }
    }

    protected void onStart() {
        super.onStart();
        connectClient();
    }

    protected void onStop() {
        if (this.mGoogleApiClient != null) {
            this.mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST /*9000*/:
                switch (resultCode) {
                    case MessageApi.UNKNOWN_REQUEST_ID /*-1*/:
                        this.mGoogleApiClient.connect();
                    default:
                }
            default:
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode == 0) {
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        }
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
        if (errorDialog != null) {
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();
            errorFragment.setDialog(errorDialog);
            errorFragment.show(getSupportFragmentManager(), "Location Updates");
        }
        return false;
    }

    public void onConnected(Bundle dataBundle) {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(this.mGoogleApiClient);
            if (location != null) {
                Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
                this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17.0f));
                startLocationUpdates();
                return;
            }
            Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        this.mLocationRequest.setInterval(this.UPDATE_INTERVAL);
        this.mLocationRequest.setFastestInterval(this.FASTEST_INTERVAL);
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest, this);
        }
    }

    public void onResume() {
        super.onResume();
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (this.locationManager.isProviderEnabled("gps")) {
            abonnementGPS();
        }
    }

    public void onPause() {
        super.onPause();
        desabonnementGPS();
    }

    public void abonnementGPS() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0 && ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
        }
    }

    public void desabonnementGPS() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0 && ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
        }
    }

    public void onLocationChanged(Location location) {
        String email = getSharedPreferences(Config.SHARED_PREF_NAME, 0).getString(KEY_EMAIL, "Not Available");
        StringBuilder msg = new StringBuilder("latitude : ");
        msg.append(location.getLatitude());
        msg.append("; longitude : ");
        msg.append(location.getLongitude());
        this.latitude = String.valueOf(location.getLatitude());
        this.longitude = String.valueOf(location.getLongitude());
        if (this.latitude != "0" && this.longitude != "0") {
            this.locatedText.setText("Vous avez \u00e9t\u00e9 localis\u00e9");
            this.locatedImage.setImageResource(R.drawable.gps_located);
            this.latitudeText.setText(this.latitude);
            this.longitudeText.setText(this.longitude);
        }
    }

    public void onConnectionSuspended(int i) {
        if (i == 1) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == 2) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                return;
            } catch (SendIntentException e) {
                e.printStackTrace();
                return;
            }
        }
        Toast.makeText(getApplicationContext(), "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
    }

    private void registerUser() throws NumberParseException {
        pDialog = new MaterialDialog.Builder(RegisterActivity.this)
                .title("Attendez svp!")
                .content("Enregistrement en cours")
                .progress(true, 0)
                .cancelable(false)
                .show();
        final String prenom = firstname.getText().toString().trim();
        final String nom = lastname.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();

        String phone = editTextPhone.getText().toString().trim();
        final String member_code = editTextMember.getText().toString().trim();
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
                                        if(response.equalsIgnoreCase(Config.LOGIN_SUCCESS)) {
                                            hidePDialog();
                                            Toast.makeText(RegisterActivity.this, "Enregistrement reussi! Recuperez le code de validation qui vous a ete envoye, ensuite, reconnectez-vous avec le numero de telephone et le mot de passe specifie lors de votre enregistrement.", Toast.LENGTH_LONG).show();
                                            Intent i = new Intent(RegisterActivity.this, LoginGeolocatedActivity.class);
                                            startActivity(i);

                                            finish();
                                        }else {
                                            hidePDialog();
                                            Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();

                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        hidePDialog();
                                        Toast.makeText(RegisterActivity.this, "Impossible de se connecter au serveur"+error.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }) {
                            @Override


                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap();
                                params.put(KEY_FIRSTNAME, prenom);
                                params.put(KEY_LASTNAME, nom);
                                params.put(KEY_PASSWORD, password);
                                params.put(KEY_EMAIL, email);
                                params.put(KEY_PHONE, phoneSend);
                                params.put(KEY_NETWORK, e_reseau);
                                params.put(KEY_MEMBER_CODE, member_code);
                                params.put(KEY_LATITUDE, latitude);
                                params.put(KEY_LONGITUDE, longitude);
                                params.put(KEY_COUNTRY, pays);
                                params.put(KEY_CATEGORY, "geolocated");
                                params.put(KEY_ACTIVE, "non");
                                params.put(KEY_TAG, "register_geolocated");
                                return params;
                            }


                        };

                        // Set timeout request
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));

                        RequestQueue requestQueue = Volley.newRequestQueue(this);
                        requestQueue.add(stringRequest);

                    } else {
                        hidePDialog();
                        Toast.makeText(RegisterActivity.this, "Pas encore localise!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    hidePDialog();
                    Toast.makeText(getApplicationContext(),
                            "Les mots de passe entres ne correspondent pas. Essayez de nouveau!", Toast.LENGTH_SHORT).show();

                }

            } else {
                hidePDialog();
                Toast.makeText(getApplicationContext(),
                        "Le pseudonyme doit être au minimum de 5 caractères", Toast.LENGTH_SHORT).show();
            }
        } else {
            hidePDialog();
            Toast.makeText(getApplicationContext(),
                    "Un ou plusieurs champs sont vides", Toast.LENGTH_SHORT).show();
        }


    }
/*
    private void registerUser() {
        String prenom = this.firstname.getText().toString().trim();
        String nom = this.lastname.getText().toString().trim();
        String password = this.editTextPassword.getText().toString().trim();
        String email = this.editTextEmail.getText().toString().trim();
        String phone = this.editTextPhone.getText().toString().trim();
        String member_code = this.editTextMember.getText().toString().trim();
        if (this.lastname.getText().toString().equals(BuildConfig.VERSION_NAME) || this.editTextPassword.getText().toString().equals(BuildConfig.VERSION_NAME) || this.firstname.getText().toString().equals(BuildConfig.VERSION_NAME) || this.editTextPhone.getText().toString().equals(BuildConfig.VERSION_NAME) || this.editTextEmail.getText().toString().equals(BuildConfig.VERSION_NAME) || this.editTextMember.getText().toString().equals(BuildConfig.VERSION_NAME)) {
            Toast.makeText(getApplicationContext(), "Un ou plusieurs champs sont vides", Toast.LENGTH_SHORT).show();
        } else if (this.editTextPhone.getText().toString().length() <= 4) {
            Toast.makeText(getApplicationContext(), "Le pseudonyme doit \u00eatre au minimum de 5 caract\u00e8res", Toast.LENGTH_SHORT).show();
        } else if (this.editTextPassword.getText().toString().length() != this.editTextPasswordRepeat.getText().toString().length()) {
            Toast.makeText(getApplicationContext(), "Les mots de passe entres ne correspondent pas. Essayez de nouveau!", Toast.LENGTH_SHORT).show();
        } else if (this.latitude == null || this.longitude == null) {
            Toast.makeText(this, "Pas encore localise!", Toast.LENGTH_LONG).show();
        } else {
            Volley.newRequestQueue(this).add(new C15646(1, REGISTER_URL, new C15624(), new C15635(), prenom, nom, password, email, phone, member_code));
        }
    }
*/

    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;

        public ErrorDialogFragment() {
            this.mDialog = null;
        }

        public void setDialog(Dialog dialog) {
            this.mDialog = dialog;
        }

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return this.mDialog;
        }
    }

    private void CheckEnableGPS(){


    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    boolean gps_enabled = false;
    boolean network_enabled = false;

    try {
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    } catch(Exception ignored) {}

    try {
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    } catch(Exception ignored) {}

    if(!gps_enabled && !network_enabled) {
        // notify user
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Vos options de localisation semblent ne pas être activée. Le GPS et la localisation par le réseau (Wifi ou réseau mobile) doivent être tous les deux activés. Souhaitez vous le faire et profiter pleinement des fonctions de Nkala?");
        dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        });
        dialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

}

    private class CheckReseau extends AsyncTask<String, String, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*nDialog = new MaterialDialog.Builder(MapsActivity.this);
            nDialog.setTitle("Vérification de la connexion réseau");
            nDialog.setContent("Chargement..");
            nDialog.setProgress(true,0);
            nDialog.setCancelable(false);
            nDialog.show();
            */

            pDialog = new MaterialDialog.Builder(RegisterActivity.this)
                    .title("Attendez svp!")
                    .content("Vérification de la connexion réseau")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();


        }

        /**
         * Gets current device state and checks for working internet connection by trying Google.
         **/
        @Override
        protected Boolean doInBackground(String... args) {


            ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                if (Util.Operations.isOnline(RegisterActivity.this)) {
                    return true;
                } else {
                    Toast.makeText(RegisterActivity.this, "Pas de connexion internet", Toast.LENGTH_SHORT).show();
                }

            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean th) {

            if (th == true) {
                hidePDialog();
                recuperationReseau();
            } else {
                hidePDialog();
                Toast.makeText(getBaseContext().getApplicationContext(), "Erreur lors de la connexion au réseau", Toast.LENGTH_LONG).show();

            }
        }
    }


    private void recuperationReseau() {

    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    public class UserRegisertTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new MaterialDialog.Builder(RegisterActivity.this)
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
                if (Util.Operations.isOnline(RegisterActivity.this)) {

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
                registerUser();
            } catch (NumberParseException e) {
                hidePDialog();
                //e.printStackTrace();
                //
                //callSnackbar(e.getMessage());
                Toast.makeText(RegisterActivity.this, "Une erreur s'est produite :" +e.toString(), Toast.LENGTH_LONG).show();


            }
            // Start Registering
        }

        @Override
        protected void onCancelled() {

        }
    }
    public void callSnackbar(String errorMessage) {
        snackbar = Snackbar.make(clRegister, errorMessage, Snackbar.LENGTH_INDEFINITE);
        sbView = snackbar.getView();
        snacktextView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color

        snacktextView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
    public void getnetworkList() {
        /*

        Recuperer la liste de reseaux

         */

        String tag_json_arry = "json_array_req";



        pDialog = new MaterialDialog.Builder(RegisterActivity.this)
                .title("Attendez svp!")
                .content("Recuperation de la liste des réseaux")
                .progress(true, 0)
                .cancelable(false)
                .show();
        //String url = "https://ilink-app.com/app/select/locations.php";
        String url = "https://ilink-app.com/app/select/network.php";
        Map<String, String> params = new HashMap<String, String>();

        params.put(KEY_COUNTRY, pays);
        params.put(KEY_TAG, "getuser");
        // Creating volley request obj
        CustomRequest movieReq = new CustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        hidePDialog();
                        Log.d(TAG, response.toString());


                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);


                                //In onresume fetching value from sharedpreference




                                /*Iterator<?> keys = obj.keys(); // get the keys of the jsonObject
                                while(keys.hasNext()) {
                                    //iterrate over them
                                    String key = (String)keys.next();
                                    if(obj.optString(key).trim()!=null) {

                                        String data = obj.optString(key);

                                        if (data.length()==0) {

                                        } else {
                                            listReseau.add(obj.optString(key));

                                        }


                                    } else {

                                    }

                                }
                                */


                                Iterator<?> iter = obj.keys();
                                reseauItem = new String[obj.length()];
                                for (int j = 0; j< obj.length(); j++)
                                {

                                    String key = (String) iter.next();






                                    if(obj.optString(key).trim()!=null) {

                                        String data = obj.optString(key);

                                        if (data.length()==0) {

                                        } else {

                                            if(data.equalsIgnoreCase(pays)) {

                                            } else {
                                                listReseau.add(obj.optString(key));
                                            }






                                        }


                                    } else {

                                    }


                                }
                                //Toast.makeText(RegisterActivity.this, listReseau.toString(), Toast.LENGTH_LONG).show();

                                populateSpinner();



                            } catch (JSONException e) {
                                hidePDialog();
                                showLocationDialog();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
                showLocationDialog();


            }
        });
        // Set timeout request
        movieReq.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        movieReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq, tag_json_arry);
    }

    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle(getString(R.string.dialog_title));
        builder.setMessage(getString(R.string.dialog_message));
        builder.setCancelable(false);

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getnetworkList();
                    }
                });

       /* String negativeText = getString(R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });*/

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }


}
