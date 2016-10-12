package com.appli.ilink;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.appli.ilink.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupMapFragment extends Fragment implements LocationListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, RoutingListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String REGISTER_URL = "http://ilink-app.com/app/";


    public static final String KEY_EMAIL = "email";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_TAG = "tag";
    private static int ADVICE_TIME_OUT = 3000;
    private boolean notShowing = false;


    private LocationManager locationManager;
    private GoogleMap gMap;
    private Marker marker;
    private boolean checked;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ProgressDialog pDialog;
    private String TAG = "tag";
    private RelativeLayout mapLayout;

    // Route


    protected LatLng start;
    protected LatLng end;
    private static final String LOG_TAG = "MyActivity";
    protected GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;
    private ArrayList<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark, R.color.primary, R.color.primary_light, R.color.accent, R.color.primary_dark_material_light};
    private Button sendRoute2;
    Double latitude;
    Double longitude;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneralMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupMapFragment newInstance(String param1, String param2) {
        GroupMapFragment fragment = new GroupMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public GroupMapFragment() {
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


        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String lat = sharedPreferences.getString(Config.LATITUDE_SHARED_PREF, "Not Available");
        final String lng = sharedPreferences.getString(Config.LONGITUDE_SHARED_PREF, "Not Available");
        Location location = null;
        latitude = Double.parseDouble(lat);
        longitude = Double.parseDouble(lng);

        View rootView = inflater.inflate(R.layout.fragment_general_map, container, false);


        polylines = new ArrayList<>();
        /*mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(getActivity().getApplicationContext())
                .addOnConnectionFailedListener(getActivity().getApplicationContext())
                .build();
        MapsInitializer.initialize(getActivity());
        mGoogleApiClient.connect();
        */

        mapLayout = (RelativeLayout) rootView.findViewById(R.id.maplayout2);

        final LatLng latLng = new LatLng(latitude, longitude);
        start = latLng;

        //Initialize CheckBox


        // create map
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.genralMap)).getMapAsync(this);
        /*marker = gMap.addMarker(new MarkerOptions().title("Vous êtes ici").position(
                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));


        gMap.setOnMarkerClickListener(this);


        new CheckPharmacie().execute();

        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.getUiSettings().setCompassEnabled(true);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.getUiSettings().setCompassEnabled(true);
        gMap.getUiSettings().setMapToolbarEnabled(false);

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


        marker.showInfoWindow();
        */

        sendRoute2 = (Button) rootView.findViewById(R.id.sendRoute2);
        sendRoute2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();


            }

        });

        sendRoute2.setVisibility(View.INVISIBLE);

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
                advice();


            }
        }, ADVICE_TIME_OUT);

        return rootView;
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
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String lat = sharedPreferences.getString(Config.LATITUDE_SHARED_PREF, "Not Available");
        final String lng = sharedPreferences.getString(Config.LONGITUDE_SHARED_PREF, "Not Available");
        Location location = null;
        latitude = Double.parseDouble(lat);
        longitude = Double.parseDouble(lng);

        final LatLng latLng = new LatLng(latitude, longitude);


        marker = gMap.addMarker(new MarkerOptions().title("Vous êtes ici").position(
                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));

        gMap.setOnMarkerClickListener(this);

        //mMap.getUiSettings().setZoomControlsEnabled(true);
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

        //Obtention de la référence du service
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //Si le GPS est disponible, on s'y abonne
        //if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
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
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    /**
     * Méthode permettant de se désabonner de la localisation par GPS.
     */
    public void desabonnementGPS() {
        //Si le GPS est disponible, on s'y abonne
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLocationChanged(final Location location) {
        //Fetching email from shared preferences

        //On affiche dans un Toat la nouvelle Localisation
        final StringBuilder msg = new StringBuilder("lat : ");
        msg.append(location.getLatitude());
        msg.append("; lng : ");
        msg.append(location.getLongitude());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");

        //Toast.makeText(getActivity(), msg.toString(), Toast.LENGTH_SHORT).show();

        //Mise à jour des coordonnées
        final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        marker.setPosition(latLng);
        marker.showInfoWindow();
        start = latLng;





        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();
                        // Toast.makeText(getActivity(), "Localisation a jour!", Toast.LENGTH_LONG).show();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"Connexion au serveur impossible : " +error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_EMAIL, email);
                params.put(KEY_LATITUDE, String.valueOf(location.getLatitude()));
                params.put(KEY_LONGITUDE, String.valueOf(location.getLongitude()));
                params.put(KEY_TAG, "updateLocation");
                return params;
            }


        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
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


    private class CheckPharmacie extends AsyncTask<String, String, Boolean> {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(getActivity());
            nDialog.setTitle("Vérification de la connexion réseau");
            nDialog.setMessage("Chargement..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        /**
         * Gets current device state and checks for working internet connection by trying Google.
         **/
        @Override
        protected Boolean doInBackground(String... args) {


            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    //e1.printStackTrace();
                    Toast.makeText(getActivity(), "Url invalide : "+e1.toString(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();

                    Toast.makeText(getActivity(), "Connexion au serveur impossible : "+e.toString(), Toast.LENGTH_LONG).show();
                }
            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean th) {

            if (th == true) {
                nDialog.dismiss();
                pharmacies();
            } else {
                nDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Erreur lors de la connexion au réseau", Toast.LENGTH_LONG).show();

            }
        }
    }


    private void pharmacies() {
        String tag_json_arry = "json_array_req";
        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Chargement marqueurs...");
        pDialog.show();


        String url = "http://ilink-app.com/app/select/locations.php";
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

                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");


                                if (obj.getString("latitude") != null) {
                                    final LatLng latLng = new LatLng(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")));


                                    if (obj.getString("email").equals(email)) {
                                        //Toast.makeText(getActivity().getApplicationContext(), "Email "+email, Toast.LENGTH_LONG).show();

                                        Marker userMarker = gMap.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));
                                        userMarker.setVisible(false);
                                    } else {

                                        //Toast.makeText(getActivity().getApplicationContext(), "Pas Email "+email, Toast.LENGTH_LONG).show();

                                        gMap.addMarker(new MarkerOptions().title(obj.getString("lastname")).position(
                                                latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_member)));


                                    }

                                } else {

                                    final LatLng latLng = new LatLng(0, 0);
                                    gMap.addMarker(new MarkerOptions().title("Gabon").position(
                                            latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));

                                }


                            } catch (JSONException e) {
                                //e.printStackTrace();
                                Toast.makeText(getActivity(), "Impossible de recuperer les marqueurs : "+e.toString(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(getActivity(),"No internet connectivity",Toast.LENGTH_SHORT).show();
        }
    }

    public void route()
    {
        if(start==null || end==null)
        {
            Toast.makeText(getActivity(),"Position and destination unknown.",Toast.LENGTH_SHORT).show();
        }
        else
        {
            progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
                    "Fetching route information.", true);
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
        progressDialog.dismiss();
        if(e != null) {
            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getActivity(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex)
    {
        progressDialog.dismiss();
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
            polyOptions.width(10 + i * 3);
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
        Log.i(LOG_TAG, "Routing was cancelled.");
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
        Log.i("MapsActivity", "onMarkerClick");


        end = marker.getPosition();
        sendRoute2.setVisibility(View.VISIBLE);
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
                                    //Creating a shared preference
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

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
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME,Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        notShowing = sharedPreferences.getBoolean(Config.CHECK_BOX_ADVICE_PREF, false);

        //If we will get true
        if(notShowing==false){
            //We will start the Profile Activity
            alertDialog.show();
        } else {
            //Toast.makeText(getActivity().getApplicationContext(),"Positif",Toast.LENGTH_SHORT).show();

        }




    }

}
