package com.otsims5if.pmc.pmc_android;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import api.place.GetListPlacesByPositionCallback;
import api.place.Place;
import api.place.PlaceServices;

/**
 * Created by Iler on 18/02/2015.
 */
public class UserMapFragment extends PlaceholderFragment{

    MapView mapView;
    GoogleMap map;
    Button parkButton;
    Button leaveButton;
    Switch placeFindSwitch;
    Marker currentPositionMarker;
    LatLng myCurrentLocation;
    Thread checkPlacesThread;
    Handler handler = new Handler();
    boolean startThread = false;
    int radius = 20;
    Circle userRange;

    ArrayList<Marker> placeMarkerList = new ArrayList<Marker>();

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
        placeFindSwitch = (Switch) v.findViewById(R.id.placeFindSwitch);

        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(v.getContext());
        checkPlacesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (startThread) {
                    try {
                        Thread.sleep(10000);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub

                                //Show marker place that are available inside user's range
                                setUpMarkerListAndShowPlaces();
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        });

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
        placeFindSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    startThread = true;
                    checkPlacesThread.start();
                    //setUpMarkerList();
                } else {
                    // The toggle is disabled
                    //checkPlacesThread.interrupt();
                    startThread = false;
                }
            }
        });

        //Perform any camera updates here

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        try{
            myCurrentLocation = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
        }catch(NullPointerException e){

        }

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

    private void setUpMarkerListAndShowPlaces(){
        //PlaceServices.getInstance().getListPlacesByPosition(45.78166386726485, 4.872752178696828, 5, new ShowListPlacesCallback()).execute();
        Log.i("Info", "SetUpMarker");
        try {
            //double latitude = map.getMyLocation().getLatitude();
            //double longitude = map.getMyLocation().getLongitude();
            myCurrentLocation = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
            //Draw a circle that show the range of the user
            if(userRange!=null) {
                userRange.remove();
            }
            userRange = map.addCircle(new CircleOptions()
                    .center(myCurrentLocation)
                    .radius(radius)
                    .fillColor(0x40ff0000)
                    .strokeColor(Color.RED)
                    .strokeWidth(2));

            Log.i("Info", "GetPlaces");

            PlaceServices.getInstance().getListPlacesByPosition(myCurrentLocation.latitude,
                    myCurrentLocation.longitude,
                    radius, new ShowListPlacesCallback()).execute();
        }catch(NullPointerException e){
            Log.i("Error", "NullPointerExecption");
        }
    }

    /*Method for displaying a place received by a service*/
    private class ShowListPlacesCallback extends GetListPlacesByPositionCallback {
        protected void callback(Exception e, Place[] places){
            if(!placeMarkerList.isEmpty()) {
                for (Marker marker : placeMarkerList) {
                    marker.remove();
                }
                placeMarkerList.clear();
            }
            for(Place place : places){
                LatLng position = new LatLng(place.getLatitude(), place.getLongitude());
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(position)
                        .title("Place "+place.getId())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                placeMarkerList.add(marker);
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #map} is not null.
     */
    private void setUpMap() {
        //Allow to get the local position
        map.setMyLocationEnabled(true);

        //Get my position
        //myCurrentLocation = new Location(map.getMyLocation().getLatitude(),map.getMyLocation().ge);

        /*Circle circle = map.addCircle(new CircleOptions()
                .center(doubleMixte)
                .radius(100)
                .fillColor(0x40ff0000)
                .strokeColor(Color.RED)
                .strokeWidth(2));*/

        setUpMarkerListAndShowPlaces();
    }
}
