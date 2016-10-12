package com.appli.ilink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appli.ilink.adapter.askCreditAdapter;
import com.appli.ilink.model.creditAsk;

import java.util.ArrayList;
import java.util.List;

public class DemandesCreditFragment extends Fragment implements OnItemClickListener, OnRefreshListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String url = "http://ilink-app.com/app/";
    private askCreditAdapter adapter;
    private List<creditAsk> askCreditList;
    private View demandeView;
    private ListView listView;
    private ListView mListView;
    private OnFragmentInteractionListener mListener;
    private String mParam1;
    private String mParam2;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MaterialDialog pDialog;

    public DemandesCreditFragment() {
        this.askCreditList = new ArrayList();
    }

    public static DemandesCreditFragment newInstance(String param1, String param2) {
        DemandesCreditFragment fragment = new DemandesCreditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        this.demandeView = inflater.inflate(R.layout.fragment_demandes_credit, container, false);
        this.mListView = (ListView) this.demandeView.findViewById(R.id.listMemberGroup);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) this.demandeView.findViewById(R.id.swipeRefreshLayoutMemberGroup);
        this.mSwipeRefreshLayout.setOnRefreshListener(this);
        int nombre_membres = 0;
        while (nombre_membres < 10) {
            creditAsk members = new creditAsk();
            int nombre_membres2 = nombre_membres + 1;
            members.setNom_membre("Capp" + nombre_membres);
            nombre_membres = nombre_membres2 + 1;
            members.setMontant("20000" + nombre_membres2);
            nombre_membres2 = nombre_membres + 1;
            members.setAdresse_membre("Nzeng-Ayong" + nombre_membres);
            this.askCreditList.add(members);
            nombre_membres = nombre_membres2 + 1;
        }
        this.adapter = new askCreditAdapter(getActivity(), this.askCreditList);
        this.mListView.setAdapter(this.adapter);
        this.mListView.setOnItemClickListener(this);
        return this.demandeView;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        creditAsk m = (creditAsk) this.askCreditList.get(position);
        Intent i = new Intent(getActivity(), MemberDetailActivity.class);
        i.putExtra("titre", String.valueOf(m.getNom_membre()));
        i.putExtra("image", String.valueOf(m.getMontant()));
        i.putExtra("contenu", String.valueOf(m.getAdresse_membre()));
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void hidePDialog() {
        if (this.pDialog != null) {
            this.pDialog.dismiss();
            this.pDialog = null;
        }
    }

    @Override
    public void onRefresh() {
        //appellé lors de l'action Pull To Refresh
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshNews();
                //avertie le SwipeRefreshLayout que la mise à jour a été effectuée
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, -1);
    }
    private void refreshNews() {
        this.mListView = (ListView) this.demandeView.findViewById(R.id.listMemberGroup);
        int nombre_membres = 0;
        while (nombre_membres < 10) {
            creditAsk members = new creditAsk();
            int nombre_membres2 = nombre_membres + 1;
            members.setNom_membre("Capp" + nombre_membres);
            nombre_membres = nombre_membres2 + 1;
            members.setMontant("20000" + nombre_membres2);
            nombre_membres2 = nombre_membres + 1;
            members.setAdresse_membre("Nzeng-Ayong" + nombre_membres);
            this.askCreditList.add(members);
            nombre_membres = nombre_membres2 + 1;
        }
        this.adapter = new askCreditAdapter(getActivity(), this.askCreditList);
        this.mListView.setAdapter(this.adapter);
        this.mListView.setOnItemClickListener(this);
    }
}
