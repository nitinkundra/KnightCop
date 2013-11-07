package cop;

import com.googlecode.javacv.FrameGrabber;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class Cam implements Runnable{
        
        private String videoCGI = "http://192.168.1.4:80/videostream.cgi?user=admin&pwd=knightcop&resolution=32&rate=0&dummy=param.mjpg";
        private JLabel label;
        byte[] mImageBytes;
        IPCamera grabber = new IPCamera(videoCGI);
        BufferedImage still = null;
        URL camURL = new URL(videoCGI);
        Graphics g;
        
        //default constructor
        public Cam(JLabel j, byte[] imageBytes) throws MalformedURLException{
                label = j;
                mImageBytes = imageBytes;
        }
                
        @Override
        public void run() {
                
                try {
                        grabber.start();
                        while (true){
                                still = grabber.grabBufferedImage();
                                label.setIcon(new ImageIcon(still));
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                ImageIO.write(still, "png", baos);
                                mImageBytes =  baos.toByteArray();
                                
                        }
                } catch (IOException | FrameGrabber.Exception ex) {
                        Logger.getLogger(Cam.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
        }
        
}
