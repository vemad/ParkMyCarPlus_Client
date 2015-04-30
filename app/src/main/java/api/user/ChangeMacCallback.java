package api.user;

/**
 * Created by Romain on 29/04/2015.
 */
public abstract class ChangeMacCallback {
    protected abstract void callback(Exception e, String message);
}