package cop;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class IPCamera extends FrameGrabber {

        private URL url;
        private URLConnection connection;
        private InputStream input;
        private Map<String, List<String>> headerfields;
        private String boundaryKey;

        public IPCamera(String urlstr) throws MalformedURLException {
                        url = new URL(urlstr);
        }

        @Override
        public void start() {
                
                try {
                        connection = url.openConnection();
                        headerfields = connection.getHeaderFields();
                        if (headerfields.containsKey("Content-Type")) {
                                List<String> ct = headerfields.get("Content-Type");
                                for (int i = 0; i < ct.size(); ++i) {
                                        String key = ct.get(i);
                                        int j = key.indexOf("boundary=");
                                        if (j != -1) {
                                                boundaryKey = key.substring(j + 9); 
                                        }
                                }
                        }
                        input = connection.getInputStream();
                } catch (IOException e) {
                        
                }
        }

        @Override
        public void stop() {
                try {
                        input.close();
                        input = null;
                        connection = null;
                        //url = null;
                } catch (IOException e) {
                }
        }

        @Override
        public void trigger() throws FrameGrabber.Exception {
        }

        @Override
        public IplImage grab() {
                try {
                        return IplImage.createFrom(grabBufferedImage());
                } catch (FrameGrabber.Exception | IOException e) {
                }
                return null;
        }

        public BufferedImage grabBufferedImage() throws FrameGrabber.Exception, IOException {
                byte[] buffer = new byte[4096];// MTU or JPG Frame Size?
                int n = -1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                StringBuffer sb = new StringBuffer();
                int total = 0;
                int c;
                // read http subheader
                while ((c = input.read()) != -1) {
                        if (c > 0) {
                                sb.append((char) c);
                                if (c == 13) {
                                        sb.append((char) input.read());// '10'+
                                        c = input.read();
                                        sb.append((char) c);
                                        if (c == 13) {
                                                sb.append((char) input.read());// '10'
                                                break; // done with subheader
                                        }
                                }
                        }
                }
                // find embedded jpeg in stream
                String subheader = sb.toString();
               
                int contentLength = -1;
                // if (boundryKey == null)
                // {
                //content length from esrver
                int c0 = subheader.indexOf("Content-Length: ");
                int c1 = subheader.indexOf('\r', c0);
                
                if (c0 < 0){   
                        return null;
                }
                c0 += 16;
                contentLength = Integer.parseInt(subheader.substring(c0, c1).trim());
                
                // adaptive size - don't want a 2G jpeg
                if (contentLength > buffer.length) {
                        buffer = new byte[contentLength];
                }
                n = -1;
                total = 0;
                if(input.available() > -1){
                while ((n = input.read(buffer, 0, contentLength - total)) != -1) {
                        total += n;
                        baos.write(buffer, 0, n);

                        if (total == contentLength) {
                                break;
                        }
                }

                baos.flush();
                
                input.read();// \r
                input.read();// \n
                input.read();// \r
                input.read();// \n

                BufferedImage bi = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
                return bi;}
                return null;
        }

        @Override
        public void release() throws FrameGrabber.Exception {
                url = null;
        }

}