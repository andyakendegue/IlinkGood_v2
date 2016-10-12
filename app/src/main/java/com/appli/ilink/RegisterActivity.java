package com.appli.ilink;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
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

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;

public class RegisterActivity extends AppCompatActivity implements OnClickListener, LocationListener, ConnectionCallbacks, OnConnectionFailedListener {
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_COUNTRY = "country_code";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_MEMBER_CODE = "member_code";
    public static final String KEY_NETWORK = "network";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_TAG = "tag";
    public static final String KEY_VALIDATE = "validate";
    private static final String REGISTER_URL = "http://ilink-app.com/app/";
    private long FASTEST_INTERVAL;
    private TextView ListPays;
    private Spinner ListReseau;
    private long UPDATE_INTERVAL;
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
    private TextView latitudeText;
    private ImageView locatedImage;
    private TextView locatedText;
    private LocationManager locationManager;
    private TextView longitudeText;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private TelephonyManager tm;
    private String networkCountryISO;
    private String SIMCountryISO;
    private PhoneNumberUtil util = null;

    private String latitude = "0";
    private String longitude = "0";
    private String[] paysItem;
    private String[] reseauItem;
    private String TAG = "tag";
    private String pays = "";
    private List<String> listReseau ;

    /* renamed from: com.appli.ilink.RegisterActivity.1 */
    class C15591 implements OnMapReadyCallback {
        C15591() {
        }

        public void onMapReady(GoogleMap map) {
            RegisterActivity.this.loadMap(map);
        }
    }

    /* renamed from: com.appli.ilink.RegisterActivity.2 */
    class C15602 implements OnItemSelectedListener {
        C15602() {
        }

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            RegisterActivity.this.e_pays = parent.getItemAtPosition(position).toString();
            if (RegisterActivity.this.e_pays.equals("Burkina-Faso")) {
                RegisterActivity.this.reseauItem = RegisterActivity.this.getResources().getStringArray(R.array.network_burkina);
            } else if (RegisterActivity.this.e_pays.equals("Cameroun")) {
                RegisterActivity.this.reseauItem = RegisterActivity.this.getResources().getStringArray(R.array.network_cameroun);
            } else if (RegisterActivity.this.e_pays.equals("France")) {
                RegisterActivity.this.reseauItem = RegisterActivity.this.getResources().getStringArray(R.array.network_france);
            } else if (RegisterActivity.this.e_pays.equals("Gabon")) {
                RegisterActivity.this.reseauItem = RegisterActivity.this.getResources().getStringArray(R.array.network_gabon);
            }
            List<String> listReseau = new ArrayList();
            for (Object add : RegisterActivity.this.reseauItem) {
                listReseau.add(String.valueOf(add));
            }
            ArrayAdapter<String> reseauAdapter = new ArrayAdapter(RegisterActivity.this, android.R.layout.simple_spinner_item, listReseau);
            reseauAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            RegisterActivity.this.ListReseau.setAdapter(reseauAdapter);
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
            RegisterActivity.this.e_pays = RegisterActivity.this.paysItem[0].toString();
        }
    }

    /* renamed from: com.appli.ilink.RegisterActivity.3 */
    class C15613 implements OnItemSelectedListener {
        C15613() {
        }

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            RegisterActivity.this.e_reseau = parent.getItemAtPosition(position).toString();
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
            RegisterActivity.this.e_reseau = RegisterActivity.this.reseauItem[0].toString();
        }
    }

    /* renamed from: com.appli.ilink.RegisterActivity.4 */
    class C15624 implements Listener<String> {
        C15624() {
        }

        public void onResponse(String response) {
            if (response.equalsIgnoreCase(Config.LOGIN_SUCCESS)) {
                Toast.makeText(RegisterActivity.this, "Enregistrement reussi! Recuperez le code de validation qui vous a ete envoye, ensuite, reconnectez-vous avec le numero de telephone et le mot de passe specifie lors de votre enregistrement.", Toast.LENGTH_LONG).show();
                RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, LoginGeolocatedActivity.class));
                RegisterActivity.this.finish();
                return;
            }
            Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();
        }
    }

    /* renamed from: com.appli.ilink.RegisterActivity.5 */
    class C15635 implements ErrorListener {
        C15635() {
        }

        public void onErrorResponse(VolleyError error) {
            Toast.makeText(RegisterActivity.this, "Impossible de se connecter au serveur", Toast.LENGTH_LONG).show();
        }
    }

    /* renamed from: com.appli.ilink.RegisterActivity.6 */
    class C15646 extends StringRequest {
        final /* synthetic */ String val$email;
        final /* synthetic */ String val$member_code;
        final /* synthetic */ String val$nom;
        final /* synthetic */ String val$password;
        final /* synthetic */ String val$phone;
        final /* synthetic */ String val$prenom;

        C15646(int x0, String x1, Listener x2, ErrorListener x3, String str, String str2, String str3, String str4, String str5, String str6) {
            super(x0, x1, x2, x3);
            this.val$prenom = str;
            this.val$nom = str2;
            this.val$password = str3;
            this.val$email = str4;
            this.val$phone = str5;
            this.val$member_code = str6;
        }

        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap();
            params.put(RegisterActivity.KEY_FIRSTNAME, this.val$prenom);
            params.put(RegisterActivity.KEY_LASTNAME, this.val$nom);
            params.put(RegisterActivity.KEY_PASSWORD, this.val$password);
            params.put(RegisterActivity.KEY_EMAIL, this.val$email);
            params.put(RegisterActivity.KEY_PHONE, this.val$phone);
            params.put(RegisterActivity.KEY_NETWORK, RegisterActivity.this.e_reseau);
            params.put(RegisterActivity.KEY_MEMBER_CODE, this.val$member_code);
            params.put(RegisterActivity.KEY_LATITUDE, RegisterActivity.this.latitude);
            params.put(RegisterActivity.KEY_LONGITUDE, RegisterActivity.this.longitude);
            params.put(RegisterActivity.KEY_COUNTRY, RegisterActivity.this.e_pays);
            params.put(RegisterActivity.KEY_CATEGORY, "geolocated");
            params.put(RegisterActivity.KEY_VALIDATE, "non");
            params.put(RegisterActivity.KEY_TAG, "register_geolocated");
            return params;
        }
    }

    /* renamed from: com.appli.ilink.RegisterActivity.7 */
    class C15657 implements DialogInterface.OnClickListener {
        C15657() {
        }

        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            RegisterActivity.this.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
        }
    }

    /* renamed from: com.appli.ilink.RegisterActivity.8 */
    class C15668 implements DialogInterface.OnClickListener {
        C15668() {
        }

        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
        }
    }

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

    public RegisterActivity() {
        this.e_pays = "Gabon";
        this.ListPays = null;
        this.ListReseau = null;
        this.latitude = "0";
        this.longitude = "0";
        this.UPDATE_INTERVAL = 60000;
        this.FASTEST_INTERVAL = 5000;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_register);
        getSupportActionBar().hide();
        CheckEnableGPS();
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
        this.e_pays = "Gabon";
        this.buttonRegister.setOnClickListener(this);
        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRegister);
        if (this.mapFragment != null) {
            this.mapFragment.getMapAsync(new C15591());
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

        firstname.setText(networkCountryISO+SIMCountryISO);

        /*

        Recuperer le nom complet du pays et l'afficher

         */
        if(networkCountryISO.equalsIgnoreCase(SIMCountryISO)) {
            Locale loc = new Locale("",networkCountryISO);
            loc.getDisplayCountry();
            pays = loc.getDisplayCountry();

        }

        ListPays.setText(pays);

        /*

        Recuperer la liste de reseaux

         */

        String tag_json_arry = "json_array_req";




        //String url = "http://ilink-app.com/app/select/locations.php";
        String url = "http://ilink-app.com/app/select/network.php";
        Map<String, String> params = new HashMap<String, String>();

        params.put(KEY_COUNTRY, pays);
        params.put(KEY_TAG, "getuser");
        // Creating volley request obj
        CustomRequest movieReq = new CustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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


                                populateSpinner();



                            } catch (JSONException e) {

                                Toast.makeText(RegisterActivity.this, "Impossible de recuperer les reseaux", Toast.LENGTH_LONG).show();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(RegisterActivity.this, "Connexion au serveur impossible", Toast.LENGTH_LONG).show();


            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq, tag_json_arry);

        // End display Full Country Code
