package GUI.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SceneManager {
    private static SceneManager instance = null;

    private Stage primaryStage;
    private List<Scene> scenes;

    public enum SceneName {
        SERVER,
        LOGIN,
        REGISTER,
    }

    private SceneManager(){}

    private Scene getScene(SceneName name){
        return scenes.get(name.ordinal());
    }

    public void setScenes() throws IOException {

        scenes = new ArrayList<>();

        Parent chooseServerRoot, loginRoot, registerRoot;

        chooseServerRoot = FXMLLoader.load(getClass().getClassLoader().getResource("GUI/View/ChooseServer.fxml"));
        Scene chooseServer = new Scene(chooseServerRoot, 800, 600);

        loginRoot = FXMLLoader.load(getClass().getClassLoader().getResource("GUI/View/Login.fxml"));
        Scene login = new Scene(loginRoot, 800, 600);

        registerRoot = FXMLLoader.load(getClass().getClassLoader().getResource("GUI/View/Register.fxml"));
        Scene register = new Scene(registerRoot, 800, 600);



        scenes.add(chooseServer);
        scenes.add(login);
        scenes.add(register);
    }

    public static SceneManager getInstance() {
        if(instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
    }
    public void start() {
        primaryStage.setTitle("Backup app");
        primaryStage.setScene(getScene(SceneName.SERVER));
        primaryStage.show();
        primaryStage.setOnCloseRequest(evt -> System.exit(0));
    }
    public void setScene(SceneName name) {
        primaryStage.setScene(getScene(name));
    }

    public void error(String title,String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public void error(String title, String header){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);

        alert.showAndWait();
    }

    public void error(String header){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);

        alert.showAndWait();
    }

    public void warning(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public void warning(String title, String header){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);

        alert.showAndWait();
    }

    public void warning(String header){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(header);

        alert.showAndWait();
    }
}
