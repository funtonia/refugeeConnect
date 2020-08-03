package com.example.schlauefuechse.refugeeconnect.Model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.BonusCard;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.Contact;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.Doctor;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.SportCourse;
import com.example.schlauefuechse.refugeeconnect.R;

import java.util.ArrayList;

/**
 * Created by Antonia on 06.05.2016.
 */
public class ListItemBonusAdapter extends ArrayAdapter {

    private ArrayList items;
    private final Context context;

    /** 
     * Constructor for the ListItemAdapter  
     * - parameters: 
     *  - context: the Context to be set 
     *  - items: the ArrayList to be set 
     *  */
    public ListItemBonusAdapter(Context context, ArrayList items) {
        super(context, R.layout.row_bonus, items);
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
        String detail;
        int distance = 0;
        Boolean distanceNeeded;
        Object o;

        View view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.row_bonus, parent, false);

        TextView nameTV = (TextView) view.findViewById(R.id.toptext);
        TextView detailTV = (TextView) view.findViewById(R.id.detailtext);
        TextView distanceTV = (TextView) view.findViewById(R.id.distance);

        ImageView fc = (ImageView) view.findViewById(R.id.fc);
        ImageView bc = (ImageView) view.findViewById(R.id.bc);
        ImageView th = (ImageView) view.findViewById(R.id.th);

        final BonusCard bonus = (BonusCard) items.get(position);

        name = bonus.getName();
        String[] bonusAddress = bonus.getAddress().split(",");

        if(bonusAddress.length==2){
            String address = bonusAddress[0] + "\n" + bonusAddress[1];
            detail = address;
        }
        else if(bonusAddress.length==3){
            String address = bonusAddress[0] + "\n" + bonusAddress[1]+"\n" +bonusAddress[2];
            detail = address;
        }
        else {
            String address = bonusAddress[0];
            detail = address;
        }

        if(Model.model.isDistanceAllowed) {
            distance = Model.model.namesDistancesBonusCard.get(bonus);
            distanceNeeded = true;
        } else {
            distanceNeeded = false;
        }


        o = bonus;

        Log.e("Ist es FC",String.valueOf(bonus.isFc()));
        if(bonus.isFc()){
            fc.setImageResource(R.drawable.fc);
        }
        Log.e("Ist es TH",String.valueOf(bonus.isFc()));
        if(bonus.isTh()){
            th.setImageResource(R.drawable.th);
        }
        Log.e("Ist es BC",String.valueOf(bonus.isFc()));
        if(bonus.isBc()){
            bc.setImageResource(R.drawable.bc);
        }


        if (o != null) {
            if (nameTV != null) {
                nameTV.setText( name);
            }
            if (detailTV != null) {
                detailTV.setText(detail);
            }
            if (distanceNeeded) {
                //show the distance
                if(distance >= 1000) {
                    Float distanceKM = (float) distance/1000;
                    distanceTV.setText(String.valueOf(Model.model.roundFloat(distanceKM, 1))+ " km");
                } else {
                    distanceTV.setText(String.valueOf(distance)+ " m");
                }
            }
        }

        return view;
    }


}