package GUI.Controllers.download;

import Backend.DownloadManager;
import GUI.Controllers.MainView;
import common.Backup;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

public class BackupList {
    private static BackupList instance;

    @FXML
    private BorderPane blPane;
    @FXML
    private ListView<Backup> dList; // graphical list of files to upload
    @FXML
//    private ObservableList<Backup> backups; // list of files to upload


    public static BackupList getInstance(){
        return instance;
    }

    public void initialize() {


        dList = new ListView<Backup>(DownloadManager.getInstance().backups);

        dList.setCellFactory(param -> new ListCell<Backup>() {
            @Override
            protected void updateItem(Backup item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.name == null) {
                    setText(null);
                } else {
                    setText(item.created.toString() + " - " + item.name + " - " + item.description);
                }
            }
        });


        // ### add graphical file list to interface ###
        blPane.setCenter(dList);



        instance = this;
    }

    public void selectBackup(ActionEvent actionEvent) {
        Backup b = dList.getSelectionModel().getSelectedItem();
        DownloadManager.getInstance().selectedBackup = b;
        DownloadManager.getInstance().filesToDownload.clear();
        DownloadManager.getInstance().filesToDownload.addAll(b.files);
        FileSelect.getInstance().FilesToDownloadInit();
        MainView.getInstance().setDownloadStep(MainView.DownloadStep.SELECT_FILES);
    }
}
