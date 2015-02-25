package api.place;

/**
 * Callback abstract class for the result of the method takePlace
 * Created by Gaetan on 24/02/2015.
 */
public abstract class TakePlaceCallback {
    protected abstract void callback(Exception e, Place place);
}
