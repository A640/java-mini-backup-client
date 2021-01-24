package GUI.Controllers.download;

import GUI.Controllers.MainView;
import GUI.Controllers.download.FileSelect;
import javafx.event.ActionEvent;

public class Finish {
    public void resetDownload(ActionEvent actionEvent) {
        FileSelect.getInstance().clear();
        MainView.getInstance().setDownloadStep(MainView.DownloadStep.SELECT_BACKUP);
    }
}
