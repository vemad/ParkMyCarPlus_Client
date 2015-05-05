package api.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import api.place.Place;

/**
 * Created by Gaetan on 09/03/2015.
 */
@JsonIgnoreProperties({"accountNonExpired", "credentialsNonExpired"})
public class User {
    protected String id;
    protected String username;
    protected String password;
    protected int score;
    protected Place place;
    protected UserLevel level;
    protected String isPaired;
    protected int confianceScore;
    protected String macAddress;

    public User(){}

    public User(String username, String password) {
        this.id = null;
        this.username = username;
        this.password = password;
        this.isPaired = "";
        this.confianceScore = 10;
        this.macAddress = "";

    }

    public String getId() { return id; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public int getScore() { return score; }

    public int getConfianceScore() { return confianceScore; }

    public Place getPlace() { return place; }

    public UserLevel getLevel(){ return level; }

    public String getMacAddress(){ return macAddress; }
}
