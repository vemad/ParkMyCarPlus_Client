package api.user;

/**
 * Created by Gaetan on 16/03/2015.
 */
public class UserLevel {
     private int startScore;
     private String levelName;
     private String nextLevelName;
     private int nextLevelScore;

    public UserLevel() {}

    public String getLevelName() { return levelName; }

    public int getStartScore() { return startScore; }

    public String getNextLevelName() { return nextLevelName; }

    public int getNextLevelScore() { return nextLevelScore; }
}
