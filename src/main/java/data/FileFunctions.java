package data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileFunctions {
    private Slp slpDbOperations = new Slp();
    private String fileLocation = "src/main/resources/slp/";
    private String fileExtension = ".slp";

    /**
     * Copies a given .slp file to an internal project directory.  After copying the file is hashed and an entry into the
     * slp table in the database occurs for the copied file
     * @param file the directory of the .slp file to be copied
     */
    public void copyFile(File file) {
        //TODO: Add validation to check if the file is a .slp
        //TODO: Move to its own thread
        try {
            int lastId = slpDbOperations.selectLastEntry() + 1; // get the id of the last added slp file to determine new file name
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            String slpChecksum = slpDbOperations.slpCheckSum(messageDigest,fileLocation+lastId+fileExtension);

            if(!slpDbOperations.doesSlpExist(slpChecksum)){
                File dest = new File(fileLocation+lastId+fileExtension);
                Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);  // copy slp file to internal directory
                Thread.sleep(5000);
                slpDbOperations.insertSlp(slpChecksum);
            }

        } catch (IOException | NoSuchAlgorithmException | InterruptedException ex) {
            Logger.getLogger(
                    FileFunctions.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
    }
}
