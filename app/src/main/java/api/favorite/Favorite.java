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

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public Density getDensity() {
        return density;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setDensity(Density density) {
        this.density = density;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
