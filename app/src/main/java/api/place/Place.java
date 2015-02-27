package api.place;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object Place
 * Created by Gaetan on 18/02/2015.
 */
public class Place {
    private String id;
    private Double longitude;
    private Double latitude;

    @JsonProperty("isTaken")
    private boolean isTaken;

    public String getId() {
        return id;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public boolean isTaken() { return isTaken; }
}
