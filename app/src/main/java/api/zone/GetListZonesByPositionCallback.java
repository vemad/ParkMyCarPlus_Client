package api.zone;

/**
 * Callback abstract class for the result of the method getListZonesByPosition
 * Created by Iler on 04/03/2015.
 */
public abstract class GetListZonesByPositionCallback{
    protected abstract void callback(Exception e, Zone[] zones);
}
