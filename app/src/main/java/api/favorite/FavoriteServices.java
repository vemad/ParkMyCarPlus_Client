package api.favorite;

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
import api.StatusMessage;
import api.authentification.AuthentificationServices;

/**
 * Created by Gaetan on 09/03/2015.
 */
public class FavoriteServices {
    private static FavoriteServices ourInstance = new FavoriteServices();

    public static FavoriteServices getInstance() {
        return ourInstance;
    }
    private FavoriteServices() {
    }

    /*
    * Service creating a favorite for the user
    */
    public AsyncTask<Void, Void, Pair<Exception, Favorite>> createFavorite(final double latitude, final double longitude, final String address, final CreateFavoriteCallback cb){

        class CreateFavorite extends AsyncTask<Void, Void, Pair<Exception, Favorite>> {
            @Override
            protected Pair<Exception, Favorite> doInBackground(Void... params) {
                try {
                    final String url = ApiConfig.favoritesRoutes + "";

                    HttpHeaders requestHeaders = new HttpHeaders();
                    AuthentificationServices.getInstance().addAuthorization(requestHeaders);
                    HttpEntity<Favorite> requestEntity = new HttpEntity<>(new Favorite(latitude, longitude, address), requestHeaders);

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    ResponseEntity<Favorite> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Favorite.class);
                    return new Pair<>(null, responseEntity.getBody());

                } catch (Exception e) {
                    return new Pair<>(e, null);
                }
            }
            @Override
            protected void onPostExecute(Pair<Exception, Favorite> resRequest) {
                if(cb != null) cb.callback(resRequest.first, resRequest.second);
            }
        }

        return new CreateFavorite();
    }

    /*
    * Service listing favorites of the user
    */
    public AsyncTask<Void, Void, Pair<Exception, Favorite[]>> listFavorites(final ListFavoritesCallback cb){

        class ListFavorites extends AsyncTask<Void, Void, Pair<Exception, Favorite[]>> {
            @Override
            protected Pair<Exception, Favorite[]> doInBackground(Void... params) {
                try {
                    final String url = ApiConfig.favoritesRoutes + "";

                    HttpHeaders requestHeaders = new HttpHeaders();
                    AuthentificationServices.getInstance().addAuthorization(requestHeaders);
                    HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    ResponseEntity<Favorite[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Favorite[].class);
                    return new Pair<>(null, responseEntity.getBody());

                } catch (Exception e) {
                    return new Pair<>(e, null);
                }
            }
            @Override
            protected void onPostExecute(Pair<Exception, Favorite[]> resRequest) {
                if(cb != null) cb.callback(resRequest.first, resRequest.second);
            }
        }

        return new ListFavorites();
    }

    /*
    * Service deleting a favorite of the user
    */
    public AsyncTask<Void, Void, Pair<Exception, String>> deleteFavorite(final int id, final DeleteFavoriteCallback cb){

        class ListFavorites extends AsyncTask<Void, Void, Pair<Exception, String>> {
            @Override
            protected Pair<Exception, String> doInBackground(Void... params) {
                try {
                    final String url = ApiConfig.favoritesRoutes + "/" + id;

                    HttpHeaders requestHeaders = new HttpHeaders();
                    AuthentificationServices.getInstance().addAuthorization(requestHeaders);
                    HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
                    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

                    ResponseEntity<StatusMessage> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, StatusMessage.class);
                    return new Pair<>(null, responseEntity.getBody().getMessage());

                } catch (Exception e) {
                    return new Pair<>(e, null);
                }
            }
            @Override
            protected void onPostExecute(Pair<Exception, String> resRequest) {
                if(cb != null) cb.callback(resRequest.first, resRequest.second);
            }
        }

        return new ListFavorites();
    }
}
