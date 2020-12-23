package Backend;

import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

public class TransferThread implements Runnable{

    private File file;
    private Socket socket;
    private DataOutputStream send;
    private DataInputStream receive;
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
            send = new DataOutputStream(socket.getOutputStream());

            //create input stream for reading response from server
            receive = new DataInputStream(socket.getInputStream());

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

//                send.println("download");
//                send.println(this.fileID);
//                send.flush();



            }



        }
    }
}
