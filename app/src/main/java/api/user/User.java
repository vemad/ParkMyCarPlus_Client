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

    public User(){}

    public User(String username, String password) {
        this.id = null;
        this.username = username;
        this.password = password;
        //this.isPaired = "";
    }

    public String getId() { return id; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public int getScore() { return score; }

    public Place getPlace() { return place; }

    public UserLevel getLevel(){ return level; }

    public String getPaired(){ return isPaired; }

    public void setPaired(String isPaired){ this.isPaired = isPaired; }
}
