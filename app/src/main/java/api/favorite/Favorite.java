package api.favorite;

import api.Density;

/**
 * Created by Gaetan on 09/03/2015.
 */
public class Favorite {

    private int id;
    protected double latitude;
    protected double longitude;
    protected String address;

    protected Density density;
    protected float intensity;

    public Favorite(){}

    public Favorite(double latitude, double longitude, String address) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.density = null;
        this.intensity = 0;
    }
}
