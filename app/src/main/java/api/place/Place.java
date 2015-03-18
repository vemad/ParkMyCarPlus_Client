package api.place;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;

import java.sql.Timestamp;

/**
 * Object Place
 * Created by Gaetan on 18/02/2015.
 */
public class Place {
    private String id;
    private Double longitude;
    private Double latitude;
    private DateTime dateLastRelease;
    private DateTime dateLastTake;

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

    public DateTime getDateLastRelease() {
        return dateLastRelease;
    }

    public DateTime getDateLastTake() {
        return dateLastTake;
    }


    public void setDateLastRelease(Timestamp dateLastRelease) {
        if(dateLastRelease != null) this.dateLastRelease = new DateTime(dateLastRelease);
    }

    public void setDateLastTake(Timestamp dateLastTake) {
        if(dateLastTake != null) this.dateLastTake = new DateTime(dateLastTake);
    }
}
