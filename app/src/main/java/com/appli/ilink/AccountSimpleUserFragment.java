package com.appli.ilink;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AccountSimpleUserFragment extends Fragment implements LocationListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_TAG = "tag";
    private static final String REGISTER_URL = "http://ilink-app.com/app/";
    private String TAG;
    public TextView abonnementLabel;
    public TextView emailLabel;
    private LocationManager locationManager;
    private OnFragmentInteractionListener mListener;
    private String mParam1;
    private String mParam2;
    public TextView nomLabel;
    private MaterialDialog pDialog;
    public TextView phoneLabel;
    public TextView prenomLabel;
    public TextView soldeLabel;

    public interface OnFragmentInteractionListener {
        void onAccountSimpleUserFragmentInteraction(Uri uri);
    }

    public static AccountSimpleUserFragment newInstance(String param1, String param2) {
        AccountSimpleUserFragment fragment = new AccountSimpleUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AccountSimpleUserFragment() {
        this.TAG = KEY_TAG;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_simple_user, container, false);
        this.nomLabel = (TextView) rootView.findViewById(R.id.lastnameAccountView);
        this.prenomLabel = (TextView) rootView.findViewById(R.id.firstnameAccountView);
        this.phoneLabel = (TextView) rootView.findViewById(R.id.phoneAccountView);
        this.emailLabel = (TextView) rootView.findViewById(R.id.emailAccountView);
        this.abonnementLabel = (TextView) rootView.findViewById(R.id.balanceAccountView);
        Button modifyProfile = (Button) rootView.findViewById(R.id.button_modify_profile);
        Button reSubscribe = (Button) rootView.findViewById(R.id.button_re_subscribe);

        modifyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ModifyProfileActivity.class);
                startActivity(i);
                getActivity().finish();

            }

        });

        reSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), RenewSubscriptionActivity.class);
                startActivity(i);
                getActivity().finish();

            }

        });
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, 0);
        String email = sharedPreferences.getString(KEY_EMAIL, "Not Available");
        String lastname = sharedPreferences.getString(RegisterSimpleActivity.KEY_LASTNAME, "Not Available");
        String firstname = sharedPreferences.getString(RegisterSimpleActivity.KEY_FIRSTNAME, "Not Available");
        String phone = sharedPreferences.getString(TamponGeolocatedActivity.KEY_PHONE, "Not Available");
        this.nomLabel.setText(lastname);
        this.prenomLabel.setText(firstname);
        this.phoneLabel.setText(phone);
        this.emailLabel.setText(email);
        this.abonnementLabel.setText("En cours");
        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (this.mListener != null) {
            this.mListener.onAccountSimpleUserFragmentInteraction(uri);
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }

    public void onResume() {
        super.onResume();
        this.locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (this.locationManager.isProviderEnabled("gps")) {
            abonnementGPS();
        }
    }

    public void onPause() {
        super.onPause();
        desabonnementGPS();
    }

    public void abonnementGPS() {
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
        this.locationManager.requestLocationUpdates("gps", 5000, 10.0f, this);
    }

    public void desabonnementGPS() {
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
        this.locationManager.removeUpdates(this);
    }

    public void onLocationChanged(final Location location) {
        StringBuilder msg = new StringBuilder("lat : ");
        msg.append(location.getLatitude());
        msg.append("; lng : ");
        msg.append(location.getLongitude());
        final String email = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, 0).getString(KEY_EMAIL, "Not Available");
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
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
                        Toast.makeText(getActivity(), "Impossible de mettre a jour votre position : "+error.toString(), Toast.LENGTH_LONG).show();
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

    private class CheckUsers extends AsyncTask<String, String, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new MaterialDialog.Builder(getActivity())
                    .title("Attendez svp!")
                    .content("Vérification de la connexion réseau")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            nomLabel.setText(" ");
            prenomLabel.setText(" ");
            phoneLabel.setText(" ");
            emailLabel.setText(" ");
            abonnementLabel.setText(" ");
            soldeLabel.setText(" ");
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
                    Toast.makeText(getActivity(), "Url ivalide : "+ e1.toString(), Toast.LENGTH_LONG).show();
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
                pDialog.dismiss();
                try {
                    user();
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Toast.makeText(getActivity(), "Impossible de recuperer vos informations : "+e.toString(), Toast.LENGTH_LONG).show();
                }
            } else {
                pDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Erreur lors de la connexion au réseau", Toast.LENGTH_LONG).show();

            }
        }
    }


    private void user() throws JSONException {
        String tag_json_arry = "json_array_req";

        pDialog = new MaterialDialog.Builder(getActivity())
                .title("Attendez svp!")
                .content("Chargement des informations...")
                .progress(true, 0)
                .cancelable(false)
                .show();


        String url = "http://ilink-app.com/app/select/users.php";

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");



        Map<String, String> params = new HashMap<String, String>();

        params.put(KEY_EMAIL, email);
        params.put(KEY_TAG, "getuser");

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        hidePDialog();
                        Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                        // Toast.makeText(getActivity(), "Localisation a jour!", Toast.LENGTH_LONG).show();

                        try {
                            JSONObject obj = response.getJSONObject(0);
                            nomLabel.setText(obj.getString("lastname"));
                            prenomLabel.setText(obj.getString("firstname"));
                            phoneLabel.setText(obj.getString("phone"));
                            emailLabel.setText(obj.getString("email"));
                            abonnementLabel.setText("En cours");

                            if(obj.getString("balance").isEmpty() || obj.getString("balance").equalsIgnoreCase("0")){

                                soldeLabel.setText("0 CFA");

                            } else {
                                soldeLabel.setText(obj.getString("balance")+" CFA");
                            }

                        } catch (JSONException e) {
                            //e.printStackTrace();

                            Toast.makeText(getActivity(), "Impossible de recuperer vos informations : "+e.toString(), Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hidePDialog();
                        Toast.makeText(getActivity(),"Connexion au serveur impossible :" +error.toString(), Toast.LENGTH_LONG).show();

                        nomLabel.setText(error.toString());
                    }
                }




        );
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        //requestQueue.add(userRequest);

        requestQueue.add(jsObjRequest);

    }



    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
