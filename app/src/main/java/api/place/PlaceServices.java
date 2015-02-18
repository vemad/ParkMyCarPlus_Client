package api.place;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Singleton proposing services on places
 * Created by Gaetan on 18/02/2015.
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
    /*TODO: ne fait pas se qu'il faut*/
    public AsyncTask<Void, Void, Place> getPlaceById(final String id, final GetPlaceByIdCallback cb){

        class GetPlaceById extends AsyncTask<Void, Void, Place> {
            @Override
            protected Place doInBackground(Void... params) {
                try {
                    final String url = "http://rest-service.guides.spring.io/greeting";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    Place place = restTemplate.getForObject(url, Place.class);
                    return place;
                } catch (Exception e) {
                    Log.e("MainActivity", e.getMessage(), e);
                }

                return null;
            }
            @Override
            protected void onPostExecute(Place place) {
                cb.callback(place);
            }
        }

        return new GetPlaceById();
    }
}

