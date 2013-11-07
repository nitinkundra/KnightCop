package com.knight.cop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Client extends Thread{

	Socket client;
	String serverIP = "192.168.1.6";
	int serverPort = 22222;
	boolean connected;
	byte[] imageByteArray;
	Bitmap bitmap;
	
	public Client(Bitmap bmp){
		bitmap = bmp;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Attempting to open Socket");
			client = new Socket(serverIP, serverPort);
			System.out.println("SOCKET OPEN~~~~~");
			connected = true;
			while (connected){
				try{
					PrintWriter toPC = 
							new PrintWriter (new BufferedWriter (new OutputStreamWriter (client.getOutputStream())));
					toPC.println("WHEEEE");
					toPC.flush();
					
					//BufferedReader fromPC = new BufferedReader(new InputStreamReader(client.getInputStream()));
					InputStream camImFromPC = new ByteArrayInputStream(imageByteArray);
					BitmapFactory.Options opt = new BitmapFactory.Options();
					opt.inDither = true;
					opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
					
					bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length, opt);
					//findViewById(R.id.camShot).setImageBitmap(bitmap);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("CATCH 2 [[]]");
			connected = false;
		}
	}

	
	
	

}
