package data;

import java.sql.*;

public class Database {

    static String urlPrefix = "jdbc:sqlite:src/main/resources/";


    public static void createNewDatabase(String fileName) {

        String url = urlPrefix + fileName;

        try {
            // establish database connection
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createSlpTable() {
        // SQLite connection string
        String url = urlPrefix+"stats.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS slp (\n"
                + " id integer PRIMARY KEY,\n"
                + " slp_hash text NOT NULL,\n"
                + " date_added DATE\n"
                + ");";

        try{
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }






    public static void main(String[] args) {

    }
}
