package com.otsims5if.pmc.pmc_android;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.otsims5if.pmc.pmc_android.design.Item;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Seconds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import api.Density;
import api.favorite.Favorite;
import api.place.GetListPlacesByPositionCallback;
import api.place.Place;
import api.place.PlaceServices;
import api.place.ReleasePlaceCallback;
import api.place.TakePlaceCallback;
import api.user.GetUserCallback;
import api.user.User;
import api.user.UserServices;
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
    ImageButton searchAdressButton;
    EditText destinationEditText;
    Marker currentPositionMarker;
    Marker favoriteMarker;
    Marker destinationMarker;
    Favorite favorite;
    LatLng myCurrentLocation;
    LatLng myParkingLocation;
    Thread checkPlacesThread;
    int second = 5;
    int zoomLevel = 17;
    boolean inMotion = true;
    float previousZoomLevel = 0;
    Handler handler = new Handler();
    boolean startThread = false;
    int radius = 200;
    Circle userRange;
    Circle userLocation;
    Polygon area;
    boolean showRange = false;

    ArrayList<Marker> placeMarkerList = new ArrayList<Marker>();
    ArrayList<Circle> placeCircleMarkerList = new ArrayList<Circle>();
    ArrayList<Circle> placeCircleFavoriteMarkerList = new ArrayList<Circle>();

    ArrayList<Polygon> areaPolygon = new ArrayList<Polygon>();

    Hashtable<String, Area> areaHashtable = new Hashtable<>();
    Collection<Area> areaCol;

    //List<WeightedLatLng> list = new ArrayList<>();

    List<WeightedLatLng> highDensityZone = new ArrayList<>();
    List<WeightedLatLng> lowDensityZone = new ArrayList<>();
    List<WeightedLatLng> mediumDensityZone = new ArrayList<>();

    List<LatLng> highDensityZoneFavorite = new ArrayList<>();
    List<LatLng> lowDensityZoneFavorite = new ArrayList<>();
    List<LatLng> mediumDensityZoneFavorite = new ArrayList<>();

    List<GroundOverlay> groundOverlayList = new ArrayList<GroundOverlay>();

    double numberOfPoints = 10;

    boolean openningActivity = true;
    boolean localPosition = true;

    //Constant for heatmap

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

    int[] greenNeutralFillColor = {
            Color.argb(255, 62, 139, 47), // green
            Color.argb(255, 139, 139, 125)    // gray
    };

    int[] greenNeutralStrokeColor = {
            Color.argb(255, 38, 91, 30), // green
            Color.argb(255, 88, 91, 85)    // gray
    };

    float[] startPoints = {
            0, 0.02f
    };

    float[] startNeutralPoints = {
            0, 1
    };

    Gradient gradientRed = new Gradient(colorsRed,startPoints);
    Gradient gradientGreen = new Gradient(colorsGreen,startPoints);
    Gradient gradientOrange = new Gradient(colorsOrange,startPoints);
    Gradient gradientNeutralFill = new Gradient(greenNeutralFillColor,startNeutralPoints);
    Gradient gradientNeutralStroke = new Gradient(greenNeutralStrokeColor,startNeutralPoints);

    HeatmapTileProvider mProvider;
    HeatmapTileProvider mProviderGreen;
    HeatmapTileProvider mProviderOrange;
    TileOverlay mOverlay;
    TileOverlay mOverlayGReen;
    TileOverlay mOverlayOrange;

    //LatLng laDouaGastonBerger = new LatLng(45.781543000000010000,4.872104000000036000);
    //LatLng insa = new LatLng(45.7829609,4.875031300000046);
    //LatLng doubleMixte = new LatLng(45.78053269999999,4.872770199999991);
    //LatLng laPoste = new LatLng(43.4778166,5.168552200000022);

    public UserMapFragment() {
        super();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflat and return the layout
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
        }

        // Get buttons by there id
        parkButton = (Button) view.findViewById(R.id.parkButton);
        leaveButton = (Button) view.findViewById(R.id.leaveButton);
        searchAdressButton = (ImageButton) view.findViewById(R.id.searchButton);

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
                        Thread.sleep(second *1000);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {

                                //Show marker place that are available inside user's range
                                setUpMarkerListAndShowPlaces();

                                try {
                                    ZoneServices.getInstance().getListZonesByPosition(myCurrentLocation.latitude,
                                            myCurrentLocation.longitude, radius, new ShowListZonesCallback()).execute();
                                }catch(Exception e){

                                }
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        });


        setUpMapIfNeeded();

        //Set actions for leave and park buttons
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        float height = displaymetrics.heightPixels;//216
        int width = displaymetrics.widthPixels;

        parkButton.setY(height-(height/3)-100);
        parkButton.setX((width/4));
        parkButton.setLayoutParams(new RelativeLayout.LayoutParams(300, 100));

        leaveButton.setY(height-(height/3)-100);
        leaveButton.setX((width/4));
        leaveButton.setLayoutParams(new RelativeLayout.LayoutParams(300, 100));

        //Get user current location if park
        try {
            UserServices.getInstance().getUser(new GetUserCallback() {
                @Override
                protected void callback(Exception e, User user) {
                    myParkingLocation = new LatLng(user.getPlace().getLatitude(), user.getPlace().getLongitude());
                }
            });
        }catch(Exception exp){

        }

        if(myParkingLocation!=null){
            leaveButton.setVisibility(View.VISIBLE);
            parkButton.setVisibility(View.GONE);
            currentPositionMarker = map.addMarker(new MarkerOptions()
                    .position(myParkingLocation)
                    .title("Je me suis garer ici")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
        }else{
            System.out.println("C'est null la position");
        }

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
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myCurrentLocation, zoomLevel);
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

        //Perform any camera updates here
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
                if(openningActivity) {
                    map.animateCamera(cameraUpdate);
                    openningActivity = false;
                }
                else if(inMotion){

                    if(previousZoomLevel!=0) {
                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, previousZoomLevel);
                    }
                    map.animateCamera(cameraUpdate);
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
                Log.d("Zoom", "Zoom: " + position.zoom);
                LatLngBounds mapVisibleBounds = map.getProjection().getVisibleRegion().latLngBounds;
                if(previousZoomLevel != position.zoom)
                {
                    try {
                        ZoneServices.getInstance().getListZonesByPosition(myCurrentLocation.latitude,
                                myCurrentLocation.longitude, radius, new ShowListZonesCallback()).execute();
                    }catch(Exception e){

                    }
                }
                previousZoomLevel = position.zoom;
                if(myCurrentLocation!=null) {
                    if(!mapVisibleBounds.contains(myCurrentLocation)){
                        inMotion = false;
                    }
                }
            }
        });

        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                myCurrentLocation = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myCurrentLocation, previousZoomLevel);
                map.animateCamera(cameraUpdate);
                localPosition = true;
                inMotion = true;
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
                    for(int i=0; i<maxIndex-1; i++) {
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
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myCurrentLocation, zoomLevel);
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
        areaCol = areaHashtable.values();
        if(areaPolygon != null) {
            for (Polygon e : areaPolygon) {
                e.remove();
            }
        }
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
                    WeightedLatLng weightedPosition = new WeightedLatLng(position, zone.getIntensity());
                    if (zone.getDensity() == Density.HIGH) {
                        highDensityZone.add(weightedPosition);
                    } else if (zone.getDensity() == Density.LOW) {
                        lowDensityZone.add(weightedPosition);
                    }else{
                        mediumDensityZone.add(weightedPosition);
                    }
                }
            }catch(Exception exp){
                System.err.println("There is no areas");
            }

            try {
                //Version heatmap
                if (mProviderGreen == null) {
                    mProviderGreen = new HeatmapTileProvider.Builder()
                            //.data(lowDensityZone)
                            .weightedData(lowDensityZone)
                            .gradient(gradientGreen)
                            .opacity(0.3)
                            .radius(50)
                            .build();
                    // Add a tile overlay to the map, using the heat map tile provider.
                    mOverlayGReen = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProviderGreen));
                } else {
                    mProviderGreen.setWeightedData(lowDensityZone);
                    //mOverlayGReen.clearTileCache();
                }

                if (mProvider == null) {
                    mProvider = new HeatmapTileProvider.Builder()
                            .weightedData(highDensityZone)
                            .gradient(gradientRed)
                            .opacity(0.3)
                            .radius(50)
                            .build();
                    // Add a tile overlay to the map, using the heat map tile provider.
                    mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                } else {
                    mProvider.setWeightedData(highDensityZone);
                    //mOverlay.clearTileCache();
                }

                if (mProviderOrange == null) {
                    mProviderOrange = new HeatmapTileProvider.Builder()
                            .weightedData(mediumDensityZone)
                            .gradient(gradientOrange)
                            .opacity(0.3)
                            .radius(50)
                            .build();
                    // Add a tile overlay to the map, using the heat map tile provider.
                    mOverlayOrange = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProviderOrange));
                } else {
                    mProviderOrange.setWeightedData(mediumDensityZone);
                    //mOverlayGReen.clearTileCache();
                }

            }catch(Exception exp){

            }

        }
    }

    public void showDensitySelectPopup(View v){

        final Item[] items = {
                new Item("Forte", R.drawable.high),
                new Item("Moyenne", R.drawable.medium),
                new Item("Faible", R.drawable.low),
                new Item("Ignorer", R.drawable.ignore),
        };

        ListAdapter adapter = new ArrayAdapter<Item>(
                v.getContext(),
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items){
            @TargetApi(Build.VERSION_CODES.CUPCAKE)
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
                    @TargetApi(Build.VERSION_CODES.CUPCAKE)
                    public void onClick(DialogInterface dialog, int item) {
                        Density currentDensity = null;
                        switch (item) {
                            case 0:
                                currentDensity = Density.HIGH;
                                break;
                            case 1:
                                currentDensity = Density.MEDIUM;
                                break;
                            case 2:
                                currentDensity = Density.LOW;
                                break;
                            default:
                                currentDensity = null;
                                break;
                        }
                        if (currentDensity != null) {
                            try {
                                ZoneServices.getInstance().indicateDensity(myCurrentLocation.latitude,
                                        myCurrentLocation.longitude, currentDensity, new IndicateDensity()).execute();
                            } catch (Exception e) {

                            }
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

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private void setUpMarkerListAndShowPlaces(){

        try {

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

            //list.clear();

            //Circle version
            if(!placeCircleMarkerList.isEmpty()) {
                for (Circle marker : placeCircleMarkerList) {
                    marker.remove();
                }
                placeCircleMarkerList.clear();
            }
            if(!placeMarkerList.isEmpty()) {
                for (Marker marker : placeMarkerList) {
                    marker.remove();
                }
                placeMarkerList.clear();
            }
            if(places!=null) {
                for (Place place : places) {
                    LatLng position = new LatLng(place.getLatitude(), place.getLongitude());
                    int stroke = Color.RED;
                    int fill = 0x40ff0000;
                    String title="";
                    if (!place.isTaken()) {
                        //Interval timeInterval = new Interval(place.getDateLastRelease(), DateTime.now());
                        double ratio = 0;
                        try {
                            Seconds timeInterval = Seconds.secondsBetween(place.getDateLastRelease(), DateTime.now());
                            Interval interv = new Interval(place.getDateLastRelease(), DateTime.now());
                            ratio = timeInterval.getSeconds()/3600;
                            String time = "";
                            if(interv.toDuration().getStandardHours()== 0){
                                time = interv.toDuration().getStandardMinutes()+" minutes";
                            }else if(interv.toDuration().getStandardHours()<= 48){
                                time = interv.toDuration().getStandardHours()+" heures";
                            }else{
                                time = interv.toDuration().getStandardDays()+" jours";
                            }
                            title = "La place a été libérée il y a "+ time;
                        }catch (Exception exp){
                            title = "La place a été libérée.";
                        }

                        if(ratio>1){
                            ratio = 1;
                        }
                        int redFill = (int)((184 - 76)*ratio +76);
                        int greenFill = 191;
                        int blueFill = (int)((175 - 61)*ratio + 61);
                        int redStroke = (int)((139 - 38)*ratio +38);
                        int greenStroke = 139;
                        int blueStroke = (int)((139 - 30)*ratio + 30);
                        //stroke = 0xFF265B1E;
                        //fill = 0xff3e8b2f;
                        fill = Color.argb(200, redFill, greenFill, blueFill);
                        stroke = Color.argb(255, redStroke, greenStroke, blueStroke);
                    }else{
                        try {
                            Interval interv = new Interval(place.getDateLastTake(), DateTime.now());
                            String time = "";
                            if(interv.toDuration().getStandardHours()== 0){
                                time = interv.toDuration().getStandardMinutes()+" minutes";
                            }else if(interv.toDuration().getStandardHours()<= 48){
                                time = interv.toDuration().getStandardHours()+" heures";
                            }else{
                                time = interv.toDuration().getStandardDays()+" jours";
                            }
                            title = "La place a été prise il y a "+ time;
                        }catch (Exception exp){
                            title = "La place a été prise.";
                        }
                    }
                    Circle marker = map.addCircle(new CircleOptions()
                            .center(position)
                            .radius(1)
                            .fillColor(fill)
                            .zIndex(2)
                            .strokeColor(stroke)
                            .strokeWidth(2));
                    placeCircleMarkerList.add(marker);

                    if(previousZoomLevel>17){
                        Marker pos = map.addMarker(new MarkerOptions()
                                .position(position)
                                .title(title)
                                .alpha(0)
                                .snippet("Place " + place.getId())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                        placeMarkerList.add(pos);
                    }
                    /*if (place.isTaken()) {
                        list.add(new WeightedLatLng(position, 10));
                    } else {
                        list.add(new WeightedLatLng(position, 1));
                    }*/
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
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
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
            if(currentPositionMarker!=null) {
                currentPositionMarker.remove();
            }

            try {
                //redraw the marker at the position beacause the user just go
                Circle marker = map.addCircle(new CircleOptions()
                        .center(new LatLng(place.getLatitude(), place.getLongitude()))
                        .radius(1)
                        .fillColor(0xff3e8b2f)
                        .zIndex(2)
                        .strokeColor(0xFF265B1E)
                        .strokeWidth(2));
                placeCircleMarkerList.add(marker);
            }catch(Exception exp){

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

        setUpMarkerListAndShowPlaces();
    }

    public void displayAndMoveToFavorite(Favorite favorite){

        if(favoriteMarker !=null){
             favoriteMarker.remove();
        }

        this.favorite = favorite;
        //First move to the right favorite address
        LatLng latLng = new LatLng(favorite.getLatitude(), favorite.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);

        map.animateCamera(cameraUpdate);

        //Display after the marker
        favoriteMarker =  map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(favorite.getAddress())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.favorite)));
        showPlacesAndZonesFromFavorite();
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void showPlacesAndZonesFromFavorite(){

        //Retrieve zones from a favorite place
        try {
            ZoneServices.getInstance().getListZonesByPosition(favorite.getLatitude(),
                    favorite.getLongitude(), radius, new ShowFavoriteListZonesCallback()).execute();

        }catch(Exception e){

        }

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

            try {
                for (Zone zone : zones) {
                    LatLng position = new LatLng(zone.getLatitude(), zone.getLongitude());
                    if (zone.getDensity() == Density.HIGH) {
                        highDensityZoneFavorite.add(position);

                    } else if (zone.getDensity() == Density.LOW) {
                        lowDensityZoneFavorite.add(position);

                    }else{
                        mediumDensityZoneFavorite.add(position);
                    }
                }
            }catch(Exception exp){
                System.err.println("There is no areas");
            }

            try {
                //Version heatmap
                if (mProviderGreen == null) {
                    mProviderGreen = new HeatmapTileProvider.Builder()
                            .data(lowDensityZoneFavorite) //list with places
                                    //.weightedData(grid) // list with grid
                            .gradient(gradientGreen)
                            .opacity(0.5)
                            .radius(30)
                            .build();
                    // Add a tile overlay to the map, using the heat map tile provider.
                    mOverlayGReen = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProviderGreen));
                } else {
                    mProviderGreen.setData(lowDensityZoneFavorite);
                    //mOverlayGReen.clearTileCache();
                }

                if (mProvider == null) {
                    mProvider = new HeatmapTileProvider.Builder()
                            .data(highDensityZoneFavorite) //list with places
                                    //.weightedData(grid) // list with grid
                            .gradient(gradientRed)
                            .opacity(0.2)
                            .radius(30)
                            .build();
                    // Add a tile overlay to the map, using the heat map tile provider.
                    mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                } else {
                    mProvider.setData(highDensityZoneFavorite);
                    //mOverlay.clearTileCache();
                }

                if (mProviderOrange == null) {
                    mProviderOrange = new HeatmapTileProvider.Builder()
                            .data(mediumDensityZoneFavorite) //list with places
                                    //.weightedData(grid) // list with grid
                            .gradient(gradientOrange)
                            .opacity(0.5)
                            .radius(30)
                            .build();
                    // Add a tile overlay to the map, using the heat map tile provider.
                    mOverlayOrange = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProviderOrange));
                } else {
                    mProviderOrange.setData(mediumDensityZoneFavorite);
                    //mOverlayGReen.clearTileCache();
                }

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