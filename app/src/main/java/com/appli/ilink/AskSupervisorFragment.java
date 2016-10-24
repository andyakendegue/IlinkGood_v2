package com.appli.ilink;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AskSupervisorFragment extends Fragment {
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_NOMBRE_MEMBRES = "membres";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_TAG = "tag";
    private static final String REGISTER_URL = "https://ilink-app.com/app/";
    private Spinner ListMembres;
    private Button btn_ask_supervisor;
    private String e_membres;
    private String[] membresItem;




    public AskSupervisorFragment() {
        this.e_membres = "0";
        this.ListMembres = null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_supervisor, container, false);
        this.ListMembres = (Spinner) rootView.findViewById(R.id.list_nbr_member);
        List<String> listeMembres = new ArrayList();
        this.membresItem = getResources().getStringArray(R.array.nombre_membres);
        for (Object add : this.membresItem) {
            listeMembres.add(String.valueOf(add));
        }
        ArrayAdapter<String> membresAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listeMembres);
        //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
        membresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ListMembres.setAdapter(membresAdapter);
        ListMembres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                e_membres = parent.getItemAtPosition(position).toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                e_membres = membresItem[0].toString();
            }

        });
        this.btn_ask_supervisor = (Button) rootView.findViewById(R.id.btn_ask_supervisor);
        btn_ask_supervisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        return rootView;
    }

    private void registerUser(){


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String phone = sharedPreferences.getString(Config.PHONE_SHARED_PREF, "Not Available");


        if (e_membres!=null) {


            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();
                            //Toast.makeText(getActivity().getApplicationContext(), "Enregistrement reussi!", Toast.LENGTH_LONG).show();
                            //Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                            //startActivity(i);

                            //getActivity().finish();
                            Toast.makeText(getActivity().getApplicationContext(),"Votre demande a bien été enregistrée! "+phone+" "+e_membres, Toast.LENGTH_LONG).show();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity().getApplicationContext(),"Impossible de se connecter au serveur", Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(KEY_PHONE, phone);
                    params.put(KEY_NOMBRE_MEMBRES, e_membres);
                    params.put(KEY_CATEGORY, "super");
                    params.put(KEY_TAG, "ask_supervisor");
                    return params;
                }


            };

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueue.add(stringRequest);

        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Vous ne pouvez avoir aucun membre!", Toast.LENGTH_LONG).show();
        }






    }

}
