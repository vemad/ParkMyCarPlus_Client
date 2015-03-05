package api.authentification;

import android.os.AsyncTask;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import api.ApiConfig;

/**
 * Created by Gaetan on 05/03/2015.
 */
public class AuthentificationServices {

    private AuthentificationResponse token;

    /*Singleton*/
    private static AuthentificationServices ourInstance = new AuthentificationServices();
    public static AuthentificationServices getInstance() {
        return ourInstance;
    }
    private AuthentificationServices() {
        this.token = null;
    }


    /*
    * Service to get a zone by its id
    */
    public AsyncTask<Void, Void, Pair<Exception, AuthentificationResponse>> authentificate(final String username, final String password, final AuthentificateCallback cb){

        class Authentificate extends AsyncTask<Void, Void, Pair<Exception, AuthentificationResponse>> {
            @Override
            protected Pair<Exception, AuthentificationResponse> doInBackground(Void... params) {
                try {
                    final String url = ApiConfig.authRoutes + "/token";

                    //Headers
                    HttpHeaders requestHeaders = new HttpHeaders();
                    requestHeaders.setAuthorization(ApiConfig.authHeader);
                    requestHeaders.add("Content-Type","application/x-www-form-urlencoded");

                    //Request
                    AuthentificationRequest authReq = new AuthentificationRequest(username, password, ApiConfig.defaultScope, ApiConfig.defaultGrant_type);
                    HttpEntity<String> requestEntity = new HttpEntity<String>(authReq.toString(), requestHeaders);

                    //Parsers
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

                    //Request execution
                    ResponseEntity<AuthentificationResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, AuthentificationResponse.class);
                    return new Pair<Exception, AuthentificationResponse>(null, response.getBody());

                } catch (Exception e) {
                    return new Pair<Exception, AuthentificationResponse>(e, null);
                }
            }
            @Override
            protected void onPostExecute(Pair<Exception, AuthentificationResponse> resRequest) {
                if(resRequest.first != null) {
                    if(cb != null) cb.callback(resRequest.first);
                }
                else {
                    if (resRequest.second != null){
                        token = resRequest.second;
                        if(cb != null) cb.callback(null);
                    }
                    else{
                        if(cb != null) cb.callback(new Exception("No token received"));
                    }
                }

            }
        }
        return new Authentificate();
    }

    public HttpHeaders addAuthorization(HttpHeaders requestHeaders) throws NotAuthenticatedException {
        if(this.token == null){
            throw new NotAuthenticatedException();
        }
        requestHeaders.add("Authorization", this.token.getToken_type() + ' ' + this.token.getAccess_token());
        return requestHeaders;
    }
}
