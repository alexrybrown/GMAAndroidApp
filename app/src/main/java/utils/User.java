package utils;

/**
 * Created by alex on 2/4/17.
 */

public class User {
    public long userID;
    public String firstName;
    public String lastName;
    public String username;
    public String email;
    public String password;

    public User(long userID, String firstName, String lastName, String username, String email, String password) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
