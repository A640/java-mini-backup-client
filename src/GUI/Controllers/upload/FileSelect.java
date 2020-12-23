package GUI.Controllers.upload;

import Backend.UploadManager;
import GUI.Controllers.MainView;
import GUI.Controllers.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileSelect {
    private static FileSelect instance;

    @FXML
    private BorderPane uPane;

    private ListView<File> uList; // graphical list of files to upload
    private ObservableList<File> filesToSend; // list of files to upload
    private ExecutorService transfers;


    public FileSelect(){
        this.transfers = Executors.newFixedThreadPool(1);
    }


    public static FileSelect getInstance(){
        return instance;
    }

    public void initialize() {

        filesToSend = FXCollections.observableArrayList();
        uList = new ListView<File>(filesToSend);


        // ### set drag&drop actions for graphical file list (that in interface) ###

        uList.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != uList
                        && event.getDragboard().hasFiles()) {
                    // allow for both copying and moving, whatever user chooses
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });

        uList.setOnDragDropped(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
//                    db.getFiles().forEach(file -> {
//                      if(file.isDirectory()){
//                          try{
//                              Files.walk(Paths.get(file.getPath()));
//                          } catch (IOException e) {
//                              e.printStackTrace();
//                          }
//                      }
//                    });
//                    filesToSend.addAll(db.getFiles());
                    addFiles(db.getFiles().toArray(new File[0]));
                    success = true;
                }
                // let the source know whether the string was successfully
                // transferred and used
                event.setDropCompleted(success);

                event.consume();
            }
        });

        // ### add graphical file list to interface ###
        uPane.setCenter(uList);

        instance = this;
    }




    public void fileChoose(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz pliki do wysłania na serwer");
        List<File> choice = fileChooser.showOpenMultipleDialog(SceneManager.getInstance().getPrimaryStage());
        if(choice != null) {
//            filesToSend.addAll(choice);
            addFiles(choice.toArray(new File[0]));
        }
    }

    public void remove(ActionEvent actionEvent) {
    }

    public void upload(ActionEvent actionEvent) {
        MainView.getInstance().setUploadStep(MainView.UploadStep.UPLOAD);
        UploadManager.getInstance().setFiles(filesToSend);
        transfers.execute(UploadManager.getInstance());
//        clear();

        //UploadManager.getInstance().run();

    }

    private void addFiles(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Directory: " + file.getName());
                addFiles(file.listFiles()); // Calls same method again.
            } else {
                System.out.println("File: " + file.getName());
                this.filesToSend.add(file);
            }
        }
    }

    public void clear(){
        this.filesToSend.clear();
    }
}
