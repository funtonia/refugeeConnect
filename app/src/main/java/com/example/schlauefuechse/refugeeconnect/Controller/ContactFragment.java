package com.example.schlauefuechse.refugeeconnect.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.Contact;
import com.example.schlauefuechse.refugeeconnect.Model.ContactsListAdapter;
import com.example.schlauefuechse.refugeeconnect.Model.Model;
import com.example.schlauefuechse.refugeeconnect.R;

public class ContactFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_contact, null);

        final ListView listView = (ListView) v.findViewById(R.id.contactList);

        ContactsListAdapter contactsAdapter = new ContactsListAdapter(getContext(), Model.model.contactArrayList);

        listView.setAdapter(contactsAdapter);

        listView.setItemsCanFocus(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Boolean isExpanded = false;
            private View pressedView = null;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                final ImageButton contactFavoriteBtn;
                final ImageButton contactMailBtn;
                final ImageView arrow = (ImageView) view.findViewById(R.id.arrow);

                final Contact contact = (Contact) listView.getItemAtPosition(position);

                LinearLayout rl_inflate;

                if(!isExpanded){
                    rl_inflate = (LinearLayout)view.findViewById(R.id.contact_rl_inflate);
                    View child = getLayoutInflater(Bundle.EMPTY).inflate(R.layout.contact_inflate, null);
                    rl_inflate.addView(child);

                    arrow.setImageResource(R.drawable.arrow_collapse);

                    contactFavoriteBtn = (ImageButton) view.findViewById(R.id.contact_favoriteBT);
                    contactMailBtn = (ImageButton) view.findViewById(R.id.contact_mailBT);

                    contactMailBtn.setOnClickListener( new View.OnClickListener() {

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

                    if(contact.isFavorit()){
                        contactFavoriteBtn.setImageResource(R.drawable.favorite_red);
                    }
                    else{
                        contactFavoriteBtn.setImageResource(R.drawable.favorite_grey);
                    }

                    contactFavoriteBtn.setOnClickListener( new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if(contact.isFavorit()){
                                contact.setFavorit(false);
                                Model.model.deleteFromContactFavorite(contact.getId());
                                contactFavoriteBtn.setImageResource(R.drawable.favorite_grey);
                            }
                            else{
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
                    arrow.setImageResource(R.drawable.arrow_expand);

                    rl_inflate = (LinearLayout)view.findViewById(R.id.contact_rl_inflate);
                    rl_inflate.removeAllViews();
                    isExpanded = false;
                }


                pressedView = view;
            }

        });

        return v;
    }
}
