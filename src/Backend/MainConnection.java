package Backend;

import GUI.Controllers.SceneManager;
import javafx.scene.control.ProgressBar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;


public class MainConnection {

    private Socket socket;

    private DataOutputStream send;
    private DataInputStream receive;

    private String host;
    private int port;
    private String uLogin;
    private String uPassword;




    private static MainConnection instance = null;


    private MainConnection() {}

    public static MainConnection getInstance(){
        if(instance == null){
            instance = new MainConnection();
        }

        return instance;
    }

    public boolean connectTo(String address, int port){
        boolean result = false;
        try{
            //establish connection with serverSocket
            this.socket = new Socket(address, port);
            this.host = address;
            this.port = port;

            //create output stream for sending data to server
            send = new DataOutputStream(socket.getOutputStream());

            //create input stream for reading response from server
            receive = new DataInputStream(socket.getInputStream());

            result = true;
        } catch (UnknownHostException e) {
            SceneManager.getInstance().error("Connect to server",
                    "Cannot connect to server. No working server on provided host. Check address and try again");
        } catch (IOException e) {
            SceneManager.getInstance().error("Connect to server",
                    "Problem occurred while connecting to server.", e.toString());
        }

        return result;
    }

    public boolean login(String login, String password){
        boolean result = false;
        try {
            send.writeUTF("login");
            send.writeUTF(login);
            send.writeUTF(password);
            send.flush();
        }catch (Exception e){
            SceneManager.getInstance().error("Connection error", "An connection error occurred while trying to log in.", e.toString());
        }
        try{
            if(receive.readUTF().equals("login") ){
                if(receive.readBoolean() == true){
                    result = true;
                    this.uLogin = login;
                    this.uPassword = password;
                }
                else {
                    result = false;
                    SceneManager.getInstance().warning("Login", "Bad login credentials",
                            "Provided login or password is incorrect. Check then and try again");
                }
            }
        } catch (IOException e) {
            SceneManager.getInstance().error("Connection error", "An connection error occurred while trying to log in.", e.toString());
        }
        return result;
    }

    public boolean register(String name, String surname, String login, String password){
        boolean result = false;
        try {
            send.writeUTF("register");
            send.writeUTF(name);
            send.writeUTF(surname);
            send.writeUTF(login);
            send.writeUTF(password);
            send.flush();
        }catch (Exception e){
            SceneManager.getInstance().error("Connection error", "An connection error occurred while trying to register.", e.toString());
        }
        try{
            if(receive.readUTF().equals("register") ){
                if(receive.readBoolean() == true){
                    result = true;
                }
                else {
                    result = false;
                }
            }
        } catch (IOException e) {
            SceneManager.getInstance().error("Connection error", "An connection error occurred while trying to register.", e.toString());
        }
        return result;
    }

    public boolean createBackup(String name, String description){

        boolean result = false;

        try {
            send.writeUTF("createBackup");
            send.writeUTF(name);
            send.writeUTF(description);
            send.flush();
        }catch (Exception e){
            SceneManager.getInstance().error("Connection error", "An connection error occurred while " +
                    "trying to create new backup.", e.toString());
        }

        int backupID = -1;
        UploadManager.getInstance().clear();

        try{
            backupID = receive.readInt();
        } catch (IOException e) {
            SceneManager.getInstance().error("Connection error", "An connection error occurred while " +
                    "trying to get backup id.", e.toString());
        }

        UploadManager.getInstance().setBackupID(backupID);

        if (backupID != -1){
            result = true;
        }
        else{
            result = false;
        }

        return result;

    }


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getuLogin() {
        return uLogin;
    }

    public String getuPassword() {
        return uPassword;
    }
}
