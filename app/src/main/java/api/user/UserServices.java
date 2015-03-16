package api.user;

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
import api.favorite.Favorite;

/**
 * Created by Gaetan on 09/03/2015.
 */
public class UserServices {
    private static UserServices ourInstance = new UserServices();

    public static UserServices getInstance() {
        return ourInstance;
    }
    private UserServices() {
    }

    /*
    * Service creating a new user
    */
    public AsyncTask<Void, Void, Pair<Exception, String>> signup(final String username, final String password, final SignupCallback cb){

        class Signup extends AsyncTask<Void, Void, Pair<Exception, String>> {
            @Override
            protected Pair<Exception, String> doInBackground(Void... params) {
                try {
                    final String url = ApiConfig.usersRoutes + "/signup";

                    HttpHeaders requestHeaders = new HttpHeaders();
                    HttpEntity<User> requestEntity = new HttpEntity<>(new User(username, password), requestHeaders);

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
                    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

                    ResponseEntity<StatusMessage> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, StatusMessage.class);
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

        return new Signup();
    }

    /*
    * Service get information about the user
    */
    public AsyncTask<Void, Void, Pair<Exception, User>> getUser(final GetUserCallback cb){

        class GetUser extends AsyncTask<Void, Void, Pair<Exception, User>> {
            @Override
            protected Pair<Exception, User> doInBackground(Void... params) {
                try {
                    final String url = ApiConfig.usersRoutes + "";

                    HttpHeaders requestHeaders = new HttpHeaders();
                    AuthentificationServices.getInstance().addAuthorization(requestHeaders);
                    HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    ResponseEntity<User> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, User.class);
                    return new Pair<>(null, responseEntity.getBody());

                } catch (Exception e) {
                    return new Pair<>(e, null);
                }
            }
            @Override
            protected void onPostExecute(Pair<Exception, User> resRequest) {
                if(cb != null) cb.callback(resRequest.first, resRequest.second);
            }
        }

        return new GetUser();
    }
}
