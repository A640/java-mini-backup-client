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
    private String host;
    private DataOutputStream send;
    private DataInputStream receive;
    private String uLogin;
    private String uPassword;
    private int backupID;
    private long u1;
    private long u2;
    private ExecutorService transfers;
    ProgressBar uProgress;
    ProgressBar dProgress;
    //private final Semaphore mutex;

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
            socket = new Socket(address, port);
            host = address;

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



}
