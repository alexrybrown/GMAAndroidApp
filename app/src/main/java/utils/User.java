package utils;

public class User {
    public int userID;
    public String firstName;
    public String lastName;
    public String username;
    public String email;
    public String token;

    public User(int userID, String firstName, String lastName, String email, String username, String token) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.token = token;
    }

    public User() {}
}
