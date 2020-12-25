package data;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.sql.*;
import java.text.SimpleDateFormat;

public class Slp {


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

    public Integer selectLastEntry(){
        String sql = "SELECT * FROM slp ORDER BY id DESC LIMIT 1";
        Integer last_id = null;


        try {
            Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                last_id = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return last_id;
    }

    public void insertSlp(String slpHash) {
        String sql = "INSERT INTO slp(slp_hash, date_added) VALUES(?,?)";
        // format date for sqlite3
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String ts = sdf.format(timestamp);

        try{
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

    public String slpCheckSum(MessageDigest digest, String fileDirectory) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(fileDirectory);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }
}
