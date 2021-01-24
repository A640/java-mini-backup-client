package Backend;

import GUI.Controllers.SceneManager;
import common.BFile;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class TransferThread implements Runnable{

    private File file;
    private BFile dfile;
    private Path path;
    private Socket socket;
    private ObjectOutputStream send;
    private ObjectInputStream receive;
    private String login;
    private String password;
    private int fileID;
    private Direction direction;
    private int backupID;
    private CountDownLatch cl;


    public enum Direction{
        UPLOAD,
        DOWNLOAD,
    }

    public TransferThread(Direction direction, File file, String host, int port, String login, String password,
                          int backupID, CountDownLatch cl) {
        this.direction = direction;
        this.file = file;
        this.login = login;
        this.password = password;
        this.backupID = backupID;
        this.cl = cl;
        try{
            //establish connection with socket
            socket = new Socket(host, port);

            //create output stream for sending data to server
            send = new ObjectOutputStream(socket.getOutputStream());

            //create input stream for reading response from server
            receive = new ObjectInputStream(socket.getInputStream());

        } catch (UnknownHostException e) {
            System.out.println("Nie znaleziono podanego serwera. Sprawdź adres i spróbuj ponownie");
        } catch (IOException e) {
            System.out.println("Wystąpił błąd podczas łączenia z serwerem" + e.toString());
        }
    }

    public TransferThread(Direction direction, BFile file, Path path, String host, int port, String login, String password,
                          int backupID, CountDownLatch cl) {
        this.direction = direction;
        this.dfile = file;
        this.login = login;
        this.password = password;
        this.backupID = backupID;
        this.cl = cl;
        this.path = path;
        try{
            //establish connection with socket
            socket = new Socket(host, port);

            //create output stream for sending data to server
            send = new ObjectOutputStream(socket.getOutputStream());

            //create input stream for reading response from server
            receive = new ObjectInputStream(socket.getInputStream());

        } catch (UnknownHostException e) {
            System.out.println("Nie znaleziono podanego serwera. Sprawdź adres i spróbuj ponownie");
        } catch (IOException e) {
            System.out.println("Wystąpił błąd podczas łączenia z serwerem" + e.toString());
        }
    }

    private boolean authorize(){
        boolean result = false;
        try {
            send.writeUTF("login");
            send.writeUTF(this.login);
            send.writeUTF(this.password);
            send.flush();
        }catch (Exception e){
            System.out.println("Błąd podczas autoryzacji transferu: " + e.toString());
        }
        try{
            if(receive.readUTF().equals("login") ){
                if(receive.readBoolean() == true){
                    result = true;
                }
                else {
                    result = false;
                }
            }
        } catch (IOException e) {
            System.out.println("Błąd podczas autoryzacji transferu: " + e.toString());
        }
        return result;
    }

    @Override
    public void run() {

        if(authorize()){

            if(direction.ordinal() == 0){
                //upload file to server

                try {
                    System.out.println("Start uploading" + this.file.getName());
                    //send request
                    send.writeUTF("upload");
                    send.writeUTF(this.file.getName());
                    send.writeLong(this.file.length()); //size in bytes
                    send.writeInt(this.backupID);
                    send.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //sending file

                try {
                    InputStream in = new FileInputStream(file);
                    int count;
                    byte[] buffer = new byte[8192]; // or 4096, or more
                    while ((count = in.read(buffer)) > 0)
                    {
                        send.write(buffer, 0, count);
                        int finalCount = count;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                UploadManager.getInstance().updateUploadProgress(finalCount);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        socket.close();
                        System.out.println("Finished uploading" + this.file.getName());
                        cl.countDown();
                        System.out.println(cl.getCount());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            else {
                //download file from server

                try {
                    System.out.println("Start downloading" + this.dfile.name);
                    //send request
                    send.writeUTF("download");
                    send.writeInt(this.backupID);
                    send.writeInt(this.dfile.fileId);
                    send.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    if(receive.readUTF().equals("File found")){
                        Path fpath = Paths.get(this.path.toString(),this.dfile.name);
                        System.out.println(fpath);

                        //sending file
                        File ndFile = new File(fpath.toString());

                        try {
                            OutputStream out = new FileOutputStream(ndFile,false);
                            int count;
                            byte[] buffer = new byte[8192]; // or 4096, or more
                            while ((count = receive.read(buffer)) > 0)
                            {
                                out.write(buffer, 0, count);
                                int finalCount = count;
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        DownloadManager.getInstance().updateDownloadProgress(finalCount);
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finally {
                            try {
                                socket.close();
                                System.out.println("Finished downloading" + this.dfile.name);
                                cl.countDown();
                                System.out.println(cl.getCount());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    else {
                        SceneManager.getInstance().error("Nie znaleziono pliku na serwerze",
                                "Plik wybrany do pobrania nie został znaleziony na serwerze.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }



        }
    }
}
