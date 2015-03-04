package api.zone;

/**
 * Callback abstract class for the result of the method indiacteDensity
 * Created by Iler on 04/03/2015.
 */
public abstract class IndicateDensityCallback{
        protected abstract void callback(Exception e, Zone zone);
}