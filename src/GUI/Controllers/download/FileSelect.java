package GUI.Controllers.download;

import Backend.DownloadManager;
import Backend.UploadManager;
import GUI.Controllers.MainView;
import GUI.Controllers.SceneManager;
import common.BFile;
import common.Backup;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileSelect {
    private static FileSelect instance;

    @FXML
    private BorderPane blPane;
    @FXML
    private ListView<BFile> dList; // graphical list of files to upload

    public ObservableList<BFile> filesToDownload;
    private ExecutorService transfers;

    public FileSelect(){
        this.transfers = Executors.newFixedThreadPool(1);
    }



    public static FileSelect getInstance(){
        return instance;
    }

    public void initialize() {


        dList = new ListView<BFile>(DownloadManager.getInstance().filesToDownload);
        filesToDownload = FXCollections.observableArrayList();

        dList.setCellFactory(CheckBoxListCell.forListView(new Callback<BFile, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(BFile item) {
                BooleanProperty observable = new SimpleBooleanProperty(true);
                observable.addListener((obs, wasSelected, isNowSelected) -> {

                    if(isNowSelected){
                        filesToDownload.add(item);
                    }
                    else{
                        filesToDownload.remove(item);
                    }
                });
                return observable ;
            }
        }));


        // ### add graphical file list to interface ###
        blPane.setCenter(dList);



        instance = this;
    }

    public void selectBackupDownloadLocation(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(SceneManager.getInstance().getPrimaryStage());

        if(selectedDirectory == null){
            //No Directory selected
        }else{
            System.out.println(selectedDirectory.getAbsolutePath());
            Path path = Paths.get(selectedDirectory.getPath(),"/"+
                    DownloadManager.getInstance().selectedBackup.name + " - " +
                    DownloadManager.getInstance().selectedBackup.created+"/");
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            DownloadManager.getInstance().path = path;
            MainView.getInstance().setDownloadStep(MainView.DownloadStep.DOWNLOAD);
            transfers.execute(DownloadManager.getInstance());
        }
    }

    public void back(ActionEvent actionEvent) {
        MainView.getInstance().setDownloadStep(MainView.DownloadStep.SELECT_BACKUP);
    }

    public void FilesToDownloadInit(){
        this.filesToDownload.clear();
        this.filesToDownload.addAll(DownloadManager.getInstance().filesToDownload);
    }

    public void clear(){
        this.filesToDownload.clear();
        DownloadManager.getInstance().filesToDownload.clear();
        DownloadManager.getInstance().selectedBackup = new Backup(-1, "", "", new Date(), new LinkedList<BFile>());
    }
}
