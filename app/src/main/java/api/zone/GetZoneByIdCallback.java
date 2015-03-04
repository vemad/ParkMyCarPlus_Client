package api.zone;

/**
 * Callback abstract class for the result of the method getZoneById
 * Created by Iler on 04/03/2015.
 */
public abstract class GetZoneByIdCallback{
    protected abstract void callback(Exception e, Zone zone);
}
