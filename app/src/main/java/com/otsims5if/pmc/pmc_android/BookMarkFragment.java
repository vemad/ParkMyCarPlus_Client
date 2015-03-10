package com.otsims5if.pmc.pmc_android;

import android.annotation.TargetApi;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.otsims5if.pmc.pmc_android.design.StableArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iler on 18/02/2015.
 */
public class BookMarkFragment extends PlaceholderFragment{


    private Button findAdressButton;
    private EditText addressEditText;
    private ListView addressFaaListView;
    private ArrayList<String> favList = new ArrayList<String>();
    private StableArrayAdapter adapter;
    private ArrayAdapter<String> arrAdapt;

    public BookMarkFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bookmark, container, false);
        findAdressButton = (Button) rootView.findViewById(R.id.findAdress);
        addressEditText = (EditText) rootView.findViewById(R.id.adressEditText);

        addressFaaListView = (ListView) rootView.findViewById(R.id.adressFavListView);
        adapter = new StableArrayAdapter(this.getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, favList);
        adapter.setNotifyOnChange(true);
        addressFaaListView.setAdapter(adapter);

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
            }

        });

        findAdressButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LatLng position = getLocationFromAddress(addressEditText.getText().toString());
                if(position!=null) {
                    String newEntry = addressEditText.getText().toString() + " - (" + position.latitude + " | " + position.longitude + " )";
                    adapter.add(newEntry);
                    adapter.notifyDataSetChanged();
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
}
