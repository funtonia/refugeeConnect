package com.example.schlauefuechse.refugeeconnect.Controller;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.schlauefuechse.refugeeconnect.R;

public class FreeTimeFragment extends Fragment {

    Button bonusCardbtn = null;
    Button courseBtn = null;

    FreeTimeButtonListener mCallback;

    // Container Activity must implement this interface
    public interface FreeTimeButtonListener {
        void onFreeTimeButtonSelected(String name);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (FreeTimeButtonListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_free_time, container, false);

        bonusCardbtn  = (Button) rootView.findViewById(R.id.bonusCardBtn);
        bonusCardbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onFreeTimeButtonSelected("bonusCard");
            }
        });

        courseBtn = (Button) rootView.findViewById(R.id.courseBtn);
        courseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onFreeTimeButtonSelected("course");
            }
        });

        return rootView;
    }
}
