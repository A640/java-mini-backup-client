package GUI.Controllers.upload;

import Backend.MainConnection;
import GUI.Controllers.MainView;
import GUI.Controllers.SceneManager;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class createBackup {
    public TextField backup_name;
    public TextArea backup_description;

    public void createBackup(ActionEvent actionEvent) {
        boolean newBackupCreated = false;
        newBackupCreated = MainConnection.getInstance().createBackup(backup_name.getText(),backup_description.getText());
        if(newBackupCreated) {
            MainView.getInstance().setUploadStep(MainView.UploadStep.SELECT_FILES);
            this.backup_name.clear();
            this.backup_description.clear();
        }
    }
}
