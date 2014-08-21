package com.driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

import com.driver.R;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WifiSetupActivity extends Activity implements OnClickListener {

	private static final String TAG = "WifiSetupActivity ";
	private static final boolean D = true;

	// socket wifi
	static Socket wifiSocket = null;
	String ipAddress = null;

	private Data data;
	private boolean fileExist = false;

	private EditText editText;
	private Button invia;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ip_activity);

		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

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
			// BTdevice=data.getBTdevice();
		} else {
			data = new Data();
		}

		invia = (Button) findViewById(R.id.invia);
		invia.setOnClickListener(this);
		editText = (EditText) findViewById(R.id.editText1);

	}

	protected void onResume() {
		super.onResume();

		if (D)
			Log.e(TAG, "+++ ON RESUME +++");

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

	}

	public void onClick(View v) {
		int resourceId = v.getId();

		if (resourceId == R.id.invia) {
			String str0 = editText.getText().toString();

			if (WifiLib.isValidIp(str0)) {
				Toast.makeText(getApplicationContext(), "ip = " + str0,
						Toast.LENGTH_SHORT).show();

				// inizializzazione connessione
				ipAddress = str0;
				data.setIpAddress(ipAddress);
				MainActivity.setIpAddress(ipAddress);

				if (!data.saveData(new File(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/driverData/data.sav"))) {
					Toast.makeText(getApplicationContext(),
							"errore nel salvataggio del file",
							Toast.LENGTH_SHORT).show();
				}
				finish();

			} else {
				Toast.makeText(getApplicationContext(),
						"Formato ip non valido", Toast.LENGTH_SHORT).show();
			}
		}
	}

	// getter socket wi-fi
	public static Socket getSocket() {
		return wifiSocket;
	}
}
