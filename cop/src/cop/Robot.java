package cop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Robot extends Thread{
        private String wiflyIP = "192.168.1.5";
        private int wiflyPort = 2000;
        Socket wiflySocket;
        
        public void Run() throws IOException{
                
                wiflySocket = new Socket (wiflyIP, wiflyPort);
                final BufferedWriter toWifly = new BufferedWriter(new OutputStreamWriter(wiflySocket.getOutputStream()));
                BufferedReader fromWifly = new BufferedReader(new InputStreamReader(wiflySocket.getInputStream()));
                try {
                        if (true){//(btnLight.isArmed()){
                                toWifly.write("1");
                                toWifly.flush();
                                System.out.println("DATA SENT");
                        }
                }       catch (IOException ex) {
                        Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
                }
                
        }
        
}
