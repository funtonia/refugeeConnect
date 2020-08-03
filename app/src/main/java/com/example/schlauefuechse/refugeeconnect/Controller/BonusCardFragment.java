package com.example.schlauefuechse.refugeeconnect.Controller;

import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.BonusCard;
import com.example.schlauefuechse.refugeeconnect.Model.ListItemBonusAdapter;
import com.example.schlauefuechse.refugeeconnect.Model.Model;
import com.example.schlauefuechse.refugeeconnect.R;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;

import static com.example.schlauefuechse.refugeeconnect.R.*;
import static com.example.schlauefuechse.refugeeconnect.R.drawable.*;

public class BonusCardFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    GoogleMap mGoogleMap;
    int bundleId = -1;


    //ArrayList mit allen BonusCard-Verguenstigungen
    ArrayList<BonusCard> bonus = new ArrayList<>();

    //Eigene Location
    private GoogleApiClient client;
    Location mLastLocation;

    View rootView;
    SupportMapFragment fragment;

    //ListView unterhalb der Karte
    ListView bonuscardLV;
    ImageButton bonusCallBT;
    ImageButton bonusMailBT;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(layout.fragment_bonuscard, container, false);



        if (savedInstanceState != null) {
            bundleId = savedInstanceState.getInt("id");
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buildGoogleApiClient();
        client.connect();

        bonuscardLV = (ListView) rootView.findViewById(id.bonuscardLV);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);

        } else {
            mGoogleMap.setMyLocationEnabled(false);
        }

        bonus = Model.model.bonusCardArrayList;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("bonusCard", "distances are calculated");
            ArrayList<BonusCard> childElementsSorted = Model.model.getSortedKeysBonusCard(Model.model.computeDistancesBonusCard(mLastLocation.getLatitude(), mLastLocation.getLongitude(), bonus));
            initialiseListView(childElementsSorted);
        } else {
            Log.d("bonusCard", "distances are not calculated");
            initialiseListView(bonus);
        }

        setBonusAnnotations(getContext(), bonus, mGoogleMap);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(48.776879, 9.181475)).zoom(12).build();

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        if(bundleId != -1) {

            BonusCard bonusCard = bonus.get(bundleId);

            Location loc = new Location("");
            LatLng locationBonus = Model.model.getLocationFromAddress(getContext(),bonusCard.getAddress());
            loc.setLatitude(locationBonus.latitude);
            loc.setLongitude(locationBonus.longitude);

            highlightPointAnnotations(getContext(), mGoogleMap, bonusCard);

            Model.model.showMapMiddle(locationBonus, mGoogleMap);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //  TextView tvLocation = (TextView) findViewById(R.id.tv_location);

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) throws SecurityException{

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        }

        fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(id.map);
        fragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void initialiseListView(ArrayList<BonusCard> bonus) {

        ListItemBonusAdapter adapter = new ListItemBonusAdapter(getActivity(), bonus);

        bonuscardLV.setAdapter(adapter);

        bonuscardLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private View pressedView = null;
            Boolean isExpanded = false;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                final BonusCard bonusCard = (BonusCard) bonuscardLV.getItemAtPosition(position);

                Location loc = new Location("");
                LatLng locationBonus = Model.model.getLocationFromAddress(getContext(),bonusCard.getAddress());
                loc.setLatitude(locationBonus.latitude);
                loc.setLongitude(locationBonus.longitude);

                LinearLayout rl_inflate;

                highlightPointAnnotations(getContext(), mGoogleMap,bonusCard);

                Model.model.showMapMiddle(locationBonus,mGoogleMap);

                if(!isExpanded) {
                    if(pressedView!=null) {
                        rl_inflate = (LinearLayout) pressedView.findViewById(R.id.rl_inflate);
                        rl_inflate.removeAllViews();

                    }
                    rl_inflate = (LinearLayout)view.findViewById(R.id.rl_inflate);
                    View child = getLayoutInflater(Bundle.EMPTY).inflate(layout.bonus_inflate, null);
                    rl_inflate.addView(child);

                    bonusCallBT = (ImageButton) child.findViewById(R.id.bonus_callBT);
                    bonusMailBT = (ImageButton) child.findViewById(R.id.bonus_mailBT);
                    TextView bonusWebsiteTV = (TextView) child.findViewById(R.id.bonus_website);
                    final ImageButton bonusFavoriteBT = (ImageButton) child.findViewById(R.id.bonus_favoriteBT);

                    //Abfrage ob Mail und Telefonnummer verfügbar ist
                    if(bonusCard.getMail().isEmpty()){
                        //bonusMailBT.setEnabled(false);
                        bonusMailBT.setVisibility(View.GONE);
                        //bonusMailBT.setBackgroundColor(getResources().getColor(color.darkgrey));
                    }
                    else{
                        bonusMailBT.setOnClickListener( new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("plain/text");
                                intent.putExtra(Intent.EXTRA_EMAIL, bonusCard.getMail());
                                intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                                intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                                startActivity(Intent.createChooser(intent, ""));
                            }
                        });

                    }
                    if(bonusCard.getTele().isEmpty()){
                        bonusCallBT.setVisibility(View.GONE);
                        Log.e("BonusCard","keine Telefonnummer");
                    }
                    else{

                        bonusCallBT.setOnClickListener( new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + bonusCard.getTele()));
                                startActivity(intent);
                            }
                        });
                    }

                    if(bonusCard.getWebsite().isEmpty()){
                        Log.e("BonusCard","keine Website");
                        //bonusWebsiteTV.setEnabled(false);
                    }

                    else {
                        bonusWebsiteTV.setText(bonusCard.getWebsite());
                        bonusWebsiteTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url = bonusCard.getWebsite();
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        });
                    }

                    if(bonusCard.isFavorit()){
                        bonusFavoriteBT.setImageResource(favorite_red);
                    }
                    else{
                        bonusFavoriteBT.setImageResource(favorite_grey);
                    }

                    bonusFavoriteBT.setOnClickListener( new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if(bonusCard.isFavorit()){
                                bonusCard.setFavorit(false);
                                Model.model.deleteFromBonusCardFavorite(bonusCard.getId());
                                bonusFavoriteBT.setImageResource(favorite_grey);
                            }
                            else{
                                bonusCard.setFavorit(true);
                                bonusFavoriteBT.setImageResource(favorite_red);
                                Model.model.addToBonusCardFavorite(bonusCard.getId());
                            }
                        }
                    });

                    bonusFavoriteBT.setFocusable(false);
                    bonusFavoriteBT.setFocusableInTouchMode(false);
                    bonusFavoriteBT.setClickable(true);
                    bonusMailBT.setFocusable(false);
                    bonusMailBT.setFocusableInTouchMode(false);
                    bonusMailBT.setClickable(true);
                    bonusCallBT.setFocusable(false);
                    bonusCallBT.setFocusableInTouchMode(false);
                    bonusCallBT.setClickable(true);

                    isExpanded = true;
                } else {
                    rl_inflate = (LinearLayout)view.findViewById(R.id.rl_inflate);
                    rl_inflate.removeAllViews();
                    isExpanded = false;
                }
            pressedView = view;
            }
        });
    }

    //Annotation wird an Adresse der Detail-View Location gesetzt
    public void setBonusAnnotations(Context context, final ArrayList<BonusCard> bonus, GoogleMap mGoogleMap) {
        this.mGoogleMap = mGoogleMap;
        int counter = 0;
        for (final BonusCard b : bonus) {

            LatLng address = Model.model.getLocationFromAddress(context, b.getAddress());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(address);
            markerOptions.title(b.getName());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(marker));
            mGoogleMap.addMarker(markerOptions);
            counter++;
        }
    }

    //zugehörige Annotation wird gehighlightet sobald Nutzer auf einen Listeneintrag klickt

    public void highlightPointAnnotations(Context context, GoogleMap mGoogleMap, BonusCard selectedBonusCard) {

        Toast.makeText(getContext(), "calledMethod", Toast.LENGTH_SHORT);

        this.mGoogleMap = mGoogleMap;
        int counter = 0;

        for (final BonusCard bonus : this.bonus) {
            if(bonus!=null){
                LatLng locationBonus = Model.model.getLocationFromAddress(context, bonus.getAddress());

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(locationBonus);
                markerOptions.title(bonus.getName());
                markerOptions.icon(BitmapDescriptorFactory.fromResource(marker));
                counter++;
                Marker m = mGoogleMap.addMarker(markerOptions);
                if(selectedBonusCard.equals(bonus)){
                    Model.model.showMapMiddle(locationBonus,mGoogleMap);
                    m.showInfoWindow();
                    Toast.makeText(getContext(), "calledIf", Toast.LENGTH_SHORT);
                }
            }
        }
    }


}