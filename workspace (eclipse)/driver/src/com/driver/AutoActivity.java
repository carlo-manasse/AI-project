package com.driver;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.NeuralNetLibrary.NeuralNetwork;
import com.driver.R;

import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.List;
import com.driver.Common;


public class AutoActivity extends Activity implements OnClickListener,
		SurfaceHolder.Callback, PreviewCallback {

	private static final String TAG = "AutoActivity";
	private static final boolean D = true;

	private boolean mPreviewRunning = false;
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera.Parameters p;
	private Size minSize;

	private Button ScattaButton;
	private TextView label;

	private int inputdim;
	private int outputdim;
	private int[] nodiPerLayer;
	private NeuralNetwork net = null;
	private boolean fileExist = false;

	private int cont = 0;
	private int width = 144;
	private int height = 176;
	private int scale = 16;

	// variabili connessione bluetooth
	BtThread btThread;
	boolean isReady = true;

	public void surfaceCreated(SurfaceHolder holder) {
		if (D)
			Log.e(TAG, "surface created");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

		if (mCamera != null) {
			if (D)
				Log.e(TAG, "surface changed");
			p = mCamera.getParameters();
			p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			List<Size> tmpList = mCamera.getParameters()
					.getSupportedPreviewSizes();
			String stringa = "sizes: ";
			minSize = tmpList.get(0);
			for (int i = 0; i < tmpList.size(); i++) {
				if (minSize.width > tmpList.get(i).width) {
					minSize.height = tmpList.get(i).height;
					minSize.width = tmpList.get(i).width;
				}
			}
			List<Integer> a = p.getSupportedPreviewFormats();
			String str2 = null;
			for (int i = 0; i < a.size(); i++) {
				str2 = str2 + " codifica " + i + " " + String.valueOf(a);
			}
			stringa = "preview size: " + minSize.width + " per "
					+ minSize.height + str2;
			p.setPreviewSize(minSize.width, minSize.height);
			label.setText(stringa);
			p.set("orientation", "portrait");
			mCamera.setParameters(p);
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				e.printStackTrace();
				if (D)
					Log.e(TAG, e.getMessage());
			}
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		if (D)
			Log.e(TAG, "surface destroyed");
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tr_activity);

		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Handler handler = new MyHandler(this);

		if (MainActivity.getBTSocket() != null) {
			btThread = new BtThread(MainActivity.getBTSocket(), handler);
			btThread.start();
		} else {
			if (D)
				Log.e(TAG, "connessione BT mancante");
			Toast.makeText(getApplicationContext(), "connessione BT mancante",
					Toast.LENGTH_SHORT).show();
		}

		// apre il file della rete neurale

		try {
			FileInputStream fin = new FileInputStream(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/driverData/reti/"+
					NeuralNetSelector.fileSelected);

			try {
				ObjectInputStream ois = new ObjectInputStream(fin);
				Log.e(TAG, "file trovato");
				try {

					net = (NeuralNetwork) ois.readObject();
					nodiPerLayer = net.getNodiPerLayer();
					inputdim = nodiPerLayer[0];
					Log.e(TAG, "nodi input " + inputdim);
					outputdim = nodiPerLayer[nodiPerLayer.length - 1];
					Log.e(TAG, "nodi output " + outputdim);
					fileExist = true;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					if (D)
						Log.e(TAG, e.getMessage());
					Toast.makeText(getApplicationContext(), "error opening neural network file",
							Toast.LENGTH_LONG).show();
				}

				ois.close();

			} catch (StreamCorruptedException e) {
				// TODO Auto-generated catch block
				if (D)
					Log.e(TAG, e.getMessage());
				Toast.makeText(getApplicationContext(), "error opening neural network file",
						Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if (D)
					Log.e(TAG, e.getMessage());
				Toast.makeText(getApplicationContext(), "error opening neural network file",
						Toast.LENGTH_LONG).show();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			if (D)
				Log.e(TAG, e.getMessage());
			Toast.makeText(getApplicationContext(), "error opening neural network file",
					Toast.LENGTH_LONG).show();
		}

		if (funzioni.checkCameraHardware(this)) {
			mCamera = funzioni.getCameraInstance();
			 mCamera.setPreviewCallback(this);
			//mCamera.setOneShotPreviewCallback(this);
			if (D)
				Log.e(TAG, "callback settato");

			mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
			mSurfaceHolder = mSurfaceView.getHolder();

			mSurfaceHolder.addCallback(this);
			mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			if (D)
				Log.e(TAG, "surfaceholder settato");

			ScattaButton = (Button) findViewById(R.id.captureButton);
			ScattaButton.setOnClickListener(this);

			label = (TextView) findViewById(R.id.label);
		} else {
			label.setText("errore camera non trovata");
			if (D)
				Log.e(TAG, "errore camera non trovata");
		}

	}

	public void onClick(View v) {
		if (v.getId() == R.id.captureButton && fileExist) {
			if (D)
				Log.e(TAG, "Button capture click");
			if (mPreviewRunning) {
				mCamera.stopPreview();
			} else {
				mCamera.startPreview();
			}
			mPreviewRunning = !mPreviewRunning;
		}
	}

	protected void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "+++ ON STOP +++");
		if (mPreviewRunning) {
			mCamera.stopPreview();
			mPreviewRunning = !mPreviewRunning;
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		if (D)
			Log.e(TAG, "+++ ON DESTROY +++");
		funzioni.releaseCamera(mCamera);
		if (D)
			Log.e(TAG, "Socket chiuso");
		if (btThread != null) {
			btThread.stopThread();
		}
		MainActivity.closeBTSocket();
	}

	public void onPreviewFrame(byte[] data, Camera camera) {
		if (mCamera != null ) {
			synchronized (this) {
				String action = getAction(data);
				label.setText(action + "   " + cont);
				cont = cont + 1;
				if (btThread!=null && btThread.isConnectedBT()) {
					btThread.write(action);
				}
				//isReady = false;
			}// synchronized
		}
	}// onpreviewframe

	private String getAction(byte[] data) {
		
		byte[] temp = new byte[height * width];
        Common.decodeYUV420SPGrayscale(temp, data, height, width);
        int[] frame = Common.byte2int(temp, height, width);

        for (int i = 0; i < frame.length; i++) {
            if (frame[i] > 50) {
                frame[i] = 0;
            } else {
                frame[i] = 255;
            }
        }

        int pixels[] = Common.getFeature3(frame, width, height, scale, scale);//Common.averageImage(frame, 2, width, height);

        double[] input = new double[inputdim];

        //normalizzazione input
        for (int j = 0; j < input.length; j++) {
            input[j] = (double) pixels[j] / 255;
        }
        
        net.setInput(input);
        double[] risultato = net.getOutputs();
        String action = Common.retrieveOutput(risultato);

		return action;

	}

	private class MyHandler extends Handler {

		AutoActivity auto;

		MyHandler(AutoActivity auto) {
			super();
			this.auto = auto;
		}

		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			if (b.containsKey("okMessage")) {
				if (D)
					Log.e(TAG, "message arrived");

				//isReady = true;
				
				/*if (mCamera != null && b.getString("okMessage").equals("p"))
					mCamera.setOneShotPreviewCallback(auto);
				*/
				
				// label.setText(b.getString("okMessage"));
			}
		}

	}
}
