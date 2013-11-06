package knight;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.FrameGrabber;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.*;
import javafx.application.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.*;
import javax.imageio.ImageIO;



public class Knight extends Application {
    
        private KnightServer kServ;     //server that would host the Java Sockets
        private String imageCGI = "http://192.168.173.102:80/snapshot.cgi?user=admin&pwd=knightcop&next_url=0";
        private String videoCGI = "http://192.168.173.102:80/videostream.cgi?user=admin&pwd=knightcop&resolution=32&rate=0&dummy=param.mjpg";
        private static double t = 0;
        
        
        @Override
        public void start(Stage mainStage) throws MalformedURLException, IOException, InterruptedException, FrameGrabber.Exception {
                
                //ARM CONTROLS
                Button btnIncJoint0 = addBtn("images/plus.png");        //joint 0
                Button btnDecJoint0 = addBtn("images/minus.png");
                TextField txtDegJoint0 = new TextField();
        
                Button btnIncJoint1 = addBtn("images/plus.png");        //joint 1
                Button btnDecJoint1 = addBtn("images/minus.png");
                TextField txtDegJoint1 = new TextField();
        
                Button btnIncJoint2 = addBtn("images/plus.png");        //joint 2
                Button btnDecJoint2 = addBtn("images/minus.png");
                TextField txtDegJoint2 = new TextField();
        
                Button btnRotateR = addBtn("images/rotateR.png");       //arm base
                Button btnRotateL = addBtn("images/rotateL.png");
                TextField txtRotate = new TextField();
        
                //CONTAINER FOR ARM CONTROLS
                GridPane armControls = new GridPane();
                armControls.setHgap(1);
                armControls.setVgap(1);
                
                armControls.add(btnIncJoint0, 0, 0);    //joint 0
                armControls.add(txtDegJoint0, 0, 1);
                armControls.add(btnDecJoint0, 0, 2);
                txtDegJoint0.setPrefSize(50, 30);
                
                armControls.add(btnIncJoint1, 1, 0);    //joint 1
                armControls.add(txtDegJoint1, 1, 1);
                armControls.add(btnDecJoint1, 1, 2);
                txtDegJoint1.setPrefSize(50, 30);
                
                armControls.add(btnIncJoint2, 2, 0);    //joint 2
                armControls.add(txtDegJoint2, 2, 1);
                armControls.add(btnDecJoint2, 2, 2);
                txtDegJoint2.setPrefSize(50, 30);
                
                armControls.add(btnRotateR, 3, 0);      //arm base
                armControls.add(txtRotate, 3, 1);
                armControls.add(btnRotateL, 3, 2);
                txtRotate.setPrefSize(50, 30);
                
                armControls.setStyle("-fx-background-color: transparent;");
                
                //TOP ROW CONTROLS
                Button btnStartServer = addBtn("");;
                Label lblTemp = addTempLabel();
                ToggleButton btnMic = addToggleBtn("", "images/mic.png");
                ToggleButton btnLight = addToggleBtn("", "images/light.png");
                ToggleButton btnGripper = addToggleBtn("", "images/gripper_open.png");
                final ToggleButton btnSaveLog = addToggleBtn("", "images/save.png");
                Button btnLock = addBtn("images/lock.png");
                ChoiceBox cbPresets = new ChoiceBox(FXCollections.observableArrayList("Preset 1", "Preset 2", "Preset 3", "Preset 4", "Preset 5"));
                
                //CONTAINER FOR TOP BUTTON CONTROLS
                HBox btnBox = new HBox();
                btnBox.setSpacing(5);
                btnBox.setStyle("-fx-background-color: transparent;");
                btnBox.setPadding(new Insets(0, 0, 0, 10));
                btnBox.getChildren().addAll(btnStartServer, lblTemp, btnMic, btnLight, btnGripper, btnSaveLog, btnLock, cbPresets);
                
                //DRIVE CONTROLS
                Button btnMovFwd = addBtn("images/up.png");
                Button btnMovBwd = addBtn("images/down.png");
                Button btnTurnRt = addBtn("images/right.png");
                Button btnTurnLt = addBtn("images/left.png");  
                
                //CONTAINER FOR DRIVE CONTROLS
                GridPane driveControls = new GridPane();
                driveControls.add(btnMovFwd, 1, 0);
                driveControls.add(btnMovBwd, 1, 1);
                driveControls.add(btnTurnRt, 2, 1);
                driveControls.add(btnTurnLt, 0, 1);
                driveControls.setStyle("-fx-background-color: transparent;");
                
                
                //ALIGN ARM & DRIVE CONTROLS TO LEFT AND RIGHT
                BorderPane combinedControls = new BorderPane();
                combinedControls.setLeft (armControls);
                combinedControls.setRight(driveControls);
                
                //CONTAINER TO HOLD ALL CONTROLS
                BorderPane overlay = new BorderPane();
                overlay.setTop(btnBox);
                overlay.setBottom(combinedControls);
                overlay.setStyle("-fx-background-color: transparent;");
                
                        
                //BROWSER
                final WebView browser = new WebView();
                final WebEngine engine = browser.getEngine();
                engine.load(imageCGI);
                //engine.load("http://192.168.173.102:80");
        
                
                //IMAGE VIEW
                final CanvasFrame camWindow = new CanvasFrame("Knight CAM");
                //WritableImage wCamShot = new WritableImage(640, 480);
                //PixelReader fetchShot = camShot.getPixelReader();
                //PixelWriter setShot = wCamShot.getPixelWriter();
                //setShot.setPixels(0, 0, 640, 480, fetchShot, 0, 0);
                //MjpgParser foscam = new MjpgParser(videoCGI);
                //camShot = foscam.nextAsImage();
                final ImageView camView;
                
       
                //MEDIA PLAYER
                String vidURL = (videoCGI);
                Media video = new Media(vidURL);
                MediaPlayer videoPlayer = new MediaPlayer(video);
                videoPlayer.setAutoPlay(true);
                MediaView videoView = new MediaView(videoPlayer);
                
                
                //THREAD TO UPDATE CAMERA VIDEO
                final Service refresh = new Service() {
                        @Override
                        protected Task createTask() { 
                                   return new Task(){
                                            @Override
                                            protected Object call() throws Exception {
                                                    engine.load(imageCGI);
                                                    return true;
                                            }
                                  };
                        }
                };
                
                
                //FRAME
                /*final Duration refreshRate = new Duration(100);
                final KeyFrame frame = new KeyFrame(refreshRate,
                        new EventHandler<ActionEvent>(){
                                @Override
                                public void handle (ActionEvent e){
                                        refresh.restart();
                                }
                });
                
                //TIMELINE
                final Timeline tLine = TimelineBuilder.create()
                        .cycleCount(Animation.INDEFINITE)
                        .keyFrames(frame)
                        .build();
                
                final PauseTransition pause = new PauseTransition (Duration.millis(1000));*/
                
        
                //~~~~~~~~~JAVA CV~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                //FrameGrabber grabber = new FFmpegFrameGrabber(videoCGI);
                //grabber.setFormat("mjpeg");
                //grabber.start();
                
                
                
                
                
                
                
                
                
                
                //UI STUFF ENDS
                //EVENT HANDLERS BEGIN
                
                
                
                //SAVE LOG
                btnSaveLog.setOnAction(new EventHandler<ActionEvent>(){
                
                        @Override
                        public void handle (ActionEvent e){
                                //engine.load("http://192.168.173.102:80/snapshot.cgi?user=admin&pwd=knightcop&next_url=0");
                                //tLine.playFromStart();
                                //camView.setImage((Image) refresh.getValue());
                                while (btnSaveLog.isArmed()){
                                        URL url = null;
                                        try {
                                                url = new URL(imageCGI);
                                        } catch (MalformedURLException ex) {
                                                Logger.getLogger(Knight.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        
                                        InputStream is = null;
                                        try {
                                                is = url.openStream();
                                        } catch (IOException ex) {
                                                Logger.getLogger(Knight.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        BufferedImage camShot = null;
                                        try {
                                                camShot = ImageIO.read(is);
                                        } catch (IOException ex) {
                                                Logger.getLogger(Knight.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        camWindow.showImage(camShot);
                                        try {
                                                is.close();
                                        } catch (IOException ex) {
                                                Logger.getLogger(Knight.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                }
                        }
                                
                });
        
               
               //CONTROL GRIPPER
               btnGripper.setOnAction(new EventHandler <ActionEvent>(){

                        @Override
                        public void handle(ActionEvent t) {
                       
                              
                        }
               });
               
               
                
                //START SERVER
                btnStartServer.setOnAction(new EventHandler<ActionEvent>(){
            
                        @Override
                        public void handle(ActionEvent t) {
                                kServ = new KnightServer(new KnightServer.OnMessageReceived() {
                
                                        @Override
                                        public void messageReceived(String message) {
                                                System.out.println(message);
                    
                                        }       
                                });
                                kServ.start();                        
                        }
                });
      
                
                
                
                
        
                //MAIN LAYOUT
                StackPane stack = new StackPane();
                //stack.getChildren().add(camView);
                //stack.getChildren().add(browser);
                stack.getChildren().add(overlay);
                //ADD NODE
                stack.setAlignment(Pos.CENTER);
        
        
                //SET STAGE
                Scene view = new Scene(stack, 640, 480, Color.WHITE);
                mainStage.setTitle("KnightCop");
                mainStage.setScene(view);
                mainStage.show();
        
        }   //start Ends

    
        public static void main(String[] args) {    //ignored in FX apps
                
                launch(args);
        
        }   //main Ends

        
        ToggleButton addToggleBtn(String caption, String path){
        
                ToggleButton btn = new ToggleButton(caption);
                btn.setPrefSize(35, 35);
                Image img = new Image(getClass().getResourceAsStream(path));
                btn.setGraphic(new ImageView(img));
                return btn;
        }
    
    
        Button addBtn (String path){
        
                Button btn = new Button();
                btn.setPrefSize(35, 35);
                Image img = new Image(getClass().getResourceAsStream(path));
                btn.setGraphic(new ImageView (img));        
                return btn;
        }
    
        
        Label addTempLabel(){
        
                Label lblTemp = new Label("25°C");
                lblTemp.setPrefSize(35, 35);
                lblTemp.setTextAlignment(TextAlignment.CENTER);
                lblTemp.setTextFill (Color.web("#000000"));
                return lblTemp;
        }
        
}       //CLASS KNIGHT ENDS
