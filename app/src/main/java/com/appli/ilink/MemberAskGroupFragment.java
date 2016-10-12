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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
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

import java.util.ArrayList;
import java.util.List;

public class MemberAskGroupFragment extends Fragment implements OnItemClickListener, OnRefreshListener {
    private static final String ACCEPT_URL = "http://ilink-app.com/app/select/accept_membre.php";
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
        imgBtnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getlocalUsersAsc();
            }
        });

        imgBtnDown.setOnClickListener(new View.OnClickListener() {
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

                        //acceptUserFromUser();


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

    /*private void acceptUserFromUser() {
        Volley.newRequestQueue(getActivity()).add(new 8(this, 1, ACCEPT_URL, new 6(this), new 7(this)));
    }*/

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
                getlocalUsers();
            } else {
                pDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Erreur lors de la connexion au réseau", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void refreshUsers() {
        //this.memberGroupList.clear();
        //JsonArrayRequest movieReq = new JsonArrayRequest("http://ilink-app.com/app/select/demandes_membres.php", new 9(this), new 10(this));
        //AppController.getInstance().addToRequestQueue(movieReq, "json_array_req");

        String tag_json_arry = "json_array_req";

        memberGroupList.clear();


        String url = "http://ilink-app.com/app/select/demandes_membres.php";
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
        String email = this.sharedPreferences.getString(KEY_EMAIL, "Not Available");
        String phone = this.sharedPreferences.getString(KEY_PHONE, "Not Available");
        String network = this.sharedPreferences.getString(RegisterSimpleActivity.KEY_NETWORK, "Not Available");
        String code_parrain = this.sharedPreferences.getString(RegisterSimpleActivity.KEY_MEMBER_CODE, "Not Available");
        String category = this.sharedPreferences.getString(RegisterSimpleActivity.KEY_CATEGORY, "Not Available");
        String categorie;
        if (category == "hyper") {
            categorie = "super";
        } else if (category == "super") {
            categorie = "geolocated";
        }
        for (int i = 0; i < usersLength; i++) {
            usersModel u = (usersModel) users.get(i);
            if (u.getLatitude() != null && u.getNetwork().equals(network) && u.getCode_parrain().equals(code_parrain)) {
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
        String email = this.sharedPreferences.getString(KEY_EMAIL, "Not Available");
        String phone = this.sharedPreferences.getString(KEY_PHONE, "Not Available");
        String network = this.sharedPreferences.getString(RegisterSimpleActivity.KEY_NETWORK, "Not Available");
        String code_parrain = this.sharedPreferences.getString(RegisterSimpleActivity.KEY_MEMBER_CODE, "Not Available");
        String category = this.sharedPreferences.getString(RegisterSimpleActivity.KEY_CATEGORY, "Not Available");
        String categorie;
        if (category == "hyper") {
            categorie = "super";
        } else if (category == "super") {
            categorie = "geolocated";
        }
        for (int i = 0; i < usersLength; i++) {
            usersModel u = (usersModel) users.get(i);
            if (u.getLatitude() != null && u.getNetwork().equals(network) && u.getCode_parrain().equals(code_parrain)) {
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


        ArrayList<usersModel> users = db.getUsersDetails();
        int usersLength = users.size();

        final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");
        final String phone = sharedPreferences.getString(Config.PHONE_SHARED_PREF, "Not Available");
        final String network = sharedPreferences.getString(Config.NETWORK_SHARED_PREF, "Not Available");

        for (int i = 0; i < usersLength ; i++) {
            usersModel u = users.get(i);




            if (u.getLatitude() != null && u.getNetwork().equals(network) && u.getCategory().equals("geolocated")) {
                memberGroup members = new memberGroup();
                members.setName(String.valueOf(Html.fromHtml(u.getLastname())));
                members.setBalance(u.getBalance());
                members.setAdress(u.getName());
                members.setPhone(u.getPhone());
                // adding movie to movies array
                memberGroupList.add(members);
            }

        }


        if (memberGroupList!=null) {
            adapter = new memberGroupAdapter(getActivity(), memberGroupList);
        }



        memberListView.setAdapter(adapter);
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
