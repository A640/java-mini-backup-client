package GUI.Controllers;

import Backend.MainConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ChooseServer {
    @FXML
    private TextField host;
    @FXML
    private TextField port;

    public void connect(ActionEvent actionEvent) {
        boolean connected = false;

        connected = MainConnection.getInstance().connectTo(this.host.getText(),Integer.parseInt(this.port.getText()));
        if (connected){
            SceneManager.getInstance().setScene(SceneManager.SceneName.LOGIN);
        }
    }
}
