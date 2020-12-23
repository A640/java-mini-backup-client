package Backend;

import GUI.Controllers.MainView;
import GUI.Controllers.SceneManager;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.LongBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class UploadManager extends Thread{
    private static UploadManager instance = null;

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
    private ObservableList<File> files;


    private UploadManager(){
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
    }

    @Override
    public void run() {
        super.run();
        this.progress.setValue(0);
        startUpload();
        return;
    }

    public void startUpload() {

        if(this.backupID != -1){

            //get connection info
            String host = MainConnection.getInstance().getHost();
            int port = MainConnection.getInstance().getPort();
            String login = MainConnection.getInstance().getuLogin();
            String password = MainConnection.getInstance().getuPassword();


            this.current.setValue(0);
            this.progress.setValue(0);
            this.progressPercent.setValue(0);
            long maxTemp = 0;
            CountDownLatch cl = new CountDownLatch(files.size());
            for(File file : files){
                maxTemp += file.length();
                this.max.setValue(maxTemp);
                TransferThread transfer = new TransferThread(TransferThread.Direction.UPLOAD, file, host, port,login,password,this.backupID,cl);
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
                    MainView.getInstance().setUploadStep(MainView.UploadStep.COMPLETE);
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


    public static UploadManager getInstance(){
        if(instance == null){
            instance = new UploadManager();
        }

        return instance;
    }


    public DoubleProperty getCurrent() {
        return this.current;
    }

    public DoubleProperty getMax() {
        return this.max;
    }

    public void updateUploadProgress(long update){

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

    public void setFiles(ObservableList<File> files) {
        this.files = files;
    }
}
