package api;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;

public class ApiConfig {
    //For use with the server on the same computer
    public static final String baseUrl = "http://10.0.2.2:8080";

    //For use with the DigitalOcean server
    //public static final String baseUrl = "http://178.62.174.107:8080";


    /* Authentification */
    public static final HttpAuthentication authHeader = new HttpBasicAuthentication("pmcAndroid", "123456");
    public static final String defaultScope = "read write";
    public static final String defaultGrant_type = "password";


    /* Routes url */
    public static final String authRoutes = baseUrl + "/oauth";
    public static final String placesRoutes = baseUrl + "/rest/places";
    public static final String zonesRoutes = baseUrl + "/rest/zones";
    public static final String usersRoutes = baseUrl + "/rest/users";
    public static final String favoritesRoutes = baseUrl + "/rest/favorites";



}