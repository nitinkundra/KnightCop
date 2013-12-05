package cop;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class KnightCop extends JPanel{
        
        //IP Addresses
        public static final int serverPort = 22222;
        public static final int wiflyPort = 2000;
        public static final String pcIP = "192.168.1.5";
        public static final String droidIP = "192.168.1.3";
        public static final String wiflyIP = "192.168.1.7";
        public static final String camIP = "192.168.1.4";
        
        String videoCGI = "http://" + KnightCop.camIP + ":80/videostream.cgi?user=admin&pwd=knightcop&resolution=32&rate=0&dummy=param.mjpg";
        //0=up, 2=down, 4=left, 6=right, 16=zoom in, 18=zoom out, 
        String panLCGI = "http://192.168.1.4:80/decoder_control.cgi?command=4&onestep=1&user=admin&pwd=knightcop";
        String panRCGI = "http://192.168.1.4:80/decoder_control.cgi?command=6&onestep=1&user=admin&pwd=knightcop";
        String tiltUCGI = "http://192.168.1.4:80/decoder_control.cgi?command=0&onestep=1&user=admin&pwd=knightcop";
        String tiltDCGI = "http://192.168.1.4:80/decoder_control.cgi?command=2&onestep=1&user=admin&pwd=knightcop";
        String ledCGI = "http://192.168.1.4:80/set_misc.cgi?led_mode=1&user=admin&pwd=knightcop";
        
        Dimension dim = new Dimension(50, 50);
        //Arm control buttons
        JButton btnIncJ1, btnIncJ2, btnIncJ3, btnDecJ1, btnDecJ2, btnDecJ3, btnRotateR, btnRotateL, btnGripperClose, btnGripperOpen, btnCamera;
        //Drive control buttons
        JButton btnFwd, btnRev, btnTurnR, btnTurnL;
        //MISC buttons
        JToggleButton btnLight, btnSaveLog;
        JButton btnLock;
        JLabel tReading, lReading, bReading, blocked;
        //CAM FEED
        JLabel vidFeed;
        Thread showCam;
        ConcurrentLinkedQueue<byte[]> q;
        HttpURLConnection pan;
       
        Socket wiflySocket;
        static BufferedReader fromWifly;
        BufferedWriter toWifly = null;
        
        public Boolean cameraOn;
        BufferedImage capture;
        IPCamera grabber;
        
        static ServerSocket server;
        static Socket client;
        static DataOutputStream toClient;
        static BufferedReader fromClient;
        
        
        public static void main(String[] args) throws MalformedURLException, UnknownHostException, IOException, InterruptedException, AWTException{
                
                JFrame root = new JFrame("KnightCop");
                KnightCop newUI = new KnightCop();
                root.setContentPane(newUI);
                root.pack();
                root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                root.setLocationRelativeTo(null); 
                root.setVisible(true);
                int temp;
                char byteIn;
                StringBuilder lux;
                StringBuilder temperature, battery;
                newUI.showCam.start();
                int batteryLevel = 0;
                
                
                
                /*
                client = server.accept();
                toClient = new DataOutputStream(client.getOutputStream());
                fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
                Robot robot = new Robot();
                */
                
                while (true){
                        
                        
                        if (fromWifly.ready()){
                                
                                byteIn = (char) (fromWifly.read());
                                if(byteIn == '#'){
                                        lux = new StringBuilder(); 
                                        temp = fromWifly.read();
                                        for (int i = 0; i < temp; i++){
                                                lux.append((char)fromWifly.read());
                                        }
                                        newUI.lReading.setText(lux.toString() + " lux");
                                }
                                else if(byteIn == '$'){
                                        temperature = new StringBuilder();
                                        temp = fromWifly.read();
                                        for (int i = 0; i < temp; i++){
                                                temperature.append((char)fromWifly.read());
                                        }
                                        newUI.tReading.setText(temperature.toString() + "° F");
                                }
                                else if(byteIn == '%'){
                                        temp = fromWifly.read();
                                        int[] level = new int[temp];
                                        int num = 0;
                                        for (int i = 0; i < temp; i++){
                                                
                                                level[i] = fromWifly.read() - 48;
                                                
                                                num = (int) (num + level[i] * Math.pow(10, temp - (i+1)));
                                        }
                                        int actualLevel;
                                        if (num > 850){
                                                actualLevel = 100;
                                        }
                                        else if (num < 50){
                                                actualLevel = 0;
                                        }
                                        else actualLevel = (num - 50)/8;
                                        newUI.bReading.setText(actualLevel + "% battery");
                                       
                                        
                                }
                                else if (byteIn == 'I'){
                                        newUI.blocked.setText("Blocked!");
                                }
                                else if (byteIn == 'J'){
                                        newUI.blocked.setText("Blocked!");
                                }
                                else if (byteIn == 'K'){
                                        newUI.blocked.setText("Blocked!");
                                }
                                else if (byteIn == 'L'){
                                        newUI.blocked.setText("Blocked!");
                                }
                                else newUI.blocked.setText("");
                        }
                        
                        
                
                
                }       //main while loop
                
                
               

        }       //main ends
        
        
        //DEFAULT CONSTRUCTOR
        public KnightCop() throws MalformedURLException, UnknownHostException, IOException {
                
                //ARM CONTROLS
                btnIncJ1 = new JButton("", getIcon("images/plus.png"));
                btnDecJ1 = new JButton("", getIcon("images/minus.png"));
                btnIncJ1.setToolTipText("Joint 1 ++");
                btnDecJ1.setToolTipText("Joint 1 --");
                
                btnIncJ2 = new JButton("", getIcon("images/plus.png"));
                btnDecJ2 = new JButton("", getIcon("images/minus.png"));
                btnIncJ2.setToolTipText("Joint 2 ++");
                btnDecJ2.setToolTipText("Joint 2 --");
                
                btnIncJ3 = new JButton("", getIcon("images/plus.png"));
                btnDecJ3 = new JButton("", getIcon("images/minus.png"));
                btnIncJ3.setToolTipText("Joint 3 ++");
                btnDecJ3.setToolTipText("Joint 3 --");
                
                btnRotateR = new JButton("", getIcon("images/rotateR.png"));
                btnRotateL = new JButton("", getIcon("images/rotateL.png"));
                btnRotateR.setToolTipText("Base ++");
                btnRotateL.setToolTipText("Base --");
                
                btnGripperOpen = new JButton("", getIcon("images/gripper_open.png"));
                btnGripperClose = new JButton("", getIcon("images/gripper_closed.png"));
                btnGripperOpen.setToolTipText("Open Gripper");
                btnGripperClose.setToolTipText("Close Gripper");
                        
                JPanel armControls = new JPanel(new GridLayout(2, 4));  //r, c
                armControls.setBounds(0, 375, 150, 100);        //x, y, width, height
                armControls.add(btnIncJ1);
                armControls.add(btnIncJ2);
                armControls.add(btnIncJ3);
                armControls.add(btnGripperOpen);
                armControls.add(btnRotateR);
                
                armControls.add(btnDecJ1);
                armControls.add(btnDecJ2);
                armControls.add(btnDecJ3);
                armControls.add(btnGripperClose);
                armControls.add(btnRotateL);
                
                //DRIVE CONTROLS
                btnFwd = new JButton("", getIcon("images/up.png"));
                btnFwd.setPreferredSize(new Dimension(50, 50));
                btnRev = new JButton("", getIcon("images/down.png"));
                btnRev.setPreferredSize(new Dimension(50, 50));
                btnTurnR = new JButton("", getIcon("images/right.png"));
                btnTurnR.setPreferredSize(new Dimension(50, 50));
                btnTurnL = new JButton("", getIcon("images/left.png"));
                btnTurnL.setPreferredSize(new Dimension(50, 50));
                
                JPanel driveControls = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                driveControls.setBounds(460, 380, 200, 100);
                        gbc.gridx = 1;                  //x = column, y = row
                        gbc.gridy = 0;
                driveControls.add(btnFwd, gbc);
                        gbc.gridx = 1;                 
                        gbc.gridy = 1;
                driveControls.add(btnRev, gbc);
                        gbc.gridx = 2;                 
                        gbc.gridy = 1;
                driveControls.add(btnTurnR, gbc);
                        gbc.gridx = 0;                 
                        gbc.gridy = 1;
                driveControls.add(btnTurnL, gbc);
                driveControls.setOpaque(false);
                
                //MISC CONTROLS
                //btnLight = new JToggleButton(getIcon("images/light.png"));
                //btnSaveLog = new JToggleButton(getIcon("images/save.png"));
                //btnLock = new JButton("", getIcon("images/lock.png"));
                btnCamera = new JButton("", getIcon("images/camera.png"));
                tReading = new JLabel(" T ");
                tReading.setBackground(Color.LIGHT_GRAY);
                tReading.setOpaque(true);
                tReading.setPreferredSize(new Dimension(50, 50));
                lReading = new JLabel(" L ");
                lReading.setPreferredSize(new Dimension(50, 50));
                lReading.setBackground(Color.LIGHT_GRAY);
                lReading.setOpaque(true);
                bReading = new JLabel(" B ");
                bReading.setPreferredSize(new Dimension(50, 50));
                bReading.setBackground(Color.LIGHT_GRAY);
                bReading.setOpaque(true);
                                
                blocked = new JLabel("");
                JPanel miscControls = new JPanel();
                miscControls.setBounds(0, 0, 400, 50);
                miscControls.setLayout(new BoxLayout(miscControls, BoxLayout.X_AXIS));
                //miscControls.add(btnLight);
                miscControls.add(tReading);
                miscControls.add(Box.createRigidArea(new Dimension(5,0)));
                miscControls.add(lReading);
                miscControls.add(Box.createRigidArea(new Dimension(5,0)));
                miscControls.add(bReading);
                miscControls.add(Box.createRigidArea(new Dimension(5,0)));
                miscControls.add(blocked);
                
                
                
                
                miscControls.setOpaque(false);
                
                //CAMERA CONTROLS
                final JButton panL = new JButton("", getIcon("images/panL.png"));
                panL.setPreferredSize(new Dimension(50, 50));
                final JButton panR = new JButton("", getIcon("images/panR.png"));
                panR.setPreferredSize(new Dimension(50, 50));
                final JButton tiltU = new JButton("", getIcon("images/tiltU.png"));
                tiltU.setPreferredSize(new Dimension(50, 50));
                final JButton tiltD = new JButton("", getIcon("images/tiltD.png"));
                tiltD.setPreferredSize(new Dimension(50, 50));
                final JButton led = new JButton("", getIcon("images/light.png"));
                
                JPanel camControls = new JPanel(new GridBagLayout());
                camControls.setBounds(450, 0, 210, 200);
                        gbc.gridx = 1;                  //x = column, y = row
                        gbc.gridy = 0;
                camControls.add(tiltU, gbc);
                        gbc.gridx = 1;
                        gbc.gridy = 1;
                camControls.add(tiltD, gbc);
                        gbc.gridx = 2;
                        gbc.gridy = 1;
                camControls.add(panR, gbc);
                        gbc.gridx = 0;
                        gbc.gridy = 1;
                camControls.add(panL, gbc);
                camControls.setOpaque(false);
                
                //VIDEO FRAME
                vidFeed = new JLabel("");
                vidFeed.setBounds(0, 0, 640, 480);
                Cam foscam = new Cam(vidFeed);
                showCam = new Thread(foscam);
                
                //DROID SERVER
                //server = new ServerSocket(serverPort);
                
                //OVERALL LAYOUT
                JLayeredPane layers = new JLayeredPane();
                layers.setPreferredSize(new Dimension(640, 480));
                layers.setOpaque(false);
                layers.add(vidFeed, JLayeredPane.DEFAULT_LAYER);
                layers.add(armControls, JLayeredPane.PALETTE_LAYER);
                layers.add(driveControls, JLayeredPane.PALETTE_LAYER);
                layers.add(miscControls, JLayeredPane.PALETTE_LAYER);
                layers.add(camControls, JLayeredPane.PALETTE_LAYER);
                this.setLayout(new BorderLayout());
                this.add(layers, BorderLayout.CENTER);
                
                
                final HttpParams params = new BasicHttpParams();
                final HttpPost postPanL = new HttpPost(panLCGI);
                final HttpPost postPanR = new HttpPost(panRCGI);
                final HttpPost postTiltU = new HttpPost(tiltUCGI);
                final HttpPost postTiltD = new HttpPost(tiltDCGI);
                final HttpPost postLED = new HttpPost(ledCGI);
                
                HttpConnectionParams.setConnectionTimeout(params, 5000);
                HttpConnectionParams.setSoTimeout(params, 5000);
                      
                //WIFLY SOCKET TO ROBOT
                
                
                wiflySocket = new Socket (wiflyIP, wiflyPort);
                toWifly = new BufferedWriter(new OutputStreamWriter(wiflySocket.getOutputStream()));
                fromWifly = new BufferedReader(new InputStreamReader(wiflySocket.getInputStream()));
               
                
                
                //CAMERA PAN/TILT
                final MouseListener camListener = new MouseListener() {
                        
                        private boolean mouseDown = false;
                        HttpPost post;
                        
                        @Override
                        public void mouseClicked(MouseEvent me) {
                        }

                        @Override
                        public void mousePressed(MouseEvent me) {
                                if (me.getButton()== MouseEvent.BUTTON1) {
                                        mouseDown = true;
                                        
                                        if (me.getSource() == panL){
                                                post = postPanR;
                                        }else if (me.getSource() == panR){
                                                post = postPanL;
                                        }else if (me.getSource() == tiltU){
                                                post = postTiltU;
                                        }else if (me.getSource() == tiltD){
                                                post = postTiltD;
                                        }else if (me.getSource() == led){
                                                post = postLED;
                                        }
                                        initThread();
                                }
                        }

                        @Override
                        public void mouseReleased(MouseEvent me) {
                                if (me.getButton() == MouseEvent.BUTTON1) {
                                        mouseDown = false;
                                }
                        }

                        @Override
                        public void mouseEntered(MouseEvent me) {
                        }

                        @Override
                        public void mouseExited(MouseEvent me) {
                        }
                        
                        private boolean isRunning = false;
                        
                        private synchronized boolean checkAndMark() {
                                if (isRunning){
                                        return false;
                                }
                                isRunning = true;
                                return true;
                        }
                        
                        private void initThread() {
                                if (checkAndMark()) {
                                        new Thread() {
                                                @Override
                                                public void run() {
                                                        do {
                                                                try (DefaultHttpClient httpClient = new DefaultHttpClient(params)) {
                                                                                httpClient.execute(post);
                                                                } catch (IOException ex) {
                                                                        Logger.getLogger(KnightCop.class.getName()).log(Level.SEVERE, null, ex);   
                                                                } 
                                                                
                                                        } while (mouseDown);
                                                        isRunning = false;
                                                }
                                        }.start();
                                }
                                
                        }
                };
                panL.addMouseListener(camListener);
                panR.addMouseListener(camListener);
                tiltU.addMouseListener(camListener);
                tiltD.addMouseListener(camListener);
                
                
                
                //LISTENER FOR DRIVE CONTROLS
               final MouseListener driveListener = new MouseListener() {
                       
                       private boolean mouseDown = false;
                       char movDir = 'S';
                       
                        @Override
                        public void mouseClicked(MouseEvent me) {
                        }

                        @Override
                        public void mousePressed(MouseEvent me) {
                                if (me.getButton()== MouseEvent.BUTTON1) {
                                        mouseDown = true;
                                        if (me.getSource() == btnFwd){
                                                movDir = 'F';
                                        }else if (me.getSource() == btnRev){
                                                movDir = 'B';
                                        }else if (me.getSource() == btnTurnR){
                                                movDir = 'R';
                                        }else if (me.getSource() == btnTurnL){
                                                movDir = ';';
                                        }
                                        initThread();
                                }
                        }

                        @Override
                        public void mouseReleased(MouseEvent me) {
                                if (me.getButton() == MouseEvent.BUTTON1) {
                                        mouseDown = false;
                                }
                        }

                        @Override
                        public void mouseEntered(MouseEvent me) {
                        }

                        @Override
                        public void mouseExited(MouseEvent me) {
                        }
                        
                        private boolean isRunning = false;
                        private synchronized boolean checkAndMark() {
                                if (isRunning){
                                        return false;
                                }
                                isRunning = true;
                                return true;
                        }
                        
                        private void initThread() {
                                if (checkAndMark()) {
                                        new Thread() {
                                                @Override
                                                public void run() {
                                                        do {
                                                                try {
                                                                        toWifly.write(movDir);     //Drive
                                                                        toWifly.flush();
                                                                        TimeUnit.MILLISECONDS.sleep(250);
                                                                } catch (InterruptedException | IOException ex) {
                                                                        Logger.getLogger(KnightCop.class.getName()).log(Level.SEVERE, null, ex);
                                                                }
                                                        } while (mouseDown);
                                                        isRunning = false;
                                                }
                                        }.start();
                                }
                        }
                };
                btnFwd.addMouseListener(driveListener);
                btnRev.addMouseListener(driveListener);
                btnTurnR.addMouseListener(driveListener);
                btnTurnL.addMouseListener(driveListener);
                
                
                //LISTENER FOR ARM CONTROLS
                final MouseListener armListener = new MouseListener() {
                       
                       private boolean mouseDown = false;
                       char arm = 'z';
                       
                        @Override
                        public void mouseClicked(MouseEvent me) {
                        }

                        @Override
                        public void mousePressed(MouseEvent me) {
                                if (me.getButton()== MouseEvent.BUTTON1) {
                                        mouseDown = true;
                                        if (me.getSource() == btnIncJ1){
                                                arm = '2';
                                        }else if (me.getSource() == btnDecJ1){
                                                arm = '1';
                                        }else if (me.getSource() == btnIncJ2){
                                                arm = '4';
                                        }else if (me.getSource() == btnDecJ2){
                                                arm = '3';
                                        }else if (me.getSource() == btnIncJ3){
                                                arm = '6';
                                        }else if (me.getSource() == btnDecJ3){
                                                arm = '5';
                                        }else if (me.getSource() == btnGripperOpen){
                                                arm = '8';
                                              
                                        }else if (me.getSource() == btnGripperClose){
                                                arm = '7';
                                              
                                        }else if (me.getSource() == btnRotateL){
                                                arm = '9';
                                        }
                                        else if (me.getSource() == btnRotateR){
                                                arm = '0';
                                        }
                                        initThread();
                                }
                        }

                        @Override
                        public void mouseReleased(MouseEvent me) {
                                if (me.getButton() == MouseEvent.BUTTON1) {
                                        mouseDown = false;
                                }
                        }

                        @Override
                        public void mouseEntered(MouseEvent me) {
                        }

                        @Override
                        public void mouseExited(MouseEvent me) {
                        }
                        
                        private boolean isRunning = false;
                        private synchronized boolean checkAndMark() {
                                if (isRunning){
                                        return false;
                                }
                                isRunning = true;
                                return true;
                        }
                        
                        private void initThread() {
                                if (checkAndMark()) {
                                        new Thread() {
                                                @Override
                                                public void run() {
                                                        do {
                                                                try {
                                                        toWifly.write(arm);     //Arm
                                                        toWifly.flush();
                                                        TimeUnit.MILLISECONDS.sleep(250);
                                                        } catch (InterruptedException | IOException ex) {
                                                                        Logger.getLogger(KnightCop.class.getName()).log(Level.SEVERE, null, ex);
                                                                }
                                                } while (mouseDown);
                                                        isRunning = false;
                                                }
                                        }.start();
                                }
                        }
                };
                
                btnIncJ1.addMouseListener(armListener);
                btnDecJ1.addMouseListener(armListener);
                btnIncJ2.addMouseListener(armListener);
                btnDecJ2.addMouseListener(armListener);
                btnIncJ3.addMouseListener(armListener);
                btnDecJ3.addMouseListener(armListener);
                btnRotateR.addMouseListener(armListener);
                btnRotateL.addMouseListener(armListener);
                btnGripperOpen.addMouseListener(armListener);
                btnGripperClose.addMouseListener(armListener);
               
                
                
        }       //constructor for KnightCop ends
        
        protected static ImageIcon getIcon(String path) {
                URL imgURL = KnightCop.class.getResource(path);
                return new ImageIcon(imgURL);
        }       //method getIcon ends
        
        
  
        
        
        
}       //class KnightCop ends
