package com.example.chlorella.blindassist;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
/**
 * Created by chlorella on 3/1/2017.
 */

public class ListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] menu;

    public ListAdapter(Activity context, String[] item) {
        super(context, R.layout.list_item, item);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.menu = item;
    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.label);

        txtTitle.setText(menu[position]);

        return rowView;
    }
}
