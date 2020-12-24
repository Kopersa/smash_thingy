import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Slp;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public final class HelloFX extends Application {

    private Desktop desktop = Desktop.getDesktop();
    private Slp slpDbOperations = new Slp();

    @Override
    public void start(final Stage stage) {
        stage.setTitle("File Chooser Sample");

        final FileChooser fileChooser = new FileChooser();


        final Button openMultipleButton = new Button("Select slp file(s)");

        openMultipleButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        List<File> list =
                                fileChooser.showOpenMultipleDialog(stage);
                        if (list != null) {
                            for (File file : list) {
                                copyFile(file);
                            }
                        }
                    }
                });


        final GridPane inputGridPane = new GridPane();


        GridPane.setConstraints(openMultipleButton, 1, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(openMultipleButton);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));

        stage.setScene(new Scene(rootGroup));
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);

    }

    private void copyFile(File file) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            int lastId = slpDbOperations.selectLastEntry() + 1; // get the id of the last added slp file to determine new file name
            File dest = new File("src/main/resources/slp/"+lastId);
            Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);  // copy slp file to internal directory
            slpDbOperations.insertSlp(slpDbOperations.slpCheckSum(messageDigest,"src/main/resources/slp/"+lastId));

        } catch (IOException | NoSuchAlgorithmException ex) {
            Logger.getLogger(
                    HelloFX.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
    }
}
