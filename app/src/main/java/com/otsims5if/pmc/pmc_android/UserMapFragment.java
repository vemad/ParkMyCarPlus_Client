package com.otsims5if.pmc.pmc_android;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.otsims5if.pmc.pmc_android.design.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import api.favorite.Favorite;
import api.place.GetListPlacesByPositionCallback;
import api.place.Place;
import api.place.PlaceServices;
import api.place.ReleasePlaceCallback;
import api.place.TakePlaceCallback;
import api.Density;
import api.zone.Area;
import api.zone.GetListZonesByPositionCallback;
import api.zone.IndicateDensityCallback;
import api.zone.Zone;
import api.zone.ZoneServices;

/**
 * Created by Iler on 18/02/2015.
 */
public class UserMapFragment extends PlaceholderFragment{

    private static View view;
    private static MapView mapView;
    private static GoogleMap map;
    Button parkButton;
    Button leaveButton;
    Switch placeFindSwitch;
    ImageButton searchAdressButton;
    EditText destinationEditText;
    Marker currentPositionMarker;
    Marker favoriteMarker;
    Marker destinationMarker;
    Favorite favorite;
    LatLng myCurrentLocation;
    LatLng myParkingLocation;
    Thread checkPlacesThread;
    int seconde = 5;
    Handler handler = new Handler();
    boolean startThread = false;
    int radius = 200;
    Circle userRange;
    Circle userLocation;
    Polygon area;
    int compteurAffichage = 0;
    boolean showRange = false;

    ArrayList<Marker> placeMarkerList = new ArrayList<Marker>();
    ArrayList<Circle> placeCircleMarkerList = new ArrayList<Circle>();
    ArrayList<Circle> placeCircleFavoriteMarkerList = new ArrayList<Circle>();

    ArrayList<Polygon> areaPolygon = new ArrayList<Polygon>();

    Hashtable<String, Area> areaHashtable = new Hashtable<>();
    Collection<Area> areaCol;

    List<WeightedLatLng> list = new ArrayList<>();

    List<LatLng> highDensityZone = new ArrayList<>();
    List<LatLng> lowDensityZone = new ArrayList<>();
    List<LatLng> mediumDensityZone = new ArrayList<>();

    List<LatLng> highDensityZoneFavorite = new ArrayList<>();
    List<LatLng> lowDensityZoneFavorite = new ArrayList<>();
    List<LatLng> mediumDensityZoneFavorite = new ArrayList<>();

    List<GroundOverlay> groundOverlayList = new ArrayList<GroundOverlay>();

    double startLongitude = 0;
    double endLongitude = 0;
    double startLatitude = 0;
    double endLatitude = 0;
    double numberOfPoints = 10;
    double intervalLongitude = 0;
    double intervalLatitude = 0;
    boolean openningActivity = true;
    boolean localPosition = true;

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
    List<Zone> zoneTestFav = new ArrayList<>();

