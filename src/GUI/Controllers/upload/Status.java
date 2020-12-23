package GUI.Controllers.upload;

import Backend.UploadManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class Status {
    @FXML
    private ProgressBar uploadProgress;
    @FXML
    private Label uploadProgressLabel1;
    @FXML
    private Label uploadProgressLabel2;

    public void initialize() {
        this.uploadProgress.progressProperty().bind(UploadManager.getInstance().getProgress());
        this.uploadProgressLabel1.textProperty().bind(UploadManager.getInstance().getProgressPercent().asString());
        this.uploadProgressLabel2.textProperty().bind(UploadManager.getInstance().getProgressSize());
    }
}
