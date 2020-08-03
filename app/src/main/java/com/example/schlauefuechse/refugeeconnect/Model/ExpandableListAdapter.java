package com.example.schlauefuechse.refugeeconnect.Model;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.BonusCard;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.Contact;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.Doctor;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.SportCourse;
import com.example.schlauefuechse.refugeeconnect.R;

import java.util.HashMap;
import java.util.List;


public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listHeaders; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Object>> listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap listDataChild) {
        this.context = context;
        this.listHeaders = listDataHeader;
        this.listDataChild = listDataChild;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listHeaders.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    Boolean imageUsed = false;

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        String name;
        String detail = null;
        int distance = 0;
        Boolean distanceNeeded = false;

        Object o;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.exp_list_item, null);
        }

        //main, detail, distance
        final Object child = getChild(groupPosition, childPosition);

        TextView nameTV = (TextView) convertView.findViewById(R.id.toptext);
        TextView detailTV = (TextView) convertView.findViewById(R.id.detailtext);
        TextView distanceTV = (TextView) convertView.findViewById(R.id.distance);

        ImageView langOne = (ImageView) convertView.findViewById(R.id.lang_one);
        ImageView langTwo = (ImageView) convertView.findViewById(R.id.lang_two);
        ImageView langThree = (ImageView) convertView.findViewById(R.id.lang_three);

        final ImageView arrow = (ImageView) convertView.findViewById(R.id.arrow);

        //instanceof Courses OR instanceof Doctors -> add distance
        if(child instanceof SportCourse) {
            SportCourse course = (SportCourse) child;
            name = course.getType().toString();
            detail = course.getDays().toString();
            if(Model.model.isDistanceAllowed) {
                distance = Model.model.namesDistancesSportsCourses.get(course);
                distanceNeeded = true;
            } else {
                distanceNeeded = false;
            }

            detailTV.setVisibility(View.VISIBLE);
            distanceTV.setVisibility(View.VISIBLE);
            arrow.setImageResource(R.drawable.arrow_right);
            if (imageUsed){
                langOne.setVisibility(View.INVISIBLE);
                langTwo.setVisibility(View.INVISIBLE);
                langThree.setVisibility(View.INVISIBLE);
            }

            o = course;
        } else if(child instanceof Doctor) {
            Doctor doctor = (Doctor) child;
            name = doctor.getName();

            ImageView la;
            String[] lang = doctor.getLanguages().split(",");
            Log.e("lang", lang.toString());

            for(int i = 0; i<lang.length; i++) {

                if (i == 0) {
                    la = langOne;
                    Log.e("language", "LangOne");
                } else if (i == 1) {
                    la = langTwo;
                    Log.e("language", "LangTwo");
                } else {
                    la = langThree;
                    Log.e("language", "LangThree");
                }

                if (lang[i].equals("en")) {
                    la.setImageResource(R.drawable.lang_en);

                } else if (lang[i].equals("ar")) {
                    la.setImageResource(R.drawable.lang_ar);

                } else if (lang[i].equals("it")) {
                    la.setImageResource(R.drawable.lang_it);

                } else if (lang[i].equals("dr")) {
                    la.setImageResource(R.drawable.lang_dr);

                } else if (lang[i].equals("fa")) {
                    la.setImageResource(R.drawable.lang_fa);

                } else if (lang[i].equals("tr")) {
                    la.setImageResource(R.drawable.lang_tr);

                } else if (lang[i].equals("fr")) {
                    la.setImageResource(R.drawable.lang_fr);

                } else if (lang[i].equals("sh")) {
                    la.setImageResource(R.drawable.lang_sh);

                } else if (lang[i].equals("sq")) {
                    la.setImageResource(R.drawable.lang_sq);

                } else if (lang[i].equals("el")) {
                    la.setImageResource(R.drawable.lang_el);

                } else if (lang[i].equals("hr")) {
                    la.setImageResource(R.drawable.lang_hr);

                } else if (lang[i].equals("sr")) {
                    la.setImageResource(R.drawable.lang_sr);

                } else if (lang[i].equals("ru")) {
                    la.setImageResource(R.drawable.lang_ru);

                } else if (lang[i].equals("ro")) {
                    la.setImageResource(R.drawable.lang_ro);

                } else if (lang[i].equals("bs")) {
                    la.setImageResource(R.drawable.lang_bs);

                } else if (lang[i].equals("es")) {
                    la.setImageResource(R.drawable.lang_es);

                } else if (lang[i].equals("mk")) {
                    la.setImageResource(R.drawable.lang_mk);

                } else {
                    la.setImageResource(R.drawable.lang_de);
                }
            }

            if(Model.model.isDistanceAllowed) {
                distance = Model.model.namesDistancesDoctors.get(doctor);
                distanceNeeded = true;
            } else {
                distanceNeeded = false;
            }

            imageUsed = true;
            detailTV.setVisibility(View.INVISIBLE);
            distanceTV.setVisibility(View.VISIBLE);
            arrow.setImageResource(R.drawable.arrow_right);
            langOne.setVisibility(View.VISIBLE);
            langTwo.setVisibility(View.VISIBLE);
            langThree.setVisibility(View.VISIBLE);
            o = doctor;
        } else if(child instanceof Contact){
            Contact contact = (Contact) child;
            name = contact.getName();
            detail = contact.getType().toString();
            detailTV.setVisibility(View.VISIBLE);
            distanceTV.setVisibility(View.INVISIBLE);
            arrow.setImageResource(R.drawable.arrow_expand);
            if (imageUsed){
                langOne.setVisibility(View.INVISIBLE);
                langTwo.setVisibility(View.INVISIBLE);
                langThree.setVisibility(View.INVISIBLE);
            }
            o = contact;
        } else {
            BonusCard bonusCard = (BonusCard) child;
            name = bonusCard.getName();
            detail = bonusCard.getAddress();

            if(Model.model.isDistanceAllowed) {
                distance = Model.model.namesDistancesBonusCard.get(bonusCard);
                distanceNeeded = true;
            } else {
                distanceNeeded = false;
            }
            detailTV.setVisibility(View.VISIBLE);
            distanceTV.setVisibility(View.VISIBLE);
            arrow.setImageResource(R.drawable.arrow_right);
            if (imageUsed){
                langOne.setVisibility(View.INVISIBLE);
                langTwo.setVisibility(View.INVISIBLE);
                langThree.setVisibility(View.INVISIBLE);
            }
            o = bonusCard;
        }

        if (o != null) {
            if (nameTV != null) {
                nameTV.setText(name);
            }
            if (detailTV != null) {
                if(detail != null) {
                    detailTV.setText(detail);
                }
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

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listHeaders.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listHeaders.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listHeaders.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.exp_list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.listHeader);
        ImageView lblListHeaderIcon = (ImageView) convertView.findViewById(R.id.listHeaderIcon);

        lblListHeader.setTypeface(null, Typeface.NORMAL);
        lblListHeader.setText(headerTitle);

        //dynamically set the header icons
        if(headerTitle.contains(" ")){
            //if there is a space -> replace it with an underscore
            headerTitle = headerTitle.replace(" ", "_");
        }else if(headerTitle.contains("-")){
            //if there is a "-" -> replace it with an underscore
            headerTitle = headerTitle.replace("-", "_");
        }
        int drawableId = context.getResources().getIdentifier(headerTitle.toLowerCase(), "drawable", context.getPackageName());
        Log.d("headerIcon", headerTitle + ": " + drawableId);
        if(drawableId != 0) {
            lblListHeaderIcon.setImageResource(drawableId);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}