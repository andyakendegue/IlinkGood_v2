package com.appli.ilink.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appli.ilink.R;
import com.appli.ilink.model.legendeModel;

import java.util.List;

/**
 * Created by capp on 15/04/16.
 */
public class LegendeAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<legendeModel> legendeItem;

    public LegendeAdapter(Activity activity, List<legendeModel> legendeItem) {
        this.activity = activity;
        this.legendeItem = legendeItem;
    }

    @Override
    public int getCount() {
        return legendeItem.size();
    }

    @Override
    public Object getItem(int location) {
        return legendeItem.get(location);
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
            convertView = inflater.inflate(R.layout.legende_list_item, null);

        TextView name = (TextView) convertView.findViewById(R.id.textLegendeName);
        ImageView image = (ImageView) convertView.findViewById(R.id.imgLegende);

        legendeModel m = legendeItem.get(position);

        name.setText(m.getLegende_name());
        image.setImageResource(m.getLegende_picture());


        return convertView;
    }
}