/*
        this.ListPays = (Spinner) findViewById(R.id.CountryCode);
        List<String> listePays = new ArrayList();
        this.paysItem = getResources().getStringArray(R.array.country_code);
        for (Object add : this.paysItem) {
            listePays.add(String.valueOf(add));
        }
        ArrayAdapter<String> paysAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listePays);
        paysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.ListPays.setAdapter(paysAdapter);
        this.ListPays.setOnItemSelectedListener(new C15602());
        this.ListReseau = (Spinner) findViewById(R.id.Network);
        List<String> listReseau = new ArrayList();
        this.reseauItem = getResources().getStringArray(R.array.network_gabon);
        for (Object add2 : this.reseauItem) {
            listReseau.add(String.valueOf(add2));
        }
        ArrayAdapter<String> reseauAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listReseau);
        reseauAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.ListReseau.setAdapter(reseauAdapter);
        this.ListReseau.setOnItemSelectedListener(new C15613());
        */
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
            registerUser();
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
            LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest, (LocationListener) this);
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

    private void CheckEnableGPS() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled("gps");
        } catch (Exception e) {
        }
        try {
            network_enabled = lm.isProviderEnabled(KEY_NETWORK);
        } catch (Exception e2) {
        }
        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage((CharSequence) "Vos options de localisation semblent ne pas \u00eatre activ\u00e9e. Le GPS et la localisation par le r\u00e9seau (Wifi ou r\u00e9seau mobile) doivent \u00eatre tous les deux activ\u00e9s. Souhaitez vous le faire et profiter pleinement des fonctions de Nkala?");
            dialog.setPositiveButton((CharSequence) "Oui", new C15657());
            dialog.setNegativeButton((CharSequence) "Annuler", new C15668());
            dialog.show();
        }
    }
}
