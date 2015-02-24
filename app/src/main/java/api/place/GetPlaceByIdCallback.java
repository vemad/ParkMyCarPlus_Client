package api.place;

/**
 * Callback abstract class for the result of the method getPlaceById
 * Created by Gaetan on 18/02/2015.
 */
public abstract class GetPlaceByIdCallback{
    protected abstract void callback(Exception e, Place place);
}
