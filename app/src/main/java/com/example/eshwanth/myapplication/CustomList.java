package com.example.eshwanth.myapplication;

/**
 * Created by Eshwanth on 11/6/2015.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] title;
    private final String[] count;
    private final String topic;
    int i = 0;

    public CustomList(Activity context, String[] web, String[] imageId, String topic) {
        super(context, R.layout.custom_list_view, web);
        this.context = context;
        this.title = web;
        this.count = imageId;
        this.topic = topic;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView;

            rowView= inflater.inflate(R.layout.custom_list_view, null, true);
            TextView course_title = (TextView) rowView.findViewById(R.id.item_title);
            course_title.setText(title[position]);

            TextView counter = (TextView) rowView.findViewById(R.id.item_counter);
            counter.setText(count[position]);

        return rowView;
    }
}


//TO add custom topic,add if else for 'i'
