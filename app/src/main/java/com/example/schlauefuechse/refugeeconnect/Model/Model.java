package com.example.schlauefuechse.refugeeconnect.Model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.example.schlauefuechse.refugeeconnect.Controller.AsyncDataFetch;
import com.example.schlauefuechse.refugeeconnect.Controller.AsyncVersionFetch;
import com.example.schlauefuechse.refugeeconnect.Controller.ListFragment;
import com.example.schlauefuechse.refugeeconnect.Controller.MainActivity;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.BonusCard;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.Contact;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.Doctor;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.SportCourse;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.VersionControll;
import com.example.schlauefuechse.refugeeconnect.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by Antonia on 07.05.2016.
 */
public class Model {
    public final static Model model = new Model();
    MainActivity main;
    GoogleMap mGoogleMap;

    public ArrayList<BonusCard> bonusCardArrayList = new ArrayList<>();
    public ArrayList<Contact> contactArrayList = new ArrayList<>();
    public ArrayList<Doctor> doctorArrayList = new ArrayList<>();
    public ArrayList<SportCourse> sportCourseArrayList = new ArrayList<>();

    //Elements for the expandable List
    public List<String> listDataHeader = new ArrayList<>();
    public HashMap<String, List> listDataChild = new HashMap<>();

    //Elements for doctors
    public List<String> listDataHeaderDoctor;
    public HashMap<String, List<Doctor>> listDataChildDoctor;

    //Elements for sportCourses
    public List<String> listDataHeaderSportCourse;
    public HashMap<String, List<SportCourse>> listDataChildSportCourse;

    //Elements for favorites
    public List<String> listDataHeaderFavorite;
    public HashMap<String, List<Object>> listDataChildFavorite;

    //ArrayLists containing the favourite elements by type (sport course, doctor, contact)
    public List<Object> childElementsSportCourse = new ArrayList<>();
    public List<Object> childElementsBonusCard = new ArrayList<>();
    public List<Object> childElementsDoctor = new ArrayList<>();
    public List<Object> childElementsContact = new ArrayList<>();

    //Filtered ArrayList
    private ArrayList<Doctor> doctorArrayListFiltered;
    private ArrayList<SportCourse> sportCourseArrayListFiltered;

    private final String SPORTCOURSE_JSON_FILE = "SportCourse.json";
    private final String CONTACT_JSON_FILE = "Contact.json";
    private final String DOCTOR_JSON_FILE = "Doctor.json";
    private final String BONUSCARD_JSON_FILE = "BonusCard.json";
    private final String VERSIONNUMBER_JSON_FILE = "Versionnumber.json";

    private final String CONTACT_FAVORITE_JSON_FILE = "ContactFavorite.json";
    private final String DOCTOR_FAVORITE_JSON_FILE = "DoctorFavorite.json";
    private final String BONUSCARD_FAVORITE_JSON_FILE = "BonusCardFavorite.json";
    private final String SPORTCOURSE_FAVORITE_JSON_FILE = "SportCourseFavorite.json";
    private HashSet<Integer> contactFavorite = new HashSet<Integer>();
    private HashSet<Integer> doctorFavorite = new HashSet<Integer>();
    private HashSet<Integer> sportCourseFavorite = new HashSet<Integer>();
    private HashSet<Integer> bonusCardFavorite = new HashSet<Integer>();
    private boolean isntFirstPull = false;

    //Map containing the names and distances of all doctors (of all types) in a non-sorted way
    public HashMap<Doctor, Integer> namesDistancesDoctors = new HashMap<>();

    //Map containing the names and distances of all sport courses (of all types) in a non-sorted way
    public HashMap<SportCourse, Integer> namesDistancesSportsCourses = new HashMap<>();

    //Map containing the names and distances of all bonus card objects in a non-sorted way
    public HashMap<BonusCard, Integer> namesDistancesBonusCard = new HashMap<>();

    //Boolean storing whether location services are on AND we are allowed to use them
    public Boolean isDistanceAllowed = false;

    public String systemLanguage = "";
    private VersionControll versionNumber = null;
    public VersionControll newVersionNumber = null;

    public boolean versionNumberFetched = false;
    public boolean sportCourseFetched = false;
    public boolean contactFetched = false;
    public boolean doctorFetched = false;
    public boolean bonusCardFetched = false;

    //Boolean of the Doctor-Filter
    public boolean d_female = false;
    public boolean d_male = false;
    public boolean d_malefemale = true;
    public boolean d_ar = false;
    public boolean d_it = false;
    public boolean d_dr = false;
    public boolean d_fa = false;
    public boolean d_tr = false;
    public boolean d_fr = false;
    public boolean d_en = false;
    public boolean d_sq = false;
    public boolean d_sh = false;
    public boolean d_el = false;
    public boolean d_hr = false;
    public boolean d_sr = false;
    public boolean d_ru = false;
    public boolean d_ro = false;
    public boolean d_mz = false;
    public boolean d_bs = false;
    public boolean d_es = false;

    //Boolean of the SportCourse-Filter
    public boolean sc_female = false;
    public boolean sc_male = false;
    public boolean sc_mixed = true;
    public boolean sc_children = false;
    public boolean sc_adolescents = false;
    public boolean sc_young_adults = false;
    public boolean sc_adults = false;
    public boolean sc_parents_child = false;

