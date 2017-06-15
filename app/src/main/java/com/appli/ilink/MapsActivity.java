package com.appli.ilink;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appli.ilink.adapter.LegendeAdapter;
import com.appli.ilink.model.legendeModel;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.appli.ilink.app.AppController;
import com.appli.ilink.model.myMarker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnMapClickListener, OnMarkerClickListener, LocationListener, RoutingListener, ConnectionCallbacks, OnConnectionFailedListener {
    private LocationManager locationManager;
    private Marker marker;
    private MaterialDialog pDialog;
    private String TAG = "tag";
    private RelativeLayout mapLayout;
    private static int ADVICE_TIME_OUT = 3000;
    private boolean checked;
    private boolean notShowing = false;

    private LegendeAdapter adapter;
    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;
    private int typeReseau = 1;

    private List<legendeModel> memberGroupList;
    private ListView memberListView;
    // Route

    protected LatLng start;
    protected LatLng end;
    private static final String LOG_TAG = "MyActivity";
    private ArrayList<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark, R.color.primary, R.color.primary_light, R.color.accent, R.color.primary_dark_material_light};
    private Button sendRoute;
    private ImageButton imageBtnRoute;
    Double latitude;
    Double longitude;
    private static final String REGISTER_URL = "https://ilink-app.com/app/";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_TAG = "tag";
    public static final String KEY_COUNTRY = "country_code";
    private LinearLayout relativeLayout;
    private Criteria crit = new Criteria();
    private Location currentLoc;
    private SharedPreferences sharedPreferences;

    private int connexionCompte;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private ArrayList<myMarker> mMyMarkersArray = new ArrayList<myMarker>();
    private HashMap<Marker, myMarker> mMarkersHashMap;

    private View customSearch;
    private LayoutInflater inflater;

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private Spinner ListDistance;
    private String[] distanceItem;
    private String e_distance;
    private Spinner ListMontant;
    private String[] montantItem;
    private String e_montant;
    private String[] reseauItem;
    private static List<String> listReseau;
    private SlidingUpPanelLayout mLayout;

    // Slide Panel view
    private TextView t;
    private TextView u;
    private ImageButton f;
    private ListView sv;
    private TextView legendes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        CheckEnableGPS();
        listReseau = new ArrayList();
        this.memberGroupList = new ArrayList();
        latitude = 0.0;
        longitude = 0.0;
        relativeLayout = (LinearLayout) findViewById(R.id
                .tgLayout);
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        connexionCompte = 0;

        this.memberListView = (ListView) findViewById(R.id.listMemberGroupAll);
        this.legendes = (TextView) findViewById(R.id.textViewLegende);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabMap);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                logout();
            }
        });

        fab.setVisibility(View.INVISIBLE);


        FloatingActionButton fabSearch = (FloatingActionButton) findViewById(R.id.fabSearch);
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*new MaterialDialog.Builder(MapsActivity.this)
                        .title(R.string.title_located)
                        .content(R.string.mon_compte)
                        .progress(true, 0)
                        .show();
                        */

                inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                customSearch = inflater.inflate(R.layout.dialog_customlayout, null, false);

                // Distance

                ListDistance = (Spinner) customSearch.findViewById(R.id.spinDistance);
                List<String> listeDistance = new ArrayList<String>();
                distanceItem = getResources().getStringArray(R.array.distance_minimale);
                final int distanceLength = distanceItem.length;

                for (int i = 0; i < distanceLength; i++) {
                    listeDistance.add(distanceItem[i]);

                }
                ArrayAdapter<String> distanceAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_spinner_item, listeDistance);
                //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
                distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ListDistance.setAdapter(distanceAdapter);
                ListDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        e_distance = parent.getItemAtPosition(position).toString();


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        e_distance = distanceItem[0].toString();
                    }

                });

                // Montant

                ListMontant = (Spinner) customSearch.findViewById(R.id.spinMontant);
                List<String> listeMontant = new ArrayList<String>();
                montantItem = getResources().getStringArray(R.array.montant_transaction);
                final int montantLength = montantItem.length;

                for (int i = 0; i < montantLength; i++) {
                    listeMontant.add(montantItem[i]);

                }
                ArrayAdapter<String> montantAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_spinner_item, listeMontant);
                //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
                montantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ListMontant.setAdapter(montantAdapter);
                ListMontant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        e_montant = parent.getItemAtPosition(position).toString();


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        e_montant = montantItem[0].toString();
                    }

                });

                boolean wrapInScrollView = true;
                new MaterialDialog.Builder(MapsActivity.this)
                        .title("Affinez votre recherche")
                        .customView(customSearch, wrapInScrollView)
                        .positiveText("Rechercher")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {

                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                map.clear();
                                new SearchAllLocations().execute();
                            }
                        })
                        .negativeText("Annuler")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .show();
            }
        });

        mapLayout = (RelativeLayout) findViewById(R.id.maplayout);

        polylines = new ArrayList<>();


        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            //Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }


        //getnetworkList();

        new CheckLocations().execute();


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        crit.setAccuracy(Criteria.ACCURACY_FINE);


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
            currentLoc = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(crit, true));
        }



        ToggleButton toggleSimple = (ToggleButton) findViewById(R.id.tgBtnSimple);
        toggleSimple.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == false) {
                    typeReseau = 1;
                    map.clear();
                    map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            // Add a marker in Sydney and move the camera


                            final SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            final String lat = sharedPreferences.getString(Config.LATITUDE_SHARED_PREF, "Not Available");
                            final String lng = sharedPreferences.getString(Config.LONGITUDE_SHARED_PREF, "Not Available");


                            //double lati = currentLoc.getLatitude();
                            //double longi = currentLoc.getLongitude();
                            //LatLng latlng = new LatLng(lati, longi);
                            LatLng latlng = new LatLng(latitude, longitude);

                            if (marker != null) {
                                marker.remove();
                            }

                            marker = map.addMarker(new MarkerOptions().title("Vous êtes ici!").position(
                                    latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));

                            //latitude = Double.parseDouble(lat);
                            //longitude = Double.parseDouble(lng);

                            map.setOnMarkerClickListener(MapsActivity.this);

                            map.getUiSettings().setZoomControlsEnabled(true);
                            map.getUiSettings().setMapToolbarEnabled(false);

                            start = latlng;
                            marker.showInfoWindow();

                        }
                    });
                    new CheckLocations().execute();

                } else {

                    typeReseau = 2;
                    map.clear();
                    new CheckAllLocations().execute();
                    map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            // Add a marker in Sydney and move the camera

                            //double lati = currentLoc.getLatitude();
                            //double longi = currentLoc.getLongitude();
                            //LatLng latlng = new LatLng(lati, longi);
                            LatLng latlng = new LatLng(latitude, longitude);

                            if (marker != null) {
                                marker.remove();
                            }

                            marker = map.addMarker(new MarkerOptions().title("Vous êtes ici!").position(
                                    latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));

                            map.setOnMarkerClickListener(MapsActivity.this);

                            map.getUiSettings().setZoomControlsEnabled(true);
                            map.getUiSettings().setMapToolbarEnabled(false);


                            start = latlng;
                            marker.showInfoWindow();

                            //Toast.makeText(MapsActivity.this, start.toString() + code, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });


        sendRoute = (Button) findViewById(R.id.sendRoute);
        sendRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();


            }

        });

        sendRoute.setVisibility(View.INVISIBLE);

        imageBtnRoute = (ImageButton) findViewById(R.id.imageBtnRoute);

        imageBtnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();


            }

        });
        imageBtnRoute.setVisibility(View.INVISIBLE);


        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

        //If we will get true
        if (loggedIn) {
            //inscription.setVisibility(View.INVISIBLE);
            //login.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.VISIBLE);

        }

        new Handler().postDelayed(new Runnable() {

			/*
             * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity


                //Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                //startActivity(i);
                advice();


            }
        }, ADVICE_TIME_OUT);


        // Sliding Panel
        t = (TextView) findViewById(R.id.name);
        u = (TextView) findViewById(R.id.name1);
        t.setText(Html.fromHtml("Trouvez votre chemin"));
        u.setText(Html.fromHtml("Avec un temps de parcours exact"));
        f = (ImageButton) findViewById(R.id.follow);
        f.setBackgroundResource(R.drawable.ic_expand_less);

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);

                if (newState.equals(PanelState.EXPANDED)) {
                    f.setBackgroundResource(R.drawable.ic_expand_more);
                } else {
                    f.setBackgroundResource(R.drawable.ic_expand_less);
                }
            }
        });
        mLayout.setFadeOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(PanelState.COLLAPSED);

            }
        });

        f.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mLayout.getPanelState().equals(PanelState.EXPANDED))
                {
                    mLayout.setPanelState(PanelState.COLLAPSED);
                } else {
                    mLayout.setPanelState(PanelState.EXPANDED);
                }

            }
        });


        // Create Layout
        //ListView
        sv = (ListView) findViewById(R.id.sv);


    }


    @Override
    public void onBackPressed() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();


    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onLocationChanged(final Location currentloc) {


        //On affiche dans un Toat la nouvelle Localisation
        currentloc.setAccuracy(Criteria.ACCURACY_FINE);
        final StringBuilder msg = new StringBuilder("latitude : ");
        msg.append(currentloc.getLatitude());
        msg.append("; longitude : ");
        msg.append(currentloc.getLongitude());
        if (marker != null) {
            marker.remove();
        }


        //Mise à jour des coordonnées
        final LatLng latLng = new LatLng(currentloc.getLatitude(), currentloc.getLongitude());

        latitude = currentloc.getLatitude();
        longitude = currentloc.getLongitude();


        start = latLng;
        SharedPreferences sharedPreferences = this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String phone = sharedPreferences.getString(Config.PHONE_SHARED_PREF, "Not Available");

            /*StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MapsActivity.this, "Connexion au serveur impossible", Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put(KEY_PHONE, phone);
                    params.put(KEY_LATITUDE, String.valueOf(currentloc.getLatitude()));
                    params.put(KEY_LONGITUDE, String.valueOf(currentloc.getLongitude()));
                    params.put(KEY_TAG, "updateLocation");
                    return params;
                }


            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
*/


        marker = map.addMarker(new MarkerOptions().title("Vous êtes ici!").position(
                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));


        if (connexionCompte == 0) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            map.animateCamera(cu);

            connexionCompte = 1;
        }
        marker.showInfoWindow();


    }


    private class CheckLocations extends AsyncTask<String, String, Boolean> {


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

            pDialog = new MaterialDialog.Builder(MapsActivity.this)
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
                if (Util.Operations.isOnline(MapsActivity.this)) {
                    return true;
                } else {
                    Toast.makeText(MapsActivity.this, "Pas de connexion internet", Toast.LENGTH_SHORT).show();
                }

            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean th) {

            if (th == true) {
                hidePDialog();
                locations();
            } else {
                hidePDialog();
                Toast.makeText(getBaseContext().getApplicationContext(), "Erreur lors de la connexion au réseau", Toast.LENGTH_LONG).show();

            }
        }
    }


    private void locations() {
        String tag_json_arry = "json_array_req";


        pDialog = new MaterialDialog.Builder(MapsActivity.this)
                .title("Attendez svp!")
                .content("Chargement des marqueurs...")
                .progress(true, 0)
                .cancelable(false)
                .show();
        this.legendes.setVisibility(View.INVISIBLE);
        memberGroupList.clear();


        //String url = "https://ilink-app.com/app/select/locations.php";
        String url = "https://ilink-app.com/app/select/locations.php";
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);

                                //In onresume fetching value from sharedpreference
                                SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                                //Fetching the boolean value form sharedpreferences
                                final String network = sharedPreferences.getString(Config.NETWORK_SHARED_PREF, "Pas disponible");


                                if (obj.getString("latitude") != null && obj.getString("network").equals(network) && obj.getString("category").equals("geolocated") && obj.getString("active").equals("oui")) {

                                    final LatLng latLng = new LatLng(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")));



                                    map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                            latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));

                                } else {


                                }


                            } catch (JSONException e) {

                                Toast.makeText(MapsActivity.this, "Impossible de generer les marqueurs", Toast.LENGTH_LONG).show();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(MapsActivity.this, "Connexion au serveur impossible", Toast.LENGTH_LONG).show();
                hidePDialog();

            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq, tag_json_arry);

    }

    private class CheckAllLocations extends AsyncTask<String, String, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new MaterialDialog.Builder(MapsActivity.this)
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
                if (Util.Operations.isOnline(MapsActivity.this)) {
                    return true;
                } else {
                    Toast.makeText(MapsActivity.this, "Pas de connexion internet", Toast.LENGTH_SHORT).show();
                }

            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean th) {

            if (th == true) {
                hidePDialog();
                allLocations();
            } else {
                hidePDialog();
                Toast.makeText(getBaseContext().getApplicationContext(), "Erreur lors de la connexion au réseau", Toast.LENGTH_LONG).show();

            }
        }
    }


    private void allLocations() {
        String tag_json_arry = "json_array_req";


        pDialog = new MaterialDialog.Builder(MapsActivity.this)
                .title("Attendez svp!")
                .content("Chargement des marqueurs...")
                .progress(true, 0)
                .cancelable(false)
                .show();

        // recupere la liste des reseaux
        getnetworkList();
        //String url = "https://ilink-app.com/app/select/locations.php";
        String url = "https://ilink-app.com/app/select/locations.php";
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);

                                //In onresume fetching value from sharedpreference
                                SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                                //Fetching the boolean value form sharedpreferences
                                final String network = sharedPreferences.getString(Config.NETWORK_SHARED_PREF, "Pas disponible");


                                if (obj.getString("latitude") != null && obj.getString("category").equals("geolocated") && obj.getString("active").equals("oui")) {

                                    final LatLng latLng = new LatLng(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")));




                                    if(obj.getString("network").equals(listReseau.get(0))) {

                                        map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_network1)));

                                    } else if(obj.getString("network").equals(listReseau.get(1))) {

                                        map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_network2)));

                                    } else if(obj.getString("network").equals(listReseau.get(2))) {

                                        map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_network3)));

                                    } else if(obj.getString("network").equals(listReseau.get(3))) {

                                        map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_network4)));

                                    } else if(obj.getString("network").equals(listReseau.get(4))) {

                                        map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_network5)));

                                    } else if(obj.getString("network").equals(listReseau.get(5))) {

                                        map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_network6)));

                                    } else if(obj.getString("network").equals(listReseau.get(6))) {

                                        map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));

                                    } else if(obj.getString("network").equals(listReseau.get(7))) {
                                        map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));

                                    } else if(obj.getString("network").equals(listReseau.get(8))) {
                                        map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));

                                    } else if(obj.getString("network").equals(listReseau.get(9))) {
                                        map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));

                                    } else if(obj.getString("network").equals(listReseau.get(10))) {
                                        map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));

                                    } else if(obj.getString("network").equals(listReseau.get(11))) {
                                        map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));

                                    } else if(obj.getString("network").equals(listReseau.get(12))) {
                                        map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));

                                    } else if(obj.getString("network").equals(listReseau.get(13))) {
                                        map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));

                                    } else if(obj.getString("network").equals(listReseau.get(14))) {
                                        map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));

                                    }else  {

                                    }
                                



                                } else {


                                }


                            } catch (JSONException e) {
                                //e.printStackTrace();
                                Toast.makeText(MapsActivity.this, "Impossible de generer les marqueurs", Toast.LENGTH_LONG).show();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(MapsActivity.this, "Connexion au serveur impossible", Toast.LENGTH_LONG).show();
                hidePDialog();

            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq, tag_json_arry);

    }

    private class SearchAllLocations extends AsyncTask<String, String, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new MaterialDialog.Builder(MapsActivity.this)
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
                if (Util.Operations.isOnline(MapsActivity.this)) {
                    return true;
                } else {
                    Toast.makeText(MapsActivity.this, "Pas de connexion internet", Toast.LENGTH_SHORT).show();
                }

            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean th) {

            if (th == true) {
                hidePDialog();
                searchallLocations(e_distance, e_montant);
            } else {
                hidePDialog();
                Toast.makeText(getBaseContext().getApplicationContext(), "Erreur lors de la connexion au réseau", Toast.LENGTH_LONG).show();

            }
        }
    }


    private void searchallLocations(String distance, String montant) {
        String tag_json_arry = "json_array_req";


        pDialog = new MaterialDialog.Builder(MapsActivity.this)
                .title("Attendez svp!")
                .content("Chargement des marqueurs...")
                .progress(true, 0)
                .cancelable(false)
                .show();

        //String url = "https://ilink-app.com/app/select/locations.php";
        String url = "https://ilink-app.com/app/select/locations.php";
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        //Toast.makeText(MapsActivity.this, "Distance "+e_distance+"Montant "+e_montant, Toast.LENGTH_SHORT).show();


                        float distance_minimale = 0.0f;


                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);

                                //In onresume fetching value from sharedpreference
                                SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                                //Fetching the boolean value form sharedpreferences
                                final String network = sharedPreferences.getString(Config.NETWORK_SHARED_PREF, "Pas disponible");


                                if (typeReseau == 1 && obj.getString("latitude") != null && obj.getString("network").equals(network) && obj.getString("category").equals("geolocated") && obj.getString("active").equals("oui")) {

                                    final LatLng latLng = new LatLng(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")));

                                    Marker custom = map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                            latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));

                                    Location starting = new Location("Depart");
                                    starting.setLatitude(start.latitude);
                                    starting.setLongitude(start.longitude);
                                    Location arrival = new Location("Arrivee");
                                    arrival.setLatitude(Double.parseDouble(obj.getString("latitude")));
                                    arrival.setLongitude(Double.parseDouble(obj.getString("longitude")));
                                    float localDistance = getlocalDistance(starting, arrival);

                                    //getlocalDistance(starting, arrival);

                                    if (e_distance.equalsIgnoreCase("500m")) {
                                        distance_minimale = 500.0f;

                                    } else if (e_distance.equalsIgnoreCase("1km")) {
                                        distance_minimale = 1000.0f;

                                    } else if (e_distance.equalsIgnoreCase("1,5km")) {
                                        distance_minimale = 1500.0f;

                                    } else if (e_distance.equalsIgnoreCase("2km")) {
                                        distance_minimale = 2000.0f;

                                    } else if (e_distance.equalsIgnoreCase("2,5km")) {
                                        distance_minimale = 2500.0f;

                                    } else if (e_distance.equalsIgnoreCase("3km")) {
                                        distance_minimale = 3000.0f;

                                    } else if (e_distance.equalsIgnoreCase("4km")) {
                                        distance_minimale = 4000.0f;

                                    } else if (e_distance.equalsIgnoreCase("5km")) {
                                        distance_minimale = 5000.0f;

                                    } else if (e_distance.equalsIgnoreCase("6km")) {
                                        distance_minimale = 6000.0f;

                                    } else if (e_distance.equalsIgnoreCase("7km")) {
                                        distance_minimale = 7000.0f;

                                    } else if (e_distance.equalsIgnoreCase("8km")) {
                                        distance_minimale = 8000.0f;

                                    } else if (e_distance.equalsIgnoreCase("9km")) {
                                        distance_minimale = 9000.0f;

                                    } else if (e_distance.equalsIgnoreCase("10km")) {
                                        distance_minimale = 10000.0f;

                                    } else if (e_distance.equalsIgnoreCase("20km")) {
                                        distance_minimale = 20000.0f;

                                    } else if (e_distance.equalsIgnoreCase("30km")) {
                                        distance_minimale = 30000.0f;

                                    } else if (e_distance.equalsIgnoreCase("50km")) {
                                        distance_minimale = 50000.0f;

                                    } else if (e_distance.equalsIgnoreCase("100km")) {
                                        distance_minimale = 100000.0f;

                                    } else if (e_distance.equalsIgnoreCase("200km")) {
                                        distance_minimale = 200000.0f;

                                    } else if (e_distance.equalsIgnoreCase("Tous les points")) {
                                        distance_minimale = 2000000000.0f;

                                    }
                                    // Toast.makeText(MapsActivity.this, "ok :"+localDistance +" Distance"+distance_minimale, Toast.LENGTH_SHORT).show();


                                    if (localDistance >= distance_minimale) {
                                        custom.setVisible(false);
                                    }
                                    String dist;

                                    float dista = (float) (Math.round(localDistance * 100.0) / 100.0);
                                    dist = dista + " M";

                                    if (localDistance > 1000.0f) {
                                        localDistance = localDistance / 1000.0f;
                                        dista = (float) (Math.round(localDistance * 100.0) / 100.0);

                                        dist = dista + " KM";
                                    }


                                } else if (typeReseau == 2 && obj.getString("latitude") != null && obj.getString("category").equals("geolocated") && obj.getString("active").equals("oui")) {

                                    final LatLng latLng = new LatLng(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")));

                                    Marker custom = map.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                            latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));

                                    Location starting = new Location("Depart");
                                    starting.setLatitude(start.latitude);
                                    starting.setLongitude(start.longitude);
                                    Location arrival = new Location("Arrivee");
                                    arrival.setLatitude(Double.parseDouble(obj.getString("latitude")));
                                    arrival.setLongitude(Double.parseDouble(obj.getString("longitude")));
                                    float localDistance = getlocalDistance(starting, arrival);

                                    //getlocalDistance(starting, arrival);

                                    if (e_distance.equalsIgnoreCase("500m")) {
                                        distance_minimale = 500.0f;

                                    } else if (e_distance.equalsIgnoreCase("1km")) {
                                        distance_minimale = 1000.0f;

                                    } else if (e_distance.equalsIgnoreCase("1,5km")) {
                                        distance_minimale = 1500.0f;

                                    } else if (e_distance.equalsIgnoreCase("2km")) {
                                        distance_minimale = 2000.0f;

                                    } else if (e_distance.equalsIgnoreCase("2,5km")) {
                                        distance_minimale = 2500.0f;

                                    } else if (e_distance.equalsIgnoreCase("3km")) {
                                        distance_minimale = 3000.0f;

                                    } else if (e_distance.equalsIgnoreCase("4km")) {
                                        distance_minimale = 4000.0f;

                                    } else if (e_distance.equalsIgnoreCase("5km")) {
                                        distance_minimale = 5000.0f;

                                    } else if (e_distance.equalsIgnoreCase("6km")) {
                                        distance_minimale = 6000.0f;

                                    } else if (e_distance.equalsIgnoreCase("7km")) {
                                        distance_minimale = 7000.0f;

                                    } else if (e_distance.equalsIgnoreCase("8km")) {
                                        distance_minimale = 8000.0f;

                                    } else if (e_distance.equalsIgnoreCase("9km")) {
                                        distance_minimale = 9000.0f;

                                    } else if (e_distance.equalsIgnoreCase("10km")) {
                                        distance_minimale = 10000.0f;

                                    } else if (e_distance.equalsIgnoreCase("20km")) {
                                        distance_minimale = 20000.0f;

                                    } else if (e_distance.equalsIgnoreCase("30km")) {
                                        distance_minimale = 30000.0f;

                                    } else if (e_distance.equalsIgnoreCase("50km")) {
                                        distance_minimale = 50000.0f;

                                    } else if (e_distance.equalsIgnoreCase("100km")) {
                                        distance_minimale = 100000.0f;

                                    } else if (e_distance.equalsIgnoreCase("200km")) {
                                        distance_minimale = 200000.0f;

                                    } else if (e_distance.equalsIgnoreCase("Tous les points")) {
                                        distance_minimale = 2000000000.0f;

                                    }
                                    // Toast.makeText(MapsActivity.this, "ok :"+localDistance +" Distance"+distance_minimale, Toast.LENGTH_SHORT).show();


                                    if (localDistance >= distance_minimale) {
                                        custom.setVisible(false);
                                    }
                                    String dist;

                                    float dista = (float) (Math.round(localDistance * 100.0) / 100.0);
                                    dist = dista + " M";

                                    if (localDistance > 1000.0f) {
                                        localDistance = localDistance / 1000.0f;
                                        dista = (float) (Math.round(localDistance * 100.0) / 100.0);

                                        dist = dista + " KM";
                                    }
                                }


                            } catch (JSONException e) {
                                //e.printStackTrace();
                                Toast.makeText(MapsActivity.this, "Impossible de generer les marqueurs", Toast.LENGTH_LONG).show();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(MapsActivity.this, "Connexion au serveur impossible", Toast.LENGTH_LONG).show();
                hidePDialog();

            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq, tag_json_arry);

    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    // Routing


    public void sendRequest() {
        if (Util.Operations.isOnline(this)) {
            route();
        } else {
            Toast.makeText(this, "Pas de connexion internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void route() {
        if (start == null || end == null) {
            Toast.makeText(this, "Votre position et votre destination sont inconnues.", Toast.LENGTH_SHORT).show();
        } else {

            pDialog = new MaterialDialog.Builder(MapsActivity.this)
                    .title("Attendez svp!")
                    .content("Nous cherchons votre chemin...")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();


            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(start, end)
                    .build();
            routing.execute();
            Location arrival = new Location("Destination");
            arrival.setLatitude(end.latitude);
            arrival.setLongitude(end.longitude);
            Location starting = new Location("Depart");
            starting.setLatitude(start.latitude);
            starting.setLongitude(start.longitude);
            toastDistance(starting, arrival);
            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(start, end);
            timeToDirection(url);
        }
    }


    @Override
    public void onRoutingFailure(RouteException e) {
        // The Routing request failed
        String test;
        hidePDialog();
        if (e != null) {
            Toast.makeText(this, "Erreur", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Une erreur est survenue, Essayez encore", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        hidePDialog();
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        map.moveCamera(center);


        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        /*for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 1);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);

            //Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
            //Snackbar.make(mapLayout, "Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(), Snackbar.LENGTH_LONG)
            //.setAction("Action", null).show();
        }*/
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(getResources().getColor(COLORS[1]));
        polyOptions.width(10 + 1 * 1);
        polyOptions.addAll(route.get(0).getPoints());
        Polyline polyline = map.addPolyline(polyOptions);
        polylines.add(polyline);

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
        map.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
        map.addMarker(options);

    }

    @Override
    public void onRoutingCancelled() {
        Log.i(LOG_TAG, "La recherche du chemin a été annulée.");
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {
        Log.i("MapsActivity", "onMarkerClick");

        String title = marker.getTitle();


        if (title.equals("Vous êtes ici!") || title.equals("Gabon")) {
            sendRoute.setVisibility(View.INVISIBLE);
            imageBtnRoute.setVisibility(View.INVISIBLE);

        } else {


            end = marker.getPosition();
            sendRoute.setVisibility(View.VISIBLE);
            imageBtnRoute.setVisibility(View.VISIBLE);

        }

        return false;

    }

    //Logout function
    private void logout() {
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Etes vous sur de vouloir vous deconnecter?");
        alertDialogBuilder.setPositiveButton("Oui",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
                        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();

                        //Puting the value false for loggedin
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                        //Putting blank value to email
                        editor.putString(Config.EMAIL_SHARED_PREF, "");

                        //Puting the value false for checkboxadvice
                        editor.putBoolean(Config.CHECK_BOX_ADVICE_PREF, false);

                        //Saving the sharedpreferences
                        editor.commit();

                        //Starting login activity
                        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("Non",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    private void toastDistance(Location starting, Location arrival) {



        if (starting != null) {
            //float distance = currentLoc.distanceTo(pointLocation);
            float distance = starting.distanceTo(arrival);

            String dist;

            float dista = (float) (Math.round(distance * 100.0) / 100.0);
            dist = dista + " M";

            if (distance > 1000.0f) {
                distance = distance / 1000.0f;
                dista = (float) (Math.round(distance * 100.0) / 100.0);

                dist = dista + " KM";
            }


            t.setText("Le point est situé à : " + dist);
            //Toast.makeText(MapsActivity.this, ""+dist, Toast.LENGTH_SHORT).show();


        } else {

            Toast.makeText(this, "Nous ne vous trouvons pas. Annulation...",
                    Toast.LENGTH_LONG).show();
            return;

        }

    }

    private float getlocalDistance(Location start, Location arrive) {



        if (start != null) {

            //float distance = currentLoc.distanceTo(pointLocation);

            float distance = start.distanceTo(arrive);

           /* String dist;

            float dista = (float) (Math.round(distance * 100.0) / 100.0);
            dist=dista+" M";

            if(distance>1000.0f)
            {
                distance=distance/1000.0f;
                dista = (float) (Math.round(distance * 100.0) / 100.0);

                dist=dista+" KM";
            }
*/

            //displayDistance.setText("Le point est situé à : " +dist);
            //Toast.makeText(MapsActivity.this, ""+distance, Toast.LENGTH_SHORT).show();
            return distance;


        } else {

            Toast.makeText(this, "Nous ne vous trouvons pas. Annulation...",
                    Toast.LENGTH_LONG).show();

            return 0;

        }


    }

    private String getDistance(Location starting, Location arrival) {



        if (starting != null) {
            //float distance = currentLoc.distanceTo(pointLocation);
            float distance = starting.distanceTo(arrival);

            String dist;

            if (distance > 1000.0f) {
                distance = distance / 1000.0f;
                float dista = (float) (Math.round(distance * 100.0) / 100.0);

                dist = dista + " KM";
            } else {

                float dista = (float) (Math.round(distance * 100.0) / 100.0);
                dist = dista + " M";

            }


            t.setText("Le point est situé à : " + dist);

            return dist;


        } else {

            Toast.makeText(this, "Nous ne vous trouvons pas. Annulation...",
                    Toast.LENGTH_LONG).show();
            return null;

        }

    }

    private void updateLocation(final Double latit, final Double longit, final String phoneT) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String phone = sharedPreferences.getString(Config.PHONE_SHARED_PREF, "Not Available");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MapsActivity.this, "Connexion au serveur impossible", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_PHONE, phoneT);
                params.put(KEY_LATITUDE, String.valueOf(latit));
                params.put(KEY_LONGITUDE, String.valueOf(longit));
                params.put(KEY_TAG, "updateLocation");
                return params;
            }


        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        sendRoute.setVisibility(View.INVISIBLE);
        imageBtnRoute.setVisibility(View.INVISIBLE);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        loadMap(map);

        double code = Math.random();

        map.setOnMarkerClickListener(this);

        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMapClickListener(this);


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
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getMyLocation();

        //mapLocationListener.onLocationChanged(currentloc);

        /*
        final LatLng latLng = new LatLng(latitude, longitude);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        crit.setAccuracy(Criteria.ACCURACY_FINE);
        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(crit, true));

        final double lati = location.getLatitude();
        final double longi = location.getLongitude();


        LatLng latlng=
                new LatLng(lati, longi);
        CameraUpdate cu=CameraUpdateFactory.newLatLngZoom(latlng, 15);


        marker = map.addMarker(new MarkerOptions().title("Vous êtes ici!").position(
                latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));



        map.animateCamera(cu);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


        start = latLng;
        marker.showInfoWindow();

*/


    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
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
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /*
     * Called when the Activity becomes visible.
    */
    @Override
    protected void onStart() {
        super.onStart();
        connectClient();
    }

    /*
	 * Called when the Activity is no longer visible.
	 */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    /*
     * Handle results returned to the FragmentActivity by Google Play services
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mGoogleApiClient.connect();
                        break;
                }

        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    /*
     * Called by Location Services when the request to connect the client
     * finishes successfully. At this point, you can request the current
     * location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Location location;
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
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        if (location != null) {
            //Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            //map.animateCamera(cameraUpdate);
            startLocationUpdates();
        } else {
            Toast.makeText(this, "Nous ne pouvons vous localiser, activez le GPS!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
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
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }

    }
    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }



// Time to destination

    private String getDirectionsUrl(LatLng origin, LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }




    private void timeToDirection(String url) {
        String tag_json_arry = "json_array_req";

        //String url = "https://ilink-app.com/app/select/locations.php";
        //String url = "https://ilink-app.com/app/select/locations.php";
        // Creating volley request obj
        JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();


                        JSONArray jRoutes = null;
                        JSONArray jLegs = null;
                        JSONArray jSteps = null;
                        JSONObject jDistance = null;
                        JSONObject jDuration = null;

                        // Parsing json
                        for (int i = 0; i < 1; i++) {
                            try {
                                //JSONArray obj = response.getJSONArray("routes");
                                //obj.get("time");
                                jRoutes = response.getJSONArray("routes");

                                for (int j=0; j <1; j++) {
                                    jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");

                                    for(int k=0;k<1;k++) {
                                        jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                                        //Toast.makeText(MapsActivity.this, jDuration.toString(), Toast.LENGTH_LONG).show();

                                        u.setText("Vous y serez en "+jDuration.getString("text"));

                                    }

                                }


                            } catch (JSONException e) {
                                //e.printStackTrace();
                                Toast.makeText(MapsActivity.this, "Impossible de generer les marqueurs", Toast.LENGTH_LONG).show();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(MapsActivity.this, "Connexion au serveur impossible"+error.toString(), Toast.LENGTH_LONG).show();


            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq, tag_json_arry);

    }

    //Advice function
    private void advice(){
        final String[] values = new String[] {"Ne plus afficher ce message?"};

        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        //alertDialogBuilder.setMessage("Etes vous sur de vouloir vous deconnecter?");
        alertDialogBuilder





                .setView(inflater.inflate(R.layout.dialog_advice, null))
                .setMultiChoiceItems(values, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {

                                checked = isChecked;
                            }
                        })


                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {


                                if (checked == true) {
                                    //Toast.makeText(getActivity().getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                    //Creating editor to store values to shared preferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    //Adding values to editor
                                    editor.putBoolean(Config.CHECK_BOX_ADVICE_PREF, true);

                                    //Saving values to editor
                                    editor.commit();
                                }


                            }
                        });

        /*alertDialogBuilder.setNegativeButton("Non",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });*/

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        //In onresume fetching value from sharedpreference

        //Fetching the boolean value form sharedpreferences
        notShowing = sharedPreferences.getBoolean(Config.CHECK_BOX_ADVICE_PREF, false);

        //If we will get true
        if(notShowing==false){
            //We will start the Profile Activity
            alertDialog.setCancelable(false);
            alertDialog.show();
        } else {
            //Toast.makeText(getActivity().getApplicationContext(),"Positif",Toast.LENGTH_SHORT).show();

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

    public void getnetworkList() {

        final SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String pays = sharedPreferences.getString(Config.COUNTRY_CODE_SHARED_PREF, "Not Available");
        /*

        Recuperer la liste de reseaux

         */

        String tag_json_arry = "json_array_req";




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


                        Log.d(TAG, response.toString());


                        int markers []= new int[] {R.drawable.location_network1,R.drawable.location_network2,R.drawable.location_network3,R.drawable.location_network4,R.drawable.location_network5,R.drawable.location_network6};
                        int k =0;
                        memberGroupList.clear();


                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);


                                //In onresume fetching value from sharedpreference


                                Iterator<?> iter = obj.keys();

                                for (int j = 0; j< obj.length(); j++)
                                {

                                    String key = (String) iter.next();


                                    if(obj.optString(key).trim()!=null) {

                                        String data = obj.optString(key);

                                        if (data.length()==0) {

                                        } else {



                                            // enleve le champs du pays
                                            if(data.equalsIgnoreCase(pays)) {

                                            } else {

                                                legendeModel members = new legendeModel();
                                                members.setLegende_name(obj.optString(key));
                                                members.setLegende_picture(markers[k]);
                                                // adding movie to movies array
                                                memberGroupList.add(members);
                                                k++;
                                                // ajoute les autres champs
                                                listReseau.add(obj.optString(key));


                                            }


                                        }



                                    } else {

                                    }


                                }
                                //Toast.makeText(RegisterActivity.this, listReseau.toString(), Toast.LENGTH_LONG).show();














                            } catch (JSONException e) {
                                hidePDialog();

                            }


                            if (memberGroupList!=null) {
                                adapter = new LegendeAdapter(MapsActivity.this, memberGroupList);
                            }
                            sv.setAdapter(adapter);


                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();



            }
        });
        // Set timeout request
        movieReq.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        movieReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq, tag_json_arry);
    }




}
