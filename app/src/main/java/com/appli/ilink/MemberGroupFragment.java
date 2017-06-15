package com.appli.ilink;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appli.ilink.adapter.memberGroupAdapter;
import com.appli.ilink.app.AppController;
import com.appli.ilink.lib.DatabaseHandler;
import com.appli.ilink.model.memberGroup;
import com.appli.ilink.model.usersModel;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberGroupFragment extends Fragment implements OnItemClickListener, OnRefreshListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String DELETE_URL = "https://ilink-app.com/app/select/delete.php";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_NETWORK = "network";
    public static final String KEY_MEMBER_CODE = "member_code";
    public static final String KEY_CATEGORY = "category";
    private static final String TAG;
    private memberGroupAdapter adapter;
    DatabaseHandler db;
    private String emailRemote;
    private ImageButton imgBtnDown;
    private ImageButton imgBtnUp;
    private ListView listView;
    private OnFragmentInteractionListener mListener;
    private String mParam1;
    private String mParam2;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<memberGroup> memberGroupList;
    private ListView memberListView;
    private View memberView;
    private MaterialDialog pDialog;
    private String phoneRemote;
    private SharedPreferences sharedPreferences;

    static {
        TAG = MainActivity.class.getSimpleName();
    }

    public MemberGroupFragment() {
        this.memberGroupList = new ArrayList();
    }

    public static MemberGroupFragment newInstance(int sectionNumber) {
        MemberGroupFragment fragment = new MemberGroupFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.memberView = inflater.inflate(R.layout.fragment_member_group, container, false);
        this.memberListView = (ListView) this.memberView.findViewById(R.id.listMemberGroupAll);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) this.memberView.findViewById(R.id.swipeRefreshLayoutMemberGroup);
        this.mSwipeRefreshLayout.setOnRefreshListener(this);
        this.sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, 0);
        this.imgBtnUp = (ImageButton) this.memberView.findViewById(R.id.imgBtnUp);
        this.imgBtnDown = (ImageButton) this.memberView.findViewById(R.id.imgBtnDown);

        this.imgBtnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getlocalUsersAsc();
            }
        });

        this.imgBtnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getlocalUsersDsc();
            }
        });
        new CheckUsers().execute();
        this.db = new DatabaseHandler(getActivity());
        this.memberListView.setOnItemClickListener(this);
        return this.memberView;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        this.phoneRemote = ((TextView) view.findViewById(R.id.textMemberGroupPhone)).getText().toString();
        deleteUser(this.phoneRemote);
    }

    public void onRefresh() {
        this.mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshUsers();
                //avertie le SwipeRefreshLayout que la mise à jour a été effectuée
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 5000);
    }
    //Logout function
    private void deleteUser(String phone){
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity( ));
        alertDialogBuilder.setMessage("Etes vous sur de vouloir supprimer le "+phone+"?");
        alertDialogBuilder.setPositiveButton("Oui",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        new   DeleteUser().execute();
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

    private class DeleteUser extends AsyncTask<String, String, Boolean> {


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

            if (th) {
                pDialog.dismiss();
                deleteFromBdd();
            } else {
                pDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Erreur lors de la connexion au réseau", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void deleteFromBdd() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();
                        // Toast.makeText(getActivity(), "Localisation a jour!", Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(), "Le membre a ete supprime", Toast.LENGTH_LONG).show();
                        refreshUsers();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Connexion au serveur impossible : " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_PHONE, phoneRemote);
                return params;
            }


        };


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);


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
                //users();
                getlocalUsers();
            } else {
                pDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Erreur lors de la connexion au réseau", Toast.LENGTH_LONG).show();

            }
        }
    }


    private void users() {
        String tag_json_arry = "json_array_req";

        pDialog = new MaterialDialog.Builder(getActivity())
                .title("Attendez svp!")
                .content("Chargement marqueurs...")
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




                        // Parsing json
                        for (int i = 0; i < response.length(); i++)
                            try {
                                JSONObject obj = response.getJSONObject(i);



                                final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");
                                final String phone = sharedPreferences.getString(Config.PHONE_SHARED_PREF, "Not Available");
                                final String network = sharedPreferences.getString(Config.NETWORK_SHARED_PREF, "Not Available");





                                if (obj.getString("latitude") != null && obj.getString("network").equals(network) && obj.getString("category").equals("geolocated")) {
                                    final LatLng latLng = new LatLng(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")));


                                    memberGroup members = new memberGroup();
                                    members.setName(String.valueOf(Html.fromHtml(obj.getString("lastname"))));
                                    members.setBalance(obj.getString("balance"));
                                    members.setAdress(obj.getString("firstname"));
                                    members.setPhone(obj.getString("phone"));
                                    members.setActive(obj.getString("active"));
                                    // adding movie to movies array
                                    memberGroupList.add(members);
                                    if (obj.getString("member_code").equals(phone)) {
                                        //Toast.makeText(getActivity().getApplicationContext(), "Email "+email, Toast.LENGTH_LONG).show();
                                    } else {
                                    }

                                } else {



                                }


                            } catch (JSONException e) {
                                //e.printStackTrace();
                                Toast.makeText(getActivity(), "Impossible de recuperer les marqueurs : " + e.toString(), Toast.LENGTH_LONG).show();
                            }




                        if (memberGroupList!=null) {
                            adapter = new memberGroupAdapter(getActivity(), memberGroupList);
                        }
                        memberListView.setAdapter(adapter);


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


    private void refreshUsers() {
        String tag_json_arry = "json_array_req";

        memberGroupList.clear();


        String url = "https://ilink-app.com/app/select/locations.php";
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        db.resetTables();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++)
                            try {
                                JSONObject obj = response.getJSONObject(i);


                                final String phone = sharedPreferences.getString(Config.PHONE_SHARED_PREF, "Not Available");
                                final String code_membre = sharedPreferences.getString(KEY_MEMBER_CODE, "Not Available");

                                if (obj.getString("phone").equals(phone)) {

                                } else {
                                    db.addUsers(obj.getString("firstname"), obj.getString("lastname"), obj.getString("email"), obj.getString("phone"), obj.getString("country_code"), obj.getString("network"), obj.getString("member_code"),
                                            obj.getString("code_parrain"), obj.getString("category"), obj.getString("balance"), obj.getString("latitude"), obj.getString("longitude"), obj.getString("mbre_reseau"), obj.getString("mbre_ss_reseau"), obj.getString("validation_code"), obj.getString("active"));

                                }


                                if ((!obj.getString("latitude").isEmpty()) && (!obj.getString("longitude").isEmpty()) && (!obj.getString("phone").equals(phone)) && obj.getString("code_parrain").equals(code_membre)) {


                                    memberGroup members = new memberGroup();
                                    members.setName(String.valueOf(Html.fromHtml(obj.getString("lastname"))));
                                    members.setBalance(obj.getString("balance"));
                                    members.setAdress(obj.getString("firstname"));
                                    members.setPhone(obj.getString("phone"));
                                    members.setActive(obj.getString("active"));
                                    // adding movie to movies array
                                    memberGroupList.add(members);


                                } else {



                                }


                            } catch (JSONException e) {
                                //e.printStackTrace();
                                Toast.makeText(getActivity(), "Impossible de recuperer les marqueurs : " + e.toString(), Toast.LENGTH_LONG).show();
                            }


                        if (memberGroupList!=null) {
                            adapter = new memberGroupAdapter(getActivity(), memberGroupList);
                        }



                        memberListView.setAdapter(adapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Erreur : " + error.getMessage());
                Toast.makeText(getActivity(), "Erreur de connexion"+error, Toast.LENGTH_SHORT).show();


            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq, tag_json_arry);

    }

    private void getlocalUsersAsc() {
        this.memberGroupList.clear();
        ArrayList<usersModel> users = this.db.getUsersDetailsAsc();
        int usersLength = users.size();
        String phone = this.sharedPreferences.getString(KEY_PHONE, "Not Available");
        String code_membre = this.sharedPreferences.getString(KEY_MEMBER_CODE, "Not Available");

        for (int i = 0; i < usersLength; i++) {
            usersModel u = users.get(i);
            if ((!u.getLatitude().isEmpty()) && (!u.getLongitude().isEmpty()) && (!u.getPhone().equals(phone)) && u.getCode_parrain().equals(code_membre)) {
                memberGroup members = new memberGroup();
                members.setName(String.valueOf(Html.fromHtml(u.getLastname())));
                members.setBalance(u.getBalance());
                members.setAdress(u.getName());
                members.setPhone(u.getPhone());

                this.memberGroupList.add(members);
            }
        }
        if (this.memberGroupList != null) {
            this.adapter = new memberGroupAdapter(getActivity(), this.memberGroupList);
        }
        this.memberListView.setAdapter(this.adapter);
    }

    private void getlocalUsersDsc() {
        this.memberGroupList.clear();
        ArrayList<usersModel> users = this.db.getUsersDetailsDsc();
        int usersLength = users.size();
        String phone = this.sharedPreferences.getString(KEY_PHONE, "Not Available");
        String code_membre = this.sharedPreferences.getString(KEY_MEMBER_CODE, "Not Available");
        String category = this.sharedPreferences.getString(KEY_CATEGORY, "Not Available");

        for (int i = 0; i < usersLength; i++) {
            usersModel u = users.get(i);
            if ((!u.getLatitude().isEmpty()) && (!u.getLongitude().isEmpty()) && (!u.getPhone().equals(phone)) && u.getCode_parrain().equals(code_membre)) {
                memberGroup members = new memberGroup();
                members.setName(String.valueOf(Html.fromHtml(u.getLastname())));
                members.setBalance(u.getBalance());
                members.setAdress(u.getName());
                members.setPhone(u.getPhone());

                this.memberGroupList.add(members);
            }
        }
        if (this.memberGroupList != null) {
            this.adapter = new memberGroupAdapter(getActivity(), this.memberGroupList);
        }
        this.memberListView.setAdapter(this.adapter);
    }

    private void getlocalUsers() {
        ArrayList<usersModel> users = this.db.getUsersDetails();
        int usersLength = users.size();
        String email = this.sharedPreferences.getString(KEY_EMAIL, "Not Available");
        String phone = this.sharedPreferences.getString(KEY_PHONE, "Not Available");
        String network = this.sharedPreferences.getString(KEY_NETWORK, "Not Available");
        String code_membre = this.sharedPreferences.getString(KEY_MEMBER_CODE, "Not Available");
        String category = this.sharedPreferences.getString(KEY_CATEGORY, "Not Available");
        String categorie;
        if (category.equals("hyper") ) {
            categorie = "super";
        } else if (category.equals("super")) {
            categorie = "geolocated";
        }
        for (int i = 0; i < usersLength; i++) {
            usersModel u = users.get(i);
            if ((!u.getLatitude().isEmpty()) && (!u.getLongitude().isEmpty()) && (!u.getPhone().equals(phone)) && u.getCode_parrain().equals(code_membre)) {
                memberGroup members = new memberGroup();
                members.setName(String.valueOf(Html.fromHtml(u.getLastname())));
                members.setBalance(u.getBalance());
                members.setAdress(u.getName());
                members.setPhone(u.getPhone());

                this.memberGroupList.add(members);
            }
        }
        if (this.memberGroupList != null) {
            this.adapter = new memberGroupAdapter(getActivity(), this.memberGroupList);
        }
        this.memberListView.setAdapter(this.adapter);
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMemberGroupFragmentInteraction(Uri uri);
    }


    private void hidePDialog() {
        if (this.pDialog != null) {
            this.pDialog.dismiss();
            this.pDialog = null;
        }
    }

    public void onResume() {

        super.onResume();

    }
}
