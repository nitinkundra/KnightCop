package knight;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javafx.scene.image.Image;




public class IPCamera {
    
        
        private String imageCGI = "http://192.168.173.102:80/snapshot.cgi?user=admin&pwd=knightcop&next_url=0";
        private String videoCGI = "http://192.168.173.102:80/videostream.cgi?user=admin&pwd=knightcop&resolution=32&rate=0";
        
        private final static int imgSize = 1460;    //images are 1460 bytes each?
        private final URL imgURL;
        private BufferedInputStream vidStream;
        String packetHeader = "";
        int headerLength = packetHeader.length();
        
        public IPCamera() throws MalformedURLException, IOException {
                this.imgURL = new URL(videoCGI);
                this.vidStream = (BufferedInputStream) imgURL.openStream();
                
        }
        
        public void close() throws IOException{
                vidStream.close();
        }
    
        public Image nextImage() throws IOException{
                
                return new Image(new ByteArrayInputStream(nextFrame()));
                
                
        }

        private byte[] nextFrame() throws IOException {
                
                String header = getHeader();
                while (!header.startsWith("--")){
                        header = getHeader();
                }
                
                int packetSize = 0;
                header = getHeader();
                while (!header.isEmpty()){
                        
                        if (header.startsWith(packetHeader)){
                                packetSize = Integer.parseInt(header.substring(headerLength));
                        }
                        header = getHeader();
                        
                }
                
                
                
                return readImageBytes(packetSize);
        }

        private String getHeader() throws IOException {
                
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                int oneByte = vidStream.read();
                while (oneByte != '\n'){
                        b.write(oneByte);
                        oneByte = vidStream.read();       
                }
                return new String(b.toByteArray()).trim();
                
        }

        private byte[] readImageBytes(int packetSize) throws IOException {
                
                byte[] imageBytes = new byte[packetSize];
                for (int i = 0; i < packetSize; i++){
                        
                        imageBytes [i] = (byte)vidStream.read();
                        
                }
                return imageBytes;                
        }




}
