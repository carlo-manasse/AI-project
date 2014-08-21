package com.driver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;
/*
 import android.annotation.SuppressLint;
 import android.annotation.TargetApi;
 import android.os.Build;
 import android.os.StrictMode;
 */
import android.util.Log;

//@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class WifiLib {

	private static final String TAG = "WIFILIB";
	private static final boolean D = true;

	// @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	// @SuppressLint("NewApi")
	public static Socket OpenSocket(String ipAddress) {

		Socket s = null;
		if (D)
			Log.e(TAG, "connecting to server at ip " + ipAddress + " ... ");

		/*
		 * StrictMode.ThreadPolicy policy = new
		 * StrictMode.ThreadPolicy.Builder().permitAll().build();
		 * StrictMode.setThreadPolicy(policy);
		 */

		try {
			s=new Socket();
			s.setSoTimeout(10000);
			InetSocketAddress remoteAddr=new  InetSocketAddress(ipAddress, 8900);
			s.connect(remoteAddr, 1000);
			//s = new Socket(ipAddress, 8900);
			if (D)
				Log.e(TAG, "connected");
		} 
		catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			if (D)
				Log.e(TAG, "Not Connected");
			s=null;
		}
		catch (	IllegalBlockingModeException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			if (D)
				Log.e(TAG, "Not Connected");
			s=null;
		}
		catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			if (D)
				Log.e(TAG, "Not Connected");
			s=null;
		}catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			if (D)
				Log.e(TAG, "Not Connected");
			s=null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			if (D)
				Log.e(TAG, "Not Connected");
			s=null;
		}
		
			return s;
	}

	public static PrintWriter InitializeOutput1(Socket s) {
		PrintWriter output = null;
		try {
			output = new PrintWriter(s.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (D)
				Log.e(TAG, "PrintWriter not available");
		}
		return output;
	}

	public static BufferedReader InitializeInput(Socket s) {
		BufferedReader input = null;

		try {
			input = new BufferedReader(
					new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (D)
				Log.e(TAG, "BufferedReader not available");
		}
		return input;
	}

	public static DataOutputStream InitializeOutput(Socket s) {
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(s.getOutputStream());
		} catch (IOException e) {
			if (D)
				Log.e(TAG, "DataOutpuStream not available");
		}

		return dos;
	}

	public static void connectionClose(Socket s) {
		try {
			// output.close();
			if (s != null)
				s.close();
		} catch (IOException e) {
			e.printStackTrace();
			if (D)
				Log.e(TAG, e.getMessage());
		}

	}

	public static boolean isValidIp(final String ip) {
		if (ip == null) {
			return false;
		} else {
			return ip
					.matches("[0-9]{1,3}+\\.[0-9]{1,3}+\\.[0-9]{1,3}+\\.[0-9]{1,3}");
		}
	}

}
