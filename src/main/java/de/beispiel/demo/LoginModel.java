package de.beispiel.demo;


import java.sql.*;

public class LoginModel {
    Connection connection;

    public LoginModel() {

        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:sqlite:" + System.getProperty("user.dir") + "/daten.db"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    //kp ob wir das jemal brauchen
    public boolean isDBConnected(){
        return this.connection != null;
    }

    //login bestätigung
    public User isLogin(String user, String password, option opt) throws Exception {

        ResultSet rs = null;

        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";

        PreparedStatement pr = this.connection.prepareStatement(sql);

        try {
            pr.setString(1, user);
            pr.setString(2, password);
            pr.setString(3, opt.toString());

            rs = pr.executeQuery();

            boolean boll1;

            if (rs.next()) {
                return  new User(user, password, opt);
            }
            return null;
        }catch(SQLException e) {
            return null;
        }

        finally {
            {
                pr.close();
                rs.close();
            }
        }
    }
}