    public UserMapFragment() {
        super();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        //View v = inflater.inflate(R.layout.fragment_map, container, false);

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        // Get buttons by there id
        parkButton = (Button) view.findViewById(R.id.parkButton);
        leaveButton = (Button) view.findViewById(R.id.leaveButton);
        searchAdressButton = (ImageButton) view.findViewById(R.id.searchButton);
       // placeFindSwitch = (Switch) view.findViewById(R.id.placeFindSwitch);

        destinationEditText = (EditText) view.findViewById(R.id.destinationEditText);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(view.getContext());
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

                                /*//Make the gps/network position as best as possible
                                String locationNetworkProvider = LocationManager.NETWORK_PROVIDER;
                                // Or, use GPS location data:
                                String locationGPSProvider = LocationManager.GPS_PROVIDER;

                                // Acquire a reference to the system Location Manager
                                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                                // Define a listener that responds to location updates
                                LocationListener locationListener = new LocationListener() {
                                    public void onLocationChanged(Location location) {
                                        // Called when a new location is found by the network location provider.
                                        if (userLocation != null) {
                                            userLocation.remove();
                                        }
                                        LatLng gpsLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                        userLocation = map.addCircle(new CircleOptions()
                                                .center(gpsLocation)
                                                .radius(10)
                                                .fillColor(Color.LTGRAY)
                                                .strokeColor(Color.DKGRAY)
                                                .zIndex(1)
                                                .strokeWidth(2));
                                    }

                                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                                    public void onProviderEnabled(String provider) {}

                                    public void onProviderDisabled(String provider) {}
                                };

                                locationManager.requestLocationUpdates(locationGPSProvider, 0, 0, locationListener);
                                locationManager.requestLocationUpdates(locationNetworkProvider, 0, 0, locationListener);*/
                                /*Location lastKnownLocation = locationManager.getLastKnownLocation(locationGPSProvider);
                                if (userLocation != null) {
                                    userLocation.remove();
                                }

                                userLocation = map.addCircle(new CircleOptions()
                                        .center(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                                        .radius(10)
                                        .fillColor(0xff758b9a)
                                        .strokeColor(0xff041126)
                                        .zIndex(1)
                                        .strokeWidth(2));*/
                                //Show marker place that are available inside user's range
                                setUpMarkerListAndShowPlaces();

                                /*if(areaCol!=null){
                                    if(compteurAffichage%2==0) {
                                        drawAreaMap();
                                    }
                                }*/

                                try {
                                    ZoneServices.getInstance().getListZonesByPosition(myCurrentLocation.latitude,
                                            myCurrentLocation.longitude, radius, new ShowListZonesCallback()).execute();
                                }catch(Exception e){

                                }

                                compteurAffichage++;
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        });

        //map = mapView.getMap();
        setUpMapIfNeeded();

        //Set actions for leave and park buttons
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

     /*   Display display = getActivity().getWindowManager().getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();
        System.out.println("screenWidth "+screenWidth);
*/

        float height = displaymetrics.heightPixels;//216
        int width = displaymetrics.widthPixels;
        System.out.println("aaaaaaaaa "+width);
        System.out.println("height "+height);//1280
        System.out.println("height-(height/4) "+(height-(height/3)));

        parkButton.setY(height-(height/3));
        parkButton.setX((width/4));
        parkButton.setLayoutParams(new RelativeLayout.LayoutParams(300, 50));



        leaveButton.setY(height-(height/3));
        leaveButton.setX((width/4));
        leaveButton.setLayoutParams(new RelativeLayout.LayoutParams(300, 50));

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

        searchAdressButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LatLng position = getLocationFromAddress(destinationEditText.getText().toString());
                if(position!=null) {
                    myCurrentLocation = position;
                    localPosition = false;
                    destinationEditText.setText("");

                    if(destinationMarker !=null){
                        destinationMarker.remove();
                    }

                    //First move to the right destination address
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myCurrentLocation, 15);
                    System.out.println(map);
                    map.animateCamera(cameraUpdate);

                    //Display after the marker
                    destinationMarker =  map.addMarker(new MarkerOptions()
                            .position(myCurrentLocation)
                            .title(destinationEditText.getText().toString())
                            .snippet(position.latitude + " - " + position.longitude)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination)));
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

        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                myCurrentLocation = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myCurrentLocation, 15);
                map.animateCamera(cameraUpdate);
                localPosition = true;
                return true;
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                List<Address> addresses = getAdressFromLatLng(latLng);
                String addressFind = "";
                if(addresses!=null){
                    int maxIndex = addresses.get(0).getMaxAddressLineIndex();
                    System.out.println("Adresse trouvé :");
                    for(int i=0; i<maxIndex-1; i++) {
                        System.out.println(addresses.get(0).getAddressLine(i));
                        addressFind+=addresses.get(0).getAddressLine(i)+"\n";
                    }
                    addressFind+=addresses.get(0).getAddressLine(maxIndex);
                }else{
                    addressFind = "Votre destination";
                }

                //Draw aeras
                //findAndDisplayAreas();

                if(latLng!=null) {
                    myCurrentLocation = latLng;
                    localPosition = false;

                    if(destinationMarker !=null){
                        destinationMarker.remove();
                    }

                    //First move to the right destination address
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myCurrentLocation, 15);
                    map.animateCamera(cameraUpdate);

                    //Display after the marker
                    if(addresses!=null) {
                        destinationMarker = map.addMarker(new MarkerOptions()
                                .position(myCurrentLocation)
                                .title(addressFind)
                                .snippet(latLng.latitude + " - " + latLng.longitude)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination)));
                    }
                }
            }
        });



        return view;
    }

    public void findAndDisplayAreas(){
        VisibleRegion vr = map.getProjection().getVisibleRegion();
        double left = vr.latLngBounds.southwest.longitude;
        double top = vr.latLngBounds.northeast.latitude;
        double right = vr.latLngBounds.northeast.longitude;
        double bottom = vr.latLngBounds.southwest.latitude;
        double pressionLat = (top - bottom)/numberOfPoints;
        double pressionLong = (right - left)/numberOfPoints;
        for(double j=bottom; j<top; j+=pressionLat){
            for(double i=left; i<right; i+=pressionLong){
                LatLng newPosition = new LatLng(j, i);
                List<Address> addr = getAdressFromLatLng(newPosition);
                if(addr!=null) {
                    String codePostale = addr.get(0).getPostalCode();
                    System.out.println("Le code est "+codePostale);
                    if(codePostale!=null) {
                        if (!areaHashtable.containsKey(codePostale)) {
                            Area area = new Area(codePostale);
                            area.addPoints(newPosition);
                            areaHashtable.put(codePostale, area);
                        } else {
                            areaHashtable.get(codePostale).addPoints(newPosition);
                        }
                    }
                }
            }
        }
        System.out.println("Taille de la hashtable"+areaHashtable.size());
        areaCol = areaHashtable.values();
        System.out.println("Taille de la colection"+areaCol.size());
        if(areaPolygon != null) {
            for (Polygon e : areaPolygon) {
                e.remove();
            }
        }
//        for(Area elt : areaCol){
//            System.out.println("Area "+elt.getId());
//            System.out.println("nb points "+elt.getPoints().size());
//
//            HeatmapTileProvider mProviderRand = new HeatmapTileProvider.Builder()
//                    .data(elt.getPoints()) //list with places
//                            //.weightedData(grid) // list with grid
//                    .gradient(elt.getGradientRand())
//                    .opacity(0.5)
//                    .radius(30)
//                    .build();
//            // Add a tile overlay to the map, using the heat map tile provider.
//            map.addTileOverlay(new TileOverlayOptions().tileProvider(mProviderRand));
//        }
    }

    public void drawAreaMap(){
        for(Area elt : areaCol){
            System.out.println("Area "+elt.getId());
            System.out.println("nb points "+elt.getPoints().size());

            HeatmapTileProvider mProviderRand = new HeatmapTileProvider.Builder()
                    .data(elt.getPoints()) //list with places
                            //.weightedData(grid) // list with grid
                    .gradient(elt.getGradientRand())
                    .opacity(0.5)
                    .radius(30)
                    .build();
            // Add a tile overlay to the map, using the heat map tile provider.
            map.addTileOverlay(new TileOverlayOptions().tileProvider(mProviderRand));
        }
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
                    Color.argb(0, 255, 204, 0), // orange opaq
                    Color.argb(255, 255, 204, 0)    // green
            };

            float[] startPoints = {
                    0, 0.02f
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
                            .opacity(0.3)
                            .radius(50)
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
                            .opacity(0.3)
                            .radius(50)
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
                            .opacity(0.3)
                            .radius(50)
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
            if(localPosition) {
                myCurrentLocation = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
            }
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

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            mapView = (MapView) view.findViewById(R.id.map);
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
            if(localPosition) {
                myCurrentLocation = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
            }
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

            try {
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
            }catch(Exception exp){

            }
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

        if(favoriteMarker !=null){
             favoriteMarker.remove();
        }

        this.favorite = favorite;
        //First move to the right favorite address
        LatLng latLng = new LatLng(favorite.getLatitude(), favorite.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        /*mapView = (MapView) view.findViewById(R.id.map);
        MapsInitializer.initialize(view.getContext());
        map = mapView.getMap();*/
        System.out.println(map);
        map.animateCamera(cameraUpdate);

        //Display after the marker
        favoriteMarker =  map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(favorite.getAddress())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.favorite)));
        showPlacesAndZonesFromFavorite();
    }

    public void showPlacesAndZonesFromFavorite(){

        System.out.println("I am there");
        //Retrieve zones from a favorite place
        try {
            ZoneServices.getInstance().getListZonesByPosition(favorite.getLatitude(),
                    favorite.getLongitude(), radius, new ShowFavoriteListZonesCallback()).execute();

        }catch(Exception e){

        }

        System.out.println("Iaaaaaaaaaaaaaaaaaaaa");
        //Retrieve places from a favorite place
        try {
            PlaceServices.getInstance().getListPlacesByPosition(favorite.getLatitude(),
                    favorite.getLongitude(), radius, new ShowFavoriteListPlacesCallback()).execute();

        }catch(Exception e){

        }
    }

    /*Method for displaying a place received by a service*/
    private class ShowFavoriteListPlacesCallback extends GetListPlacesByPositionCallback {
        protected void callback(Exception e, Place[] places){

            //Circle version
            if(!placeCircleFavoriteMarkerList.isEmpty()) {
                for (Circle marker : placeCircleFavoriteMarkerList) {
                    marker.remove();
                }
                placeCircleFavoriteMarkerList.clear();
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
                    placeCircleFavoriteMarkerList.add(marker);
                }
            }

        }
    }

    private class ShowFavoriteListZonesCallback extends GetListZonesByPositionCallback {
        protected void callback(Exception e, Zone[] zones){

            highDensityZoneFavorite.clear();
            lowDensityZoneFavorite.clear();
            mediumDensityZoneFavorite.clear();
            zoneTestFav.clear();

            try {
                for (Zone zone : zones) {
                    LatLng position = new LatLng(zone.getLatitude(), zone.getLongitude());
                    if (zone.getDensity() == Density.HIGH) {
                        highDensityZoneFavorite.add(position);
                        /*GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromResource(R.drawable.redcube))
                                .transparency(0.5f)
                                .position(position, 20f, 20f);

                        // Add an overlay to the map, retaining a handle to the GroundOverlay object.
                        groundOverlayList.add(map.addGroundOverlay(newarkMap));*/
                    } else if (zone.getDensity() == Density.LOW) {
                        lowDensityZoneFavorite.add(position);
                        /*GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromResource(R.drawable.greencube))
                                .transparency(0.5f)
                                .position(position, 20f, 20f);

                        // Add an overlay to the map, retaining a handle to the GroundOverlay object.
                        groundOverlayList.add(map.addGroundOverlay(newarkMap));*/
                    }else{
                        mediumDensityZoneFavorite.add(position);
                    }
                }
            }catch(Exception exp){
                System.err.println("There is no areas");
            }

            //Update list for heatmap

            //Just for Test
            double j=0;
            /*//create some zones
            for(int i=0; i<10; i++){
                double a = -1;
                if(i%2==0) {
                    a=1;
                }
                zoneTest.add(new Zone(favorite.getLatitude()+j, favorite.getLongitude(), Density.MEDIUM));
                j = a*0.001 * Math.random();
                zoneTest.add(new Zone(favorite.getLatitude()+j, favorite.getLongitude()+j, Density.LOW));
                j = a*0.001 * Math.random();
                zoneTest.add(new Zone(favorite.getLatitude()+2*j, favorite.getLongitude()+2*j, Density.LOW));
                j = a*0.001 * Math.random();

            }
            for (Zone zone : zoneTest) {
                LatLng position = new LatLng(zone.getLatitude(), zone.getLongitude());
                if (zone.getDensity() == Density.HIGH) {
                    highDensityZone.add(position);
                } else if (zone.getDensity() == Density.LOW) {
                    lowDensityZone.add(position);
                }else{
                    mediumDensityZone.add(position);
                }
            }*/
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

    public List<Address> getAdressFromLatLng(LatLng location){
        Geocoder geocoder;
        List<Address> addresses;
        try {
            geocoder = new Geocoder(this.getActivity(), Locale.getDefault());
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);

            String address = addresses.get(0).getAddressLine(0);
            //String city = addresses.get(0).getAddressLine(1);
            //String country = addresses.get(0).getAddressLine(2);
            return addresses;
        }catch(Exception e){

        }

        return null;
    }

}
