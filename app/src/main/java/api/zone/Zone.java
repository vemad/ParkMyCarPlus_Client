package api.zone;

/**
 * Created by Iler on 04/03/2015.
 */

public class Zone {

    private String id;
    protected double longitude;
    protected double latitude;
    protected Density density;

    public Zone(){}

    public Zone(double latitude, double longitude, Density density) {
        this.id = null;
        this.longitude = longitude;
        this.latitude = latitude;
        this.density = density;
    }

    public String getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public Density getDensity() {
        return density;
    }
}
