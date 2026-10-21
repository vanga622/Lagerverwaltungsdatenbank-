package de.beispiel.demo;

public class User {
    private static int counter = 0;
    private int id;
    private final String username;
    private final String password;
    private final option role;

    public User(String username, String password, option role) {
        this.id = counter + 1;
        this.username = username;
        this.password = password;
        this.role = role;
        counter++;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public option getRole() { return role; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role='" + role + "'}";
    }
}