    private Context context;

    private Model(){
        super();
    }

    /**
     * Reading the data from the different .json files.
     * - parameters:
     *      - jsonType: The type (contact, doctor etc) of the json
     */
    public void loadJSONFromAsset(Context context) {
        //Array containing the different json-file-types
        String[] jsonTypes = {"bonusCard", "contact", "doctor", "sportCourse"};
        //String that will later contain the json from the file
        String json;
        this.context = context;

        try {
            //Iterating over the four different json-file-types
            for(String s: jsonTypes){
                InputStream is = context.getAssets().open(s + ".json");

                int size = is.available();

                byte[] buffer = new byte[size];

                is.read(buffer);

                is.close();

                json = new String(buffer, "UTF-8");

                //Depending on which type the json is, call a different method to add it to the ArrayLists
                switch(s) {
                    case "bonusCard":
                        addBonusCardToArrayList(json, s);
                        break;
                    case "contact":
                        addContactsToArrayList(json, s);
                        break;
                    case "doctor":
                        addDoctorsToArrayList(json, s);
                        break;
                    case "sportCourse":
                        addSportCourseToArrayList(json, s);
                        break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Adding the data retrieved from the loadJSONFromAsset() method to the BonusCard ArrayList.
     * <p/>
     * - parameters:
     *      - json: The string retrieved from the json-file
     *      - jsonType: the type of the jsonFile standing in front of the data in the json
     */
    private void addBonusCardToArrayList(String json, String jsonType) {
        try {
            JSONObject jsonObj = new JSONObject(json);
            //jsonType has to be the word used in the json ("toilets" in need2pee)!
            JSONArray jsonArray = jsonObj.getJSONArray(jsonType);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject js = jsonArray.getJSONObject(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adding the data retrieved from the loadJSONFromAsset() method to the Doctor ArrayList.
     * <p/>
     * - parameters:
     *      - json: The string retrieved from the json-file
     *      - jsonType: the type of the jsonFile standing in front of the data in the json
     */
    private void addDoctorsToArrayList(String json, String jsonType) {
        try {
            JSONObject jsonObj = new JSONObject(json);
            //jsonType has to be the word used in the json ("toilets" in need2pee)!
            JSONArray jsonArray = jsonObj.getJSONArray(jsonType);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject js = jsonArray.getJSONObject(i);

                Doctor doctor = new Doctor(js.getString("name"), js.getString("type"), js.getString("gender"), js.getString("languages"), js.getString("address"), js.getString("tele"), js.getString("lang"), js.getInt("id"));
                doctorArrayList.add(doctor);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adding the data retrieved from the loadJSONFromAssett() method to the SportCourse ArrayList.
     * <p/>
     * - parameters:
     *      - json: The string retrieved from the json-file
     *      - jsonType: the type of the jsonFile standing in front of the data in the json
     */
    private void addSportCourseToArrayList(String json, String jsonType) {
        try {
            JSONObject jsonObj = new JSONObject(json);
            //jsonType has to be the word used in the json ("toilets" in need2pee)!
            JSONArray jsonArray = jsonObj.getJSONArray(jsonType);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject js = jsonArray.getJSONObject(i);

                SportCourse sportCourse = new SportCourse(js.getString("type"), js.getString("group"), js.getString("tele"), js.getString("mail"), js.getString("address"), js.getString("days"), js.getString("times"), js.getString("lang"), js.getInt("id"));

                sportCourseArrayList.add(sportCourse);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adding the data retrieved from the loadJSONFromAssett() method to the Contacts ArrayList.
     * <p/>
     * - parameters:
     *      - json: The string retrieved from the json-file
     *      - jsonType: the type of the jsonFile standing in front of the data in the json
     */
    private void addContactsToArrayList(String json, String jsonType) {
        try {
            JSONObject jsonObj = new JSONObject(json);
            //jsonType has to be the word used in the json ("toilets" in need2pee)!
            JSONArray jsonArray = jsonObj.getJSONArray(jsonType);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject js = jsonArray.getJSONObject(i);

                Contact contact = new Contact(js.getString("name"), js.getString("type"), js.getString("mail"), js.getString("lang"), js.getInt("id"));
                contactArrayList.add(contact);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //-------------------- Methods for the lists --------------------

    public void initialiseListHeaders() {
        Log.d("ListInitialising", "Initialising headers");

        prepareListDataHeaderDoctor(doctorArrayList);
        prepareListDataHeaderSportCourse(sportCourseArrayList);
    }

    public void initialiseListChildren() {
        Log.d("ListInitialising", "Initialising children");

        prepareListDataChildrenDoctor(doctorArrayList);
        prepareListDataChildrenSportCourse(sportCourseArrayList);
    }

    public void changeExpandableListData(String type) {
        listDataChild.clear();
        listDataHeader.clear();

        switch (type) {
            case "medical":
                Log.d("listDataChildDoctor", String.valueOf(listDataChildDoctor.isEmpty()));
                listDataChild.putAll(listDataChildDoctor);
                listDataHeader.addAll(listDataHeaderDoctor);
                Log.d("listDataHeaderDoctor", String.valueOf(listDataHeaderDoctor.isEmpty()));

                break;
            case "favorite":
                listDataChild.putAll(listDataChildFavorite);
                listDataHeader.addAll(listDataHeaderFavorite);
                break;
            case "course":
                listDataChild.putAll(listDataChildSportCourse);
                listDataHeader.addAll(listDataHeaderSportCourse);
                break;
        }
    }

    //Filtered ArrayList Doctor
    public void prepareListDataDoctorFiltered() {


        doctorArrayListFiltered = new ArrayList<>();
        ArrayList<Doctor> tmpArrayList = new ArrayList<>();

        if(d_male){
            for(Doctor c : Model.model.doctorArrayList){
                if(c.getGender().equals(R.string.male)){
                    tmpArrayList.add(c);
                }
            }
        } else if(d_female){
            for(Doctor c : Model.model.doctorArrayList){
                if(c.getGender().equals(R.string.female)){
                    tmpArrayList.add(c);
                }
            }
        } else {
            tmpArrayList = Model.model.doctorArrayList;
        }

        for(Doctor c : tmpArrayList){
            List<String> languagesArrayList = Arrays.asList(c.getLanguages().split(","));
            if(d_ar){
                if(languagesArrayList.contains("ar")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_it){
                if(languagesArrayList.contains("it")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_dr){
                if(languagesArrayList.contains("dr")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_fa){
                if(languagesArrayList.contains("fa")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_tr){
                if(languagesArrayList.contains("tr")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_fr){
                if(languagesArrayList.contains("fr")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_en){
                if(languagesArrayList.contains("en")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_sq){
                if(languagesArrayList.contains("sq")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_sh){
                if(languagesArrayList.contains("sh")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_el){
                if(languagesArrayList.contains("el")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_hr){
                if(languagesArrayList.contains("hr")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_sr){
                if(languagesArrayList.contains("sr")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_ru){
                if(languagesArrayList.contains("ru")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_ro){
                if(languagesArrayList.contains("ro")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_mz){
                if(languagesArrayList.contains("mz")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_bs){
                if(languagesArrayList.contains("bs")){
                    doctorArrayListFiltered.add(c);
                }
            } else if(d_es){
                if(languagesArrayList.contains("es")){
                    doctorArrayListFiltered.add(c);
                }
            }
        }
        if(doctorArrayListFiltered.isEmpty()){
            doctorArrayListFiltered = tmpArrayList;
        }
        prepareListDataHeaderDoctor(doctorArrayListFiltered);
        prepareListDataChildrenDoctor(doctorArrayListFiltered);
        changeExpandableListData("medical");
    }

    //Doctor
    private void prepareListDataHeaderDoctor(ArrayList<Doctor> filteredList) {
        listDataHeaderDoctor = new ArrayList<>();

        //Adding header data
        for(Doctor c : filteredList) {
            //Add the contact's types to the ArrayList (without duplicates)
            if (!listDataHeaderDoctor.contains(c.getType())) {
                Log.d("AddedHeader", "Added header type: " + c.getType());
                listDataHeaderDoctor.add(c.getType());
            }
        }
    }

    private void prepareListDataChildrenDoctor(ArrayList<Doctor> filteredList) {
        listDataChildDoctor = new HashMap<>();
        List<Doctor> childElements;

        int counter = 0;

        //Adding child data -> ab hier neue Methode
        for(String s : listDataHeaderDoctor) {
            //ArrayList containing the child elements
            childElements = new ArrayList<>();

            //Add all the elements belonging to this type to the arrayList
            for (Doctor c : filteredList) {
                //Is the contact's type equal to the type we want to add elements to?
                if (c.getType().equals(s)) {
                    //Add the element to the arrayList containing the child elements
                    childElements.add(c);
                    Log.d("AddedChild", "Added child '" + c.getName() + "' to header type " + s);
                }
            }

            if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && main.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && main.mLastLocation != null) {
                isDistanceAllowed = true;
            } else {
                isDistanceAllowed = false;
            }


            //May the location be used and is there a location?
            if (isDistanceAllowed) {
                Log.d("childElements", "distances are calculated");
                ArrayList<Doctor> childElementsSorted = getSortedKeysDoctor(computeDistancesDoctor(main.mLastLocation.getLatitude(), main.mLastLocation.getLongitude(), childElements));
                listDataChildDoctor.put(listDataHeaderDoctor.get(counter), childElementsSorted);
                counter++;
            } else {
                Log.d("childElements", "distances are not calculated");
                listDataChildDoctor.put(listDataHeaderDoctor.get(counter), childElements);
                counter++;
            }
        }

    }

    //Filteres ArrayList SportCourse
    public void prepareListDataSportCourseFiltered() {
        sportCourseArrayListFiltered = new ArrayList<>();
        ArrayList<SportCourse> tmpArrayList = new ArrayList<>();

        List<String> groupArrayList2 = Arrays.asList(Model.model.sportCourseArrayList.get(1).getGroup().split(",\\u0020"));

        if(sc_male){
            for(SportCourse c : Model.model.sportCourseArrayList){
                List<String> groupArrayList = Arrays.asList(c.getGroup().split(",\\u0020"));
                if(groupArrayList.contains(main.getString(R.string.male))){
                    tmpArrayList.add(c);
                }
            }
        } else if(sc_female){
            for(SportCourse c : Model.model.sportCourseArrayList){
                List<String> groupArrayList = Arrays.asList(c.getGroup().split(",\\u0020"));
                if(groupArrayList.contains(main.getString(R.string.female))){
                    tmpArrayList.add(c);
                }
            }
        } else {
            tmpArrayList = Model.model.sportCourseArrayList;
        }

        for(SportCourse c : tmpArrayList) {
            List<String> groupArrayList = Arrays.asList(c.getGroup().split(",\\u0020"));
            if (sc_children) {
                if (groupArrayList.contains(main.getString(R.string.children))) {
                    sportCourseArrayListFiltered.add(c);
                }
            } else if (sc_adolescents) {
                if (groupArrayList.contains(main.getString(R.string.adolescents))) {
                    sportCourseArrayListFiltered.add(c);
                }
            } else if (sc_young_adults) {
                if (groupArrayList.contains(main.getString(R.string.youngadults))) {
                    sportCourseArrayListFiltered.add(c);
                }
            } else if (sc_adults) {
                if (groupArrayList.contains(main.getString(R.string.adults))) {
                    sportCourseArrayListFiltered.add(c);
                }
            } else if (sc_parents_child) {
                if (groupArrayList.contains(main.getString(R.string.parents_with_child))) {
                    sportCourseArrayListFiltered.add(c);
                }
            }
        }
        if (sportCourseArrayListFiltered.isEmpty()){
            sportCourseArrayListFiltered = tmpArrayList;
        }

        prepareListDataHeaderSportCourse(sportCourseArrayListFiltered);
        prepareListDataChildrenSportCourse(sportCourseArrayListFiltered);
        changeExpandableListData("course");
    }

    //SportCourse
    private void prepareListDataHeaderSportCourse(ArrayList<SportCourse> filteredList) {
        listDataHeaderSportCourse = new ArrayList<>();

        //Adding header data
        for(SportCourse c : filteredList) {
            //Add the contact's types to the ArrayList (without duplicates)
            if(!listDataHeaderSportCourse.contains(c.getType())){
                listDataHeaderSportCourse.add(c.getType());
            }
        }
    }

    private void prepareListDataChildrenSportCourse(ArrayList<SportCourse> filteredList) {
        listDataChildSportCourse = new HashMap<>();
        List<SportCourse> childElements;

        int counter = 0;

        //Adding child data
        for(String s : listDataHeaderSportCourse) {
            //ArrayList containing the child elements
            childElements = new ArrayList<>();

            //Add all the elements belonging to this type to the arrayList
            for(SportCourse c : filteredList) {
                //Is the contact's type equal to the type we want to add elements to?
                if(c.getType().equals(s)){
                    //Add the element to the arrayList containing the child elements
                    childElements.add(c);
                }
            }

            //May the location be used and is there a location?
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && main.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && main.mLastLocation != null) {
                Log.d("childElements", "distances are calculated");
                ArrayList<SportCourse> childElementsSorted = getSortedKeysSportCourse(computeDistancesSportCourse(main.mLastLocation.getLatitude(), main.mLastLocation.getLongitude(), childElements));
                listDataChildSportCourse.put(listDataHeaderSportCourse.get(counter), childElementsSorted);
                counter++;
            } else {
                Log.d("childElements", "distances are not calculated");
                listDataChildSportCourse.put(listDataHeaderSportCourse.get(counter), childElements);
                counter++;
            }
        }
    }

    //Favorites
    public void prepareListDataFavorites() {
        listDataHeaderFavorite = new ArrayList<>();
        listDataChildFavorite = new HashMap<>();

        childElementsSportCourse = new ArrayList<>();
        childElementsBonusCard = new ArrayList<>();
        childElementsDoctor = new ArrayList<>();
        childElementsContact = new ArrayList<>();

        //Iterating the three ArrayLists adding objects to the favoritesArrayList
        //SPORT COURSES
        for(SportCourse s : sportCourseArrayList) {
            if (s.isFavorit()) {
                if(!listDataHeaderFavorite.contains("Sport Courses")) {
                    listDataHeaderFavorite.add("Sport Courses");
                }

                //Add element to the specific ArrayList
                childElementsSportCourse.add(s);
            }
        }
        if(childElementsSportCourse != null) {
            listDataChildFavorite.put("Sport Courses", childElementsSportCourse);
        }

        //BONUS CARD
        for(BonusCard b : bonusCardArrayList) {
            if (b.isFavorit()) {
                if(!listDataHeaderFavorite.contains("Bonus-/Family-Card")) {
                    listDataHeaderFavorite.add("Bonus-/Family-Card");
                }

                //Add element to the specific ArrayList
                childElementsBonusCard.add(b);
            }
        }
        if(childElementsBonusCard != null) {
            listDataChildFavorite.put("Bonus-/Family-Card", childElementsBonusCard);
        }

        //DOCTORS
        for(Doctor d : doctorArrayList) {
            if (d.isFavorit()) {
                if(!listDataHeaderFavorite.contains("Doctors")) {
                    listDataHeaderFavorite.add("Doctors");
                }
                //Add element to the specific ArrayList
                childElementsDoctor.add(d);
            }
        }
        if(childElementsDoctor != null) {
            listDataChildFavorite.put("Doctors", childElementsDoctor);
        }

        //CONTACTS
        for(Contact c : contactArrayList) {
            if (c.isFavorit()) {
                if(!listDataHeaderFavorite.contains("Contacts")) {
                    listDataHeaderFavorite.add("Contacts");
                }
                //Add element to the specific ArrayList
                childElementsContact.add(c);
            }
        }
        if(childElementsContact != null) {
            listDataChildFavorite.put("Contacts", childElementsContact);
        }
    }

    /**
     * Computes the distance from the user's current location to the toilets
     * <p/>
     * - parameters:
     * - ownLocationLatitude: the user's current location's latitude
     * - ownLocationLongitude: the user's current location's longitude
     * <p/>
     * - returns: A map with the toilets' names and the corresponding distances to the user's current location
     */

    public HashMap computeDistancesDoctor(Double ownLocationLatitude, Double ownLocationLongitude, List<Doctor> list) {
        float[] results;

        //Variable used to only store the doctors of the currently needed type
        HashMap<Doctor, Integer> tmp = new HashMap<>();

        for(Doctor d : list) {
            //The distance from the current position to the toilet is stored in this array
            results = new float[1];
            LatLng location = getLocationFromAddress(context, d.getAddress());
            //Method computing the distance
            Location.distanceBetween(ownLocationLatitude, ownLocationLongitude, location.latitude, location.longitude, results);
            //The distance is added to the HashMap
            namesDistancesDoctors.put(d, Math.round(results[0]));
            tmp.put(d, Math.round(results[0]));
        }

        return tmp;

    }

    /**
     * Computes the distance from the user's current location to the toilets
     * <p/>
     * - parameters:
     * - ownLocationLatitude: the user's current location's latitude
     * - ownLocationLongitude: the user's current location's longitude
     * <p/>
     * - returns: A map with the toilets' names and the corresponding distances to the user's current location
     */

    public HashMap computeDistancesBonusCard(Double ownLocationLatitude, Double ownLocationLongitude, List<BonusCard> list) {
        float[] results;

        //Variable used to only store the doctors of the currently needed type
        HashMap<BonusCard, Integer> tmp = new HashMap<>();

        for(BonusCard bonusCard : list) {
            //The distance from the current position to the toilet is stored in this array
            results = new float[1];
            LatLng location = getLocationFromAddress(context, bonusCard.getAddress());
            //Method computing the distance
            Location.distanceBetween(ownLocationLatitude, ownLocationLongitude, location.latitude, location.longitude, results);
            //The distance is added to the HashMap
            namesDistancesBonusCard.put(bonusCard, Math.round(results[0]));
            tmp.put(bonusCard, Math.round(results[0]));
        }
        return tmp;

    }

    /**
     * Sorts the calculated distances from the user's current location to the toilets ascendingly
     * <p/>
     * - parameters:
     * - namesDistances: the map containing the toilets' names and the corresponding distances
     * <p/>
     * - returns: An ArrayList containing the toilets' names sorted after their distances to the user's current location in an ascending way
     */
    public ArrayList<BonusCard> getSortedKeysBonusCard(final HashMap<BonusCard, Integer> namesDistances) {
        ArrayList<BonusCard> sortedKeysBonusCard = new ArrayList<>(namesDistances.keySet());

        Collections.sort(sortedKeysBonusCard, new Comparator<BonusCard>() {
            @Override
            public int compare(BonusCard bonusCard1, BonusCard bonusCard2) {
                return namesDistances.get(bonusCard1).compareTo(namesDistances.get(bonusCard2));
            }
        });

        for(BonusCard d : sortedKeysBonusCard) {
            Log.d("SortedDoctors", d.getName());
        }

        return sortedKeysBonusCard;
    }

    /**
     * Computes the distance from the user's current location to the toilets
     * <p/>
     * - parameters:
     * - ownLocationLatitude: the user's current location's latitude
     * - ownLocationLongitude: the user's current location's longitude
     * <p/>
     * - returns: A map with the toilets' names and the corresponding distances to the user's current location
     */

    public HashMap computeDistancesSportCourse(Double ownLocationLatitude, Double ownLocationLongitude, List<SportCourse> list) {
        float[] results;

        //Variable used to only store the doctors of the currently needed type
        HashMap<SportCourse, Integer> tmp = new HashMap<>();

        for(SportCourse s : list) {
                //The distance from the current position to the toilet is stored in this array
                results = new float[1];
                LatLng location = getLocationFromAddress(context, s.getAddress());
                //Method computing the distance
                Location.distanceBetween(ownLocationLatitude, ownLocationLongitude, location.latitude, location.longitude, results);
                //The distance is added to the HashMap
                namesDistancesSportsCourses.put(s, Math.round(results[0]));
                tmp.put(s, Math.round(results[0]));
            }
        return tmp;
    }

    /**
     * Sorts the calculated distances from the user's current location to the toilets ascendingly
     * <p/>
     * - parameters:
     * - namesDistances: the map containing the toilets' names and the corresponding distances
     * <p/>
     * - returns: An ArrayList containing the toilets' names sorted after their distances to the user's current location in an ascending way
     */
    public ArrayList<Doctor> getSortedKeysDoctor(final HashMap<Doctor, Integer> namesDistances) {
        ArrayList<Doctor> sortedKeysDoctor = new ArrayList<>(namesDistances.keySet());

        for(Doctor d : sortedKeysDoctor) {
            Log.d("neuesArray", d.getName());
        }

        Collections.sort(sortedKeysDoctor, new Comparator<Doctor>() {
            @Override
            public int compare(Doctor doctor1, Doctor doctor2) {
                return namesDistances.get(doctor1).compareTo(namesDistances.get(doctor2));
            }
        });

        for(Doctor d : sortedKeysDoctor) {
            Log.d("SortedDoctors", d.getName());
        }

        return sortedKeysDoctor;
    }

    /**
     * Sorts the calculated distances from the user's current location to the toilets ascendingly
     * <p/>
     * - parameters:
     * - namesDistances: the map containing the toilets' names and the corresponding distances
     * <p/>
     * - returns: An ArrayList containing the toilets' names sorted after their distances to the user's current location in an ascending way
     */
    public ArrayList<SportCourse> getSortedKeysSportCourse(final HashMap<SportCourse, Integer> namesDistances) {
        ArrayList<SportCourse> sortedKeysSportCourses = new ArrayList(namesDistances.keySet());

        Collections.sort(sortedKeysSportCourses, new Comparator<SportCourse>() {
            @Override
            public int compare(SportCourse sportCourse1, SportCourse sportCourse2) {
                return namesDistances.get(sportCourse1).compareTo(namesDistances.get(sportCourse2));
            }
        });
        return sortedKeysSportCourses;
    }

    //Get Contact to given id
    public Contact getContactToID(int id){
        for ( Contact contact : contactArrayList) {
            if(id == contact.getId()){
                return contact;
            }
        }
        return null;
    }

    //Get Doctor to given id
    public Doctor getDoctorToID(int id){
        for ( Doctor doctor : doctorArrayList) {
            if(id == doctor.getId()){
                return doctor;
            }
        }
        return null;
    }

    //Get Course to given id
    public SportCourse getCourseToID(int id){
        for ( SportCourse course : sportCourseArrayList) {
            if(id == course.getId()){
                return course;
            }
        }
        return null;
    }

    //Choose the right Fragment-Type
    public void getDetailTypeToSetAnnotation(String type, int id, Context context){
        if(type.equals("course")){
            for ( SportCourse course : sportCourseArrayList) {
                if(id == course.getId()){
                    //setDetailAnnotation(course.getType(), course.getAddress(), context);
                }
            }
        } else if(type.equals("medical")){
            for ( Doctor doctor : doctorArrayList) {
                if(id == doctor.getId()){
                    //setDetailAnnotation(doctor.getName(), doctor.getAddress(), context);
                }
            }
        }
    }

    //Annotation wird an Adresse der Detail-View Location gesetzt
    public void setDetailAnnotation(String name, String address, Context context, GoogleMap map) {
        LatLng location = getLocationFromAddress(context, address);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(name);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        map.addMarker(markerOptions);
    }


    //Annotation wird an Adresse der Detail-View Location gesetzt
    public void setBonusAnnotations(Context context, final ArrayList<BonusCard> bonus, GoogleMap mGoogleMap, View view) {
        final View viewBonus = view;
        this.mGoogleMap = mGoogleMap;
        for (final BonusCard b : bonus) {

            LatLng address = getLocationFromAddress(context, b.getAddress());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(address);
            markerOptions.title(b.getName());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
            mGoogleMap.addMarker(markerOptions);
        }
    }


    //Adressen werden in Latitude Longitude umgewandelt
    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> address;
        LatLng latLng;

        try {
            address = geocoder.getFromLocationName(strAddress, 5);

            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            latLng = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception e) {
            latLng = new LatLng(48.776879, 9.181475);
        }
        return latLng;
    }

    public void showMapMiddle(LatLng location, GoogleMap mGoogleMap) {
        LatLng loc = new LatLng(location.latitude,location.longitude);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                loc, 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(loc)      // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    public void saveVersionNumber(){

        FileOutputStream fileOutputStream = null;
        try{
            fileOutputStream = context.openFileOutput(VERSIONNUMBER_JSON_FILE, Context.MODE_PRIVATE);

            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(versionNumber);

            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
            Log.d("saving", versionNumber.toString());

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void saveAllData(){
        saveSportCourses();
        saveDoctors();
        saveContacts();
        saveBonusCards();
    }

    public void saveSportCourses(){
        FileOutputStream fileOutputStream = null;
        try{
            fileOutputStream = context.openFileOutput(SPORTCOURSE_JSON_FILE, Context.MODE_PRIVATE);

            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(sportCourseArrayList);

            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
            Log.d("saving", sportCourseArrayList.toString());


        } catch (IOException e){
            e.printStackTrace();
        }


    }

    public void saveContacts(){
        FileOutputStream fileOutputStream = null;
        try{
            fileOutputStream = context.openFileOutput(CONTACT_JSON_FILE, Context.MODE_PRIVATE);

            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(contactArrayList);

            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
            Log.d("saving", contactArrayList.toString());


        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void saveDoctors(){
        FileOutputStream fileOutputStream = null;
        try{
            fileOutputStream = context.openFileOutput(DOCTOR_JSON_FILE, Context.MODE_PRIVATE);

            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(doctorArrayList);

            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
            Log.d("saving", doctorArrayList.toString());


        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public void saveBonusCards(){
        FileOutputStream fileOutputStream = null;
        try{
            fileOutputStream = context.openFileOutput(BONUSCARD_JSON_FILE, Context.MODE_PRIVATE);

            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(bonusCardArrayList);

            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
            Log.d("saving", bonusCardArrayList.toString());


        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public void getSystemLanguage(){
        String systemLang = Locale.getDefault().getLanguage();
        switch (systemLang){
            case "de": systemLanguage = "DE";
                break;
            case "en": systemLanguage = "EN";
                break;
            default: systemLanguage = "EN";
        }
        Log.d("lang", systemLanguage);
        Log.d("lang", systemLang);

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) Model.model.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    public boolean doVersionControll(MainActivity acty, Context conty){
        this.context = conty;
        this.main = acty;
        getSystemLanguage();
        loadVersionnumber();
        boolean network = isNetworkAvailable();

        if(!(network) && versionNumber == null){
            Log.d("vers", "keine internetverbindung");
            return false;
        }
        else if(!(network) && versionNumber != null){
            Log.d("vers", "Die Daten werden vom Gerät geladen");
            loadStoredData();
        }
        else if(network){
            Log.d("vers", "Wir haben internet und die Versionsnummer wird abgerufen.");

            AsyncVersionFetch asy = new AsyncVersionFetch(acty, conty);
            asy.execute();
        }
        return true;
    }

    public void checkVersionNumber() {
        if (versionNumber == null) {
            versionNumber = newVersionNumber;
            saveVersionNumber();

            Log.d("load", "versionNumber is null and AsyncDataFetch is called");

            AsyncDataFetch asyJunior = new AsyncDataFetch(context);
            asyJunior.execute();
        }
        else if(versionNumber.getVersionNumber().equals(newVersionNumber.getVersionNumber())){
            loadStoredData();
            loadFavorites();
            isntFirstPull = true;
            Log.d("load", sportCourseArrayList.toString());
            Log.d("load", contactArrayList.toString());
            Log.d("load", doctorArrayList.toString());
            Log.d("load", bonusCardArrayList.toString());
        } else if (!(versionNumber.getVersionNumber().equals(newVersionNumber.getVersionNumber()))){
            versionNumber.setVersionNumber(newVersionNumber.getVersionNumber());
            isntFirstPull = true;
            saveVersionNumber();
            loadFavorites();
            AsyncDataFetch asyJunior = new AsyncDataFetch(context);
            asyJunior.execute();
        }
    }

    public void loadVersionnumber() {
        Log.d("load", "loadVersionnumber is called");
        try {
            Log.d("load", "loadVersionnumber is called");
            BufferedReader in = new BufferedReader(new InputStreamReader(context.openFileInput(VERSIONNUMBER_JSON_FILE)));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<VersionControll>>() {}.getType();
            VersionControll arry = (VersionControll)(gson.fromJson(builder.toString(), VersionControll.class));
            versionNumber = arry;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("FileNotFound", "Es gibt noch keine VersionNumber und deshalb muss sie aus dem Internet kommen.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadStoredData(){
        Log.d("load", "loadStoredData was called");
        loadSportCourses();
        loadContacts();
        loadDoctors();
        loadBonusCard();
        initialiseListHeaders();
    }

    public void loadSportCourses(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(context.openFileInput(SPORTCOURSE_JSON_FILE)));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<SportCourse>>() {}.getType();
            sportCourseArrayList = gson.fromJson(builder.toString(), listType);
            Log.d("load", "Sportcourse has been loaded from storage");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadContacts(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(context.openFileInput(CONTACT_JSON_FILE)));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Contact>>() {}.getType();
            contactArrayList = gson.fromJson(builder.toString(), listType);
            Log.d("load", "Contact has been loaded from storage");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDoctors(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(context.openFileInput(DOCTOR_JSON_FILE)));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Doctor>>() {}.getType();
            doctorArrayList = gson.fromJson(builder.toString(), listType);
            Log.d("load", "Doctor has been loaded from storage");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadBonusCard(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(context.openFileInput(BONUSCARD_JSON_FILE)));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<BonusCard>>() {}.getType();
            bonusCardArrayList = gson.fromJson(builder.toString(), listType);
            Log.d("load", "BonusCard has been loaded from storage");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void checkIfLoadingIsFinished(){
        if(sportCourseFetched && contactFetched && doctorFetched && bonusCardFetched){
           // main.initialiseAfterLoading();
            Log.d("load", "Sportcourse: " + sportCourseArrayList.toString() +"\n" +
                    "Contact: " + contactArrayList.toString() +"\n" +
                    "Doctor: " + doctorArrayList.toString() +"\n" +
                    "BonusCard: " + bonusCardArrayList.toString());

            if(isntFirstPull){
                setFavoritesInArrayLists();
            }
            //initialising listheaders after everything has been loaded
            initialiseListHeaders();
        }
    }

    public float roundFloat(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    //-------------------- Methods to put/load/delete favorites --------------------
    public void loadFavorites(){
        loadSportCourseFavorite();
        loadDoctorFavorite();
        loadContactFavorite();
        loadBonusCardFavorite();
    }

    public void loadSportCourseFavorite(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(context.openFileInput(SPORTCOURSE_FAVORITE_JSON_FILE)));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<HashSet<Integer>>() {}.getType();
            sportCourseFavorite = gson.fromJson(builder.toString(), listType);
            Log.d("load", "Sportcourse favorite has been loaded from storage");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDoctorFavorite(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(context.openFileInput(DOCTOR_FAVORITE_JSON_FILE)));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<HashSet<Integer>>() {}.getType();
            doctorFavorite = gson.fromJson(builder.toString(), listType);
            Log.d("load", "Doctor favorite has been loaded from storage");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadContactFavorite(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(context.openFileInput(CONTACT_FAVORITE_JSON_FILE)));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<HashSet<Integer>>() {}.getType();
            contactFavorite = gson.fromJson(builder.toString(), listType);
            Log.d("load", "Contact favorite has been loaded from storage");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadBonusCardFavorite(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(context.openFileInput(BONUSCARD_FAVORITE_JSON_FILE)));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<HashSet<Integer>>() {}.getType();
            bonusCardFavorite = gson.fromJson(builder.toString(), listType);
            Log.d("load", "Sportcourse favorite has been loaded from storage");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFavorites(){
        saveSportCourseFavorite();
        saveDoctorFavorite();
        saveContactFavorite();
        saveBonusCardFavorite();
    }

    public void saveSportCourseFavorite (){
        FileOutputStream fileOutputStream = null;
        try{
            fileOutputStream = context.openFileOutput(SPORTCOURSE_FAVORITE_JSON_FILE, Context.MODE_PRIVATE);

            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(sportCourseFavorite);

            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
            Log.d("saving", sportCourseFavorite.toString());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void saveBonusCardFavorite (){
        FileOutputStream fileOutputStream = null;
        try{
            fileOutputStream = context.openFileOutput(BONUSCARD_FAVORITE_JSON_FILE, Context.MODE_PRIVATE);

            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(bonusCardFavorite);

            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
            Log.d("saving", bonusCardFavorite.toString());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void saveDoctorFavorite (){
        FileOutputStream fileOutputStream = null;
        try{
            fileOutputStream = context.openFileOutput(DOCTOR_FAVORITE_JSON_FILE, Context.MODE_PRIVATE);

            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(doctorFavorite);

            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
            Log.d("saving", doctorFavorite.toString());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void saveContactFavorite (){
        FileOutputStream fileOutputStream = null;
        try{
            fileOutputStream = context.openFileOutput(CONTACT_FAVORITE_JSON_FILE, Context.MODE_PRIVATE);

            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(contactFavorite);

            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
            Log.d("saving", contactFavorite.toString());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void addToSportCourseFavorite (int id){
        sportCourseFavorite.add(id);
    }

    public void addToDoctorFavorite (int id){
        doctorFavorite.add(id);
    }

    public void addToContactFavorite (int id){
        contactFavorite.add(id);
    }

    public void addToBonusCardFavorite (int id){
        bonusCardFavorite.add(id);
    }

    public void deleteFromSportcourseFavorite (int id){
        if(sportCourseFavorite.contains(id)) {
            sportCourseFavorite.remove(id);
        }
    }

    public void deleteFromDoctorFavorite (int id){
        if(doctorFavorite.contains(id)) {
            doctorFavorite.remove(id);
        }
    }

    public void deleteFromContactFavorite (int id){
        if(contactFavorite.contains(id)) {
            contactFavorite.remove(id);
        }
    }

    public void deleteFromBonusCardFavorite (int id){
        if(bonusCardFavorite.contains(id)) {
            bonusCardFavorite.remove(id);
        }
    }

    public void setFavoritesInArrayLists (){
        setSportCourseFavoriteInArrayList();
        setDoctorFavoriteInArrayList();
        setContactFavoriteInArrayList();
        setBonusCardFavoriteInArrayList();
    }

    public void setSportCourseFavoriteInArrayList (){
        for(Integer inti : sportCourseFavorite){
            for(SportCourse eachy : sportCourseArrayList) {
                if (eachy.getId() == inti){
                    eachy.setFavorit(true);
                    break;
                }
            }
        }
    }

    public void setDoctorFavoriteInArrayList (){
        for(Integer inti : doctorFavorite){
            for(Doctor eachy : doctorArrayList) {
                if (eachy.getId() == inti){
                    eachy.setFavorit(true);
                    break;
                }
            }
        }
    }

    public void setContactFavoriteInArrayList (){
        for(Integer inti : contactFavorite){
            for(Contact eachy : contactArrayList) {
                if (eachy.getId() == inti){
                    eachy.setFavorit(true);
                    break;
                }
            }
        }
    }

    public void setBonusCardFavoriteInArrayList (){
        for(Integer inti : bonusCardFavorite){
            for(BonusCard eachy : bonusCardArrayList) {
                if (eachy.getId() == inti){
                    eachy.setFavorit(true);
                    break;
                }
            }
        }
    }

}
