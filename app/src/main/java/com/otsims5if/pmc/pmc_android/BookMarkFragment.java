package com.otsims5if.pmc.pmc_android;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.otsims5if.pmc.pmc_android.design.StableArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import api.Density;
import api.favorite.CreateFavoriteCallback;
import api.favorite.DeleteFavoriteCallback;
import api.favorite.Favorite;
import api.favorite.FavoriteServices;
import api.favorite.ListFavoritesCallback;

/**
 * Created by Iler on 18/02/2015.
 */
public class BookMarkFragment extends PlaceholderFragment{


    private Button findAdressButton;
    private EditText addressEditText;
    private ListView addressFaaListView;
    private ArrayList<Favorite> favList = new ArrayList<Favorite>();
    private StableArrayAdapter adapter;
    private ArrayAdapter<String> arrAdapt;
    private ViewPager viewPager;
    private View toastView;
    private View listView;
    Favorite favorite;

    DialogInterface.OnClickListener dialogClickListener;


    public BookMarkFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Just for the toast view
        toastView = inflater.inflate(R.layout.custom_toast, (ViewGroup) getActivity().findViewById(R.id.relativeLayout1));

        View rootView = inflater.inflate(R.layout.fragment_bookmark, container, false);
        findAdressButton = (Button) rootView.findViewById(R.id.findAdress);
        addressEditText = (EditText) rootView.findViewById(R.id.adressEditText);

        addressFaaListView = (ListView) rootView.findViewById(R.id.adressFavListView);
        adapter = new StableArrayAdapter(this.getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, favList);
        adapter.setNotifyOnChange(true);
        addressFaaListView.setAdapter(adapter);

        dialogClickListener = new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        listView.animate().setDuration(2000).alpha(0)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Trying to add a favorite to the user account
                                        try {
                                            FavoriteServices.getInstance().deleteFavorite(
                                                    favorite.getId(),
                                                    new DeleteFavorite()).execute();
                                            adapter.remove(favorite);
                                            adapter.notifyDataSetChanged();
                                            listView.setAlpha(1);
                                        }catch(Exception e){

                                        }

                                    }
                                });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        //Get favorite from User account
        try {
            FavoriteServices.getInstance().listFavorites(new ListFavoritesCallback() {
                @Override
                protected void callback(Exception e, Favorite[] listFavorites) {
                    if (listFavorites!=null) {
                        System.out.println("Il y a "+listFavorites.length+" favoris!!!");
                        for (Favorite fav : listFavorites) {
                            //favList.add(fav);
                            adapter.add(fav);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }).execute();
        }catch(Exception e){

        }
        addressFaaListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                favorite = adapter.getItemFromPosition(position);
                listView = view;
//                final String item = (String) parent.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Voulez vous supprimer le favoris :\n"+favorite.getAddress()).setPositiveButton("Oui", dialogClickListener)
                        .setNegativeButton("Non", dialogClickListener).show();


                return true;
            }
        });
        addressFaaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                /*final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                list.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });*/
                Favorite favorite = adapter.getItemFromPosition(position);
                ((MainUserActivity) getActivity()).mViewPager.setCurrentItem(0,true);
                UserMapFragment test = ((UserMapFragment)(((MainUserActivity) getActivity()).mSectionsPagerAdapter.getItem(0)));
                test.displayAndMoveToFavorite(favorite);

            }

        });

        findAdressButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LatLng position = getLocationFromAddress(addressEditText.getText().toString());
                if(position!=null) {
                    final Favorite[] newFavorite = new Favorite[1];
//                    newFavorite.setDensity(Density.LOW);
//                    adapter.add(newFavorite);
//                    adapter.notifyDataSetChanged();

                    //Trying to add a favorite to the user account
                    try {
                        FavoriteServices.getInstance().createFavorite(
                                position.latitude, position.longitude,
                                addressEditText.getText().toString(),
                                new CreateFavorite()).execute();
                    }catch(Exception e){

                    }

                    addressEditText.setText("");

                }
            }
        });

        return rootView;
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this.getActivity().getApplicationContext());
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng((location.getLatitude()),(location.getLongitude()));

        }catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }

        return p1;
    }

    /*Method for displaying a place received by a service*/
    private class CreateFavorite extends CreateFavoriteCallback {
        protected void callback(Exception e, Favorite favorite){

            if (favorite != null) {
                adapter.add(favorite);
                adapter.notifyDataSetChanged();
            }

            Toast toast = new Toast(getActivity().getApplicationContext());

            if(favorite != null){
                ((ImageView) toastView.findViewById(R.id.smileyImage)).setImageResource(R.drawable.happy);
                ((TextView) toastView.findViewById(R.id.toastTextView)).setText("Nouvelle adresse en favoris");
            }else{
                ((TextView) toastView.findViewById(R.id.toastTextView)).setText("Adresse non conforme !!!");
            }
            toast.setView(toastView);
            toast.show();
        }
    }

    private class DeleteFavorite extends DeleteFavoriteCallback {
        protected void callback(Exception e, String idFavorite){

            Toast toast = new Toast(getActivity().getApplicationContext());

            if(idFavorite != null){
                ((ImageView) toastView.findViewById(R.id.smileyImage)).setImageResource(R.drawable.happy);
                ((TextView) toastView.findViewById(R.id.toastTextView)).setText("Le favoris a été supprimé avec succès");
            }else{
                ((TextView) toastView.findViewById(R.id.toastTextView)).setText("La suppression du favoris a échouée !!!");
            }
            toast.setView(toastView);
            toast.show();
        }
    }


}
