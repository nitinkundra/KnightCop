package com.knight.cop;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity implements OnGestureListener, SensorEventListener{

	GestureDetector driveControl;
	Point screenSize;
	Thread droid;
	Thread showCam;
	Socket KC;
	PrintWriter toKC;
	DataInputStream fromKC;
	ConcurrentLinkedQueue<Character> dataOut;
	Handler h;
	String serverIP = "192.168.1.5";
	String wiflyIP = "192.168.1.7";
	int wiflyPort = 2000;
	int serverPort = 22222;
	String videoCGI = "http://192.168.1.4:80/videostream.cgi?user=admin&pwd=knightcop&resolution=32&rate=0&dummy=param.mjpg";
	String imageCGI = "http://192.168.1.4:80/snapshot.cgi?user=admin&pwd=knightcop&next_url=0";
	String panLCGI = "http://192.168.1.4:80/decoder_control.cgi?command=4&onestep=1&user=admin&pwd=knightcop";
    String panRCGI = "http://192.168.1.4:80/decoder_control.cgi?command=6&onestep=1&user=admin&pwd=knightcop";
    String tiltUCGI = "http://192.168.1.4:80/decoder_control.cgi?command=0&onestep=1&user=admin&pwd=knightcop";
    String tiltDCGI = "http://192.168.1.4:80/decoder_control.cgi?command=2&onestep=1&user=admin&pwd=knightcop";
    String ledCGI = "http://192.168.1.4:80/set_misc.cgi?led_mode=1&user=admin&pwd=knightcop";
    HttpParams params;
    HttpPost postPanL, postPanR, postTiltU, postTiltD, postLED;
    private Vibrator vib;
    StringBuilder lux, temperature, battery;
    TextView tvLux, tvTemp, tvBatt, tv1;
    
	int leftDrive = 0, rightDrive = 0;
	MjpegView mv;
	private SensorManager sMgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		vib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				
		//setContentView(R.layout.activity_main);
		
		dataOut = new ConcurrentLinkedQueue<Character>();
		
		driveControl = new GestureDetector(getBaseContext(), this);
		
		h = new Handler();
		new OpenSocket().execute();
		
		//GYRO CONTROl
		sMgr = (SensorManager)getSystemService(SENSOR_SERVICE);
		sMgr.registerListener(this, sMgr.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),SensorManager.SENSOR_DELAY_FASTEST);
		params = new BasicHttpParams();
        postPanL = new HttpPost(panLCGI);
        postPanR = new HttpPost(panRCGI);
        postTiltU = new HttpPost(tiltUCGI);
        postTiltD = new HttpPost(tiltDCGI);
        postLED = new HttpPost(ledCGI);
        
        HttpConnectionParams.setConnectionTimeout(params, 5000);
        HttpConnectionParams.setSoTimeout(params, 5000);
		
		mv = new MjpegView(this);
		setContentView(mv);
		View stolenView = mv;
		setContentView(R.layout.activity_main);
		View view = findViewById(R.id.root);
		((ViewGroup) view).addView(stolenView);
		
		
		
		findViewById(R.id.controls).bringToFront();
		findViewById(R.id.movFwd).bringToFront();
		findViewById(R.id.movBwd).bringToFront();
		findViewById(R.id.turnR).bringToFront();
		findViewById(R.id.turnL).bringToFront();
		
		tvLux = (TextView)findViewById(R.id.tvLux);
		tvTemp = (TextView)findViewById(R.id.tvTemp);
		//tv1 = (TextView)findViewById(R.id.textView1);
		tvBatt = (TextView)findViewById(R.id.tvBatt);
		
		findViewById(R.id.movFwd).setOnTouchListener(drive);
		findViewById(R.id.movBwd).setOnTouchListener(drive);
		findViewById(R.id.turnR).setOnTouchListener(drive);
		findViewById(R.id.turnL).setOnTouchListener(drive);
		findViewById(R.id.incj1).setOnTouchListener(drive);
		findViewById(R.id.decj1).setOnTouchListener(drive);
		findViewById(R.id.incj2).setOnTouchListener(drive);
		findViewById(R.id.decj2).setOnTouchListener(drive);
		findViewById(R.id.incj3).setOnTouchListener(drive);
		findViewById(R.id.decj3).setOnTouchListener(drive);
		findViewById(R.id.incg).setOnTouchListener(drive);
		findViewById(R.id.decg).setOnTouchListener(drive);
		findViewById(R.id.incbase).setOnTouchListener(drive);
		findViewById(R.id.decbase).setOnTouchListener(drive);
		
		findViewById(R.id.panLeft).setOnTouchListener(camListener);
    	findViewById(R.id.panRight).setOnTouchListener(camListener);
    	findViewById(R.id.tiltUp).setOnTouchListener(camListener);
    	findViewById(R.id.tiltDown).setOnTouchListener(camListener);
		
		new StartCam().execute(videoCGI);
		
		Handler read = new Handler();
		GoRead thread = new GoRead(read);
		thread.start();
		
		
		
		
	}

	
	
	public void onPause() {
        super.onPause();
        //mv.stopPlayback();
    }
	
	public class StartCam extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... url) {
            HttpResponse reply = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();     
            try {
            	reply = httpclient.execute(new HttpGet(URI.create(url[0])));
                if(reply.getStatusLine().getStatusCode()==401){
                    return null;
                }
                return new MjpegInputStream(reply.getEntity().getContent());  
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            mv.setSource(result);
            mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            mv.showFps(false);
        }
    }

	
	private class GoRead extends Thread{
		 
		private final Handler readHandle;
		GoRead(Handler h){
			readHandle = h;
		}
		
        @Override
        public void run() {  	
        		
        	
        		int temp = 0;
        		char byteIn = '0';
        		try {
					temp = fromKC.available();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        		while (temp > -1){
        			
						try {
						
						byteIn = (char)fromKC.read();
						
						
							
							if(byteIn == '#'){
								lux = new StringBuilder(); 
								temp = fromKC.read();
								for (int i = 0; i < temp; i++){  
									lux.append((char)fromKC.read());
								}
								readHandle.post(new Runnable(){
									@Override
									public void run(){
										updateLux(lux.toString() + " lux");
									}
								});
							
							}
							else if(byteIn == '$'){
								temperature = new StringBuilder();
								temp = fromKC.read();
								for (int i = 0; i < temp; i++){
									temperature.append((char)fromKC.read());
								}
								readHandle.post(new Runnable(){
									@Override
									public void run(){
										updateTemp(temperature.toString() + "° F");
									}
								});
						    
							}
							else if(byteIn == '%'){
								temp = fromKC.read();
                                int[] level = new int[temp];
                                int num = 0;
                                for (int i = 0; i < temp; i++){
                                        
                                        level[i] = fromKC.read() - 48;
                                        System.out.println(level[i]);
                                        num = (int) (num + level[i] * Math.pow(10, temp - (i+1)));
                                }
                                final int actualLevel;
                                if (num > 850){
                                        actualLevel = 100;
                                }
                                else if (num < 50){
                                        actualLevel = 0;
                                }
                                else actualLevel = (num - 50)/8;
                                
						        readHandle.post(new Runnable(){
									@Override
									public void run(){
										updateBatt(actualLevel + "% battery");
									}
								});
							}
							else if (byteIn == 'I'){
								vib.vibrate(100);
								System.out.println("Received");
							}else if (byteIn == 'J'){
								vib.vibrate(100);
								System.out.println("Received");
							}
							else if (byteIn == 'K'){
								vib.vibrate(100);
								System.out.println("Received");
							}
							else if (byteIn == 'L'){
								vib.vibrate(100);
								System.out.println("Received");
							}
						


					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		
        }			
        

        }
        }
	
	
	 private class relayInput extends AsyncTask<Void, Void, Void> {
		 
	        @Override
	        protected Void doInBackground(Void... params) {  	
	        	if (!dataOut.isEmpty() && toKC != null){
	        		char c = dataOut.remove();
	        		toKC.write(c);
	        		toKC.flush();
	        		System.out.println("Sent: " + c);
	        	}
	        	return null;			
	        }

	        @Override
	        protected void onPostExecute(Void result) {}

	        @Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	    }
    

	 private class OpenSocket extends AsyncTask<Void, Void, Void> {
		 
	        @Override
	        protected Void doInBackground(Void... params) {
	        	try {
	        		KC = new Socket(wiflyIP, wiflyPort);
	    			toKC = new PrintWriter (new BufferedWriter (new OutputStreamWriter (KC.getOutputStream())));
	    			fromKC = new DataInputStream(KC.getInputStream());
	        		
	    		} catch (UnknownHostException e) {
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
				return null;
	        }

	        @Override
	        protected void onPostExecute(Void result) {}

	        @Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	    }
	 
	 private class PostIt extends AsyncTask<Void, Void, Void> {
		 
		 	HttpPost p;
		 	public PostIt(HttpPost post) {
		        super();
		        p = post;
		    }
		 
		 	
	        @Override
	        protected Void doInBackground(Void... v) {
	        	try {
	        		DefaultHttpClient httpClient = new DefaultHttpClient(params);
	                httpClient.execute(p);
	    		} catch (UnknownHostException e) {
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
				return null;
	        }

	        @Override
	        protected void onPostExecute(Void result) {}

	        @Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	    }
	
	 /*
	 private OnClickListener driveListener = new OnClickListener() {
		    @Override
		    public void onClick(final View v) {
		    	
		        
		    }
		};
	 */
	 private OnTouchListener camListener = new OnTouchListener(){

			private Handler h;
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()){
					
					case MotionEvent.ACTION_DOWN:
						if (h != null) return false;
			            h = new Handler();
			            h.postDelayed(check, 0);
			            break;
						
					case MotionEvent.ACTION_UP:
						if (h == null) return false;
			            h.removeCallbacks(check);
			            h = null;
			            break;
				}
				return false; //findViewById(R.id.controls).onTouchEvent(e);
			}
			
			
			Runnable check = new Runnable() {
		        @Override public void run() {
		        	
		        	ImageButton panLeft = (ImageButton) findViewById(R.id.panLeft);
		        	ImageButton panRight = (ImageButton) findViewById(R.id.panRight);
		        	ImageButton tiltUp = (ImageButton) findViewById(R.id.tiltUp);
		        	ImageButton tiltDown = (ImageButton) findViewById(R.id.tiltDown);

		        	
		        	if (panLeft.isPressed()){
		        		new PostIt(postPanR).execute();
		        	}
		        	else if (panRight.isPressed()){
		        		new PostIt(postPanL).execute();
		        	}
		        	else if (tiltUp.isPressed()){
		        		new PostIt(postTiltU).execute();
		        	}
		        	else if (tiltDown.isPressed()){
		        		new PostIt(postTiltD).execute();
		        	}

		            h.postDelayed(this, 225);
		        }
		        
		    };
			
		};
	 
	 
		private OnTouchListener drive = new OnTouchListener(){

			private Handler h;
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()){
					
					case MotionEvent.ACTION_DOWN:
						if (h != null) return false;
			            h = new Handler();
			            h.postDelayed(check, 0);
			            break;
						
					case MotionEvent.ACTION_UP:
						if (h == null) return false;
			            h.removeCallbacks(check);
			            h = null;
			            break;
				}
				return false; //findViewById(R.id.controls).onTouchEvent(e);
			}
			
			
			Runnable check = new Runnable() {
		        @Override public void run() {
		        	
		        	ImageButton btnF = (ImageButton) findViewById(R.id.movFwd);
		        	ImageButton btnB = (ImageButton) findViewById(R.id.movBwd);
		        	ImageButton btnR = (ImageButton) findViewById(R.id.turnR);
		        	ImageButton btnL = (ImageButton) findViewById(R.id.turnL);
		        	ImageButton incJ1 = (ImageButton) findViewById(R.id.incj1);
		    		ImageButton decJ1 = (ImageButton) findViewById(R.id.decj1);
		    		ImageButton incJ2 = (ImageButton) findViewById(R.id.incj2);
		    		ImageButton decJ2 = (ImageButton) findViewById(R.id.decj2);
		    		ImageButton incJ3 = (ImageButton) findViewById(R.id.incj3);
		    		ImageButton decJ3 = (ImageButton) findViewById(R.id.decj3);
		    		ImageButton incGripper = (ImageButton) findViewById(R.id.incg);
		    		ImageButton decGripper = (ImageButton) findViewById(R.id.decg);
		    		ImageButton incBase = (ImageButton) findViewById(R.id.incbase);
		    		ImageButton decBase = (ImageButton) findViewById(R.id.decbase);
		        	
		        	if (btnF.isPressed()){
		        		dataOut.offer('F');
		        		new relayInput().execute();
		        		
		        	}
		        	else if (btnB.isPressed()){
		        		dataOut.offer('B');
		        		new relayInput().execute();
		        	}
		        	else if (btnR.isPressed()){
		        		dataOut.offer('R');
		        		new relayInput().execute();
		        	}
		        	else if (btnL.isPressed()){
		        		dataOut.offer(';');
		        		new relayInput().execute();
		        	}
		        	else if (incJ1.isPressed()){
		        		dataOut.offer('2');
		        		new relayInput().execute();
		        	}
		        	else if (decJ1.isPressed()){
		        		dataOut.offer('1');
		        		new relayInput().execute();
		        	}
		        	else if (incJ2.isPressed()){
		        		dataOut.offer('4');
		        		new relayInput().execute();
		        	}
		        	else if (decJ2.isPressed()){
		        		dataOut.offer('3');
		        		new relayInput().execute();
		        	}
		        	else if (incJ3.isPressed()){
		        		dataOut.offer('6');
		        		new relayInput().execute();
		        	}
		        	else if (decJ3.isPressed()){
		        		dataOut.offer('5');
		        		new relayInput().execute();
		        	}
		        	else if (incGripper.isPressed()){
		        		dataOut.offer('8');
		        		new relayInput().execute();
		        	}
		        	else if (decGripper.isPressed()){
		        		dataOut.offer('7');
		        		new relayInput().execute();
		        	}
		        	else if (incBase.isPressed()){
		        		dataOut.offer('9');
		        		new relayInput().execute();
		        	}
		        	else if (decBase.isPressed()){
		        		dataOut.offer('0');
		        		new relayInput().execute();
		        	}

		            h.postDelayed(this, 225);
		        }
		        
		    };
			
		};
		
		
	 
	@Override
	public boolean onTouchEvent (MotionEvent e){
		return false;
		
	}

	@Override
	public boolean onDown(MotionEvent arg0) {return false;}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {return false;}

	@Override
	public void onLongPress(MotionEvent e) {
		
		return;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float x,float y) {return true;}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {return false;}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {		
	}
	@Override
	public void onSensorChanged(SensorEvent e) {
		
		//tv1.setText("Orientation X (Roll) :"+ Float.toString(e.values[2]) +"\n"+  
          //      "Orientation Y (Pitch) :"+ Float.toString(e.values[1]) +"\n"+  
            //    "Orientation Z (Yaw) :"+ Float.toString(e.values[0]));
		//if sensor is unreliable, return void  
        /*if (e.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)  
        {  
            return;  
        }  
		
		final Handler hcam;
  
        if (e.values[0] > 0){
        	hcam = new Handler();
        	hcam.postDelayed( new Runnable () {
    	        @Override public void run() {
    	        	new PostIt(postPanL).execute();
    	            h.postDelayed(this, 225);
    	        }
    	        
    	    }, 0);
            
        	//new PostIt(postTiltU).execute();
        }
        else if (e.values[0] < 0){
        	hcam = new Handler();
        	hcam.postDelayed( new Runnable () {
    	        @Override public void run() {
    	        	new PostIt(postPanR).execute();
    	            h.postDelayed(this, 225);
    	        }
    	        
    	    }, 0);
        }
        
        */
    }  
		
	public void updateTemp(String s){
		tvTemp.setText(s);
	}
	public void updateLux(String s){
		tvLux.setText(s);
	}
	public void updateBatt(String s){
		tvBatt.setText(s);
	}
}
