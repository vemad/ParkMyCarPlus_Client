package com.otsims5if.pmc.pmc_android;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

import api.authentification.AuthentificateCallback;
import api.authentification.AuthentificationServices;
import api.place.*;
import api.user.SignupCallback;
import api.user.User;
import api.user.UserServices;
import api.zone.Density;
import api.zone.GetListZonesByPositionCallback;
import api.zone.GetZoneByIdCallback;
import api.zone.IndicateDensityCallback;
import api.zone.Zone;
import api.zone.ZoneServices;


public class PlaceInformation extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_information);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        //Call the service getPlaceById and execute the callback ShowPlaceCallback with the result
        PlaceServices.getInstance().getPlaceById(1, new ShowPlaceCallback()).execute();
        PlaceServices.getInstance().getListPlacesByPosition(45.78166386726485, 4.872752178696828, 5, new ShowListPlacesCallback()).execute();
        PlaceServices.getInstance().takePlace(45.78166386726485, 4.872752178696828, new ShowResultTakePlaceCallback()).execute();
        PlaceServices.getInstance().releasePlace(45.78166386726485, 4.872752178696828, new ShowResultReleasePlaceCallback()).execute();
        PlaceServices.getInstance().takePlace(45.78166386726485, 4.872752178696828, new ShowResultTakePlaceCallback()).execute();
        PlaceServices.getInstance().takePlace(45.78166386726485, 4.872752178696828, new ShowResultTakePlaceCallback()).execute();
        PlaceServices.getInstance().releasePlace(45.78166386726485, 4.872752178696828, new ShowResultReleasePlaceCallback()).execute();
        PlaceServices.getInstance().releasePlace(45.78166386726485, 4.872752178696828, new ShowResultReleasePlaceCallback()).execute();
        //PlaceServices.getInstance().takePlace(45.78166386726485, 4.872752178696828, new ShowResultTakePlaceCallback()).execute();

        ZoneServices.getInstance().getZoneById(12,new ShowZoneCallback()).execute();
        ZoneServices.getInstance().indicateDensity(45.78166386726485, 4.872752178696828, Density.MEDIUM, new IndicateCallback()).execute();
        ZoneServices.getInstance().getListZonesByPosition(45.78166386726485, 4.872752178696828,5,  new ShowListZoneCallback()).execute();

        UserServices.getInstance().signup("myname", "mypsw", new ResSignupCallback()).execute();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_place_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_place_information, container, false);
            return rootView;
        }
    }

    /*Method for displaying a place received by a service*/
    private class ShowPlaceCallback extends GetPlaceByIdCallback {
        protected void callback(Exception e, Place place){
            TextView placeIdText = (TextView) findViewById(R.id.id_place_value);
            if(e != null || place == null) {
                Log.e("MainActivity", e.getMessage(), e);
                placeIdText.setText("Une erreur est survenu");
            }
            else{
                placeIdText.setText(place.getId());
                Log.i("maPlace", "id:" + place.getId());
            }
        }
    }

    /*Method for displaying a place received by a service*/
    private class ShowListPlacesCallback extends GetListPlacesByPositionCallback {
        protected void callback(Exception e, Place[] places){
            if(e != null || places == null) {
                Log.e("MainActivity", e.getMessage(), e);
                Log.e("erreur", "Une erreur est survenu");
            }
            else{
                Log.i("listPlaces", "listPlaces");
                for(int i=0; i<places.length; i++){
                    Log.i("place", "lat: " + places[i].getLatitude() + " lng: " + places[i].getLongitude());
                }
            }
        }
    }

    /*Method for displaying a place received by a service*/
    private class ShowResultReleasePlaceCallback extends ReleasePlaceCallback {
        protected void callback(Exception e, Place place){
            if(e != null || place == null) {
                Log.e("MainActivity", e.getMessage(), e);
                Log.e("erreur", "Une erreur est survenu release");
            }
            else{
                Log.i("message", "Superman release" + place.getId());
            }
        }
    }

    /*Method for displaying a place received by a service*/
    private class ShowResultTakePlaceCallback extends TakePlaceCallback {
        protected void callback(Exception e, Place place){
            if(e != null || place == null) {
                Log.e("MainActivity", e.getMessage(), e);
                Log.e("erreur", "Une erreur est survenu take");
            }
            else{
                Log.i("message", "Superman take " + place.getId());
            }
        }
    }

    private class ShowZoneCallback extends GetZoneByIdCallback{

        @Override
        protected void callback(Exception e, Zone zone) {
            if(e != null || zone == null) {
                Log.e("MainActivity", e.getMessage(), e);
                Log.e("erreur", "Une erreur est survenu getZone");
            }
            else {
                Log.i("idZone", "id:" + zone.getId() + " density:" + zone.getDensity() + " intensity:" + zone.getIntensity());
            }
        }
    }

    private class IndicateCallback extends IndicateDensityCallback {
        @Override
        protected void callback(Exception e, Zone zone) {
            if(e != null || zone == null) {
                Log.e("MainActivity", e.getMessage(), e);
                Log.e("erreur", "Une erreur est survenu indicate");
            }
            else {
                Log.i("maZone", "id:" + zone.getId() + " density:" + zone.getDensity() + " intensity:" + zone.getIntensity());
            }
        }
    }

    private class ShowListZoneCallback extends GetListZonesByPositionCallback{
        @Override
        protected void callback(Exception e, Zone[] zones) {
            if(e != null || zones == null) {
                Log.e("MainActivity", e.getMessage(), e);
                Log.e("erreur", "Une erreur est survenu listZone");
            }
            else {
                Log.i("listZones", "listZones");
                for (int i = 0; i < zones.length; i++) {
                    Log.i("zone", "id:" + zones[i].getId() + " density:" + zones[i].getDensity() + " intensity:" + zones[i].getIntensity());
                }
            }
        }
    }

    private class ResAuthentificateCallback extends AuthentificateCallback {
        @Override
        protected void callback(Exception e) {
            if(e != null) {
                Log.e("MainActivity", e.getMessage(), e);
                Log.e("erreur", "Une erreur est survenu listZone");
            }
            else{
                Log.i("authOK", "Authentification OK");
            }
        }
    }

    private class ResSignupCallback extends SignupCallback{
        @Override
        protected void callback(Exception e, String message) {
            if(e != null || message == null) {
                Log.e("MainActivity", e.getMessage(), e);
                Log.e("erreur", "Une erreur est survenu signup");
            }
            else {
                Log.i("message", message);
            }
        }
    }
}
