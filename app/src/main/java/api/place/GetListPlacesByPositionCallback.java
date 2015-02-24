package api.place;

import java.util.List;

/**
 * Callback abstract class for the result of the method getListPlacesByPosition
 * Created by Gaetan on 24/02/2015.
 */
public abstract class GetListPlacesByPositionCallback{
    protected abstract void callback(Exception e, Place[] places);
}