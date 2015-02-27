package api.place;

/**
 *  Callback abstract class for the result of the method releasePlace
 * Created by Gaetan on 24/02/2015.
 */
public abstract class ReleasePlaceCallback {
    protected abstract void callback(Exception e, String message);
}
