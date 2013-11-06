package knight;


import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.FrameGrabber;

public class CVcam {
        
        private String videoCGI = "http://192.168.173.102:80/videostream.cgi?user=admin&pwd=knightcop&resolution=32&rate=0&dummy=param.mjpg";
        FrameGrabber grabber = new FFmpegFrameGrabber(videoCGI);
        
        public void main(String[] args) throws FrameGrabber.Exception{
                
                grabber.setFormat("mjpeg");
               grabber.start();
        }
}