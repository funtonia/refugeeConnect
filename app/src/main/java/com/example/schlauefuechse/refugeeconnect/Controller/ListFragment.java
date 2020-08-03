package com.example.schlauefuechse.refugeeconnect.Controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.BonusCard;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.Contact;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.Doctor;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.SportCourse;
import com.example.schlauefuechse.refugeeconnect.Model.ExpandableListAdapter;
import com.example.schlauefuechse.refugeeconnect.Model.Model;
import com.example.schlauefuechse.refugeeconnect.R;

/*
* In this class, the list data is made that is displayed in the differents lists (doctors, courses, favorites etc.)
* The data is only computed once, but has to be computed again whenever the version is changed.
* There are four different lists for the four different list types. They are filled when required, not on the first start of the app.
*
* */

/*
* Logik: die Listen werden alle im Model initialisiert, wenn auch die Daten aus den jsons gezogen werden. Der ListAdapter wird auf eine Liste gesetzt, diese wird je nachdem mit
* den jeweiligen Listen (Doktor, Sportkurs etc) befüllt.
* */
public class ListFragment extends Fragment implements Animation.AnimationListener {

    Boolean isExpanded = false;

    ExpandableListView expListView;

    //Filter Button
    Button filterBtn;

    private OnListRowSelectedListener mCallback;

    private View v;

    private String type;

    public static ExpandableListAdapter listAdapter;

    // Container Activity must implement this interface
    interface OnListRowSelectedListener {
        void onRowSelected(String type, int id, String titleType);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnListRowSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnListRowSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        listAdapter = new ExpandableListAdapter(getContext(), Model.model.listDataHeader, Model.model.listDataChild);
        Log.d("ListFragment", "called");
        v = inflater.inflate(R.layout.exp_fragment_list, null);

        expListView = (ExpandableListView) v.findViewById(R.id.expList);
        expListView.setEmptyView(v.findViewById(R.id.empty_list_item));

        expListView.setAdapter(listAdapter);

        filterBtn = (Button) v.findViewById(R.id.filterBtn);

        type = getArguments().getString("type");

        // Überprüft, ob der Benutzer in der Favoriten-Activity ist und von dort aus in die Detail-
        // Activity navigiert. Ist dies der Fall sollte er durch einen Klick auf den Back-Button
        // wieder in die Favoriten-Activity zurück gelangen.
        if(MainActivity.backToFavoriteActivity) {
            type = "favorite";
        }

        switch (type) {
            case "medical":
                    Log.d("ListFragment", "Doctor");
                    Model.model.changeExpandableListData(type);

                    listAdapter.notifyDataSetChanged();

                    break;
            case "favorite":

                    Model.model.prepareListDataFavorites();

                    Log.d("ListFragment", "Favorites");
                    Model.model.changeExpandableListData(type);

                    listAdapter.notifyDataSetChanged();
                    //Set Button-height = 0 because the filter-option isn't required
                    android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                    filterBtn.setLayoutParams(lp);

                    View filterBtnSeperator = v.findViewById(R.id.filterBtnSeperator);
                    android.widget.LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                    filterBtnSeperator.setLayoutParams(lp2);

                    break;
            case "course":
                    Log.d("ListFragment", "Doctor");
                    Model.model.changeExpandableListData(type);

                    listAdapter.notifyDataSetChanged();
                    break;

        }

