package com.example.schlauefuechse.refugeeconnect.Controller;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schlauefuechse.refugeeconnect.Model.Model;
import com.example.schlauefuechse.refugeeconnect.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainFragment.OnButtonSelectedListener,
        FreeTimeFragment.FreeTimeButtonListener, ListFragment.OnListRowSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    NavigationView navigationView = null;

    TextView toolbarTV = null;
    Toolbar toolbar = null;

    ImageButton bonusCardInfoBtn;
    Bundle args;

    //String saving the last Title
    ArrayList<String> lastTitle;
    int lastTitleIndex;

    //Boolean (zeigt an, ob der Benutzer von der Favoriten-Activity aus in eine Detail-Activity
    //gelangt ist und somit auch bei Klick auf den Back-Button auch dorthin zurück wechseln sollte)
    static boolean backToFavoriteActivity = false;

    MenuItem bonusCardInfo;

    //---------- Variables needed for the location -------
    private GoogleMap mGoogleMap;
    private SupportMapFragment mFragment;

    public Boolean permissionGranted;

    private GoogleApiClient client;
    public Location mLastLocation;

    private ListView listView;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 193;

    private Boolean isProviderEnabled;

    public LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTV = (TextView) findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        toolbarTV.setText(R.string.refugeeconnect);
        lastTitle = new ArrayList<>();
        lastTitle.add(getString(R.string.refugeeconnect));
        lastTitleIndex = 0;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        drawer.setDrawerListener(toggle);

        toggle.syncState();

        args = new Bundle();

        if(!Model.model.doVersionControll(this, getApplicationContext())){
            //Set the fragment
            ErrorFragment fragment = new ErrorFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            //Set the fragment
            MainFragment fragment = new MainFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Button in BonusCard für weitere Informationen
        bonusCardInfoBtn = (ImageButton) findViewById(R.id.bonus_card_infoBtn);
        bonusCardInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupWindowInfo(bonusCardInfoBtn);
            }
        });

        //--------- Location code starts here --------
        if (client == null) {
            Log.d("onConnected", "client is null");
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API)
                    .build();
        }

        //Ask the user for permission at runtime
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStart", "called");

        if (permissionGranted != null && client != null) {
            if (!client.isConnected()) {
                //client is not yet connected -> needs to be connected
                Log.d("onStart", "connect client");
                client.connect();
            }
        }

        Action viewAction = Action.newAction(
                        Action.TYPE_VIEW,
                        "Main Page",
                        Uri.parse("http://host/path"),
                        Uri.parse("android-app://com.example.schlauefuechse.refugeeconnect/http/host/path")
                );
                AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    Log.d("permissionGranted", "true");
                } else {
                    permissionGranted = false;
                    Log.d("permissionGranted", "false");
                }
                client.connect();
                return;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("onConnected", "called");
        if (permissionGranted) {
            Log.d("onConnected", "permissionGranted");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
            if (mLastLocation == null) {
                isProviderEnabled = false;
                Log.d("onConnected", "Last location is null");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Please activate your location.")
                        .setMessage("Click 'ok' to go to your location settings.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else {
                isProviderEnabled = true;
                Log.d("onConnected", "Last location is not null");
            }
        } else {
            Log.d("onConnected", "permission not granted");
            Log.d("onConnected", "Location could not be shown.");
        }

        //checking for the case that headers have not been initialised due to missing internet provider
        if(Model.model.listDataHeaderSportCourse != null && Model.model.listDataChildSportCourse == null) {
            //initialise lists -> done here to make sure that the location request is already done.
            Model.model.initialiseListChildren();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Model.model.saveFavorites();
        Model.model.saveAllData();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            toolbarTV.setText(lastTitle.get(lastTitleIndex));
            lastTitle.remove(lastTitleIndex);
            lastTitleIndex--;
            super.onBackPressed();
        }
    }

    // Display popup window to filter the SportCourses
    private void displayPopupWindowInfo(View anchorView) {

        View layoutPopupWindow;
        layoutPopupWindow = getLayoutInflater().inflate(R.layout.popup_info_bonuscard, null);
        final PopupWindow popup = new PopupWindow(layoutPopupWindow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        popup.setBackgroundDrawable(new BitmapDrawable());

        final Button bonusCardCloseBtn = (Button) layoutPopupWindow.findViewById(R.id.bonusCardCloseBtn);
        final TextView bonusCardWebsite = (TextView) layoutPopupWindow.findViewById(R.id.infoWebsite);
        bonusCardWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = bonusCardWebsite.getText().toString();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        bonusCardCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
        popup.showAsDropDown(anchorView);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Set the fragment depending on the id
        if (id == R.id.nav_home) {
            MainFragment fragment = new MainFragment();
            changeFragment(fragment);
            lastTitleIndex++;
            lastTitle.add(toolbarTV.getText().toString());
            toolbarTV.setText(R.string.refugeeconnect);
            backToFavoriteActivity = false;
        } else if (id == R.id.nav_free_time) {
            FreeTimeFragment fragment = new FreeTimeFragment();
            args.putString("type", "freeTime");
            fragment.setArguments(args);
            changeFragment(fragment);
            lastTitleIndex++;
            lastTitle.add(toolbarTV.getText().toString());
            toolbarTV.setText(R.string.freetime);
            backToFavoriteActivity = false;
        } else if (id == R.id.nav_medical_scientist) {
            //old_ListFragment fragment = new old_ListFragment();
            ListFragment fragment = new ListFragment();
            args.putString("type", "medical");
            fragment.setArguments(args);
            changeFragment(fragment);
            lastTitleIndex++;
            lastTitle.add(toolbarTV.getText().toString());
            toolbarTV.setText(R.string.doctors);
            backToFavoriteActivity = false;
        } else if (id == R.id.nav_contacts) {
            //old_ListFragment fragment = new old_ListFragment();
            ContactFragment fragment = new ContactFragment();
            args.putString("type", "contact");
            fragment.setArguments(args);
            changeFragment(fragment);
            lastTitleIndex++;
            lastTitle.add(toolbarTV.getText().toString());
            toolbarTV.setText(R.string.contacts);
            backToFavoriteActivity = false;
        } else if (id == R.id.nav_favorite) {
            //old_ListFragment fragment = new old_ListFragment();
            ListFragment fragment = new ListFragment();
            args.putString("type", "favorite");
            fragment.setArguments(args);
            changeFragment(fragment);
            lastTitleIndex++;
            lastTitle.add(toolbarTV.getText().toString());
            toolbarTV.setText(R.string.favorite);
            backToFavoriteActivity = true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("onStop", "onStopCalled");

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.example.schlauefuechse.refugeeconnect/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    //Home screen methode
    @Override
    public void onButtonSelected(String name) {

        if (name.equals("freeTime")) {
            FreeTimeFragment fragment = new FreeTimeFragment();
            changeFragment(fragment);
            lastTitleIndex++;
            lastTitle.add(toolbarTV.getText().toString());
            toolbarTV.setText(R.string.freetime);
            backToFavoriteActivity = false;
        } else if (name.equals("medical")) {
            ListFragment fragment = new ListFragment();
            //old_ListFragment fragment = new old_ListFragment();
            args.putString("type", "medical");
            fragment.setArguments(args);
            changeFragment(fragment);
            lastTitleIndex++;
            lastTitle.add(toolbarTV.getText().toString());
            toolbarTV.setText(R.string.doctors);
            backToFavoriteActivity = false;
        } else if (name.equals("contact")) {
            ContactFragment fragment = new ContactFragment();
            //old_ListFragment fragment = new old_ListFragment();
            args.putString("type", "contact");
            fragment.setArguments(args);
            changeFragment(fragment);
            lastTitleIndex++;
            lastTitle.add(toolbarTV.getText().toString());
            toolbarTV.setText(R.string.contacts);
            backToFavoriteActivity = false;
        }
    }

    //Free time fragment methode
    @Override
    public void onFreeTimeButtonSelected(String name) {
        if (name.equals("bonusCard")) {
            BonusCardFragment fragment = new BonusCardFragment();
            changeFragment(fragment);
            lastTitleIndex++;
            lastTitle.add(toolbarTV.getText().toString());
            toolbarTV.setText(R.string.bonusfamilycard);
            backToFavoriteActivity = false;
        } else if (name.equals("course")) {
            ListFragment fragment = new ListFragment();
            //old_ListFragment fragment = new old_ListFragment();
            args.putString("type", "course");
            fragment.setArguments(args);
            changeFragment(fragment);
            lastTitleIndex++;
            lastTitle.add(toolbarTV.getText().toString());
            toolbarTV.setText(R.string.course);
            backToFavoriteActivity = false;
        }
    }

    // list fragment methode
    @Override
    public void onRowSelected(String type, int id, String titleType) {
        if(type.equals("course")){
            DetailFragment fragment = new DetailFragment();
            //Sets arguments to Bundle so that the fragment can use them
            args.putString("type", type);
            args.putInt("id", id);
            fragment.setArguments(args);
            changeFragment(fragment);
            lastTitleIndex++;
            lastTitle.add(toolbarTV.getText().toString());
            toolbarTV.setText(titleType);
        } else if(type.equals("medical")){
            DetailFragment fragment = new DetailFragment();
            //Sets arguments to Bundle so that the fragment can use them
            args.putString("type", type);
            args.putInt("id", id);
            fragment.setArguments(args);
            changeFragment(fragment);
            lastTitleIndex++;
            lastTitle.add(toolbarTV.getText().toString());
            toolbarTV.setText(titleType);
        } else if(type.equals("bonus")) {
            BonusCardFragment fragment = new BonusCardFragment();
            //Sets arguments to Bundle so that the fragment can use themZ
            args.putInt("id", id);
            fragment.setArguments(args);
            changeFragment(fragment);
            lastTitleIndex++;
            lastTitle.add(toolbarTV.getText().toString());
            toolbarTV.setText(R.string.bonuscard);
        }
    }

    public void changeFragment(Fragment fragment){
        // Replace whatever is in the fragment_container view with the entered fragment,
        // and add the transaction to the back stack so the user can navigate back
        if(fragment instanceof BonusCardFragment){
            bonusCardInfoBtn.setVisibility(View.VISIBLE);
        } else {
            bonusCardInfoBtn.setVisibility(View.INVISIBLE);
        }

        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        // Commit the transaction
        fragmentTransaction.commit();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("onConnectionSuspended", "called");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("onConnectionFailed", "called");
    }
}
