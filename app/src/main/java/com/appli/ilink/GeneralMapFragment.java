package com.appli.ilink;

import android.*;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
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
import com.appli.ilink.lib.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

public class GeneralMapFragment extends Fragment implements OnMapReadyCallback, OnMapClickListener, OnMarkerClickListener, RoutingListener, OnConnectionFailedListener, ConnectionCallbacks {
    private static int ADVICE_TIME_OUT = 0;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int[] COLORS = new int[]{R.color.primary_dark, R.color.primary, R.color.primary_light, R.color.accent, R.color.primary_dark_material_light};
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_COUNTRY_CODE = "country_code";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_GENRE = "genre";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_MEMBER_CODE = "member_code";
    private static final String KEY_NETWORK = "network";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PHOTO = "photo";
    public static final String KEY_TAG = "tag";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_VALIDATION_CODE = "validation_code";
    private static final String LOG_TAG = "MyActivity";
    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1;
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
    private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";
    private static final long POINT_RADIUS = 1000;
    private static final long PROX_ALERT_EXPIRATION = -1;
    private static final String PROX_ALERT_INTENT = "com.nkala.ProximityAlert";
    private static final String REGISTER_URL = "https://ilink-app.com/app/";
    static String laTitude;
    static String lonGitude;
    private static final NumberFormat nf = new DecimalFormat("##.########");
    private String TAG;
    private boolean checked;
    private LatLng currentLoc;
    DatabaseHandler db;
    protected LatLng end;
    private GoogleMap gMap;
    private ImageButton imageBtnRoute2;
    private Intent intent;
    Double latitude;
    private LocationManager locationManager;
    Double longitude;
    protected GoogleApiClient mGoogleApiClient;
    private OnFragmentInteractionListener mListener;
    private String mParam1;
    private String mParam2;
    private RelativeLayout mapLayout;
    private Marker marker;
    String nameSnippet;
    private boolean notShowing;
    private MaterialDialog pDialog;
    private ArrayList<Polyline> polylines;
    private String provider;
    private PendingIntent proximityIntent;
    private Button sendRoute2;
    private SharedPreferences sharedPreferences;
    protected LatLng start;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneralMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneralMapFragment newInstance(String param1, String param2) {
        GeneralMapFragment fragment = new GeneralMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public GeneralMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment


        View rootView = inflater.inflate(R.layout.fragment_general_map, container, false);
        sendRoute2 = (Button) rootView.findViewById(R.id.sendRoute2);
        imageBtnRoute2 = (ImageButton) rootView.findViewById(R.id.imageBtnRoute2);

        db = new DatabaseHandler(getActivity());


        new CheckPharmacie().execute();
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }





        sharedPreferences =  getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        nameSnippet = sharedPreferences.getString(Config.LASTNAME_SHARED_PREF, "Not Available");
        laTitude = sharedPreferences.getString(Config.LATITUDE_SHARED_PREF, "Pas disponible");

        lonGitude = sharedPreferences.getString(Config.LONGITUDE_SHARED_PREF, "Pas disponible");





        polylines = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


        } else {


        }


        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.genralMap)).getMapAsync(GeneralMapFragment.this);



