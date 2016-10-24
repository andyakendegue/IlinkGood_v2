package com.appli.ilink;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class MemberAskGroupFragment extends Fragment implements OnItemClickListener, OnRefreshListener {
    private static final String ACCEPT_URL = "https://ilink-app.com/app/select/accept_membre.php";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";
    private static final String TAG;
    private memberGroupAdapter adapter;
    DatabaseHandler db;
    private String emailRemote;
    private ImageButton imgBtnDown;
    private ImageButton imgBtnUp;
    private ListView listView;
    private TextView enAttente;
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

    public MemberAskGroupFragment() {
        this.memberGroupList = new ArrayList();
    }

    public static MemberAskGroupFragment newInstance(int sectionNumber) {
        MemberAskGroupFragment fragment = new MemberAskGroupFragment();
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

        this.enAttente = (TextView) this.memberView.findViewById(R.id.textViewMember);
        this.enAttente.setText("En attente de validation");

        imgBtnUp.setVisibility(View.INVISIBLE);

        imgBtnDown.setVisibility(View.INVISIBLE);
        new CheckUsers().execute();
        this.db = new DatabaseHandler(getActivity());
        this.memberListView.setOnItemClickListener(this);
        return this.memberView;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        acceptUser(this.phoneRemote);
    }

    public void onRefresh() {

        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshUsers();
                //avertie le SwipeRefreshLayout que la mise à jour a été effectuée
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, -1);
    }

    private void acceptUser(String phone) {
        Builder alertDialogBuilder = new Builder(getActivity());


        alertDialogBuilder.create().show();


        alertDialogBuilder.setMessage("Etes vous sur de valider le " + phone + "?");
        alertDialogBuilder.setPositiveButton("Oui",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        new acceptUserFromUser().execute();


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

    private class acceptUserFromUser extends AsyncTask<String, String, Boolean> {


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

            if (th == true) {
                pDialog.dismiss();
                acceptFromBdd();
            } else {
                pDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Erreur lors de la connexion au réseau", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void acceptFromBdd() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ACCEPT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();
                        // Toast.makeText(getActivity(), "Localisation a jour!", Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(), "Le membre a ete accepté", Toast.LENGTH_LONG).show();
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
                refreshUsers();
            } else {
                pDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Erreur lors de la connexion au réseau", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void refreshUsers() {
        //this.memberGroupList.clear();
        //JsonArrayRequest movieReq = new JsonArrayRequest("https://ilink-app.com/app/select/demandes_membres.php", new 9(this), new 10(this));
        //AppController.getInstance().addToRequestQueue(movieReq, "json_array_req");

        String tag_json_arry = "json_array_req";

        memberGroupList.clear();


        String url = "https://ilink-app.com/app/select/demandes_membres.php";
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
                                final String code_membre = sharedPreferences.getString(Config.MEMBER_CODE_SHARED_PREF, "Not Available");





                                if ( obj.getString("code").equals(code_membre) && obj.getString("Statut").equals("en cours")) {


                                    phoneRemote = obj.getString("phone");

                                    memberGroup members = new memberGroup();

                                    members.setName(String.valueOf("Telephone : "+Html.fromHtml(obj.getString("phone"))));
                                    //members.setBalance("Categorie : "+obj.getString("categorie"));
                                    members.setAdress("Code membre : "+obj.getString("code"));
                                    members.setPhone("Nombre de membres : "+obj.getString("nbrecode"));
                                    members.setActive("Statut : "+obj.getString("Statut"));
                                    // adding movie to movies array
                                    memberGroupList.add(members);


                                } else {



                                }


                            } catch (JSONException e) {
                                //e.printStackTrace();
                                Toast.makeText(getActivity(), "Impossible de recuperer les marqueurs " , Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity(), "Erreur de connexion", Toast.LENGTH_SHORT).show();


            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq, tag_json_arry);
    }




    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMemberAskGroupFragmentInteraction(Uri uri);
    }


    private void hidePDialog() {
        if (this.pDialog != null) {
            this.pDialog.dismiss();
            this.pDialog = null;
        }
    }
}
