package com.otsims5if.pmc.pmc_android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Iler on 18/02/2015.
 */
public class UserMapFragment extends PlaceholderFragment{

    MapView mapView;
    GoogleMap map;
    Button parkButton;
    Button leaveButton;
    Marker currentPositionMarker;

    LatLng laDouaGastonBerger = new LatLng(45.781543000000010000,4.872104000000036000);
    LatLng insa = new LatLng(45.7829609,4.875031300000046);
    LatLng doubleMixte = new LatLng(45.78053269999999,4.872770199999991);
    LatLng laPoste = new LatLng(43.4778166,5.168552200000022);

    public UserMapFragment() {
        super();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        // Get buttons by there id
        parkButton = (Button) v.findViewById(R.id.parkButton);
        leaveButton = (Button) v.findViewById(R.id.leaveButton);

        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(v.getContext());

        /*if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            s.setThumbTextPadding(100);
        }else{
            s.setThumbTextPadding(50);
        }*/
        //mapView.onResume();//needed to get the map to display immediately

        /*try {
            MapsInitializer.initialize(this);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }*/

        //map = mapView.getMap();
        setUpMapIfNeeded(v);

        //Set actions for leave and park buttons
        parkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                leaveButton.setVisibility(View.VISIBLE);
                parkButton.setVisibility(View.GONE);

                //Mark the position in order to park the car at the current position deliver by the GPS
                currentPositionMarker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()))
                        .title("Je me suis garer ici")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


            }
        });
        leaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                leaveButton.setVisibility(View.GONE);
                parkButton.setVisibility(View.VISIBLE);
                currentPositionMarker.remove();
            }
        });
        //Perform any camera updates here

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void setUpMapIfNeeded(View v) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            mapView = (MapView) v.findViewById(R.id.map);
            map = mapView.getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #map} is not null.
     */
    private void setUpMap() {

        CircleOptions circleOptions = new CircleOptions();

        Circle circle = map.addCircle(new CircleOptions()
                .center(doubleMixte)
                .radius(100)
                .fillColor(0x40ff0000)
                .strokeColor(Color.RED)
                .strokeWidth(2));

        map.addMarker(new MarkerOptions()
                            .position(laDouaGastonBerger)
                            .title("La Doua - Gaston Berger")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        map.addMarker(new MarkerOptions()
                .position(insa)
                .title("Insa Lyon")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        map.addMarker(new MarkerOptions()
                            .position(doubleMixte)
                            .title("Double Mixte")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        map.addMarker(new MarkerOptions()
                            .position(laPoste)
                            .title("La Poste")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        map.setMyLocationEnabled(true);
    }
}
