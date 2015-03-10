package api.user;

/**
 * Created by Gaetan on 09/03/2015.
 */
public abstract class SignupCallback {
    protected abstract void callback(Exception e, String message);
}
