package cop;

import java.io.*;
import java.net.*;
 
public class Server extends Thread {
 
    int port = 22222;
    String serverIP = "192.168.1.6";
    String droidIP = "192.168.1.3";
    boolean running = false;
    PrintWriter toClient;
    byte[] mImageBytes;
    
    
    public Server (byte[] imageBytes){
            mImageBytes = imageBytes;
    }
         
    public void sendMessage(String message){
        if (toClient != null && !toClient.checkError()) {
            toClient.println(message);
            toClient.flush();
        }
    }
 
    @Override
    public void run() {
        //this.start();
        running = true;
        System.out.println("Server started");
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            
 
            try (Socket client = serverSocket.accept()) {
                toClient = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));

                while (running) {
                        System.out.println("Receiving");
                    String message = fromClient.readLine();
                    if (message != null && toClient != null) {
                         System.out.println(message);
                    }
                    toClient.print(mImageBytes);
                    toClient.flush();
                }
 
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
 
    }
    
    
 
}
