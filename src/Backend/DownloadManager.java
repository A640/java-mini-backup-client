package Backend;

import GUI.Controllers.MainView;
import GUI.Controllers.SceneManager;
import common.BFile;
import common.Backup;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class DownloadManager extends Thread{
    private static DownloadManager instance = null;

    private ExecutorService transfers;
    private final Semaphore mutex;

    private DoubleProperty current;
    private DoubleProperty max;
    private DoubleProperty progress;
    private IntegerProperty progressPercent;
    private StringProperty progressSize;

    private String sizeUnit;
    private int sizeDivider;


    private int backupID;
    public ObservableList<Backup> backups;
    public Backup selectedBackup;
    public ObservableList<BFile> filesToDownload;
    public Path path;


    private DownloadManager(){
        this.transfers = Executors.newFixedThreadPool(5);
        this.mutex = new Semaphore(1);
        this.current = new SimpleDoubleProperty(-1);
        this.max = new SimpleDoubleProperty(-1);
        this.progress = new SimpleDoubleProperty(0);
        this.progressPercent = new SimpleIntegerProperty(0);
        this.progressSize = new SimpleStringProperty(" 0 / 0 MB");
        this.backupID = -1;
        this.sizeUnit = "kB";
        this.sizeDivider = 1024;
        this.backups = FXCollections.observableArrayList();
        this.selectedBackup = new Backup(-1, "", "", new Date(), new LinkedList<BFile>());
        this.filesToDownload = FXCollections.observableArrayList();
    }

    @Override
    public void run() {
        super.run();
        this.progress.setValue(0);
        startDownload();
        return;
    }

    public void startDownload() {

        if(this.selectedBackup.backupId != -1){

            //get connection info
            String host = MainConnection.getInstance().getHost();
            int port = MainConnection.getInstance().getPort();
            String login = MainConnection.getInstance().getuLogin();
            String password = MainConnection.getInstance().getuPassword();


            this.current.setValue(0);
            this.progress.setValue(0);
            this.progressPercent.setValue(0);
            long maxTemp = 0;
            CountDownLatch cl = new CountDownLatch(filesToDownload.size());
            for(BFile file : filesToDownload){
                maxTemp += file.size;
                this.max.setValue(maxTemp);
                TransferThread transfer = new TransferThread(TransferThread.Direction.DOWNLOAD, file, path, host,
                        port,login,password,this.selectedBackup.backupId,cl);
                transfers.execute(transfer);
            }

            if(maxTemp < 1048576){
                this.sizeUnit = "kB";
                this.sizeDivider = 1024;
            } else if(maxTemp < 1073741824){
                this.sizeUnit = "MB";
                this.sizeDivider = 1048576;
            } else{
                this.sizeUnit = "GB";
                this.sizeDivider = 1073741824;
            }


            try {
                cl.await();
                Platform.runLater(() ->{
                    MainView.getInstance().setDownloadStep(MainView.DownloadStep.COMPLETE);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



        }
        else{
            SceneManager.getInstance().error("UploadManager problem",
                    "Incorrect backup ID");
            MainView.getInstance().setUploadStep(MainView.UploadStep.CREATE_BACKUP);
        }
        return;
    }


    public static DownloadManager getInstance(){
        if(instance == null){
            instance = new DownloadManager();
        }

        return instance;
    }


    public DoubleProperty getCurrent() {
        return this.current;
    }

    public DoubleProperty getMax() {
        return this.max;
    }

    public void updateDownloadProgress(long update){

        try {
            this.mutex.acquire();
            this.current.setValue(current.doubleValue() + update);
            //this.updateProgress(current.longValue(),max.longValue());
            double tempProgress = current.doubleValue() / max.doubleValue();
            this.progress.setValue(tempProgress);
            this.progressPercent.setValue(Math.ceil(tempProgress * 100));
            this.progressSize.setValue(String.format("%.2f",this.current.doubleValue() / this.sizeDivider) + " / " +
                    String.format("%.2f",this.max.doubleValue() / this.sizeDivider) + " " + this.sizeUnit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.mutex.release();
        }
    }


    public void setBackupID(int backupID) {
        this.backupID = backupID;
    }

    public void clear(){
        this.backupID = -1;
    }

    public DoubleProperty getProgress(){
        return this.progress;
    }

    public IntegerProperty getProgressPercent(){
        return this.progressPercent;
    }

    public StringProperty getProgressSize(){
        return this.progressSize;
    }

//    public void setFiles(ObservableList<File> files) {
//        this.files = files;
//    }
}
