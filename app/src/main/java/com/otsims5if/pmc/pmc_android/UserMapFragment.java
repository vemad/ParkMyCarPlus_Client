package com.otsims5if.pmc.pmc_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;

/**
 * Created by Iler on 18/02/2015.
 */
public class UserMapFragment extends PlaceholderFragment{

    MapView mapView;
    GoogleMap map;

    public UserMapFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        return rootView;
    }
}
