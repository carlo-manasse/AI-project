package com.driver;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;


import com.NeuralNetLibrary.NeuralNetwork;
import com.driver.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiveNetActivity extends Activity implements OnClickListener {

	// variabili debug
	private static final String TAG = "ReceiveNetActivity";
	private static final boolean D = true;

	// variabili connessione wifi
	private boolean isConnected = false;
	private Socket wifiSocket = null;
	//private DataOutputStream output = null;
	private InputStream input = null;

	private Button receiveButton;
	private TextView receiveLabel;
	private ProgressBar progressBar;

	File dir = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/driverData/reti");
	String[] allFiles;
	
	
	// onCreate, primo entry point dell'activity
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receive_net_layout);

		allFiles = dir.list();
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");
		double eps=0.2;
		int[] nodi=new int[3];
		nodi[0]=1;
		nodi[1]=1;
		nodi[2]=1;
		NeuralNetwork net = new NeuralNetwork(eps,nodi);
		if (D)
			Log.e(TAG,net.getClass().toString());
		receiveButton = (Button) findViewById(R.id.receiveNetButton);
		receiveButton.setOnClickListener(this);

		receiveLabel = (TextView) findViewById(R.id.receiveLabel);
		receiveLabel.setText("control to be connected, then click the button to receive net file");

		progressBar = (ProgressBar) findViewById(R.id.progressBar1);

		progressBar.setVisibility(View.INVISIBLE);

		wifiSocket = MainActivity.getSocket();

		if (wifiSocket != null // && BTsocket!=null
		) {
			isConnected = wifiSocket.isConnected();
			try {
				wifiSocket.setSoTimeout(10000);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				isConnected = false;
				Log.e(TAG, e.getMessage());
			}
			// input = WifiLib.InitializeInput(wifiSocket);
			try {
				input = wifiSocket.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if (D)
				Log.e(TAG, "connessione al pc mancante");
			Toast.makeText(getApplicationContext(),
					"connessione al pc mancante", Toast.LENGTH_SHORT).show();
		}
	}

	protected void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "+++ ON PAUSE +++");
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

		MainActivity.closeBTSocket();
	}

	@Override
	public void onClick(View v) {
		int resourceId = v.getId();

		if (resourceId == R.id.receiveNetButton) {
			if (isConnected) {
				progressBar.setVisibility(View.VISIBLE);
				receiveLabel.setText("receiving file ...");
				if (D)
					Log.e(TAG,"receiving file");
				
				try {
					ObjectInputStream ois = new ObjectInputStream(input);
					Log.e(TAG, "file trovato");
					NeuralNetwork net = (NeuralNetwork) ois.readObject();

					ois.close();
					Integer num=allFiles.length;
					String name="Net"+num.toString()+".dat";
					if(!saveNet(name,net)){
						if (D)
							Log.e(TAG,"error saving neural network file");
						Toast.makeText(getApplicationContext(),
								"error saving neural network file",
								Toast.LENGTH_SHORT).show();
					}

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					if (D)
						Log.e(TAG, "1 "+e.getMessage()+
								e.getCause().getMessage());
					Toast.makeText(getApplicationContext(),
							"error opening neural network file",
							Toast.LENGTH_SHORT).show();
				} catch (StreamCorruptedException e) {
					// TODO Auto-generated catch block
					if (D)
						Log.e(TAG, "2"+e.getMessage());
					Toast.makeText(getApplicationContext(),
							"error opening neural network file",
							Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if (D)
						Log.e(TAG, "3"+e.getMessage());
					Toast.makeText(getApplicationContext(),
							"error opening neural network file",
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(getApplicationContext(),
						"not connected",
						Toast.LENGTH_SHORT).show();

			}
		}
	}
	
	
    public boolean saveNet(String path,NeuralNetwork net) {
    	boolean ret=true;
        FileOutputStream saveFile;
        try {
            saveFile = new FileOutputStream(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/driverData/reti/"+path);
            ObjectOutputStream save = new ObjectOutputStream(saveFile);
            save.writeObject(net);
            save.close();
            receiveLabel.setText("file saved with name"+path);
        } catch (Exception exc) {
            
    		if (D)
    			Log.e(TAG, exc.getMessage());

        }
        
        return ret;
    }


}
