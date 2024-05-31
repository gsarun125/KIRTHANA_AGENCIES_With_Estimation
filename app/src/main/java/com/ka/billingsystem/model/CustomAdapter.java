package com.ka.billingsystem.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ka.billingsystem.R;
import com.ka.billingsystem.model.DataModel;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<DataModel> {

    public CustomAdapter(Context context, List<DataModel> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout, parent, false);
        }
        // Lookup view for data population
        TextView textView1 = convertView.findViewById(R.id.textView1);
        TextView textView2 = convertView.findViewById(R.id.textView2);
        // Populate the data into the template view using the data object
        textView1.setText(dataModel.getEscusName());
        textView2.setText(String.valueOf(dataModel.getAmount()));
        // Return the completed view to render on screen
        return convertView;
    }
}
