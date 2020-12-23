package GUI.Controllers;

import Backend.MainConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Register {
    @FXML
    private TextField name;
    @FXML
    private TextField surname;
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;

    public void register(ActionEvent actionEvent) {
        boolean success = false;
        success = MainConnection.getInstance().register(name.getText(),surname.getText(),login.getText(),password.getText());
        if(success){
            clear();
            SceneManager.getInstance().setScene(SceneManager.SceneName.LOGIN);
        }
    }

    public void cancel(ActionEvent actionEvent) {
        clear();

        //return to login panel
        SceneManager.getInstance().setScene(SceneManager.SceneName.LOGIN);
    }

    private void clear(){
        //clear all values
        this.name.clear();
        this.surname.clear();
        this.login.clear();
        this.password.clear();
    }
}
