package api;



/**
 * Created by Gaetan on 25/02/2015.
 */
public class Position{
    protected double longitude;
    protected double latitude;

    public Position(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
