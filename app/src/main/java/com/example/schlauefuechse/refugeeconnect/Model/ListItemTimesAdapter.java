package com.example.schlauefuechse.refugeeconnect.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.schlauefuechse.refugeeconnect.R;

/**
 * Created by bachmau on 07/06/16.
 */
public class ListItemTimesAdapter extends ArrayAdapter {

        private String[] items;
        private final Context context;

        /** 
         * Constructor for the ListItemAdapter  
         * - parameters: 
         *  - context: the Context to be set 
         *  - items: the ArrayList to be set 
         *  */
        public ListItemTimesAdapter(Context context, String[] items) {
            super(context, R.layout.row_course_times, items);
            this.items = items;
            this.context = context;
        }


        /** 
         * Sets the data that is to be displayed in the list.  
         * - returns:      The View containing the different list items  
         * - parameters: 
         *  - position: the position of the list item 
         *  - convertView 
         *  - parent: the parent element of the list item 
         *  */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String name;

            View view;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_course_times, parent, false);

            TextView timeTV = (TextView) view.findViewById(R.id.days);
            timeTV.setText(items[position]);

            return view;
        }

    }