        expListView.setItemsCanFocus(true);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                    switch (type) {
                        case "course": {
                            SportCourse listItem = Model.model.listDataChildSportCourse.get(
                                    Model.model.listDataHeaderSportCourse.get(groupPosition)).get(
                                    childPosition);
                            mCallback.onRowSelected("course", listItem.getId(), listItem.getType());
                            break;
                        }
                        case "medical": {
                            Doctor listItem = Model.model.listDataChildDoctor.get(
                                    Model.model.listDataHeaderDoctor.get(groupPosition)).get(
                                    childPosition);
                            mCallback.onRowSelected("medical", listItem.getId(), listItem.getType());
                            break;
                        }
                        case "favorite":
                            if (Model.model.listDataChildFavorite.get(
                                    Model.model.listDataHeaderFavorite.get(groupPosition)).get(
                                    childPosition) instanceof Doctor) {
                                Doctor listItem = (Doctor) Model.model.listDataChildFavorite.get(
                                        Model.model.listDataHeaderFavorite.get(groupPosition)).get(
                                        childPosition);
                                mCallback.onRowSelected("medical", listItem.getId(), listItem.getType());
                            } else if (Model.model.listDataChildFavorite.get(
                                    Model.model.listDataHeaderFavorite.get(groupPosition)).get(
                                    childPosition) instanceof Contact) {
                                final ImageView arrow = (ImageView) v.findViewById(R.id.arrow);

                                final Contact contact = (Contact) Model.model.listDataChildFavorite.get(
                                        Model.model.listDataHeaderFavorite.get(groupPosition)).get(
                                        childPosition);

                                final ImageButton contactFavoriteBtn;
                                final ImageButton contactMailBtn;

                                LinearLayout rl_inflate;

                                if (!isExpanded) {
                                    rl_inflate = (LinearLayout) v.findViewById(R.id.exp_rl_inflate);
                                    View child = getLayoutInflater(Bundle.EMPTY).inflate(R.layout.contact_inflate, null);
                                    rl_inflate.addView(child);

                                    arrow.setImageResource(R.drawable.arrow_collapse);

                                    contactFavoriteBtn = (ImageButton) v.findViewById(R.id.contact_favoriteBT);
                                    contactMailBtn = (ImageButton) v.findViewById(R.id.contact_mailBT);

                                    contactMailBtn.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            intent.setType("plain/text");
                                            intent.putExtra(Intent.EXTRA_EMAIL, contact.getMail());
                                            intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                                            intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                                            startActivity(Intent.createChooser(intent, ""));
                                        }
                                    });

                                    if (contact.isFavorit()) {
                                        contactFavoriteBtn.setImageResource(R.drawable.favorite_red);
                                    } else {
                                        contactFavoriteBtn.setImageResource(R.drawable.favorite_grey);
                                    }

