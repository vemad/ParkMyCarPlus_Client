package api.authentification;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Gaetan on 05/03/2015.
 */

public class AuthentificationResponse {

    private String access_token;

    @JsonProperty("token_type")
    private String token_type;
    @JsonProperty("refresh_token")
    private String refresh_token;
    @JsonProperty("expires_in")
    private int expires_in;
    @JsonProperty("scope")
    private String scope;

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return "AuthentificationResponse{" +
                "access_token='" + access_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", expires_in=" + expires_in +
                ", scope='" + scope + '\'' +
                '}';
    }
}
