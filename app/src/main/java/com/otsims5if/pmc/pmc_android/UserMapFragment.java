package com.otsims5if.pmc.pmc_android;

import android.annotation.TargetApi;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.List;

import api.place.GetListPlacesByPositionCallback;
import api.place.Place;
import api.place.PlaceServices;
import api.place.ReleasePlaceCallback;
import api.place.TakePlaceCallback;

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
    LatLng myParkingLocation;
    Thread checkPlacesThread;
    Handler handler = new Handler();
    boolean startThread = false;
    int radius = 200;
    Circle userRange;
    //double i = 0;
    boolean showRange = false;

    ArrayList<Marker> placeMarkerList = new ArrayList<Marker>();
    ArrayList<Circle> placeCircleMarkerList = new ArrayList<Circle>();
    List<WeightedLatLng> grid = new ArrayList<>();

    double startLongitude = 0;
    double endLongitude = 0;
    double startLatitude = 0;
    double endLatitude = 0;
    double numberOfPoints = 50;


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

                                //just for test
                                //i+=0.0001;
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
                myParkingLocation = new LatLng(myCurrentLocation.latitude, myCurrentLocation.longitude);
                PlaceServices.getInstance().takePlace(myParkingLocation.latitude,
                        myParkingLocation.longitude, new TakeAndDisplayPlace()).execute();


            }
        });
        leaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                leaveButton.setVisibility(View.GONE);
                parkButton.setVisibility(View.VISIBLE);
                //currentPositionMarker.remove();
                PlaceServices.getInstance().releasePlace(myParkingLocation.latitude,
                        myParkingLocation.longitude, new ReleaseAndRemovePlace()).execute();
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
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                /*LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
                System.out.println("NorthEst : Latitude = "+bounds.northeast.latitude+" and Longitude = "+bounds.northeast.longitude);
                System.out.println("SouthWest : Latitude = "+bounds.southwest.latitude+" and Longitude = "+bounds.southwest.longitude);
                //Set the right bound for calulate the grid for points
                //Latitude case
                if(bounds.northeast.latitude >= bounds.southwest.latitude){
                    startLatitude = bounds.southwest.latitude;
                    endLatitude = bounds.northeast.latitude;
                }else{
                    startLatitude = bounds.northeast.latitude;
                    endLatitude = bounds.southwest.latitude;
                }
                //Longitude case
                if(bounds.northeast.longitude >= bounds.southwest.longitude){
                    startLongitude = bounds.southwest.longitude;
                    endLongitude = bounds.northeast.longitude;
                }else{
                    startLongitude = bounds.northeast.longitude;
                    endLongitude = bounds.southwest.longitude;
                }

                //Now calculate each point inside de grid thanks to a number of want points
                double intervalLongitude = (endLongitude - startLongitude)/numberOfPoints;
                double intervalLatitude = (endLatitude - startLatitude)/numberOfPoints;

                for(double i=startLongitude; i<endLongitude; i=i+intervalLongitude){
                    for(double j=startLatitude; j<endLatitude; j=j+intervalLatitude){
                        //set weight to 1 by default
                        grid.add(new WeightedLatLng(new LatLng(j, i), 1));
                    }
                }*/
            }
        });

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
            if(showRange) {
                if (userRange != null) {
                    userRange.remove();
                }
                userRange = map.addCircle(new CircleOptions()
                        .center(myCurrentLocation)
                        .radius(radius)
                        .fillColor(0x40ff0000)
                        .strokeColor(Color.RED)
                        .zIndex(1)
                        .strokeWidth(2));
            }

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
            /*if(!placeMarkerList.isEmpty()) {
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
            }*/

            List<WeightedLatLng> list = new ArrayList<>();

            //Circle version
            if(!placeCircleMarkerList.isEmpty()) {
                for (Circle marker : placeCircleMarkerList) {
                    marker.remove();
                }
                placeCircleMarkerList.clear();
            }
            for(Place place : places){
                LatLng position = new LatLng(place.getLatitude(), place.getLongitude());
                int stroke = Color.RED;
                int fill = 0x40ff0000;
                if(!place.isTaken()){
                    stroke = 0xFF265B1E;
                    fill = 0xff3e8b2f;
                }
                Circle marker = map.addCircle(new CircleOptions()
                        .center(position)
                        .radius(1)
                        .fillColor(fill)
                        .zIndex(2)
                        .strokeColor(stroke)
                        .strokeWidth(2));
                placeCircleMarkerList.add(marker);
                if(place.isTaken()) {
                    list.add(new WeightedLatLng(position, 20));
                }else{
                    list.add(new WeightedLatLng(position, 1));
                }
            }

            int[] colors = {
                    Color.rgb(102, 225, 0), // green
                    Color.rgb(255, 0, 0)    // red
            };

            float[] startPoints = {
                    0.1f, 2f
            };

            Gradient gradient = new Gradient(colors,startPoints);

            // Create a heat map tile provider, passing it the latlngs.
            HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                    .weightedData(list)
                    .gradient(gradient)
                    .opacity(0.2)
                    .radius(50)
                    .build();
            // Add a tile overlay to the map, using the heat map tile provider.
            TileOverlay mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }
    }

    /*Method for displaying a place received by a service*/
    private class TakeAndDisplayPlace extends TakePlaceCallback {
        protected void callback(Exception e, Place place){

            //Mark the position in order to park the car at the current position deliver by the GPS
            currentPositionMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(place.getLatitude(), place.getLongitude()))
                    .title("Je me suis garer ici")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            //Mark the place that has been parked by the user
            Circle marker = map.addCircle(new CircleOptions()
                    .center(new LatLng(place.getLatitude(), place.getLongitude()))
                    .radius(1)
                    .fillColor(0x40ff0000)
                    .zIndex(2)
                    .strokeColor(Color.RED)
                    .strokeWidth(2));
            placeCircleMarkerList.add(marker);
        }
    }

    /*Method for remove a place taken by the user*/
    private class ReleaseAndRemovePlace extends ReleasePlaceCallback {
        protected void callback(Exception e, Place place){

            //Mark the position in order to park the car at the current position deliver by the GPS
            currentPositionMarker.remove();

            //redraw the marker at the position beacause the user just go
            Circle marker = map.addCircle(new CircleOptions()
                    .center(new LatLng(place.getLatitude(), place.getLongitude()))
                    .radius(1)
                    .fillColor(0xff3e8b2f)
                    .zIndex(2)
                    .strokeColor(0xFF265B1E)
                    .strokeWidth(2));
            placeCircleMarkerList.add(marker);
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
