package com.example.schlauefuechse.refugeeconnect.Controller;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.schlauefuechse.refugeeconnect.R;

public class MainFragment extends Fragment {

    Button freeTimeBtn = null;
    Button medicalScientistBtn = null;
    Button contactBtn = null;

    OnButtonSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnButtonSelectedListener {
        void onButtonSelected(String name);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnButtonSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        freeTimeBtn  = (Button) rootView.findViewById(R.id.freeTimeBtn);
        freeTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onButtonSelected("freeTime");
            }
        });

        medicalScientistBtn = (Button) rootView.findViewById(R.id.medicalScientistBtn);
        medicalScientistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onButtonSelected("medical");
            }
        });

        contactBtn = (Button) rootView.findViewById(R.id.contactBtn);
        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onButtonSelected("contact");
            }
        });

        return rootView;
    }
}