                                    contactFavoriteBtn.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            if (contact.isFavorit()) {
                                                contact.setFavorit(false);
                                                Model.model.deleteFromContactFavorite(contact.getId());
                                                contactFavoriteBtn.setImageResource(R.drawable.favorite_grey);
                                            } else {
                                                contact.setFavorit(true);
                                                contactFavoriteBtn.setImageResource(R.drawable.favorite_red);
                                                Model.model.addToContactFavorite(contact.getId());
                                            }
                                        }
                                    });

                                    contactFavoriteBtn.setFocusable(false);
                                    contactFavoriteBtn.setFocusableInTouchMode(false);
                                    contactFavoriteBtn.setClickable(true);
                                    contactMailBtn.setFocusable(false);
                                    contactMailBtn.setFocusableInTouchMode(false);
                                    contactMailBtn.setClickable(true);

                                    isExpanded = true;
                                } else {
                                    rl_inflate = (LinearLayout) v.findViewById(R.id.exp_rl_inflate);
                                    rl_inflate.removeAllViews();
                                    arrow.setImageResource(R.drawable.arrow_expand);
                                    isExpanded = false;
                                }
                            } else if (Model.model.listDataChildFavorite.get(
                                    Model.model.listDataHeaderFavorite.get(groupPosition)).get(
                                    childPosition) instanceof SportCourse) {
                                SportCourse listItem = (SportCourse) Model.model.listDataChildFavorite.get(
                                        Model.model.listDataHeaderFavorite.get(groupPosition)).get(
                                        childPosition);
                                mCallback.onRowSelected("course", listItem.getId(), listItem.getType());
                            } else if (Model.model.listDataChildFavorite.get(
                                    Model.model.listDataHeaderFavorite.get(groupPosition)).get(
                                    childPosition) instanceof BonusCard) {
                                BonusCard listItem = (BonusCard) Model.model.listDataChildFavorite.get(
                                        Model.model.listDataHeaderFavorite.get(groupPosition)).get(
                                        childPosition);
                                mCallback.onRowSelected("bonusCard", listItem.getId(), getString(R.string.bonusfamilycard));
                            }
                            break;
                    }
                    return false;

            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(type.equals("medical")){
                    displayPopupWindowDoctor(filterBtn);
                } else if(type.equals("course")){
                    displayPopupWindowCourse(filterBtn);
                }
            }
        });


        return v;

    }

    // Display popup window to filter the Doctors
    private void displayPopupWindowDoctor(View anchorView) {
        View layoutPopupWindow;
        layoutPopupWindow = getActivity().getLayoutInflater().inflate(R.layout.popup_filter_doctor, null);
        final PopupWindow popup = new PopupWindow(layoutPopupWindow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        popup.setBackgroundDrawable(new BitmapDrawable());


        final RadioButton femaleRadio = (RadioButton) layoutPopupWindow.findViewById(R.id.d_female_radio);
        femaleRadio.setChecked(Model.model.d_female);

        final RadioButton maleRadio = (RadioButton) layoutPopupWindow.findViewById(R.id.d_male_radio);
        maleRadio.setChecked(Model.model.d_male);

        final RadioButton malefemaleRadio = (RadioButton) layoutPopupWindow.findViewById(R.id.d_mixed_radio);
        malefemaleRadio.setChecked(Model.model.d_malefemale);

        final CheckBox ar =(CheckBox) layoutPopupWindow.findViewById(R.id.d_ar_checkbox);
        ar.setChecked(Model.model.d_ar);

        final CheckBox it =(CheckBox) layoutPopupWindow.findViewById(R.id.d_it_checkbox);
        it.setChecked(Model.model.d_it);

        final CheckBox dr =(CheckBox) layoutPopupWindow.findViewById(R.id.d_dr_checkbox);
        dr.setChecked(Model.model.d_dr);

        final CheckBox fa =(CheckBox) layoutPopupWindow.findViewById(R.id.d_fa_checkbox);
        fa.setChecked(Model.model.d_fa);

        final CheckBox tr =(CheckBox) layoutPopupWindow.findViewById(R.id.d_tr_checkbox);
        tr.setChecked(Model.model.d_tr);

        final CheckBox fr =(CheckBox) layoutPopupWindow.findViewById(R.id.d_fr_checkbox);
        fr.setChecked(Model.model.d_fr);

        final CheckBox en =(CheckBox) layoutPopupWindow.findViewById(R.id.d_en_checkbox);
        en.setChecked(Model.model.d_en);

        final CheckBox sq =(CheckBox) layoutPopupWindow.findViewById(R.id.d_sq_checkbox);
        sq.setChecked(Model.model.d_sq);

        final CheckBox sh =(CheckBox) layoutPopupWindow.findViewById(R.id.d_sh_checkbox);
        sh.setChecked(Model.model.d_sh);

        final CheckBox el =(CheckBox) layoutPopupWindow.findViewById(R.id.d_el_checkbox);
        el.setChecked(Model.model.d_el);

        final CheckBox hr =(CheckBox) layoutPopupWindow.findViewById(R.id.d_hr_checkbox);
        hr.setChecked(Model.model.d_hr);

        final CheckBox sr =(CheckBox) layoutPopupWindow.findViewById(R.id.d_sr_checkbox);
        sr.setChecked(Model.model.d_sr);

        final CheckBox ru =(CheckBox) layoutPopupWindow.findViewById(R.id.d_ru_checkbox);
        ru.setChecked(Model.model.d_ru);

        final CheckBox ro =(CheckBox) layoutPopupWindow.findViewById(R.id.d_ro_checkbox);
        ro.setChecked(Model.model.d_ro);

        final CheckBox mz =(CheckBox) layoutPopupWindow.findViewById(R.id.d_mz_checkbox);
        mz.setChecked(Model.model.d_mz);

        final CheckBox bs =(CheckBox) layoutPopupWindow.findViewById(R.id.d_bs_checkbox);
        bs.setChecked(Model.model.d_bs);

        final CheckBox es =(CheckBox) layoutPopupWindow.findViewById(R.id.d_es_checkbox);
        es.setChecked(Model.model.d_es);

        Button applyFilterBtn = (Button) layoutPopupWindow.findViewById(R.id.d_apply_btn);
        applyFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Model.model.d_female = femaleRadio.isChecked();
                Model.model.d_male = maleRadio.isChecked();
                Model.model.d_malefemale = malefemaleRadio.isChecked();
                Model.model.d_ar = ar.isChecked();
                Model.model.d_it = it.isChecked();
                Model.model.d_dr = dr.isChecked();
                Model.model.d_fa = fa.isChecked();
                Model.model.d_tr = tr.isChecked();
                Model.model.d_fr = fr.isChecked();
                Model.model.d_en = en.isChecked();
                Model.model.d_sq = sq.isChecked();
                Model.model.d_sh = sh.isChecked();
                Model.model.d_el = el.isChecked();
                Model.model.d_hr = hr.isChecked();
                Model.model.d_sr = sr.isChecked();
                Model.model.d_ru = ru.isChecked();
                Model.model.d_ro = ro.isChecked();
                Model.model.d_mz = mz.isChecked();
                Model.model.d_bs = bs.isChecked();
                Model.model.d_es = es.isChecked();

                Model.model.prepareListDataDoctorFiltered();

                listAdapter.notifyDataSetChanged();
                popup.dismiss();
            }
        });
        popup.showAsDropDown(anchorView);
    }

    // Display popup window to filter the SportCourses
    private void displayPopupWindowCourse(View anchorView) {
        View layoutPopupWindow;
        layoutPopupWindow = getActivity().getLayoutInflater().inflate(R.layout.popup_filter_course, null);
        final PopupWindow popup = new PopupWindow(layoutPopupWindow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        popup.setBackgroundDrawable(new BitmapDrawable());

        final RadioButton femaleRadio = (RadioButton) layoutPopupWindow.findViewById(R.id.sc_female_radio);
        femaleRadio.setChecked(Model.model.sc_female);

        final RadioButton maleRadio = (RadioButton) layoutPopupWindow.findViewById(R.id.sc_male_radio);
        maleRadio.setChecked(Model.model.sc_male);

        final RadioButton malefemaleRadio = (RadioButton) layoutPopupWindow.findViewById(R.id.sc_mixed_radio);
        malefemaleRadio.setChecked(Model.model.sc_mixed);

        final CheckBox children =(CheckBox) layoutPopupWindow.findViewById(R.id.sc_chilren_checkbox);
        children.setChecked(Model.model.sc_children);

        final CheckBox adolescents =(CheckBox) layoutPopupWindow.findViewById(R.id.sc_adolescents_checkbox);
        adolescents.setChecked(Model.model.sc_adolescents);

        final CheckBox young_adults =(CheckBox) layoutPopupWindow.findViewById(R.id.sc_youngadults_checkbox);
        young_adults.setChecked(Model.model.sc_young_adults);

        final CheckBox adults =(CheckBox) layoutPopupWindow.findViewById(R.id.sc_adults_checkbox);
        adults.setChecked(Model.model.sc_adults);

        final CheckBox parents_child =(CheckBox) layoutPopupWindow.findViewById(R.id.sc_parents_with_children_checkbox);
        parents_child.setChecked(Model.model.sc_parents_child);

        Button applyFilterBtn = (Button) layoutPopupWindow.findViewById(R.id.sc_apply_btn);
        applyFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.model.sc_female = femaleRadio.isChecked();
                Model.model.sc_male = maleRadio.isChecked();
                Model.model.sc_mixed = malefemaleRadio.isChecked();
                Model.model.sc_children = children.isChecked();
                Model.model.sc_adolescents = adolescents.isChecked();
                Model.model.sc_young_adults = young_adults.isChecked();
                Model.model.sc_adults = adults.isChecked();
                Model.model.sc_parents_child = parents_child.isChecked();
                Model.model.prepareListDataSportCourseFiltered();

                listAdapter.notifyDataSetChanged();
                popup.dismiss();
            }
        });
        popup.showAsDropDown(anchorView);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}