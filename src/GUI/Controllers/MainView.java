package GUI.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.io.IOException;

public class MainView {
    private static MainView instance = null;

    @FXML
    private Tab download_tab;
    @FXML
    private Tab upload_tab;
    @FXML
    private Circle statusC;

    private Node[] uploadStep; // stores steps for uploading files to server

    public enum UploadStep {
        CREATE_BACKUP,
        SELECT_FILES,
        UPLOAD,
        COMPLETE,
    }

    public MainView(){
        uploadStep = new Node[4];
        loadViews();
        instance = this;
    }

    public void initialize() {
        upload_tab.setContent(uploadStep[UploadStep.CREATE_BACKUP.ordinal()]);
        instance = this;
    }

    public static MainView getInstance(){
        return instance;
    }

    private void loadViews(){
        // ############### upload views #################

        AnchorPane createBackupScreen = null;
        AnchorPane uploadFileSelectScreen = null;
        AnchorPane uploadStatusScreen = null;
        AnchorPane uploadFinishScreen = null;
        try {
//            FXMLLoader createBackupLoader = FXMLLoader.load(getClass().getClassLoader().getResource(""));
            FXMLLoader createBackupLoader = new FXMLLoader(getClass().getClassLoader().getResource("GUI/View/createBackup.fxml"));
            createBackupScreen = (AnchorPane) createBackupLoader.load();

            FXMLLoader fileSelectLoader = new FXMLLoader(getClass().getClassLoader().getResource("GUI/View/uploadFileSelect.fxml"));
            uploadFileSelectScreen= (AnchorPane) fileSelectLoader.load();

            FXMLLoader uploadStatusLoader = new FXMLLoader(getClass().getClassLoader().getResource("GUI/View/uploadStatus.fxml"));
            uploadStatusScreen = (AnchorPane) uploadStatusLoader.load();

            FXMLLoader uploadFinishLoader = new FXMLLoader(getClass().getClassLoader().getResource("GUI/View/uploadFinish.fxml"));
            uploadFinishScreen = (AnchorPane) uploadFinishLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //uPane.setCenter(uList);

        this.uploadStep[0] = createBackupScreen;
        this.uploadStep[1] = uploadFileSelectScreen;
        this.uploadStep[2] = uploadStatusScreen;
        this.uploadStep[3] = uploadFinishScreen;


    }

    public void setUploadStep(MainView.UploadStep name) {
       upload_tab.setContent(uploadStep[name.ordinal()]);
    }
}