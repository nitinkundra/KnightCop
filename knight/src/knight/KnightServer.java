package knight;

import java.io.*;
import java.net.*;
 
public class KnightServer extends Thread {
 
    private int port = 1001;
    private boolean running = false;
    private PrintWriter msgOut;
    private OnMessageReceived messageListener;
 
    public KnightServer(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }

    
 
    public void sendMessage(String message){
        if (msgOut != null && !msgOut.checkError()) {
            msgOut.println(message);
            msgOut.flush();
        }
    }
 
    @Override
    public void run() {
        this.start();
        running = true;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket client = serverSocket.accept();
            System.out.println("S: Receiving...");
 
            try {
                msgOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                while (running) {
                    String message = in.readLine();
                    if (message != null && messageListener != null) {
                        messageListener.messageReceived(message);
                    }
                }
 
            } catch (Exception e) {
            } finally {
                client.close();
            }
        } catch (Exception e) {
        }
 
    }
    
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
 
}
