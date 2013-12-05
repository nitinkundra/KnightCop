package cop;

import com.googlecode.javacv.FrameGrabber;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JToggleButton;


public class Cam implements Runnable{
        
        String videoCGI = "http://" + KnightCop.camIP + ":80/videostream.cgi?user=admin&pwd=knightcop&resolution=32&rate=0&dummy=param.mjpg";
        private String imageCGI = "http://192.168.1.4:80/snapshot.cgi?user=admin&pwd=knightcop&next_url=0";
        private String panLeftCGI = "http://192.168.1.4:80/decoder_control.cgi?command=4&user=admin&pwd=knightcop&next_url=0";
        private JLabel label;
        private JToggleButton camToggle;
        byte[] mImageBytes;
        IPCamera grabber = new IPCamera(videoCGI);
        BufferedImage still = null;
        URL camURL = new URL(videoCGI);
        Boolean cameraOn;
        
        //default constructor
        public Cam(JLabel j) throws MalformedURLException{
                label = j;
        }
                
        @Override
        public void run() {
                
                try {
                        grabber.start();
                        while (true){
                                still = grabber.grabBufferedImage();
                                label.setIcon(new ImageIcon(still));

                        }
                        
                } catch (IOException | FrameGrabber.Exception ex) {
                        grabber.stop();
                        Logger.getLogger(Cam.class.getName()).log(Level.SEVERE, null, ex);
                }
               
                
        }
        
}
