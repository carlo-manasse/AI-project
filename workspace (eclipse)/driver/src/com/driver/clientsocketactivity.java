package com.driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.UUID;

import com.driver.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class ClientSocketActivity  extends Activity
{
	private static final String TAG = ClientSocketActivity.class.getSimpleName();
	private static final int REQUEST_DISCOVERY = 0x1;;
	//private Handler _handler = new Handler();
	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
	
	private static final UUID SerialPortServiceClass_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private BluetoothSocket BTsocket = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
		WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.client_socket);
		if (!_bluetooth.isEnabled()) {
			finish();
			return;
		}
		Intent intent = new Intent(this, DiscoveryActivity.class);
		
		/* Prompted to select a server to connect */
		Toast.makeText(getApplicationContext(), "select device to connect", Toast.LENGTH_SHORT).show();
		
		/* Select device for list */
		startActivityForResult(intent, REQUEST_DISCOVERY);
	}
	
	/* after select, connect to device */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != REQUEST_DISCOVERY) {
			return;
		}
		if (resultCode != RESULT_OK) {
			return;
		}
		final BluetoothDevice device = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		new Thread() {
			public void run() {
				connect(device);
			};
		}.start();
	}
	
	
	protected void connect(BluetoothDevice device) {
		//BluetoothSocket socket = null;
		
		BluetoothSocket tmp = null;

        // Get a BluetoothSocket for a connection with the
        // given BluetoothDevice
        try {
            tmp = device.createRfcommSocketToServiceRecord(SerialPortServiceClass_UUID);
        } catch (IOException e) {
            Log.e(TAG, "create() failed", e);
        }

        BTsocket=tmp;
        
        
        try {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            BTsocket.connect();
    		//Toast.makeText(getApplicationContext(), "Device Connected Successfully", Toast.LENGTH_SHORT).show();
    		BluetoothActivity.setBTSocket(BTsocket);
    		//MainActivity.setBTSocket(BTsocket);
    		boolean fileExist=false;
    		Data data=null;
    		try {
    			FileInputStream fin = new FileInputStream(new File(Environment
    					.getExternalStorageDirectory().getAbsolutePath()
    					+ "/driverData/data.sav"));

    			try {
    				ObjectInputStream ois = new ObjectInputStream(fin);
    				try {

    					data = (Data) ois.readObject();
    					fileExist = true;
    				} catch (ClassNotFoundException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}

    				ois.close();

    			} catch (StreamCorruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

    		if (fileExist) {
    			data.setBTdevice(device.getAddress());

				if (!data.saveData(new File(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/driverData/data.sav"))) {
					Toast.makeText(getApplicationContext(),
							"errore nel salvataggio del file",
							Toast.LENGTH_SHORT).show();
				}
    		}

    		finish();
        } catch (IOException e) {
            //connectionFailed();

            Log.e(TAG, "connection failed", e);
            // Close the socket
            try {
                BTsocket.close();
            } catch (IOException e2) {
                Log.e(TAG, "unable to close() socket during connection failure", e2);
            }
            // Start the service over to restart listening mode
            //BluetoothSerialService.this.start();
            return;
        }
        /*
		try {
			
			_bluetooth.cancelDiscovery();
			socket.connect();
			Log.d("EF-BTBee", ">>Client connected");
			
			
			///// questa parte va spostata su training activity
			/// inoltre va fatta la funzione per gettare il socket
			InputStream inputStream = socket.getInputStream();														
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(new byte[] { (byte) 0xa0, 0, 7, 16, 0, 4, 0 });
			
//			new Thread() {
//				public void run() {
//						while(true)
//						{	
//						try {
//							Log.d("EF-BTBee", ">>Send data thread!");
//							OutputStream outputStream = socket.getOutputStream();
//							outputStream.write(new byte[] { (byte) 0xa2, 0, 7, 16, 0, 4, 0 });
//						} catch (IOException e) {
//							Log.e("EF-BTBee", "", e);
//						}
//						}
//				};
//			}.start();
			
		} catch (IOException e) {
			Log.e("EF-BTBee", "errore connect", e);
		} finally {
			if (socket != null) {
				try {
					Log.d("EF-BTBee", ">>Client Close");
					socket.close();	
					finish();
					return ;
				} catch (IOException e) {
					Log.e("EF-BTBee", "", e);
				}
			}
		}
		
		
		*/
	}
}

