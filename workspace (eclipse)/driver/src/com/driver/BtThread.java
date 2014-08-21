package com.driver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BtThread extends Thread {

	private static final String TAG = "BTthread";
	private static final boolean D = true;

	private Handler handler;

	private BluetoothSocket BTsocket;
	private OutputStream BToutputStream;
	private InputStream BTinputStream;
	private boolean isConnectedBT;

	private boolean isRunning = true;

	private Integer bytes;
	private byte[] buffer = new byte[1024];

	BtThread(BluetoothSocket BTsocket, Handler handler) {
		this.handler = handler;
		this.BTsocket = BTsocket;

		if (this.BTsocket != null) {
			try { 
				BToutputStream = this.BTsocket.getOutputStream();
				BTinputStream = this.BTsocket.getInputStream();
				isConnectedBT = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				isConnectedBT = false;
				isRunning=false;
				if (D)
					Log.e(TAG, e.getMessage());
			}catch (Exception e) {
				// TODO Auto-generated catch block
				isConnectedBT = false;
				isRunning=false;
				if (D)
					Log.e(TAG, e.getMessage());
			}

		} else {
			isConnectedBT = false;
			isRunning=false;
		}
	}

	public void run() {

		try {

			while (isRunning) {
				try {
					bytes = BTinputStream.read(buffer);
					if (bytes != -1) {
						String readMessage = new String(buffer, 0, bytes,
								"US-ASCII");

						Log.e(TAG, "mess " + readMessage);
						Log.e(TAG, "bytes " + bytes.toString());
						Log.e(TAG, "buffer " + buffer.length);

						notifyMessage(readMessage);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(TAG, e.getMessage());
					isConnectedBT = false;
				}

				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//isConnectedBT = false;
		}

		if (D)
			Log.e(TAG, "btThread terminato");

	}

	public void stopThread() {
		isConnectedBT = false;
		isRunning = false;
	}

	public void write(String action) {
		try {
			String Data = action + "E";
			BToutputStream.write(Data.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getMessage());
			isConnectedBT = false;
		}
	}

	private void notifyMessage(String str) {
		Message msg = handler.obtainMessage();
		Bundle b = new Bundle();
		b.putString("okMessage", str);
		msg.setData(b);
		handler.sendMessage(msg);
	}

	public boolean isConnectedBT() {
		return isConnectedBT;
	}
}