/*
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                marker.showInfoWindow();
                String title = marker.getTitle();


                if (title.equals("Vous êtes localisé ici!") || title.equals("Gabon")) {
                    sendRoute2.setVisibility(View.INVISIBLE);
                    imageBtnRoute2.setVisibility(View.INVISIBLE);

                } else {

                    //addProximityAlert(Double.parseDouble(laTitude), Double.parseDouble(lonGitude));

                    end = marker.getPosition();
                    sendRoute2.setVisibility(View.INVISIBLE);
                    imageBtnRoute2.setVisibility(View.INVISIBLE);

                }
                return true;
            }
        });

        onMapReady(gMap);


        gMap.setOnMapClickListener(this);

        gMap.setOnMarkerClickListener(this);



        gMap.getUiSettings().setCompassEnabled(true);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setCompassEnabled(true);
        gMap.getUiSettings().setMapToolbarEnabled(false);

*/
        //Initialize CheckBox


        // create map
        /*gMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.genralMap)).getMap();

        */


        sendRoute2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();


            }

        });

        sendRoute2.setVisibility(View.INVISIBLE);


        imageBtnRoute2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();


            }

        });
        imageBtnRoute2.setVisibility(View.INVISIBLE);

        //marker.showInfoWindow();
        ToggleButton toggleSimple = (ToggleButton) rootView.findViewById(R.id.sendReseau2);
        toggleSimple.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == false) {
                    gMap.clear();
                    gMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(0.4112103, 9.4346296);
        gMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */


                            double code = Math.random();
                            //final LatLng latLng = new LatLng(latitude, longitude);

                            final String latitude = sharedPreferences.getString(Config.LATITUDE_SHARED_PREF, "Not Available");
                            final String longitude = sharedPreferences.getString(Config.LONGITUDE_SHARED_PREF, "Not Available");

                            final LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                            marker = gMap.addMarker(new MarkerOptions().title("Vous êtes localisé ici!").snippet(nameSnippet).position(
                                    latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));


                            gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                                    marker.showInfoWindow();
                                    String title = marker.getTitle();


                                    if (title.equals("Vous êtes localisé ici!") || title.equals("Gabon")) {
                                        sendRoute2.setVisibility(View.INVISIBLE);
                                        imageBtnRoute2.setVisibility(View.INVISIBLE);

                                    } else {

                                        //addProximityAlert(Double.parseDouble(laTitude), Double.parseDouble(lonGitude));

                                        end = marker.getPosition();
                                        sendRoute2.setVisibility(View.INVISIBLE);
                                        imageBtnRoute2.setVisibility(View.INVISIBLE);

                                    }
                                    return true;
                                }
                            });


                            gMap.getUiSettings().setZoomControlsEnabled(true);
                            gMap.getUiSettings().setMyLocationButtonEnabled(true);
                            gMap.getUiSettings().setCompassEnabled(true);
                            gMap.getUiSettings().setMapToolbarEnabled(false);


                            start = latLng;
                            marker.showInfoWindow();

                            //Toast.makeText(getActivity(), start.toString() + code, Toast.LENGTH_SHORT).show();
                        }
                    });
                    new CheckLocations().execute();


                } else {

                    gMap.clear();
                    new CheckAllLocations().execute();
                    gMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(0.4112103, 9.4346296);
        gMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */


                            double code = Math.random();
                            //final LatLng latLng = new LatLng(latitude, longitude);

                            final String latitude = sharedPreferences.getString(Config.LATITUDE_SHARED_PREF, "Not Available");
                            final String longitude = sharedPreferences.getString(Config.LONGITUDE_SHARED_PREF, "Not Available");
                            final LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                            marker = gMap.addMarker(new MarkerOptions().title("Vous êtes localisé ici!").snippet(nameSnippet).position(
                                    latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));


                            gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                                    marker.showInfoWindow();
                                    String title = marker.getTitle();


                                    if (title.equals("Vous êtes localisé ici!") || title.equals("Gabon")) {
                                        sendRoute2.setVisibility(View.INVISIBLE);
                                        imageBtnRoute2.setVisibility(View.INVISIBLE);

                                    } else {

                                        //addProximityAlert(currentLoc.getLatitude(), currentLoc.getLongitude());

                                        end = marker.getPosition();
                                        sendRoute2.setVisibility(View.INVISIBLE);
                                        imageBtnRoute2.setVisibility(View.INVISIBLE);

                                    }
                                    return true;
                                }
                            });


                            gMap.getUiSettings().setZoomControlsEnabled(true);
                            gMap.getUiSettings().setMyLocationButtonEnabled(true);
                            gMap.getUiSettings().setCompassEnabled(true);
                            gMap.getUiSettings().setMapToolbarEnabled(false);


                            start = latLng;
                            marker.showInfoWindow();

                            //Toast.makeText(getActivity(), start.toString() + code, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        // Display advice for route button
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
                //advice();


            }
        }, ADVICE_TIME_OUT);


        return rootView;
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onGeneralMapFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       /* try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onMapClick(LatLng latLng) {
        sendRoute2.setVisibility(View.INVISIBLE);
        imageBtnRoute2.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;


        final LatLng latLng = new LatLng(Double.parseDouble(laTitude), Double.parseDouble(lonGitude));


        marker = gMap.addMarker(new MarkerOptions().title("Vous localisé ici!").snippet(nameSnippet).position(
                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));

        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker)
            {
                marker.showInfoWindow();
                String title = marker.getTitle();



                if(title.equals("Vous êtes localisé ici!")|| title.equals("Gabon")) {
                    sendRoute2.setVisibility(View.INVISIBLE);
                    imageBtnRoute2.setVisibility(View.INVISIBLE);

                } else {

                    //addProximityAlert(currentLoc.getLatitude(), currentLoc.getLongitude());

                    end = marker.getPosition();
                    sendRoute2.setVisibility(View.INVISIBLE);

                }
                return true;
            }
        });

        //gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.getUiSettings().setCompassEnabled(true);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.getUiSettings().setCompassEnabled(true);
        gMap.getUiSettings().setMapToolbarEnabled(false);

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        marker.showInfoWindow();

        start = latLng;

        //Toast.makeText(getActivity(), start.toString(), Toast.LENGTH_SHORT).show();


    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onGeneralMapFragmentInteraction(Uri uri);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();



    }






    private class CheckPharmacie extends AsyncTask<String, String, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new MaterialDialog.Builder(getActivity())
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


            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                if (Util.Operations.isOnline(getActivity())) {
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Pas de connexion internet", Toast.LENGTH_SHORT).show();
                }

            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean th) {

            if (th == true) {
                pDialog.dismiss();
                pharmacies();
            } else {
                pDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Erreur lors de la connexion au réseau", Toast.LENGTH_LONG).show();

            }
        }
    }


    private void pharmacies() {
        String tag_json_arry = "json_array_req";

        pDialog = new MaterialDialog.Builder(getActivity())
                .title("Attendez svp!")
                .content("Chargement des marqueurs...")
                .progress(true, 0)
                .cancelable(false)
                .show();


        String url = "https://ilink-app.com/app/select/locations.php";
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        //db.resetTables();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++)
                            try {
                                JSONObject obj = response.getJSONObject(i);




                                final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");
                                final String phone = sharedPreferences.getString(Config.PHONE_SHARED_PREF, "Not Available");
                                final String network = sharedPreferences.getString(Config.NETWORK_SHARED_PREF, "Not Available");

                                /*if (obj.getString("phone").equals(phone)) {

                                } else {
                                    db.addUsers(obj.getString("firstname"), obj.getString("lastname"), obj.getString("email"), obj.getString("phone"), obj.getString("country_code"), obj.getString("network"), obj.getString("member_code"),
                                            obj.getString("category"), obj.getString("balance"), obj.getString("latitude"), obj.getString("longitude"), obj.getString("validation_code"), obj.getString("active"));

                                }
                                */


                                if (obj.getString("latitude") != null && obj.getString("network").equals(network) && obj.getString("category").equals("geolocated")&& obj.getString("active").equals("oui")) {
                                    final LatLng latLng = new LatLng(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")));


                                    if (obj.getString("phone").equals(phone)) {
                                        //Toast.makeText(getActivity().getApplicationContext(), "Email "+email, Toast.LENGTH_LONG).show();

                                        marker = gMap.addMarker(new MarkerOptions().title("Vous êtes localisé ici!").snippet(nameSnippet).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));

                                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                        marker.showInfoWindow();
                                    } else {

                                        //Toast.makeText(getActivity().getApplicationContext(), "Pas Email "+email, Toast.LENGTH_LONG).show();

                                        gMap.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));


                                    }

                                } else {

                                    /*final LatLng latLng = new LatLng(0, 0);
                                    gMap.addMarker(new MarkerOptions().title("Gabon").position(
                                            latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));
                                            */

                                }
                                gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
                                {
                                    @Override
                                    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker)
                                    {
                                        marker.showInfoWindow();
                                        String title = marker.getTitle();


                                        if (title.equals("Vous êtes localisé ici!") || title.equals("Gabon")) {
                                            sendRoute2.setVisibility(View.INVISIBLE);
                                            imageBtnRoute2.setVisibility(View.INVISIBLE);

                                        } else {

                                            //addProximityAlert(Double.parseDouble(laTitude), Double.parseDouble(lonGitude));

                                            end = marker.getPosition();
                                            sendRoute2.setVisibility(View.INVISIBLE);
                                            imageBtnRoute2.setVisibility(View.INVISIBLE);

                                        }
                                        return true;
                                    }
                                });


                            } catch (JSONException e) {
                                //e.printStackTrace();
                                Toast.makeText(getActivity(), "Impossible de recuperer les marqueurs : " + e.toString(), Toast.LENGTH_LONG).show();
                            }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Erreur : " + error.getMessage());
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


    public void sendRequest()
    {
        if(Util.Operations.isOnline(getActivity()))
        {
            route();
        }
        else
        {
            Toast.makeText(getActivity(),"Aucune Connexion Internet",Toast.LENGTH_SHORT).show();
        }
    }

    public void route()
    {
        if(start==null || end==null)
        {
            Toast.makeText(getActivity(),"Votre Position et votre destination semblent être inconnus.",Toast.LENGTH_SHORT).show();
        }
        else
        {


            pDialog = new MaterialDialog.Builder(getActivity())
                    .title("Attendez svp!")
                    .content("Récupération des informations sur le chemin...")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(start, end)
                    .build();
            routing.execute();

        }
    }


    @Override
    public void onRoutingFailure(RouteException e) {
        // The Routing request failed
        pDialog.dismiss();
        if(e != null) {
            Toast.makeText(getActivity(), "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getActivity(), "Une erreur s'est produite, Essayez encore", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex)
    {
        pDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        gMap.moveCamera(center);


        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 1);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = gMap.addPolyline(polyOptions);
            polylines.add(polyline);

            //Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
            //Snackbar.make(mapLayout, "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Snackbar.LENGTH_LONG)
            //.setAction("Action", null).show();
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
        gMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
        gMap.addMarker(options);

    }

    @Override
    public void onRoutingCancelled() {
        Log.i(LOG_TAG, "La recherche du chemin a été annulée.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.v(LOG_TAG, connectionResult.toString());
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public boolean onMarkerClick (final Marker marker) {
        Log.i("GeneralMapFragment", "onMarkerClick");



        return false;

    }


    //Advice function
    private void advice(){
        final String[] values = new String[] {"Ne plus afficher ce message?"};

        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //alertDialogBuilder.setMessage("Etes vous sur de vouloir vous deconnecter?");
        alertDialogBuilder





                .setView(inflater.inflate(R.layout.dialog_advice, null))
                .setMultiChoiceItems(values, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                // If the user checked the item, add it to the selected items
// Else, if the item is already in the array, remove it
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


    private class CheckLocations extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new MaterialDialog.Builder(getActivity())
                    .title("Attendez svp!")
                    .content("Chargement des marqueurs...")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
        }

        /**
         * Gets current device state and checks for working internet connection by trying Google.
         **/
        @Override
        protected Boolean doInBackground(String... args) {


            ConnectivityManager cm = (ConnectivityManager) getActivity().getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                if (Util.Operations.isOnline(getActivity())) {
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Pas de connexion internet", Toast.LENGTH_SHORT).show();
                }

            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean th) {

            if (th == true) {
                pDialog.dismiss();
                locations();
            } else {
                pDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Erreur lors de la connexion au réseau", Toast.LENGTH_LONG).show();

            }
        }
    }


    private void locations() {
        String tag_json_arry = "json_array_req";


        pDialog = new MaterialDialog.Builder(getActivity())
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

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);


                                //Fetching the boolean value form sharedpreferences
                                final String network = sharedPreferences.getString(Config.NETWORK_SHARED_PREF, "Pas disponible");
                                final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Pas disponible");
                                final String phone = sharedPreferences.getString(Config.PHONE_SHARED_PREF, "Pas disponible");


                                if (obj.getString("latitude") != null && obj.getString("network").equals(network) && obj.getString("category").equals("geolocated")&& obj.getString("active").equals("oui")) {

                                    final LatLng latLng = new LatLng(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")));

                                    currentLoc = new LatLng(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")));
                                    if (obj.getString("phone").equals(phone)) {
                                        //Toast.makeText(getActivity().getApplicationContext(), "Email "+email, Toast.LENGTH_LONG).show();


                                        laTitude = sharedPreferences.getString(Config.LATITUDE_SHARED_PREF, "Pas disponible");

                                        lonGitude = sharedPreferences.getString(Config.LONGITUDE_SHARED_PREF, "Pas disponible");

                                        marker = gMap.addMarker(new MarkerOptions().title("Vous êtes localisé ici!").snippet(nameSnippet).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));
                                        marker.setVisible(true);
                                        marker.showInfoWindow();
                                    } else {

                                        //Toast.makeText(getActivity().getApplicationContext(), "Pas Email "+email, Toast.LENGTH_LONG).show();

                                        gMap.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));


                                    }
                                    //addProximityAlert(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")));

                                } else {


                                }


                            } catch (JSONException e) {
                                //e.printStackTrace();
                                Toast.makeText(getActivity(), "Impossible de generer les marqueurs", Toast.LENGTH_LONG).show();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Connexion au serveur impossible : " + error.toString(), Toast.LENGTH_LONG).show();
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

            pDialog = new MaterialDialog.Builder(getActivity())
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


            ConnectivityManager cm = (ConnectivityManager) getActivity().getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                if (Util.Operations.isOnline(getActivity())) {
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Pas de connexion internet", Toast.LENGTH_SHORT).show();
                }

            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean th) {

            if (th == true) {
                pDialog.dismiss();
                allLocations();
            } else {
                pDialog.dismiss();
                Toast.makeText(getActivity().getBaseContext().getApplicationContext(), "Erreur lors de la connexion au réseau", Toast.LENGTH_LONG).show();

            }
        }
    }


    private void allLocations() {
        String tag_json_arry = "json_array_req";


        pDialog = new MaterialDialog.Builder(getActivity())
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

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);


                                //Fetching the boolean value form sharedpreferences
                                final String network = sharedPreferences.getString(Config.NETWORK_SHARED_PREF, "Pas disponible");
                                final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Pas disponible");
                                final String phone = sharedPreferences.getString(Config.PHONE_SHARED_PREF, "Pas disponible");


                                if (obj.getString("latitude") != null && obj.getString("category").equals("geolocated")&& obj.getString("active").equals("oui")) {

                                    final LatLng latLng = new LatLng(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")));




                                    if (obj.getString("phone").equalsIgnoreCase(phone)){

                                        laTitude = sharedPreferences.getString(Config.LATITUDE_SHARED_PREF, "Pas disponible");

                                        lonGitude = sharedPreferences.getString(Config.LONGITUDE_SHARED_PREF, "Pas disponible");

                                        marker = gMap.addMarker(new MarkerOptions().title("Vous êtes localisé ici!").snippet(nameSnippet).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));
                                        marker.setVisible(true);
                                        marker.showInfoWindow();

                                    } else {
                                        gMap.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));
                                    }

                                    //addProximityAlert(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")));

                                } else {

                                    /*final LatLng latLng = new LatLng(0, 0);
                                    gMap.addMarker(new MarkerOptions().title("Gabon").position(
                                            latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));
                                            */

                                }


                            } catch (JSONException e) {
                                //e.printStackTrace();
                                Toast.makeText(getActivity(), "Impossible de generer les marqueurs", Toast.LENGTH_LONG).show();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Connexion au serveur impossible : " + error.toString(), Toast.LENGTH_LONG).show();
                hidePDialog();

            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq, tag_json_arry);

    }

    ///New Proximity
    public void onClearProximityAlertClick() {
        if (currentLoc != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            } else {
                locationManager.removeProximityAlert(proximityIntent);

            }

        } else {

            //Intent i = new Intent(getActivity(), ProximityAlertService.class);
            //stopService(i);

        }


    }



    private void saveProximityAlertPointManual() {

        double latitude = end.latitude;
        double longitude = end.longitude;


        if (Double.compare(latitude, Double.NaN) == 0
                && Double.compare(longitude, Double.NaN) == 0) {
            Toast.makeText(getActivity(), "No currentloc enter. Aborting...",
                    Toast.LENGTH_LONG).show();
            return;
        }

        saveCoordinatesInPreferences((float) latitude, (float) longitude);
        // addProximityAlert(latitude, longitude);

    }

    private void saveProximityAlertPoint() {
        // Location currentloc = null;
        // if (LocationManager.GPS_PROVIDER != null){



        // }

        if (currentLoc == null) {
            Toast.makeText(getActivity(), "No last known currentloc. Aborting...",
                    Toast.LENGTH_LONG).show();
            return;
        }

        saveCoordinatesInPreferences((float) Double.parseDouble(laTitude),
                (float) Double.parseDouble(lonGitude));
        //addProximityAlert(Double.parseDouble(laTitude), Double.parseDouble(lonGitude));
    }

    private void addProximityAlert(double latitude, double longitude) {


        int radius = 100;

        if (radius != 0) {

            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            } else {
                locationManager.addProximityAlert(latitude, // the latitude of the
                        // central point of the
                        // alert region
                        longitude, // the longitude of the central point of the
                        // alert region
                        // POINT_RADIUS, // the radius of the central point of the
                        // alert region, in meters
                        radius, // the radius of the central point of the alert
                        // region, in meters
                        PROX_ALERT_EXPIRATION, // time for this proximity alert, in
                        // milliseconds, or -1 to indicate
                        // no expiration
                        proximityIntent // will be used to generate an Intent to
                        // fire when entry to or exit from the alert
                        // region is detected
                );

            }


            //Toast.makeText(getApplicationContext(), "Android Alert Added",
            //Toast.LENGTH_SHORT).show();

        } else {

            // service
            /*Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            Intent intent = new Intent(getActivity(), ProximityAlertService.class);
            intent.putExtra(ProximityAlertService.LATITUDE_INTENT_KEY, latitude);
            intent.putExtra(ProximityAlertService.LONGITUDE_INTENT_KEY,
                    longitude);
            intent.putExtra(ProximityAlertService.RADIUS_INTENT_KEY,
                    (float) radius);
            //startService(intent);

            Toast.makeText(getActivity().getApplicationContext(), "custom Alert Added",
                    Toast.LENGTH_SHORT).show();
*/
        }

        IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
        //registerReceiver(new ProximityIntentReceiver(), filter);


    }

    private void populateCoordinatesFromLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            Location currentloc = locationManager.getLastKnownLocation(provider);

            if (currentloc != null) {
            }
        }

    }

    private void saveCoordinatesInPreferences(float latitude, float longitude) {

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putFloat(POINT_LATITUDE_KEY, latitude);
        prefsEditor.putFloat(POINT_LONGITUDE_KEY, longitude);
        prefsEditor.commit();
    }

    private Location retrievelocationFromPreferences() {
        SharedPreferences prefs = getActivity().getSharedPreferences(getClass()
                .getSimpleName(), Context.MODE_PRIVATE);
        Location currentloc = new Location("POINT_LOCATION");
        currentloc.setLatitude(prefs.getFloat(POINT_LATITUDE_KEY, 0));
        currentloc.setLongitude(prefs.getFloat(POINT_LONGITUDE_KEY, 0));
        return currentloc;
    }

    ///End Proximity


    /// Start Validation Number Generator

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(128);
        char tempChar;
        for (int i = 0; i < randomLength; i++ ) {

            tempChar = (char) (generator.nextInt(96)+32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();

    }
}
