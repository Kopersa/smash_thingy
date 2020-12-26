package data;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.sql.*;
import java.text.SimpleDateFormat;

public class Slp {

    /**
     * establishes a connection to the SQLITE3 database, to be used when preforming CRUD operations
     * @return connection to the database
     */
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:src/main/resources/stats.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * selects the last entry in the slp table, intended to be used to determine the name for a newly copied .slp file
     * @return the id of the last entry in the slp table
     */
    public Integer selectLastEntry() {
        String sql = "SELECT * FROM slp ORDER BY id DESC LIMIT 1";
        Integer last_id = null;


        try {
            Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                last_id = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return last_id;
    }

    /**
     * inserts an entry into the slp table, logging an instance of a .slp file that has been copied to internal directory
     * @param slpHash the md5 hash of a given .slp file
     */

    public void insertSlp(String slpHash) {
        String sql = "INSERT INTO slp(slp_hash, date_added) VALUES(?,?)";
        // format date for sqlite3
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String ts = sdf.format(timestamp);

        try {
            Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            // map values to insert statement
            pstmt.setString(1, slpHash);
            pstmt.setString(2, ts);
            System.out.println(pstmt.executeUpdate());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * generates a checksum when given a hashing algorithm and a file to be hashed
     *
     * @param digest        provides applications the functionality of a message digest algorithm, such as SHA-1 or SHA-256
     * @param fileDirectory the file location of the .slp file to be hashed
     * @return checksum the md5 checksum of the .slp file
     * @throws IOException
     */
    public String slpCheckSum(MessageDigest digest, String fileDirectory) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(fileDirectory);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

    /**
     * searches the db for an entry with a given hash, if it exists return true if not return false
     *
     * @param checksum the checksum to be searched to see if it already exists
     * @return doesSlpExist true of false depending on if this checksum is present in db
     */
    public boolean doesSlpExist(String checksum) {

        String sql = "SELECT id,slp_hash"
                + " FROM slp WHERE slp_hash = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setString(1, checksum);
            //
            ResultSet rs = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id"));
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
