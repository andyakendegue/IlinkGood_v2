package com.appli.ilink;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddSuperviseurActivity extends AppCompatActivity implements OnClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_COUNTRY = "country";
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
    public static final String KEY_VALIDATE = "validate";
    private static final String REGISTER_URL = "http://ilink-app.com/app/";
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_superviseur);
        getSupportActionBar().hide();
        CheckEnableGPS();
        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRegister);
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
        final SharedPreferences sharedPreferences = this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String member_code = sharedPreferences.getString(Config.MEMBER_CODE_SHARED_PREF, "Not Available");;
        this.listReseau = new ArrayList<String>();
        this.firstname = (EditText) findViewById(R.id.firstname);
        this.lastname = (EditText) findViewById(R.id.lastname);
        this.editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        this.editTextPasswordRepeat = (EditText) findViewById(R.id.editTextPasswordRepeat);
        this.editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        this.editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        this.editTextMember = (EditText) findViewById(R.id.editTextMemberCode);
        this.buttonRegister = (Button) findViewById(R.id.buttonRegister);
        this.buttonRegister.setOnClickListener(this);
        this.ListPays = (TextView) findViewById(R.id.CountryCode);
        this.ListReseau = (Spinner) findViewById(R.id.Network);
        this.titleLayout = (TextView) findViewById(R.id.textView5);

        this.locatedText = (TextView) findViewById(R.id.textLocated);
        this.latitudeText = (TextView) findViewById(R.id.textLatitude);
        this.longitudeText = (TextView) findViewById(R.id.textLongitude);
        this.locatedImage = (ImageView) findViewById(R.id.imageLocated);
        titleLayout.setText("Ajoutez un Superviseur");
        this.e_pays = "Gabon";



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
        editTextMember.setText(member_code);

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

                                Toast.makeText(AddSuperviseurActivity.this, "Impossible de recuperer les reseaux", Toast.LENGTH_LONG).show();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(AddSuperviseurActivity.this, "Connexion au serveur impossible", Toast.LENGTH_LONG).show();


            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq, tag_json_arry);

        // End display Full Country Code




        /*List<String> listePays = new ArrayList();
        this.paysItem = getResources().getStringArray(R.array.country_code);
        for (Object add : this.paysItem) {
            listePays.add(String.valueOf(add));
        }
        ArrayAdapter<String> paysAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listePays);
        //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
        paysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ListPays.setAdapter(paysAdapter);
        ListPays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                e_pays = parent.getItemAtPosition(position).toString();
                if (e_pays.equals("Burkina-Faso")) {
                    reseauItem = getResources().getStringArray(R.array.network_burkina);

                } else if (e_pays.equals("Cameroun")) {
                    reseauItem = getResources().getStringArray(R.array.network_cameroun);

                } else if (e_pays.equals("France")) {
                    reseauItem = getResources().getStringArray(R.array.network_france);

                } else if (e_pays.equals("Gabon")) {
                    reseauItem = getResources().getStringArray(R.array.network_gabon);
                }
                List<String> listReseau = new ArrayList<String>();
                final int reseauLength = reseauItem.length;

                for (int i = 0; i < reseauLength; i++) {
                    listReseau.add(reseauItem[i]);

                }
                ArrayAdapter<String> reseauAdapter = new ArrayAdapter<String>(AddSuperviseurActivity.this, android.R.layout.simple_spinner_item, listReseau);
                //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
                reseauAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ListReseau.setAdapter(reseauAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                e_pays = paysItem[0].toString();
            }

        });
*/


        //List<String> listReseau = new ArrayList();
        /*
        this.reseauItem = getResources().getStringArray(R.array.network_gabon);
        for (Object add2 : this.reseauItem) {
            listReseau.add(String.valueOf(add2));
        }
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
            try {
                registerUser();
            } catch (NumberParseException e) {
                //e.printStackTrace();
                Toast.makeText(AddSuperviseurActivity.this, "Une erreur s'est produite :" +e.toString(), Toast.LENGTH_LONG).show();

            }
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
    public void onBackPressed() {
        this.finish();
    }

    public void abonnementGPS() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //this.locationManager.requestLocationUpdates("gps", 5000, 10.0f, this);
    }

    public void desabonnementGPS() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //this.locationManager.removeUpdates(this);
    }

    public void onLocationChanged(Location location) {
        //Fetching email from shared preferences
        SharedPreferences sharedPreferences = this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");
        //On affiche dans un Toat la nouvelle Localisation
        final StringBuilder msg = new StringBuilder("latitude : ");
        msg.append(location.getLatitude());
        msg.append("; longitude : ");
        msg.append(location.getLongitude());
/*
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        //Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show();

        if (latitude != "0" && longitude != "0" && latitude != null && longitude != null) {
            locatedText.setText("Vous avez été localisé");
            locatedImage.setImageResource(R.drawable.gps_located);
            latitudeText.setText(latitude);
            longitudeText.setText(longitude);
        }
        */
    }

    public void onProviderDisabled(String provider) {
        if ("gps".equals(provider)) {
            desabonnementGPS();
        }
    }

    public void onProviderEnabled(String provider) {
        if ("gps".equals(provider)) {
            abonnementGPS();
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }



    // End other Location Method

    private void registerUser() throws NumberParseException {
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
                                            Toast.makeText(AddSuperviseurActivity.this, "Enregistrement reussi! Recuperez le code de validation qui vous a ete envoye, ensuite, reconnectez-vous avec le numero de telephone et le mot de passe specifie lors de votre enregistrement.", Toast.LENGTH_LONG).show();
                                            Intent i = new Intent(AddSuperviseurActivity.this, LoginGeolocatedActivity.class);
                                            startActivity(i);

                                            finish();
                                        }else {
                                            Toast.makeText(AddSuperviseurActivity.this, response, Toast.LENGTH_LONG).show();

                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(AddSuperviseurActivity.this, "Impossible de se connecter au serveur", Toast.LENGTH_LONG).show();
                                    }
                                }) {
                            @Override


                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap();
                                params.put(AddSuperviseurActivity.KEY_FIRSTNAME, prenom);
                                params.put(AddSuperviseurActivity.KEY_LASTNAME, nom);
                                params.put(AddSuperviseurActivity.KEY_PASSWORD, password);
                                params.put(AddSuperviseurActivity.KEY_EMAIL, email);
                                params.put(AddSuperviseurActivity.KEY_PHONE, phoneSend);
                                params.put(AddSuperviseurActivity.KEY_NETWORK, e_reseau);
                                params.put(AddSuperviseurActivity.KEY_MEMBER_CODE, member_code);
                                params.put(AddSuperviseurActivity.KEY_LATITUDE, latitude);
                                params.put(AddSuperviseurActivity.KEY_LONGITUDE, longitude);
                                params.put(AddSuperviseurActivity.KEY_COUNTRY, e_pays);
                                params.put(AddSuperviseurActivity.KEY_CATEGORY, "geolocated");
                                params.put(AddSuperviseurActivity.KEY_VALIDATE, "non");
                                params.put(AddSuperviseurActivity.KEY_TAG, "register_geolocated");
                                return params;
                            }


                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(this);
                        requestQueue.add(stringRequest);

                    } else {
                        Toast.makeText(AddSuperviseurActivity.this, "Pas encore localise!", Toast.LENGTH_LONG).show();
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

/*
    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.map = googleMap;
        if (this.map != null) {
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
                this.map.setMyLocationEnabled(true);
                this.mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this).build();
                connectClient();
                return;
            }
            return;
        }
        Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        map = googleMap;


        final LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));


        marker = map.addMarker(new MarkerOptions().title("Vous êtes localisé ici!").snippet(pays).position(
                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));



        //map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        marker.showInfoWindow();




    }

    */

    // Locations Method
    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            } else {
                map.setMyLocationEnabled(true);
            }


            // Now that map has loaded, let's get our location!
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();

            connectClient();
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }
    protected void connectClient() {
        if (isGooglePlayServicesAvailable() && this.mGoogleApiClient != null) {
            this.mGoogleApiClient.connect();
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
            RegisterActivity.ErrorDialogFragment errorFragment = new RegisterActivity.ErrorDialogFragment();
            errorFragment.setDialog(errorDialog);
            errorFragment.show(getSupportFragmentManager(), "Location Updates");
        }
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(this.mGoogleApiClient);
            if (location != null) {
                Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
                this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17.0f));
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                //Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show();

                if (latitude != "0" && longitude != "0" && latitude != null && longitude != null) {
                    locatedText.setText("Vous avez été localisé");
                    locatedImage.setImageResource(R.drawable.gps_located);
                    latitudeText.setText(latitude);
                    longitudeText.setText(longitude);
                }
                startLocationUpdates();
                return;
            }
            Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
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
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                return;
            }
        }
        Toast.makeText(getApplicationContext(), "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
    }
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, (com.google.android.gms.location.LocationListener) this);
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
            dialog.show();
        }

    }
}
