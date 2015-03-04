package api.zone;

import android.os.AsyncTask;
import android.util.Pair;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import api.ApiConfig;
import api.Position;

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
                    final String url = ApiConfig.baseUrl + "/zones/" + id;
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    Zone zone = restTemplate.getForObject(url, Zone.class);
                    return new Pair<Exception, Zone>(null, zone);
                } catch (Exception e) {
                    return new Pair<Exception, Zone>(e, null);
                }
            }
            @Override
            protected void onPostExecute(Pair<Exception, Zone> resRequest) {
                cb.callback(resRequest.first, resRequest.second);
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
                    final String url = ApiConfig.baseUrl + "/zones?longitude=" + longitude + "&latitude=" + latitude + "&radius=" + radius;
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    Zone[] zones = restTemplate.getForObject(url, Zone[].class);
                    return new Pair<>(null, zones);
                } catch (Exception e) {
                    return new Pair<>(e, null);
                }
            }
            @Override
            protected void onPostExecute(Pair<Exception, Zone[]> resRequest) {
                cb.callback(resRequest.first, resRequest.second);
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
                    final String url = ApiConfig.baseUrl + "/zones/indicate";
                    RestTemplate restTemplate = new RestTemplate();

                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    Zone zone = restTemplate.postForObject(url, new Zone(latitude, longitude, density), Zone.class);
                    return new Pair<>(null, zone);
                } catch (Exception e) {
                    return new Pair<>(e, null);
                }
            }
            @Override
            protected void onPostExecute(Pair<Exception, Zone> resRequest) {
                cb.callback(resRequest.first, resRequest.second);
            }
        }

        return new IndicateDensity();
    }
}
