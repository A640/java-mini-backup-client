package GUI.Controllers;

import Backend.MainConnection;
import common.Backup;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.List;

public class Login {
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;

    public void login(ActionEvent actionEvent) {
        boolean loggedIn = false;
        loggedIn = MainConnection.getInstance().login(login.getText(),password.getText());
        if(loggedIn){
            SceneManager.getInstance().setScene(SceneManager.SceneName.MAIN);
            Platform.runLater(() -> {
                MainConnection.getInstance().getBackups();
            });
        }
    }


    public void register(ActionEvent actionEvent) {
        SceneManager.getInstance().setScene(SceneManager.SceneName.REGISTER);
    }
}
