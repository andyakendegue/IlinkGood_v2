package com.appli.ilink.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appli.ilink.R;
import com.appli.ilink.model.memberGroup;

import java.util.List;

/**
 * Created by capp on 15/04/16.
 */
public class memberGroupAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<memberGroup> memberGroupItems;

    public memberGroupAdapter(Activity activity, List<memberGroup> memberGroupItems) {
        this.activity = activity;
        this.memberGroupItems = memberGroupItems;
    }

    @Override
    public int getCount() {
        return memberGroupItems.size();
    }

    @Override
    public Object getItem(int location) {
        return memberGroupItems.get(location);
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

        TextView name = (TextView) convertView.findViewById(R.id.textLegendeName);
        TextView amount = (TextView) convertView.findViewById(R.id.textMemberGroupAmount);
        TextView adress = (TextView) convertView.findViewById(R.id.textMemberGroupAdress);
        TextView phone = (TextView) convertView.findViewById(R.id.textMemberGroupPhone);
        TextView active = (TextView) convertView.findViewById(R.id.textMemberGroupActive);

        memberGroup m = memberGroupItems.get(position);

        name.setText(m.getName());
        amount.setText(m.getBalance());
        adress.setText(m.getAdress());
        phone.setText(m.getPhone());
        active.setText(m.getActive());


        return convertView;
    }
}
