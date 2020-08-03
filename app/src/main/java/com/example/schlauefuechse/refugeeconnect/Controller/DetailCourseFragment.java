package com.example.schlauefuechse.refugeeconnect.Controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.SportCourse;
import com.example.schlauefuechse.refugeeconnect.Model.ListItemTimesAdapter;
import com.example.schlauefuechse.refugeeconnect.Model.Model;
import com.example.schlauefuechse.refugeeconnect.R;
import com.google.android.gms.vision.text.Text;

public class DetailCourseFragment extends Fragment {

    ListView courseTimesTV;
    TextView courseGroupTV;
    TextView courseAddressTV;

    //Contact
    ImageButton courseCallBT;
    ImageButton courseMailBT;
    ImageButton courseFavoriteBT;

    String[] times = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_detail_course, container, false);

        courseTimesTV = (ListView) rootView.findViewById(R.id.timesList);
        courseGroupTV = (TextView) rootView.findViewById(R.id.course_groupTV);
        courseCallBT = (ImageButton) rootView.findViewById(R.id.course_callBT);
        courseMailBT = (ImageButton) rootView.findViewById(R.id.course_mailBT);
        courseAddressTV = (TextView) rootView.findViewById(R.id.course_addressTV);
        courseFavoriteBT = (ImageButton)rootView.findViewById(R.id.course_favoriteBT);
        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        int bundleId = args.getInt("id");
        final SportCourse course = Model.model.getCourseToID(bundleId);

        if(course.getDays().isEmpty()||course.getDays().isEmpty()){
            String [] onRequest = new String[1];
            onRequest[0] = getString(R.string.onRequest);
            initialiseListView(onRequest);
        }
        else {
            String[] courseDays = course.getDays().split(",");
            String[] courseTimes = course.getTimes().split(",");

            times = new String[courseDays.length];
            for (int i = 0; i < courseDays.length; i++) {
                times[i] = courseDays[i] + ":\t\t" + courseTimes[i];

            }

            initialiseListView(times);
        }

        courseGroupTV.setText(course.getGroup());
        String detail;

        String[] bonusAddress = course.getAddress().split(",");

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

        courseAddressTV.setText(detail);

        if(course.isFavorit()){
            courseFavoriteBT.setImageResource(R.drawable.favorite_red);
        }
        else{
            courseFavoriteBT.setImageResource(R.drawable.favorite_grey);
        }

        courseFavoriteBT.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(course.isFavorit()){
                    course.setFavorit(false);
                    Model.model.deleteFromSportcourseFavorite(course.getId());
                    courseFavoriteBT.setImageResource(R.drawable.favorite_grey);
                }
                else{
                    course.setFavorit(true);
                    courseFavoriteBT.setImageResource(R.drawable.favorite_red);
                    Model.model.addToSportCourseFavorite(course.getId());

                }
            }
        });

        if(course.getTele().isEmpty()){
            courseCallBT.setVisibility(View.GONE);
        }

        else{
            courseCallBT.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final CharSequence numbers[];

                    String[] courseTele = course.getTele().split(",");
                    if(courseTele.length==2){
                         numbers = new CharSequence[] {courseTele[0], courseTele[1]};
                    }
                    else if(courseTele.length==3){
                        numbers = new CharSequence[] {courseTele[0], courseTele[1], courseTele[2]};
                    }
                    else {
                        numbers = new CharSequence[] {courseTele[0]};
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + course.getTele() ));
                        startActivity(intent);
                    }

                    if(courseTele.length>=2) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Nummer auswählen");
                        builder.setItems(numbers, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + numbers[which]));
                                startActivity(intent);
                            }
                        });
                        builder.show();
                    }
                }
            });
        }

        courseMailBT.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String[] courseMails = course.getMail().split(",");

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, courseMails);
                intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                startActivity(Intent.createChooser(intent, ""));
            }
        });
    }


    public void initialiseListView(String[]times) {

        ListItemTimesAdapter adapter = new ListItemTimesAdapter(getActivity(), times);

        courseTimesTV.setAdapter(adapter);
    }
}
