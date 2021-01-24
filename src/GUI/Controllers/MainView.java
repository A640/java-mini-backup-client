package GUI.Controllers;

import Backend.MainConnection;
import common.Backup;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.List;

public class MainView {
    private static MainView instance = null;

    @FXML
    private Tab download_tab;
    @FXML
    private Tab upload_tab;
    @FXML
    private Circle statusC;

    private Node[] uploadStep; // stores steps for uploading files to server
    private Node[] downloadStep; // stores steps for downloading backups from server

    public enum UploadStep {
        CREATE_BACKUP,
        SELECT_FILES,
        UPLOAD,
        COMPLETE,
    }

    public enum DownloadStep {
        SELECT_BACKUP,
        SELECT_FILES,
        DOWNLOAD,
        COMPLETE,
    }

    public MainView(){
        uploadStep = new Node[4];
        downloadStep = new Node[4];
        loadViews();
        instance = this;
    }

    public void initialize() {
//        upload_tab.setContent(uploadStep[UploadStep.CREATE_BACKUP.ordinal()]);
        setUploadStep(UploadStep.CREATE_BACKUP);
        setDownloadStep(DownloadStep.SELECT_BACKUP);

        download_tab.setOnSelectionChanged(event -> {
            Platform.runLater(() -> {
                List<Backup> backupsTemp = MainConnection.getInstance().getBackups();
                System.out.println("GOt backups");
            });
        });


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
        AnchorPane downloadBackupListScreen = null;
        AnchorPane downloadFileSelectScreen = null;
        AnchorPane downloadStatusScreen = null;
        AnchorPane downloadFinishScreen = null;
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

            FXMLLoader downloadBackupLoader = new FXMLLoader(getClass().getClassLoader().getResource("GUI/View/downloadBackupList.fxml"));
            downloadBackupListScreen = (AnchorPane) downloadBackupLoader.load();

            FXMLLoader downloadFileSelectLoader = new FXMLLoader(getClass().getClassLoader().getResource("GUI/View/downloadFileSelect.fxml"));
            downloadFileSelectScreen = (AnchorPane) downloadFileSelectLoader.load();

            FXMLLoader downloadStatusLoader = new FXMLLoader(getClass().getClassLoader().getResource("GUI/View/downloadStatus.fxml"));
            downloadStatusScreen = (AnchorPane) downloadStatusLoader.load();

            FXMLLoader downloadFinishLoader = new FXMLLoader(getClass().getClassLoader().getResource("GUI/View/downloadFinish.fxml"));
            downloadFinishScreen = (AnchorPane) downloadFinishLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //uPane.setCenter(uList);

        this.uploadStep[0] = createBackupScreen;
        this.uploadStep[1] = uploadFileSelectScreen;
        this.uploadStep[2] = uploadStatusScreen;
        this.uploadStep[3] = uploadFinishScreen;

        this.downloadStep[0] = downloadBackupListScreen;
        this.downloadStep[1] = downloadFileSelectScreen;
        this.downloadStep[2] = downloadStatusScreen;
        this.downloadStep[3] = downloadFinishScreen;


    }

    public void setUploadStep(MainView.UploadStep name) {
        upload_tab.setContent(uploadStep[name.ordinal()]);
    }

    public void setDownloadStep(MainView.DownloadStep name) {
        download_tab.setContent(downloadStep[name.ordinal()]);
    }
}