package cop;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class KnightCop extends JPanel{
        
        Dimension dim = new Dimension(50, 50);
        //Arm control buttons
        JButton btnIncJ1, btnIncJ2, btnIncJ3, btnDecJ1, btnDecJ2, btnDecJ3, btnRotateR, btnRotateL;
        JTextField txtJ1, txtJ2, txtJ3, txtBase;
        //Drive control buttons
        JButton btnFwd, btnRev, btnTurnR, btnTurnL;
        //MISC buttons
        JToggleButton btnLight, btnSaveLog;
        JButton btnLock;
        //CAM FEED
        JLabel vidFeed;
        Thread showCam;
        byte[] imageBytes;
        //DROID SERVER
        Thread server;
        //Thread throwCam;
        
        public static void main(String[] args){
                javax.swing.SwingUtilities.invokeLater(new Runnable(){
                        @Override
                        public void run() {
                                try {
                                        setupUI();
                                } catch (IOException ex) {
                                        Logger.getLogger(KnightCop.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        }  
                });
            
        }       //main ends
        
        
        //CREATE NEW INSTANCE
        public static void setupUI() throws IOException{
                
                JFrame root = new JFrame("KnightCop");
                KnightCop newUI = new KnightCop();
                root.setContentPane(newUI);
                root.pack();
                root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                root.setVisible(true);
                newUI.showCam.start();
                newUI.server.start();
                //newUI.throwCam.start();
        }       //method setupUI ends
        
        
        //DEFAULT CONSTRUCTOR
        public KnightCop() throws MalformedURLException {
                
                //ARM CONTROLS
                btnIncJ1 = new JButton("", getIcon("images/plus.png"));
                btnDecJ1 = new JButton("", getIcon("images/minus.png"));
                txtJ1 = new JTextField("", 3);
                
                btnIncJ2 = new JButton("", getIcon("images/plus.png"));
                btnDecJ2 = new JButton("", getIcon("images/minus.png"));
                txtJ2 = new JTextField("", 3);
                
                btnIncJ3 = new JButton("", getIcon("images/plus.png"));
                btnDecJ3 = new JButton("", getIcon("images/minus.png"));
                txtJ3 = new JTextField("", 3);
                
                btnRotateR = new JButton("", getIcon("images/rotateR.png"));
                btnRotateL = new JButton("", getIcon("images/rotateL.png"));
                txtBase = new JTextField("", 3);
                
                JPanel armControls = new JPanel(new GridLayout(2, 4));  //r, c
                armControls.setBounds(0, 375, 150, 100);        //x, y, width, height
                armControls.add(btnIncJ1);
                armControls.add(btnIncJ2);
                armControls.add(btnIncJ3);
                armControls.add(btnRotateR);
                //armControls.add(txtJ1);
                //armControls.add(txtJ2);
                //armControls.add(txtJ3);
                //armControls.add(txtBase);
                armControls.add(btnDecJ1);
                armControls.add(btnDecJ2);
                armControls.add(btnDecJ3);
                armControls.add(btnRotateL);
                
                //DRIVE CONTROLS
                btnFwd = new JButton("", getIcon("images/up.png"));
                btnRev = new JButton("", getIcon("images/down.png"));
                btnTurnR = new JButton("", getIcon("images/right.png"));
                btnTurnL = new JButton("", getIcon("images/left.png"));
                
                JPanel driveControls = new JPanel(new BorderLayout());
                driveControls.setBounds(440, 380, 200, 100);
                
                driveControls.add(btnFwd, BorderLayout.PAGE_START);
                driveControls.add(btnRev, BorderLayout.CENTER);
                driveControls.add(btnTurnR, BorderLayout.LINE_END);
                driveControls.add(btnTurnL, BorderLayout.LINE_START);
                
                //MISC CONTROLS
                btnLight = new JToggleButton(getIcon("images/light.png"));
                btnSaveLog = new JToggleButton(getIcon("images/save.png"));
                btnLock = new JButton("", getIcon("images/lock.png"));
                
                JPanel miscControls = new JPanel();
                miscControls.setBounds(0, 0, 200, 50);
                miscControls.setLayout(new BoxLayout(miscControls, BoxLayout.X_AXIS));
                miscControls.add(btnLight);
                miscControls.add(btnSaveLog);
                miscControls.add(btnLock);                
                
                //VIDEO FRAME
                vidFeed = new JLabel("");
                vidFeed.setBounds(0, 0, 640, 480);
                Cam foscam = new Cam(vidFeed, imageBytes);
                showCam = new Thread(foscam);
                
                //DROID SERVER
                server = new Server(imageBytes);
                CamToDroid droidFeed = new CamToDroid(imageBytes);
                //throwCam = new Thread(droidFeed);
                
                //OVERALL LAYOUT
                JLayeredPane layers = new JLayeredPane();
                layers.setPreferredSize(new Dimension(640, 480));
                layers.setOpaque(false);
                layers.add(vidFeed, JLayeredPane.DEFAULT_LAYER);
                layers.add(armControls, JLayeredPane.PALETTE_LAYER);
                layers.add(driveControls, JLayeredPane.PALETTE_LAYER);
                layers.add(miscControls, JLayeredPane.PALETTE_LAYER);
                this.setLayout(new BorderLayout());
                this.add(layers, BorderLayout.CENTER);
                
                
        }       //constructor for KnightCop ends
        
        protected static ImageIcon getIcon(String path) {
                URL imgURL = KnightCop.class.getResource(path);
                return new ImageIcon(imgURL);
        }       //method getIcon ends
        
        
        
        
        
        
}       //class KnightCop ends
