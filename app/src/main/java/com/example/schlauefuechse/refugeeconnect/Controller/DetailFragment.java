package com.example.schlauefuechse.refugeeconnect.Controller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.Doctor;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.SportCourse;
import com.example.schlauefuechse.refugeeconnect.Model.Model;
import com.example.schlauefuechse.refugeeconnect.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class DetailFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    String bundleType;
    int bundleId;

    View rootView;

    LatLng latLng;
    LocationRequest mLocationRequest;

    GoogleMap mGoogleMap;
    private Location ownLocation;

    private GoogleApiClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if (savedInstanceState != null) {
            bundleType = savedInstanceState.getString("type");
            bundleId = savedInstanceState.getInt("id");
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment fragment;
        fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);


        Bundle args = getArguments();
        bundleId = args.getInt("id");
        bundleType = args.getString("type");

        if(bundleType.equals("course")){
            setDetailFragmentTextCourse();
            SportCourse course = Model.model.getCourseToID(bundleId);
        } else if(bundleType.equals("medical")){
            setDetailFragmentTextMedical();
            Doctor doctor = Model.model.getDoctorToID(bundleId);
            doctor.getAddress();
        }
    }

    public void setDetailFragmentTextCourse(){
        //DetailCourseFragment wird gesetzt und die erhaltenen Werte werden an dieses weiter übergeben
        Fragment detailTextFragment = new DetailCourseFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        Bundle argsDetail = new Bundle();
        argsDetail.putInt("id", bundleId);
        argsDetail.putString("type", bundleType);
        detailTextFragment.setArguments(argsDetail);
        transaction.add(R.id.innerFragment, detailTextFragment).commit();
    }

    public void setDetailFragmentTextMedical(){
        //DetailDoctorFragment wird gesetzt und die erhaltenen Werte werden an dieses weiter übergeben
        Fragment detailTextFragment = new DetailDoctorFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        Bundle argsDetail = new Bundle();
        argsDetail.putInt("id", bundleId);
        argsDetail.putString("type", bundleType);
        detailTextFragment.setArguments(argsDetail);
        transaction.add(R.id.innerFragment, detailTextFragment).commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt("id", bundleId);
        outState.putString("type", bundleType);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    client);
            ownLocation = mLastLocation;
        }
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000); //10 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) throws SecurityException {


        mGoogleMap = googleMap;
        if (Model.model.isDistanceAllowed) {
            mGoogleMap.setMyLocationEnabled(true);

        } else {
            mGoogleMap.setMyLocationEnabled(false);
        }

        buildGoogleApiClient();
        client.connect();

        CameraPosition cameraPosition;

        if(bundleType.equals("course")){

            SportCourse course = Model.model.getCourseToID(bundleId);
            cameraPosition = new CameraPosition.Builder()
                    .target(Model.model.getLocationFromAddress(getContext(),course.getAddress())).zoom(12).build();
            //Setzen der Annotation
            Model.model.setDetailAnnotation(course.getType(),course.getAddress(),getContext(),mGoogleMap);

        } else{

            Doctor doctor= Model.model.getDoctorToID(bundleId);
            cameraPosition = new CameraPosition.Builder()
                    .target(Model.model.getLocationFromAddress(getContext(),doctor.getAddress())).zoom(12).build();
            //Setzen der Annotation
            Model.model.setDetailAnnotation(doctor.getType(),doctor.getAddress(),getContext(),mGoogleMap);
        }

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    }
    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

}

