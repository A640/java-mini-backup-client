package GUI.Controllers.upload;

import GUI.Controllers.MainView;
import javafx.event.ActionEvent;

public class Finish {
    public void resetUpload(ActionEvent actionEvent) {
        FileSelect.getInstance().clear();
        MainView.getInstance().setUploadStep(MainView.UploadStep.CREATE_BACKUP);
    }
}
