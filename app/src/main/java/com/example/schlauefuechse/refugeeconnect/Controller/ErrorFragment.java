package com.example.schlauefuechse.refugeeconnect.Controller;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.schlauefuechse.refugeeconnect.R;

public class ErrorFragment extends Fragment {

    Button retryBtn = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_error, container, false);

        retryBtn  = (Button) rootView.findViewById(R.id.try_again);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                getActivity().startActivity(getActivity().getIntent());
            }
        });

        return rootView;
    }
}
