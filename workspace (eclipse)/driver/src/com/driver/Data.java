package com.driver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import android.os.Environment;
import android.util.Log;

public class Data implements Serializable{
	/**
	 * 
	 */

	private static final String TAG = "Data";
	private static final boolean D = true;
	
	private static final long serialVersionUID = 1L;
	private String ipAddress=null;
	private String BTdevice=null;
	
	public void setIpAddress(String ipAddress){
		this.ipAddress=ipAddress;
	}
	
	public void setBTdevice(String BTdevice){
		this.BTdevice=BTdevice;
	}
	
	public String getIpAddress(){
		return ipAddress;
	}
	
	public String getBTdevice(){
		return BTdevice;
	}
	

    public boolean saveData(File path) {
    	boolean ret=true;
        FileOutputStream saveFile;
        try {
            saveFile = new FileOutputStream(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/driverData/data.sav");
            ObjectOutputStream save = new ObjectOutputStream(saveFile);
            save.writeObject(this);
            save.close();
        } catch (Exception exc) {
            
    		if (D)
    			Log.e(TAG, exc.getMessage());

        }
        
        return ret;
    }

}
