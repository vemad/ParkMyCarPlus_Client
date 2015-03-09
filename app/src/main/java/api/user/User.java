package api.user;

/**
 * Created by Gaetan on 09/03/2015.
 */
public class User {
    protected String id;
    protected String username;
    protected String password;

    public User(){}

    public User(String username, String password) {
        this.id = null;
        this.username = username;
        this.password = password;
    }

    public String getId() { return id; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }
}
