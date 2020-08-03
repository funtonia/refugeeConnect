package com.example.schlauefuechse.refugeeconnect.Model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.Contact;
import com.example.schlauefuechse.refugeeconnect.R;

import java.util.ArrayList;

/**
 * Created by schlauefuechse on 17.06.16.
 */

public class ContactsListAdapter extends ArrayAdapter {

    private ArrayList items;
    private final Context context;

    /** 
     * Constructor for the ListItemAdapter  
     * - parameters: 
     *  - context: the Context to be set 
     *  - items: the ArrayList to be set 
     *  */
    public ContactsListAdapter(Context context, ArrayList items) {
        super(context, R.layout.row_contact, items);
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

        View view;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.row_contact, parent, false);

        TextView nameTV = (TextView) view.findViewById(R.id.toptext);
        TextView detailTV = (TextView) view.findViewById(R.id.detailtext);

        final Contact c = (Contact) items.get(position);
        String name = c.getName();
        String detail = c.getType();

        if (c != null) {
            if (nameTV != null) {
                nameTV.setText(name);
            }
            if (detailTV != null) {
                detailTV.setText(detail);
            }
        }

        return view;
    }


}
