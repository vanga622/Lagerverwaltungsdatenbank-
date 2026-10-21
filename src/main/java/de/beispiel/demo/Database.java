package de.beispiel.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String URL = "jdbc:sqlite:daten.db";


    //tabelle anlegen
    public static void init() {
        String sql = """
            CREATE TABLE IF NOT EXISTS produkte (
                id TEXT PRIMARY KEY,
                name TEXT,
                kategorie TEXT,
                lager INTEGER,
                einkauf REAL,
                verkauf REAL,
                verkauft INTEGER DEFAULT 0
            );
        """;


        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        sql = """
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL,
            role TEXT NOT NULL
        );
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Tabelle für verkäufe____________
        sql = """
        CREATE TABLE IF NOT EXISTS sales (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            produkt_id TEXT NOT NULL,
            menge INTEGER NOT NULL,
            datum TEXT NOT NULL,
            FOREIGN KEY (produkt_id) REFERENCES produkte(id)
        );
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        



    }

    // produkt speichern
    public static void saveProdukt(Produkt p) {
        String sql = """
            INSERT INTO produkte (id, name, kategorie, lager, einkauf, verkauf, verkauft)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getId());
            ps.setString(2, p.getName());
            ps.setString(3, p.getKategorie());
            ps.setInt(4, p.getLager());
            ps.setDouble(5, p.getEinkauf());
            ps.setDouble(6, p.getVerkauf());
            ps.setInt(7, p.getVerkauft());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateProdukt(Produkt p) {
        String sql = """
        UPDATE produkte
        SET name = ?, kategorie = ?, lager = ?, einkauf = ?, verkauf = ?, verkauft = ?
        WHERE id = ?
    """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getKategorie());
            ps.setInt(3, p.getLager());
            ps.setDouble(4, p.getEinkauf());
            ps.setDouble(5, p.getVerkauf());
            ps.setInt(6, p.getVerkauft());
            ps.setString(7, p.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    public static void deleteProdukt(Produkt p){
        String sql = """ 
                DELETE FROM produkte 
                WHERE id = ?
                """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    // Produkte laden (z. B. für TableView)
    public static List<Produkt> loadProdukte() {
        List<Produkt> liste = new ArrayList<>();
        String sql = "SELECT * FROM produkte";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Produkt p = new Produkt(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("kategorie"),
                        rs.getInt("lager"),
                        rs.getDouble("einkauf"),
                        rs.getDouble("verkauf"),
                        rs.getInt("verkauft")
                );
                liste.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return liste;
    }


    public static boolean saveUser(String username, String password, option opt) {

        String checkSql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(URL)) {

            // prüfen ob user schon in db ist_____________
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, username);

            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                return false;
            }


            // user speichern______________________

            String insertSql = """
            INSERT INTO users (username, password, role)
            VALUES (?, ?, ?)
            """;

            PreparedStatement insertPs = conn.prepareStatement(insertSql);
            insertPs.setString(1, username);
            insertPs.setString(2, password);
            insertPs.setString(3, opt.toString());

            insertPs.executeUpdate();


            System.out.println("User gespeichert: " + username);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    //um die tabelle als css datei zu exportieren
    public static void exportTable(String pfad) throws SQLException, IOException {

        String sql ="SELECT * FROM produkte";

        try(Connection conn = DriverManager.getConnection(URL);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        FileWriter writer = new FileWriter(pfad);) {

            writer.write("id,name,kategorie,lager,einkauf,verkauf \n");

            while (rs.next()) {
                writer.write(
                        rs.getString("id") + "," +
                            rs.getString("name") + "," +
                            rs.getString("kategorie") + "," +
                            rs.getInt("lager") + "," +
                            rs.getDouble("einkauf") + "," +
                            rs.getDouble("verkauf") + "\n"
                );

            }
        }

    }

    public static boolean sellProdukt(String produktId, int menge){

        String checkSql = "SELECT lager FROM produkte WHERE id = ?";
        String veringerSql = "UPDATE produkte SET lager = lager - ?, verkauft = verkauft + ? WHERE id = ?";
        String einfSql = """
            INSERT INTO sales (produkt_id, menge, datum)
            VALUES (?, ?, datetime('now'))
        """;

        try (Connection conn = DriverManager.getConnection(URL)) {

            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, produktId);

            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) return false;

            int lager = rs.getInt("lager");

            if (lager < menge) return false;


            PreparedStatement veringerPs = conn.prepareStatement(veringerSql);
            veringerPs.setInt(1, menge);
            veringerPs.setInt(2, menge);
            veringerPs.setString(3, produktId);
            veringerPs.executeUpdate();


            PreparedStatement insertPs = conn.prepareStatement(einfSql);
            insertPs.setString(1, produktId);
            insertPs.setInt(2, menge);
            insertPs.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static double getGewinn() {

        String sql = "SELECT einkauf, verkauf, verkauft FROM produkte";
        double gesamt = 0;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                double einkauf = rs.getDouble("einkauf");
                double verkauf = rs.getDouble("verkauf");
                int verkauft = rs.getInt("verkauft");

                gesamt += (verkauf - einkauf) * verkauft;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return gesamt;
    }


    public static boolean produktIdExistiert(String id) {
        String sql = "SELECT 1 FROM produkte WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



}
