package GUI.Controllers.download;

import Backend.DownloadManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class Status {
    @FXML
    private ProgressBar downloadProgress;
    @FXML
    private Label downloadProgressLabel1;
    @FXML
    private Label downloadProgressLabel2;

    public void initialize() {
        this.downloadProgress.progressProperty().bind(DownloadManager.getInstance().getProgress());
        this.downloadProgressLabel1.textProperty().bind(DownloadManager.getInstance().getProgressPercent().asString());
        this.downloadProgressLabel2.textProperty().bind(DownloadManager.getInstance().getProgressSize());
    }
}
