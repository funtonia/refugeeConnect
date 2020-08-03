package com.example.schlauefuechse.refugeeconnect.Controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.Doctor;
import com.example.schlauefuechse.refugeeconnect.Model.Model;
import com.example.schlauefuechse.refugeeconnect.R;

public class DetailDoctorFragment extends Fragment {
    TextView doctorNameTV;
    TextView doctorTypeTV;
    TextView doctorAddressTV;
    ImageButton doctorFavoriteBT;
    ImageButton doctorCallBT;
    ImageView langOne;
    ImageView langTwo;
    ImageView langThree;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_detail_doctor, container, false);
        doctorNameTV = (TextView) rootView.findViewById(R.id.doctor_nameTV);
        doctorTypeTV = (TextView) rootView.findViewById(R.id.doctor_typeTV);
        doctorAddressTV = (TextView) rootView.findViewById(R.id.doctor_addressTV);
        doctorFavoriteBT = (ImageButton) rootView.findViewById(R.id.doctor_favoriteBT);
        doctorCallBT = (ImageButton)rootView.findViewById(R.id.doctor_callBT);
        langOne =(ImageView) rootView.findViewById(R.id.lang_one);
        langTwo =(ImageView)rootView.findViewById(R.id.lang_two);
        langThree =(ImageView) rootView.findViewById(R.id.lang_three);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        int bundleId = args.getInt("id");
        final Doctor doctor = Model.model.getDoctorToID(bundleId);

        doctorNameTV.setText(doctor.getName());
        doctorTypeTV.setText(doctor.getType());

        String detail;
        String[] bonusAddress = doctor.getAddress().split(",");

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

        doctorAddressTV.setText(detail);

        if(doctor.isFavorit()){
            doctorFavoriteBT.setImageResource(R.drawable.favorite_red);
        }
        else{
            doctorFavoriteBT.setImageResource(R.drawable.favorite_grey);
        }

        ImageView la;
        String[] lang = doctor.getLanguages().split(",");
        Log.e("lang", lang.toString());

        for(int i = 0; i<lang.length; i++){

            if(i==0) {
                la = langOne;
                Log.e("language", "LangOne");
            }
            else if(i==1) {
                la = langTwo;
                Log.e("language", "LangTwo");
            }
            else {
                la = langThree;
                Log.e("language", "LangThree");
            }

            if(lang[i].equals("en")) {
                la.setImageResource(R.drawable.lang_en);

            } else if (lang[i].equals("ar")) {
                    la.setImageResource(R.drawable.lang_ar);

            } else if(lang[i].equals("it")){
                    la.setImageResource(R.drawable.lang_it);

            } else if (lang[i].equals("dr")) {
                    la.setImageResource(R.drawable.lang_dr);

            } else if(lang[i].equals("fa")){
                    la.setImageResource(R.drawable.lang_fa);

            } else if (lang[i].equals("tr")) {
                    la.setImageResource(R.drawable.lang_tr);

            } else if(lang[i].equals("fr")){
                    la.setImageResource(R.drawable.lang_fr);

            } else if (lang[i].equals("sh")) {
                    la.setImageResource(R.drawable.lang_sh);

            } else if(lang[i].equals("sq")){
                    la.setImageResource(R.drawable.lang_sq);

            } else if (lang[i].equals("el")) {
                    la.setImageResource(R.drawable.lang_el);

            } else if(lang[i].equals("hr")){
                    la.setImageResource(R.drawable.lang_hr);

            } else if (lang[i].equals("sr")) {
                    la.setImageResource(R.drawable.lang_sr);

            } else if(lang[i].equals("ru")){
                    la.setImageResource(R.drawable.lang_ru);

            } else if (lang[i].equals("ro")) {
                    la.setImageResource(R.drawable.lang_ro);

            } else if(lang[i].equals("bs")){
                    la.setImageResource(R.drawable.lang_bs);

            } else if (lang[i].equals("es")) {
                    la.setImageResource(R.drawable.lang_es);

            } else if(lang[i].equals("mk")){
                    la.setImageResource(R.drawable.lang_mk);
            }
            else{
                    la.setImageResource(R.drawable.lang_de);
                }

        }

        doctorFavoriteBT.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(doctor.isFavorit()){
                    doctor.setFavorit(false);
                    Model.model.deleteFromDoctorFavorite(doctor.getId());
                    doctorFavoriteBT.setImageResource(R.drawable.favorite_grey);
                }
                else{
                    doctor.setFavorit(true);
                    doctorFavoriteBT.setImageResource(R.drawable.favorite_red);
                    Model.model.addToDoctorFavorite(doctor.getId());
                }
            }
        });
    }
}
