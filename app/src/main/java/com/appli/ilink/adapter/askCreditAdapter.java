package com.appli.ilink.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appli.ilink.R;
import com.appli.ilink.model.creditAsk;

import java.util.List;

/**
 * Created by capp on 15/04/16.
 */
public class askCreditAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<creditAsk> askCreditItems;

    public askCreditAdapter(Activity activity, List<creditAsk> askCreditItems) {
        this.activity = activity;
        this.askCreditItems = askCreditItems;
    }

    @Override
    public int getCount() {
        return askCreditItems.size();
    }

    @Override
    public Object getItem(int location) {
        return askCreditItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.member_group_list_item, null);

        TextView name = (TextView) convertView.findViewById(R.id.textMemberGroupName);
        TextView montant = (TextView) convertView.findViewById(R.id.textMemberGroupAmount);
        TextView adress = (TextView) convertView.findViewById(R.id.textMemberGroupAdress);

        creditAsk m = askCreditItems.get(position);

        name.setText(m.getNom_membre());
        montant.setText(m.getMontant());
        adress.setText(m.getAdresse_membre());


        return convertView;
    }
}
