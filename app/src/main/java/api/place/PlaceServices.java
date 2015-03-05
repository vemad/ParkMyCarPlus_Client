package api.place;

import android.os.AsyncTask;
import android.util.Pair;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;


import api.ApiConfig;
import api.Position;
import api.StatusMessage;
import api.authentification.AuthentificationServices;

/**
 * Singleton proposing services on places
 * Created by0 Gaetan on 18/02/2015.
 */
public class PlaceServices {
    private static PlaceServices ourInstance = new PlaceServices();

    public static PlaceServices getInstance() {
        return ourInstance;
    }
    private PlaceServices() {
    }

    /*
    * Service to get a place by its id
    */
    public AsyncTask<Void, Void, Pair<Exception, Place>> getPlaceById(final int id, final GetPlaceByIdCallback cb){

        class GetPlaceById extends AsyncTask<Void, Void, Pair<Exception, Place>> {
            @Override
            protected Pair<Exception, Place> doInBackground(Void... params) {
                try {
                    final String url = ApiConfig.placesRoutes + "/" + id;

                    HttpHeaders requestHeaders = new HttpHeaders();
                    AuthentificationServices.getInstance().addAuthorization(requestHeaders);
                    HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    ResponseEntity<Place> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Place.class);
                    return new Pair<Exception, Place>(null, responseEntity.getBody());

                } catch (Exception e) {
                    return new Pair<Exception, Place>(e, null);
                }
            }
            @Override
            protected void onPostExecute(Pair<Exception, Place> resRequest) {
                if(cb != null) cb.callback(resRequest.first, resRequest.second);
            }
        }

        return new GetPlaceById();
    }

    /*
    * Service to get the list of place around a position
    */
    public AsyncTask<Void, Void, Pair<Exception, Place[]>> getListPlacesByPosition(final double latitude, final double longitude, final int radius, final GetListPlacesByPositionCallback cb){

        class GetListPlacesByPosition extends AsyncTask<Void, Void, Pair<Exception, Place[]>> {
            @Override
            protected Pair<Exception, Place[]> doInBackground(Void... params) {
                try {
                    final String url = ApiConfig.placesRoutes + "?longitude=" + longitude + "&latitude=" + latitude + "&radius=" + radius;

                    HttpHeaders requestHeaders = new HttpHeaders();
                    AuthentificationServices.getInstance().addAuthorization(requestHeaders);
                    HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    ResponseEntity<Place[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Place[].class);
                    return new Pair<>(null, responseEntity.getBody());

                } catch (Exception e) {
                    return new Pair<>(e, null);
                }
            }
            @Override
            protected void onPostExecute(Pair<Exception, Place[]> resRequest) {
                if(cb != null) cb.callback(resRequest.first, resRequest.second);
            }
        }

        return new GetListPlacesByPosition();
    }

    /*
    * Service indicating that the user take a place
    */
    public AsyncTask<Void, Void, Pair<Exception, Place>> takePlace(final double latitude, final double longitude, final TakePlaceCallback cb){

        class TakePlace extends AsyncTask<Void, Void, Pair<Exception, Place>> {
            @Override
            protected Pair<Exception, Place> doInBackground(Void... params) {
                try {
                    final String url = ApiConfig.placesRoutes + "/taken";

                    HttpHeaders requestHeaders = new HttpHeaders();
                    AuthentificationServices.getInstance().addAuthorization(requestHeaders);
                    HttpEntity<Position> requestEntity = new HttpEntity<Position>(new Position(latitude, longitude), requestHeaders);

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    ResponseEntity<Place> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Place.class);
                    return new Pair<>(null, responseEntity.getBody());

                } catch (Exception e) {
                    return new Pair<>(e, null);
                }
            }
            @Override
            protected void onPostExecute(Pair<Exception, Place> resRequest) {
                if(cb != null) cb.callback(resRequest.first, resRequest.second);
            }
        }

        return new TakePlace();
    }

    /*
    * Service indicating that the user release a place
    */
    public AsyncTask<Void, Void, Pair<Exception, Place>> releasePlace(final double latitude, final double longitude, final ReleasePlaceCallback cb){

        class ReleasePlace extends AsyncTask<Void, Void, Pair<Exception, Place>> {
            @Override
            protected Pair<Exception, Place> doInBackground(Void... params) {
                try {
                    final String url = ApiConfig.placesRoutes + "/released";

                    HttpHeaders requestHeaders = new HttpHeaders();
                    AuthentificationServices.getInstance().addAuthorization(requestHeaders);
                    HttpEntity<Position> requestEntity = new HttpEntity<Position>(new Position(latitude, longitude), requestHeaders);

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    ResponseEntity<Place> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Place.class);
                    return new Pair<>(null, responseEntity.getBody());

                } catch (Exception e) {
                    return new Pair<>(e, null);
                }
            }
            @Override
            protected void onPostExecute(Pair<Exception, Place> resRequest) {
                if(cb != null) cb.callback(resRequest.first, resRequest.second);
            }
        }

        return new ReleasePlace();
    }
}

