package com.driver;

import java.io.File;
import java.util.Date;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class NeuralNetSelector extends ListActivity {
	 
	
	File dir = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/driverData/reti");

	String[] files;
	String[] filesExt;
	
	
	public static String fileSelected="Net.dat";

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
	 
			// no more this
			// setContentView(R.layout.list_fruit);
	 
			int count=0;
			String[] allFiles = dir.list();
			for(int j=0;j<allFiles.length;j++){
				if(allFiles[j].contains(".dat"))
					count++;
			}
			
			files=new String[count];
			filesExt=new String[count];
			int k=0;
			for(int j=0;j<allFiles.length;j++){
				if(allFiles[j].contains(".dat")){
					files[k]=allFiles[j];
					File file = new File(Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/driverData/reti/"+files[k]);
					Date lastModDate = new Date(file.lastModified());
					filesExt[k]=files[k]+"  ("+lastModDate.toLocaleString()+")";
					k++;
					}
			}
			
			setListAdapter(new ArrayAdapter<String>(this, R.layout.net_selector_layout,filesExt));
	 
			ListView listView = getListView();
			listView.setTextFilterEnabled(true);
	 
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long arg3) {
					// TODO Auto-generated method stub
				    //Toast.makeText(getApplicationContext(),
					//((TextView) view).getText(), Toast.LENGTH_SHORT).show();
				    
				    fileSelected=files[position];
				    Toast.makeText(getApplicationContext(),
							"selected network: "+fileSelected, Toast.LENGTH_SHORT).show();
						    
				    finish();
					
				}
			});
	 
		}
}
	 