package com.driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.util.UUID;

import com.driver.R;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = "MainActivity";
	private static final boolean D = true;
	private Button AutoButton;
	private Button TrainingButton;

	// socket wifi
	static Socket wifiSocket = null;
	static String ipAddress = null;
	private WifiManager wifiManager;

	// socket bluetooth
	static BluetoothSocket BTsocket = null;
	public static boolean isBTconnected = false;
	String BTdevice = null;

	private Data data;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		TrainingButton = (Button) findViewById(R.id.button2);
		TrainingButton.setOnClickListener(this);

		AutoButton = (Button) findViewById(R.id.captureButton);
		AutoButton.setOnClickListener(this);

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

	}

	protected void onResume() {
		super.onResume();

		if (D)
			Log.e(TAG, "+++ ON RESUME +++");
		// The activity has become visible (it is now "resumed").

		boolean fileExist = false;
		try {
			FileInputStream fin = new FileInputStream(new File(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/driverData/data.sav"));

			try {
				ObjectInputStream ois = new ObjectInputStream(fin);
				if (D)
					Log.e(TAG, "file data.sav trovato");
				try {

					data = (Data) ois.readObject();
					fileExist = true;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					if (D)
						Log.e(TAG, e.getMessage());
					e.printStackTrace();
				}

				ois.close();

			} catch (StreamCorruptedException e) {
				// TODO Auto-generated catch block
				if (D)
					Log.e(TAG, e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if (D)
					Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			if (D)
				Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}

		if (fileExist) {
			ipAddress = data.getIpAddress();
			BTdevice = data.getBTdevice();
		}

	}

	protected void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "+++ ON START +++");
	}

	protected void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "+++ ON STOP +++");

	}

	protected void onDestroy() {
		super.onDestroy();
		if (D)
			Log.e(TAG, "+++ ON DESTROY +++");
		WifiLib.connectionClose(wifiSocket);
		if (D)
			Log.e(TAG, "Socket wifi chiuso");

		closeBTSocket();
		if (D)
			Log.e(TAG, "Socket BT chiuso");

	}

	public void onClick(View v) {
		int resourceId = v.getId();

		if (resourceId == R.id.button2) {

			if (wifiManager.isWifiEnabled()) {
				if (WifiLib.isValidIp(ipAddress)) {
					Toast.makeText(getApplicationContext(),
							"ip = " + ipAddress, Toast.LENGTH_SHORT).show();

					wifiSocket = WifiLib.OpenSocket(ipAddress);
					connect(BTdevice);

					Intent startNewActivityOpen = new Intent(this,
							TrainingActivity.class);
					// startActivityForResult(startNewActivityOpen,0);
					startActivity(startNewActivityOpen);
				} else {

					Toast.makeText(getApplicationContext(),
							"ip address not valid", Toast.LENGTH_SHORT).show();

				}
			} else {
				Toast.makeText(getApplicationContext(), "WiFi is off",
						Toast.LENGTH_SHORT).show();
			}
		}

		else if (resourceId == R.id.captureButton) {
			if (BTdevice != null) {
				connect(BTdevice);
				Intent startNewActivityOpen = new Intent(this,
						AutoActivity.class);
				startActivityForResult(startNewActivityOpen, 0);
			} else {
				Toast.makeText(getApplicationContext(), "setup BT device",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Intent enabler;
		switch (item.getItemId()) {
		case R.id.ipaddress:
			enabler = new Intent(this, WifiSetupActivity.class);
			startActivityForResult(enabler, 0);
			return true;
		case R.id.bluetooth:
			enabler = new Intent(this, BluetoothActivity.class);
			startActivityForResult(enabler, 0);
			return true;
		case R.id.neuralNetFile:
			enabler = new Intent(this, NeuralNetSelector.class);
			startActivityForResult(enabler, 0);
			return true;
		case R.id.receive_net_menu:
			if (wifiManager.isWifiEnabled()) {
				if (WifiLib.isValidIp(ipAddress)) {
					Toast.makeText(getApplicationContext(),
							"ip = " + ipAddress, Toast.LENGTH_SHORT).show();

					wifiSocket = WifiLib.OpenSocket(ipAddress);

			enabler = new Intent(this, ReceiveNetActivity.class);
			startActivityForResult(enabler, 0);
				}
			}else {
				Toast.makeText(getApplicationContext(), "WiFi is off",
						Toast.LENGTH_SHORT).show();
			}
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static void setIpAddress(String ipAddressp) {
		ipAddress = ipAddressp;
	}

	// getter socket wi-fi
	public static Socket getSocket() {
		return wifiSocket;
	}

	// setter socket btooth
	public static void setBTSocket(BluetoothSocket Bsocket) {
		BTsocket = Bsocket;
		isBTconnected = true;
	}

	// getter socket btooth
	public static BluetoothSocket getBTSocket() {
		return BTsocket;
	}

	public static void closeBTSocket() {
		if (BTsocket != null) {
			try {
				BTsocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		BTsocket=null;
	}

	private void connect(String deviceAddress) {

		try {
			BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
			BluetoothDevice device = _bluetooth.getRemoteDevice(deviceAddress);
			UUID SerialPortServiceClass_UUID = UUID
					.fromString("00001101-0000-1000-8000-00805F9B34FB");

			BluetoothSocket tmp = null;
			tmp = device
					.createRfcommSocketToServiceRecord(SerialPortServiceClass_UUID);
			BTsocket = tmp;

			try {
				BTsocket.connect();
				BluetoothActivity.setBTSocket(BTsocket);
				MainActivity.setBTSocket(BTsocket);
			} catch (IOException e) {
				Log.e(TAG, "connection BT failed", e);
				closeBTSocket();
			} catch (Exception e) {
				Log.e(TAG, "connection BT failed", e);
				closeBTSocket();
			}

		} catch (IOException e) {
			Log.e(TAG, "create() BT failed", e);
			closeBTSocket();
		} catch (Exception e) {
			Log.e(TAG, "create() failed", e);
			closeBTSocket();
		}

	}

}
