package com.otsims5if.pmc.pmc_android;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.otsims5if.pmc.pmc_android.design.Item;

import java.util.ArrayList;
import java.util.List;

import api.favorite.Favorite;
import api.place.GetListPlacesByPositionCallback;
import api.place.Place;
import api.place.PlaceServices;
import api.place.ReleasePlaceCallback;
import api.place.TakePlaceCallback;
import api.Density;
import api.zone.GetListZonesByPositionCallback;
import api.zone.IndicateDensityCallback;
import api.zone.Zone;
import api.zone.ZoneServices;

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
    Marker favoriteMarker;
    LatLng myCurrentLocation;
    LatLng myParkingLocation;
    Thread checkPlacesThread;
    int seconde = 5;
    Handler handler = new Handler();
    boolean startThread = false;
    int radius = 200;
    Circle userRange;
    //double i = 0;
    boolean showRange = false;

    ArrayList<Marker> placeMarkerList = new ArrayList<Marker>();
    ArrayList<Circle> placeCircleMarkerList = new ArrayList<Circle>();
    List<Polygon> gridPolygon = new ArrayList<Polygon>();
    List<LatLng> grid = new ArrayList<>();


    List<WeightedLatLng> list = new ArrayList<>();

    List<LatLng> highDensityZone = new ArrayList<>();
    List<LatLng> lowDensityZone = new ArrayList<>();
    List<LatLng> mediumDensityZone = new ArrayList<>();

    //ArrayList<Circle> highDensityZoneMarkerList = new ArrayList<Circle>();
    //ArrayList<Circle> lowDensityZoneMarkerList = new ArrayList<Circle>();
    List<GroundOverlay> groundOverlayList = new ArrayList<GroundOverlay>();

    double startLongitude = 0;
    double endLongitude = 0;
    double startLatitude = 0;
    double endLatitude = 0;
    double numberOfPoints = 10;
    double intervalLongitude = 0;
    double intervalLatitude = 0;
    boolean openningActivity = true;

    //Constant for heatmap
    int[] colors = {
            Color.rgb(102, 225, 0), // green
            Color.rgb(255, 0, 0)    // red
    };

    float[] startPoints = {
            0.1f, 2f
    };

    Gradient gradient = new Gradient(colors,startPoints);
    HeatmapTileProvider mProvider;
    HeatmapTileProvider mProviderGreen;
    HeatmapTileProvider mProviderOrange;
    TileOverlay mOverlay;
    TileOverlay mOverlayGReen;
    TileOverlay mOverlayOrange;

    LatLng laDouaGastonBerger = new LatLng(45.781543000000010000,4.872104000000036000);
    LatLng insa = new LatLng(45.7829609,4.875031300000046);
    LatLng doubleMixte = new LatLng(45.78053269999999,4.872770199999991);
    LatLng laPoste = new LatLng(43.4778166,5.168552200000022);

    List<Zone> zoneTest = new ArrayList<>();

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
                        Thread.sleep(seconde*1000);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub

                                //Show marker place that are available inside user's range
                                setUpMarkerListAndShowPlaces();

                                try {
                                    ZoneServices.getInstance().getListZonesByPosition(myCurrentLocation.latitude,
                                            myCurrentLocation.longitude, 200, new ShowListZonesCallback()).execute();


                                }catch(Exception e){

                                }

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

                //Show popup
                showDensitySelectPopup(v);


            }
        });
        leaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                leaveButton.setVisibility(View.GONE);
                parkButton.setVisibility(View.VISIBLE);
                //currentPositionMarker.remove();
                PlaceServices.getInstance().releasePlace(myParkingLocation.latitude,
                        myParkingLocation.longitude, new ReleaseAndRemovePlace()).execute();

                //Show popup
                showDensitySelectPopup(v);
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

        //Create the heatmap

        // Create a heat map tile provider, passing it the latlngs.


        //Perform any camera updates here

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                if(openningActivity) {
                    map.animateCamera(cameraUpdate);
                    openningActivity = false;
                }
                //locationManager.removeUpdates(this);
            }
        });

        if(checkPlacesThread.getState()!= Thread.State.RUNNABLE){
            startThread = true;
            checkPlacesThread.start();
        }

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {

            }
        });


        return v;
    }

    /*Method for displaying a place received by a service*/
    private class ShowListZonesCallback extends GetListZonesByPositionCallback {
        protected void callback(Exception e, Zone[] zones){

            highDensityZone.clear();
            lowDensityZone.clear();
            mediumDensityZone.clear();
            zoneTest.clear();

            if(!groundOverlayList.isEmpty()){
                for(GroundOverlay element :groundOverlayList ){
                    element.remove();
                }
                groundOverlayList.clear();
            }

            try {
                System.out.println("There is "+zones.length+" areas");
                for (Zone zone : zones) {
                    LatLng position = new LatLng(zone.getLatitude(), zone.getLongitude());
                    if (zone.getDensity() == Density.HIGH) {
                        highDensityZone.add(position);
                        /*GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromResource(R.drawable.redcube))
                                .transparency(0.5f)
                                .position(position, 20f, 20f);

                        // Add an overlay to the map, retaining a handle to the GroundOverlay object.
                        groundOverlayList.add(map.addGroundOverlay(newarkMap));*/
                    } else if (zone.getDensity() == Density.LOW) {
                        lowDensityZone.add(position);
                        /*GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromResource(R.drawable.greencube))
                                .transparency(0.5f)
                                .position(position, 20f, 20f);

                        // Add an overlay to the map, retaining a handle to the GroundOverlay object.
                        groundOverlayList.add(map.addGroundOverlay(newarkMap));*/
                    }else{
                        mediumDensityZone.add(position);
                    }
                }
            }catch(Exception exp){
                System.err.println("There is no areas");
            }

            //Update list for heatmap

            //Just for Test
            double j=0;
            //create some zones
            for(int i=0; i<20; i++){
                double a = -1;
                if(i%2==0) {
                   a=1;
                }
                zoneTest.add(new Zone(laDouaGastonBerger.latitude+j, laDouaGastonBerger.longitude, Density.MEDIUM));
                j = a*0.001 * Math.random();
                zoneTest.add(new Zone(laDouaGastonBerger.latitude, laDouaGastonBerger.longitude+j, Density.LOW));
                j = a*0.001 * Math.random();
                zoneTest.add(new Zone(laDouaGastonBerger.latitude+j, laDouaGastonBerger.longitude+j, Density.LOW));
                j = a*0.001 * Math.random();
                zoneTest.add(new Zone(doubleMixte.latitude+j, doubleMixte.longitude, Density.LOW));
                j = a*0.001 * Math.random();
                zoneTest.add(new Zone(doubleMixte.latitude, doubleMixte.longitude+j, Density.HIGH));
                j = a*0.001 * Math.random();
                zoneTest.add(new Zone(doubleMixte.latitude+j, doubleMixte.longitude+j, Density.LOW));
                j = a*0.001 * Math.random();

            }
            for (Zone zone : zoneTest) {
                LatLng position = new LatLng(zone.getLatitude(), zone.getLongitude());
                if (zone.getDensity() == Density.HIGH) {
                    highDensityZone.add(position);
                    /*    GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromResource(R.drawable.redcube))
                                .transparency(0.5f)
                                .position(position, 20f, 20f);

                        // Add an overlay to the map, retaining a handle to the GroundOverlay object.
                    groundOverlayList.add(map.addGroundOverlay(newarkMap));*/
                } else if (zone.getDensity() == Density.LOW) {
                    lowDensityZone.add(position);
                     /*   GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromResource(R.drawable.greencube))
                                .transparency(0.5f)
                                .position(position, 20f, 20f);

                        // Add an overlay to the map, retaining a handle to the GroundOverlay object.
                        groundOverlayList.add(map.addGroundOverlay(newarkMap));*/
                }else{
                    mediumDensityZone.add(position);
                }
            }
            //updateDataForHeatMap(places);

            int[] colorsRed = {
                    Color.argb(0, 255, 0, 0), // red opaq
                    Color.argb(255, 255, 0, 0)    // red
            };
            int[] colorsGreen = {
                    Color.argb(0, 102, 225, 0), // green opaq
                    Color.argb(255, 102, 225, 0)    // green
            };

            int[] colorsOrange = {
                    Color.argb(0, 255, 204, 0), // green opaq
                    Color.argb(255, 255, 204, 0)    // green
            };

            float[] startPoints = {
                    0, 1f
            };

            Gradient gradientRed = new Gradient(colorsRed,startPoints);
            Gradient gradientGreen = new Gradient(colorsGreen,startPoints);
            Gradient gradientOrange = new Gradient(colorsOrange,startPoints);

            try {
                //Version heatmap
                if (mProviderGreen == null) {
                    mProviderGreen = new HeatmapTileProvider.Builder()
                            .data(lowDensityZone) //list with places
                                    //.weightedData(grid) // list with grid
                            .gradient(gradientGreen)
                            .opacity(0.5)
                            .radius(30)
                            .build();
                    // Add a tile overlay to the map, using the heat map tile provider.
                    mOverlayGReen = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProviderGreen));
                } else {
                    mProviderGreen.setData(lowDensityZone);
                    //mOverlayGReen.clearTileCache();
                }

                if (mProvider == null) {
                    mProvider = new HeatmapTileProvider.Builder()
                            .data(highDensityZone) //list with places
                                    //.weightedData(grid) // list with grid
                            .gradient(gradientRed)
                            .opacity(0.2)
                            .radius(30)
                            .build();
                    // Add a tile overlay to the map, using the heat map tile provider.
                    mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                } else {
                    mProvider.setData(highDensityZone);
                    //mOverlay.clearTileCache();
                }

                if (mProviderOrange == null) {
                    mProviderOrange = new HeatmapTileProvider.Builder()
                            .data(mediumDensityZone) //list with places
                                    //.weightedData(grid) // list with grid
                            .gradient(gradientOrange)
                            .opacity(0.5)
                            .radius(30)
                            .build();
                    // Add a tile overlay to the map, using the heat map tile provider.
                    mOverlayOrange = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProviderOrange));
                } else {
                    mProviderOrange.setData(mediumDensityZone);
                    //mOverlayGReen.clearTileCache();
                }

                //New version heatmap


            }catch(Exception exp){

            }

        }
    }

    public void showDensitySelectPopup(View v){


        final Item[] items = {
                new Item("Forte", R.drawable.high),
                new Item("Moyenne", R.drawable.medium),
                new Item("Faible", R.drawable.low),
        };

        ListAdapter adapter = new ArrayAdapter<Item>(
                v.getContext(),
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items){
            public View getView(int position, View convertView, ViewGroup parent) {
                //User super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView)v.findViewById(android.R.id.text1);
                //v.setBackgroundColor(Color.BLACK);

                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

                //Add margin between image and text (support various screen densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                tv.setCompoundDrawablePadding(dp5);

                return v;
            }
        };


        new AlertDialog.Builder(getActivity())
                .setTitle("Etat de la zone")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        System.out.println("item "+item);
                        Density currentDensity = Density.LOW;
                        switch(item){
                            case 0:
                                currentDensity = Density.HIGH;
                                break;
                            case 1:
                                currentDensity = Density.MEDIUM;
                                break;
                            case 2:
                                currentDensity = Density.LOW;
                                break;
                        }
                        try {
                            ZoneServices.getInstance().indicateDensity(myCurrentLocation.latitude,
                                    myCurrentLocation.longitude, currentDensity, new IndicateDensity()).execute();
                        }catch(Exception e){

                        }
                    }
                }).show();
    }

    public boolean insideRange(double refValue, double checkValue, double range){
        double infRefValue = refValue-range;
        double supRefValue = refValue+range;

        if(checkValue >= infRefValue && checkValue <= supRefValue){
            return true;
        }

        return false;
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

            list.clear();

            //Circle version
            if(!placeCircleMarkerList.isEmpty()) {
                for (Circle marker : placeCircleMarkerList) {
                    marker.remove();
                }
                placeCircleMarkerList.clear();
            }
            if(places!=null) {
                for (Place place : places) {
                    LatLng position = new LatLng(place.getLatitude(), place.getLongitude());
                    int stroke = Color.RED;
                    int fill = 0x40ff0000;
                    if (!place.isTaken()) {
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
                    if (place.isTaken()) {
                        list.add(new WeightedLatLng(position, 10));
                    } else {
                        list.add(new WeightedLatLng(position, 1));
                    }
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
            /*HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                    .weightedData(list) //list with places
                    .gradient(gradient)
                    .opacity(1)
                    .radius(50)
                    .build();
            // Add a tile overlay to the map, using the heat map tile provider.
            TileOverlay mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));*/

            /*if (mProvider == null) {
                mProvider = new HeatmapTileProvider.Builder()
                        .weightedData(list) //list with places
                                //.weightedData(grid) // list with grid
                        .gradient(gradient)
                        .opacity(0.8)
                        .radius(50)
                        .build();
                // Add a tile overlay to the map, using the heat map tile provider.
                mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            } else {
                mProvider.setWeightedData(list);
                mOverlay.clearTileCache();
            }*/
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

    /*Method for displaying a place received by a service*/
    private class IndicateDensity extends IndicateDensityCallback {
        protected void callback(Exception e, Zone zone){
            //Now do nothing but maybe in the futur
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

        setUpMarkerListAndShowPlaces();
    }

    public void displayAndMoveToFavorite(Favorite favorite){

        //First move to the right favorite address
        LatLng latLng = new LatLng(favorite.getLatitude(), favorite.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        map.animateCamera(cameraUpdate);

        //Display after the marker
        favoriteMarker =  map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(favorite.getAddress())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.favorite)));
    }
}
