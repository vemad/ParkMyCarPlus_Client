package api.zone;

import android.os.AsyncTask;
import android.util.Pair;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import api.ApiConfig;
import api.Density;
import api.authentification.AuthentificationServices;

/**
 * Created by Iler on 04/03/2015.
 */
public class ZoneServices {
    private static ZoneServices ourInstance = new ZoneServices();

    public static ZoneServices getInstance() {
        return ourInstance;
    }
    private ZoneServices() {
    }

    /*
    * Service to get a zone by its id
    */
    public AsyncTask<Void, Void, Pair<Exception, Zone>> getZoneById(final int id, final GetZoneByIdCallback cb){

        class GetZoneById extends AsyncTask<Void, Void, Pair<Exception, Zone>> {
            @Override
            protected Pair<Exception, Zone> doInBackground(Void... params) {
                try {
                    final String url = ApiConfig.zonesRoutes + "/" + id;

                    HttpHeaders requestHeaders = new HttpHeaders();
                    AuthentificationServices.getInstance().addAuthorization(requestHeaders);
                    HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    ResponseEntity<Zone> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Zone.class);
                    return new Pair<>(null, responseEntity.getBody());

                } catch (Exception e) {
                    return new Pair<>(e, null);
                }
            }
            @Override
            protected void onPostExecute(Pair<Exception, Zone> resRequest) {
                if(cb != null) cb.callback(resRequest.first, resRequest.second);
            }
        }

        return new GetZoneById();
    }

    /*
    * Service to get the list of zone around a position
    */
    public AsyncTask<Void, Void, Pair<Exception, Zone[]>> getListZonesByPosition(final double latitude, final double longitude, final int radius, final GetListZonesByPositionCallback cb){

        class GetListZonesByPosition extends AsyncTask<Void, Void, Pair<Exception, Zone[]>> {
            @Override
            protected Pair<Exception, Zone[]> doInBackground(Void... params) {
                try {
                    final String url = ApiConfig.zonesRoutes + "?longitude=" + longitude + "&latitude=" + latitude + "&radius=" + radius;

                    HttpHeaders requestHeaders = new HttpHeaders();
                    AuthentificationServices.getInstance().addAuthorization(requestHeaders);
                    HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    ResponseEntity<Zone[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Zone[].class);
                    return new Pair<>(null, responseEntity.getBody());

                } catch (Exception e) {
                    return new Pair<>(e, null);
                }
            }
            @Override
            protected void onPostExecute(Pair<Exception, Zone[]> resRequest) {
                if(cb != null) cb.callback(resRequest.first, resRequest.second);
            }
        }

        return new GetListZonesByPosition();
    }

    /*
    * Service indicating that the user give information about the state of a zone
    */
    public AsyncTask<Void, Void, Pair<Exception, Zone>> indicateDensity(final double latitude, final double longitude, final Density density, final IndicateDensityCallback cb){

        class IndicateDensity extends AsyncTask<Void, Void, Pair<Exception, Zone>> {
            @Override
            protected Pair<Exception, Zone> doInBackground(Void... params) {
                try {
                    final String url = ApiConfig.zonesRoutes + "/indicate";

                    HttpHeaders requestHeaders = new HttpHeaders();
                    AuthentificationServices.getInstance().addAuthorization(requestHeaders);
                    HttpEntity<Zone> requestEntity = new HttpEntity<>(new Zone(latitude, longitude, density), requestHeaders);

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    ResponseEntity<Zone> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Zone.class);
                    return new Pair<>(null, responseEntity.getBody());

                } catch (Exception e) {
                    return new Pair<>(e, null);
                }
            }
            @Override
            protected void onPostExecute(Pair<Exception, Zone> resRequest) {
                if(cb != null) cb.callback(resRequest.first, resRequest.second);
            }
        }

        return new IndicateDensity();
    }
}
