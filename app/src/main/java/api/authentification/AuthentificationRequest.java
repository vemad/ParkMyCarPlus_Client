package api.authentification;

/**
 * Created by Gaetan on 05/03/2015.
 */
public class AuthentificationRequest {
    private String username;
    private String password;
    private String scope;
    private String grant_type;

    public AuthentificationRequest(String username, String password, String scope, String grant_type) {
        this.username = username;
        this.password = password;
        this.scope = scope;
        this.grant_type = grant_type;
    }

    @Override
    public String toString() {
        return "username=" + this.username + "&password=" + this.password + "&scope=" + this.scope + "&grant_type=" + grant_type;
    }
}
