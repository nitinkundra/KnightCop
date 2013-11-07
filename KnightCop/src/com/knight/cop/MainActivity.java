package com.knight.cop;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity implements OnGestureListener{

	GestureDetector driveControl;
	Point screenSize;
	Bitmap bmpImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
								WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_main);
		
		Client droid = new Client(bmpImage);
		droid.start();
		findViewById(R.id.camShot).se
		
		//figure out display pixel size
		Display display = getWindowManager().getDefaultDisplay();
		screenSize = new Point();
		display.getSize(screenSize);
		
		driveControl = new GestureDetector(this);
		
		
		
		
		
		

		
	}

	@Override
	public boolean onTouchEvent (MotionEvent e){
		return driveControl.onTouchEvent(e);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// use in buttons
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// nope
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// maybe
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float x,float y) {
		
		//get location of upper left corner of the view
		int[] locL = new int[2];	//[0] is x, [1] is y
		findViewById(R.id.leftSlider).getLocationOnScreen(locL);
		int[] locR = new int[2];
		findViewById(R.id.rightSlider).getLocationOnScreen(locR);
		
		//e1 is the initial touch down event
		if (e1.getRawX() < locR[0] + 100){	//swipe started to the left of rightSlider, 300 px buffer 
			
			//e2 is the touch drag event
			if (e2.getRawY() < screenSize.y/2){	//swipe ended in top half of screen
				//left slider accelerate
				System.out.println("ls a");
			}	
			else{	//swipe was towards bottom
				//left slider decelerate
				System.out.println("ls d");
			}
		}
		else {	//otherwise user swiped in the right slider zone
			
			if (e2.getRawY() < screenSize.y/2){	//swipe ended in top half of screen
				//right slider accelerate
				System.out.println("rs a");
			}	
			else{	//swipe to bottom
				//right slider decelerate
				System.out.println("rs d");
			}
		}
		
		return true;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
