package api.user;

/**
 * Created by Gaetan on 16/03/2015.
 */
public abstract class GetUserCallback {
    protected abstract void callback(Exception e, User user);
}
