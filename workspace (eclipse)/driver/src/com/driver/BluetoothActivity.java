package com.driver;

import java.io.IOException;
import java.io.OutputStream;

import com.driver.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class BluetoothActivity extends Activity {
	/* Get Default Adapter */
	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();

	/* request BT enable */
	//private static final int REQUEST_ENABLE = 0x1;
	/* request BT discover */
	private static final int REQUEST_DISCOVERABLE = 0x2;

	private static final String TAG = "BluetoothActivity";

	static BluetoothSocket socket = null;

	private int statoTest=0;
	
	public void cancel() {
		if (socket!=null)
		try {
			socket.close();
		} catch (IOException e) {
			Log.e(TAG, "close() of connect socket failed", e);
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bt_main);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cancel();
		
	}
	
	/* Enable BT */
	public void onEnableButtonClicked(View view) {

		if (!_bluetooth.isEnabled()) {
			_bluetooth.enable();
			Intent enabler = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			startActivityForResult(enabler, REQUEST_DISCOVERABLE);
		}
	}

	/* Close BT */
	public void onDisableButtonClicked(View view) {
		if (_bluetooth.isEnabled()) {

			_bluetooth.disable();
		}
	}

	/* Start search */
	public void onStartDiscoveryButtonClicked(View view) {
		Intent enabler = new Intent(this, ClientSocketActivity.class);
		startActivity(enabler);
	}

	public void onDisconnectButtonClicked(View view) {
		cancel();
	}

	public void onTestButtonClicked(View view) {
				
		String Str="255_060E";
		if(statoTest!=0){
			statoTest=0;
			Str="000_090E";
		}
		else{
			statoTest=1;
		}
		//String Str="255_60";
		OutputStream outputStream;
		//InputStream inputStream;
		try {
			outputStream = socket.getOutputStream();
			//inputStream = socket.getInputStream();
				outputStream.write(Str.getBytes());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception e){
			
			
		}
		
	}

	
	public static void setBTSocket(BluetoothSocket Bsocket) {
		socket = Bsocket;

	}

}
